#!/usr/bin/env bash
set -euo pipefail
export LC_ALL=C

channel=${1:-}
platform=${2:-}
manifestUrl=${3:-}

case "${channel}" in
  preview|release) ;;
  *)
    echo "ZrLog channel must be preview or release" >&2
    exit 1
    ;;
esac
case "${platform}" in
  Linux-amd64|Linux-arm64|Windows-x86_64) ;;
  *)
    echo "Unsupported ZrLog Native platform" >&2
    exit 1
    ;;
esac

expectedSourceCommit=${ZRLOG_EXPECTED_SOURCE_COMMIT:-}
expectedVersion=${ZRLOG_EXPECTED_VERSION:-}
explicitUrl=${ZRLOG_NATIVE_ZIP_URL:-}
explicitSha256=${ZRLOG_NATIVE_ZIP_SHA256:-}
attempts=${ZRLOG_NATIVE_RESOLVE_ATTEMPTS:-1}
retryInterval=${ZRLOG_NATIVE_RESOLVE_RETRY_SECONDS:-5}

if [[ ! "${attempts}" =~ ^[1-9][0-9]*$ || ! "${retryInterval}" =~ ^[0-9]+$ ]]; then
  echo "Native package resolve attempts and retry interval must be integers" >&2
  exit 1
fi
if [[ -n "${expectedSourceCommit}" && ! "${expectedSourceCommit}" =~ ^[0-9a-f]{40}$ ]]; then
  echo "Expected source commit must be a full lowercase Git SHA" >&2
  exit 1
fi
if [[ -n "${expectedVersion}" && ! "${expectedVersion}" =~ ^[0-9A-Za-z][0-9A-Za-z._-]*$ ]]; then
  echo "Invalid expected ZrLog version" >&2
  exit 1
fi

isSafeToken() {
  local value=${1}
  [[ ${#value} -le 128 && "${value}" =~ ^[0-9A-Za-z][0-9A-Za-z._-]*$ ]]
}

validateManifestPackage() {
  local url=${1}
  local sha256=${2}
  local version=${3}
  local buildId=${4}
  local sourceCommit=${5}
  local expectedUrl

  if [[ ! "${sha256}" =~ ^[0-9a-fA-F]{64}$ ]]; then
    return 1
  fi
  if ! isSafeToken "${version}" || ! isSafeToken "${buildId}"; then
    return 1
  fi
  if [[ ! "${sourceCommit}" =~ ^[0-9a-f]{40}$ ]]; then
    return 1
  fi
  if [[ "${channel}" == release ]] \
      && { [[ ! "${buildId}" =~ ^[0-9a-f]{7,40}$ ]] || [[ "${sourceCommit}" != "${buildId}"* ]]; }; then
    return 1
  fi
  expectedUrl="https://dl.zrlog.com/${channel}/zrlog-${version}-${buildId}-${channel}-${platform}.zip"
  [[ "${url}" == "${expectedUrl}" ]]
}

validateExplicitPackage() {
  local url=${1}
  local sha256=${2}
  local version=${3}
  local prefix
  local suffix="-${channel}-${platform}.zip"
  local buildId

  if [[ ! "${sha256}" =~ ^[0-9a-fA-F]{64}$ ]] || ! isSafeToken "${version}"; then
    return 1
  fi
  prefix="https://dl.zrlog.com/${channel}/zrlog-${version}-"
  if [[ "${url}" != "${prefix}"* || "${url}" != *"${suffix}" ]]; then
    return 1
  fi
  buildId=${url#"${prefix}"}
  buildId=${buildId%"${suffix}"}
  isSafeToken "${buildId}" && [[ "${url}" == "${prefix}${buildId}${suffix}" ]]
}

emitPackage() {
  local url=${1}
  local sha256=${2,,}
  if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
    printf 'url=%s\nsha256=%s\n' "${url}" "${sha256}" >> "${GITHUB_OUTPUT}"
  else
    printf 'url=%s\nsha256=%s\n' "${url}" "${sha256}"
  fi
}

if [[ -n "${explicitUrl}" || -n "${explicitSha256}" ]]; then
  if [[ -z "${explicitUrl}" || -z "${explicitSha256}" ]]; then
    echo "ZRLOG_NATIVE_ZIP_URL and ZRLOG_NATIVE_ZIP_SHA256 must be provided together" >&2
    exit 1
  fi
  if [[ -n "${expectedSourceCommit}" ]]; then
    echo "Expected source commit can only be verified through a Native package manifest" >&2
    exit 1
  fi
  if [[ -z "${expectedVersion}" ]]; then
    echo "ZRLOG_EXPECTED_VERSION is required with an explicit Native package URL" >&2
    exit 1
  fi
  if ! validateExplicitPackage "${explicitUrl}" "${explicitSha256}" "${expectedVersion}"; then
    echo "Explicit Native package URL or SHA-256 is invalid" >&2
    exit 1
  fi
  emitPackage "${explicitUrl}" "${explicitSha256}"
  exit 0
fi

if [[ -z "${manifestUrl}" ]]; then
  manifestUrl="https://dl.zrlog.com/${channel}/last.${platform}.version.json"
fi

for commandName in curl jq; do
  if ! command -v "${commandName}" >/dev/null 2>&1; then
    echo "${commandName} is required to resolve the Native package" >&2
    exit 1
  fi
done

manifestFile=$(mktemp)
trap 'rm -f "${manifestFile}"' EXIT

for ((attempt = 1; attempt <= attempts; attempt++)); do
  if curl --fail --silent --show-error --location \
      --retry 3 --retry-all-errors --connect-timeout 15 --max-time 60 \
      --output "${manifestFile}" -- "${manifestUrl}"; then
    packageUrl=$(jq -er '
      select(type == "object")
      | select(.checksumAlgorithm == "sha256")
      | .zipDownloadUrl
      | select(type == "string" and length > 0)
    ' "${manifestFile}" 2>/dev/null || true)
    packageSha256=$(jq -er '
      .zipSha256
      | select(type == "string" and test("^[0-9a-fA-F]{64}$"))
    ' "${manifestFile}" 2>/dev/null || true)
    sourceCommit=$(jq -er '.sourceCommit | select(type == "string")' "${manifestFile}" 2>/dev/null || true)
    version=$(jq -er '.version | select(type == "string")' "${manifestFile}" 2>/dev/null || true)
    buildId=$(jq -er '.buildId | select(type == "string")' "${manifestFile}" 2>/dev/null || true)

    if validateManifestPackage "${packageUrl}" "${packageSha256}" "${version}" "${buildId}" "${sourceCommit}" \
        && [[ -z "${expectedSourceCommit}" || "${sourceCommit}" == "${expectedSourceCommit}" ]] \
        && [[ -z "${expectedVersion}" || "${version}" == "${expectedVersion}" ]]; then
      emitPackage "${packageUrl}" "${packageSha256}"
      exit 0
    fi
  fi

  if (( attempt < attempts )); then
    sleep "${retryInterval}"
  fi
done

echo "Native package manifest did not match channel, platform, source commit, version, and SHA-256" >&2
exit 1
