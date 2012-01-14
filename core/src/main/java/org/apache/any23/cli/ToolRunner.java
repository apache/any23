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

import org.apache.any23.plugin.Any23PluginManager;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class is the main class responsible to provide a uniform command-line
 * access points to all the others tools like {@link Rover}.
 *
 * @see ExtractorDocumentation
 * @see Rover
 */
@ToolRunner.Skip
public class ToolRunner {

    public static final File HOME_PLUGIN_DIR = new File(
            new File(System.getProperty("user.home")),
            ".any23/plugins"
    );

    private static final String USAGE = String.format(
            "Usage: %s <utility> [options...]",
            ToolRunner.class.getSimpleName()
    );

    public static void main(String[] args) throws IOException {
        //Generate automatically the cli.
        final Class<Tool>[] tools = getToolsInClasspath();
        try {
            if (args.length < 1) {
                usage(null, tools);
            }

            final String toolName = args[0];
            Class<Tool> targetTool = null;
            for(Class<Tool> tool : tools) {
                if(tool.getSimpleName().equals(toolName)) {
                    targetTool = tool;
                    break;
                }
            }
            if(targetTool == null) {
                usage( String.format("[%s] is not a valid tool name.", toolName), tools);
                throw new IllegalStateException();
            }

            String[] mainArgs = new String[args.length - 1];
            System.arraycopy(args, 1, mainArgs, 0, mainArgs.length);
            final Tool targetToolInstance = targetTool.newInstance();
            targetToolInstance.run(mainArgs);
        } catch (Throwable e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            if(cause != null) cause.printStackTrace();
            usage(e.toString(), null);
        }
    }

    public static Class<Tool>[] getToolsInClasspath() throws IOException {
        final Any23PluginManager pluginManager =  Any23PluginManager.getInstance();
        if(HOME_PLUGIN_DIR.exists()) {
            pluginManager.loadJARDir(HOME_PLUGIN_DIR);
        }
        return pluginManager.getTools();
    }

    private static String padLeft(String s, int n) {
        return String.format("%1$#" + n + "s", s);
    }

    private static String getUtilitiesMessage(Class<Tool>[] toolClasses) {
        StringBuffer sb = new StringBuffer();
        sb.append(" where <utility> is one of:\n");
        Description description;
        String utilityName;
        int padding;
        for (Class<Tool> toolClass :  toolClasses) {
            utilityName = toolClass.getSimpleName();
            sb.append("\t").append(utilityName);
            description = toolClass.getAnnotation(Description.class);
            padding = 100 - utilityName.length();
            if (description != null) {
                sb.append( padLeft( description.value(), padding >= 0 ? padding : 0) );
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    private static void usage(String msg, Class<Tool>[] utilities) {
        if(msg != null) {
            System.err.println("*** ERROR: " + msg);
            System.err.println();
        }
        System.err.println(USAGE);
        if(utilities != null) {
            System.err.println( getUtilitiesMessage(utilities) );
        }
        System.exit(1);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Skip {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Description { String value();  }

}

