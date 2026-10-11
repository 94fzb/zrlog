#!/usr/bin/env bash
set -euo pipefail
# Run after the application build generated its exact build.properties.
output_dir=${1:?output directory is required}
source shell/lib/release-manifest.sh
version=$(releaseManifestBuildProp version)
mirror=$(releaseManifestBuildProp mirrorWebSite)
docker_manifest=$(releaseManifestBuildProp dockerUpdateVersionJsonFilename)
./mvnw -B -f docker/updater/pom.xml -Pnative package \
  "-Drevision=${version}" "-DmirrorWebSite=${mirror}" \
  "-DupdateVersionJsonFilename=${docker_manifest:-last.docker-image.version.json}"
python3 docker/updater/smoke-native.py docker/updater/target/zrlog-updater
python3 docker/updater/package-native.py --binary docker/updater/target/zrlog-updater \
  --build-properties zrlog-web/src/main/resources/build.properties --output-dir "${output_dir}"
