#!/usr/bin/env python3
"""Guard the shared build path, supported platforms and publication boundary."""
import hashlib
import importlib.util
import json
import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest
from unittest.mock import patch
import zipfile

import yaml

ROOT = Path(__file__).resolve().parents[2]
WORKFLOWS = ROOT / '.github/workflows'
SHARED = yaml.safe_load((WORKFLOWS / 'build-package.yml').read_text())
PLAN_SPEC = importlib.util.spec_from_file_location('plan_package', Path(__file__).with_name('plan-package-build.py'))
PLANNER = importlib.util.module_from_spec(PLAN_SPEC)
PLAN_SPEC.loader.exec_module(PLANNER)
SPEC = importlib.util.spec_from_file_location('record_package', Path(__file__).with_name('record-package-build.py'))
RECORD = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(RECORD)


def plan(kind='all', full=None, channel='preview', event='workflow_dispatch', **overrides):
    env = dict(PACKAGE_TYPE=kind, CHANNEL=channel,
               GITHUB_REPOSITORY='94fzb/zrlog', GITHUB_REF='refs/heads/master',
               GITHUB_EVENT_NAME=event, GITHUB_SHA='a' * 40)
    if full is not None:
        env['FULL_PLATFORMS'] = str(full).lower()
    env.update(overrides)
    return PLANNER.plan(env)


def platforms(settings):
    targets = list(settings['matrix']['include'])
    if settings['build_native']:
        targets.append(dict(package_type='native', os='ubuntu-22.04', java='25'))
        if settings['full_platforms']:
            for job in ['native_linux_arm64', 'native_windows', 'native_macos_intel', 'native_macos_arm64']:
                targets.extend(SHARED['jobs'][job]['strategy']['matrix']['include'])
    return targets


class PackageWorkflowContract(unittest.TestCase):
    def test_automatic_builds_use_the_same_full_matrix_on_both_branches(self):
        expected = platforms(plan())
        for event, ref, channel in [('push', 'master', 'preview'),
                                    ('push', 'release', 'release'),
                                    ('repository_dispatch', 'master', 'preview')]:
            with self.subTest(event=event, ref=ref):
                settings = plan('java', False, 'release', event=event,
                                GITHUB_REF=f'refs/heads/{ref}')
                self.assertEqual(channel, settings['channel'])
                self.assertTrue(settings['publish'])
                self.assertTrue(settings['full_platforms'])
                self.assertEqual(expected, platforms(settings))

    def test_manual_defaults_build_all_formats_and_platforms_without_publication(self):
        expected = platforms(plan(full=True))
        for channel in ['preview', 'release']:
            with self.subTest(channel=channel):
                settings = plan(channel=channel)
                self.assertFalse(settings['publish'])
                self.assertTrue(settings['full_platforms'])
                self.assertEqual(expected, platforms(settings))

    def test_manual_full_matrix_preserves_every_release_platform(self):
        expected = {
            'java': {'ubuntu-22.04'},
            'native': {'ubuntu-22.04', 'ubuntu-24.04-arm', 'macos-15-intel', 'macos-15', 'windows-2022'},
            'deb': {'ubuntu-22.04', 'ubuntu-24.04-arm'},
            'faas': {'ubuntu-22.04', 'ubuntu-24.04-arm'},
        }
        for channel in ['preview', 'release']:
            for kind, runners in expected.items():
                with self.subTest(channel=channel, kind=kind):
                    targets = platforms(plan(kind, True, channel))
                    self.assertEqual(runners, {x['os'] for x in targets})
                    self.assertEqual(len(runners), len(targets))
                    self.assertEqual({kind}, {x['package_type'] for x in targets})
        self.assertEqual(10, len(platforms(plan(full=True))))
        intel = next(x for x in platforms(plan('native', True)) if x['os'] == 'macos-15-intel')
        self.assertEqual('25.0.1', intel['java'])

    def test_manual_builds_never_publish_even_in_release_mode(self):
        for full in [False, True]:
            settings = plan('native', full, 'release', PUBLISH='true')
            self.assertFalse(settings['publish'])
            self.assertEqual('release', settings['channel'])
            self.assertEqual(full, settings['full_platforms'])
            self.assertEqual(5 if full else 1, len(platforms(settings)))

    def test_release_push_always_publishes_all_formats_and_platforms(self):
        settings = plan('java', False, 'preview', event='push', GITHUB_REF='refs/heads/release')
        self.assertTrue(settings['publish'])
        self.assertTrue(settings['full_platforms'])
        self.assertEqual('release', settings['channel'])
        self.assertEqual(10, len(platforms(settings)))
        for overrides in [dict(GITHUB_REF='refs/heads/feature'), dict(GITHUB_REPOSITORY='someone/fork'),
                          dict(event='pull_request'),
                          dict(event='repository_dispatch', GITHUB_REF='refs/heads/release')]:
            options = dict(event='push')
            options.update(overrides)
            with self.subTest(overrides=overrides), self.assertRaises(ValueError):
                plan(**options)

    def test_one_entry_and_one_step_list_for_every_application_package(self):
        trigger = SHARED.get('on', SHARED.get(True))
        self.assertEqual(['master', 'release'], trigger['push']['branches'])
        self.assertNotIn('workflow_call', trigger)
        self.assertEqual('all', trigger['workflow_dispatch']['inputs']['package_type']['default'])
        self.assertIs(True, trigger['workflow_dispatch']['inputs']['full_platforms']['default'])
        plan_env = next(step['env'] for step in SHARED['jobs']['plan']['steps'] if step.get('id') == 'plan')
        self.assertEqual("${{ github.event_name != 'workflow_dispatch' || inputs.full_platforms }}",
                         plan_env['FULL_PLATFORMS'])
        reference = SHARED['jobs']['native_linux_amd64']['steps']
        self.assertEqual('${{ matrix.package_type }} on ${{ matrix.os }}',
                         SHARED['jobs']['native_linux_amd64']['name'])
        for job in ['native_linux_arm64', 'native_windows', 'native_macos_intel', 'native_macos_arm64', 'packages']:
            self.assertIs(reference, SHARED['jobs'][job]['steps'])
        for step in reference:
            self.assertNotIn('channel', step.get('if', ''), 'Channel must not select a different package pipeline')
        self.assertFalse(list(WORKFLOWS.glob('*-build-preview-*.yml')))
        self.assertFalse(list(WORKFLOWS.glob('*-build-release-*.yml')))
        self.assertFalse((WORKFLOWS / 'docker-release-publish.yml').exists())
        for workflow in WORKFLOWS.glob('*.yml'):
            if workflow.name == 'build-package.yml':
                continue
            text = workflow.read_text()
            for builder in ['build-final-java.sh', 'build-final-native.sh']:
                self.assertNotIn(builder, text,
                                 f'{workflow.name} must consume archived packages instead of building another candidate')

    def test_each_docker_platform_waits_for_its_own_native_package(self):
        expected = {'docker_linux_amd64': 'native_linux_amd64',
                    'docker_linux_arm64': 'native_linux_arm64', 'docker_windows': 'native_windows'}
        for docker, native in expected.items():
            job = SHARED['jobs'][docker]
            self.assertEqual({'plan', native}, set(job['needs']))
            self.assertNotIn('channel', job['if'])
            self.assertNotIn('publish', job['if'])
            self.assertNotIn('always()', job['if'])
            self.assertEqual(['plan'], [SHARED['jobs'][native]['needs']])
        self.assertEqual({'plan', 'docker_linux_amd64', 'docker_linux_arm64'},
                         set(SHARED['jobs']['docker_linux_manifest']['needs']))
        for job_id in ['native_macos_intel', 'native_macos_arm64']:
            job = SHARED['jobs'][job_id]
            self.assertEqual('plan', job['needs'])
            self.assertEqual(SHARED['jobs']['native_windows']['if'], job['if'])
        windows = SHARED['jobs']['docker_windows']
        self.assertEqual('windows-2022', windows['runs-on'])
        self.assertTrue(any(step.get('name') == 'Push Windows image' for step in windows['steps']))
        self.assertTrue(any(step.get('name') == 'Sign canonical Windows image' for step in windows['steps']))
        self.assertNotIn('docker_preview', SHARED['jobs'])
        self.assertIs(SHARED['jobs']['docker_linux_amd64']['steps'], SHARED['jobs']['docker_linux_arm64']['steps'])
        aggregate = SHARED['jobs']['docker_linux_manifest']
        self.assertNotIn('publish', aggregate['if'])
        self.assertTrue(any('assemble-linux-images.py' in step.get('run', '') and 'if' not in step
                            for step in aggregate['steps']))
        build = next(step for step in SHARED['jobs']['docker_linux_amd64']['steps']
                     if step.get('uses', '').startswith('docker/build-push-action@'))
        self.assertEqual('mode=max', build['with']['provenance'])
        self.assertIs(True, build['with']['sbom'])
        self.assertEqual('type=oci,dest=.prepared-native/image.oci.tar', build['with']['outputs'])
        for job_id in expected:
            steps = SHARED['jobs'][job_id]['steps']
            self.assertTrue(any(step.get('with', {}).get('artifact-ids') for step in steps))
            self.assertTrue(any('prepare-workflow-package.py' in step.get('run', '') for step in steps))
            self.assertFalse(any('resolve-native-package.sh' in step.get('run', '') for step in steps))
            self.assertTrue(any('image.tar' in step.get('with', {}).get('path', '') for step in steps))
            for step in steps:
                if 'channel' in step.get('if', ''):
                    self.fail('Channel cannot select a different Docker build or publication step')

    def test_faas_action_runs_between_compile_and_final_package(self):
        steps = SHARED['jobs']['packages']['steps']
        build = next(step for step in steps if step.get('id') == 'build_package')
        action = next(step for step in steps if step.get('uses', '').startswith('zrlog/zrlog-process-artifact@'))
        finish = next(step for step in steps if 'faas finish' in step.get('run', ''))
        record = next(step for step in steps if 'record-package-build.py' in step.get('run', ''))
        self.assertLess(steps.index(build), steps.index(action))
        self.assertLess(steps.index(action), steps.index(finish))
        self.assertLess(steps.index(finish), steps.index(record))
        self.assertRegex(action['uses'], r'@([0-9a-f]{40})$')
        self.assertIn('faas prepare', build['run'])
        self.assertEqual('zrlog', action['with']['artifact-file'])
        self.assertEqual('zrlog', action['with']['artifact-name'])
        for key in ['artifact-version', 'artifact-architecture']:
            self.assertEqual('${{ steps.build_package.outputs.' + key + ' }}', action['with'][key])
        self.assertEqual('${{ vars.ARTIFACT_SERVICE_URL }}', action['with']['service-url'])
        self.assertEqual('${{ secrets.ARTIFACT_SERVICE_TOKEN }}', action['with']['service-token'])
        self.assertEqual('14400', action['with']['process-timeout-seconds'])
        self.assertGreater(SHARED['jobs']['packages']['timeout-minutes'] * 60,
                           int(action['with']['process-timeout-seconds']))
        for step in [action, finish]:
            self.assertEqual("matrix.package_type == 'faas'", step['if'])
        for step in [build, action, finish, record]:
            self.assertFalse(step.get('continue-on-error', False))
            self.assertNotIn('always()', step.get('if', ''))
        for step in steps:
            self.assertNotIn('zrlog/zrlog-process-artifact', step.get('with', {}).get('repository', ''))
            self.assertNotIn('ARTIFACT_PROCESS_SCRIPT', step.get('env', {}))
            self.assertNotIn('ARTIFACT_SERVICE_TOKEN', step.get('env', {}))

    def test_index_binds_actual_bytes_to_source_and_effective_java(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            subprocess.run(['git', 'init', '-q', str(root)], check=True)
            subprocess.run(['git', '-c', 'user.name=fixture', '-c', 'user.email=fixture@example.invalid',
                            'commit', '-q', '--allow-empty', '-m', 'fixture'], cwd=root, check=True)
            sha = subprocess.check_output(['git', 'rev-parse', 'HEAD'], cwd=root, text=True).strip()
            directory = root / '.package-output/release'
            directory.mkdir(parents=True)
            package = directory / 'zrlog-4.0.0-abcdef0-release.zip'
            package.write_bytes(b'actual-package')
            env = dict(EXPECTED_COMMIT=sha, CHANNEL='release', PACKAGE_TYPE='java', REQUESTED_JAVA='11',
                       GITHUB_SERVER_URL='https://github.com', GITHUB_REPOSITORY='94fzb/zrlog',
                       GITHUB_RUN_ID='123', GITHUB_RUN_ATTEMPT='1')
            real_run = subprocess.run
            def run(command, **kwargs):
                if command == ['java', '-version']:
                    return subprocess.CompletedProcess(command, 0, '', 'effective JDK 11.0.x')
                return real_run(command, **kwargs)
            with patch.object(RECORD.subprocess, 'run', side_effect=run):
                record = RECORD.record(root, env)
                self.assertEqual(sha, record['sourceCommit'])
                self.assertEqual('effective JDK 11.0.x', record['javaVersion'])
                self.assertEqual('release/zrlog-4.0.0-abcdef0-release.zip', record['artifacts'][0]['path'])
                self.assertEqual(hashlib.sha256(package.read_bytes()).hexdigest(), record['artifacts'][0]['sha256'])
                env['EXPECTED_COMMIT'] = 'b' * 40
                with self.assertRaisesRegex(ValueError, 'source differs'):
                    RECORD.record(root, env)


@unittest.skipUnless(os.uname().sysname == 'Linux', 'FaaS runs on Linux')
class NativePackagePhases(unittest.TestCase):
    """Exercise phase handoff and real manifests with disposable compiler/assembly fixtures."""

    def setUp(self):
        temporary = tempfile.TemporaryDirectory()
        self.addCleanup(temporary.cleanup)
        self.root = Path(temporary.name)
        for name in ['shell/native/build-final-native.sh', 'shell/lib/release-manifest.sh']:
            destination = self.root / name
            destination.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(ROOT / name, destination)
        (self.root / 'zrlog-web/src/main/resources').mkdir(parents=True)
        (self.root / 'original.bin').write_bytes(b'original-native\x00\xff')
        (self.root / 'processed.bin').write_bytes(b'processed-native\x00\xfe')
        with zipfile.ZipFile(self.root / 'fixture-native.zip', 'w') as archive:
            archive.write(self.root / 'original.bin', 'zrlog')
        (self.root / 'shell/native/package-native-zip.sh').write_text('''#!/usr/bin/env bash
set -e
printf '%s\\n' "$ZRLOG_RUNTIME_TYPE" "$ZRLOG_PACKAGE_TYPE" "$@" >> compile.calls
cp fixture.properties zrlog-web/src/main/resources/build.properties
cp original.bin zrlog
mkdir -p target
cp fixture-native.zip target/zrlog-4.0.0-SNAPSHOT-native.zip
''')
        (self.root / 'shell/native/package-faas-zip.sh').write_text('''#!/usr/bin/env bash
set -e
printf '%s\\n' "$@" >> finish.calls
python3 - <<'PY'
import zipfile
with zipfile.ZipFile('target/zrlog-4.0.0-SNAPSHOT-faas.zip', 'w') as archive:
    archive.write('zrlog')
PY
''')
        (self.root / 'process.sh').write_text('''#!/usr/bin/env bash
set -e
printf '%s\\n' "$@" >> process.calls
if [[ ${FAIL_PROCESS:-} == true ]]; then exit 9; fi
cp processed.bin "$1"
''')
        self.env = os.environ.copy()
        for key in ['ARTIFACT_PROCESS_SCRIPT', 'ARTIFACT_SERVICE_URL', 'ARTIFACT_SERVICE_TOKEN', 'GITHUB_OUTPUT']:
            self.env.pop(key, None)
        self.env['GITHUB_OUTPUT'] = str(self.root / 'outputs')
        self.channel = 'preview'
        self.arch = 'Linux-amd64'
        self.metadata()

    def metadata(self):
        (self.root / 'fixture.properties').write_text(
            'version=4.0.0-SNAPSHOT\nbuildId=abcdef0\nbuildTime=2026-10-17 01:02:03+08:00\n'
            f'runMode={self.channel}\nrunModeDesc=fixture\nfileArch={self.arch}\n'
            'mirrorWebSite=https://dl.zrlog.com/\nruntimeType=native\npackageType=faas\n'
            'sourceCommit=abcdef0123456789abcdef0123456789abcdef01\n'
            'sourceRepository=https://github.com/94fzb/zrlog\nbuildWorkflow=fixture\n')

    def run_build(self, *args, check=True):
        result = subprocess.run(['bash', '-e', 'shell/native/build-final-native.sh', self.channel,
                                 'output', *args], cwd=self.root, env=self.env, capture_output=True, text=True)
        if check:
            self.assertEqual(0, result.returncode, result.stdout + result.stderr)
        return result

    def assert_final_package(self):
        directory = self.root / 'output' / self.channel
        package = directory / f'zrlog-{self.arch}-faas.zip'
        dated = directory / f'zrlog-4.0.0-SNAPSHOT-abcdef0-{self.channel}-{self.arch}-faas.zip'
        self.assertEqual(package.read_bytes(), dated.read_bytes())
        with zipfile.ZipFile(package) as archive:
            self.assertEqual((self.root / 'processed.bin').read_bytes(), archive.read('zrlog'))
        manifest = json.loads((directory / f'last.{self.arch}.faas.version.json').read_text())
        self.assertEqual(hashlib.sha256(package.read_bytes()).hexdigest(), manifest['zipSha256'])
        self.assertEqual(package.stat().st_size, manifest['zipFileSize'])
        self.assertEqual(f'https://dl.zrlog.com/{self.channel}/{dated.name}', manifest['zipDownloadUrl'])
        self.assertEqual(['native', 'faas', self.channel, '-Dmysql-scope=provided', '-Dzrlog.faas.native=true'],
                         (self.root / 'compile.calls').read_text().splitlines())
        self.assertEqual([self.arch], (self.root / 'finish.calls').read_text().splitlines())

    def test_prepare_and_finish_use_actual_metadata_and_compile_once(self):
        for channel, arch in [('preview', 'Linux-amd64'), ('release', 'Linux-arm64')]:
            with self.subTest(channel=channel, arch=arch):
                self.channel, self.arch = channel, arch
                self.metadata()
                self.run_build('faas', 'prepare')
                self.assertEqual(f'artifact-version=4.0.0-SNAPSHOT\nartifact-architecture={arch}\n',
                                 (self.root / 'outputs').read_text())
                self.assertFalse((self.root / 'output').exists())
                self.assertFalse((self.root / 'finish.calls').exists())
                shutil.copyfile(self.root / 'processed.bin', self.root / 'zrlog')
                self.run_build('faas', 'finish')
                self.assert_final_package()
                shutil.rmtree(self.root / 'output')
                for name in ['outputs', 'compile.calls', 'finish.calls']:
                    (self.root / name).unlink()

    def test_local_full_build_keeps_client_handoff(self):
        self.env['ARTIFACT_PROCESS_SCRIPT'] = str(self.root / 'process.sh')
        self.run_build('faas')
        self.assert_final_package()
        self.assertEqual(['zrlog', 'zrlog', '4.0.0-SNAPSHOT', self.arch],
                         (self.root / 'process.calls').read_text().splitlines())

    def test_processing_failure_prevents_final_package_and_manifest(self):
        self.env.update(ARTIFACT_PROCESS_SCRIPT=str(self.root / 'process.sh'), FAIL_PROCESS='true')
        self.assertNotEqual(0, self.run_build('faas', check=False).returncode)
        self.assertFalse((self.root / 'finish.calls').exists())
        self.assertFalse(list((self.root / 'output').rglob('*.*')))
        self.assertEqual((self.root / 'original.bin').read_bytes(), (self.root / 'zrlog').read_bytes())

    def test_missing_client_fails_before_local_compile_and_native_needs_no_client(self):
        self.assertNotEqual(0, self.run_build('faas', check=False).returncode)
        self.assertFalse((self.root / 'compile.calls').exists())
        self.run_build('zip')
        self.assertTrue((self.root / 'output/preview/zrlog-Linux-amd64.zip').exists())
        self.assertFalse((self.root / 'process.calls').exists())

    def test_finish_rejects_missing_binary_and_mismatched_metadata(self):
        self.assertNotEqual(0, self.run_build('faas', 'finish', check=False).returncode)
        self.run_build('faas', 'prepare')
        properties = self.root / 'zrlog-web/src/main/resources/build.properties'
        properties.write_text(properties.read_text().replace('packageType=faas', 'packageType=zip'))
        self.assertNotEqual(0, self.run_build('faas', 'finish', check=False).returncode)
        self.assertFalse((self.root / 'finish.calls').exists())
        self.assertFalse((self.root / 'output').exists())


if __name__ == '__main__':
    unittest.main()
