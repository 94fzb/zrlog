bash -e bin/add-build-info.sh "dev"
./mvnw -Djakarta-scope='provided' -Dlambda-scope='provided' -Dservlet-scope='provided' -pl "!admin-web,!admin-token" -Pjar clean package -U
rm -rf lib/admin-*.jar
rm -rf lib/zrlog-install-web-*.jar
sh bin/run.sh