#!/usr/bin/env bash
set -euo pipefail

fail() {
  echo "$1" >&2
  exit 1
}

if (( $# != 1 )); then
  fail "Usage: $0 <java-package.zip|war>"
fi

archive=$1
[[ -f "${archive}" ]] || fail "Java package does not exist: ${archive}"

for requiredCommand in unzip jar java; do
  command -v "${requiredCommand}" >/dev/null \
    || fail "Required command is unavailable: ${requiredCommand}"
done

archiveDir=$(cd "$(dirname "${archive}")" && pwd)
workDir=$(mktemp -d "${archiveDir}/.java-package-contract.XXXXXX")
trap 'rm -rf "${workDir}"' EXIT

case "${archive}" in
  *.zip)
    libraryRoot=lib
    packageType=ZIP
    ;;
  *.war)
    libraryRoot=WEB-INF/lib
    packageType=WAR
    ;;
  *)
    fail "Unsupported Java package type: ${archive}"
    ;;
esac

mapfile -t libraryEntries < <(
  unzip -Z1 "${archive}" | grep -E "^${libraryRoot}/[^/]+\\.jar$" || true
)
(( ${#libraryEntries[@]} > 0 )) || fail "${packageType} contains no runtime libraries"

mapfile -t polyglotEntries < <(
  printf '%s\n' "${libraryEntries[@]}" \
    | grep -E "^${libraryRoot}/zrlog-polyglot-template-[^/]+\\.jar$" \
    || true
)
(( ${#polyglotEntries[@]} == 1 )) \
  || fail "Expected exactly one zrlog-polyglot-template JAR, found ${#polyglotEntries[@]}"

for graalArtifact in js-language truffle-runtime polyglot; do
  mapfile -t graalEntries < <(
    printf '%s\n' "${libraryEntries[@]}" \
      | grep -E "^${libraryRoot}/${graalArtifact}-[^/]+\\.jar$" \
      || true
  )
  (( ${#graalEntries[@]} == 1 )) \
    || fail "Expected exactly one ${graalArtifact} runtime JAR, found ${#graalEntries[@]}"
done

unzip -qq "${archive}" -d "${workDir}/package"

mapfile -t webEntries < <(
  printf '%s\n' "${libraryEntries[@]}" \
    | grep -E "^${libraryRoot}/zrlog-web-[^/]+\\.jar$" \
    || true
)
(( ${#webEntries[@]} == 1 )) \
  || fail "Expected exactly one zrlog-web JAR, found ${#webEntries[@]}"

webJar="${workDir}/package/${webEntries[0]}"
buildProperties=$(unzip -p "${webJar}" build.properties 2>/dev/null) \
  || fail "${webEntries[0]} contains no build.properties"

buildProperty() {
  local key=$1
  local values
  values=$(printf '%s\n' "${buildProperties}" | sed -n "s/^${key}=//p")
  [[ -n "${values}" ]] || fail "build.properties contains no ${key}"
  [[ $(printf '%s\n' "${values}" | wc -l) -eq 1 ]] \
    || fail "build.properties contains duplicate ${key} entries"
  printf '%s\n' "${values}"
}

metadataVersion=$(buildProperty version)
metadataBuildId=$(buildProperty buildId)
metadataBuildTime=$(buildProperty buildTime)
metadataRuntimeType=$(buildProperty runtimeType)
metadataPackageType=$(buildProperty packageType)
metadataSourceCommit=$(buildProperty sourceCommit)
metadataSourceRepository=$(buildProperty sourceRepository)

webJarName=${webEntries[0]##*/}
webJarVersion=${webJarName#zrlog-web-}
webJarVersion=${webJarVersion%.jar}
[[ "${metadataVersion}" == "${webJarVersion}" ]] \
  || fail "build.properties version ${metadataVersion} does not match ${webJarName}"
[[ "${metadataVersion}" != "1.0.0-SNAPSHOT" ]] \
  || fail "build.properties still uses the placeholder version"
[[ "${metadataBuildId}" != "0000000" ]] \
  || fail "build.properties still uses the placeholder buildId"
[[ "${metadataBuildId}" =~ ^[A-Za-z0-9._-]+$ ]] \
  || fail "build.properties contains an unsafe buildId: ${metadataBuildId}"
[[ "${metadataBuildTime}" =~ ^[0-9]{4}-[0-9]{2}-[0-9]{2}[[:space:]][0-9]{2}:[0-9]{2}:[0-9]{2}(Z|[+-][0-9]{2}:[0-9]{2})$ ]] \
  || fail "build.properties contains an invalid buildTime: ${metadataBuildTime}"
[[ "${metadataBuildTime}" != "2015-03-29 00:00:00+08:00" ]] \
  || fail "build.properties still uses the placeholder buildTime"
[[ "${metadataRuntimeType}" == "java" ]] \
  || fail "Expected java runtime metadata, found ${metadataRuntimeType}"
[[ "${metadataPackageType}" == "${packageType,,}" ]] \
  || fail "Expected ${packageType,,} package metadata, found ${metadataPackageType}"
[[ "${metadataSourceCommit}" =~ ^[0-9a-f]{40}$ ]] \
  || fail "build.properties contains an invalid sourceCommit: ${metadataSourceCommit}"
[[ -n "${metadataSourceRepository}" ]] \
  || fail "build.properties contains an empty sourceRepository"

expectedSourceCommit=${ZRLOG_SOURCE_COMMIT:-}
expectedBuildId=${ZRLOG_BUILD_ID:-}
if [[ -z "${expectedSourceCommit}" ]] && git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  expectedSourceCommit=$(git rev-parse HEAD)
fi
if [[ -z "${expectedBuildId}" ]] && git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  expectedBuildId=$(git rev-parse --short HEAD)
fi
if [[ -n "${expectedSourceCommit}" ]]; then
  [[ "${metadataSourceCommit}" == "${expectedSourceCommit}" ]] \
    || fail "build.properties sourceCommit does not match the candidate source"
fi
if [[ -n "${expectedBuildId}" ]]; then
  [[ "${metadataBuildId}" == "${expectedBuildId}" ]] \
    || fail "build.properties buildId does not match the candidate source"
elif [[ "${metadataBuildId}" =~ ^[0-9a-f]{7,40}$ ]]; then
  [[ "${metadataSourceCommit}" == "${metadataBuildId}"* ]] \
    || fail "build.properties buildId is not a prefix of sourceCommit"
fi

rendererClass='com/zrlog/blog/polyglot/markdown/MarkdownJsRenderer.class'
rendererJars=()
for entry in "${libraryEntries[@]}"; do
  if jar tf "${workDir}/package/${entry}" | grep -Fx "${rendererClass}" >/dev/null; then
    rendererJars+=("${entry}")
  fi
done

(( ${#rendererJars[@]} == 1 )) \
  || fail "Expected exactly one runtime JAR containing ${rendererClass}, found ${#rendererJars[@]}"
[[ "${rendererJars[0]}" == "${polyglotEntries[0]}" ]] \
  || fail "${rendererClass} is not provided by ${polyglotEntries[0]}"

if [[ "${packageType}" == ZIP ]]; then
  starterJar="${workDir}/package/zrlog-starter.jar"
  [[ -f "${starterJar}" ]] || fail "Java ZIP contains no zrlog-starter.jar"

  manifest=$(
    unzip -p "${starterJar}" META-INF/MANIFEST.MF \
      | tr -d '\r' \
      | sed -e ':join' -e 'N' -e '$!b join' -e 's/\n //g'
  )
  manifestClassPath=$(printf '%s\n' "${manifest}" | sed -n 's/^Class-Path: //p')
  [[ -n "${manifestClassPath}" ]] || fail "Starter manifest contains no Class-Path"
  for entry in "${libraryEntries[@]}"; do
    case " ${manifestClassPath} " in
      *" ${entry} "*) ;;
      *) fail "Starter manifest Class-Path omits ${entry}" ;;
    esac
  done
  for entry in ${manifestClassPath}; do
    case "${entry}" in
      lib/*.jar)
        [[ -f "${workDir}/package/${entry}" ]] \
          || fail "Starter manifest Class-Path references missing ${entry}"
        ;;
    esac
  done
fi

scriptDir=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
java -cp "${workDir}/package/${libraryRoot}/*" \
  "${scriptDir}/MarkdownRuntimeContract.java"

echo "Java ${packageType} contract ok: ${metadataVersion} ${metadataBuildId} ${metadataPackageType}; ${polyglotEntries[0]} provides MarkdownJsRenderer"
