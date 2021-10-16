rm -rf web/src/main/lib
rm -rf web/src/main/zrlog.*
./mvnw clean package -Dmaven.war.skip -DfinalName=zrlog
cd web/src/main/ && sh bin/run.sh