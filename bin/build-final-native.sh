#!/usr/bin/env bash
#!/bin/bash
version=$(printf 'VER\t${project.version}' | ./mvnw help:evaluate | grep '^VER' | cut -f2)
echo "current "$PWD
PWD=`pwd`
mirrorWebSite=https://dl.zrlog.com/
runMode=${1}
runModeDesc=${2}
Date=$(git log -1 --format=%cd --date=format:'%Y-%m-%d %H:%M:%S%z' | sed "s/\([+-]\)\([0-9][0-9]\)\([0-9][0-9]\)/\1\2:\3/")
buildId=$(git rev-parse --short HEAD)
mkdir -p data/src/main/resources
packageExt="zip"
# 判断操作系统类型
if [[ "${4}" == "windows" ]]; then
    fileArch=Windows-$(uname -m)
elif [[ "${4}" == "ubuntu" ]]; then
    fileArch=$(uname -s)-$(dpkg --print-architecture)
elif [[ "${4}" == "deb" ]]; then
    fileArch=$(uname -s)-$(dpkg --print-architecture)
    packageExt="deb"
else
    fileArch=$(uname -s)-$(uname -m)
fi
echo -e "version=${version}\nrunMode=${runMode}\nbuildId=${buildId}\nbuildTime=${Date}\nmirrorWebSite=${mirrorWebSite}" > data/src/main/resources/build.properties
bash -e bin/package-native-${4}-${packageExt}.sh
mv zrlog-${version}-${fileArch}.${packageExt}  zrlog-${fileArch}.${packageExt}
#finnally workPath, https://dl.zrlog.com mirror folder
syncPath=${3}
mkdir -p ${syncPath}/${runMode}
zipFileName=${runMode}/zrlog-${version}-${buildId}-${runMode}-${fileArch}.${packageExt}
zipFinalFileName=${syncPath}/${runMode}/zrlog-${fileArch}.${packageExt}
cp zrlog-${fileArch}.${packageExt} ${zipFinalFileName}
cp zrlog-${fileArch}.${packageExt} ${syncPath}/${zipFileName}
zipFileSize=$(ls -ls ${zipFinalFileName} | awk '{print $6}')
zipMd5sum=$(md5sum ${zipFinalFileName} | awk '{print $1}')
if [[ "${packageExt}" == "zip" ]]; then
  echo -e '{"zipMd5sum":"'${zipMd5sum}'","zipDownloadUrl":"'${mirrorWebSite}${zipFileName}'","type":"'${runModeDesc}'","version":"'${version}'","buildId":"'${buildId}'","zipFileSize":'${zipFileSize}',"releaseDate":"'${Date}'"}' > ${syncPath}/${runMode}/last.${fileArch}.version.json
fi
