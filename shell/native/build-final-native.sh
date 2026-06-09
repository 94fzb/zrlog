#!/usr/bin/env bash
set -e

syncPath=${2}
: "${syncPath:?sync path is required}"

echo "current ${PWD}"
function buildProp() {
    sed -n -e "s/^${1}=//p" "zrlog-web/src/main/resources/build.properties"
}
buildSubType=${3:-zip}
packageExt="zip"
# 判断操作系统类型
if [[ ${buildSubType} == "deb" ]]; then
    packageExt="deb"
fi
if [[ "${buildSubType}" == "faas" ]]; then
ZRLOG_RUNTIME_TYPE=native ZRLOG_PACKAGE_TYPE="${buildSubType}" bash -e shell/native/package-native-${packageExt}.sh "${1}" "-Dmysql-scope=provided"
else
ZRLOG_RUNTIME_TYPE=native ZRLOG_PACKAGE_TYPE="${buildSubType}" bash -e shell/native/package-native-${packageExt}.sh "${1}"
fi

mirrorWebSite=$(buildProp 'mirrorWebSite')
version=$(buildProp 'version')
runMode=$(buildProp 'runMode')
Date=$(buildProp 'buildTime')
buildId=$(buildProp 'buildId')
runModeDesc=$(buildProp 'runModeDesc')
fileArch=$(buildProp 'fileArch')
updateVersionJsonFilename=$(buildProp 'updateVersionJsonFilename')
if [[ -z "${updateVersionJsonFilename}" ]]; then
  if [[ "${buildSubType}" == "faas" ]]; then
    updateVersionJsonFilename="last.${fileArch}.faas.version.json"
  elif [[ "${buildSubType}" == "deb" ]]; then
    updateVersionJsonFilename="last.${fileArch}.deb.version.json"
  else
    updateVersionJsonFilename="last.${fileArch}.version.json"
  fi
fi

mkdir -p "${syncPath}/${runMode}"
function md5Of() {
  if command -v md5sum >/dev/null 2>&1; then
    md5sum "${1}" | awk '{ print $1 }'
  else
    md5 "${1}" | awk '{ print $NF }'
  fi
}
function writeZipVersionJson() {
  local artifactFile=${1}
  local downloadPath=${2}
  local jsonFilename=${3}
  local zipFileSize
  local zipMd5sum
  zipFileSize=$(ls -ls "${artifactFile}" | awk '{print $6}')
  zipMd5sum=$(md5Of "${artifactFile}")
  printf '{"zipMd5sum":"%s","zipDownloadUrl":"%s","type":"%s","version":"%s","buildId":"%s","zipFileSize":%s,"releaseDate":"%s"}\n' \
    "${zipMd5sum}" "${mirrorWebSite}${downloadPath}" "${runModeDesc}" "${version}" "${buildId}" "${zipFileSize}" "${Date}" \
    > "${syncPath}/${runMode}/${jsonFilename}"
}
#faas
if [[ "$(uname)" == "Linux" && "${buildSubType}" == "faas" ]]; then
  bash -e shell/native/package-${buildSubType}-${packageExt}.sh "${fileArch}"
  zipFileName=${runMode}/zrlog-${version}-${buildId}-${runMode}-${fileArch}-${buildSubType}.${packageExt}
  zipFinalFileName=${syncPath}/${runMode}/zrlog-${fileArch}-${buildSubType}.${packageExt}
  buildFile=target/zrlog-${version}-${buildSubType}.${packageExt}
  cp "${buildFile}" "${syncPath}/${zipFileName}"
  cp "${buildFile}" "${zipFinalFileName}"
  writeZipVersionJson "${zipFinalFileName}" "${zipFileName}" "${updateVersionJsonFilename}"
else
  #
  buildFile=target/zrlog-${version}-native.${packageExt}
  zipFileName=${runMode}/zrlog-${version}-${buildId}-${runMode}-${fileArch}.${packageExt}
  zipFinalFileName=${syncPath}/${runMode}/zrlog-${fileArch}.${packageExt}
  cp "${buildFile}" "${zipFinalFileName}"
  cp "${buildFile}" "${syncPath}/${zipFileName}"
  if [[ "${packageExt}" == "zip" || "${packageExt}" == "deb" ]]; then
    writeZipVersionJson "${zipFinalFileName}" "${zipFileName}" "${updateVersionJsonFilename}"
  fi
fi
