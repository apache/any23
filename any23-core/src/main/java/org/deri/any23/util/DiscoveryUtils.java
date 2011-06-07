/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * This class provides utility methods
 * for discovering classes in packages.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DiscoveryUtils {

    /**
     * Scans all classes accessible from the context class loader
     * which belong to the given package and sub-packages.
     *
     * @param packageName the root package.
     * @return list of matching classes.
     * @throws IOException
     */
    public static List<Class> getClassesInPackage(String packageName) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        final String path = packageName.replace('.', '/');
        final Enumeration<URL> resources;
        try {
            resources = classLoader.getResources(path);
        } catch (IOException ioe) {
            throw new IllegalStateException("Error while retrieving internal resource path.", ioe);
        }
        final List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            final URL resource = resources.nextElement();
            final String fileName = resource.getFile();
            final String fileNameDecoded;
            try {
                fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException uee) {
                throw new IllegalStateException("Error while decoding class file name.", uee);
            }
            dirs.add( new File(fileNameDecoded) );
        }
        final ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName) );
        }
        return classes;
    }

    /**
     * Scans all classes accessible from the context class loader
     * which belong to the given package and sub-packages and filter
     * them by ones implementing the specified interface <code>iface</code>.
     *
     * @param packageName the root package.
     * @param filter the interface/class filter.
     * @return list of matching classes.
     */
    public static List<Class> getClassesInPackage(String packageName, Class filter) {
        final List<Class> classesInPackage = getClassesInPackage(packageName);
        final List<Class> result = new ArrayList<Class>();
        for(Class clazz : classesInPackage) {
            if(clazz.equals(filter)) {
                continue;
            }
            if( clazz.getSuperclass().equals(filter) || contains(clazz.getInterfaces(), filter) ) {
                result.add(clazz);
            }
        }
        return result;
    }

    /**
     * Recursive method used to find all classes in a given directory and sub-dirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     */
    private static List<Class> findClasses(File directory, String packageName)
    {
        final List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                assert !fileName.contains(".");
                classes.addAll(findClasses(file, packageName + "." + fileName));
            } else if (fileName.endsWith(".class") && !fileName.contains("$")) {
                try {
                    Class clazz;
                    try {
                        clazz = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6));
                    } catch (ExceptionInInitializerError e) {
                        /*
                        happen, for example, in classes, which depend on Spring to inject some beans,
                        and which fail, if dependency is not fulfilled
                        */
                        clazz = Class.forName(
                                packageName + '.' + fileName.substring(0, fileName.length() - 6),
                                false,
                                Thread.currentThread().getContextClassLoader()
                        );
                    }
                    classes.add(clazz);
                } catch (ClassNotFoundException cnfe) {
                    throw new IllegalStateException("Error while loading detected class.", cnfe);
                }
            }
        }
        return classes;
    }

    private static boolean contains(Object[] list, Object t) {
        for(Object o : list) {
            if( o.equals(t) ) {
                return true;
            }
        }
        return false;
    }

    private DiscoveryUtils(){}

}
