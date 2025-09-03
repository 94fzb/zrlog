#!/usr/bin/env bash
sh shell/native/package-native.sh "${1}"
function buildProp() {
    grep "${1}" "web/src/main/resources/build.properties"|awk -F "${1}"'=' '{print $2}'
}

# shellcheck disable=SC2034
version="$(buildProp 'version')"
buildId="$(git log -1 --format=%cd --date=format:'%Y%m%d%H%M%S')-$(buildProp 'buildId')"
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Darchitecture="$(dpkg --print-architecture)" -Pjdeb package -DgitCommitId="${buildId}"