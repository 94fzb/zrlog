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
bash -e bin/package-native-${4}.sh
#finnally workPath, https://dl.zrlog.com mirror folder
syncPath=${3}
mkdir -p ${syncPath}/${runMode}
buildFile=target/zrlog-*.${packageExt}
zipFileName=${runMode}/zrlog-${version}-${buildId}-${runMode}-${fileArch}.${packageExt}
zipFinalFileName=${syncPath}/${runMode}/zrlog-${fileArch}.${packageExt}
cp ${buildFile} ${zipFinalFileName}
cp ${buildFile} ${syncPath}/${zipFileName}
zipFileSize=$(ls -ls ${zipFinalFileName} | awk '{print $6}')
zipMd5sum=$(md5sum ${zipFinalFileName} | awk '{print $1}')
if [[ "${packageExt}" == "zip" ]]; then
  echo -e '{"zipMd5sum":"'${zipMd5sum}'","zipDownloadUrl":"'${mirrorWebSite}${zipFileName}'","type":"'${runModeDesc}'","version":"'${version}'","buildId":"'${buildId}'","zipFileSize":'${zipFileSize}',"releaseDate":"'${Date}'"}' > ${syncPath}/${runMode}/last.${fileArch}.version.json
fi
