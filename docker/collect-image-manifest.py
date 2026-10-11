#!/usr/bin/env python3
"""Collect and recheck immutable registry digests after the workflow's per-platform runtime tests."""
import argparse
import importlib.util
import json
import re
import subprocess
from pathlib import Path


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--native-metadata", type=Path, required=True)
    parser.add_argument("--image", action="append", required=True)
    parser.add_argument("--platform", action="append", required=True)
    parser.add_argument("--channel", choices=("release", "preview"), required=True)
    parser.add_argument("--tag", required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    native = json.loads(args.native_metadata.read_text())
    entries = []
    for image in args.image:
        repository, separator, digest = image.rpartition("@")
        if not separator or not re.fullmatch("sha256:[0-9a-f]{64}", digest):
            raise ValueError("Use the actual immutable registry digest")
        descriptor = subprocess.check_output(["docker", "buildx", "imagetools", "inspect", image,
                                              "--format", "{{.Manifest.Digest}}"], text=True).strip()
        if descriptor != digest:
            raise ValueError("Registry digest changed")
        raw = json.loads(subprocess.check_output(["docker", "buildx", "imagetools", "inspect", image, "--raw"]))
        platforms = sorted(f"{item['platform']['os']}/{item['platform']['architecture']}"
                           for item in raw.get("manifests", []) if item.get("platform", {}).get("os") == "linux")
        if platforms != sorted(args.platform):
            raise ValueError("Published image platforms differ from runtime-tested platforms")
        entries.append(dict(repository=repository, digest=digest, tag=args.tag, platforms=platforms,
                            version=native["version"], sourceCommit=native["sourceCommit"]))
    spec = importlib.util.spec_from_file_location("image_manifest", Path(__file__).with_name("write-image-manifest.py"))
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    manifest = module.generate(native, entries, args.channel)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(manifest, ensure_ascii=False, indent=2) + "\n")


if __name__ == "__main__":
    main()
