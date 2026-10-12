#!/usr/bin/env bash
# No network: prove deletion is opt-in and restricted to the exact retired repository.
set -euo pipefail
script_dir=$(cd "$(dirname "$0")" && pwd)
tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT
mkdir "$tmp/bin"
export RETIRE_TEST_STATE="$tmp" DOCKERHUB_PASSWORD=fixture
export PATH="$tmp/bin:$PATH"
cat > "$tmp/bin/curl" <<'MOCK'
#!/usr/bin/env bash
set -euo pipefail
method=GET url='' output=''
while (($#)); do
  case $1 in
    --connect-timeout|--max-time|-H|-w) shift 2 ;;
    --data-binary) method=POST; shift 2 ;;
    --request) method=$2; shift 2 ;;
    -o) output=$2; shift 2 ;;
    https://*) url=$1; shift ;;
    --fail|--silent|--show-error) shift ;;
    *) exit 1 ;;
  esac
done
if [[ $url == https://hub.docker.com/v2/auth/token && $method == POST ]]; then
  printf '{"access_token":"fixture"}\n' > "$output"; exit 0
fi
[[ $url == https://hub.docker.com/v2/repositories/zrlog/zrlog-preview/ ]]
if [[ $method == DELETE ]]; then
  printf 'DELETE\n' >> "$RETIRE_TEST_STATE/deletes"
  touch "$RETIRE_TEST_STATE/absent"
  printf '{}\n' > "$output"
  printf 204
elif [[ -f $RETIRE_TEST_STATE/absent ]]; then
  printf '{}\n' > "$output"; printf 404
elif [[ ${RETIRE_TEST_DENIED:-0} == 1 ]]; then
  printf '{}\n' > "$output"; printf 403
else
  jq -n --arg name "${RETIRE_TEST_NAME:-zrlog-preview}" '{namespace:"zrlog",name:$name}' > "$output"
  printf 200
fi
MOCK
chmod +x "$tmp/bin/curl"
retire() { bash "$script_dir/retire-preview-repository.sh" "$@"; }
reject() { if "$@" > "$tmp/rejected.log" 2>&1; then echo 'Unexpected retirement success' >&2; exit 1; fi; }
reject retire
RETIRE_TEST_NAME=zrlog reject retire --delete-zrlog-preview
RETIRE_TEST_DENIED=1 reject retire --delete-zrlog-preview
[[ ! -e $tmp/deletes ]]
retire --delete-zrlog-preview
[[ $(cat "$tmp/deletes") == DELETE ]]
retire --delete-zrlog-preview
[[ $(wc -l < "$tmp/deletes") == 1 ]]
echo 'Preview repository retirement scope and idempotency checks passed'
