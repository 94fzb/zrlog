#!/usr/bin/env python3
"""Select the package jobs without allowing manual builds to publish."""
import json
import os
from pathlib import Path
import re
import xml.etree.ElementTree as ET


def plan(env: dict[str, str]) -> dict:
    event = env['GITHUB_EVENT_NAME']
    source = env['GITHUB_SHA']
    if not re.fullmatch(r'[0-9a-f]{40}', source):
        raise ValueError('Source must be a full commit SHA')
    publish = event != 'workflow_dispatch'
    if publish:
        ref = env['GITHUB_REF']
        if (env['GITHUB_REPOSITORY'] != '94fzb/zrlog'
                or ref not in {'refs/heads/master', 'refs/heads/release'}
                or event not in {'push', 'repository_dispatch'}
                or (event == 'repository_dispatch' and ref != 'refs/heads/master')):
            raise ValueError('Publication requires the matching trusted branch and event')
        kind = 'all'
        channel = 'release' if ref == 'refs/heads/release' else 'preview'
        full = True
    else:
        kind = env.get('PACKAGE_TYPE', 'all')
        channel = env.get('CHANNEL', 'preview')
        full = env.get('FULL_PLATFORMS', 'true') == 'true'
    if kind not in {'all', 'java', 'native', 'deb', 'faas'} or channel not in {'preview', 'release'}:
        raise ValueError('Unsupported package type or channel')
    kinds = {'java', 'native', 'deb', 'faas'} if kind == 'all' else {kind}
    # Native platforms have explicit jobs; each Docker image waits for its own
    # Linux or Windows Native package. Java, DEB and FaaS share this matrix.
    packages = []
    for package_type in ['java', 'deb', 'faas']:
        if package_type not in kinds:
            continue
        targets = [('ubuntu-22.04', '11' if package_type == 'java' else '25')]
        if full and package_type != 'java':
            targets.append(('ubuntu-24.04-arm', '25'))
        packages.extend(dict(package_type=package_type, os=runner, java=java) for runner, java in targets)
    return dict(matrix={'include': packages}, has_packages=bool(packages),
                build_native='native' in kinds, channel=channel, publish=publish,
                full_platforms=full, source_commit=source)


if __name__ == '__main__':
    settings = plan(dict(os.environ))
    project = ET.parse('pom.xml').getroot()
    settings['version'] = project.findtext('{*}version')
    if not settings['version']:
        raise ValueError('Project version is required')
    with Path(os.environ['GITHUB_OUTPUT']).open('a') as output:
        for name, value in settings.items():
            encoded = json.dumps(value, separators=(',', ':')) if not isinstance(value, str) else value
            output.write(f'{name}={encoded}\n')
