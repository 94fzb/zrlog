#!/usr/bin/env bash
# Upload JSON only: immutable metadata first, channel pointer last, both conditional.
set -euo pipefail
script_dir=$(cd "$(dirname "$0")" && pwd)
manifest=$(realpath "${1:?manifest required}")
: "${ZRLOG_S3_BUCKET:?}" "${ZRLOG_S3_ENDPOINT:?}" "${AWS_ACCESS_KEY_ID:?}" "${AWS_SECRET_ACCESS_KEY:?}"
[[ $ZRLOG_S3_ENDPOINT == https://* ]]
export AWS_DEFAULT_REGION=${AWS_DEFAULT_REGION:-auto} AWS_PAGER=''
tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT
jq -e '.schemaVersion == 1 and .packageType == "docker-image"' "$manifest" >/dev/null
channel=$(jq -er .channel "$manifest")
entries=()
for ((i=0; i<$(jq '.images | length' "$manifest"); i++)); do
  jq --argjson i "$i" '. as $m | .images[$i] + {version:$m.version, sourceCommit:$m.sourceCommit}' \
    "$manifest" > "$tmp/entry-$i.json"
  entries+=(--image-entry "$tmp/entry-$i.json")
done
bash "$script_dir/write-image-manifest.sh" --native-metadata "$manifest" "${entries[@]}" \
  --channel "$channel" --output "$tmp/canonical.json"
diff -u <(jq -S . "$manifest") <(jq -S . "$tmp/canonical.json")
key="$channel/docker-image/$(jq -r '.version + "-" + .buildId' "$manifest").json"
latest_key="$channel/last.docker-image.version.json"
s3() { aws --endpoint-url "$ZRLOG_S3_ENDPOINT" s3api "$@"; }
get_object() {
  local key=$1 path=$2
  if s3 get-object --bucket "$ZRLOG_S3_BUCKET" --key "$key" "$path" > "$path.response" 2> "$tmp/s3.error"; then return 0; fi
  if grep -Eq '\((NoSuchKey|404|NotFound)\)' "$tmp/s3.error"; then return 4; fi
  cat "$tmp/s3.error" >&2
  return 1
}
status=0
get_object "$latest_key" "$tmp/previous" || status=$?
condition=(--if-none-match '*')
if [[ $status == 0 ]]; then
  old_time=$(date -d "$(jq -er .releaseDate "$tmp/previous")" +%s)
  new_time=$(date -d "$(jq -er .releaseDate "$manifest")" +%s)
  ((old_time <= new_time)) || { echo 'Refusing older channel metadata' >&2; exit 1; }
  if ((old_time == new_time)); then
    [[ $(jq -r .sourceCommit "$tmp/previous") == "$(jq -r .sourceCommit "$manifest")" ]] || exit 1
  fi
  condition=(--if-match "$(jq -er .ETag "$tmp/previous.response")")
else
  [[ $status == 4 ]]
fi
status=0
get_object "$key" "$tmp/immutable" || status=$?
if [[ $status == 0 ]]; then
  cmp "$manifest" "$tmp/immutable"
else
  [[ $status == 4 ]]
  s3 put-object --bucket "$ZRLOG_S3_BUCKET" --key "$key" --body "$manifest" \
    --content-type application/json --cache-control 'public, max-age=31536000, immutable' --if-none-match '*' >/dev/null
fi
get_object "$key" "$tmp/readback"
cmp "$manifest" "$tmp/readback"
if [[ ! -f $tmp/previous ]] || ! cmp -s "$manifest" "$tmp/previous"; then
  s3 put-object --bucket "$ZRLOG_S3_BUCKET" --key "$latest_key" --body "$manifest" \
    --content-type application/json --cache-control no-cache "${condition[@]}" >/dev/null
fi
get_object "$latest_key" "$tmp/latest"
cmp "$manifest" "$tmp/latest"
jq -n --arg immutableKey "$key" --arg channelKey "$latest_key" \
  --arg sha256 "$(sha256sum "$manifest" | cut -d ' ' -f 1)" '{immutableKey:$immutableKey, channelKey:$channelKey, sha256:$sha256}'
