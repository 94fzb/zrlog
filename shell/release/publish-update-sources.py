#!/usr/bin/env python3
"""Add publicly verified GitHub/dl locations after an approved GitHub Release is published.

Retains legacy package URLs, lengths and checksums. Does not create a release, upload packages,
guess an asset URL, or mark draft attachments as available.
"""
import argparse
import copy
import hashlib
import json
import os
from pathlib import Path
import re
import urllib.parse
import urllib.request


def verify_download(url, size, sha256):
    request = urllib.request.Request(url, headers={"User-Agent": "zrlog-update-distribution/1"})
    total, digest = 0, hashlib.sha256()
    with urllib.request.urlopen(request, timeout=60) as response:
        if response.status != 200:
            raise ValueError("Package source did not return HTTP 200")
        while block := response.read(1024 * 1024):
            total += len(block)
            if total > size:
                raise ValueError("Package source returned too many bytes")
            digest.update(block)
    if total != size or digest.hexdigest() != sha256:
        raise ValueError("Package source bytes differ from the published checksum")


def enrich(manifest, release, commit, verifier=verify_download):
    if release.get("draft") is not False or release.get("prerelease") is not False:
        raise ValueError("Only a public official Release can advertise GitHub downloads")
    if not re.fullmatch(r"[0-9a-f]{40}", commit) or manifest.get("sourceCommit") != commit:
        raise ValueError("Package metadata does not match the approved release commit")
    if release.get("tag_name") != "v" + manifest.get("version", ""):
        raise ValueError("Release tag differs from package metadata")
    assets = release.get("assets", [])
    result = copy.deepcopy(manifest)
    found = False
    for package in ("zip", "war"):
        url = manifest.get(package + "DownloadUrl")
        if not url:
            continue
        found = True
        parsed = urllib.parse.urlparse(url)
        if (parsed.scheme != "https" or parsed.netloc != "dl.zrlog.com" or not parsed.path.startswith("/release/")
                or parsed.query or parsed.fragment):
            raise ValueError("Official package URL is not a fixed dl.zrlog.com release URL")
        name = parsed.path.rsplit("/", 1)[-1]
        matches = [asset for asset in assets if asset.get("name") == name and asset.get("state") == "uploaded"]
        if len(matches) != 1:
            raise ValueError("Release does not contain exactly one matching published package")
        asset = matches[0]
        github_url = asset.get("browser_download_url", "")
        expected = "https://github.com/94fzb/zrlog/releases/download/" + release["tag_name"] + "/" + name
        if github_url != expected:
            raise ValueError("GitHub asset URL does not match the actual release")
        size, checksum = manifest.get(package + "FileSize"), manifest.get(package + "Sha256", "")
        if type(size) is not int or size <= 0 or asset.get("size") != size or not re.fullmatch("[0-9a-f]{64}", checksum):
            raise ValueError("Package source identity is incomplete")
        verifier(url, size, checksum)
        verifier(github_url, size, checksum)
        result[package + "Sources"] = [{"id": "dl", "url": url}, {"id": "github", "url": github_url}]
    if not found:
        raise ValueError("No package download in manifest")
    return result


def publish(store, bucket, release, commit):
    # S3 enumeration is restricted to existing package channel entries; Docker has its own publication lifecycle.
    keys = []
    for page in store.get_paginator("list_objects_v2").paginate(Bucket=bucket, Prefix="release/last."):
        keys.extend(entry["Key"] for entry in page.get("Contents", [])
                    if re.fullmatch(r"release/last(?:\.[A-Za-z0-9_-]+)*\.version\.json", entry["Key"])
                    and ".docker-image." not in entry["Key"])
    if not keys:
        raise ValueError("No published package metadata")
    prepared = []
    for key in sorted(keys):
        response = store.get_object(Bucket=bucket, Key=key)
        manifest = json.loads(response["Body"].read(1024 * 1024 + 1))
        # Another version may have become current while this Release was distributing. Never rewind it.
        if manifest.get("sourceCommit") != commit:
            continue
        if manifest.get("packageType") == "updater":
            continue  # Independently versioned/published by zrlog/zrlog-updater.
        result = enrich(manifest, release, commit)
        body = (json.dumps(result, ensure_ascii=False, indent=2) + "\n").encode()
        immutable = f"release/package-sources/{result['version']}-{result['buildId']}/{key.rsplit('/', 1)[1]}"
        prepared.append((key, immutable, response["ETag"], body))
    if not prepared:
        raise ValueError("Current package metadata no longer describes this release; no channel entries changed")
    # Validate every source before mutating any channel entry.
    for key, immutable, etag, body in prepared:
        try:
            existing = store.get_object(Bucket=bucket, Key=immutable)["Body"].read(1024 * 1024 + 1)
            if existing != body:
                raise ValueError("Immutable source metadata already differs")
        except Exception as error:
            if getattr(error, "response", {}).get("Error", {}).get("Code") not in ("NoSuchKey", "404"):
                raise
            store.put_object(Bucket=bucket, Key=immutable, Body=body, ContentType="application/json",
                             CacheControl="public, max-age=31536000, immutable", IfNoneMatch="*")
        if store.get_object(Bucket=bucket, Key=immutable)["Body"].read(1024 * 1024 + 1) != body:
            raise ValueError("Immutable source metadata readback failed")
        store.put_object(Bucket=bucket, Key=key, Body=body, ContentType="application/json", CacheControl="no-cache", IfMatch=etag)
        if store.get_object(Bucket=bucket, Key=key)["Body"].read(1024 * 1024 + 1) != body:
            raise ValueError("Package channel metadata readback failed")
    return [key for key, _, _, _ in prepared]


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--release", type=Path, required=True)
    parser.add_argument("--commit", required=True)
    args = parser.parse_args()
    import boto3
    store = boto3.client("s3", endpoint_url=os.environ["ZRLOG_S3_ENDPOINT"], region_name="auto")
    print(json.dumps(publish(store, os.environ["ZRLOG_S3_BUCKET"], json.loads(args.release.read_text()), args.commit)))
