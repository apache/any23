============
Any23 README
============

Anything To Triples (any23) is a library and web service that extracts
structured data in RDF format from a variety of Web documents.

-------------
Documentation
-------------

For details on the command line tool and web interface, see:
  http://developers.any23.org/getting-started.html

For a guide to using any23 as a library in your Java applications, see:
  http://developers.any23.org/developers.html

Javadocs are available here:
  http://developers.any23.org/apidocs/

----------------------
Build from Source Code
----------------------

Be sure to have the Apache Maven v.2.2.x+ installed and included in PATH.

For specific informations about Maven see: http://maven.apache.org/

Go to the trunk folder:

    $ cd trunk/

and execute the following command:

    trunk$ mvn clean install

This will install the Any23 artifacts and its dependencies in your 
local Maven2 repository.

------------------------
Any23 Command line Tools
------------------------

Any23 comes with some command line tools:

   any23       allows to perform a metadata extraction on a file or URL source.
   any23tools  provides access to some auxiliary tools.

The complete documentation about these tools can be found here: 
http://developers.any23.org/getting-started.html

To run such tools, go to the any23-core bin folder:

  trunk$ cd any23-core/bin

and then invoke them:
  
  bin$ ./any23
  [usage instructions will be printed out]

  bin$ ./any23tools
  [usage instructions will be printed out]


---------------------
Run the Any23 Service
---------------------

Any23 can be run as a service. 
To run the Any23 service go to the any23-service bin folder:

  trunk$ cd any23-service/bin

and then invoke:

  bin$ ./any23server   

You can check the service is running by accessing
http://localhost:8080/ with your browser.

The complete documentation about this service can be found here: 
http://developers.any23.org/getting-started.html

----------------------
Generate Documentation
----------------------

To generate the project site locally execute the following command from the trunk dir:

    trunk$ MAVEN_OPTS='-Xmx512m' mvn clean site:site

You can speed up the site generation process specifying the offline option ( -o ),
but it works only if all the involved plugin dependencies has been already downloaded
in the local M2 repository:

    trunk$ MAVEN_OPTS='-Xmx512m' mvn -o clean site:site

If you're interested in generating the Javadoc enriched with navigable UML graphs, you can activate
the umlgraphdoc profile. This profile relies on graphviz ( http://www.graphviz.org/) that must be 
installed in your system.

    trunk$ MAVEN_OPTS='-Xmx256m' mvn -P umlgraphdoc clean site:site

In order to correctly deploy the site to a remote FTP you just need to properly set up
the following lines in your <distributionManagement> of the root pom.xml:

<site>
    <id>any23.developers</id>
    <name>Any23 Developer Web Site</name>
    <url>ftp://ftp.cyganiak.de</url>
</site>

Please remember that you need to set up your username and password to access to that FTP in your
settings.xml in this way:

<server>
    <id>any23.developers</id>
    <username>username</username>
    <password>password</password>
</server>

------------------
Make a new release
------------------

To prepare a new release, just verify that the are no local changes and then invoke:

	mvn release:prepare -Dusername=<svn.username> -Dpassword=<svn.pass>
	
if everything goes right, perform the release simply typing:

	mvn release:perform

EOF
