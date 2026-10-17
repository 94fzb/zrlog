#!/usr/bin/env python3
"""Exercise real OCI blobs, combined indexes and rejection of mixed build inputs."""
from contextlib import ExitStack
import hashlib
import importlib.util
import io
import json
import os
from pathlib import Path
import tarfile
import tempfile
import unittest

SPEC = importlib.util.spec_from_file_location('assembly', Path(__file__).with_name('assemble-linux-images.py'))
ASSEMBLY = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(ASSEMBLY)


class LinuxImageAssemblyTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.env = dict(GITHUB_SERVER_URL='https://github.com', GITHUB_REPOSITORY='94fzb/zrlog',
                        GITHUB_RUN_ID='123', ZRLOG_EXPECTED_SOURCE_COMMIT='a' * 40,
                        ZRLOG_EXPECTED_VERSION='4.0.0-SNAPSHOT', ZRLOG_CHANNEL='release')

    def fixture(self, arch, *, corrupt=False, wrong_commit=False, missing_sbom=False):
        directory = self.root / arch
        directory.mkdir()
        native = dict(sourceCommit='a' * 40, version='4.0.0-SNAPSHOT', buildId='aaaaaaa',
                      buildWorkflow='https://github.com/94fzb/zrlog/actions/runs/123', type='正式版本',
                      zipSha256=hashlib.sha256(('zip-' + arch).encode()).hexdigest())
        (directory / 'native-image-input.json').write_text(json.dumps(native))
        producer = dict(sourceCommit='a' * 40, workflow=native['buildWorkflow'], channel='release',
                        packageType='native', runnerOs='Linux', runnerArch='X64' if arch == 'amd64' else 'ARM64')
        (directory / 'build.json').write_text(json.dumps(producer))
        binary_sha = hashlib.sha256(arch.encode()).hexdigest()
        (directory / 'native.sha256').write_text(binary_sha + '  zrlog\n')
        verification = {key: native[key] for key in ['sourceCommit', 'version', 'buildId']}
        verification.update(platform='linux/' + arch, nativeExecutableSha256=binary_sha)
        (directory / 'image-verification.json').write_text(json.dumps(verification))
        blobs = {}

        def blob(value, media):
            data = value if isinstance(value, bytes) else json.dumps(value).encode()
            sha = hashlib.sha256(data).hexdigest()
            blobs['blobs/sha256/' + sha] = data
            return dict(mediaType=media, digest='sha256:' + sha, size=len(data))

        config = blob(dict(os='linux', architecture=arch, config=dict(Labels={
            'org.opencontainers.image.version': native['version'],
            'org.opencontainers.image.revision': 'b' * 40 if wrong_commit else native['sourceCommit']})),
            'application/vnd.oci.image.config.v1+json')
        layer = blob(b'actual layer ' + arch.encode(), 'application/vnd.oci.image.layer.v1.tar')
        runtime = blob(dict(schemaVersion=2, mediaType=ASSEMBLY.MANIFEST, config=config, layers=[layer]),
                       ASSEMBLY.MANIFEST)
        runtime['platform'] = dict(os='linux', architecture=arch)
        predicates = ['https://slsa.dev/provenance/v0.2']
        if not missing_sbom:
            predicates.append('https://spdx.dev/Document')
        statements = []
        for predicate in predicates:
            statement = blob(dict(predicateType=predicate), 'application/vnd.in-toto+json')
            statement['annotations'] = {'in-toto.io/predicate-type': predicate}
            statements.append(statement)
        attestation = blob(dict(schemaVersion=2, mediaType=ASSEMBLY.MANIFEST, config=config, layers=statements),
                           ASSEMBLY.MANIFEST)
        attestation.update(platform=dict(os='unknown', architecture='unknown'), annotations={
            'vnd.docker.reference.type': 'attestation-manifest', 'vnd.docker.reference.digest': runtime['digest']})
        child = blob(dict(schemaVersion=2, mediaType=ASSEMBLY.INDEX, manifests=[runtime, attestation]), ASSEMBLY.INDEX)
        files = {**blobs, 'oci-layout': b'{"imageLayoutVersion":"1.0.0"}',
                 'index.json': json.dumps(dict(schemaVersion=2, manifests=[child])).encode()}
        if corrupt:
            files['blobs/sha256/' + layer['digest'][7:]] = b'x' * layer['size']
        with tarfile.open(directory / 'image.oci.tar', 'w') as target:
            for name, data in files.items():
                info = tarfile.TarInfo(name)
                info.size = len(data)
                target.addfile(info, io.BytesIO(data))
        return runtime['digest']

    def run_assembly(self, platforms=('linux/amd64', 'linux/arm64')):
        return ASSEMBLY.assemble(self.root, self.root / 'result', platforms, self.env)

    def test_combines_runtime_and_attestations_without_changing_runtime_digests(self):
        expected = {self.fixture('amd64'), self.fixture('arm64')}
        report = self.run_assembly()
        self.assertFalse(report['distributionMetadataEligible'])
        self.assertEqual(expected, {entry['runtime']['digest'] for entry in report['inputs']})
        with ExitStack() as stack:
            for image in report['images']:
                archive = ASSEMBLY.Archive(self.root / 'result' / image['archive'], stack)
                manifests = archive.manifests()
                self.assertEqual(4 if image['role'] == 'dockerhub' else 2, len(manifests))
                self.assertEqual(expected, {desc['digest'] for desc, _ in manifests
                                           if desc['platform']['os'] == 'linux'})

    def test_rejects_missing_or_duplicate_platform(self):
        self.fixture('amd64')
        with self.assertRaisesRegex(ValueError, 'platforms are missing'):
            self.run_assembly()
        self.fixture('arm64')
        with self.assertRaisesRegex(ValueError, 'platforms are missing'):
            self.run_assembly(('linux/amd64',))

    def test_rejects_corrupted_blob(self):
        self.fixture('amd64', corrupt=True)
        with self.assertRaisesRegex(ValueError, 'checksum mismatch'):
            self.run_assembly(('linux/amd64',))

    def test_rejects_image_from_another_source(self):
        self.fixture('amd64', wrong_commit=True)
        with self.assertRaisesRegex(ValueError, 'source labels mismatch'):
            self.run_assembly(('linux/amd64',))

    def test_rejects_missing_sbom(self):
        self.fixture('amd64', missing_sbom=True)
        with self.assertRaisesRegex(ValueError, 'SBOM and provenance'):
            self.run_assembly(('linux/amd64',))

    def test_rejects_producer_from_another_run(self):
        self.fixture('amd64')
        self.env['GITHUB_RUN_ID'] = '456'
        with self.assertRaisesRegex(ValueError, 'producer mismatch'):
            self.run_assembly(('linux/amd64',))


if __name__ == '__main__':
    unittest.main()
