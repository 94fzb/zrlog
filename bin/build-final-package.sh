#!/usr/bin/env bash
#!/bin/bash
version=$(printf 'VER\t${project.version}' | ./mvnw help:evaluate | grep '^VER' | cut -f2)
echo "current "$PWD
PWD=`pwd`
runMode=${1}
runModeDesc=${2}
Date=$(date --rfc-3339="seconds")
length=7
commitId=$(git log --format="%H" -n 1)
buildId=$(expr substr ${commitId} 1 ${length})
mkdir -p data/src/main/resources
echo -e "version=${version}\nrunMode=${runMode}\nbuildId=${buildId}\nbuildTime=${Date}" > data/src/main/resources/build.properties
bash -e bin/package-zip.sh
mv zrlog-${version}.zip  zrlog.zip
#finnally workPath, https://dl.zrlog.com mirror folder
syncPath=${3}
mirrorWebSite=https://dl.zrlog.com/
mkdir -p ${syncPath}/${runMode}
zipFileName=${runMode}/zrlog-${version}-${buildId}-${runMode}.zip
zipFinalFileName=${syncPath}/${runMode}/zrlog.zip
cp zrlog.zip ${zipFinalFileName}
cp zrlog.zip ${syncPath}/${zipFileName}
zipFileSize=$(ls ${zipFinalFileName} -ls | awk '{print $6}')
zipMd5sum=$(md5sum ${zipFinalFileName} | awk '{print $1}')
echo -e '{"zipMd5sum":"'${zipMd5sum}'","zipDownloadUrl":"'${mirrorWebSite}${zipFileName}'","type":"'${runModeDesc}'","version":"'${version}'","buildId":"'${buildId}'","zipFileSize":'${zipFileSize}',"releaseDate":"'${Date}'"}' > ${syncPath}/${runMode}/last.version.json
