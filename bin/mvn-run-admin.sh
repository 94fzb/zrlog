./mvnw -Djakarta-scope='provided' -Dlambda-scope='provided' -Dservlet-scope='provided' -pl "!blog-web" -Pjar clean package -U
rm -rf lib/zrlog-install-web-*.jar
sh bin/run.sh