#!/usr/bin/env bash
# Use one OCI copier on every Linux runner, including Ubuntu 22.04.
set -euo pipefail
# quay.io/skopeo/stable:v1.18.0, multiarch index verified from Quay's tag API.
image=quay.io/skopeo/stable@sha256:c4c8a9d6fc95e331fa92fc31de3f6c9b5fe4761c82f0acb99669eef067fb7c33
arguments=(--rm --network host --user "$(id -u):$(id -g)" --volume "$PWD:$PWD" --workdir "$PWD")
config=${DOCKER_CONFIG:-$HOME/.docker}/config.json
if [[ -f $config ]]; then
  arguments+=(--volume "$config:/registry-auth.json:ro" --env REGISTRY_AUTH_FILE=/registry-auth.json)
fi
docker run "${arguments[@]}" "$image" "$@"
