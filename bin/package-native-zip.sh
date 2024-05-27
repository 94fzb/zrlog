#!/usr/bin/env bash
sh bin/package-native.sh
version=$(printf 'VER\t${project.version}' | ./mvnw help:evaluate | grep '^VER' | cut -f2)
zip -9 -r zrlog-"${version}-$(uname -s)-$(uname -m)".zip bin/run.sh bin/start.sh bin/start.bat static zrlog
