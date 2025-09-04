#!/usr/bin/env bash
echo "current "$PWD
PWD=`pwd`
bash -e shell/java/package-java-zip.sh "${1}"
function buildProp() {
    grep "${1}" "zrlog-web/src/main/resources/build.properties"|awk -F ${1}'=' '{print $2}'
}

mirrorWebSite=$(buildProp 'mirrorWebSite')
version=$(buildProp 'version')
runMode=$(buildProp 'runMode')
Date=$(buildProp 'buildTime')
buildId=$(buildProp 'buildId')
runModeDesc=$(buildProp 'runModeDesc')
mv target/zrlog-${version}.zip  zrlog.zip
cp target/zrlog-"${version}".war zrlog.war
#zip zrlog.war -d WEB-INF/install.lock WEB-INF/db.properties
#finnally workPath
syncPath=${2}
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
echo -e '{"md5sum":"54db99172e53542a152c505f0c23a845","zipMd5sum":"'${zipMd5sum}'","warMd5sum":"'${md5sum}'","downloadUrl":"'${mirrorWebSite}release/javax-war/zrlog.war'","zipDownloadUrl":"'${mirrorWebSite}${zipFileName}'" ,"warDownloadUrl":"'${mirrorWebSite}${relativeFileName}'","type":"'${runModeDesc}'","version":"'${version}'","buildId":"'${buildId}'","fileSize":10794045,"warFileSize":'${fileSize}',"zipFileSize":'${zipFileSize}',"releaseDate":"'${Date}'"}' > ${syncPath}/${runMode}/last.version.json
