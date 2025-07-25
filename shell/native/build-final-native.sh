#!/usr/bin/env bash
echo "current "$PWD
PWD=`pwd`
function buildProp() {
    grep "${1}" "web/src/main/resources/build.properties"|awk -F ${1}'=' '{print $2}'
}

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
elif [[ "${4}" == "deb" ]]; then
    fileArch=$(uname -s)-$(dpkg --print-architecture)
    packageExt="deb"
elif [[ "${OS}" == "linux" ]]; then
    fileArch=$(uname -s)-$(dpkg --print-architecture)
else
    fileArch=$(uname -s)-$(uname -m)
fi
bash -e shell/native/package-native-${packageExt}.sh "${1}" "${2}"

mirrorWebSite=$(buildProp 'mirrorWebSite')
version=$(buildProp 'version')
runMode=$(buildProp 'runMode')
Date=$(buildProp 'buildTime')
buildId=$(buildProp 'buildId')
runModeDesc=$(buildProp 'runModeDesc')

syncPath=${3}
mkdir -p ${syncPath}/${runMode}
if [[ "${OS}" == "linux" && "${packageExt}" == "zip" ]]; then
  bash -e shell/native/package-faas-${packageExt}.sh
#copy faas
  cp target/zrlog-${version}-faas.${packageExt} ${syncPath}/${runMode}/zrlog-${version}-${buildId}-${runMode}-${fileArch}-faas.${packageExt}
  cp target/zrlog-${version}-faas.${packageExt} ${syncPath}/${runMode}/zrlog-${fileArch}-faas.${packageExt}
fi
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
