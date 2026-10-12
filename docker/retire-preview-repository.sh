#!/usr/bin/env bash
# Explicitly invoked one-time retirement of the old zrlog/zrlog-preview repository only.
set -euo pipefail
[[ ${1:-} == --delete-zrlog-preview ]]
: "${DOCKERHUB_PASSWORD:?}"
umask 077
tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT
endpoint=https://hub.docker.com/v2/repositories/zrlog/zrlog-preview/
jq -n '{identifier:"zrlog",secret:env.DOCKERHUB_PASSWORD}' > "$tmp/login.json"
curl --fail --silent --show-error --connect-timeout 15 --max-time 60 \
  -H 'Content-Type: application/json' --data-binary "@$tmp/login.json" \
  https://hub.docker.com/v2/auth/token -o "$tmp/auth.json"
printf 'Authorization: Bearer %s\n' "$(jq -er .access_token "$tmp/auth.json")" > "$tmp/headers"
request() {
  curl --silent --show-error --connect-timeout 15 --max-time 60 \
    -H "@$tmp/headers" --request "$1" "$endpoint" -o "$tmp/response.json" -w '%{http_code}'
}
status=$(request GET)
if [[ $status == 404 ]]; then echo 'Old preview repository is already absent'; exit 0; fi
[[ $status == 200 ]] || { echo "Repository lookup returned HTTP $status" >&2; exit 1; }
jq -e '.namespace == "zrlog" and .name == "zrlog-preview"' "$tmp/response.json" >/dev/null
status=$(request DELETE)
[[ $status == 202 || $status == 204 ]] || { echo "Repository deletion returned HTTP $status" >&2; exit 1; }
status=$(request GET)
[[ $status == 404 ]] || { echo "Repository deletion verification returned HTTP $status" >&2; exit 1; }
echo 'Deleted Docker Hub repository zrlog/zrlog-preview; authenticated lookup returns 404'
