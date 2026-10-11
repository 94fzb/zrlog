#!/usr/bin/env python3
"""Publish validated Docker metadata: immutable version first, channel pointer last.

Executed only by the authorized publishing workflow. S3 credentials use the standard AWS
environment variables. No packages/images are built or pushed by this command.
"""
import argparse
import datetime
import hashlib
import json
import os
from pathlib import Path


def publish(store, bucket, manifest):
    channel = manifest["channel"]
    if channel not in ("release", "preview") or manifest.get("packageType") != "docker-image":
        raise ValueError("Invalid Docker channel document")
    # Reuse the generator's identity, repository, digest and platform validation.
    import importlib.util
    spec = importlib.util.spec_from_file_location("image_manifest", Path(__file__).with_name("write-image-manifest.py"))
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    entries = [dict(image, version=manifest["version"], sourceCommit=manifest["sourceCommit"]) for image in manifest["images"]]
    if module.generate(manifest, entries, channel) != manifest:
        raise ValueError("Manifest is not canonical")
    key = f"{channel}/docker-image/{manifest['version']}-{manifest['buildId']}.json"
    latest_key = f"{channel}/last.docker-image.version.json"
    body = (json.dumps(manifest, ensure_ascii=False, indent=2) + "\n").encode()

    def read(key):
        try:
            value = store.get_object(Bucket=bucket, Key=key)
            return value["Body"].read(1024 * 1024 + 1), value["ETag"]
        except Exception as error:
            if getattr(error, "response", {}).get("Error", {}).get("Code") in ("NoSuchKey", "404"):
                return None, None
            raise

    previous, etag = read(latest_key)
    if previous is not None:
        old = json.loads(previous)
        old_time = datetime.datetime.strptime(old["releaseDate"], "%Y-%m-%d %H:%M:%S%z")
        new_time = datetime.datetime.strptime(manifest["releaseDate"], "%Y-%m-%d %H:%M:%S%z")
        if old_time > new_time or old_time == new_time and old["sourceCommit"] != manifest["sourceCommit"]:
            raise ValueError("Refusing to replace a newer or conflicting channel manifest")
    immutable, _ = read(key)
    if immutable is not None and immutable != body:
        raise ValueError("Immutable Docker manifest already has different bytes")
    if immutable is None:
        store.put_object(Bucket=bucket, Key=key, Body=body, ContentType="application/json",
                         CacheControl="public, max-age=31536000, immutable", IfNoneMatch="*")
    copied, _ = read(key)
    if copied != body:
        raise ValueError("Immutable Docker manifest readback failed")
    if previous != body:
        condition = {"IfMatch": etag} if etag else {"IfNoneMatch": "*"}
        store.put_object(Bucket=bucket, Key=latest_key, Body=body, ContentType="application/json",
                         CacheControl="no-cache", **condition)
    latest, _ = read(latest_key)
    if latest != body:
        raise ValueError("Docker channel manifest readback failed")
    return {"immutableKey": key, "channelKey": latest_key, "sha256": hashlib.sha256(body).hexdigest()}


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("manifest", type=Path)
    args = parser.parse_args()
    import boto3
    client = boto3.client("s3", endpoint_url=os.environ["ZRLOG_S3_ENDPOINT"], region_name="auto")
    print(json.dumps(publish(client, os.environ["ZRLOG_S3_BUCKET"], json.loads(args.manifest.read_text()))))
