#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
resolver="${SCRIPT_DIR}/resolve-native-package.sh"
fixtureDir=$(mktemp -d)
trap 'rm -rf "${fixtureDir}"' EXIT

sourceCommit=0123456789abcdef0123456789abcdef01234567
version=4.0.0
buildId=0123456
sha256=aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
packageUrl=https://dl.zrlog.com/release/zrlog-4.0.0-0123456-release-Linux-amd64.zip
manifest="${fixtureDir}/manifest.json"

fail() {
  echo "$1" >&2
  exit 1
}

expectFailure() {
  local description=$1
  shift
  if "$@" >/dev/null 2>&1; then
    fail "${description}"
  fi
}

writeManifest() {
  local output=${1}
  local url=${2}
  local manifestVersion=${3}
  local manifestBuildId=${4}
  local manifestSourceCommit=${5}
  local manifestSha256=${6:-${sha256}}
  local checksumAlgorithm=${7:-sha256}

  jq -n \
    --arg checksumAlgorithm "${checksumAlgorithm}" \
    --arg zipSha256 "${manifestSha256}" \
    --arg zipDownloadUrl "${url}" \
    --arg version "${manifestVersion}" \
    --arg buildId "${manifestBuildId}" \
    --arg sourceCommit "${manifestSourceCommit}" \
    '{
      checksumAlgorithm: $checksumAlgorithm,
      zipSha256: $zipSha256,
      zipDownloadUrl: $zipDownloadUrl,
      version: $version,
      buildId: $buildId,
      sourceCommit: $sourceCommit
    }' > "${output}"
}

writeManifest "${manifest}" "${packageUrl}" "${version}" "${buildId}" "${sourceCommit}"

# Exercise the default manifest URL without making a network request.
mkdir -p "${fixtureDir}/bin"
cat > "${fixtureDir}/bin/curl" <<'MOCK_CURL'
#!/usr/bin/env bash
set -euo pipefail

output=
url=
userAgent=
while (( $# > 0 )); do
  case "$1" in
    --output)
      output=$2
      shift 2
      ;;
    --user-agent|-A)
      userAgent=$2
      shift 2
      ;;
    --retry|--connect-timeout|--max-time)
      shift 2
      ;;
    --*)
      shift
      ;;
    *)
      url=$1
      shift
      ;;
  esac
done
[[ "${url}" == "${MOCK_EXPECTED_MANIFEST_URL}" ]]
[[ "${userAgent}" == 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36' ]]
cp "${MOCK_MANIFEST_FILE}" "${output}"
MOCK_CURL
chmod +x "${fixtureDir}/bin/curl"

resolved=$(env \
  PATH="${fixtureDir}/bin:${PATH}" \
  MOCK_EXPECTED_MANIFEST_URL=https://dl.zrlog.com/release/last.Linux-amd64.version.json \
  MOCK_MANIFEST_FILE="${manifest}" \
  ZRLOG_EXPECTED_SOURCE_COMMIT="${sourceCommit}" \
  ZRLOG_EXPECTED_VERSION="${version}" \
  "${resolver}" release Linux-amd64)
[[ "${resolved}" == $'url='"${packageUrl}"$'\nsha256='"${sha256}" ]] \
  || fail "Resolver did not return the expected default manifest package"

explicit=$(ZRLOG_NATIVE_ZIP_URL="${packageUrl}" \
  ZRLOG_NATIVE_ZIP_SHA256="${sha256^^}" \
  ZRLOG_EXPECTED_VERSION="${version}" \
  "${resolver}" release Linux-amd64)
[[ "${explicit}" == "${resolved}" ]] \
  || fail "Explicit Native package resolution did not match the manifest result"

githubOutput="${fixtureDir}/github-output"
: > "${githubOutput}"
githubStdout=$(GITHUB_OUTPUT="${githubOutput}" \
  ZRLOG_EXPECTED_SOURCE_COMMIT="${sourceCommit}" \
  ZRLOG_EXPECTED_VERSION="${version}" \
  "${resolver}" release Linux-amd64 "file://${manifest}")
[[ -z "${githubStdout}" ]] || fail "Resolver wrote package data to stdout in GitHub output mode"
mapfile -t githubOutputLines < "${githubOutput}"
[[ ${#githubOutputLines[@]} -eq 2 ]] || fail "Resolver wrote an unexpected number of GitHub outputs"
[[ "${githubOutputLines[0]}" == "url=${packageUrl}" ]] || fail "Resolver wrote an unexpected URL output"
[[ "${githubOutputLines[1]}" == "sha256=${sha256}" ]] || fail "Resolver wrote an unexpected SHA-256 output"

expectFailure "Resolver accepted a manifest from the wrong source commit" \
  env ZRLOG_EXPECTED_SOURCE_COMMIT=ffffffffffffffffffffffffffffffffffffffff \
  ZRLOG_EXPECTED_VERSION="${version}" \
  "${resolver}" release Linux-amd64 "file://${manifest}"

expectFailure "Resolver accepted a package for the wrong architecture" \
  env ZRLOG_EXPECTED_SOURCE_COMMIT="${sourceCommit}" \
  ZRLOG_EXPECTED_VERSION="${version}" \
  "${resolver}" release Linux-arm64 "file://${manifest}"

expectFailure "Resolver accepted an unsupported channel" \
  env ZRLOG_EXPECTED_VERSION="${version}" \
  "${resolver}" development Linux-amd64 "file://${manifest}"

expectFailure "Resolver accepted an unsupported platform" \
  env ZRLOG_EXPECTED_VERSION="${version}" \
  "${resolver}" release Darwin-arm64 "file://${manifest}"

wrongBuildManifest="${fixtureDir}/wrong-build.json"
writeManifest "${wrongBuildManifest}" "${packageUrl}" "${version}" other-build "${sourceCommit}"
expectFailure "Resolver accepted a URL that did not match the manifest build ID" \
  env ZRLOG_EXPECTED_SOURCE_COMMIT="${sourceCommit}" \
  ZRLOG_EXPECTED_VERSION="${version}" \
  "${resolver}" release Linux-amd64 "file://${wrongBuildManifest}"

unrelatedSourceCommit=f123456789abcdef0123456789abcdef01234567
unrelatedSourceManifest="${fixtureDir}/unrelated-source.json"
writeManifest "${unrelatedSourceManifest}" "${packageUrl}" "${version}" "${buildId}" "${unrelatedSourceCommit}"
expectFailure "Resolver accepted a release build ID unrelated to its source commit" \
  env ZRLOG_EXPECTED_VERSION="${version}" \
  "${resolver}" release Linux-amd64 "file://${unrelatedSourceManifest}"

previewBuildId=adminweb-7654321
previewVersion=4.0.0-SNAPSHOT
previewUrl=https://dl.zrlog.com/preview/zrlog-4.0.0-SNAPSHOT-adminweb-7654321-preview-Linux-amd64.zip
previewManifest="${fixtureDir}/preview.json"
writeManifest "${previewManifest}" "${previewUrl}" "${previewVersion}" "${previewBuildId}" "${sourceCommit}"
previewResolved=$(ZRLOG_EXPECTED_SOURCE_COMMIT="${sourceCommit}" \
  ZRLOG_EXPECTED_VERSION="${previewVersion}" \
  "${resolver}" preview Linux-amd64 "file://${previewManifest}")
[[ "${previewResolved}" == $'url='"${previewUrl}"$'\nsha256='"${sha256}" ]] \
  || fail "Resolver rejected a valid preview package with a dispatched build ID"

wrongVersionManifest="${fixtureDir}/wrong-version.json"
writeManifest "${wrongVersionManifest}" "${packageUrl}" 4.0.1 "${buildId}" "${sourceCommit}"
expectFailure "Resolver accepted a URL that did not match the manifest version" \
  env ZRLOG_EXPECTED_SOURCE_COMMIT="${sourceCommit}" \
  ZRLOG_EXPECTED_VERSION="${version}" \
  "${resolver}" release Linux-amd64 "file://${wrongVersionManifest}"

wrongChannelUrl=https://dl.zrlog.com/preview/zrlog-4.0.0-0123456-preview-Linux-amd64.zip
wrongChannelManifest="${fixtureDir}/wrong-channel.json"
writeManifest "${wrongChannelManifest}" "${wrongChannelUrl}" "${version}" "${buildId}" "${sourceCommit}"
expectFailure "Resolver accepted a package from the wrong channel" \
  env ZRLOG_EXPECTED_SOURCE_COMMIT="${sourceCommit}" \
  ZRLOG_EXPECTED_VERSION="${version}" \
  "${resolver}" release Linux-amd64 "file://${wrongChannelManifest}"

expectFailure "Resolver accepted a non-canonical artifact host" \
  env ZRLOG_NATIVE_ZIP_URL=https://example.com/release/zrlog-4.0.0-0123456-release-Linux-amd64.zip \
  ZRLOG_NATIVE_ZIP_SHA256="${sha256}" \
  ZRLOG_EXPECTED_VERSION="${version}" \
  "${resolver}" release Linux-amd64

expectFailure "Resolver accepted a URL without a SHA-256" \
  env ZRLOG_NATIVE_ZIP_URL="${packageUrl}" \
  ZRLOG_EXPECTED_VERSION="${version}" \
  "${resolver}" release Linux-amd64

expectFailure "Resolver accepted an explicit URL without an expected version" \
  env ZRLOG_NATIVE_ZIP_URL="${packageUrl}" \
  ZRLOG_NATIVE_ZIP_SHA256="${sha256}" \
  "${resolver}" release Linux-amd64

expectFailure "Resolver ignored a source commit requirement for an explicit URL" \
  env ZRLOG_NATIVE_ZIP_URL="${packageUrl}" \
  ZRLOG_NATIVE_ZIP_SHA256="${sha256}" \
  ZRLOG_EXPECTED_SOURCE_COMMIT="${sourceCommit}" \
  ZRLOG_EXPECTED_VERSION="${version}" \
  "${resolver}" release Linux-amd64

expectFailure "Resolver accepted a package for the wrong version" \
  env ZRLOG_NATIVE_ZIP_URL="${packageUrl}" \
  ZRLOG_NATIVE_ZIP_SHA256="${sha256}" \
  ZRLOG_EXPECTED_VERSION=4.0.1 \
  "${resolver}" release Linux-amd64

maliciousUrls=(
  "${packageUrl}"$'\ninjected_output=controlled'
  'https://dl.zrlog.com/release/zrlog-4.0.0-quote"value-release-Linux-amd64.zip'
  'https://dl.zrlog.com/release/zrlog-4.0.0-back`tick-release-Linux-amd64.zip'
  'https://dl.zrlog.com/release/zrlog-4.0.0-dollar$(whoami)-release-Linux-amd64.zip'
  $'https://dl.zrlog.com/release/zrlog-4.0.0-control\x1fvalue-release-Linux-amd64.zip'
  'https://dl.zrlog.com/release/zrlog-4.0.0-white space-release-Linux-amd64.zip'
  $'https://dl.zrlog.com/release/zrlog-4.0.0-nonascii-\xc3\xa9-release-Linux-amd64.zip'
)
for maliciousUrl in "${maliciousUrls[@]}"; do
  : > "${githubOutput}"
  expectFailure "Resolver accepted unsafe characters in an explicit package URL" \
    env GITHUB_OUTPUT="${githubOutput}" \
    ZRLOG_NATIVE_ZIP_URL="${maliciousUrl}" \
    ZRLOG_NATIVE_ZIP_SHA256="${sha256}" \
    ZRLOG_EXPECTED_VERSION="${version}" \
    "${resolver}" release Linux-amd64
  [[ ! -s "${githubOutput}" ]] || fail "Rejected package data modified GITHUB_OUTPUT"
done

injectedManifest="${fixtureDir}/injected-output.json"
writeManifest "${injectedManifest}" \
  "${packageUrl}"$'\ninjected_output=controlled' \
  "${version}" "${buildId}" "${sourceCommit}"
: > "${githubOutput}"
expectFailure "Resolver accepted output injection through a manifest URL" \
  env GITHUB_OUTPUT="${githubOutput}" \
  ZRLOG_EXPECTED_SOURCE_COMMIT="${sourceCommit}" \
  ZRLOG_EXPECTED_VERSION="${version}" \
  "${resolver}" release Linux-amd64 "file://${injectedManifest}"
[[ ! -s "${githubOutput}" ]] || fail "Rejected manifest data modified GITHUB_OUTPUT"

if grep -nH 'r2\.dev' \
    "${SCRIPT_DIR}/Dockerfile" \
    "${SCRIPT_DIR}/Dockerfile.windows" \
    "${SCRIPT_DIR}/../.github/workflows/docker-preview-publish.yml" \
    "${SCRIPT_DIR}/../.github/workflows/docker-release-publish.yml"; then
  fail "Docker build contract still references the non-canonical R2 hostname"
fi

grep -Fq 'sha256sum --check --strict' "${SCRIPT_DIR}/Dockerfile"
windowsDockerfile="${SCRIPT_DIR}/Dockerfile.windows"
grep -Fq 'Get-FileHash -LiteralPath $archive -Algorithm SHA256' "${windowsDockerfile}"
grep -Fq "ZRLOG_CHANNEL -cnotin @('preview', 'release')" "${windowsDockerfile}"
grep -Fq 'Expected source commit can only be verified through a Native package manifest' "${windowsDockerfile}"
grep -Fq 'ZRLOG_EXPECTED_VERSION is required with an explicit Native package URL' "${windowsDockerfile}"
grep -Fq "manifestVersion)) { throw 'Native package manifest version is invalid'" "${windowsDockerfile}"
grep -Fq "manifestBuildId)) { throw 'Native package manifest build ID is invalid'" "${windowsDockerfile}"
grep -Fq "manifestSourceCommit -cnotmatch '^[0-9a-f]{40}$'" "${windowsDockerfile}"
grep -Fq "manifestBuildId -cnotmatch '^[0-9a-f]{7,40}$'" "${windowsDockerfile}"
grep -Fq 'manifestSourceCommit.StartsWith($manifestBuildId, [StringComparison]::Ordinal)' "${windowsDockerfile}"
grep -Fq '[string]::Equals($artifactUrl, $expectedArtifactUrl, [StringComparison]::Ordinal)' "${windowsDockerfile}"
if grep -Fq "ZRLOG_CHANNEL -notmatch '^[A-Za-z0-9._-]+$'" "${windowsDockerfile}"; then
  fail "Windows Dockerfile still accepts unsupported Native package channels"
fi

for workflow in docker-preview-publish.yml docker-release-publish.yml; do
  workflowPath="${SCRIPT_DIR}/../.github/workflows/${workflow}"
  grep -Fq 'ZRLOG_EXPECTED_SOURCE_COMMIT:' "${workflowPath}"
  grep -Fq 'ZRLOG_EXPECTED_VERSION:' "${workflowPath}"
  grep -Fq 'ZRLOG_NATIVE_ZIP_URL=' "${workflowPath}"
  grep -Fq 'ZRLOG_NATIVE_ZIP_SHA256=' "${workflowPath}"
  if grep -A3 -F 'bash docker/resolve-native-package.sh' "${workflowPath}" | grep -Fq 'GITHUB_OUTPUT'; then
    fail "${workflow} redirects resolver stdout into GITHUB_OUTPUT"
  fi
  if grep -Fq -- '--build-arg "ZRLOG_NATIVE_ZIP_URL=${{' "${workflowPath}" \
      || grep -Fq -- '--build-arg "ZRLOG_NATIVE_ZIP_SHA256=${{' "${workflowPath}" \
      || grep -Fq -- '--build-arg "ZRLOG_EXPECTED_VERSION=${{' "${workflowPath}"; then
    fail "${workflow} interpolates Native package data into PowerShell source"
  fi
  grep -Fq -- '--build-arg "ZRLOG_NATIVE_ZIP_URL=$env:ZRLOG_NATIVE_ZIP_URL"' "${workflowPath}"
  grep -Fq -- '--build-arg "ZRLOG_NATIVE_ZIP_SHA256=$env:ZRLOG_NATIVE_ZIP_SHA256"' "${workflowPath}"
  grep -Fq -- '--build-arg "ZRLOG_EXPECTED_VERSION=$env:ZRLOG_EXPECTED_VERSION"' "${workflowPath}"
  grep -Fq 'version:$env:ZRLOG_EXPECTED_VERSION - ' "${workflowPath}"
done

grep -Fq 'zrlog version:${ZRLOG_EXPECTED_VERSION} - ' \
  "${SCRIPT_DIR}/../.github/workflows/docker-release-publish.yml"

echo "native Docker package contract ok"
