#!/bin/bash

CURR_DIR="$(cd "$(dirname "$0")"; pwd -P)"

EXT_REPO=https://svn.apache.org/repos/asf/incubator/any23/repo/

echo Installing Any23 project dependencies in External Repo

echo Installing [crawler4j-2.6.1]
echo
echo
mvn -f $CURR_DIR/../pom.xml \
     deploy:deploy-file    \
    -DgroupId=edu.uci.ics  \
    -DartifactId=crawler4j \
    -Dversion=2.6.1        \
    -Dpackaging=jar        \
    -Dfile=$CURR_DIR/crawler4j-2.6.1.jar \
    -Durl=svn:$EXT_REPO \
    -DrepositoryId=any23-repository-external \
|| { echo "Error while installing project dependency."; exit 1; }

echo
echo
echo Installing [dsiutils-2.0.1]
echo
echo

mvn -f $CURR_DIR/../pom.xml \
     deploy:deploy-file     \
    -DgroupId=it.unimi.dsi  \
    -DartifactId=dsiutils   \
    -Dversion=2.0.1         \
    -Dpackaging=jar         \
    -Dfile=$CURR_DIR/dsiutils-2.0.1.jar \
    -Durl=svn:$EXT_REPO \
    -DrepositoryId=any23-repository-external \
|| { echo "Error while installing project dependency."; exit 1; }

echo
echo
echo Dependencies installation completed successfully.
echo

exit 0 
