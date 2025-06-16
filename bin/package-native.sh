#!/usr/bin/env bash
rm -rf lib
rm -rf zrlog.*
rm -rf zrlog-.*
rm -rf conf/plugins
java -version
export JDK_JAVA_OPTIONS='--add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.desktop/java.awt.font=ALL-UNNAMED'
./mvnw clean -Pjava21-enabled install -U
./mvnw -Pjava21-enabled,native -Dagent exec:exec@java-agent -U  -f package-web/pom.xml
./mvnw -Pjava21-enabled,native package -U -f package-web/pom.xml
if [ -f 'package-web/target/zrlog.exe' ];
then
  mv package-web/target/zrlog.exe zrlog.exe
fi
#copy file
if [ -f 'package-web/target/zrlog' ];
then
  mv package-web/target/zrlog zrlog
fi