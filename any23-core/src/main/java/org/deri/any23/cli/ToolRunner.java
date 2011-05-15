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
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * This class is the main class responsible to provide a uniform command-line
 * access points to all the others tools like {@link org.deri.any23.cli.Rover}.
 *
 * @see org.deri.any23.cli.ExtractorDocumentation
 * @see org.deri.any23.cli.Rover
 */
@ToolRunner.Skip
public class ToolRunner {

    private static final String USAGE = "Usage: " + ToolRunner.class.getSimpleName() + " <utility> [options...]";
    private static final String PREFIX = "org.deri.any23.cli.";

    public static void main(String[] args) {
        if(args.length == 0) {
            usage("Missing JAR file location.", null);
        }

        //generate automatically the cli.
        List<Class> utilities = getClasseNamesInPackage(args[0], "org.deri.any23.cli");
        try {
            if (args.length < 2) {
                usage( getUtilitiesMessage(utilities), utilities );
            }

            final String className = args[1];
            final Class<?> cls;
            try {
                cls = Class.forName(PREFIX + className);
            } catch (ClassNotFoundException cnfe) {
                usage( String.format("[%s] is not a valid tool name.", className), utilities);
                throw new IllegalStateException();
            }

            Method mainMethod = cls.getMethod("main", new Class[]{String[].class});

            String[] mainArgs = new String[args.length - 2];
            System.arraycopy(args, 2, mainArgs, 0, mainArgs.length);

            mainMethod.invoke(null, new Object[]{mainArgs});
        } catch (Throwable e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            cause.printStackTrace();
            usage(e.toString(), null);
        }
    }

    /**
     * See http://www.rgagnon.com/javadetails/java-0513.html
     *
     * @param jarName
     * @param packageName
     * @return
     */
    public static List<Class> getClasseNamesInPackage(String jarName, String packageName) {
        ArrayList<Class> classes = new ArrayList<Class>();
        packageName = packageName.replaceAll("\\.", "/");
        try {
            System.err.println("Jar " + jarName + " looking for " + packageName);

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
                    final String classStr = classEntry.substring(0, classEntry.indexOf(".class"));
                    final Class clazz = Class.forName(classStr);
                    if(clazz.getAnnotation(Skip.class) != null) {
                        continue;
                    }
                    if(clazz.isInterface()) {
                        continue;
                    }
                    classes.add(clazz);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    private static String getUtilitiesMessage(List<Class> utilities) {
        StringBuffer sb = new StringBuffer();
        sb.append(" where <utility> one of:");
        for (Class util : utilities)
            sb.append("\n\t").append(util.getSimpleName());
        return sb.toString();
    }

    private static void usage(String msg, List<Class> utilities) {
        System.err.println("*** ERROR: " + msg);
        System.err.println();
        System.err.println(USAGE);
        if(utilities != null) {
            System.err.println( getUtilitiesMessage(utilities) );
        }
        System.exit(1);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Skip {}

}

