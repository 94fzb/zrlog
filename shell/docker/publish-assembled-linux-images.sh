#!/usr/bin/env bash
# Publish the already assembled OCI indexes; never rebuild an image here.
set -euo pipefail
directory=$1
report=$directory/image-assembly.json
[[ $(jq -r .channel "$report") == "$CHANNEL" ]]
jq -e '.distributionMetadataEligible == true' "$report" >/dev/null
version=$(jq -er .version "$report")
if [[ $CHANNEL == preview ]]; then
  tag=$(bash shell/docker/preview-image-tag.sh "$directory/native-image-input.json")
  alias=preview
else
  tag=$version
  alias=latest
fi
tls_verify=${REGISTRY_TLS_VERIFY:-true}
if [[ $tls_verify != true ]]; then
  [[ $tls_verify == false && $DOCKERHUB_IMAGE == localhost:*/* && $ALIYUN_IMAGE == localhost:*/* ]]
fi
expected=$(jq -cS '[.inputs[] | {platform:.platform,digest:.runtime.digest}] | sort_by(.platform)' "$report")
tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT
platform_digests() {
  jq -cS '[.manifests[] | select(.platform.os == "linux") |
    {platform:(.platform.os + "/" + .platform.architecture),digest:.digest}] | sort_by(.platform)' "$1"
}
for role in dockerhub aliyun; do
  if [[ $role == dockerhub ]]; then repository=$DOCKERHUB_IMAGE; else repository=$ALIYUN_IMAGE; fi
  reference=$repository:$tag
  archive=$directory/$(jq -er --arg role "$role" '.images[] | select(.role == $role) | .archive' "$report")
  archive_sha=$(jq -er --arg role "$role" '.images[] | select(.role == $role) | .sha256' "$report")
  [[ $(sha256sum "$archive" | cut -d ' ' -f 1) == "$archive_sha" ]]
  if bash shell/docker/skopeo.sh inspect --tls-verify="$tls_verify" --raw "docker://$reference" > "$tmp/existing.json" 2> "$tmp/error"; then
    [[ $(platform_digests "$tmp/existing.json") == "$expected" ]] || {
      echo "Immutable image already differs: $reference" >&2; exit 1;
    }
  else
    grep -Eqi 'not found|manifest unknown' "$tmp/error" || { cat "$tmp/error" >&2; exit 1; }
    bash shell/docker/skopeo.sh copy --all --preserve-digests --dest-tls-verify="$tls_verify" \
      "oci-archive:$archive" "docker://$reference"
    bash shell/docker/skopeo.sh inspect --tls-verify="$tls_verify" --raw "docker://$reference" > "$tmp/existing.json"
    expected_digest=$(jq -er --arg role "$role" '.images[] | select(.role == $role) | .digest' "$report")
    [[ sha256:$(sha256sum "$tmp/existing.json" | cut -d ' ' -f 1) == "$expected_digest" ]]
  fi
  [[ $(platform_digests "$tmp/existing.json") == "$expected" ]]
  value=sha256:$(sha256sum "$tmp/existing.json" | cut -d ' ' -f 1)
  echo "$role=$repository@$value" >> "$GITHUB_OUTPUT"
done
echo "DOCKER_TAG_VERSION=$tag" >> "$GITHUB_ENV"
echo "DOCKER_ALIAS=$alias" >> "$GITHUB_ENV"
