#!/usr/bin/env bash
set -e

syncPath=${2}
: "${syncPath:?sync path is required}"

echo "current ${PWD}"
bash -e shell/java/package-java-zip.sh "${1}"
source shell/lib/release-manifest.sh

mirrorWebSite=$(releaseManifestBuildProp 'mirrorWebSite')
version=$(releaseManifestBuildProp 'version')
runMode=$(releaseManifestBuildProp 'runMode')
Date=$(releaseManifestBuildProp 'buildTime')
buildId=$(releaseManifestBuildProp 'buildId')
runModeDesc=$(releaseManifestBuildProp 'runModeDesc')
updateVersionJsonFilename=$(releaseManifestBuildProp 'updateVersionJsonFilename')
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
releaseManifestWriteJavaVersionJson "${syncPath}/${runMode}/${updateVersionJsonFilename}" \
  "${mirrorWebSite}" "${zipFileName}" "${relativeFileName}" \
  "${runModeDesc}" "${version}" "${buildId}" "${Date}" \
  "${finalFileName}" "${zipFinalFileName}"
