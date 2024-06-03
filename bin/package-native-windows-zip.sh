#!/usr/bin/env bash
sh bin/package-native.sh
version=$(printf 'VER\t${project.version}' | ./mvnw help:evaluate | grep '^VER' | cut -f2)
commonFiles='README.md README.en-us.md LICENSE doc'
7z a -tzip "zrlog-${version}-Windows-$(uname -m).zip" zrlog.exe ${commonFiles}