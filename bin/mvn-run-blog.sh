#!/usr/bin/env bash
set -e

bash -e bin/add-build-info.sh "dev" java zip
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Dmaven.test.skip=false -DskipTests=false -Djakarta-scope='provided' -Dlambda-scope='provided' -Dservlet-scope='provided' -Pjar clean package -U
rm -rf lib/zrlog-admin-*.jar
rm -rf lib/zrlog-install-web-*.jar
sh bin/run.sh
