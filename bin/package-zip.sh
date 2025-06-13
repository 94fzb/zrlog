#!/usr/bin/env bash
rm -rf web/src/main/lib
rm -rf web/src/main/zrlog.*
version=$(printf 'VER\t${project.version}' | ./mvnw help:evaluate | grep '^VER' | cut -f2)
./mvnw clean package
mv web/src/main/zrlog-starter.jar target/zrlog-starter.jar
cd target && mkdir webapp && cp zrlog-"${version}".war webapp/ROOT.war && cd webapp && unzip ROOT.war
rm -rf WEB-INF/classes && rm WEB-INF/db.properties && rm WEB-INF/install.lock  && rm -rf WEB-INF/lib && rm -rf ROOT.war
cd ..
cp -R ../web/src/main/bin bin
cp -R ../web/src/main/lib lib
zip -9 -r zrlog-"${version}".zip bin lib webapp zrlog-starter.jar
rm -rf bin lib webapp zrlog-start.jar
