bash -e bin/add-build-info.sh "dev" "开发版"
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Djakarta-scope='provided' -Dlambda-scope='provided' -Dservlet-scope='provided' -Pjar clean package -U
rm -rf lib/zrlog-install-web-*.jar
rm -rf lib/blog-web-*.jar
sh bin/run.sh