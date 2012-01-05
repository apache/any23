#!/bin/bash

mvn -f ../pom.xml      \
     deploy:deploy-file\
    -DgroupId=net.xeoh \
    -DartifactId=jspf  \
    -Dversion=0.9.0    \
    -Dpackaging=jar    \
    -Dfile=./lib/jspf.core-0.9.0.jar \
    -Durl=svn:https://svn.apache.org/repos/asf/incubator/any23/repo/ \
|| { echo "Error while installing project dep."; exit 1; }

exit 0 
