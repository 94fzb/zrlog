#!/usr/bin/env bash

# Keep deployment implementations out of unrelated runtime packages while retaining test coverage.
updaterSelectRuntime() {
  local mode=$1
  local module scope
  local selected
  case "$mode" in
    java-zip) selected=' zip docker ' ;;
    java-war) selected=' war ' ;;
    native-zip) selected=' native docker ' ;;
    native-faas) selected=' faas ' ;;
    native-deb) selected=' deb ' ;;
    *) printf 'Unsupported updater assembly: %s\n' "$mode" >&2; return 1 ;;
  esac
  zrlogUpdaterMavenArgs=()
  for module in zip native war faas docker deb; do
    scope=test
    [[ "$selected" != *" $module "* ]] || scope=runtime
    zrlogUpdaterMavenArgs+=("-Dzrlog-updater-$module-scope=$scope")
  done
}
