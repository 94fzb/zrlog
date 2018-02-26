mvnw.cmd clean install -Dmaven.test.skip=true && mvnw.cmd -f web/pom.xml tomcat7:run -Dtomcat-scope=provided

