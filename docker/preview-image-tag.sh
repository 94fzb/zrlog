#!/usr/bin/env bash
# Reuse the package buildId, including dependency-triggered IDs. Never derive it from sourceCommit.
set -euo pipefail
jq -er '
  if (.version | test("^[0-9]+\\.[0-9]+\\.[0-9]+-SNAPSHOT$")) and
     (.buildId | test("^[A-Za-z0-9][A-Za-z0-9._-]{0,127}$")) and
     (.buildId | test("^[0-9a-f]{40}$") | not)
  then .version + "-" + .buildId + "-preview"
  else error("Expected preview package identity and existing short buildId convention") end |
  if length <= 128 then . else error("Docker tag exceeds 128 characters") end
' "${1:?Native metadata required}"
