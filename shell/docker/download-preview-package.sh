#!/usr/bin/env bash
# Select an exact artifact from a successful Native job, even if Docker failed the run.
set -euo pipefail
run_id=${1:?Native preview run ID required}
output=${2:?output directory required}
artifact_id=${3:?Native artifact ID required}
[[ $run_id =~ ^[0-9]+$ && $artifact_id =~ ^[0-9]+$ ]]
repo=94fzb/zrlog
tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT
gh api "repos/$repo/actions/runs/$run_id" > "$tmp/run.json"
jq -e --arg repo "$repo" '.repository.full_name == $repo and
  .head_repository.full_name == $repo and
  (.path == ".github/workflows/build-package.yml" or
   .path == ".github/workflows/native-build-preview-package-zip.yml")' "$tmp/run.json" >/dev/null
gh api --paginate "repos/$repo/actions/runs/$run_id/jobs?filter=all&per_page=100" \
  --jq '.jobs[]' | jq -s '[.[] | select((.name == "ZrLog on ubuntu-22.04" or .name == "build / native on ubuntu-22.04" or .name == "native on ubuntu-22.04") and .conclusion == "success") | .run_attempt] | unique' > "$tmp/attempts.json"
gh api --paginate "repos/$repo/actions/runs/$run_id/artifacts?per_page=100" \
  --jq '.artifacts[]' | jq -s '.' > "$tmp/artifacts.json"
selected=$(jq -er --arg id "$artifact_id" --slurpfile attempts "$tmp/attempts.json" '
  [.[] | select(.expired == false and (.name | test("^native-preview-docker-[0-9]+$"))) |
    . + {attempt: (.name | sub("^native-preview-docker-"; "") | tonumber)} |
    select(.attempt as $attempt | $attempts[0] | index($attempt)) |
    select((.id | tostring) == $id)] |
  if length == 1 then .[0].id
  else error("No retained Docker artifact from the successful Native job") end
' "$tmp/artifacts.json")
gh api "repos/$repo/actions/artifacts/$selected/zip" > "$tmp/artifact.zip"
unzip -q "$tmp/artifact.zip" -d "$tmp/artifact"
bash "$(dirname "$0")/prepare-preview-package.sh" "$tmp/artifact" "$run_id" "$output"
