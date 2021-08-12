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
sh bin/package-zip.sh
mv target/zrlog-${version}.war zrlog.war
mv target/zrlog-${version}.zip  zrlog.zip
zip zrlog.war -d WEB-INF/install.lock WEB-INF/db.properties

#finnally workPath, https://dl.zrlog.com mirror folder
syncPath=${3}
mirrorWebSite=http://dl.zrlog.com/
mkdir -p ${syncPath}/${runMode}
relativeFileName=${runMode}/zrlog-${version}-${buildId}-${runMode}.war
zipFileName=${runMode}/zrlog-${version}-${buildId}-${runMode}.zip
finalFileName=${syncPath}/${runMode}/zrlog.war
zipFinalFileName=${syncPath}/${runMode}/zrlog.zip
cp zrlog.war ${finalFileName}
cp zrlog.war ${syncPath}/${relativeFileName}
cp zrlog.zip ${zipFinalFileName}
cp zrlog.zip ${syncPath}/${zipFileName}
fileSize=$(ls ${finalFileName} -ls | awk '{print $6}')
zipFileSize=$(ls ${zipFinalFileName} -ls | awk '{print $6}')
md5sum=$(md5sum ${finalFileName} | awk '{print $1}')
zipMd5sum=$(md5sum ${zipFinalFileName} | awk '{print $1}')
echo -e '{"zipMd5sum":"'${zipMd5sum}'","md5sum":"'${md5sum}'","zipDownloadUrl":"'${mirrorWebSite}${zipFileName}'" ,"downloadUrl":"'${mirrorWebSite}${relativeFileName}'","type":"'${runModeDesc}'","version":"'${version}'","buildId":"'${buildId}'","fileSize":'${fileSize}',"zipFileSize":'${zipFileSize}',"releaseDate":"'${Date}'"}' > ${syncPath}/${runMode}/last.version.json
