#!/usr/bin/env bash
# Runner-only integration fixture: real OCI export, executable checks and isolated registries.
set -euo pipefail
temporary=$(mktemp -d "$PWD/.oci-roundtrip.XXXXXX")
registry=zrlog-oci-roundtrip-$GITHUB_RUN_ID
cleanup() {
  docker rm -f "$registry" >/dev/null 2>&1 || true
  rm -rf "$temporary"
}
trap cleanup EXIT
export ZRLOG_EXPECTED_VERSION=4.0.0-SNAPSHOT ZRLOG_CHANNEL=preview CHANNEL=preview
export ZRLOG_EXPECTED_SOURCE_COMMIT=$GITHUB_SHA
build_id=${GITHUB_SHA:0:7}
mkdir -p "$temporary/context"
cat > "$temporary/context/Dockerfile" <<'DOCKERFILE'
FROM busybox:1.37.0
ARG VERSION
ARG COMMIT
COPY --chmod=755 zrlog /opt/zrlog/zrlog
LABEL org.opencontainers.image.version="$VERSION" org.opencontainers.image.revision="$COMMIT"
DOCKERFILE
printf '#!/bin/sh\necho "zrlog version:%s - %s fixture"\n' "$ZRLOG_EXPECTED_VERSION" "$build_id" > "$temporary/context/zrlog"
for arch in amd64 arm64; do
  directory=$temporary/inputs/$arch
  mkdir -p "$directory"
  docker buildx build --platform "linux/$arch" --provenance mode=max --sbom true \
    --build-arg "VERSION=$ZRLOG_EXPECTED_VERSION" --build-arg "COMMIT=$GITHUB_SHA" \
    --output "type=oci,dest=$directory/image.oci.tar" "$temporary/context"
  jq -n --arg sourceCommit "$GITHUB_SHA" --arg buildId "$build_id" \
    --arg workflow "$GITHUB_SERVER_URL/$GITHUB_REPOSITORY/actions/runs/$GITHUB_RUN_ID" \
    '{sourceCommit:$sourceCommit,version:"4.0.0-SNAPSHOT",buildId:$buildId,buildWorkflow:$workflow,type:"预览版本",zipSha256:("0" * 64)}' \
    > "$directory/native-image-input.json"
  runner_arch=X64
  [[ $arch != arm64 ]] || runner_arch=ARM64
  jq -n --arg sourceCommit "$GITHUB_SHA" --arg runnerArch "$runner_arch" \
    --arg workflow "$GITHUB_SERVER_URL/$GITHUB_REPOSITORY/actions/runs/$GITHUB_RUN_ID" \
    '{sourceCommit:$sourceCommit,workflow:$workflow,channel:"preview",packageType:"native",runnerOs:"Linux",runnerArch:$runnerArch}' \
    > "$directory/build.json"
  sha256sum "$temporary/context/zrlog" > "$directory/native.sha256"
  bash docker/verify-linux-image.sh "$directory" "linux/$arch" "zrlog/oci-fixture:$arch"
done
python3 docker/assemble-linux-images.py --inputs "$temporary/inputs" --output "$temporary/assembly" \
  --platform linux/amd64 --platform linux/arm64
docker run -d --name "$registry" -p 5000:5000 registry:2
export DOCKERHUB_IMAGE=localhost:5000/fixture/hub ALIYUN_IMAGE=localhost:5000/fixture/aliyun
export REGISTRY_TLS_VERIFY=false GITHUB_OUTPUT=$temporary/output GITHUB_ENV=$temporary/environment
for attempt in {1..20}; do
  if curl -fsS http://localhost:5000/v2/ >/dev/null; then break; fi
  [[ $attempt -lt 20 ]]
  sleep 1
done
bash docker/publish-assembled-linux-images.sh "$temporary/assembly"
# A retry must reuse both indexes without replacing an immutable runtime.
bash docker/publish-assembled-linux-images.sh "$temporary/assembly"
jq '.inputs[0].runtime.digest = ("sha256:" + ("0" * 64))' \
  "$temporary/assembly/image-assembly.json" > "$temporary/changed.json"
mv "$temporary/changed.json" "$temporary/assembly/image-assembly.json"
if bash docker/publish-assembled-linux-images.sh "$temporary/assembly"; then
  echo 'A conflicting immutable image was accepted' >&2
  exit 1
fi
