baseVersion=2.1
releaseVersion=${baseVersion}.${1}
nextVersion=${baseVersion}.$((${1}+1))
./mvnw versions:set -DnewVersion=${releaseVersion}
git add -A
git commit -m '[shell-release]release version '${releaseVersion}
git push