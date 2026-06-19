#!/usr/bin/env bash
set -e

syncPath=${2}
: "${syncPath:?sync path is required}"

echo "current ${PWD}"
source shell/lib/release-manifest.sh
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

mirrorWebSite=$(releaseManifestBuildProp 'mirrorWebSite')
version=$(releaseManifestBuildProp 'version')
runMode=$(releaseManifestBuildProp 'runMode')
Date=$(releaseManifestBuildProp 'buildTime')
buildId=$(releaseManifestBuildProp 'buildId')
runModeDesc=$(releaseManifestBuildProp 'runModeDesc')
fileArch=$(releaseManifestBuildProp 'fileArch')
updateVersionJsonFilename=$(releaseManifestBuildProp 'updateVersionJsonFilename')
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
function writeZipVersionJson() {
  local artifactFile=${1}
  local downloadPath=${2}
  local jsonFilename=${3}
  releaseManifestWriteZipVersionJson "${syncPath}/${runMode}/${jsonFilename}" \
    "${artifactFile}" "${downloadPath}" "${mirrorWebSite}" \
    "${runModeDesc}" "${version}" "${buildId}" "${Date}"
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
