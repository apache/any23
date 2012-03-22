
          :::     ::::    ::: :::   :::  ::::::::   ::::::::
       :+: :+:   :+:+:   :+: :+:   :+: :+:    :+: :+:    :+:
     +:+   +:+  :+:+:+  +:+  +:+ +:+        +:+         +:+
   +#++:++#++: +#+ +:+ +#+   +#++:       +#+        +#++:
  +#+     +#+ +#+  +#+#+#    +#+      +#+             +#+
 #+#     #+# #+#   #+#+#    #+#     #+#       #+#    #+#
###     ### ###    ####    ###    ##########  ########

============
Apache Any23 README
============

Apache Anything To Triples (Any23) is a library and web service that extracts
structured data in RDF format from a variety of Web documents.

--------------------
Distribution Content
--------------------

core           	     The library core codebase.
service        	     The library HTTP service codebase.
lib                  Contains the the external deps (read lib/README.txt for further details).
plugins              Library plugins codebase (read plugins/README.txt for further details).
RELEASE-NOTES.txt    File reporting main release notes for every version.
LICENSE.txt          Applicable project license.
README.txt           This file.

--------------------
Online Documentation
--------------------

For details on the command line tool and web interface, see:
  http://incubator.apache.org/any23/getting-started.html

For a guide to using any23 as a library in your Java applications, see:
  http://incubator.apache.org/any23/developers.html

Javadocs is available here:
  http://incubator.apache.org/any23/apidocs/

----------------------------
Build Any23 from Source Code
----------------------------

Be sure to have the Apache Maven v.2.2.x+ installed and included in $PATH.

For specific information about Maven see: http://maven.apache.org/

Go to the trunk folder:

    $ cd trunk/

and execute the following command:

    trunk$ mvn clean install

This will install the Any23 artifacts and its dependencies in your 
local Maven2 repository.

-------------------------------
Run the Any23 Commandline Tools
-------------------------------

Any23 comes with some command line tools:

   any23       allows to perform a metadata extraction on a file or URL source.
   any23tools  provides access to some auxiliary tools.

The complete documentation about these tools can be found here: 
http://incubator.apache.org/any23/getting-started.html

To run such tools, go to the core bin folder:

  trunk$ cd core/target/any23-${version}/bin

and then invoke them:
  
  bin$ ./any23
  [usage instructions will be printed out]

  bin$ ./any23tools
  [usage instructions will be printed out]


-------------------------
Run the Any23 Web Service
-------------------------

Any23 can be run as a service. 
To run the Any23 service go to the any23-service bin folder:

  trunk$ cd service/target/any23-${version}/bin

and then invoke:

  bin$ ./any23server   

You can check the service is running by accessing
http://localhost:8080/ with your browser.

The complete documentation about this service can be found here: 
http://incubator.apache.org/any23/getting-started.html

-------------------------------
Build the Any23 Web Service WAR
-------------------------------

The Any23 Service WAR by default will be generated as self-contained,
all the dependencies will be included as JAR within the WEB-INF/lib archive dir.

To generate the self contained WAR invoke:

  any23-service$ mvn [-o] [-Dmaven.test.skip=true] clean package

Where -o will build the process offline, and -Dmaven.test.skip=true
will force the test skipping.

The WAR will be generated in

  target/any23-service-X.Y.Z-VERSION.war

To produce a instead a WAR WITHOUT the included JAR dependencies it is possible to use
the war-without-deps profile:

  any23-service$ mvn [-o] [-Dmaven.test.skip=true] -Pwar-without-deps clean package

Again the WAR will be generated in

  target/any23-service-X.Y.Z-VERSION.war

--------------------------
Generate the Documentation
--------------------------

To generate the project site locally execute the following command from the trunk dir:

    trunk$ MAVEN_OPTS='-Xmx1024m' mvn clean site:site

You can speed up the site generation process specifying the offline option ( -o ),
but it works only if all the involved plugin dependencies has been already downloaded
in the local M2 repository:

    trunk$ MAVEN_OPTS='-Xmx1024m' mvn -o clean site:site

If you're interested in generating the Javadoc enriched with navigable UML graphs, you can activate
the umlgraphdoc profile. This profile relies on graphviz ( http://www.graphviz.org/) that must be 
installed in your system.

    trunk$ MAVEN_OPTS='-Xmx1024m' mvn -P umlgraphdoc clean site:site

------------------------
Deploy the Documentation
------------------------

::Developers interest only.::

In order to correctly deploy the site to a remote FTP host you just need to properly set up
the following lines in your <distributionManagement> section of the root pom.xml:

    <site>
        <id>any23.developers</id>
        <name>Any23 Developer Web Site</name>
        <url>ftp://FTP-HOSTNAME</url>
    </site>

Remember that you need to set up your username and password to access to that FTP in your
settings.xml in this way:

    <server>
        <id>any23.developers</id>
        <username>FTP-USERNAME</username>
        <password>FTP-PASSWORD</password>
    </server>

To perform the deployment simply run:

    mvn clean site:site site:deploy

Optionally you may require to fix the mimetype for *.html files:

  cd site
  svn up
  find . -name "*.html" | xargs svn ps svn:mime-type text/html
  find . -name "*.css"  | xargs svn ps svn:mime-type text/css
  svn ci

----------------------------------------------
Deploy a Snapshot Release on Remote Repository
----------------------------------------------

::Developers interest only.::

Check the configuration in section distributionManagement
within pom.xml:

    <distributionManagement>
        ...
        <repository>
            <id>rdf-commons-googlecode</id>
            <name>RDF Commons Google Code Snapshot Repository</name>
            <url>svn:https://rdf-commons.googlecode.com/svn/repo/</url>
        </repository>
        ...
    <distributionManagement>

Then to deploy a snapshot release perform:

    mvn clean deploy

------------------
Make a New Release
------------------

::Developers interest only.::

To prepare a new release, just verify that the are no local changes and then invoke:

	mvn release:prepare [-Dusername=<svn.username> -Dpassword=<svn.pass>]
	
if everything goes right, perform the release simply typing:

	trunk$ MAVEN_OPTS='-Xmx2048m' mvn release:perform

Export the just created tag:

    tmp-dir$ svn export <path/to/curr-tag>

Package all modules for direct download:

    $ cd <curr-tag-export>/
    <curr-tag-export>$ mvn clean package
    cd any23-core
    mvn assembly:assembly
    cd ..
    cd ..
    tar cvzf any23-<curr-tag>.tar.gz tags/<curr-tag>
    zip   -r any23-<curr-tag>.zip    tags/<curr-tag>

Upload the produced packages in download section:

   http://code.google.com/p/any23/downloads/list

--------------------
Manage External Deps
--------------------

::Developers interest only.::

External Deps are libraries used by some Any23 modules which are
not available in public Maven repositories. Such libraries are
managed within the 'lib' dir.

----------------------------
Munging of Any23 code to ASF
----------------------------
When it was decided[0] that the Any23 code be brought into the Apache Incubator, the existing code
was migrated over to the ASF infrastructure and documented/managed via a number of Jira tickets [1-3].

The commentary provided within the below references spans the entire history of the code migration.    


[0] http://wiki.apache.org/incubator/Any23Proposal
[1] https://issues.apache.org/jira/browse/INFRA-3978
[2] https://issues.apache.org/jira/browse/INFRA-4146
[2] https://issues.apache.org/jira/browse/ANY23-29
EOF
