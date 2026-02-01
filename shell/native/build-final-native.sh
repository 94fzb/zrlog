#!/usr/bin/env bash
echo "current "$PWD
PWD=`pwd`
function buildProp() {
    grep "${1}" "zrlog-web/src/main/resources/build.properties"|awk -F ${1}'=' '{print $2}'
}
buildSubType=${3}
packageExt="zip"
# 判断操作系统类型
if [[ ${buildSubType} == "deb" ]]; then
    packageExt="deb"
fi
if [[ "${buildSubType}" == "faas" ]]; then
bash -e shell/native/package-native-${packageExt}.sh "${1}" "-Dmysql-scope='provided'"
else
bash -e shell/native/package-native-${packageExt}.sh "${1}"
fi

mirrorWebSite=$(buildProp 'mirrorWebSite')
version=$(buildProp 'version')
runMode=$(buildProp 'runMode')
Date=$(buildProp 'buildTime')
buildId=$(buildProp 'buildId')
runModeDesc=$(buildProp 'runModeDesc')
fileArch=$(buildProp 'fileArch')

syncPath=${2}
mkdir -p ${syncPath}/${runMode}
#faas
if [[ "$(uname)" == "Linux" && "${buildSubType}" == "faas" ]]; then
  bash -e shell/native/package-${buildSubType}-${packageExt}.sh ${fileArch}
  cp target/zrlog-${version}-${buildSubType}.${packageExt} ${syncPath}/${runMode}/zrlog-${version}-${buildId}-${runMode}-${fileArch}-${buildSubType}.${packageExt}
  cp target/zrlog-${version}-${buildSubType}.${packageExt} ${syncPath}/${runMode}/zrlog-${fileArch}-${buildSubType}.${packageExt}
else
  #
  buildFile=target/zrlog-${version}-native.${packageExt}
  zipFileName=${runMode}/zrlog-${version}-${buildId}-${runMode}-${fileArch}.${packageExt}
  zipFinalFileName=${syncPath}/${runMode}/zrlog-${fileArch}.${packageExt}
  cp ${buildFile} ${zipFinalFileName}
  cp ${buildFile} ${syncPath}/${zipFileName}
  zipFileSize=$(ls -ls ${zipFinalFileName} | awk '{print $6}')
  if command -v md5sum >/dev/null 2>&1; then
  zipMd5sum=$(md5sum ${zipFinalFileName} | awk '{ print $1 }')
  else
  zipMd5sum=$(md5 ${zipFinalFileName} | awk '{ print $NF }')
  fi
  if [[ "${packageExt}" == "zip" ]]; then
    echo -e '{"zipMd5sum":"'${zipMd5sum}'","zipDownloadUrl":"'${mirrorWebSite}${zipFileName}'","type":"'${runModeDesc}'","version":"'${version}'","buildId":"'${buildId}'","zipFileSize":'${zipFileSize}',"releaseDate":"'${Date}'"}' > ${syncPath}/${runMode}/last.${fileArch}.version.json
  fi
fi