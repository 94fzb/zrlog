#!/usr/bin/env bash
set -e

rm -rf lib
rm -rf zrlog.*
rm -rf conf/plugins/*
bash -e bin/add-build-info.sh "${1}" java zip
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Dmaven.test.skip=false -DskipTests=false -Djakarta-scope='provided' -Dlambda-scope='provided' -Dservlet-scope='provided' -Dzrlog-polyglot-template-scope='runtime' -Pjar clean package -U
shopt -s nullglob
javaZips=(target/zrlog-*.zip)
if [[ ${#javaZips[@]} -ne 1 || ! -f "${javaZips[0]}" ]]; then
  echo "Expected exactly one Java ZIP in target, found ${#javaZips[@]}" >&2
  exit 1
fi
bash -e shell/java/test-java-package-contract.sh "${javaZips[0]}"
bash -e bin/add-build-info.sh "${1}" java war
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Dmaven.test.skip=false -DskipTests=false -Djakarta-scope='provided' -Dlambda-scope='provided' -Dzrlog-polyglot-template-scope='runtime' -Pwar -DpackageType=war package -U
javaWars=(target/zrlog-*.war)
if [[ ${#javaWars[@]} -ne 1 || ! -f "${javaWars[0]}" ]]; then
  echo "Expected exactly one WAR in target, found ${#javaWars[@]}" >&2
  exit 1
fi
bash -e shell/java/test-java-package-contract.sh "${javaWars[0]}"
