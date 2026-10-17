#!/usr/bin/env bash
# Recheck actual immutable registry digests and runtime-tested platforms.
set -euo pipefail
script_dir=$(cd "$(dirname "$0")" && pwd)
images=() platforms=()
while (($#)); do
  case $1 in
    --native-metadata) native=$2; shift 2 ;;
    --image) images+=("$2"); shift 2 ;;
    --platform) platforms+=("$2"); shift 2 ;;
    --channel) channel=$2; shift 2 ;;
    --tag) tag=$2; shift 2 ;;
    --output) output=$2; shift 2 ;;
    *) echo "Unknown option: $1" >&2; exit 1 ;;
  esac
done
: "${native:?}" "${channel:?}" "${tag:?}" "${output:?}"
((${#images[@]} > 0 && ${#platforms[@]} > 0))
tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT
expected=$(printf '%s\n' "${platforms[@]}" | jq -Rsc 'split("\n")[:-1] | sort')
entries=()
for image in "${images[@]}"; do
  repo=${image%@*} digest=${image##*@}
  [[ $image == *@* && $digest =~ ^sha256:[0-9a-f]{64}$ ]]
  [[ $(docker buildx imagetools inspect "$image" --format '{{.Manifest.Digest}}') == "$digest" ]]
  docker buildx imagetools inspect "$image" --raw > "$tmp/descriptor.json"
  actual=$(jq -c '[.manifests[] | select(.platform.os == "linux") |
    .platform.os + "/" + .platform.architecture] | sort' "$tmp/descriptor.json")
  [[ $actual == "$expected" ]] || { echo 'Image platforms differ from tested platforms' >&2; exit 1; }
  entry="$tmp/${#entries[@]}.json"
  jq --arg repo "$repo" --arg digest "$digest" --arg tag "$tag" --argjson platforms "$actual" \
    '{version, sourceCommit, repository:$repo, digest:$digest, tag:$tag, platforms:$platforms}' "$native" > "$entry"
  entries+=(--image-entry "$entry")
done
bash "$script_dir/write-image-manifest.sh" --native-metadata "$native" --channel "$channel" \
  "${entries[@]}" --output "$output"
