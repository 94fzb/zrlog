#!/usr/bin/env bash
set -e

function projectVersion() {
    if [[ -n "${ZRLOG_VERSION:-}" ]]; then
        printf '%s\n' "${ZRLOG_VERSION}"
        return
    fi
    awk '
        /<parent>/ { inParent = 1 }
        /<\/parent>/ { inParent = 0; next }
        !inParent && /<version>/ {
            line = $0
            sub(/.*<version>/, "", line)
            sub(/<\/version>.*/, "", line)
            print line
            exit
        }
    ' pom.xml
}

version=$(projectVersion)
: "${version:?project version is required}"
# https://dl.zrlog.com mirror folder
mirrorWebSite=https://dl.zrlog.com/
runMode=${1:-dev}
runtimeType="${ZRLOG_RUNTIME_TYPE:-}"
packageType="${ZRLOG_PACKAGE_TYPE:-}"
if [[ "${2}" == "java" || "${2}" == "native" ]]; then
runtimeType="${2}"
fi
if [[ "${3}" == "zip" || "${3}" == "war" || "${3}" == "deb" || "${3}" == "faas" ]]; then
packageType="${3}"
fi
runtimeType="${runtimeType:-java}"
packageType="${packageType:-zip}"
if [[ $runMode == 'release' ]]; then
runModeDesc="\\u6B63\\u5F0F\\u7248\\u672C"
elif [[ $runMode == 'preview' ]]; then
runModeDesc="\\u9884\\u89C8\\u7248\\u672C"
else
runModeDesc="\\u5F00\\u53D1\\u7248\\u672C"
fi

Date="${ZRLOG_BUILD_TIME:-$(git log -1 --format=%cd --date=format:'%Y-%m-%d %H:%M:%S%z' | sed "s/\([+-]\)\([0-9][0-9]\)\([0-9][0-9]\)/\1\2:\3/")}"
defaultBuildId="$(git rev-parse --short HEAD)"
buildId="${ZRLOG_BUILD_ID:-$defaultBuildId}"

OS="$(uname)"
case "${OS}" in
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
if [[ "${OS}" == "windows" ]]; then
    fileArch=Windows-$(uname -m)
elif [[ "${OS}" == "linux" ]]; then
    if command -v dpkg >/dev/null 2>&1; then
        linuxArch=$(dpkg --print-architecture)
    else
        linuxArch=$(uname -m)
    fi
    fileArch=$(uname -s)-${linuxArch}
else
    fileArch=$(uname -s)-$(uname -m)
fi

if [[ "${runtimeType}" == "native" ]]; then
    if [[ "${packageType}" == "faas" ]]; then
        updateVersionJsonFilename="last.${fileArch}.faas.version.json"
    elif [[ "${packageType}" == "deb" ]]; then
        updateVersionJsonFilename="last.${fileArch}.deb.version.json"
    else
        updateVersionJsonFilename="last.${fileArch}.version.json"
    fi
else
    updateVersionJsonFilename="last.version.json"
fi

mkdir -p zrlog-web/src/main/resources/
printf "version=%s\nrunMode=%s\nrunModeDesc=%s\nbuildId=%s\nbuildTime=%s\nmirrorWebSite=%s\nfileArch=%s\nruntimeType=%s\npackageType=%s\nupdateVersionJsonFilename=%s\n" \
  "$version" "$runMode" "$runModeDesc" "$buildId" "$Date" "$mirrorWebSite" "$fileArch" "$runtimeType" "$packageType" "$updateVersionJsonFilename" \
  > zrlog-web/src/main/resources/build.properties

bash bin/build-system-info.sh
