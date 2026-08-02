#!/usr/bin/env bash
set -e

nativeBuildArgs=("${@:2}")
ZRLOG_RUNTIME_TYPE=native ZRLOG_PACKAGE_TYPE="${ZRLOG_PACKAGE_TYPE:-zip}" bash -e shell/native/package-native.sh "${1}" "${nativeBuildArgs[@]}"
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Dmaven.test.skip=false -DskipTests=false -Ppackage-native-zip assembly:single -f "package/pom.xml"
