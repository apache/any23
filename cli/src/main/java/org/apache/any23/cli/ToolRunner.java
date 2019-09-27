/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.any23.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import org.apache.any23.Any23;
import org.apache.any23.plugin.Any23PluginManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.exit;

/**
 * This class is the main class responsible to provide a uniform command-line
 * access points to all the others tools like {@link Rover}.
 *
 * @see ExtractorDocumentation
 * @see Rover
 */
public final class ToolRunner {

    public static final File DEFAULT_PLUGIN_DIR = new File(new File(System.getProperty("user.home")), ".any23/plugins");

    private static final PrintStream infoStream = System.err;

    @Parameter( names = { "-h", "--help" }, description = "Display help information." )
    private boolean printHelp;

    @Parameter( names = { "-v", "--version" }, description = "Display version information." )
    private boolean showVersion;

    @Parameter( names = { "-X", "--verbose" }, description = "Produce execution verbose output." )
    private boolean verbose;

    @Parameter(
            names = { "--plugins-dir" },
            description = "The Any23 plugins directory.",
            converter = FileConverter.class
    )
    private File pluginsDir = DEFAULT_PLUGIN_DIR;

    public static void main( String[] args ) throws Exception {
        exit( new ToolRunner().execute( args ) );
    }

    public int execute(String... args) throws Exception {
        return execute(false, args);
    }

    int execute(boolean concise, String... args) throws Exception {
        JCommander commander = new JCommander(this);
        commander.setProgramName(System.getProperty("app.name"));

        // TODO (low) : this dirty solution has been introduced because it is not possible to
        //              parse arguments ( commander.parse() ) twice.
        final File pluginsDirOption;
        try {
            pluginsDirOption = parsePluginDirOption(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return 1;
        }
        if(pluginsDirOption != null) {
            pluginsDir = pluginsDirOption;
        }

        // add all plugins first
        final Iterator<Tool> tools = getToolsInClasspath();
        while (tools.hasNext()) {
            Tool tool = tools.next();
            commander.addCommand(tool);
        }

        commander.parse(args);

        Map<String, JCommander> commands = commander.getCommands();
        String parsedCommand = commander.getParsedCommand();

        if (printHelp) {
            commander.usage();
            return 0;
        }

        if (showVersion) {
            printVersionInfo();
            return 0;
        }

        if(parsedCommand == null) {
            infoStream.println("A command must be specified.");
            commander.usage();
            return 1;
        }

        long start = currentTimeMillis();
        int exit = 0;

        Throwable error = null;

        // execute the parsed command
        infoStream.println();
        infoStream.println( "------------------------------------------------------------------------" );
        infoStream.printf(Locale.ROOT, "Apache Any23 :: %s%n", parsedCommand );
        infoStream.println( "------------------------------------------------------------------------" );
        infoStream.println();

        try {
            Tool tool = Tool.class.cast(commands.get(parsedCommand).getObjects().get(0));
            if (tool instanceof BaseTool) {
                ((BaseTool) tool).run(concise);
            } else {
                tool.run();
            }
        } catch (Throwable t) {
            exit = 1;
            error = t;
        } finally {
            infoStream.println();
            infoStream.println( "------------------------------------------------------------------------" );
            infoStream.printf(Locale.ROOT, "Apache Any23 %s%n", ( exit != 0 ) ? "FAILURE" : "SUCCESS" );

            if (exit != 0) {
                infoStream.println();

                if (verbose) {
                    System.err.println( "Execution terminated with errors:" );
                    error.printStackTrace(infoStream);
                } else {
                    infoStream.printf(Locale.ROOT, "Execution terminated with errors: %s%n", error.getMessage() );
                }

                infoStream.println();
            }

            infoStream.printf(Locale.ROOT, "Total time: %ss%n", ( ( currentTimeMillis() - start ) / 1000 ) );
            infoStream.printf(Locale.ROOT, "Finished at: %s%n", new Date() );

            final Runtime runtime = Runtime.getRuntime();
            final int megaUnit = 1024 * 1024;
            infoStream.printf(Locale.ROOT, "Final Memory: %sM/%sM%n", ( runtime.totalMemory() - runtime.freeMemory() ) / megaUnit,
                         runtime.totalMemory() / megaUnit );

            infoStream.println( "------------------------------------------------------------------------" );
        }

        return exit;
    }

    Iterator<Tool> getToolsInClasspath() throws IOException {
        final Any23PluginManager pluginManager =  Any23PluginManager.getInstance();
        if (pluginsDir.exists() && pluginsDir.isDirectory()) {
            pluginManager.loadJARDir(pluginsDir);
        }
        return pluginManager.getTools();
    }

    private static void printVersionInfo() {
        Properties properties = new Properties();
        InputStream input = ToolRunner.class.getClassLoader().getResourceAsStream( "META-INF/maven/org.apache.any23/any23-core/pom.properties" );

        if ( input != null ) {
            try {
                properties.load( input );
            } catch ( IOException e ) {
                // ignore, just don't load the properties
            } finally {
                try {
                    input.close();
                } catch (IOException e) {
                    // close quietly
                }
            }
        }

        infoStream.printf(Locale.ROOT, "Apache Any23 %s%n", Any23.VERSION );
        infoStream.printf(Locale.ROOT, "Java version: %s, vendor: %s%n",
                           System.getProperty( "java.version" ),
                           System.getProperty( "java.vendor" ) );
        infoStream.printf(Locale.ROOT, "Java home: %s%n", System.getProperty( "java.home" ) );
        infoStream.printf(Locale.ROOT, "Default locale: %s_%s, platform encoding: %s%n",
                           System.getProperty( "user.language" ),
                           System.getProperty( "user.country" ),
                           System.getProperty( "sun.jnu.encoding" ) );
        infoStream.printf(Locale.ROOT, "OS name: \"%s\", version: \"%s\", arch: \"%s\", family: \"%s\"%n",
                           System.getProperty( "os.name" ),
                           System.getProperty( "os.version" ),
                           System.getProperty( "os.arch" ),
                           getOsFamily() );
    }

    private static final String getOsFamily() {
        String osName = System.getProperty( "os.name" ).toLowerCase(Locale.ROOT);
        String pathSep = System.getProperty( "path.separator" );

        if (osName.contains("windows")) {
            return "windows";
        } else if (osName.contains("os/2")) {
            return "os/2";
        } else if (osName.contains("z/os") || osName.contains("os/390")) {
            return "z/os";
        } else if (osName.contains("os/400")) {
            return "os/400";
        } else if (";".equals(pathSep)) {
            return "dos";
        } else if (osName.contains("mac")) {
            if (osName.endsWith("x")) {
                return "mac"; // MACOSX
            }
            return "unix";
        } else if (osName.contains("nonstop_kernel")) {
            return "tandem";
        } else if (osName.contains("openvms")) {
            return "openvms";
        } else if (":".equals(pathSep)) {
            return "unix";
        }

        return "undefined";
    }

    private static File parsePluginDirOption(String[] args) {
        int optionIndex = -1;
        for(int i = 0; i < args.length; i++) {
            if("--plugins-dir".equals(args[i])) {
                optionIndex = i;
            }
        }
        if(optionIndex == -1)
            return null;

        if(optionIndex == args.length - 1) {
            throw new IllegalArgumentException("Missing argument for --plugins-dir option.");
        }
        final File pluginsDir = new File( args[optionIndex + 1] );
        if( ! pluginsDir.isDirectory() ) {
            throw  new IllegalArgumentException("Expected a directory for --plugins-dir option value.");
        }
        return pluginsDir;
    }

}
