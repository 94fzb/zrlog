#!/usr/bin/env bash
# Reuse a successful, commit-pinned Native acceptance artifact without publishing its ZIP.
set -euo pipefail
run_id=${1:?run ID required}
commit=${2:?source commit required}
version=${3:?version required}
output=${4:?output directory required}
[[ $run_id =~ ^[0-9]+$ && $commit =~ ^[0-9a-f]{40}$ ]]
tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT
gh api "repos/94fzb/zrlog/actions/runs/$run_id" > "$tmp/run.json"
jq -e --arg commit "$commit" '.conclusion == "success" and .head_sha == $commit and
  .repository.full_name == "94fzb/zrlog" and .head_repository.full_name == "94fzb/zrlog" and
  .path == ".github/workflows/docker-updater-acceptance.yml"' "$tmp/run.json" >/dev/null
gh run download "$run_id" --repo 94fzb/zrlog --name docker-updater-native-acceptance --dir "$tmp/artifact"
native="$tmp/artifact/preview/last.Linux-amd64.version.json"
archive="$tmp/artifact/preview/zrlog-Linux-amd64.zip"
properties="$tmp/artifact/evidence/build.properties"
jq -e --arg commit "$commit" --arg version "$version" --arg run "$run_id" '
  .sourceCommit == $commit and .version == $version and (.version | endswith("-SNAPSHOT")) and
  .sourceRepository == "https://github.com/94fzb/zrlog" and
  .buildWorkflow == ("https://github.com/94fzb/zrlog/actions/runs/" + $run) and
  (.zipSha256 | test("^[0-9a-f]{64}$"))' "$native" >/dev/null
[[ $(cat "$tmp/artifact/evidence/application-commit.txt") == "$commit" ]]
[[ $(stat -c %s "$archive") == "$(jq -r .zipFileSize "$native")" ]]
sha=$(jq -r .zipSha256 "$native")
printf '%s  %s\n' "$sha" "$archive" | sha256sum --check --strict -
awk -v sha="$sha" '$1 == sha {found=1} END {exit !found}' "$tmp/artifact/evidence/sha256.txt"
property() { sed -n "s/^$1=//p" "$properties"; }
[[ $(property version) == "$version" && $(property sourceCommit) == "$commit" ]]
[[ $(property buildId) == "$(jq -r .buildId "$native")" && $(property buildTime) == "$(jq -r .releaseDate "$native")" ]]
[[ $(property mirrorWebSite) == https://dl.zrlog.com/ && $(property dockerUpdateVersionJsonFilename) == last.docker-image.version.json ]]
[[ $(property fileArch) == Linux-amd64 && $(property runtimeType) == native && $(property runMode) == preview ]]
unzip -tq "$archive"
# Only the executable enters the image; documentation/evidence remain CI artifacts.
mkdir -p "$output/package"
unzip -p "$archive" zrlog > "$output/package/zrlog"
chmod +x "$output/package/zrlog"
cp "$native" "$output/native-image-input.json"
cp "$properties" "$output/build.properties"
sha256sum "$output/package/zrlog" > "$output/native.sha256"
