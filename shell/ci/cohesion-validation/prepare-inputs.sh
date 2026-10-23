#!/usr/bin/env bash
set -euo pipefail

# Temporary verification branch: use the existing package workflow and scripts.
evidence="$PWD/.package-evidence/cohesion"
mkdir -p "$evidence"
python3 shell/ci/cohesion-validation/inputs.py sources

(
  cd .cohesion-sources/base
  ./mvnw -B -ntp -DskipTests clean install
) > "$evidence/base-build.log" 2>&1 || { tail -100 "$evidence/base-build.log"; exit 1; }
(
  cd .cohesion-sources/admin
  ./mvnw -B -ntp -PnodeBuild -DskipTests clean install
) > "$evidence/admin-build.log" 2>&1 || { tail -100 "$evidence/admin-build.log"; exit 1; }

# Warm every package/agent profile before freezing Maven resolution offline.
./mvnw -B -ntp -U clean install > "$evidence/main-warm.log" 2>&1 || { tail -100 "$evidence/main-warm.log"; exit 1; }
source shell/lib/updater-modules.sh
updaterSelectRuntime native-zip
./mvnw -B -ntp -U "${zrlogUpdaterMavenArgs[@]}" \
  -Pnative,native-sqlite -Dagent -Djakarta-scope=provided -Dzrlog-polyglot-template-scope=compile \
  dependency:go-offline -f package/pom.xml > "$evidence/native-warm.log" 2>&1 || { tail -100 "$evidence/native-warm.log"; exit 1; }
./mvnw -B -ntp -Ppackage-native-zip dependency:go-offline -f package/pom.xml \
  > "$evidence/assembly-warm.log" 2>&1 || { tail -100 "$evidence/assembly-warm.log"; exit 1; }

# Warming may resolve newer remote snapshots. Reinstall our exact sources last.
for source in base admin; do
  (
    cd ".cohesion-sources/$source"
    ./mvnw -B -ntp -o -DskipTests install
  ) > "$evidence/$source-restore.log" 2>&1 || { tail -100 "$evidence/$source-restore.log"; exit 1; }
done
./mvnw -B -ntp -o "${zrlogUpdaterMavenArgs[@]}" \
  -Pnative,native-sqlite -Djakarta-scope=provided -Dzrlog-polyglot-template-scope=compile \
  dependency:build-classpath -Dmdep.outputFile="$evidence/native-classpath.txt" -f package/pom.xml
python3 shell/ci/cohesion-validation/inputs.py record
printf '%s\n' 'MAVEN_ARGS=-o -DjvmArgs=-Xmx6g,-XX:ActiveProcessorCount=4' >> "$GITHUB_ENV"
