#!/usr/bin/env python3
"""Exercise real ZIP handoff and reject mismatched sources, bytes and platforms."""
import hashlib
import importlib.util
import json
from pathlib import Path
import tempfile
import unittest
import zipfile

SPEC = importlib.util.spec_from_file_location('prepare', Path(__file__).with_name('prepare-workflow-package.py'))
PREPARE = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(PREPARE)


class WorkflowPackageTest(unittest.TestCase):
    def fixture(self, root, channel, platform):
        source = 'abcdef0' + '1' * 33
        version = '4.0.0' + ('-SNAPSHOT' if channel == 'preview' else '')
        build_id = 'adminweb-1234567' if channel == 'preview' else 'abcdef0'
        runner_os, arch, executable, magic = PREPARE.PLATFORMS[platform]
        env = dict(ZRLOG_CHANNEL=channel, NATIVE_PLATFORM=platform,
                   ZRLOG_EXPECTED_SOURCE_COMMIT=source, ZRLOG_EXPECTED_VERSION=version,
                   GITHUB_SERVER_URL='https://github.com', GITHUB_REPOSITORY='94fzb/zrlog', GITHUB_RUN_ID='123')
        directory = root / '.package-output' / channel
        directory.mkdir(parents=True)
        evidence = root / '.package-evidence'
        evidence.mkdir()
        archive_name = f'zrlog-{version}-{build_id}-{channel}-{platform}.zip'
        archive = directory / archive_name
        with zipfile.ZipFile(archive, 'w') as target:
            target.writestr(executable, magic + b'actual fixture executable')
        alias = directory / f'zrlog-{platform}.zip'
        alias.write_bytes(archive.read_bytes())
        sha = hashlib.sha256(archive.read_bytes()).hexdigest()
        metadata = dict(sourceCommit=source, version=version, buildId=build_id,
                        sourceRepository='https://github.com/94fzb/zrlog',
                        buildWorkflow='https://github.com/94fzb/zrlog/actions/runs/123',
                        checksumAlgorithm='sha256', type='预览版本' if channel == 'preview' else '正式版本',
                        zipDownloadUrl=f'https://dl.zrlog.com/{channel}/{archive_name}',
                        zipSha256=sha, zipFileSize=archive.stat().st_size)
        native = directory / f'last.{platform}.version.json'
        native.write_text(json.dumps(metadata))
        index = dict(sourceCommit=source, channel=channel, packageType='native', workflow=metadata['buildWorkflow'],
                     runnerOs=runner_os, runnerArch=arch, runAttempt='1',
                     artifacts=[dict(path=f'{channel}/{p.name}', size=p.stat().st_size, sha256=sha)
                                for p in [archive, alias]])
        index_path = evidence / 'build.json'
        index_path.write_text(json.dumps(index))
        return env, native, index_path, archive, alias

    def test_same_handoff_for_both_channels_and_every_docker_platform(self):
        for channel in ['preview', 'release']:
            for platform in PREPARE.PLATFORMS:
                with self.subTest(channel=channel, platform=platform), tempfile.TemporaryDirectory() as temporary:
                    root = Path(temporary)
                    env, *_ = self.fixture(root, channel, platform)
                    settings = PREPARE.prepare(root, root / 'output', env)
                    executable = PREPARE.PLATFORMS[platform][2]
                    self.assertTrue((root / 'output/package' / executable).exists())
                    self.assertTrue((root / 'output/native.sha256').exists())
                    self.assertEqual('4.0.0-SNAPSHOT-adminweb-1234567' if channel == 'preview' else '4.0.0',
                                     settings['image_tag'])

    def test_reject_mismatched_identity_run_platform_index_and_bytes(self):
        for field, value, document in [
            ('sourceCommit', 'b' * 40, 'metadata'), ('version', '4.0.1', 'metadata'),
            ('checksumAlgorithm', 'md5', 'metadata'), ('type', '正式版本', 'metadata'),
            ('zipFileSize', 0, 'metadata'), ('zipSha256', '0' * 64, 'metadata'),
            ('zipDownloadUrl', 'https://dl.zrlog.com/preview/wrong.zip', 'metadata'),
            ('buildWorkflow', 'https://github.com/94fzb/zrlog/actions/runs/999', 'metadata'),
            ('sourceRepository', 'https://github.com/other/repo', 'metadata'),
            ('sourceCommit', 'b' * 40, 'index'), ('channel', 'release', 'index'),
            ('packageType', 'faas', 'index'), ('runnerArch', 'ARM64', 'index'),
            ('runnerOs', 'Windows', 'index'), ('runAttempt', '0', 'index'),
            ('workflow', 'https://github.com/94fzb/zrlog/actions/runs/999', 'index'),
            ('artifacts', [], 'index'),
        ]:
            with self.subTest(field=field, document=document), tempfile.TemporaryDirectory() as temporary:
                root = Path(temporary)
                env, native, index, *_ = self.fixture(root, 'preview', 'Linux-amd64')
                path = native if document == 'metadata' else index
                data = json.loads(path.read_text())
                data[field] = value
                path.write_text(json.dumps(data))
                with self.assertRaises(ValueError):
                    PREPARE.prepare(root, root / 'output', env)
        for position in [3, 4]:
            with tempfile.TemporaryDirectory() as temporary:
                root = Path(temporary)
                fixture = self.fixture(root, 'release', 'Windows-x86_64')
                with fixture[position].open('ab') as archive:
                    archive.write(b'corrupted')
                with self.assertRaises(ValueError):
                    PREPARE.prepare(root, root / 'output', fixture[0])


if __name__ == '__main__':
    unittest.main()
