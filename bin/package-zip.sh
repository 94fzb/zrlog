#!/usr/bin/env bash
rm -rf lib
rm -rf zrlog.*
rm -rf conf/plugins
version=$(printf 'VER\t${project.version}' | ./mvnw help:evaluate | grep '^VER' | cut -f2)
./mvnw clean package
zip -9 -r zrlog-"${version}".zip bin/run.sh bin/start.sh bin/start.bat lib static zrlog-starter.jar
