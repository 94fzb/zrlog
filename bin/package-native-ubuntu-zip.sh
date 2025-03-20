#!/usr/bin/env bash
sh bin/package-native.sh
version=$(printf 'VER\t${project.version}' | ./mvnw help:evaluate | grep '^VER' | cut -f2)
commonFiles='README.md README.en-us.md LICENSE doc'
zip -9 -r zrlog-"${version}-$(uname -s)-$(dpkg --print-architecture)".zip ${commonFiles} zrlog
