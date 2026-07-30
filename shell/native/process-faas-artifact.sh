#!/usr/bin/env bash
set -euo pipefail

artifactFile=${1:-}
: "${artifactFile:?artifact file is required}"
: "${ARTIFACT_SERVICE_URL:?ARTIFACT_SERVICE_URL is required}"
: "${ARTIFACT_SERVICE_TOKEN:?ARTIFACT_SERVICE_TOKEN is required}"

for commandName in curl jq sha256sum; do
  if ! command -v "${commandName}" >/dev/null 2>&1; then
    echo "${commandName} is required to process the FaaS artifact" >&2
    exit 1
  fi
done

if [[ ! -f "${artifactFile}" ]]; then
  echo "FaaS artifact does not exist: ${artifactFile}" >&2
  exit 1
fi

source shell/lib/release-manifest.sh
version=$(releaseManifestBuildProp 'version')
fileArch=$(releaseManifestBuildProp 'fileArch')
artifactName=$(basename "${artifactFile}")

for metadata in version fileArch artifactName; do
  value=${!metadata}
  if [[ -z "${value}" || ! "${value}" =~ ^[A-Za-z0-9._-]+$ ]]; then
    echo "Invalid ${metadata} for FaaS artifact processing: ${value}" >&2
    exit 1
  fi
done

serviceUrl=${ARTIFACT_SERVICE_URL%/}
pollInterval=${ARTIFACT_POLL_INTERVAL_SECONDS:-5}
processTimeout=${ARTIFACT_PROCESS_TIMEOUT_SECONDS:-900}

if [[ ! "${pollInterval}" =~ ^[1-9][0-9]*$ || ! "${processTimeout}" =~ ^[1-9][0-9]*$ ]]; then
  echo "Artifact poll interval and timeout must be positive integers" >&2
  exit 1
fi

authorizationHeader="Authorization: Bearer ${ARTIFACT_SERVICE_TOKEN}"
curlArgs=(
  --fail-with-body
  --silent
  --show-error
  --retry 3
  --retry-all-errors
  --connect-timeout 15
)

if ! jobResponse=$(curl "${curlArgs[@]}" --max-time 900 \
    -X POST \
    -H "${authorizationHeader}" \
    -H "Content-Type: application/octet-stream" \
    -H "Expect:" \
    -H "X-Artifact-Name: ${artifactName}" \
    -H "X-Artifact-Version: ${version}" \
    -H "X-Artifact-Architecture: ${fileArch}" \
    --data-binary "@${artifactFile}" \
    "${serviceUrl}/api/v1/uploads"); then
  echo "Unable to upload artifact for processing: ${jobResponse:-no response body}" >&2
  exit 1
fi
jobId=$(jq -er 'select(.success == true) | .data.id
    | select(type == "string" and length > 0)' <<< "${jobResponse}") || {
  echo "Artifact upload response is invalid: ${jobResponse}" >&2
  exit 1
}
encodedJobId=$(jq -nr --arg value "${jobId}" '$value | @uri')

deadline=$((SECONDS + processTimeout))
while (( SECONDS < deadline )); do
  if ! jobResponse=$(curl "${curlArgs[@]}" --max-time 60 \
      -H "${authorizationHeader}" \
      "${serviceUrl}/api/v1/jobs/detail?id=${encodedJobId}"); then
    echo "Unable to query artifact processing job: ${jobResponse:-no response body}" >&2
    exit 1
  fi
  jobStatus=$(jq -er 'select(.success == true) | .data.status
      | select(type == "string")' <<< "${jobResponse}") || {
    echo "Artifact job status response is invalid: ${jobResponse}" >&2
    exit 1
  }

  case "${jobStatus}" in
    SUCCEEDED)
      break
      ;;
    FAILED)
      errorMessage=$(jq -r '.data.errorMessage // "unknown processing error"' \
        <<< "${jobResponse}")
      echo "Artifact processing job failed: ${errorMessage}" >&2
      exit 1
      ;;
    PENDING|PROCESSING)
      sleep "${pollInterval}"
      ;;
    *)
      echo "Artifact processing job returned unknown status: ${jobStatus}" >&2
      exit 1
      ;;
  esac
done

if [[ "${jobStatus:-}" != "SUCCEEDED" ]]; then
  echo "Artifact processing job timed out after ${processTimeout} seconds" >&2
  exit 1
fi

downloadPath=$(jq -er '.data.downloadPath
    | select(type == "string" and startswith("/api/v1/jobs/result?id="))' \
    <<< "${jobResponse}") || {
  echo "Succeeded artifact job has no download path: ${jobResponse}" >&2
  exit 1
}
expectedSha256=$(jq -er '.data.sha256
    | select(type == "string" and length == 64)' <<< "${jobResponse}") || {
  echo "Succeeded artifact job has no checksum: ${jobResponse}" >&2
  exit 1
}
expectedSize=$(jq -er '.data.sizeBytes
    | select(type == "number" and . > 0)' <<< "${jobResponse}") || {
  echo "Succeeded artifact job has no output size: ${jobResponse}" >&2
  exit 1
}

artifactDirectory=$(dirname "${artifactFile}")
processedFile=$(mktemp "${artifactDirectory}/.faas-artifact.XXXXXX")
trap 'rm -f "${processedFile}"' EXIT
if ! curl "${curlArgs[@]}" --max-time 900 \
    -H "${authorizationHeader}" \
    "${serviceUrl}${downloadPath}" \
    --output "${processedFile}"; then
  echo "Unable to download processed artifact" >&2
  exit 1
fi

actualSha256=$(sha256sum "${processedFile}" | awk '{print $1}')
actualSize=$(wc -c < "${processedFile}" | awk '{print $1}')
if [[ "${actualSha256}" != "${expectedSha256}" ]]; then
  echo "Processed artifact checksum mismatch" >&2
  exit 1
fi
if [[ "${actualSize}" != "${expectedSize}" ]]; then
  echo "Processed artifact size mismatch" >&2
  exit 1
fi

chmod +x "${processedFile}"
mv "${processedFile}" "${artifactFile}"
trap - EXIT

if ! cleanupResponse=$(curl "${curlArgs[@]}" --max-time 60 \
    -X DELETE -H "${authorizationHeader}" \
    "${serviceUrl}${downloadPath}"); then
  echo "Warning: unable to remove temporary artifact result; server TTL cleanup will retry" >&2
elif ! jq -e 'select(.success == true and .data.deleted == true)' \
    <<< "${cleanupResponse}" >/dev/null; then
  echo "Warning: artifact result cleanup response is invalid; server TTL cleanup will retry" >&2
fi

echo "Replaced ${artifactFile} with processed artifact from job ${jobId}"
