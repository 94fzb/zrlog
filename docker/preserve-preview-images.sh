#!/usr/bin/env bash
# Keep immutable tags; migrate the earlier full-commit tags only after checking the actual build.
set -euo pipefail
metadata=${1:?Native metadata required}
tag=${2:?Docker tag required}
version=$(jq -er .version "$metadata")
build_id=$(jq -er .buildId "$metadata")
commit=$(jq -er .sourceCommit "$metadata")
tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT
inspect() {
  if docker buildx imagetools inspect "$1" --format '{{.Manifest.Digest}}' > "$tmp/digest" 2> "$tmp/error"; then return 0; fi
  if grep -Eqi 'not found|manifest unknown' "$tmp/error"; then return 4; fi
  cat "$tmp/error" >&2
  return 1
}
for repository in zrlog/zrlog registry.cn-chengdu.aliyuncs.com/hibegin/zrlog; do
  registry=dockerhub
  [[ $repository != registry.* ]] || registry=aliyun
  status=0
  inspect "$repository:$tag" || status=$?
  if [[ $status == 0 ]]; then printf '%s=true\n' "$registry"; continue; fi
  [[ $status == 4 ]]
  status=0
  inspect "$repository:$version-$commit-preview" || status=$?
  if [[ $status == 4 ]]; then continue; fi
  [[ $status == 0 ]]
  reference="$repository@$(cat "$tmp/digest")"
  docker pull --platform linux/amd64 "$reference" >&2
  actual=$(docker run --rm --entrypoint /opt/zrlog/zrlog "$reference" --version)
  actual=${actual/ version: / version:}
  # A dependency rebuild may share sourceCommit but has its own buildId.
  [[ $actual == "zrlog version:$version - $build_id "* ]] || continue
  [[ $(docker image inspect --format '{{index .Config.Labels "org.opencontainers.image.revision"}}' "$reference") == "$commit" ]]
  docker buildx imagetools create --tag "$repository:$tag" "$reference" >&2
  printf '%s=true\n' "$registry"
done
