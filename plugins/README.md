# Any23 Plugins

This is the root dir of the Any23 Plugins module.

A plugin is an extension of the Any23 core and can be plugged using
the Plugin Manager capabilities.

# Plugins

## basic-crawler

A CLI tool which extends the Rover CLI adding crawler specific
capabilities.

## html-scraper

The HTML scraper is able to convert any HTML page to triples
containing the text scraped from the page.

## office-scraper

The Office scraper is able to convert the main MS Office compatible
formats and convert them to triples.

## integration-test

This module contains the integration tests for all the defined plugins.

# Generate Plugin Packaging

To generate the desired plugin package, navigate to the plugin directory and execute 
```
mvn package
```
e.g. to generate the basic-crawler plugin package
```
$cd $ANY23-HOME/plugins/basic-crawler
$ mvn package
```
From the basic-crawler directory this generates
```
.
|-- pom.xml
|-- src
|   |-- main
|   |   |-- assembly
|   |   `-- java
|   `-- test
`-- target
    |-- any23-basic-crawler-${version}.jar
    |-- apache-any23-basic-crawler-${version}-bin.tar.gz <<<
    |-- apache-any23-basic-crawler-${version}-bin.zip <<<
    |-- archive-tmp
    |-- classes
    |   |-- META-INF
    |   `-- org
    |-- generated-sources
    |-- maven-archiver
    |-- maven-shared-archive-resources
    |-- surefire
    |-- surefire-reports
    `-- test-classes
...
```
Plugin specific README's can be found in either ./target/*.tar.gz || ./target/*.zip (annotated above with '<<<'), where much more detailed information sources can be located.
  
