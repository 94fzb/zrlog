#!/usr/bin/env bash
sh bin/package-native.sh
version=$(printf 'VER\t${project.version}' | ./mvnw help:evaluate | grep '^VER' | cut -f2)
./mvnw -Pjdeb package
mv package-web/target/*.deb zrlog-"${version}-$(uname -s)-$(dpkg --print-architecture)".deb