#!/usr/bin/env bash
sh bin/package-native.sh
zip -9 -r zrlog-"${version}-graalvm-native".zip bin/run.sh bin/start.sh bin/start.bat static zrlog
