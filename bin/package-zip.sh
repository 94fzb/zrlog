#!/usr/bin/env bash
version=$(printf 'VER\t${project.version}' | ./mvnw help:evaluate | grep '^VER' | cut -f2)
./mvnw clean install  -Dmaven.test.skip=true
cd web && ../mvnw clean compile assembly:single
cd ../target && mkdir webapp && cp zrlog-${version}.war webapp/ROOT.war && cd webapp && unzip ROOT.war
rm -rf WEB-INF/classes && rm WEB-INF/db.properties && rm WEB-INF/install.lock  && rm -rf WEB-INF/lib && rm -rf ROOT.war
cd ..
cp -R ../web/src/main/bin bin
zip -9 -r zrlog-${version}.zip bin webapp zrlog.jar
