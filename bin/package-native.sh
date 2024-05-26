rm -rf lib
rm -rf zrlog.*
rm -rf zrlog-.*
rm -rf conf/plugins
version=$(printf 'VER\t${project.version}' | ./mvnw help:evaluate | grep '^VER' | cut -f2)
./mvnw clean install
./mvnw -Pnative -Dagent exec:exec@java-agent -U  -f package-web/pom.xml
./mvnw -Pnative package
mv package-web/target/zrlog zrlog