#!/usr/bin/env python3
"""Package a verified updater binary with the application's exact release identity."""
import argparse
import datetime
import hashlib
import json
from pathlib import Path
import re
import subprocess
import zipfile


def package(binary, properties, output):
    required = ("version", "buildId", "sourceCommit", "buildTime", "fileArch", "mirrorWebSite", "runMode")
    if not all(properties.get(key) for key in required):
        raise ValueError("Missing release identity in build.properties")
    version, commit, build_id = (properties[key] for key in ("version", "sourceCommit", "buildId"))
    channel, platform = properties["runMode"], properties["fileArch"]
    if (not re.fullmatch(r"[0-9]+\.[0-9]+\.[0-9]+(?:-[A-Za-z0-9.-]+)?", version)
            or not re.fullmatch("[0-9a-f]{40}", commit) or not re.fullmatch("[A-Za-z0-9][A-Za-z0-9._-]{0,127}", build_id)
            or channel == "release" and (not re.fullmatch("[0-9a-f]{7,40}", build_id) or not commit.startswith(build_id))
            or channel not in ("release", "preview")
            or platform not in ("Linux-amd64", "Linux-arm64")):
        raise ValueError("Invalid updater release identity/platform")
    datetime.datetime.strptime(properties["buildTime"], "%Y-%m-%d %H:%M:%S%z")
    reported = subprocess.check_output([str(binary.resolve()), "--version"], text=True).strip()
    if reported != "zrlog-updater " + version:
        raise ValueError("Native updater version does not match build.properties")
    output.mkdir(parents=True, exist_ok=True)
    filename = f"zrlog-{version}-{build_id}-{channel}-updater-{platform}.zip"
    archive = output / filename
    module = Path(__file__).resolve().parent
    entries = {"zrlog-updater": binary, "README.md": module / "README.md",
               "docker-compose.updater.yml": module.parent / "docker-compose.updater.yml"}
    with zipfile.ZipFile(archive, "w", compression=zipfile.ZIP_DEFLATED, compresslevel=9) as result:
        for name, path in entries.items():
            entry = zipfile.ZipInfo(name, (2026, 1, 1, 0, 0, 0))
            entry.create_system = 3
            entry.external_attr = (0o100755 if name == "zrlog-updater" else 0o100644) << 16
            result.writestr(entry, path.read_bytes(), compress_type=zipfile.ZIP_DEFLATED, compresslevel=9)
    digest = hashlib.sha256(archive.read_bytes()).hexdigest()
    url = properties["mirrorWebSite"].rstrip("/") + "/" + channel + "/" + filename
    metadata = dict(packageType="updater", version=version, buildId=build_id, sourceCommit=commit,
                    releaseDate=properties["buildTime"], type=channel, zipDownloadUrl=url,
                    checksumAlgorithm="sha256", zipSha256=digest, zipFileSize=archive.stat().st_size,
                    zipSources=[dict(id="dl", url=url)],
                    sourceRepository=properties.get("sourceRepository", "https://github.com/94fzb/zrlog"),
                    buildWorkflow=properties.get("buildWorkflow", ""))
    (output / f"last.updater.{platform}.version.json").write_text(json.dumps(metadata, ensure_ascii=False, indent=2) + "\n")
    return archive


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--binary", type=Path, required=True)
    parser.add_argument("--build-properties", type=Path, required=True)
    parser.add_argument("--output-dir", type=Path, required=True)
    args = parser.parse_args()
    values = dict(line.split("=", 1) for line in args.build_properties.read_text().splitlines()
                  if "=" in line and not line.startswith("#"))
    print(package(args.binary, values, args.output_dir))
