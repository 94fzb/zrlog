#!/usr/bin/env bash
set -euo pipefail

artifactFile=${1:-}
: "${artifactFile:?artifact file is required}"
: "${ARTIFACT_SERVICE_URL:?ARTIFACT_SERVICE_URL is required}"
: "${ARTIFACT_SERVICE_TOKEN:?ARTIFACT_SERVICE_TOKEN is required}"
: "${ARTIFACT_SOURCE_PREFIX:?ARTIFACT_SOURCE_PREFIX is required}"
: "${ARTIFACT_R2_ACCESS_KEY_ID:?ARTIFACT_R2_ACCESS_KEY_ID is required}"
: "${ARTIFACT_R2_SECRET_ACCESS_KEY:?ARTIFACT_R2_SECRET_ACCESS_KEY is required}"
: "${ARTIFACT_R2_BUCKET:?ARTIFACT_R2_BUCKET is required}"
: "${ARTIFACT_R2_ENDPOINT:?ARTIFACT_R2_ENDPOINT is required}"

for commandName in curl jq aws sha256sum; do
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
runMode=$(releaseManifestBuildProp 'runMode')
buildId=$(releaseManifestBuildProp 'buildId')
fileArch=$(releaseManifestBuildProp 'fileArch')
artifactName=$(basename "${artifactFile}")
sourcePrefix=${ARTIFACT_SOURCE_PREFIX%/}

for metadata in version runMode buildId fileArch artifactName; do
  value=${!metadata}
  if [[ -z "${value}" || ! "${value}" =~ ^[A-Za-z0-9._-]+$ ]]; then
    echo "Invalid ${metadata} for FaaS artifact path: ${value}" >&2
    exit 1
  fi
done
if [[ -z "${sourcePrefix}" || ! "${sourcePrefix}" =~ ^[A-Za-z0-9._/-]+$ \
      || "${sourcePrefix}" == /* || "${sourcePrefix}" == *..* ]]; then
  echo "ARTIFACT_SOURCE_PREFIX must be a normalized relative object prefix" >&2
  exit 1
fi

serviceUrl=${ARTIFACT_SERVICE_URL%/}
sourceKey="${sourcePrefix}/${runMode}/${artifactName}-${version}-${buildId}-${fileArch}.bin"
idempotencyHash=$(printf '%s' "${sourceKey}" | sha256sum | awk '{print $1}')
idempotencyKey="${artifactName}-faas-${idempotencyHash}"
pollInterval=${ARTIFACT_POLL_INTERVAL_SECONDS:-5}
processTimeout=${ARTIFACT_PROCESS_TIMEOUT_SECONDS:-900}

if [[ ! "${pollInterval}" =~ ^[1-9][0-9]*$ || ! "${processTimeout}" =~ ^[1-9][0-9]*$ ]]; then
  echo "Artifact poll interval and timeout must be positive integers" >&2
  exit 1
fi

authorizationHeader="Authorization: Bearer ${ARTIFACT_SERVICE_TOKEN}"
jsonHeader="Content-Type: application/json"
curlArgs=(
  --fail-with-body
  --silent
  --show-error
  --retry 3
  --retry-all-errors
  --connect-timeout 15
)

uploadRequest=$(jq -cn --arg sourceKey "${sourceKey}" '{sourceKey: $sourceKey}')
if ! uploadResponse=$(curl "${curlArgs[@]}" --max-time 60 \
    -X POST -H "${authorizationHeader}" -H "${jsonHeader}" \
    --data "${uploadRequest}" "${serviceUrl}/api/v1/uploads"); then
  echo "Unable to create artifact upload ticket: ${uploadResponse:-no response body}" >&2
  exit 1
fi
uploadUrl=$(jq -er 'select(.success == true) | .data.uploadUrl | select(type == "string" and length > 0)' \
  <<< "${uploadResponse}") || {
  echo "Artifact upload ticket response is invalid: ${uploadResponse}" >&2
  exit 1
}

if ! curl "${curlArgs[@]}" --max-time 600 -X PUT \
    -H "Content-Type: application/octet-stream" \
    --upload-file "${artifactFile}" "${uploadUrl}" >/dev/null; then
  echo "Unable to upload source artifact to R2" >&2
  exit 1
fi

jobRequest=$(jq -cn \
  --arg artifactName "${artifactName}" \
  --arg artifactVersion "${version}" \
  --arg architecture "${fileArch}" \
  --arg sourceKey "${sourceKey}" \
  '{
    artifactName: $artifactName,
    artifactVersion: $artifactVersion,
    architecture: $architecture,
    sourceKey: $sourceKey
  }')
if ! jobResponse=$(curl "${curlArgs[@]}" --max-time 60 \
    -X POST -H "${authorizationHeader}" -H "${jsonHeader}" \
    -H "Idempotency-Key: ${idempotencyKey}" \
    --data "${jobRequest}" "${serviceUrl}/api/v1/jobs"); then
  echo "Unable to create artifact processing job: ${jobResponse:-no response body}" >&2
  exit 1
fi
jobId=$(jq -er 'select(.success == true) | .data.id | select(type == "string" and length > 0)' \
  <<< "${jobResponse}") || {
  echo "Artifact job response is invalid: ${jobResponse}" >&2
  exit 1
}

encodedJobId=$(jq -nr --arg value "${jobId}" '$value | @uri')
initialJobStatus=$(jq -er 'select(.success == true) | .data.status | select(type == "string")' \
  <<< "${jobResponse}") || {
  echo "Artifact job response has no status: ${jobResponse}" >&2
  exit 1
}
if [[ "${initialJobStatus}" == "FAILED" ]]; then
  if ! retryResponse=$(curl "${curlArgs[@]}" --max-time 60 \
      -X POST -H "${authorizationHeader}" \
      "${serviceUrl}/api/v1/jobs/retry?id=${encodedJobId}"); then
    echo "Unable to retry failed artifact processing job: ${retryResponse:-no response body}" >&2
    exit 1
  fi
  jq -e 'select(.success == true and .data.status == "PENDING")' \
    <<< "${retryResponse}" >/dev/null || {
    echo "Artifact job retry response is invalid: ${retryResponse}" >&2
    exit 1
  }
fi

deadline=$((SECONDS + processTimeout))
while (( SECONDS < deadline )); do
  if ! jobResponse=$(curl "${curlArgs[@]}" --max-time 60 \
      -H "${authorizationHeader}" \
      "${serviceUrl}/api/v1/jobs/detail?id=${encodedJobId}"); then
    echo "Unable to query artifact processing job: ${jobResponse:-no response body}" >&2
    exit 1
  fi
  jobStatus=$(jq -er 'select(.success == true) | .data.status | select(type == "string")' \
    <<< "${jobResponse}") || {
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

targetKey=$(jq -er '.data.targetKey | select(type == "string" and length > 0)' \
  <<< "${jobResponse}") || {
  echo "Succeeded artifact job has no target key: ${jobResponse}" >&2
  exit 1
}
expectedSha256=$(jq -er '.data.sha256 | select(type == "string" and length > 0)' \
  <<< "${jobResponse}") || {
  echo "Succeeded artifact job has no checksum: ${jobResponse}" >&2
  exit 1
}
expectedSize=$(jq -er '.data.sizeBytes | select(type == "number" and . > 0)' \
  <<< "${jobResponse}") || {
  echo "Succeeded artifact job has no output size: ${jobResponse}" >&2
  exit 1
}

artifactDirectory=$(dirname "${artifactFile}")
processedFile=$(mktemp "${artifactDirectory}/.faas-artifact.XXXXXX")
trap 'rm -f "${processedFile}"' EXIT
if ! AWS_ACCESS_KEY_ID="${ARTIFACT_R2_ACCESS_KEY_ID}" \
    AWS_SECRET_ACCESS_KEY="${ARTIFACT_R2_SECRET_ACCESS_KEY}" \
    AWS_DEFAULT_REGION=auto \
    aws s3 cp "s3://${ARTIFACT_R2_BUCKET}/${targetKey}" "${processedFile}" \
      --only-show-errors --endpoint-url "${ARTIFACT_R2_ENDPOINT}"; then
  echo "Unable to download processed artifact from R2: ${targetKey}" >&2
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
echo "Replaced ${artifactFile} with processed artifact ${targetKey}"
