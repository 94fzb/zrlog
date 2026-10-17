#!/usr/bin/env bash
# Offline archive and recovery tests; no registry or Native compilation required.
set -euo pipefail
script_dir=$(cd "$(dirname "$0")" && pwd)
tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT
export PREVIEW_TEST_DIR="$tmp"
mkdir -p "$tmp/artifact/preview" "$tmp/artifact/evidence" "$tmp/bin" "$tmp/payload"
commit=abcdef1000000000000000000000000000000000
printf '#!/bin/sh\nprintf "Native fixture\\n"\n' > "$tmp/payload/zrlog"
(cd "$tmp/payload" && zip -q "$tmp/artifact/preview/zrlog-Linux-amd64.zip" zrlog)
archive="$tmp/artifact/preview/zrlog-Linux-amd64.zip"
native="$tmp/artifact/preview/last.Linux-amd64.version.json"
jq -n --arg commit "$commit" --arg sha "$(sha256sum "$archive" | cut -d ' ' -f 1)" \
  --argjson size "$(stat -c %s "$archive")" '{checksumAlgorithm:"sha256",zipSha256:$sha,
    zipFileSize:$size,type:"预览版本",version:"4.0.0-SNAPSHOT",buildId:"adminweb-1234567",
    releaseDate:"2026-10-12 13:00:00Z",sourceCommit:$commit,
    sourceRepository:"https://github.com/94fzb/zrlog",
    buildWorkflow:"https://github.com/94fzb/zrlog/actions/runs/123"}' > "$native"
cat > "$tmp/artifact/evidence/build.properties" <<EOF
version=4.0.0-SNAPSHOT
buildId=adminweb-1234567
sourceCommit=$commit
sourceRepository=https://github.com/94fzb/zrlog
buildWorkflow=https://github.com/94fzb/zrlog/actions/runs/123
buildTime=2026-10-12 13:00:00Z
mirrorWebSite=https://dl.zrlog.com/
dockerUpdateVersionJsonFilename=last.docker-image.version.json
fileArch=Linux-amd64
runtimeType=native
runMode=preview
packageType=zip
EOF
prepare() { bash "$script_dir/prepare-preview-package.sh" "$tmp/artifact" 123 "$tmp/prepared"; }
reject() {
  if "$@" > "$tmp/rejected.log" 2>&1; then
    echo "Unexpected success: $*" >&2; exit 1
  fi
}
prepare > /dev/null
cmp "$tmp/payload/zrlog" "$tmp/prepared/package/zrlog"
[[ -x "$tmp/prepared/package/zrlog" ]]
[[ $(bash "$script_dir/preview-image-tag.sh" "$tmp/prepared/native-image-input.json") == 4.0.0-SNAPSHOT-adminweb-1234567 ]]
cp "$native" "$tmp/original-native.json"
for change in '.sourceCommit="bad"' '.version="4.0.0"' '.buildId=.sourceCommit' \
  '.buildWorkflow="https://github.com/94fzb/zrlog/actions/runs/124"' \
  '.sourceRepository="https://github.com/other/repo"' '.checksumAlgorithm="md5"' \
  '.zipFileSize+=1' '.zipSha256="aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"' \
  '.type="正式版本"' '.releaseDate="bad"' '.buildId="adminweb-7654321"'; do
  jq "$change" "$tmp/original-native.json" > "$native"
  reject prepare
done
cp "$tmp/original-native.json" "$native"
cp "$tmp/artifact/evidence/build.properties" "$tmp/properties"
sed 's/fileArch=Linux-amd64/fileArch=Linux-arm64/' "$tmp/properties" > "$tmp/artifact/evidence/build.properties"
reject prepare
cp "$tmp/properties" "$tmp/artifact/evidence/build.properties"
cp "$archive" "$tmp/original-archive.zip"
printf corrupted >> "$archive"
reject prepare
cp "$tmp/original-archive.zip" "$archive"

# A valid ZIP without the expected executable must still fail after its hash matches.
printf no-executable > "$tmp/payload/readme"
(cd "$tmp/payload" && zip -q "$tmp/no-executable.zip" readme)
cp "$tmp/no-executable.zip" "$archive"
jq --arg sha "$(sha256sum "$archive" | cut -d ' ' -f 1)" --argjson size "$(stat -c %s "$archive")" \
  '.zipSha256=$sha | .zipFileSize=$size' "$tmp/original-native.json" > "$native"
reject prepare
cp "$tmp/original-archive.zip" "$archive"
cp "$tmp/original-native.json" "$native"
(cd "$tmp/artifact" && zip -qr "$tmp/artifact.zip" .)

# Run head differs from the producer commit for repository_dispatch; the trusted
# producer snapshot provides the source identity. Overall Docker failure is recoverable.
jq -n '{repository:{full_name:"94fzb/zrlog"},head_repository:{full_name:"94fzb/zrlog"},
  path:".github/workflows/native-build-preview-package-zip.yml",conclusion:"failure",
  head_sha:"bbbbbbb0000000000000000000000000000000000"}' > "$tmp/run.json"
jq -n '[{name:"ZrLog on ubuntu-22.04",conclusion:"success",run_attempt:1},
  {name:"Publish preview Docker image",conclusion:"failure",run_attempt:2},
  {name:"ZrLog on ubuntu-22.04",conclusion:"failure",run_attempt:3},
  {name:"build / native on ubuntu-22.04",conclusion:"success",run_attempt:4},
  {name:"build / java on ubuntu-22.04",conclusion:"success",run_attempt:5},
  {name:"native on ubuntu-22.04",conclusion:"success",run_attempt:6}]' > "$tmp/jobs.json"
jq -n '[{id:11,name:"native-preview-docker-1",expired:false},
  {id:33,name:"native-preview-docker-3",expired:false},
  {id:44,name:"native-preview-docker-4",expired:false},
  {id:55,name:"native-preview-docker-5",expired:false},
  {id:66,name:"native-preview-docker-6",expired:false},
  {id:99,name:"native-preview-docker-99",expired:false},
  {id:100,name:"unrelated-100",expired:false}]' > "$tmp/artifacts.json"
cat > "$tmp/bin/gh" <<'MOCK'
#!/usr/bin/env bash
set -euo pipefail
[[ $1 == api ]]
shift
[[ ${1:-} != --paginate ]] || shift
case $1 in
  repos/94fzb/zrlog/actions/runs/123) cat "$PREVIEW_TEST_DIR/run.json" ;;
  'repos/94fzb/zrlog/actions/runs/123/jobs?filter=all&per_page=100') jq -c '.[]' "$PREVIEW_TEST_DIR/jobs.json" ;;
  'repos/94fzb/zrlog/actions/runs/123/artifacts?per_page=100') jq -c '.[]' "$PREVIEW_TEST_DIR/artifacts.json" ;;
  repos/94fzb/zrlog/actions/artifacts/*/zip)
    printf '%s\n' "$1" > "$PREVIEW_TEST_DIR/selected"
    cat "$PREVIEW_TEST_DIR/artifact.zip" ;;
  *) echo "Unexpected API: $*" >&2; exit 1 ;;
esac
MOCK
chmod +x "$tmp/bin/gh"
export PATH="$tmp/bin:$PATH"
recover() { bash "$script_dir/download-preview-package.sh" 123 "$tmp/recovered" "${1:-}"; }
recover 44 > /dev/null
[[ $(cat "$tmp/selected") == repos/94fzb/zrlog/actions/artifacts/44/zip ]]
cmp "$tmp/payload/zrlog" "$tmp/recovered/package/zrlog"
# The consolidated entry remains recoverable with the exact Native job artifact.
jq '.path=".github/workflows/build-package.yml"' "$tmp/run.json" > "$tmp/run-new.json"
mv "$tmp/run-new.json" "$tmp/run.json"
recover 66 > /dev/null
[[ $(cat "$tmp/selected") == repos/94fzb/zrlog/actions/artifacts/66/zip ]]
# Re-run failed Docker jobs use the exact old artifact ID, not the new run_attempt.
recover 11 > /dev/null
[[ $(cat "$tmp/selected") == repos/94fzb/zrlog/actions/artifacts/11/zip ]]
reject recover 33
reject recover 55
reject recover 99
reject recover 100
reject recover 777
reject recover '11;echo injected'
cp "$tmp/artifacts.json" "$tmp/artifacts-original.json"
jq 'map(.expired=true)' "$tmp/artifacts-original.json" > "$tmp/artifacts.json"
reject recover 11
cp "$tmp/artifacts-original.json" "$tmp/artifacts.json"
cp "$tmp/run.json" "$tmp/run-original.json"
for change in '.path=".github/workflows/docker-release-publish.yml"' \
  '.repository.full_name="other/repo"' '.head_repository.full_name="other/repo"'; do
  jq "$change" "$tmp/run-original.json" > "$tmp/run.json"
  reject recover 11
done
cp "$tmp/run-original.json" "$tmp/run.json"
printf '[]\n' > "$tmp/jobs.json"
reject recover 11
reject recover
echo "Native preview archive and Docker recovery contract ok"
