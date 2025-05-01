#!/usr/bin/env bash
version=$(printf 'VER\t${project.version}' | ./mvnw help:evaluate | grep '^VER' | cut -f2)
# https://dl.zrlog.com mirror folder
mirrorWebSite=https://dl.zrlog.com/
runMode=${1}
runModeDesc=${2}
Date=$(git log -1 --format=%cd --date=format:'%Y-%m-%d %H:%M:%S%z' | sed "s/\([+-]\)\([0-9][0-9]\)\([0-9][0-9]\)/\1\2:\3/")
buildId=$(git rev-parse --short HEAD)

mkdir -p web/src/main/resources/
echo -e "version=${version}\nrunMode=${runMode}\nrunModeDesc=${runModeDesc}\nbuildId=${buildId}\nbuildTime=${Date}\nmirrorWebSite=${mirrorWebSite}" > web/src/main/resources/build.properties

bash bin/build-system-info.sh