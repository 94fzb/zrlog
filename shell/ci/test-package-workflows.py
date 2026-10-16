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
PLAN_SPEC = importlib.util.spec_from_file_location('plan_package', Path(__file__).with_name('plan-package-build.py'))
PLANNER = importlib.util.module_from_spec(PLAN_SPEC)
PLAN_SPEC.loader.exec_module(PLANNER)
SPEC = importlib.util.spec_from_file_location('record_package', Path(__file__).with_name('record-package-build.py'))
RECORD = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(RECORD)


def plan(kind='all', full=False, channel='preview', event='workflow_dispatch', **overrides):
    env = dict(PACKAGE_TYPE=kind, CHANNEL=channel, FULL_PLATFORMS=str(full).lower(),
               GITHUB_REPOSITORY='94fzb/zrlog', GITHUB_REF='refs/heads/master',
               GITHUB_EVENT_NAME=event, GITHUB_SHA='a' * 40)
    env.update(overrides)
    return PLANNER.plan(env)


def platforms(settings):
    targets = list(settings['matrix']['include'])
    if settings['build_native']:
        targets.append(dict(package_type='native', os='ubuntu-22.04', java='25'))
        if settings['full_platforms']:
            for job in ['native_linux_arm64', 'native_windows']:
                targets.extend(SHARED['jobs'][job]['strategy']['matrix']['include'])
    return targets


class PackageWorkflowContract(unittest.TestCase):
    def test_daily_builds_all_formats_on_linux_amd64_only(self):
        for event in ['push', 'repository_dispatch']:
            settings = plan(event=event)
            self.assertEqual('preview', settings['channel'])
            self.assertTrue(settings['publish'])
            targets = platforms(settings)
            self.assertEqual({'java', 'native', 'deb', 'faas'}, {x['package_type'] for x in targets})
            self.assertEqual(['ubuntu-22.04'] * 4, [x['os'] for x in targets])

    def test_manual_full_matrix_preserves_every_release_platform(self):
        expected = {
            'java': {'ubuntu-22.04'},
            'native': {'ubuntu-22.04', 'ubuntu-24.04-arm', 'macos-15-intel', 'macos-14', 'windows-2022'},
            'deb': {'ubuntu-22.04', 'ubuntu-24.04-arm'},
            'faas': {'ubuntu-22.04', 'ubuntu-24.04-arm'},
        }
        for kind, runners in expected.items():
            targets = platforms(plan(kind, True, 'release'))
            self.assertEqual(runners, {x['os'] for x in targets})
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
        self.assertIs(False, trigger['workflow_dispatch']['inputs']['full_platforms']['default'])
        reference = SHARED['jobs']['native_linux_amd64']['steps']
        self.assertEqual('${{ matrix.package_type }} on ${{ matrix.os }}',
                         SHARED['jobs']['native_linux_amd64']['name'])
        for job in ['native_linux_arm64', 'native_windows', 'packages']:
            self.assertIs(reference, SHARED['jobs'][job]['steps'])
        self.assertFalse(list(WORKFLOWS.glob('*-build-preview-*.yml')))
        self.assertFalse(list(WORKFLOWS.glob('*-build-release-*.yml')))
        self.assertFalse((WORKFLOWS / 'docker-release-publish.yml').exists())

    def test_each_docker_platform_waits_for_its_own_native_package(self):
        expected = {'docker_preview': 'native_linux_amd64', 'docker_linux_amd64': 'native_linux_amd64',
                    'docker_linux_arm64': 'native_linux_arm64', 'docker_windows': 'native_windows'}
        for docker, native in expected.items():
            job = SHARED['jobs'][docker]
            self.assertEqual({'plan', native}, set(job['needs']))
            self.assertIn("needs.plan.outputs.publish == 'true'", job['if'])
            self.assertNotIn('always()', job['if'])
            self.assertEqual(['plan'], [SHARED['jobs'][native]['needs']])
        self.assertEqual({'plan', 'docker_linux_amd64', 'docker_linux_arm64'},
                         set(SHARED['jobs']['docker_linux_manifest']['needs']))
        windows = SHARED['jobs']['docker_windows']
        self.assertEqual('windows-2022', windows['runs-on'])
        self.assertTrue(any(step.get('name') == 'Push Windows image' for step in windows['steps']))
        self.assertTrue(any(step.get('name') == 'Sign canonical Windows image' for step in windows['steps']))

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
