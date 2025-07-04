#!/usr/bin/env bash
version=$(printf 'VER\t${project.version}' | ./mvnw help:evaluate | grep '^VER' | cut -f2)
echo "current "$PWD
PWD=`pwd`
mirrorWebSite=https://dl.zrlog.com/
runMode=${1}
runModeDesc=${2}
Date=$(git log -1 --format=%cd --date=format:'%Y-%m-%d %H:%M:%S%z' | sed "s/\([+-]\)\([0-9][0-9]\)\([0-9][0-9]\)/\1\2:\3/")
buildId=$(git rev-parse --short HEAD)
mkdir -p data/src/main/resources
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
echo -e "version=${version}\nrunMode=${runMode}\nbuildId=${buildId}\nbuildTime=${Date}\nmirrorWebSite=${mirrorWebSite}" > data/src/main/resources/build.properties
syncPath=${3}
#finnally workPath, https://dl.zrlog.com mirror folder
mkdir -p ${syncPath}/${runMode}
bash -e bin/package-native-${packageExt}.sh
if [[ "${OS}" == "linux" && "${packageExt}" == "zip" ]]; then
  bash -e bin/package-lambda-${packageExt}.sh
#copy lambda
  cp target/zrlog-${version}-lambda.${packageExt} ${syncPath}/${runMode}/zrlog-${version}-${buildId}-${runMode}-${fileArch}-lambda.${packageExt}
  cp target/zrlog-${version}-lambda.${packageExt} ${syncPath}/${runMode}/zrlog-${fileArch}-lambda.${packageExt}
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
