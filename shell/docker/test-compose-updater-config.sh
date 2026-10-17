#!/usr/bin/env bash
# Validate actual Compose rendering without contacting or changing a Docker Engine.
set -euo pipefail
cd "$(dirname "$0")/../.."
compose=(docker compose)
if [[ -n ${ZRLOG_COMPOSE_COMMAND:-} ]]; then compose=("$ZRLOG_COMPOSE_COMMAND"); fi
root=$(mktemp -d)
trap 'rm -rf "$root"' EXIT
# Test the file as the source of deployment values, regardless of the caller's env.
while IFS='=' read -r key _; do
  if [[ $key =~ ^[A-Z][A-Z0-9_]*$ ]]; then unset "$key"; fi
done < docker/.env.example
unset COMPOSE_FILE COMPOSE_ENV_FILES COMPOSE_DISABLE_ENV_FILE
cp docker/docker-compose.yml "$root/docker-compose.yml"
sed -e 's/^MYSQL_PASSWORD=$/MYSQL_PASSWORD=compose-validation-password/' \
  -e 's/^COMPOSE_PROJECT_NAME=$/COMPOSE_PROJECT_NAME=updater-config-test/' \
  -e "s|^ZRLOG_DEPLOYMENT_DIR=$|ZRLOG_DEPLOYMENT_DIR=$root|" \
  -e 's/^ZRLOG_UPDATER_TOKEN=$/ZRLOG_UPDATER_TOKEN=compose_validation_token_1234567890abcdef/' \
  docker/.env.example > "$root/.env"
# Plain `docker compose up` and updater's explicit --env-file must see the same config.
(cd "$root" && "${compose[@]}" config --format json) > "$root/default.json"
"${compose[@]}" --env-file "$root/.env" -f "$root/docker-compose.yml" config --format json > "$root/explicit.json"
cmp "$root/default.json" "$root/explicit.json"
expected=$(sed -n 's/^ZRLOG_UPDATER_IMAGE=//p' docker/.env.example)
expected_app=$(sed -n 's/^ZRLOG_IMAGE=//p' docker/.env.example)
expected_mysql=$(sed -n 's/^MYSQL_IMAGE=//p' docker/.env.example)
jq -e --arg image "$expected" --arg app "$expected_app" --arg mysql "$expected_mysql" --arg root "$root" '
  .name == "updater-config-test" and
  (.services | keys) == ["mysql","updater","zrlog"] and
  .services.updater.image == $image and
  .services.zrlog.image == $app and
  .services.mysql.image == $mysql and
  .services.updater.environment.UPDATER_COMPOSE_FILES == "docker-compose.yml" and
  .services.updater.environment.UPDATER_TOKEN == "compose_validation_token_1234567890abcdef" and
  .services.zrlog.environment.ZRLOG_UPDATER_TOKEN == .services.updater.environment.UPDATER_TOKEN and
  .services.zrlog.environment.ZRLOG_UPDATER_URL == "http://updater:8081" and
  (.services.updater.ports // [] | length) == 0 and
  any(.services.updater.volumes[]; .type == "bind" and .source == $root and .target == $root) and
  (.volumes | keys) == ["conf","mysql-data","static","updater-state"]' "$root/default.json" >/dev/null
for registry in zrlog registry.cn-chengdu.aliyuncs.com/hibegin; do
  sed -e "s|^ZRLOG_IMAGE=.*|ZRLOG_IMAGE=$registry/zrlog:verified|" \
    -e "s|^ZRLOG_UPDATER_IMAGE=.*|ZRLOG_UPDATER_IMAGE=$registry/zrlog-updater:verified|" \
    -e 's|^MYSQL_IMAGE=.*|MYSQL_IMAGE=registry.example.com/mysql:8.4|' \
    -e 's/^ZRLOG_BIND_ADDRESS=.*/ZRLOG_BIND_ADDRESS=127.0.0.1/' \
    -e 's/^ZRLOG_PORT=.*/ZRLOG_PORT=18080/' "$root/.env" > "$root/override.env"
  "${compose[@]}" --env-file "$root/override.env" -f "$root/docker-compose.yml" config --format json > "$root/override.json"
  jq -e --arg app "$registry/zrlog:verified" --arg updater "$registry/zrlog-updater:verified" '
    .services.zrlog.image == $app and .services.updater.image == $updater and
    .services.mysql.image == "registry.example.com/mysql:8.4" and
    .services.zrlog.ports[0].host_ip == "127.0.0.1" and
    .services.zrlog.ports[0].published == "18080"' "$root/override.json" >/dev/null
done
# Changing only ZRLOG_IMAGE must preserve the complete remaining deployment config.
sed 's|^ZRLOG_IMAGE=.*|ZRLOG_IMAGE=registry.example.com/zrlog@sha256:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa|' \
  "$root/.env" > "$root/upgraded.env"
"${compose[@]}" --env-file "$root/upgraded.env" -f "$root/docker-compose.yml" config --format json > "$root/upgraded.json"
jq -e '.services.zrlog.image == "registry.example.com/zrlog@sha256:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"' "$root/upgraded.json" >/dev/null
jq -S 'del(.services.zrlog.image)' "$root/default.json" > "$root/before.json"
jq -S 'del(.services.zrlog.image)' "$root/upgraded.json" > "$root/after.json"
cmp "$root/before.json" "$root/after.json"
# Legacy/custom Compose integration remains separate from the new default file.
# shellcheck disable=SC2016
printf 'services:\n  zrlog:\n    image: ${ZRLOG_IMAGE:?Set ZRLOG_IMAGE in .env}\n' > "$root/legacy.yml"
cp docker/docker-compose.updater.yml "$root/docker-compose.updater.yml"
sed 's/^ZRLOG_COMPOSE_FILES=.*/ZRLOG_COMPOSE_FILES=legacy.yml,docker-compose.updater.yml/' "$root/.env" > "$root/overlay.env"
"${compose[@]}" --env-file "$root/overlay.env" -f "$root/legacy.yml" -f "$root/docker-compose.updater.yml" \
  config --format json > "$root/overlay.json"
jq -e '.services.updater.environment.UPDATER_COMPOSE_FILES == "legacy.yml,docker-compose.updater.yml"' "$root/overlay.json" >/dev/null
while IFS='=' read -r key _; do
  if [[ ! $key =~ ^[A-Z][A-Z0-9_]*$ ]]; then continue; fi
  sed "/^${key}=/d" "$root/.env" > "$root/missing.env"
  if "${compose[@]}" --env-file "$root/missing.env" -f "$root/docker-compose.yml" config --quiet >/dev/null 2>&1; then
    printf 'Missing %s was accepted\n' "$key" >&2
    exit 1
  fi
done < docker/.env.example
printf '.env loading, registry/port overrides, upgrade preservation, legacy overlay and required settings checks passed\n'
