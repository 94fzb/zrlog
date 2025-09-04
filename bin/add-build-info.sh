#!/usr/bin/env bash
# shellcheck disable=SC2016
version=$(printf 'VER\t${project.version}' | ./mvnw help:evaluate | grep '^VER' | cut -f2)
# https://dl.zrlog.com mirror folder
mirrorWebSite=https://dl.zrlog.com/
runMode=${1}
if [[ $runMode == 'release' ]]; then
runModeDesc="\\u6B63\\u5F0F\\u7248\\u672C"
elif [[ $runMode == 'preview' ]]; then
runModeDesc="\\u9884\\u89C8\\u7248\\u672C"
else
runModeDesc="\\u5F00\\u53D1\\u7248\\u672C"
fi

Date=$(git log -1 --format=%cd --date=format:'%Y-%m-%d %H:%M:%S%z' | sed "s/\([+-]\)\([0-9][0-9]\)\([0-9][0-9]\)/\1\2:\3/")
buildId=$(git rev-parse --short HEAD)

mkdir -p zrlog-web/src/main/resources/
cat <<EOF > zrlog-web/src/main/resources/build.properties
version=${version}
runMode=${runMode}
runModeDesc=${runModeDesc}
buildId=${buildId}
buildTime=${Date}
mirrorWebSite=${mirrorWebSite}
EOF

bash bin/build-system-info.sh