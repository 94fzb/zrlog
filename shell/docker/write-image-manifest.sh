#!/usr/bin/env bash
# Generate Docker metadata from verified Native identity and registry results.
set -euo pipefail
entries=()
while (($#)); do
  case $1 in
    --native-metadata) native=$2; shift 2 ;;
    --image-entry) entries+=("$2"); shift 2 ;;
    --channel) channel=$2; shift 2 ;;
    --output) output=$2; shift 2 ;;
    *) echo "Unknown option: $1" >&2; exit 1 ;;
  esac
done
: "${native:?}" "${channel:?}" "${output:?}"
((${#entries[@]} > 0))
date -d "$(jq -er .releaseDate "$native")" +%s >/dev/null
mkdir -p "$(dirname "$output")"
jq -se --slurpfile native "$native" --arg channel "$channel" '
  def require($ok; $message): if $ok then . else error($message) end;
  def repository:
    require(type == "string" and length <= 255 and
      test("^[a-z0-9][a-z0-9.:-]*(/[a-z0-9]+([._-][a-z0-9]+)*)+$"); "Invalid repository") |
    if (split("/")[0] | test("[.:]") or . == "localhost") then . else "docker.io/" + . end |
    sub("^index.docker.io/"; "docker.io/");
  $native[0] as $n |
  require($channel == "release" or $channel == "preview"; "Invalid channel") |
  require($n.version | test("^[0-9]+\\.[0-9]+\\.[0-9]+(-[A-Za-z0-9.-]+)?$"); "Invalid version") |
  require($n.sourceCommit | test("^[0-9a-f]{40}$"); "Invalid source commit") |
  require($n.buildId | test("^[A-Za-z0-9][A-Za-z0-9._-]{0,127}$"); "Invalid build ID") |
  require($channel != "release" or
    (($n.version | contains("SNAPSHOT") | not) and
     ($n.buildId | test("^[0-9a-f]{7,40}$")) and ($n.sourceCommit | startswith($n.buildId)));
    "Invalid release identity") |
  require($n.releaseDate | test("^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}(Z|[+-][0-9]{2}:[0-9]{2})$"); "Invalid date") |
  require(length >= 1 and length <= 32; "Expected 1-32 images") |
  map(
    require(.version == $n.version and .sourceCommit == $n.sourceCommit; "Image identity mismatch") |
    .repository |= repository |
    require(.digest | test("^sha256:[0-9a-f]{64}$"); "Invalid digest") |
    require(.tag | test("^[A-Za-z0-9_][A-Za-z0-9_.-]{0,127}$"); "Invalid tag") |
    require(.platforms | type == "array" and length >= 1 and length <= 16; "Missing platforms") |
    require(all(.platforms[]; test("^[a-z0-9]+/[a-z0-9]+(/[a-z0-9]+)?$")); "Invalid platform") |
    .platforms |= (map(if . == "linux/arm64/v8" then "linux/arm64" else . end) | sort) |
    {repository, tag, digest, platforms}
  ) |
  require(([.[] | .repository as $r | .platforms[] | [$r, .]] | length) ==
    ([.[] | .repository as $r | .platforms[] | [$r, .]] | unique | length); "Ambiguous repository/platform") |
  sort_by(.repository, .platforms) as $images |
  {schemaVersion:1, packageType:"docker-image", channel:$channel,
   version:$n.version, buildId:$n.buildId, sourceCommit:$n.sourceCommit,
   releaseDate:$n.releaseDate, type:($n.type // $channel),
   sourceRepository:($n.sourceRepository // "https://github.com/94fzb/zrlog"),
   buildWorkflow:($n.buildWorkflow // ""), images:$images}
' "${entries[@]}" > "$output"
