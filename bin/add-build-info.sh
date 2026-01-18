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

OS="$(uname)"
case $OS in
  Linux)
    OS='linux'
    ;;
  Darwin)
    OS='mac'
    ;;
  *)
    OS='windows'
    ;;
esac
packageExt="zip"
# 判断操作系统类型
if [[ "${OS}" == "windows" ]]; then
    fileArch=Windows-$(uname -m)
elif [[ "${OS}" == "linux" ]]; then
    fileArch=$(uname -s)-$(dpkg --print-architecture)
else
    fileArch=$(uname -s)-$(uname -m)
fi

mkdir -p zrlog-web/src/main/resources/
printf "version=%s\nrunMode=%s\nrunModeDesc=%s\nbuildId=%s\nbuildTime=%s\nmirrorWebSite=%s\nfileArch=%s\n" \
  "$version" "$runMode" "$runModeDesc" "$buildId" "$Date" "$mirrorWebSite" "$fileArch" \
  > zrlog-web/src/main/resources/build.properties

bash bin/build-system-info.sh