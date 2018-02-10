./mvnw.cmd clean install -Dmaven.test.skip=true && cd web  && ../mvnw.cmd tomcat7:run -Dtomcat-scope=provided
