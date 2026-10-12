#!/usr/bin/env bash
# Offline publication contract: identity, per-registry digests, CAS, and immutable readback.
set -euo pipefail
script_dir=$(cd "$(dirname "$0")" && pwd)
tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT
commit=abcdef1000000000000000000000000000000000
digest=sha256:1111111111111111111111111111111111111111111111111111111111111111
jq -n --arg commit "$commit" '{version:"4.0.1",buildId:"abcdef1",sourceCommit:$commit,
  releaseDate:"2026-10-11 13:00:00Z",type:"release"}' > "$tmp/native.json"
jq --arg digest "$digest" '. + {repository:"zrlog/zrlog",tag:"4.0.1",digest:$digest,
  platforms:["linux/amd64","linux/arm64"]}' "$tmp/native.json" > "$tmp/hub.json"
jq '.repository="registry.cn-chengdu.aliyuncs.com/hibegin/zrlog" |
  .digest="sha256:2222222222222222222222222222222222222222222222222222222222222222"' "$tmp/hub.json" > "$tmp/mirror.json"
generate() {
  bash "$script_dir/write-image-manifest.sh" --native-metadata "$tmp/native.json" \
    --channel "${CHANNEL:-release}" --output "$tmp/manifest.json" "$@"
}
reject() {
  if "$@" > "$tmp/rejected.log" 2>&1; then echo "Unexpected success: $*" >&2; exit 1; fi
}
generate --image-entry "$tmp/hub.json" --image-entry "$tmp/mirror.json"
jq -e --arg digest "$digest" --arg commit "$commit" '.images[0].repository == "docker.io/zrlog/zrlog" and
  .images[0].digest == $digest and .images[1].digest != $digest and .sourceCommit == $commit' "$tmp/manifest.json" >/dev/null
for change in '.version="4.0.2"' '.sourceCommit="bad"' '.digest="latest"' '.platforms=[]' \
  '.platforms=["linux/arm64","linux/arm64/v8"]' '.repository="https://docker.io/zrlog/zrlog"'; do
  jq "$change" "$tmp/hub.json" > "$tmp/invalid.json"
  reject generate --image-entry "$tmp/invalid.json"
done
jq '.repository="index.docker.io/zrlog/zrlog"' "$tmp/hub.json" > "$tmp/alias.json"
reject generate --image-entry "$tmp/hub.json" --image-entry "$tmp/alias.json"
cp "$tmp/native.json" "$tmp/original-native.json"
jq '.buildId="adminweb-1234567" | .releaseDate="2026-10-11 21:00:00+08:00"' "$tmp/native.json" > "$tmp/changed.json"
cp "$tmp/changed.json" "$tmp/native.json"
CHANNEL=preview generate --image-entry "$tmp/hub.json"
jq -e '.buildId == "adminweb-1234567" and .releaseDate == "2026-10-11 21:00:00+08:00"' "$tmp/manifest.json" >/dev/null
reject generate --image-entry "$tmp/hub.json"
cp "$tmp/original-native.json" "$tmp/native.json"
generate --image-entry "$tmp/hub.json" --image-entry "$tmp/mirror.json"
cp "$tmp/manifest.json" "$tmp/original-manifest.json"

mkdir "$tmp/bin" "$tmp/store"
export IMAGE_TEST_STORE="$tmp/store"
export ZRLOG_S3_BUCKET=fixture ZRLOG_S3_ENDPOINT=https://fixture.invalid AWS_ACCESS_KEY_ID=fixture AWS_SECRET_ACCESS_KEY=fixture
export PATH="$tmp/bin:$PATH"
cat > "$tmp/bin/aws" <<'MOCK'
#!/usr/bin/env bash
set -euo pipefail
[[ $1 == --endpoint-url && $3 == s3api ]]
operation=$4; shift 4
key='' body='' condition='' etag='' destination=''
while (($#)); do
  case $1 in
    --bucket|--content-type|--cache-control) shift 2 ;;
    --key) key=$2; shift 2 ;;
    --body) body=$2; shift 2 ;;
    --if-none-match) condition=absent; [[ $2 == '*' ]]; shift 2 ;;
    --if-match) condition=etag; etag=$2; shift 2 ;;
    *) destination=$1; shift ;;
  esac
done
file="$IMAGE_TEST_STORE/$key"
if [[ ${IMAGE_TEST_ACCESS_DENIED:-0} == 1 ]]; then echo 'An error occurred (AccessDenied)' >&2; exit 1; fi
if [[ $operation == get-object ]]; then
  [[ -f $file ]] || { echo 'An error occurred (NoSuchKey)' >&2; exit 1; }
  cp "$file" "$destination"
  [[ ${IMAGE_TEST_BAD_READBACK:-0} != 1 || $key != */docker-image/* ]] || printf '\ncorrupt' >> "$destination"
  jq -n --arg etag "$(sha256sum "$file" | cut -d ' ' -f1)" '{ETag:$etag}'
else
  [[ $operation == put-object && -n $condition ]]
  if [[ ${IMAGE_TEST_CONCURRENT:-0} == 1 && $key == */last.* ]]; then
    echo 'An error occurred (PreconditionFailed)' >&2; exit 1
  fi
  if [[ $condition == absent ]]; then [[ ! -e $file ]];
  else [[ -f $file && $(sha256sum "$file" | cut -d ' ' -f1) == "$etag" ]]; fi
  mkdir -p "$(dirname "$file")"
  cp "$body" "$file"
  printf '%s\n' "$key" >> "$IMAGE_TEST_STORE/writes"
  printf '{}\n'
fi
MOCK
chmod +x "$tmp/bin/aws"
publish() { bash "$script_dir/publish-image-manifest.sh" "$tmp/manifest.json"; }
publish > "$tmp/result.json"
printf '%s\n' release/docker-image/4.0.1-abcdef1.json release/last.docker-image.version.json > "$tmp/expected-writes"
cmp "$tmp/expected-writes" "$tmp/store/writes"
publish > /dev/null
cmp "$tmp/expected-writes" "$tmp/store/writes"
for change in '.releaseDate="2026-10-10 13:00:00Z"' \
  '.releaseDate="2026-10-11 12:00:00Z" | .buildId="abcdef100"' \
  '.sourceCommit="bbbbbbb0000000000000000000000000000000000" | .buildId="bbbbbbb"' \
  '.images[0].digest="sha256:3333333333333333333333333333333333333333333333333333333333333333"' \
  '.unknownField=true'; do
  jq "$change" "$tmp/original-manifest.json" > "$tmp/manifest.json"
  reject publish
  cmp "$tmp/expected-writes" "$tmp/store/writes"
done
cp "$tmp/original-manifest.json" "$tmp/manifest.json"
IMAGE_TEST_ACCESS_DENIED=1 reject publish
cmp "$tmp/expected-writes" "$tmp/store/writes"
jq '.buildId="abcdef100" | .releaseDate="2026-10-12 13:00:00Z"' "$tmp/original-manifest.json" > "$tmp/manifest.json"
IMAGE_TEST_CONCURRENT=1 reject publish
cmp "$tmp/original-manifest.json" "$tmp/store/release/last.docker-image.version.json"
IMAGE_TEST_BAD_READBACK=1 reject publish
cmp "$tmp/original-manifest.json" "$tmp/store/release/last.docker-image.version.json"
publish > /dev/null
cmp "$tmp/manifest.json" "$tmp/store/release/last.docker-image.version.json"

# Registry platform discovery must reject platforms never runtime-tested.
cat > "$tmp/bin/docker" <<'MOCK'
#!/usr/bin/env bash
set -euo pipefail
if [[ $5 == --format ]]; then printf '%s\n' "${4##*@}";
else printf '{"manifests":[{"platform":{"os":"linux","architecture":"amd64"}},{"platform":{"os":"unknown","architecture":"unknown"}}]}\n'; fi
MOCK
chmod +x "$tmp/bin/docker"
collect() {
  bash "$script_dir/collect-image-manifest.sh" --native-metadata "$tmp/native.json" --channel release \
    --tag 4.0.1 --image "zrlog/zrlog@$digest" --output "$tmp/collected.json" "$@"
}
collect --platform linux/amd64
reject collect --platform linux/amd64 --platform linux/arm64
echo 'Docker metadata identity, registry platform checks, immutable publication, readback and concurrency checks passed'
