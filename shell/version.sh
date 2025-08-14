#!/usr/bin/env bash
baseVersion=3.2
releaseVersion=${baseVersion}.${1}
nextVersion=${baseVersion}.$((${1}+1))-SNAPSHOT
tagName="v${releaseVersion}"
./mvnw versions:set -DnewVersion=${releaseVersion}
git add -A
git commit -m '[shell-release]release version '${releaseVersion}
git checkout release
git reset --hard master
git tag ${tagName}
git push origin ${tagName}
git push origin release -f
git checkout master
./mvnw versions:set -DnewVersion=${nextVersion}
git add -A
git commit -m '[shell-release]next version '${nextVersion}
git push