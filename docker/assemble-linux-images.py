#!/usr/bin/env python3
"""Verify BuildKit OCI archives and assemble the same Linux indexes in every mode."""
import argparse
from contextlib import ExitStack
import hashlib
import io
import json
import os
from pathlib import Path
import re
import tarfile


INDEX = 'application/vnd.oci.image.index.v1+json'
MANIFEST = 'application/vnd.oci.image.manifest.v1+json'


def require(condition, message):
    if not condition:
        raise ValueError(message)


def encode(value):
    return json.dumps(value, separators=(',', ':'), sort_keys=True).encode()


def digest(data):
    return 'sha256:' + hashlib.sha256(data).hexdigest()


def file_sha(path):
    with path.open('rb') as source:
        return stream_sha(source)


def stream_sha(source):
    result = hashlib.sha256()
    for block in iter(lambda: source.read(1024 * 1024), b''):
        result.update(block)
    return result.hexdigest()


class Archive:
    def __init__(self, path, stack):
        self.path = path
        self.tar = stack.enter_context(tarfile.open(path))
        self.members = {}
        for member in self.tar:
            if member.isdir():
                continue
            require(member.isfile() and member.name not in self.members,
                    'OCI archive has a link or duplicate member')
            require(member.name in {'index.json', 'oci-layout'} or
                    re.fullmatch(r'blobs/sha256/[0-9a-f]{64}', member.name),
                    'Unexpected OCI archive member')
            self.members[member.name] = member
        require(json.loads(self.read('oci-layout')) == {'imageLayoutVersion': '1.0.0'},
                'Unsupported OCI layout')
        self.verified = {}

    def read(self, name):
        require(name in self.members, 'Missing OCI member: ' + name)
        require(self.members[name].size <= 16 * 1024 * 1024, 'OCI JSON is too large')
        return self.tar.extractfile(self.members[name]).read()

    def verify(self, descriptor):
        value = descriptor['digest']
        require(re.fullmatch(r'sha256:[0-9a-f]{64}', value), 'Unsupported OCI digest')
        name = 'blobs/sha256/' + value.removeprefix('sha256:')
        require(name in self.members and self.members[name].size == descriptor['size'],
                'OCI blob is missing or has a different size')
        if value not in self.verified:
            with self.tar.extractfile(self.members[name]) as source:
                require('sha256:' + stream_sha(source) == value,
                        'OCI blob checksum mismatch')
            self.verified[value] = self.members[name]
        return name

    def manifests(self):
        found = []

        def visit(descriptor, depth=0):
            require(depth < 8, 'OCI index nesting exceeds the supported depth')
            name = self.verify(descriptor)
            document = json.loads(self.read(name))
            if document.get('mediaType') == INDEX:
                for child in document['manifests']:
                    visit(child, depth + 1)
            else:
                require(document.get('mediaType') == MANIFEST, 'Unsupported OCI manifest')
                self.verify(document['config'])
                for layer in document['layers']:
                    self.verify(layer)
                found.append((descriptor, document))

        for descriptor in json.loads(self.read('index.json'))['manifests']:
            visit(descriptor)
        return found


def inspect(archive, directory, env):
    native = json.loads((directory / 'native-image-input.json').read_text())
    producer = json.loads((directory / 'build.json').read_text())
    workflow = f"{env['GITHUB_SERVER_URL']}/{env['GITHUB_REPOSITORY']}/actions/runs/{env['GITHUB_RUN_ID']}"
    require(native['sourceCommit'] == env['ZRLOG_EXPECTED_SOURCE_COMMIT'] == producer['sourceCommit']
            and native['version'] == env['ZRLOG_EXPECTED_VERSION']
            and native['buildWorkflow'] == workflow == producer['workflow']
            and producer['channel'] == env['ZRLOG_CHANNEL'] and producer['packageType'] == 'native'
            and native['type'] == ('预览版本' if env['ZRLOG_CHANNEL'] == 'preview' else '正式版本'),
            'Image input source, version, channel or producer mismatch')
    architecture = {'X64': 'amd64', 'ARM64': 'arm64'}.get(producer['runnerArch'])
    require(producer['runnerOs'] == 'Linux' and architecture, 'Unsupported Linux input platform')
    platform = 'linux/' + architecture
    descriptors = archive.manifests()
    runtime = [(desc, manifest) for desc, manifest in descriptors
               if desc.get('platform', {}).get('os') == 'linux'
               and desc['platform'].get('architecture') == architecture]
    require(len(runtime) == 1, 'Expected exactly one Linux runtime manifest')
    descriptor, manifest = runtime[0]
    config = json.loads(archive.read(archive.verify(manifest['config'])))
    labels = config['config']['Labels']
    require(config['os'] == 'linux' and config['architecture'] == architecture
            and labels['org.opencontainers.image.version'] == native['version']
            and labels['org.opencontainers.image.revision'] == native['sourceCommit'],
            'OCI runtime platform or source labels mismatch')
    attestations = [desc for desc, _ in descriptors if desc != descriptor]
    require(attestations and all(desc.get('annotations', {}).get('vnd.docker.reference.type') ==
            'attestation-manifest' and desc['annotations'].get('vnd.docker.reference.digest') ==
            descriptor['digest'] for desc in attestations), 'Missing or mismatched BuildKit attestations')
    predicates = {layer.get('annotations', {}).get('in-toto.io/predicate-type', '')
                  for desc, document in descriptors if desc in attestations for layer in document['layers']}
    require('https://spdx.dev/Document' in predicates and
            any(value.startswith('https://slsa.dev/provenance/') for value in predicates),
            'Both SBOM and provenance attestations are required')
    binary_sha = (directory / 'native.sha256').read_text().split()[0]
    require(re.fullmatch(r'[0-9a-f]{64}', binary_sha), 'Invalid Native executable checksum')
    require(re.fullmatch(r'[0-9a-f]{64}', native['zipSha256']), 'Invalid Native ZIP checksum')
    verification = json.loads((directory / 'image-verification.json').read_text())
    require(verification == {'platform': platform, 'sourceCommit': native['sourceCommit'],
                            'version': native['version'], 'buildId': native['buildId'],
                            'nativeExecutableSha256': binary_sha},
            'Image executable verification does not match the Native package')
    return dict(platform=platform, native=native, runtime=descriptor, attestations=attestations,
                nativeExecutableSha256=binary_sha, nativeZipSha256=native['zipSha256'],
                ociArchiveSha256=file_sha(archive.path))


def write_archive(output, descriptors, blobs):
    index = encode(dict(schemaVersion=2, mediaType=INDEX, manifests=descriptors))
    index_digest = digest(index)
    root = dict(schemaVersion=2, mediaType=INDEX, manifests=[dict(
        mediaType=INDEX, digest=index_digest, size=len(index),
        annotations={'org.opencontainers.image.ref.name': 'zrlog'})])
    with tarfile.open(output, 'w') as target:
        def add(name, data):
            info = tarfile.TarInfo(name)
            info.size = len(data)
            info.mode = 0o644
            target.addfile(info, io.BytesIO(data))
        add('oci-layout', encode({'imageLayoutVersion': '1.0.0'}))
        add('index.json', encode(root))
        add('blobs/sha256/' + index_digest[7:], index)
        for value, (archive, member) in sorted(blobs.items()):
            if value != index_digest:
                info = tarfile.TarInfo('blobs/sha256/' + value[7:])
                info.size = member.size
                info.mode = 0o644
                with archive.tar.extractfile(member) as source:
                    target.addfile(info, source)
    return index_digest


def assemble(inputs, output, platforms, env):
    output.mkdir(parents=True, exist_ok=True)
    records, blobs = [], {}
    with ExitStack() as stack:
        for path in sorted(inputs.rglob('image.oci.tar')):
            archive = Archive(path, stack)
            record = inspect(archive, path.parent, env)
            records.append(record)
            for value, member in archive.verified.items():
                blobs[value] = (archive, member)
        require(sorted(record['platform'] for record in records) == sorted(platforms),
                'Image platforms are missing, duplicated or unexpected')
        native = records[0]['native']
        for record in records:
            require(all(record['native'][key] == native[key] for key in
                        ['version', 'sourceCommit', 'buildId', 'buildWorkflow']),
                    'Linux platforms have different build identities')
        runtimes = [record['runtime'] for record in records]
        full = [desc for record in records for desc in [record['runtime'], *record['attestations']]]
        images = []
        for role, descriptors in [('dockerhub', full), ('aliyun', runtimes)]:
            path = output / f'linux-{role}.oci.tar'
            value = write_archive(path, descriptors, blobs)
            images.append(dict(role=role, digest=value, archive=path.name, sha256=file_sha(path)))
        report = dict(schemaVersion=1, channel=env['ZRLOG_CHANNEL'],
                      sourceCommit=native['sourceCommit'], version=native['version'],
                      buildId=native['buildId'], buildWorkflow=native['buildWorkflow'],
                      distributionMetadataEligible=env['ZRLOG_CHANNEL'] != 'release' or
                      'SNAPSHOT' not in native['version'],
                      platforms=sorted(platforms), images=images,
                      inputs=[{key: record[key] for key in
                               ['platform', 'runtime', 'nativeExecutableSha256', 'nativeZipSha256', 'ociArchiveSha256']}
                              for record in records])
        (output / 'image-assembly.json').write_text(json.dumps(report, indent=2) + '\n')
        (output / 'native-image-input.json').write_text(json.dumps(native, indent=2) + '\n')
        return report


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--inputs', required=True, type=Path)
    parser.add_argument('--output', required=True, type=Path)
    parser.add_argument('--platform', action='append', required=True)
    args = parser.parse_args()
    assemble(args.inputs, args.output, args.platform, dict(os.environ))
