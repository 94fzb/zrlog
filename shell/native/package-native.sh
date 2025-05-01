#!/usr/bin/env bash
rm -rf lib
rm -rf zrlog.*
rm -rf zrlog-.*
rm -rf conf/plugins
java -version
bash -e bin/add-build-info.sh "${1}" "${2}"
export JDK_JAVA_OPTIONS='--add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.desktop/java.awt.font=ALL-UNNAMED'
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z clean install -U
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Djakarta-scope='provided' -Pnative -Dagent exec:exec@java-agent -U  -f package-web/pom.xml
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Djakarta-scope='provided' -Pnative package -U -f package-web/pom.xml
if [ -f 'package-web/target/zrlog.exe' ];
then
  mv package-web/target/zrlog.exe zrlog.exe
fi
#copy file
if [ -f 'package-web/target/zrlog' ];
then
  mv package-web/target/zrlog zrlog
fi