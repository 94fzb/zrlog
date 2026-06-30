#!/usr/bin/env bash
set -e

ZRLOG_RUNTIME_TYPE=native ZRLOG_PACKAGE_TYPE=deb bash -e shell/native/package-native.sh "${1}"
function buildProp() {
    sed -n -e "s/^${1}=//p" "zrlog-web/src/main/resources/build.properties"
}

# shellcheck disable=SC2034
version="$(buildProp 'version')"
buildId="$(git log -1 --format=%cd --date=format:'%Y%m%d%H%M%S')-$(buildProp 'buildId')"
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Dmaven.test.skip=false -DskipTests=false -Darchitecture="$(dpkg --print-architecture)" -Pjdeb package -DgitCommitId="${buildId}"
