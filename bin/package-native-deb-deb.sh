#!/usr/bin/env bash
sh bin/package-native.sh
version=$(printf 'VER\t${project.version}' | ./mvnw help:evaluate | grep '^VER' | cut -f2)
buildId=$(git log -1 --format=%cd --date=format:'%Y%m%d%H%M%S')
./mvnw -Pjdeb package -DgitCommitId=${buildId}
mv package-web/target/*.deb zrlog-"${version}-$(uname -s)-$(dpkg --print-architecture)".deb