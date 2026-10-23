"""Verify this branch's source refs and actual source-built Native dependencies."""
import hashlib
import json
import os
from pathlib import Path
import subprocess
import sys
import xml.etree.ElementTree as ET

ROOT = Path.cwd()
CONFIG = Path(__file__).resolve().parent
EVIDENCE = ROOT / '.package-evidence/cohesion'
REFS = json.loads((CONFIG / 'source-refs.json').read_text())
MANIFEST = json.loads((CONFIG / 'source-manifest.json').read_text())
NS = {'m': 'http://maven.apache.org/POM/4.0.0'}


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def sources():
    result = {}
    for key, ref in REFS.items():
        source = ROOT / '.cohesion-sources' / key
        head = subprocess.check_output(['git', 'rev-parse', 'HEAD'], cwd=source, text=True).strip()
        assert head == ref['commit'], (key, head)
        for name, expected in MANIFEST[key]['files'].items():
            assert digest(source / name) == expected, name
        result[key] = dict(ref, changedFiles=MANIFEST[key]['files'])
    EVIDENCE.mkdir(parents=True, exist_ok=True)
    (EVIDENCE / 'sources.json').write_text(json.dumps(result, indent=2) + '\n')


def record():
    sources()
    classpath = {Path(p).resolve() for p in (EVIDENCE / 'native-classpath.txt').read_text().strip().split(os.pathsep)}
    artifacts = []
    for key in REFS:
        source = ROOT / '.cohesion-sources' / key
        pom = ET.parse(source / 'pom.xml').getroot()
        version = pom.findtext('m:version', namespaces=NS)
        for module in pom.findall('m:modules/m:module', NS):
            name = module.text
            if name == 'zrlog-test-support':
                continue
            built = source / name / 'target' / f'{name}-{version}.jar'
            installed = Path.home() / '.m2/repository/com/hibegin' / name / version / built.name
            assert built.is_file() and digest(built) == digest(installed), name
            assert installed.resolve() in classpath, f'Native classpath misses {name}'
            artifacts.append(dict(module=name, source=key, built=str(built), installed=str(installed), sha256=digest(built)))
    assert len(artifacts) == 14, len(artifacts)
    (EVIDENCE / 'inputs.json').write_text(json.dumps(artifacts, indent=2) + '\n')
    print(f'Recorded {len(artifacts)} exact Native inputs')


def verify():
    artifacts = json.loads((EVIDENCE / 'inputs.json').read_text())
    for artifact in artifacts:
        assert digest(Path(artifact['installed'])) == artifact['sha256'], artifact['module']
        assert digest(Path(artifact['built'])) == artifact['sha256'], artifact['module']
    (EVIDENCE / 'inputs-verified.json').write_text(json.dumps(dict(status='passed', artifacts=artifacts), indent=2) + '\n')
    print(f'Native compilation preserved all {len(artifacts)} source-built inputs')


{'sources': sources, 'record': record, 'verify': verify}[sys.argv[1]]()
