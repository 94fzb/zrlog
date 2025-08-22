#!/usr/bin/env bash
echo "current "$PWD
PWD=`pwd`
function buildProp() {
    grep "${1}" "web/src/main/resources/build.properties"|awk -F ${1}'=' '{print $2}'
}
buildSubType=${4}
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
elif [[ ${buildSubType} == "deb" ]]; then
    fileArch=$(uname -s)-$(dpkg --print-architecture)
    packageExt="deb"
elif [[ "${OS}" == "linux" ]]; then
    fileArch=$(uname -s)-$(dpkg --print-architecture)
else
    fileArch=$(uname -s)-$(uname -m)
fi
if [[ "${buildSubType}" == "faas" ]]; then
bash -e shell/native/package-native-${packageExt}.sh "${1}" "${2}" "-Dmysql-scope='provided'"
else
bash -e shell/native/package-native-${packageExt}.sh "${1}" "${2}"
fi

mirrorWebSite=$(buildProp 'mirrorWebSite')
version=$(buildProp 'version')
runMode=$(buildProp 'runMode')
Date=$(buildProp 'buildTime')
buildId=$(buildProp 'buildId')
runModeDesc=$(buildProp 'runModeDesc')

syncPath=${3}
mkdir -p ${syncPath}/${runMode}
#copy faas
if [[ "${OS}" == "linux" && "${buildSubType}" == "faas" ]]; then
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
  if [[ "${packageExt}" == "zip" && "${buildSubType}" != "faas" &&  "${buildSubType}" != "deb" ]]; then
    echo -e '{"zipMd5sum":"'${zipMd5sum}'","zipDownloadUrl":"'${mirrorWebSite}${zipFileName}'","type":"'${runModeDesc}'","version":"'${version}'","buildId":"'${buildId}'","zipFileSize":'${zipFileSize}',"releaseDate":"'${Date}'"}' > ${syncPath}/${runMode}/last.${fileArch}.version.json
  fi
fi