#!/usr/bin/env python3
"""Verify every actual package from a complete-platform archived producer."""
import argparse
import hashlib
import json
from pathlib import Path
import struct
import subprocess
import zipfile
from urllib.parse import urlparse


def stream_sha(source):
    digest = hashlib.sha256()
    for block in iter(lambda: source.read(1024 * 1024), b''):
        digest.update(block)
    return digest.hexdigest()


def sha(path):
    with path.open('rb') as source:
        return stream_sha(source)


def verify_trust_sidecars(file, source_commit, workflow):
    checksum = sha(file)
    assert file.with_name(file.name + '.sha256').read_text().split() == [checksum, file.name]
    provenance = json.loads(file.with_name(file.name + '.provenance.json').read_text())
    assert provenance['artifact'] == dict(name=file.name, size=file.stat().st_size, sha256=checksum)
    assert provenance['source'] == dict(repository='https://github.com/94fzb/zrlog', commit=source_commit)
    assert provenance['build']['workflow'] == workflow
    sbom = file.with_name(file.name + '.sbom.cdx.json')
    assert provenance['evidence']['sbom']['name'] == sbom.name
    assert provenance['evidence']['sbom']['sha256'] == sha(sbom)
    assert json.loads(sbom.read_text())['bomFormat'] == 'CycloneDX'
    bundle = file.with_name(file.name + '.sigstore.json')
    assert provenance['evidence']['signatureBundle'] == bundle.name
    assert isinstance(json.loads(bundle.read_text()), dict) and bundle.stat().st_size > 0


def verify_package(root, source_commit, producer_run, channel, published=False):
    index=json.loads((root/'.package-evidence/build.json').read_text())
    assert index['sourceCommit']==source_commit and index['workflow']==f'https://github.com/94fzb/zrlog/actions/runs/{producer_run}'
    assert index['channel']==channel and int(index['runAttempt'])>0
    base=root/'.package-output'
    metadata_files=list((base/channel).glob('last*.json'));assert len(metadata_files)==1
    metadata=json.loads(metadata_files[0].read_text())
    assert metadata['sourceCommit']==source_commit
    assert metadata['buildWorkflow']==index['workflow'] and metadata['checksumAlgorithm']=='sha256'
    assert metadata['type']==('预览版本' if channel=='preview' else '正式版本')
    result={k:index[k] for k in ['sourceCommit','workflow','channel','packageType','runnerOs','runnerArch','javaVersion','requestedJava']}
    result['packages']=[]
    if not published:
        assert not list(root.rglob('*.sigstore.json')), 'Manual archive must not contain signing outputs'
    versioned_names = {Path(urlparse(metadata[key]).path).name
                       for key in ['zipDownloadUrl', 'warDownloadUrl'] if key in metadata}
    assert versioned_names and versioned_names <= {Path(entry['path']).name for entry in index['artifacts']}
    for entry in index['artifacts']:
        relative=entry['path'];assert '\\' not in relative
        file=base/relative;assert file.is_relative_to(base) and '..' not in Path(relative).parts
        value=sha(file);assert value==entry['sha256'] and file.stat().st_size==entry['size']
        prefix='war' if file.suffix=='.war' else 'zip'
        assert metadata[prefix+'Sha256']==value and metadata[prefix+'FileSize']==file.stat().st_size
        item=dict(name=file.name,sha256=value,size=file.stat().st_size)
        if published and file.name in versioned_names:
            verify_trust_sidecars(file, source_commit, index['workflow'])
            item['trustSidecarsMatchActualBytes'] = True
            item['signatureBundlePresent'] = True
        if file.suffix in {'.zip','.war'}:
            with zipfile.ZipFile(file) as archive:
                assert archive.testzip() is None
                if index['packageType'] in {'native','faas'}:
                    matches=[]
                    for name in archive.namelist():
                        if Path(name).name in {'zrlog','zrlog.exe','bootstrap'}:
                            with archive.open(name) as binary:header=binary.read(64)
                            if header[:4] in {b'\x7fELF',b'\xcf\xfa\xed\xfe'} or header[:2]==b'MZ':matches.append((name,header))
                    assert len(matches)==1,matches
                    name,header=matches[0]
                    if header[:4]==b'\x7fELF':
                        assert struct.unpack('<H',header[18:20])[0]==(62 if index['runnerArch']=='X64' else 183)
                    elif header[:4]==b'\xcf\xfa\xed\xfe':
                        assert struct.unpack('<I',header[4:8])[0]==(0x01000007 if index['runnerArch']=='X64' else 0x0100000c)
                    else:
                        assert index['runnerOs']=='Windows'
                        with archive.open(name) as binary:
                            binary.seek(struct.unpack('<I',header[60:64])[0]);assert binary.read(4)==b'PE\x00\x00';assert binary.read(2)==b'\x64\x86'
                    with archive.open(name) as binary:item['executableSha256']=stream_sha(binary)
                    item['executable']=name
        elif file.suffix=='.deb':
            arch=subprocess.check_output(['dpkg-deb','--field',str(file),'Architecture'],text=True).strip()
            assert arch==('amd64' if index['runnerArch']=='X64' else 'arm64')
            version=subprocess.check_output(['dpkg-deb','--field',str(file),'Version'],text=True).strip()
            assert version==metadata['debPackageVersion']
            item.update(debArchitecture=arch,debPackageVersion=version)
        result['packages'].append(item)
    result['updateJsonMatchesActualBytes']=True
    result['publicationMode'] = 'published' if published else 'archive'
    result['signatureVerificationPerformed'] = False
    return result



def verify(inputs, source, producer, channel, published=False):
    records = []
    for path in sorted(inputs.rglob('.package-evidence/build.json')):
        records.append(verify_package(path.parent.parent, source, producer, channel, published))
    expected = {
        'java': {('Linux', 'X64')},
        'native': {('Linux', 'X64'), ('Linux', 'ARM64'), ('Windows', 'X64'), ('macOS', 'X64'), ('macOS', 'ARM64')},
        'deb': {('Linux', 'X64'), ('Linux', 'ARM64')},
        'faas': {('Linux', 'X64'), ('Linux', 'ARM64')},
    }
    assert len(records) == 10, 'Expected all ten application package jobs'
    for kind, platforms in expected.items():
        selected = [r for r in records if r['packageType'] == kind]
        assert len(selected) == len(platforms)
        assert {(r['runnerOs'], r['runnerArch']) for r in selected} == platforms
    return dict(sourceCommit=source, producerRun=producer, channel=channel,
                allPackageBytesAndMetadataMatch=True, packages=records,
                qualification='Archive structure and checksums only; application not executed; no formal publication')


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--inputs', required=True, type=Path)
    parser.add_argument('--source', required=True)
    parser.add_argument('--producer-run', required=True)
    parser.add_argument('--channel', required=True, choices=['preview', 'release'])
    parser.add_argument('--output', required=True, type=Path)
    parser.add_argument('--published', action='store_true', help='Require trust sidecars from an automatic publishing producer')
    args = parser.parse_args()
    result = verify(args.inputs, args.source, args.producer_run, args.channel, args.published)
    args.output.write_text(json.dumps(result, indent=2, ensure_ascii=False) + '\n')
    print('Verified all ten actual application package archives')
