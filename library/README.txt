============
Any23 README
============

----------------------
Build from Source Code
----------------------

There are two ways to build from source code:

   - using the legacy Apache Ant build system;

   - using the Apache Maven Build system.

---------------------
Build with Apache Ant
---------------------

WARNING: THE ANT BUILD SYSTEM WILL BE NO LONGER MAINTAINED.

Be sure to have the Apache Ant v.1.7.x+ installed and included in PATH.

For specific informations about Ant see: http://ant.apache.org/

Execute the following commands:

    $ cd trunk/

    $ ant -p

This will show you the list of available build targets:

It will appear something as below:

  clean
  compile
  compile.tests
  copy
  dist
  init
  init.tests
  jar
  javadoc
  tar
  test
  war
  zip

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

To generate the project site locally execute the following command:

    $ MAVEN_OPTS='-Xmx512m' mvn clean site

You can speed up the site generation process specifying the offline option ( -o ),
but it works only if all the involved plugin dependencies has been already downloaded
in the local M2 repository:

    $ MAVEN_OPTS='-Xmx512m' mvn -o clean site


EOF

