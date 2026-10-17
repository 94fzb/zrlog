#!/usr/bin/env bash
# Consume real archived package images using current tooling without recompiling the application.
set -euo pipefail
metadata=$(find .producer-images -name native-image-input.json -print -quit)
index=$(find .producer-images -name build.json -print -quit)
[[ -n $metadata && -n $index ]]
source_commit=$(jq -er .head_sha producer-run.json)
version=$(jq -er .version "$metadata")
channel=$(jq -er .channel "$index")
[[ $channel == preview || $channel == release ]]
export ZRLOG_EXPECTED_SOURCE_COMMIT=$source_commit ZRLOG_EXPECTED_VERSION=$version ZRLOG_CHANNEL=$channel
# This subprocess validates the original producer identity. The verification run
# and tooling commit are recorded separately below, never as a new application build.
GITHUB_RUN_ID=$PRODUCER_RUN_ID python3 shell/docker/assemble-linux-images.py \
  --inputs .producer-images --output .producer-verification --platform linux/amd64 --platform linux/arm64
old_report=$(find .producer-images -name image-assembly.json -print -quit)
[[ -n $old_report ]]
planned=$(jq -cS '[.images[] | {role,digest}] | sort_by(.role)' .producer-verification/image-assembly.json)
[[ $(jq -cS '[.images[] | {role,digest}] | sort_by(.role)' "$old_report") == "$planned" ]]
for role in dockerhub aliyun; do
  archive=.producer-verification/linux-$role.oci.tar
  reference=localhost:5000/producer/$role:verified
  bash shell/docker/skopeo.sh copy --all --preserve-digests --dest-tls-verify=false \
    "oci-archive:$archive" "docker://$reference"
  bash shell/docker/skopeo.sh inspect --tls-verify=false --raw "docker://$reference" \
    > ".producer-verification/$role-index.json"
  actual=sha256:$(sha256sum ".producer-verification/$role-index.json" | cut -d ' ' -f 1)
  expected=$(jq -er --arg role "$role" '.images[] | select(.role == $role) | .digest' .producer-verification/image-assembly.json)
  [[ $actual == "$expected" ]]
done
(cd .producer-verification && sha256sum linux-*.oci.tar > verified-archive.sha256)
jq -n --arg producerRun "$PRODUCER_RUN_ID" --arg sourceCommit "$source_commit" \
  --arg channel "$channel" --arg toolingCommit "$(git rev-parse HEAD)" \
  --arg verificationWorkflow "$GITHUB_SERVER_URL/$GITHUB_REPOSITORY/actions/runs/$GITHUB_RUN_ID" \
  '{producerRun:$producerRun,sourceCommit:$sourceCommit,channel:$channel,toolingCommit:$toolingCommit,
    verificationWorkflow:$verificationWorkflow,isolatedCopiesMatchOriginalIndexes:true,
    applicationRecompiled:false,externalPublicationPerformed:false}' \
  > .producer-verification/verification.json
