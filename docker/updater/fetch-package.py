#!/usr/bin/env python3
"""Resolve a published updater archive by exact app version/commit before building its container."""
import argparse
import hashlib
import io
import json
from pathlib import Path
import re
import urllib.request
import zipfile


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--platform", choices=("Linux-amd64", "Linux-arm64"), required=True)
    parser.add_argument("--version", required=True)
    parser.add_argument("--commit", required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    if not re.fullmatch("[0-9a-f]{40}", args.commit) or not re.fullmatch(r"[0-9]+\.[0-9]+\.[0-9]+", args.version):
        raise ValueError("Expected an immutable official release identity")
    url = "https://dl.zrlog.com/release/last.updater." + args.platform + ".version.json"
    with urllib.request.urlopen(url, timeout=30) as response:
        metadata = json.loads(response.read(1024 * 1024 + 1))
    if (metadata.get("packageType") != "updater" or metadata.get("checksumAlgorithm") != "sha256"
            or metadata.get("version") != args.version or metadata.get("sourceCommit") != args.commit):
        raise ValueError("Published updater package belongs to another build")
    build_id = metadata.get("buildId", "")
    if not re.fullmatch("[0-9a-f]{7,40}", build_id) or not args.commit.startswith(build_id):
        raise ValueError("Updater build ID does not match the approved source")
    expected = f"https://dl.zrlog.com/release/zrlog-{args.version}-{build_id}-release-updater-{args.platform}.zip"
    if metadata.get("zipDownloadUrl") != expected:
        raise ValueError("Updater URL does not match its release identity")
    size, checksum = metadata.get("zipFileSize"), metadata.get("zipSha256", "")
    if type(size) is not int or not 0 < size <= 128 * 1024 * 1024 or not re.fullmatch("[0-9a-f]{64}", checksum):
        raise ValueError("Invalid updater checksum or archive size")
    with urllib.request.urlopen(expected, timeout=120) as response:
        data = response.read(size + 1)
    if len(data) != size or hashlib.sha256(data).hexdigest() != checksum:
        raise ValueError("Updater archive checksum mismatch")
    with zipfile.ZipFile(io.BytesIO(data)) as archive:
        if set(archive.namelist()) != {"zrlog-updater", "README.md", "docker-compose.updater.yml"}:
            raise ValueError("Unexpected files in standalone updater package")
        entry = archive.getinfo("zrlog-updater")
        if entry.file_size > 128 * 1024 * 1024:
            raise ValueError("Updater executable exceeds size bound")
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_bytes(archive.read(entry))
        args.output.chmod(0o755)


if __name__ == "__main__":
    main()
