#!/usr/bin/env bash
rm -rf lib
rm -rf zrlog.*
rm -rf conf/plugins
version=$(printf 'VER\t${project.version}' | ./mvnw help:evaluate | grep '^VER' | cut -f2)
./mvnw -Pjar clean package
commonFiles='README.md README.en-us.md LICENSE doc'
zip -9 -r zrlog-"${version}".zip bin/run.sh bin/start.sh bin/start.bat ${commonFiles} lib zrlog-starter.jar
