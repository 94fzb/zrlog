#!/usr/bin/env python3
"""Keep signed automatic producers and unsigned manual archives distinct."""
import importlib.util
import json
from pathlib import Path
import tempfile
import unittest
import zipfile


SPEC = importlib.util.spec_from_file_location('verifier', Path(__file__).with_name('verify-package-archives.py'))
VERIFIER = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(VERIFIER)
SOURCE = 'a' * 40
WORKFLOW = 'https://github.com/94fzb/zrlog/actions/runs/123'


def fixture(root, channel, signed):
    directory = root / '.package-output' / channel
    directory.mkdir(parents=True)
    name = f'zrlog-4.0.0-aaaaaaa-{channel}.zip'
    package = directory / name
    with zipfile.ZipFile(package, 'w') as archive:
        archive.writestr('readme.txt', 'actual package bytes')
    digest = VERIFIER.sha(package)
    metadata = dict(sourceCommit=SOURCE, buildWorkflow=WORKFLOW, checksumAlgorithm='sha256',
                    type='预览版本' if channel == 'preview' else '正式版本',
                    zipSha256=digest, zipFileSize=package.stat().st_size,
                    zipDownloadUrl=f'https://dl.example.invalid/{channel}/{name}')
    (directory / 'last.version.json').write_text(json.dumps(metadata))
    evidence = root / '.package-evidence'
    evidence.mkdir()
    index = dict(sourceCommit=SOURCE, workflow=WORKFLOW, channel=channel, runAttempt='2',
                 packageType='java', runnerOs='Linux', runnerArch='X64', javaVersion='11', requestedJava='11',
                 artifacts=[dict(path=f'{channel}/{name}', sha256=digest, size=package.stat().st_size)])
    (evidence / 'build.json').write_text(json.dumps(index))
    if signed:
        package.with_name(name + '.sha256').write_text(f'{digest}  {name}\n')
        sbom = package.with_name(name + '.sbom.cdx.json')
        sbom.write_text('{"bomFormat":"CycloneDX"}\n')
        package.with_name(name + '.sigstore.json').write_text('{"verificationMaterial":{}}\n')
        provenance = dict(artifact=dict(name=name, size=package.stat().st_size, sha256=digest),
                          source=dict(repository='https://github.com/94fzb/zrlog', commit=SOURCE),
                          build=dict(workflow=WORKFLOW),
                          evidence=dict(sbom=dict(name=sbom.name, sha256=VERIFIER.sha(sbom)),
                                        signatureBundle=name + '.sigstore.json'))
        package.with_name(name + '.provenance.json').write_text(json.dumps(provenance))
    return package


class PackageArchiveTest(unittest.TestCase):
    def test_both_channels_accept_signed_automatic_and_unsigned_manual_archives(self):
        for channel in ['preview', 'release']:
            for published in [False, True]:
                with self.subTest(channel=channel, published=published), tempfile.TemporaryDirectory() as temporary:
                    root = Path(temporary)
                    fixture(root, channel, signed=published)
                    result = VERIFIER.verify_package(root, SOURCE, '123', channel, published)
                    self.assertEqual('published' if published else 'archive', result['publicationMode'])
                    self.assertFalse(result['signatureVerificationPerformed'])
                    if published:
                        self.assertTrue(result['packages'][0]['trustSidecarsMatchActualBytes'])

    def test_signed_manual_archive_is_rejected(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            fixture(root, 'release', signed=True)
            with self.assertRaisesRegex(AssertionError, 'Manual archive'):
                VERIFIER.verify_package(root, SOURCE, '123', 'release')

    def test_missing_or_mismatched_automatic_sidecars_are_rejected(self):
        for suffix in ['.sha256', '.sigstore.json', '.sbom.cdx.json', '.provenance.json']:
            with self.subTest(missing=suffix), tempfile.TemporaryDirectory() as temporary:
                root = Path(temporary)
                package = fixture(root, 'preview', signed=True)
                package.with_name(package.name + suffix).unlink()
                with self.assertRaises((AssertionError, FileNotFoundError)):
                    VERIFIER.verify_package(root, SOURCE, '123', 'preview', published=True)
        for field in ['source', 'artifact', 'sbom']:
            with self.subTest(tampered=field), tempfile.TemporaryDirectory() as temporary:
                root = Path(temporary)
                package = fixture(root, 'release', signed=True)
                path = package.with_name(package.name + '.provenance.json')
                value = json.loads(path.read_text())
                if field == 'source':
                    value['source']['commit'] = 'b' * 40
                elif field == 'artifact':
                    value['artifact']['sha256'] = '0' * 64
                else:
                    value['evidence']['sbom']['sha256'] = '0' * 64
                path.write_text(json.dumps(value))
                with self.assertRaises(AssertionError):
                    VERIFIER.verify_package(root, SOURCE, '123', 'release', published=True)

    def test_changed_package_bytes_are_rejected_in_both_modes(self):
        for published in [False, True]:
            with self.subTest(published=published), tempfile.TemporaryDirectory() as temporary:
                root = Path(temporary)
                fixture(root, 'release', signed=published).write_bytes(b'changed')
                with self.assertRaises(AssertionError):
                    VERIFIER.verify_package(root, SOURCE, '123', 'release', published)


if __name__ == '__main__':
    unittest.main()
