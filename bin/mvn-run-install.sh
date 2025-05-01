bash -e bin/add-build-info.sh "dev" "开发版"
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Djakarta-scope='provided' -Dlambda-scope='provided' -Dservlet-scope='provided' -Pjar clean package -U
export INSTALL_LOCK_FILE_NAME=temp/install.lock
export DB_PROPERTIES_FILE_NAME=temp/db.properties
rm conf/${INSTALL_LOCK_FILE_NAME}
rm conf/${DB_PROPERTIES_FILE_NAME}
sh bin/run.sh