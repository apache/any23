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

Apache Any23 Service ${project.version} (${implementation.build.tstamp})

  What is it?
  -----------

  ${project.description}

  Documentation
  -------------

  The most up-to-date documentation can be found at ${project.parent.url}.

  Release Notes
  -------------

  The full list of changes can be found at ${project.parent.url}/service.html.

  System Requirements
  -------------------

  JDK:
    ${javac.target.version} or above. (see http://www.oracle.com/technetwork/java/)
  Memory:
    No minimum requirement.
  Disk:
    No minimum requirement.
  Operating System:
    No minimum requirement. On Windows, Windows NT and above or Cygwin is required for
    the startup scripts. Tested on Windows XP, Fedora Core and Mac OS X.

  Installing Apache Any23 Service
  ----------------

** Windows 2000/XP

  1) Unzip the distribution archive, i.e. apache-${project.build.finalName}-bin.zip to the directory you wish to
        install Apache Any23 ${project.version}.
        These instructions assume you chose C:\Program Files\Apache Software Foundation.
        The subdirectory apache-${project.build.finalName} will be created from the archive.

  2) Add the ANY23_HOME environment variable by opening up the system properties (WinKey + Pause),
        selecting the "Advanced" tab, and the "Environment Variables" button, then adding the ANY23_HOME
        variable in the user variables with the value
        C:\Program Files\Apache Software Foundation\apache-${project.build.finalName}.

  3) In the same dialog, add the ANY23 environment variable in the user variables with the value %ANY23_HOME%\bin.

  4) Optional: In the same dialog, add the EXTRA_JVM_ARGUMENTS environment variable in the user variables to specify
        JVM properties, e.g. the value -Xms256m -Xmx512m. This environment variable can be used to supply extra options.
        By default, it is set to: -Xms500m -Xmx500m -XX:-UseGCOverheadLimit

  5) In the same dialog, update/create the Path environment variable in the user variables and prepend the value
        %ANY23% to add Apache Any23 available in the command line.

  6) In the same dialog, make sure that JAVA_HOME exists in your user variables or in the system variables and it is
        set to the location of your JDK, e.g. C:\Program Files\Java\jdk1.5.0_02 and that %JAVA_HOME%\bin is in your Path
        environment variable.

  7) Open a new command prompt (Winkey + R then type cmd) and run any23server to launch the service.

** Unix-based Operating Systems (Linux, Solaris and Mac OS X)

  1) Extract the distribution archive, i.e. apache-${project.build.finalName}-bin.tar.gz to the directory you wish to
        install Apache Any23 ${project.version}.
        These instructions assume you chose /usr/local/apache-any23.
        The subdirectory apache-${project.build.finalName} will be created from the archive.

  2) In a command terminal, add the ANY23_HOME environment variable, e.g.
        export ANY23_HOME=/usr/local/apache-any23/apache-${project.build.finalName}.

  3) Add the ANY23 environment variable, e.g. export ANY23=$ANY23_HOME/bin.

  4) Optional: Add the EXTRA_JVM_ARGUMENTS environment variable to specify JVM properties, e.g.
        export EXTRA_JVM_ARGUMENTS="-Xms256m -Xmx512m".
        This environment variable can be used to supply extra options.

  5) Add ANY23 environment variable to your path, e.g. export PATH=$ANY23:$PATH.

  6) Make sure that JAVA_HOME is set to the location of your JDK, e.g.
        export JAVA_HOME=/usr/java/jdk1.5.0_02 and that $JAVA_HOME/bin is in your PATH environment variable.

  7) Run any23server to launch the service.

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
