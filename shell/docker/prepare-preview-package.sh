#!/usr/bin/env bash
# Validate the producer snapshot before handing its executable to Docker.
set -euo pipefail
artifact=${1:?artifact directory required}
run_id=${2:?Native preview run ID required}
output=${3:?output directory required}
[[ $run_id =~ ^[0-9]+$ ]]
native="$artifact/preview/last.Linux-amd64.version.json"
archive="$artifact/preview/zrlog-Linux-amd64.zip"
properties="$artifact/evidence/build.properties"
jq -e --arg run "$run_id" '
  .checksumAlgorithm == "sha256" and .type == "预览版本" and
  (.sourceCommit | test("^[0-9a-f]{40}$")) and
  .sourceRepository == "https://github.com/94fzb/zrlog" and
  .buildWorkflow == ("https://github.com/94fzb/zrlog/actions/runs/" + $run) and
  (.zipSha256 | test("^[0-9a-f]{64}$")) and
  (.zipFileSize | type == "number" and . > 0 and floor == .) and
  (.releaseDate | test("^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}(Z|[+-][0-9]{2}:[0-9]{2})$"))
' "$native" >/dev/null
bash "$(dirname "$0")/preview-image-tag.sh" "$native" >/dev/null
[[ $(stat -c %s "$archive") == "$(jq -r .zipFileSize "$native")" ]]
printf '%s  %s\n' "$(jq -r .zipSha256 "$native")" "$archive" | sha256sum --check --strict -
property() { sed -n "s/^$1=//p" "$properties"; }
for key in version buildId sourceCommit sourceRepository buildWorkflow; do
  [[ $(property "$key") == "$(jq -r --arg key "$key" '.[$key]' "$native")" ]]
done
[[ $(property buildTime) == "$(jq -r .releaseDate "$native")" ]]
[[ $(property mirrorWebSite) == https://dl.zrlog.com/ && $(property dockerUpdateVersionJsonFilename) == last.docker-image.version.json ]]
[[ $(property fileArch) == Linux-amd64 && $(property runtimeType) == native && $(property runMode) == preview && $(property packageType) == zip ]]
unzip -tq "$archive"
unzip -Z -1 "$archive" | awk '$0 == "zrlog" {count++} END {exit count != 1}'
mkdir -p "$output/package"
unzip -p "$archive" zrlog > "$output/package/zrlog"
[[ -s "$output/package/zrlog" ]]
chmod +x "$output/package/zrlog"
cp "$native" "$output/native-image-input.json"
cp "$properties" "$output/build.properties"
sha256sum "$output/package/zrlog" > "$output/native.sha256"
