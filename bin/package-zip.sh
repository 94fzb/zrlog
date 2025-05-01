#!/usr/bin/env bash
rm -rf lib
rm -rf zrlog.*
rm -rf conf/plugins/*
version=$(printf 'VER\t${project.version}' | ./mvnw help:evaluate | grep '^VER' | cut -f2)
bash bin/build-system-info.sh
./mvnw -Djakarta-scope='provided' -Dservlet-scope='provided' -Pjar clean package -U
./mvnw -Djakarta-scope='provided' -Pwar -DpackageType=war package -U