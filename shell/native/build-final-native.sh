#!/usr/bin/env bash
set -e

syncPath=${2}
: "${syncPath:?sync path is required}"

echo "current ${PWD}"
source shell/lib/release-manifest.sh
buildSubType=${3:-zip}
# FaaS CI calls prepare, invokes the shared Action, then calls finish.
# The default all phase also supports a locally supplied processing client.
buildPhase=${4:-all}
case "${buildPhase}" in
  all) ;;
  prepare|finish)
    if [[ "$(uname)" != "Linux" || "${buildSubType}" != "faas" ]]; then
      echo "The ${buildPhase} phase requires a Linux FaaS build" >&2
      exit 1
    fi
    ;;
  *) echo "Unknown build phase: ${buildPhase}" >&2; exit 1 ;;
esac
if [[ "$(uname)" == "Linux" && "${buildSubType}" == "faas" && "${buildPhase}" == "all" ]]; then
  : "${ARTIFACT_PROCESS_SCRIPT:?Set ARTIFACT_PROCESS_SCRIPT to the pinned zrlog-process-artifact scripts/process-artifact.sh}"
  if [[ ! -f "${ARTIFACT_PROCESS_SCRIPT}" ]]; then
    echo "Artifact processing client does not exist: ${ARTIFACT_PROCESS_SCRIPT}" >&2
    exit 1
  fi
fi
packageExt="zip"
# 判断操作系统类型
if [[ ${buildSubType} == "deb" ]]; then
    packageExt="deb"
fi
if [[ "${buildPhase}" != "finish" ]]; then
  if [[ "${buildSubType}" == "faas" ]]; then
    ZRLOG_RUNTIME_TYPE=native ZRLOG_PACKAGE_TYPE="${buildSubType}" bash -e shell/native/package-native-${packageExt}.sh "${1}" "-Dmysql-scope=provided" "-Dzrlog.faas.native=true"
  else
    ZRLOG_RUNTIME_TYPE=native ZRLOG_PACKAGE_TYPE="${buildSubType}" bash -e shell/native/package-native-${packageExt}.sh "${1}"
  fi
fi

mirrorWebSite=$(releaseManifestBuildProp 'mirrorWebSite')
version=$(releaseManifestBuildProp 'version')
runMode=$(releaseManifestBuildProp 'runMode')
Date=$(releaseManifestBuildProp 'buildTime')
buildId=$(releaseManifestBuildProp 'buildId')
runModeDesc=$(releaseManifestBuildProp 'runModeDesc')
fileArch=$(releaseManifestBuildProp 'fileArch')
updateVersionJsonFilename=$(releaseManifestBuildProp 'updateVersionJsonFilename')
if [[ "${buildPhase}" != "all" ]]; then
  if [[ ! -s zrlog || ! "${version}" =~ ^[A-Za-z0-9._-]+$ || ! "${fileArch}" =~ ^Linux-[A-Za-z0-9._-]+$ \
      || "${runMode}" != "${1}" || "$(releaseManifestBuildProp 'runtimeType')" != "native" \
      || "$(releaseManifestBuildProp 'packageType')" != "faas" ]]; then
    echo "FaaS binary or build metadata does not match the requested build" >&2
    exit 1
  fi
fi
if [[ "${buildPhase}" == "prepare" ]]; then
  if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
    printf 'artifact-version=%s\nartifact-architecture=%s\n' "${version}" "${fileArch}" >> "${GITHUB_OUTPUT}"
  fi
  exit 0
fi
if [[ -z "${updateVersionJsonFilename}" ]]; then
  if [[ "${buildSubType}" == "faas" ]]; then
    updateVersionJsonFilename="last.${fileArch}.faas.version.json"
  elif [[ "${buildSubType}" == "deb" ]]; then
    updateVersionJsonFilename="last.${fileArch}.deb.version.json"
  else
    updateVersionJsonFilename="last.${fileArch}.version.json"
  fi
fi

mkdir -p "${syncPath}/${runMode}"
function writeZipVersionJson() {
  local artifactFile=${1}
  local downloadPath=${2}
  local jsonFilename=${3}
  releaseManifestWriteZipVersionJson "${syncPath}/${runMode}/${jsonFilename}" \
    "${artifactFile}" "${downloadPath}" "${mirrorWebSite}" \
    "${runModeDesc}" "${version}" "${buildId}" "${Date}"
}
#faas
if [[ "$(uname)" == "Linux" && "${buildSubType}" == "faas" ]]; then
  if [[ "${buildPhase}" == "all" ]]; then
    bash -e "${ARTIFACT_PROCESS_SCRIPT}" \
      zrlog zrlog "${version}" "${fileArch}"
  fi
  bash -e shell/native/package-${buildSubType}-${packageExt}.sh "${fileArch}"
  zipFileName=${runMode}/zrlog-${version}-${buildId}-${runMode}-${fileArch}-${buildSubType}.${packageExt}
  zipFinalFileName=${syncPath}/${runMode}/zrlog-${fileArch}-${buildSubType}.${packageExt}
  buildFile=target/zrlog-${version}-${buildSubType}.${packageExt}
  cp "${buildFile}" "${syncPath}/${zipFileName}"
  cp "${buildFile}" "${zipFinalFileName}"
  writeZipVersionJson "${zipFinalFileName}" "${zipFileName}" "${updateVersionJsonFilename}"
else
  #
  buildFile=target/zrlog-${version}-native.${packageExt}
  zipFileName=${runMode}/zrlog-${version}-${buildId}-${runMode}-${fileArch}.${packageExt}
  zipFinalFileName=${syncPath}/${runMode}/zrlog-${fileArch}.${packageExt}
  cp "${buildFile}" "${zipFinalFileName}"
  cp "${buildFile}" "${syncPath}/${zipFileName}"
  if [[ "${packageExt}" == "zip" || "${packageExt}" == "deb" ]]; then
    writeZipVersionJson "${zipFinalFileName}" "${zipFileName}" "${updateVersionJsonFilename}"
  fi
fi
