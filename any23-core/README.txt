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

There are two ways to build from source code:

   - using the Apache Ant build system;

   - using the Apache Maven Build system.


---------------------
Build with Apache Ant
---------------------

Be sure to have the Apache Ant v.1.7.x+ installed and included in PATH.

For specific informations about Ant see: http://ant.apache.org/

Execute the following commands:

    $ cd trunk/

    $ ant -p

This will show you the list of available build targets:

It will appear something as below:

    clean    Cleanup the generated files and folders.
    compile  Compile the source code.
    dist     Generate both ZIP and TAR distributions.
    jar      Generate the library JAR.
    javadoc  Generate the source Javadoc.
    tar      Generate the TAR library distribution.
    test     Run the test suite.
    war      Generate the library WAR archive.
    zip      Generate the ZIP library distribution.

To obtain the library JAR distribution invoke:

  $ ant clean jar

You will find the library JAR and its dependencies inside the lib/ folder.


----------------
Build with Maven
----------------

Be sure to have the Apache Maven v.2.2.x+ installed and included in PATH.

For specific informations about Maven see: http://maven.apache.org/

Go to the trunk folder:

    $ cd trunk/

and execute the following command:

    $ mvn clean install

This will install the Any23 artifact and its dependencies in your local M2 repository.

------------------------
Any23 Command line Tools
------------------------

Any23 comes with some command line tools:

   any23       allows to perform a metadata extraction on a file or URL source.
   any23tools  provides access to some auxiliary tools.

The complete documentation about these tools can be found here: 
http://developers.any23.org/getting-started.html

To run such tools, go to the bin folder:

  $ cd bin

and then invoke them:
  
  $ ./any23
  [usage instructions will be printed out]

  $ ./any23tools
  [usage instructions will be printed out]


---------------------
Run the Any23 Service
---------------------

Any23 can be run as a service. 
To run the Any23 service go to the bin folder:

  $ cd bin

and then invoke:

  ./any23server   

You can check the service is running by accessing
http://localhost:8080/ with your browser.

The complete documentation about this service can be found here: 
http://developers.any23.org/getting-started.html

----------------------
Generate Documentation
----------------------

To generate the project site locally execute the following command:

    $ MAVEN_OPTS='-Xmx512m' mvn clean site

You can speed up the site generation process specifying the offline option ( -o ),
but it works only if all the involved plugin dependencies has been already downloaded
in the local M2 repository:

    $ MAVEN_OPTS='-Xmx512m' mvn -o clean site

If you're interested in generating the Javadoc enriched with navigable UML graphs, you can activate
the umlgraphdoc profile. This profile relies on graphviz ( http://www.graphviz.org/) that must be 
installed in your system.

    $ MAVEN_OPTS='-Xmx256m' mvn -P umlgraphdoc clean site  


EOF
