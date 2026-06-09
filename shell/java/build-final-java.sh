#!/usr/bin/env bash
set -e

syncPath=${2}
: "${syncPath:?sync path is required}"

echo "current ${PWD}"
bash -e shell/java/package-java-zip.sh "${1}"
function buildProp() {
    sed -n -e "s/^${1}=//p" "zrlog-web/src/main/resources/build.properties"
}
function md5Of() {
  if command -v md5sum >/dev/null 2>&1; then
    md5sum "${1}" | awk '{ print $1 }'
  else
    md5 "${1}" | awk '{ print $NF }'
  fi
}

mirrorWebSite=$(buildProp 'mirrorWebSite')
version=$(buildProp 'version')
runMode=$(buildProp 'runMode')
Date=$(buildProp 'buildTime')
buildId=$(buildProp 'buildId')
runModeDesc=$(buildProp 'runModeDesc')
updateVersionJsonFilename=$(buildProp 'updateVersionJsonFilename')
updateVersionJsonFilename=${updateVersionJsonFilename:-last.version.json}
mv target/zrlog-${version}.zip  zrlog.zip
cp target/zrlog-"${version}".war zrlog.war
#zip zrlog.war -d WEB-INF/install.lock WEB-INF/db.properties
# final work path
mkdir -p "${syncPath}/${runMode}"
relativeFileName=${runMode}/zrlog-${version}-${buildId}-${runMode}.war
finalFileName=${syncPath}/${runMode}/zrlog.war
zipFileName=${runMode}/zrlog-${version}-${buildId}-${runMode}.zip
zipFinalFileName=${syncPath}/${runMode}/zrlog.zip
cp zrlog.zip "${zipFinalFileName}"
cp zrlog.zip "${syncPath}/${zipFileName}"
cp zrlog.war "${finalFileName}"
cp zrlog.war "${syncPath}/${relativeFileName}"
fileSize=$(ls -ls "${finalFileName}" | awk '{print $6}')
warMd5sum=$(md5Of "${finalFileName}")
zipFileSize=$(ls -ls "${zipFinalFileName}" | awk '{print $6}')
zipMd5sum=$(md5Of "${zipFinalFileName}")
printf '{"md5sum":"54db99172e53542a152c505f0c23a845","zipMd5sum":"%s","warMd5sum":"%s","downloadUrl":"%s","zipDownloadUrl":"%s","warDownloadUrl":"%s","type":"%s","version":"%s","buildId":"%s","fileSize":10794045,"warFileSize":%s,"zipFileSize":%s,"releaseDate":"%s"}\n' \
  "${zipMd5sum}" "${warMd5sum}" "${mirrorWebSite}release/javax-war/zrlog.war" "${mirrorWebSite}${zipFileName}" "${mirrorWebSite}${relativeFileName}" \
  "${runModeDesc}" "${version}" "${buildId}" "${fileSize}" "${zipFileSize}" "${Date}" \
  > "${syncPath}/${runMode}/${updateVersionJsonFilename}"
