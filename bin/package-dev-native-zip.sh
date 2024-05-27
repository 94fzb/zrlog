#!/usr/bin/env bash
export JAVA_HOME=${HOME}/dev/graalvm-jdk-latest
export PATH=${JAVA_HOME}/bin:$PATH
sh bin/package-native.sh
zip -9 -r zrlog-dev-graalvm-native.zip static zrlog
