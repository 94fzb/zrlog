#!/usr/bin/env python3
"""Generate update metadata from validated Native metadata and actual registry push results.

This command does not publish anything. Call it only after image version/platform checks succeed;
publish the immutable document before advancing the channel's last.docker-image.version.json.
"""
import argparse
import datetime
import json
import re
from pathlib import Path


def repository(value):
    if not isinstance(value, str) or len(value) > 255 or not re.fullmatch(
        r"[a-z0-9][a-z0-9.:-]*(?:/[a-z0-9]+(?:[._-][a-z0-9]+)*)+", value
    ):
        raise ValueError("Invalid image repository")
    first = value.split("/", 1)[0]
    if "." not in first and ":" not in first and first != "localhost":
        value = "docker.io/" + value
    if value.startswith("index.docker.io/"):
        value = "docker.io/" + value[len("index.docker.io/"):]
    return value


def generate(native, entries, channel):
    if channel not in ("release", "preview"):
        raise ValueError("Unknown channel")
    version, commit, build_id = (native.get(key, "") for key in ("version", "sourceCommit", "buildId"))
    if not re.fullmatch(r"[0-9]+\.[0-9]+\.[0-9]+(?:-[A-Za-z0-9.-]+)?", version):
        raise ValueError("Invalid version")
    if (not re.fullmatch(r"[0-9a-f]{40}", commit) or not re.fullmatch(r"[A-Za-z0-9][A-Za-z0-9._-]{0,127}", build_id)
            or channel == "release" and (not re.fullmatch(r"[0-9a-f]{7,40}", build_id) or not commit.startswith(build_id))):
        raise ValueError("Native metadata has no matching source identity")
    datetime.datetime.strptime(native["releaseDate"], "%Y-%m-%d %H:%M:%S%z")
    if not entries or len(entries) > 32:
        raise ValueError("Expected 1–32 published images")
    images, targets = [], set()
    for entry in entries:
        if entry.get("version") != version or entry.get("sourceCommit") != commit:
            raise ValueError("Image result does not match the Native build")
        repo = repository(entry.get("repository"))
        digest, tag = entry.get("digest", ""), entry.get("tag", "")
        if not re.fullmatch(r"sha256:[0-9a-f]{64}", digest) or not re.fullmatch(r"[A-Za-z0-9_][A-Za-z0-9_.-]{0,127}", tag):
            raise ValueError("Invalid registry digest or tag")
        platforms = entry.get("platforms")
        if not isinstance(platforms, list) or not 1 <= len(platforms) <= 16:
            raise ValueError("Missing verified platform list")
        normalized = []
        for platform in platforms:
            if not isinstance(platform, str) or not re.fullmatch(r"[a-z0-9]+/[a-z0-9]+(?:/[a-z0-9]+)?", platform):
                raise ValueError("Invalid platform")
            platform = "linux/arm64" if platform == "linux/arm64/v8" else platform
            if (repo, platform) in targets:
                raise ValueError("Ambiguous repository/platform mapping")
            targets.add((repo, platform))
            normalized.append(platform)
        images.append(dict(repository=repo, tag=tag, digest=digest, platforms=sorted(normalized)))
    return dict(
        schemaVersion=1, packageType="docker-image", channel=channel,
        version=version, buildId=build_id, sourceCommit=commit,
        releaseDate=native["releaseDate"], type=native.get("type", channel),
        sourceRepository=native.get("sourceRepository", "https://github.com/94fzb/zrlog"),
        buildWorkflow=native.get("buildWorkflow", ""),
        images=sorted(images, key=lambda image: (image["repository"], image["platforms"])),
    )


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--native-metadata", type=Path, required=True)
    parser.add_argument("--image-entry", type=Path, action="append", required=True)
    parser.add_argument("--channel", choices=("release", "preview"), required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    result = generate(json.loads(args.native_metadata.read_text()),
                      [json.loads(path.read_text()) for path in args.image_entry], args.channel)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(result, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


if __name__ == "__main__":
    main()
