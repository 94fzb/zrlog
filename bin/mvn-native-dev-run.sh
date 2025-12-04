#!/usr/bin/env bash
source shell/native/.graalvm_rc
bash -e bin/add-build-info.sh "dev" "开发版"
export JAVA_HOME=${HOME}/dev/graalvm-jdk-latest
export PATH=${JAVA_HOME}/bin:$PATH
export DEV_MODE=true
bash -e shell/native/package-native.sh
./zrlog --port=28080

