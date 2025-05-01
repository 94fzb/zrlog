bash -e bin/add-build-info.sh "dev"
export DEV_MODE=true
./mvnw -Djakarta-scope='provided' -Dlambda-scope='provided' -Dservlet-scope='provided' -Pjar clean package -U
sh bin/run.sh