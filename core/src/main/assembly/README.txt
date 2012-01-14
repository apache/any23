  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

Apache Any23 (${implementation.build}; ${maven.build.timestamp})

  What is it?
  -----------

  ${project.description}

  Documentation
  -------------

  The most up-to-date documentation can be found at ${project.parent.url}.

  Release Notes
  -------------

  The full list of changes can be found at ${project.parent.url}/changes-report.html.

  System Requirements
  -------------------

  JDK:
    ${javac.target.version} or above.
  Memory:
    No minimum requirement.
  Disk:
    No minimum requirement.
  Operating System:
    No minimum requirement. On Windows, Windows NT and above or Cygwin is required for
    the startup scripts. Tested on Windows XP, Fedora Core and Mac OS X.

  Installing Apache Any23
  ----------------

  1) Unpack the archive where you would like to store the binaries, eg:

    Unix-based Operating Systems (Linux, Solaris and Mac OS X)
      tar zxvf ${project.build.finalName}.tar.gz
    Windows 2000/XP
      unzip ${project.build.finalName}.zip

  2) A directory called "apache-${project.build.finalName}" will be created.

  3) Add the bin directory to your PATH, eg:

    Unix-based Operating Systems (Linux, Solaris and Mac OS X)
      export PATH=/usr/local/apache-${project.build.finalName}/bin:$PATH
    Windows 2000/XP
      set PATH="c:\program files\apache-${project.build.finalName}\bin";%PATH%

  4) Make sure JAVA_HOME is set to the location of your JDK

  5) Run "any23 --version" to verify that it is correctly installed.

  Licensing
  ---------

  Please see the file called LICENSE.TXT

  Apache Any23 URLS
  ----------

  Home Page:          ${project.parent.url}/
  Downloads:          ${project.parent.url}/download.html
  Release Notes:      ${project.parent.url}/changes-report.html
  Mailing Lists:      ${project.parent.url}/mail-lists.html
  Source Code:        ${project.parent.scm.url}
  Issue Tracking:     ${project.issueManagement.url}
  Available Plugins:  ${project.parent.url}/plugins.html
