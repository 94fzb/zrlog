#!/usr/bin/env bash
rm -rf lib
rm -rf zrlog.*
rm -rf conf/plugins/*
bash -e bin/add-build-info.sh "${1}" "${2}"
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Djakarta-scope='provided' -Dlambda-scope='provided' -Dservlet-scope='provided' -Pjar clean package -U
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Djakarta-scope='provided' -Dlambda-scope='provided' -Pwar -DpackageType=war package -U