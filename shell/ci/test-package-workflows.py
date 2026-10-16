#!/usr/bin/env python3
"""Guard the shared build path, supported platforms and publication boundary."""
import hashlib
import importlib.util
import json
import os
from pathlib import Path
import subprocess
import tempfile
import unittest
from unittest.mock import patch

import yaml

ROOT = Path(__file__).resolve().parents[2]
WORKFLOWS = ROOT / '.github/workflows'
SHARED = yaml.safe_load((WORKFLOWS / 'build-package.yml').read_text())
PLAN = SHARED['jobs']['plan']['steps'][0]['run']
SPEC = importlib.util.spec_from_file_location('record_package', Path(__file__).with_name('record-package-build.py'))
RECORD = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(RECORD)


def plan(kind='native', full=False, channel='preview', publish=False, **overrides):
    with tempfile.TemporaryDirectory() as temporary:
        output = Path(temporary) / 'output'
        env = dict(os.environ, PACKAGE_TYPE=kind, CHANNEL=channel, SOURCE_COMMIT='a' * 40,
                   FULL_PLATFORMS=str(full).lower(), PUBLISH=str(publish).lower(),
                   GITHUB_REPOSITORY='94fzb/zrlog', GITHUB_REF='refs/heads/master',
                   GITHUB_EVENT_NAME='push', GITHUB_SHA='a' * 40, GITHUB_OUTPUT=str(output))
        env.update(overrides)
        result = subprocess.run(['bash', '-e', '-c', PLAN], env=env, capture_output=True, text=True)
        if result.returncode:
            raise ValueError(result.stderr)
        return json.loads(output.read_text().removeprefix('matrix='))['include']


class PackageWorkflowContract(unittest.TestCase):
    def test_daily_builds_only_linux_amd64_for_every_package_type(self):
        for kind in ['java', 'native', 'deb', 'faas']:
            with self.subTest(kind=kind):
                self.assertEqual([dict(os='ubuntu-22.04', java='11' if kind == 'java' else '25')], plan(kind))

    def test_full_build_covers_release_platforms_and_intel_toolchain(self):
        self.assertEqual(['ubuntu-22.04'], [x['os'] for x in plan('java', True)])
        for kind in ['deb', 'faas']:
            self.assertEqual(['ubuntu-22.04', 'ubuntu-24.04-arm'], [x['os'] for x in plan(kind, True)])
        native = plan('native', True)
        self.assertEqual(['ubuntu-22.04', 'ubuntu-24.04-arm', 'macos-15-intel', 'macos-14', 'windows-2022'],
                         [x['os'] for x in native])
        self.assertEqual(['25', '25', '25.0.1', '25', '25'], [x['java'] for x in native])
        self.assertEqual(native, plan('native', True, 'release'))

    def test_release_cannot_publish_partial_matrix_or_from_manual_event(self):
        for kwargs in [dict(), dict(full=True, GITHUB_EVENT_NAME='workflow_dispatch'),
                       dict(full=True, SOURCE_COMMIT='b' * 40),
                       dict(full=True, GITHUB_REF='refs/heads/master'),
                       dict(full=True, GITHUB_REPOSITORY='someone/fork')]:
            options = dict(channel='release', publish=True, GITHUB_REF='refs/heads/release')
            options.update(kwargs)
            with self.subTest(options=options), self.assertRaises(ValueError):
                plan(**options)
        self.assertEqual(5, len(plan(full=True, channel='release', publish=True,
                                     GITHUB_REF='refs/heads/release')))
        self.assertEqual(5, len(plan(full=True, channel='release', GITHUB_EVENT_NAME='workflow_dispatch')))

    def test_every_entry_uses_the_shared_builder_and_forwards_platform_selection(self):
        for kind in ['java', 'native', 'deb', 'faas']:
            pattern = f'{kind}-build-{{}}-' + ('deb.yml' if kind == 'deb' else 'package-zip.yml')
            for channel in ['preview', 'release']:
                workflow = yaml.safe_load((WORKFLOWS / pattern.format(channel)).read_text())
                build = workflow['jobs']['build']
                self.assertEqual('./.github/workflows/build-package.yml', build['uses'])
                self.assertNotIn('steps', build)
                self.assertEqual(kind, build['with']['package_type'])
                self.assertEqual('${{ github.sha }}', build['with']['source_commit'])
                if channel == 'release':
                    self.assertIs(True, build['with']['full_platforms'])
                    self.assertIs(True, build['with']['publish'])
                else:
                    self.assertEqual("${{ github.event_name != 'workflow_dispatch' }}", build['with']['publish'])
                    self.assertIn('inputs.full_platforms', build['with']['full_platforms'])
                    trigger = workflow.get('on', workflow.get(True))
                    self.assertIs(False, trigger['workflow_dispatch']['inputs']['full_platforms']['default'])

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
                self.assertEqual(hashlib.sha256(package.read_bytes()).hexdigest(), record['artifacts'][0]['sha256'])
                env['EXPECTED_COMMIT'] = 'b' * 40
                with self.assertRaisesRegex(ValueError, 'source differs'):
                    RECORD.record(root, env)


if __name__ == '__main__':
    unittest.main()
