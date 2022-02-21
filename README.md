# Apache Any23 Project

<img src="http://any23.apache.org/images/logo-any23.png" align="right" width="300" />

[![license](https://img.shields.io/github/license/apache/any23.svg?maxAge=2592000)](http://www.apache.org/licenses/LICENSE-2.0)
[![Jenkins](https://img.shields.io/jenkins/s/https/ci-builds.apache.org/job/Any23/job/any23-master.svg?maxAge=3600)](https://ci-builds.apache.org//job/Any23/job/any23-master/)
[![Jenkins tests](https://img.shields.io/jenkins/t/https/ci-builds.apache.org/job/Any23/job/any23-master.svg?maxAge=3600)](https://ci-builds.apache.org//job/Any23/job/any23-master/lastBuild/testReport/)
[![Maven Central](https://img.shields.io/maven-central/v/org.apache.any23/apache-any23.svg?maxAge=86400)](http://search.maven.org/#search|ga|1|g%3A%22org.apache.any23%22)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=apache_any23&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=apache_any23)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=apache_any23&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=apache_any23)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=apache_any23&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=apache_any23)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=apache_any23&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=apache_any23)

Apache Anything To Triples (Any23) is a library and web service that extracts
structured data in RDF format from a variety of Web documents.
Any23 documentation can be found on the [website](http://any23.apache.org)

# Online Documentation

For details on the command line tool and web interface, see [here](http://any23.apache.org/getting-started.html)

For a guide to using Any23 as a library in your Java applications, see [here](http://any23.apache.org/developers.html)

Javadocs is available [here](http://any23.apache.org/apidocs/)

# Community

You can reach our and connect with our community on our [mailing lists](http://any23.apache.org/mail-lists.html)

# Build Any23 from Source Code

The canonical Any23 source code lives at https://github.com/apache/any23.git

Be sure to have the [Apache Maven v.3.x+](http://maven.apache.org/) installed and included in $PATH.

## Clone the source:
```
git clone https://github.com/apache/any23.git
```
## Navigate and build:
```
cd any23
mvn clean install
```
From now on the above directory **any23** is referred to as **$ANY23_HOME**
This will install the Any23 artifacts and its dependencies in your 
local Maven3 repository.
You can then extract the compiled code and use the command line interface
Please note you will need to change the version to the tar or zip you are extracting.
```
tar -zxvf $ANY23_HOME/cli/target/apache-any23-cli-${version-SNAPSHOT}.tar.gz
```
# Run the Any23 Commandline Tools

Any23 comes with some command line tools. Within the directory you just extracted, you can invoke:
Linux
```  
$ANY23_HOME/cli/target/apache-any23-cli-${version-SNAPSHOT}/bin/any23       
# Provides the main Any23 use case: metadata extraction on a file or URL source.
```
Windows
```
$ANY23_HOME/cli/target/apache-any23-cli-${version-SNAPSHOT}/bin/any23.bat      
# Provides the main Any23 use case: metadata extraction on a file or URL source.
```
The complete documentation about these tools can be found [here](http://any23.apache.org/getting-started.html)

The bin scripts are generated dynamically during the package phase.
To ensure the package generation, from the top level directory run:
```
mvn package
```
You can void extracting the archive files by going to the cli generated bin folder
```
cd  $ANY23_HOME/cli/target/appassembler/bin/
```
and finally invoke the script for your OS (UNIX or Windows):
```
  bin$ ./any23
```
usage instructions will be printed out.

# Generate the Documentation

To generate the project site locally execute the following command from $ANY23_HOME:
```
cd $ANY23_HOME
MAVEN_OPTS='-Xmx1024m' mvn [-o] clean site:site
```
You can speed up the site generation process specifying the offline option [-o],
but it works only if all the involved plugin dependencies has been already downloaded
in the local M2 repository.

If you're interested in generating the Javadoc enriched with navigable UML graphs, you can activate
the umlgraphdoc profile. This profile relies on [graphviz](http://www.graphviz.org/) that must be 
installed in your system.
```
cd $ANY23_HOME
MAVEN_OPTS='-Xmx1024m' mvn -P umlgraphdoc clean site:site
```

# Munging of Any23 code to ASF

When it was [decided](http://wiki.apache.org/incubator/Any23Proposal) that the Any23 code be brought into the Apache Incubator, the existing code was migrated over to the ASF infrastructure and documented/managed via a number of Jira tickets e.g, [INFRA-3978](https://issues.apache.org/jira/browse/INFRA-3978) [INFRA-4146](https://issues.apache.org/jira/browse/INFRA-4146) and [ANY23-29](https://issues.apache.org/jira/browse/ANY23-29).
