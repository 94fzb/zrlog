#!/usr/bin/env bash
sh bin/package-native.sh
./mvnw -Ppackage-native-zip assembly:single -f "package-web/pom.xml"
