./mvnw clean install -Dmaven.test.skip=true && cd web  && ../mvnw tomcat7:run -DcontextPath=/zrlog -Dtomcat-scope=provided
