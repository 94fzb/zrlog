#!/usr/bin/env bash
version=$(printf 'VER\t${project.version}' | ./mvnw help:evaluate | grep '^VER' | cut -f2)
echo "current "$PWD
PWD=`pwd`
# https://dl.zrlog.com mirror folder
mirrorWebSite=https://dl.zrlog.com/
runMode=${1}
runModeDesc=${2}
Date=$(git log -1 --format=%cd --date=format:'%Y-%m-%d %H:%M:%S%z' | sed "s/\([+-]\)\([0-9][0-9]\)\([0-9][0-9]\)/\1\2:\3/")
buildId=$(git rev-parse --short HEAD)
mkdir -p data/src/main/resources
echo -e "version=${version}\nrunMode=${runMode}\nbuildId=${buildId}\nbuildTime=${Date}\nmirrorWebSite=${mirrorWebSite}" > data/src/main/resources/build.properties
bash -e bin/package-zip.sh
mv zrlog-${version}.zip  zrlog.zip
cp target/zrlog-"${version}".war zrlog.war
#zip zrlog.war -d WEB-INF/install.lock WEB-INF/db.properties
#finnally workPath
syncPath=${3}
mkdir -p ${syncPath}/${runMode}
relativeFileName=${runMode}/zrlog-${version}-${buildId}-${runMode}.war
finalFileName=${syncPath}/${runMode}/zrlog.war
zipFileName=${runMode}/zrlog-${version}-${buildId}-${runMode}.zip
zipFinalFileName=${syncPath}/${runMode}/zrlog.zip
cp zrlog.zip ${zipFinalFileName}
cp zrlog.zip ${syncPath}/${zipFileName}
cp zrlog.war ${finalFileName}
cp zrlog.war ${syncPath}/${relativeFileName}
fileSize=$(ls ${finalFileName} -ls | awk '{print $6}')
md5sum=$(md5sum ${finalFileName} | awk '{print $1}')
zipFileSize=$(ls ${zipFinalFileName} -ls | awk '{print $6}')
zipMd5sum=$(md5sum ${zipFinalFileName} | awk '{print $1}')
echo -e '{"zipMd5sum":"'${zipMd5sum}'","md5sum":"'${md5sum}'","zipDownloadUrl":"'${mirrorWebSite}${zipFileName}'" ,"downloadUrl":"'${mirrorWebSite}${relativeFileName}'","type":"'${runModeDesc}'","version":"'${version}'","buildId":"'${buildId}'","fileSize":'${fileSize}',"zipFileSize":'${zipFileSize}',"releaseDate":"'${Date}'"}' > ${syncPath}/${runMode}/last.version.json
