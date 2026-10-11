#!/usr/bin/env bash
# Validate actual Compose rendering without contacting or changing a Docker Engine.
set -euo pipefail
cd "$(dirname "$0")/.."
compose=(docker compose)
if [[ -n ${ZRLOG_COMPOSE_COMMAND:-} ]]; then compose=("$ZRLOG_COMPOSE_COMMAND"); fi
root=$(mktemp -d)
trap 'rm -rf "$root"' EXIT
export MYSQL_PASSWORD=compose-validation-password
export COMPOSE_PROJECT_NAME=updater-config-test ZRLOG_DEPLOYMENT_DIR="$root"
export ZRLOG_UPDATER_TOKEN=compose_validation_token_1234567890abcdef
unset ZRLOG_IMAGE ZRLOG_UPDATER_IMAGE MYSQL_IMAGE ZRLOG_COMPOSE_FILES
"${compose[@]}" --env-file /dev/null -f docker/docker-compose.yml config --format json > "$root/default.json"
expected=$(sed -n 's/^ZRLOG_UPDATER_IMAGE=//p' docker/.env.example)
jq -e --arg image "$expected" --arg root "$root" --arg token "$ZRLOG_UPDATER_TOKEN" '
  (.services | keys) == ["mysql","updater","zrlog"] and
  .services.updater.image == $image and
  .services.updater.environment.UPDATER_COMPOSE_FILES == "docker-compose.yml" and
  .services.updater.environment.UPDATER_TOKEN == $token and
  .services.zrlog.environment.ZRLOG_UPDATER_TOKEN == $token and
  .services.zrlog.environment.ZRLOG_UPDATER_URL == "http://updater:8081" and
  (.services.updater.ports // [] | length) == 0 and
  any(.services.updater.volumes[]; .type == "bind" and .source == $root and .target == $root) and
  (.volumes | keys) == ["conf","mysql-data","static","updater-state"]' "$root/default.json" >/dev/null
for registry in zrlog registry.cn-chengdu.aliyuncs.com/hibegin; do
  export ZRLOG_IMAGE="$registry/zrlog:3.9.2" ZRLOG_UPDATER_IMAGE="$registry/zrlog-updater:verified"
  "${compose[@]}" --env-file /dev/null -f docker/docker-compose.yml config --format json > "$root/override.json"
  jq -e --arg app "$ZRLOG_IMAGE" --arg updater "$ZRLOG_UPDATER_IMAGE" \
    '.services.zrlog.image == $app and .services.updater.image == $updater' "$root/override.json" >/dev/null
done
export ZRLOG_COMPOSE_FILES=docker-compose.yml,docker-compose.updater.yml
printf 'services:\n  zrlog:\n    image: zrlog/zrlog:3.9.2\n' > "$root/docker-compose.yml"
cp docker/docker-compose.updater.yml "$root/docker-compose.updater.yml"
"${compose[@]}" --env-file /dev/null -f "$root/docker-compose.yml" -f "$root/docker-compose.updater.yml" \
  config --format json > "$root/overlay.json"
jq -e '.services.updater.environment.UPDATER_COMPOSE_FILES == "docker-compose.yml,docker-compose.updater.yml"' "$root/overlay.json" >/dev/null
unset ZRLOG_UPDATER_TOKEN
if "${compose[@]}" --env-file /dev/null -f docker/docker-compose.yml config --quiet >/dev/null 2>&1; then
  printf 'Missing updater token was accepted\n' >&2
  exit 1
fi
printf 'Default Compose, registry overrides, legacy overlay and required token checks passed\n'
