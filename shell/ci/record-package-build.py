#!/usr/bin/env python3
"""Record the source, effective toolchain and actual application package bytes."""
import hashlib
import json
import os
from pathlib import Path
import subprocess


def record(root: Path, env: dict[str, str]) -> dict:
    source = subprocess.check_output(['git', 'rev-parse', 'HEAD'], cwd=root, text=True).strip()
    if source != env['EXPECTED_COMMIT']:
        raise ValueError('Package source differs from the requested commit')
    channel = env['CHANNEL']
    directory = root / '.package-output' / channel
    packages = sorted(p for p in directory.iterdir() if p.suffix in {'.zip', '.war', '.deb'})
    if not packages:
        raise ValueError('No application packages produced')
    artifacts = []
    for package in packages:
        digest = hashlib.sha256()
        with package.open('rb') as stream:
            for block in iter(lambda: stream.read(1024 * 1024), b''):
                digest.update(block)
        artifacts.append(dict(path=str(package.relative_to(root / '.package-output')),
                              size=package.stat().st_size, sha256=digest.hexdigest()))
    java = subprocess.run(['java', '-version'], check=True, capture_output=True, text=True)
    return dict(sourceCommit=source, packageType=env['PACKAGE_TYPE'], channel=channel,
                requestedJava=env['REQUESTED_JAVA'], javaVersion=(java.stdout + java.stderr).strip(),
                runnerOs=env.get('RUNNER_OS'), runnerArch=env.get('RUNNER_ARCH'),
                workflow=f"{env['GITHUB_SERVER_URL']}/{env['GITHUB_REPOSITORY']}/actions/runs/{env['GITHUB_RUN_ID']}",
                runAttempt=env['GITHUB_RUN_ATTEMPT'], artifacts=artifacts)


if __name__ == '__main__':
    evidence = Path('.package-evidence')
    evidence.mkdir(exist_ok=True)
    (evidence / 'build.json').write_text(json.dumps(record(Path.cwd(), dict(os.environ)),
                                                 ensure_ascii=False, indent=2) + '\n')
