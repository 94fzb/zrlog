#!/usr/bin/env bash
#!/bin/bash
version=$(printf 'VER\t${project.version}' | ./mvnw help:evaluate | grep '^VER' | cut -f2)
echo "current "$PWD
PWD=`pwd`
mirrorWebSite=https://dl.zrlog.com/
runMode=${1}
runModeDesc=${2}
Date=$(date +"%Y-%m-%d %H:%M:%S%z" | sed "s/\([+-]\)\([0-9][0-9]\)\([0-9][0-9]\)/\1\2:\3/")
buildId=$(git rev-parse --short HEAD)
mkdir -p data/src/main/resources
# 判断操作系统类型
if [[ "${4}" == "windows" ]]; then
    fileArch=Windows-$(uname -m)
elif [[ "${4}" == "ubuntu" ]]; then
    fileArch=$(uname -s)-$(dpkg --print-architecture)
else
    fileArch=$(uname -s)-$(uname -m)
fi
echo -e "version=${version}\nrunMode=${runMode}\nbuildId=${buildId}\nbuildTime=${Date}\nmirrorWebSite=${mirrorWebSite}" > data/src/main/resources/build.properties
bash -e bin/package-native-${4}-zip.sh
mv zrlog-${version}-${fileArch}.zip  zrlog-${fileArch}.zip
#finnally workPath, https://dl.zrlog.com mirror folder
syncPath=${3}
mkdir -p ${syncPath}/${runMode}
zipFileName=${runMode}/zrlog-${version}-${buildId}-${runMode}-${fileArch}.zip
zipFinalFileName=${syncPath}/${runMode}/zrlog-${fileArch}.zip
cp zrlog-${fileArch}.zip ${zipFinalFileName}
cp zrlog-${fileArch}.zip ${syncPath}/${zipFileName}
zipFileSize=$(ls -ls ${zipFinalFileName} | awk '{print $6}')
zipMd5sum=$(md5sum ${zipFinalFileName} | awk '{print $1}')
echo -e '{"zipMd5sum":"'${zipMd5sum}'","zipDownloadUrl":"'${mirrorWebSite}${zipFileName}'","type":"'${runModeDesc}'","version":"'${version}'","buildId":"'${buildId}'","zipFileSize":'${zipFileSize}',"releaseDate":"'${Date}'"}' > ${syncPath}/${runMode}/last.${fileArch}.version.json
