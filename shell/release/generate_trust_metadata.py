#!/usr/bin/env python3
"""Generate checksums, SBOMs, signatures, and provenance for release files."""

from __future__ import annotations

import argparse
import hashlib
import json
import re
import subprocess
from datetime import datetime, timezone
from pathlib import Path


ARTIFACT_PATTERN = re.compile(r"^zrlog-.+-release(?:-.+)?\.(?:zip|war|deb)$")
COMMIT_PATTERN = re.compile(r"^[0-9a-f]{40}$")


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for block in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(block)
    return digest.hexdigest()


def run(command: list[str]) -> str:
    result = subprocess.run(
        command,
        check=False,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
    )
    if result.returncode != 0:
        detail = result.stderr.strip() or result.stdout.strip() or f"exit {result.returncode}"
        raise RuntimeError(f"{command[0]} failed: {detail}")
    return result.stdout.strip()


def write_text(path: Path, value: str) -> None:
    temporary = path.with_name(path.name + ".tmp")
    temporary.write_text(value, encoding="utf-8")
    temporary.replace(path)


def generate(
    artifact: Path,
    *,
    source_commit: str,
    source_repository: str,
    workflow_url: str,
    build_timestamp: str,
) -> list[Path]:
    checksum = sha256_file(artifact)
    checksum_path = artifact.with_name(artifact.name + ".sha256")
    sbom_path = artifact.with_name(artifact.name + ".sbom.cdx.json")
    signature_path = artifact.with_name(artifact.name + ".sigstore.json")
    provenance_path = artifact.with_name(artifact.name + ".provenance.json")

    write_text(checksum_path, f"{checksum}  {artifact.name}\n")
    run(["syft", "scan", str(artifact), "-o", f"cyclonedx-json={sbom_path}"])
    run(["cosign", "sign-blob", "--yes", "--bundle", str(signature_path), str(artifact)])

    provenance = {
        "schemaVersion": 1,
        "artifact": {
            "name": artifact.name,
            "size": artifact.stat().st_size,
            "sha256": checksum,
        },
        "source": {
            "repository": source_repository,
            "commit": source_commit,
        },
        "build": {
            "builder": "github-actions",
            "workflow": workflow_url,
            "timestamp": build_timestamp,
        },
        "evidence": {
            "sbom": {
                "name": sbom_path.name,
                "sha256": sha256_file(sbom_path),
                "format": "CycloneDX JSON",
            },
            "signatureBundle": signature_path.name,
        },
    }
    write_text(provenance_path, json.dumps(provenance, ensure_ascii=False, indent=2) + "\n")
    return [checksum_path, sbom_path, signature_path, provenance_path]


def release_artifacts(directory: Path) -> list[Path]:
    return sorted(
        path
        for path in directory.iterdir()
        if path.is_file() and ARTIFACT_PATTERN.fullmatch(path.name)
    )


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--artifact-dir", required=True, type=Path)
    parser.add_argument("--source-commit", required=True)
    parser.add_argument("--source-repository", required=True)
    parser.add_argument("--workflow-url", required=True)
    parser.add_argument("--build-timestamp")
    args = parser.parse_args()

    if not COMMIT_PATTERN.fullmatch(args.source_commit):
        raise SystemExit("--source-commit must be a full lowercase commit SHA")
    directory = args.artifact_dir.resolve()
    if not directory.is_dir():
        raise SystemExit(f"artifact directory does not exist: {directory}")
    artifacts = release_artifacts(directory)
    if not artifacts:
        raise SystemExit(f"no versioned release artifacts found in {directory}")

    timestamp = args.build_timestamp or datetime.now(timezone.utc).isoformat()
    generated = []
    for artifact in artifacts:
        generated.extend(
            generate(
                artifact,
                source_commit=args.source_commit,
                source_repository=args.source_repository,
                workflow_url=args.workflow_url,
                build_timestamp=timestamp,
            )
        )
    print(f"generated {len(generated)} trust metadata files for {len(artifacts)} artifacts")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
