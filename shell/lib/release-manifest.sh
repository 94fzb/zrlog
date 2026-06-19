#!/usr/bin/env bash

function releaseManifestBuildProp() {
    sed -n -e "s/^${1}=//p" "zrlog-web/src/main/resources/build.properties"
}

function releaseManifestMd5Of() {
    if command -v md5sum >/dev/null 2>&1; then
        md5sum "${1}" | awk '{ print $1 }'
    else
        md5 "${1}" | awk '{ print $NF }'
    fi
}

function releaseManifestFileSizeOf() {
    wc -c < "${1}" | awk '{ print $1 }'
}

function releaseManifestWriteJavaVersionJson() {
    local outputFile=${1}
    local mirrorWebSite=${2}
    local zipDownloadPath=${3}
    local warDownloadPath=${4}
    local type=${5}
    local version=${6}
    local buildId=${7}
    local releaseDate=${8}
    local warFile=${9}
    local zipFile=${10}
    local warFileSize
    local zipFileSize
    local warMd5sum
    local zipMd5sum

    warFileSize=$(releaseManifestFileSizeOf "${warFile}")
    zipFileSize=$(releaseManifestFileSizeOf "${zipFile}")
    warMd5sum=$(releaseManifestMd5Of "${warFile}")
    zipMd5sum=$(releaseManifestMd5Of "${zipFile}")

    printf '{"md5sum":"54db99172e53542a152c505f0c23a845","zipMd5sum":"%s","warMd5sum":"%s","downloadUrl":"%s","zipDownloadUrl":"%s","warDownloadUrl":"%s","type":"%s","version":"%s","buildId":"%s","fileSize":10794045,"warFileSize":%s,"zipFileSize":%s,"releaseDate":"%s"}\n' \
        "${zipMd5sum}" "${warMd5sum}" "${mirrorWebSite}release/javax-war/zrlog.war" "${mirrorWebSite}${zipDownloadPath}" "${mirrorWebSite}${warDownloadPath}" \
        "${type}" "${version}" "${buildId}" "${warFileSize}" "${zipFileSize}" "${releaseDate}" \
        > "${outputFile}"
}

function releaseManifestWriteZipVersionJson() {
    local outputFile=${1}
    local artifactFile=${2}
    local downloadPath=${3}
    local mirrorWebSite=${4}
    local type=${5}
    local version=${6}
    local buildId=${7}
    local releaseDate=${8}
    local zipFileSize
    local zipMd5sum

    zipFileSize=$(releaseManifestFileSizeOf "${artifactFile}")
    zipMd5sum=$(releaseManifestMd5Of "${artifactFile}")

    printf '{"zipMd5sum":"%s","zipDownloadUrl":"%s","type":"%s","version":"%s","buildId":"%s","zipFileSize":%s,"releaseDate":"%s"}\n' \
        "${zipMd5sum}" "${mirrorWebSite}${downloadPath}" "${type}" "${version}" "${buildId}" "${zipFileSize}" "${releaseDate}" \
        > "${outputFile}"
}
