=============
Any23 Web Service
=============

This is the root dir of the Any23 Web-Service module.

Apache Any23 provides a Web-Service that can be used to extract RDF from Web documents.

Generate Web-Service Packaging
===============================

To generate the desired Web-service package, execute 'mvn package' from this directory.

$cd $ANY23-HOME/service
$ mvn package

From this directory it generates:
.
├── pom.xml
├── README.txt
├── src
│   ├── main
│   │   ├── assembly
│   │   ├── bin
│   │   ├── java
│   │   ├── resources
│   │   └── webapp
│   └── test
│       ├── java
│       └── resources
└── target
    ├── any23-service-${version}.war
    ├── any23-service-${version}-without-deps.war
    ├── apache-any23-service-${version}-bin-server-embedded.tar.gz <<<
    ├── apache-any23-service-${version}-bin-server-embedded.zip <<<
    ├── apache-any23-service-${version}-bin.tar.gz <<<
    ├── apache-any23-service-${version}-bin-without-deps.tar.gz <<<
    ├── apache-any23-service-${version}-bin-without-deps.zip <<<
    ├── apache-any23-service-${version}-bin.zip <<<
    ├── archive-tmp
    ├── classes
    ├── generated-sources
    ├── maven-archiver
    ├── maven-shared-archive-resources
    ├── surefire
    ├── surefire-reports
    └── test-classes
...

Specific README's can be found in either ./target/*.tar.gz || ./target/*.zip (annotated above with '<<<'), where much more detailed information sources can be located.
