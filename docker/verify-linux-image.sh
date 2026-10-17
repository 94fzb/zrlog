#!/usr/bin/env bash
# Run the archived image and compare its actual binary with the Native input.
set -euo pipefail
directory=$1
platform=$2
image=$3
architecture=${platform#linux/}
bash docker/skopeo.sh copy --override-os linux --override-arch "$architecture" \
  "oci-archive:$directory/image.oci.tar" "docker-archive:$directory/image.tar:$image"
docker load --input "$directory/image.tar"
build_id=$(jq -er .buildId "$directory/native-image-input.json")
version_output=$(docker run --rm --platform "$platform" --entrypoint /opt/zrlog/zrlog "$image" --version)
version_output="${version_output/ version: / version:}"
[[ $version_output == "zrlog version:$ZRLOG_EXPECTED_VERSION - $build_id "* ]]
docker image inspect "$image" | jq -e --arg version "$ZRLOG_EXPECTED_VERSION" \
  --arg commit "$ZRLOG_EXPECTED_SOURCE_COMMIT" \
  '.[0].Config.Labels | .["org.opencontainers.image.version"] == $version and .["org.opencontainers.image.revision"] == $commit'
expected_sha=$(cut -d ' ' -f 1 "$directory/native.sha256")
actual_sha=$(docker run --rm --platform "$platform" --entrypoint sha256sum "$image" /opt/zrlog/zrlog | cut -d ' ' -f 1)
[[ $actual_sha == "$expected_sha" ]]
jq -n --arg platform "$platform" --arg sourceCommit "$ZRLOG_EXPECTED_SOURCE_COMMIT" \
  --arg version "$ZRLOG_EXPECTED_VERSION" --arg buildId "$build_id" \
  --arg nativeExecutableSha256 "$actual_sha" \
  '{platform:$platform,sourceCommit:$sourceCommit,version:$version,buildId:$buildId,nativeExecutableSha256:$nativeExecutableSha256}' \
  > "$directory/image-verification.json"
(cd "$directory" && sha256sum image.tar image.oci.tar > image.sha256)
