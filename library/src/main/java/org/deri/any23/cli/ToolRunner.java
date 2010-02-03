/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.cli;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * This class is the main class responsible to provide a uniform command-line
 * access points to all the others tools like {@link org.deri.any23.cli.Eval}.
 *
 * @see org.deri.any23.cli.ExtractorDocumentation
 * @see org.deri.any23.cli.Rover
 */
public class ToolRunner {

    private static final String USAGE = " <utility> [options...]";
    private static final String PREFIX = "org.deri.any23.cli.";

    public static void main(String[] args) {
        //generate automatically the cli.
        List<String> utilities = getClasseNamesInPackage(args[0], "org.deri.any23.cli");

        try {
            if (args.length < 2) {
                StringBuffer sb = new StringBuffer();
                sb.append(" where <utility> one of");
                for (String util : utilities)
                    sb.append("\n\t" + util);
                usage(sb.toString());
            }

            Class<?> cls = Class.forName(PREFIX + args[1]);

            Method mainMethod = cls.getMethod("main", new Class[]{String[].class});

            String[] mainArgs = new String[args.length - 2];
            System.arraycopy(args, 2, mainArgs, 0, mainArgs.length);

            mainMethod.invoke(null, new Object[]{mainArgs});
        } catch (Throwable e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            cause.printStackTrace();
            usage(e.toString());
        }
    }

    /**
     * See http://www.rgagnon.com/javadetails/java-0513.html
     */
    public static List<String> getClasseNamesInPackage(String jarName, String packageName) {
        boolean debug = true;
        ArrayList<String> classes = new ArrayList<String>();
        packageName = packageName.replaceAll("\\.", "/");
        try {
            if (debug) System.err.println
                    ("Jar " + jarName + " looking for " + packageName);

            JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
            JarEntry jarEntry;

            while (true) {
                jarEntry = jarFile.getNextJarEntry();
                if (jarEntry == null) {
                    break;
                }
                if ((jarEntry.getName().startsWith(packageName)) &&
                        (jarEntry.getName().endsWith(".class"))) {
                    String classEntry = jarEntry.getName().replaceAll("/", "\\.");
                    if (debug) System.err.println
                            ("Found " + jarEntry.getName().replaceAll("/", "\\."));


                    classes.add(classEntry.substring("org.deri.any23.cli.".length(), classEntry.indexOf(".class")));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    private static void usage(String msg) {
        System.err.println(USAGE);
        System.err.println(msg);
        System.exit(-1);
    }
}

