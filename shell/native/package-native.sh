#!/usr/bin/env bash
rm -rf lib
rm -rf zrlog.*
rm -rf zrlog-.*
rm -rf conf/plugins
java -version
bash -e bin/add-build-info.sh "${1}"
export JDK_JAVA_OPTIONS='--add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.desktop/java.awt.font=ALL-UNNAMED'
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z clean install -U
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Djakarta-scope='provided' -Dzrlog-polyglot-template-scope='compile' ${2} -Pnative -Dagent exec:exec@java-agent -U  -f package/pom.xml
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Djakarta-scope='provided' -Dzrlog-polyglot-template-scope='compile' ${2} -Pnative package -U -f package/pom.xml
if [ -f 'package/target/zrlog.exe' ];
then
  mv package/target/zrlog.exe zrlog.exe
fi
#copy file
if [ -f 'package/target/zrlog' ];
then
  mv package/target/zrlog zrlog
fi