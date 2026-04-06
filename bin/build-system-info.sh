REPORT_FILE=zrlog-web/src/main/resources/build_system_info.md

mkdir -p "$(dirname "$REPORT_FILE")"

echo "### 🧾 Build System info" > "$REPORT_FILE"
echo "" >> "$REPORT_FILE"

# OS Info
echo "#### 🖥 OS Info" >> "$REPORT_FILE"
{
  echo '```'
  uname -srmv
  echo
  [ -f /etc/os-release ] && cat /etc/os-release
  command -v sw_vers &> /dev/null && sw_vers
  echo '```'
} >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"

# CPU Info
echo "#### 🧠 CPU Info" >> "$REPORT_FILE"
{
  echo '```'
  if command -v lscpu &> /dev/null; then
    lscpu
  elif [[ "$OSTYPE" == "darwin"* ]]; then
    sysctl -a | grep machdep.cpu
  elif command -v wmic &> /dev/null; then
    powershell -Command "(Get-CimInstance Win32_Processor)[0] | ForEach-Object { 'Name=' + \$_.Name; 'Cores=' + \$_.NumberOfCores; 'Threads=' + \$_.NumberOfLogicalProcessors }"
  else
    echo "CPU info not available for this platform"
  fi
  echo '```'
} >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"

# Java Version
echo "#### ☕ Java Version" >> "$REPORT_FILE"
{
  echo '```'
  java -version 2>&1
  echo '```'
} >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"

# GitHub Runner Context (optional)
echo "#### 🤖 Runner Context" >> "$REPORT_FILE"
{
  echo '```'
  echo "RUNNER_OS=$RUNNER_OS"
  echo "RUNNER_ARCH=$RUNNER_ARCH"
  echo "GITHUB_REPOSITORY=$GITHUB_REPOSITORY"
  echo "GITHUB_SHA=$GITHUB_SHA"
  echo '```'
} >> "$REPORT_FILE"

# Build properties info
echo "#### 📃 Build.properties Info" >> "$REPORT_FILE"
{
  echo '```properties'
  # shellcheck disable=SC2046
  echo "$(cat 'zrlog-web/src/main/resources/build.properties')"
  echo '```'
} >> "$REPORT_FILE"

./mvnw -Dzrlog-polyglot-template-scope='test' -Dlambda-scope="test" -Dservlet-scope="test" -Djakarta-scope="test" com.github.ferstl:depgraph-maven-plugin:aggregate -DgraphFormat=json -DtransitiveExcludes=* -Dscope=compile
jq '[.artifacts[] | {key: (.groupId + ":" + .artifactId), value: .version}] | from_entries' target/dependency-graph.json > target/pure-deps.json
targetJsonFile=zrlog-web/src/main/resources/pure-deps.json
cat 'target/pure-deps.json'  |grep -v "servlet" > ${targetJsonFile}
# Build dep info
echo "#### 📃 Dependency Info" >> "$REPORT_FILE"
{
  echo '```json'
  # shellcheck disable=SC2046
  echo "$(cat ${targetJsonFile})"
  echo '```'
} >> "$REPORT_FILE"

cp ${REPORT_FILE} "doc/"