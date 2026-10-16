#!/usr/bin/env python3
"""Verify the exact Native job artifact before using its executable in Docker."""
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import sys
from urllib.parse import urlparse
import zipfile


PLATFORMS = {
    'Linux-amd64': ('Linux', 'X64', 'zrlog', b'\x7fELF'),
    'Linux-arm64': ('Linux', 'ARM64', 'zrlog', b'\x7fELF'),
    'Windows-x86_64': ('Windows', 'X64', 'zrlog.exe', b'MZ'),
}


def require(condition, message):
    if not condition:
        raise ValueError(message)


def sha256(path):
    digest = hashlib.sha256()
    with path.open('rb') as stream:
        for block in iter(lambda: stream.read(1024 * 1024), b''):
            digest.update(block)
    return digest.hexdigest()


def prepare(artifact: Path, output: Path, env: dict) -> dict:
    channel = env['ZRLOG_CHANNEL']
    platform = env['NATIVE_PLATFORM']
    require(channel in {'preview', 'release'} and platform in PLATFORMS, 'Unsupported channel or platform')
    commit = env['ZRLOG_EXPECTED_SOURCE_COMMIT']
    version = env['ZRLOG_EXPECTED_VERSION']
    repository = f"{env['GITHUB_SERVER_URL']}/{env['GITHUB_REPOSITORY']}"
    workflow = f"{repository}/actions/runs/{env['GITHUB_RUN_ID']}"
    index_file = artifact / '.package-evidence/build.json'
    index = json.loads(index_file.read_text())
    runner_os, runner_arch, executable, magic = PLATFORMS[platform]
    require(index['sourceCommit'] == commit and index['channel'] == channel
            and index['packageType'] == 'native' and index['workflow'] == workflow
            and index['runnerOs'] == runner_os and index['runnerArch'] == runner_arch
            and str(index['runAttempt']).isdigit() and int(index['runAttempt']) > 0,
            'Native index does not match the requested source, run, channel or platform')
    directory = artifact / '.package-output' / channel
    metadata_file = directory / f'last.{platform}.version.json'
    metadata = json.loads(metadata_file.read_text())
    build_id = metadata['buildId']
    require(re.fullmatch(r'[0-9A-Za-z][0-9A-Za-z._-]{0,127}', version)
            and re.fullmatch(r'[0-9A-Za-z][0-9A-Za-z._-]{0,127}', build_id), 'Invalid package identity')
    require(metadata['sourceCommit'] == commit and metadata['version'] == version
            and metadata['sourceRepository'] == repository and metadata['buildWorkflow'] == workflow
            and metadata['checksumAlgorithm'] == 'sha256'
            and metadata['type'] == ('预览版本' if channel == 'preview' else '正式版本'),
            'Native update JSON does not match the requested package')
    archive_name = f'zrlog-{version}-{build_id}-{channel}-{platform}.zip'
    require(urlparse(metadata['zipDownloadUrl']).path == f'/{channel}/{archive_name}',
            'Native update URL does not identify the requested archive')
    if channel == 'release':
        require(re.fullmatch(r'[0-9a-f]{7,40}', build_id) and commit.startswith(build_id),
                'Release build ID does not match its source')
        image_tag = version
    else:
        require(version.endswith('-SNAPSHOT') and not re.fullmatch(r'[0-9a-f]{40}', build_id),
                'Preview identity must retain the package short build ID')
        image_tag = f'{version}-{build_id}'
    require(len(image_tag + '-windows-ltsc2022') <= 128, 'Docker tag exceeds 128 characters')
    # Verify both versioned and alias archives against the producer index and update JSON.
    archive = directory / archive_name
    for name in [archive_name, f'zrlog-{platform}.zip']:
        path = directory / name
        entries = [entry for entry in index['artifacts'] if entry['path'] == f'{channel}/{name}']
        digest = sha256(path)
        require(len(entries) == 1 and entries[0]['sha256'] == digest
                and entries[0]['size'] == path.stat().st_size
                and metadata['zipSha256'] == digest and metadata['zipFileSize'] == path.stat().st_size,
                'Native ZIP bytes differ from the producer index or update JSON')
    package = output / 'package'
    package.mkdir(parents=True, exist_ok=True)
    target = package / executable
    with zipfile.ZipFile(archive) as source:
        require(source.namelist().count(executable) == 1, 'Native ZIP must contain exactly one executable')
        with source.open(executable) as stream, target.open('wb') as destination:
            shutil.copyfileobj(stream, destination)
    with target.open('rb') as stream:
        require(stream.read(len(magic)) == magic, 'Native executable has the wrong platform format')
    target.chmod(0o755)
    shutil.copyfile(metadata_file, output / 'native-image-input.json')
    shutil.copyfile(index_file, output / 'build.json')
    digest = sha256(target)
    (output / 'native.sha256').write_text(f'{digest}  {executable}\n')
    return dict(image_tag=image_tag, build_id=build_id)


if __name__ == '__main__':
    settings = prepare(Path(sys.argv[1]), Path(sys.argv[2]), dict(os.environ))
    with Path(os.environ['GITHUB_OUTPUT']).open('a') as output:
        for key, value in settings.items():
            output.write(f'{key}={value}\n')
