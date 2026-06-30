#!/usr/bin/env bash
set -e

rm -rf lib
rm -rf zrlog.*
rm -rf zrlog-.*
rm -rf conf/plugins
java -version
runMode="${1:-dev}"
bash -e bin/add-build-info.sh "${runMode}" "${ZRLOG_RUNTIME_TYPE:-native}" "${ZRLOG_PACKAGE_TYPE:-zip}"
export JDK_JAVA_OPTIONS='--add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.desktop/java.awt.font=ALL-UNNAMED'
nativeBuildArgs=()
if [[ -n "${2:-}" ]]; then
  nativeBuildArgs+=("${2}")
fi
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Dmaven.test.skip=false -DskipTests=false clean install -U
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Dmaven.test.skip=false -DskipTests=false -Djakarta-scope='provided' -Dzrlog-polyglot-template-scope='compile' "${nativeBuildArgs[@]}" -Pnative -Dagent exec:exec@java-agent -U -f package/pom.xml
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Dmaven.test.skip=false -DskipTests=false -Djakarta-scope='provided' -Dzrlog-polyglot-template-scope='compile' "${nativeBuildArgs[@]}" -Pnative package -U -f package/pom.xml
if [ -f 'package/target/zrlog.exe' ];
then
  mv "package/target/zrlog.exe" "zrlog.exe"
fi
#copy file
if [ -f 'package/target/zrlog' ];
then
  mv "package/target/zrlog" "zrlog"
fi
