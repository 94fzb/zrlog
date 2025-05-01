#!/usr/bin/env bash
sh shell/native/package-native.sh "${1}" "${2}"
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Ppackage-native-zip assembly:single -f "package-web/pom.xml"
