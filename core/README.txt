=============
Any23 Core
=============

This is the root dir of the Any23 Core module.

The core library from which the command-line tools support is provided.

Generate Core Packaging
=========================

To generate the desired Core package, execute 'mvn package' from this directory.

$cd $ANY23-HOME/core
$ mvn package

From this directory it generates:

.
├── pom.xml
├── README.txt
├── src
│   ├── main
│   │   ├── assembly
│   │   ├── java
│   │   └── resources
│   └── test
├── target
│   ├── any23-core-0.7.0-incubating-SNAPSHOT.jar <<<
│   ├── apache-any23-0.7.0-incubating-SNAPSHOT-bin.tar.gz <<<
│   ├── apache-any23-0.7.0-incubating-SNAPSHOT-bin.zip <<<
│   ├── appassembler
│   ├── archive-tmp
│   ├── classes
│   ├── generated-sources
│   ├── maven-archiver
│   ├── maven-shared-archive-resources
│   └── test-classes
...

Once you unzip/tar the relevant file, specific README's can be found in either ./target/*.tar.gz || ./target/*.zip (annotated above with '<<<'), where much more detailed information sources can be located.
