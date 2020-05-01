rm -rf web/src/main/lib
rm -rf web/src/main/zrlog.*
./mvnw clean package -Dmaven.war.skip
mv web/src/main/zrlog*.jar web/src/main/zrlog.jar
cd web/src/main/ && sh bin/run.sh