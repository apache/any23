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

package org.apache.any23.plugin;

import org.apache.any23.cli.Tool;
import org.apache.any23.configuration.DefaultConfiguration;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.ExtractorGroup;
import org.apache.any23.extractor.ExtractorRegistry;
import org.apache.any23.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * The <i>Any23PluginManager</i> is responsible for inspecting
 * dynamically the classpath and retrieving useful classes.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Any23PluginManager {

    /**
     * Any23 Command Line Interface package.
     */
    public static final String CLI_PACKAGE = Tool.class.getPackage().getName();

    /**
     * Any23 Plugins package.
     */
    public static final String PLUGINS_PACKAGE = ExtractorPlugin.class.getPackage().getName();

    /**
     * Property where look for plugins.
     */
    public static final String PLUGIN_DIRS_PROPERTY = "any23.plugin.dirs";

    /**
     * List separator for the string declaring the plugin list.
     */
    public static final String PLUGIN_DIRS_LIST_SEPARATOR = ":";

    /**
     * Internal logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Any23PluginManager.class);

    /**
     * Singleton lazy instance.
     */
    private static final Any23PluginManager instance = new Any23PluginManager();

    /**
     * Internal class loader used to dynamically load classes.
     */
    private final DynamicClassLoader dynamicClassLoader;

    /**
     * @return a singleton instance of {@link Any23PluginManager}.
     */
    public static synchronized Any23PluginManager getInstance() {
        return instance;
    }

    /**
     * Constructor.
     */
    private Any23PluginManager() {
        dynamicClassLoader = new DynamicClassLoader();
    }

    /**
     * Loads a <i>JAR</i> file in the classpath.
     *
     * @param jar the JAR file to be loaded.
     * @return <code>true</code> if the JAR is added for the first time to the classpath,
     *         <code>false</code> otherwise.
     * @throws MalformedURLException
     */
    public synchronized boolean loadJAR(File jar) {
        if(jar == null) throw new NullPointerException("jar file cannot be null.");
        if (!jar.isFile() && !jar.exists()) {
            throw new IllegalArgumentException(
                    String.format("Invalid JAR [%s], must be an existing file.", jar.getAbsolutePath())
            );
        }
        return dynamicClassLoader.addJAR(jar);
    }

    /**
     * Loads a list of <i>JAR</i>s in the classpath.
     *
     * @param jars list of JARs to be loaded.
     * @return list of exceptions raised during the loading.
     */
    public synchronized Throwable[] loadJARs(File... jars) {
        final List<Throwable> result = new ArrayList<Throwable>();
        for (File jar : jars) {
            try {
                loadJAR(jar);
            } catch (Throwable t) {
                result.add(
                        new IllegalArgumentException(
                                String.format("Error while loading jar [%s]", jar.getAbsolutePath()),
                                t
                        )
                );
            }
        }
        return result.toArray(new Throwable[result.size()]);
    }

    /**
     * Loads a <i>classes</i> directory in the classpath.
     *
     * @param classDir the directory to be loaded.
     * @return <code>true</code> if the directory is added for the first time to the classpath,
     *         <code>false</code> otherwise.
     */
    public synchronized boolean loadClassDir(File classDir) {
        if(classDir == null) throw new NullPointerException("classDir cannot be null.");
        if (!classDir.isDirectory() && !classDir.exists()) {
            throw new IllegalArgumentException(
                    String.format("Invalid class dir [%s], must be an existing file.", classDir.getAbsolutePath())
            );
        }
        return dynamicClassLoader.addClassDir(classDir);
    }

    /**
     * Loads a list of class dirs in the classpath.
     *
     * @param classDirs list of class dirs to be loaded.
     * @return  list of exceptions raised during the loading.
     */
    public synchronized Throwable[] loadClassDirs(File... classDirs) {
        final List<Throwable> result = new ArrayList<Throwable>();
        for (File classDir : classDirs) {
            try {
                loadClassDir(classDir);
            } catch (Throwable t) {
                result.add(
                        new IllegalArgumentException(
                                String.format("Error while loading class dir [%s]", classDir.getAbsolutePath()),
                                t
                        )
                );
            }
        }
        return result.toArray(new Throwable[result.size()]);
    }

    /**
     * Loads all the JARs detected in a given directory.
     *
     * @param jarDir directory containing the JARs to be loaded.
     * @return <code>true</code> if all JARs in dir are loaded.
     */
    public synchronized boolean loadJARDir(File jarDir) {
        if(jarDir == null)
            throw new NullPointerException("JAR dir must be not null.");
        if(  ! jarDir.exists() )
            throw new IllegalArgumentException("Given directory doesn't exist:" + jarDir.getAbsolutePath());
        if(! jarDir.isDirectory() )
            throw new IllegalArgumentException(
                    "given file exists and it is not a directory: " + jarDir.getAbsolutePath()
            );
        boolean loaded = true;
        for (File jarFile : jarDir.listFiles(
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".jar");
                    }
                })
        ) {
            loaded &= loadJAR(jarFile);
        }
        return loaded;
    }

    /**
     * Loads a generic list of files, trying to determine the type of every file.
     *
     * @param files list of files to be loaded.
     * @return list of errors occurred during loading.
     */
    public synchronized Throwable[] loadFiles(File... files) {
        final List<Throwable> errors = new ArrayList<Throwable>();
        for(File file : files) {
            try {
                if (file.isFile() && file.getName().endsWith(".jar")) {
                    loadJAR(file);
                } else if (file.isDirectory()) {
                    if (file.getName().endsWith("classes")) {
                        loadClassDir(file);
                    } else {
                        loadJARDir(file);
                    }
                } else {
                    throw new IllegalArgumentException("Cannot handle file " + file.getAbsolutePath());
                }
            } catch (Throwable t) {
                errors.add(t);
            }
        }
        return errors.toArray(new Throwable[errors.size()]);
    }

    /**
     * Returns all classes within the specified <code>packageName</code> satisfying the given class
     * <code>filter</code>. The search is performed on the static classpath (the one the application
     * started with) and the dynamic classpath (the one specified using the load methods).
     *
     * @param <T> type of filtered class.
     * @param packageName package name to look at classes, if <code>null</code> all packages will be found.
     * @param filter class filter to select classes, if <code>null</code> all classes will be returned.
     * @return list of matching classes.
     * @throws IOException
     */
    public synchronized <T> Set<Class<T>> getClassesInPackage(final String packageName, final ClassFilter filter)
    throws IOException {
        final Set<Class<T>> result = new HashSet<Class<T>>();
        loadClassesInPackageFromClasspath(packageName, filter, result);
        for(File jar : dynamicClassLoader.jars) {
            loadClassesInPackageFromJAR(jar, packageName, filter, result);
        }
        for(File dir : dynamicClassLoader.dirs) {
            loadClassesInPackageFromDir(dir, packageName, filter, result);
        }
        return result;
    }

    /**
     * Returns the list of all the {@link Tool} classes declared within the classpath.
     *
     * @return not <code>null</code> list of tool classes.
     * @throws IOException
     */
    public synchronized Class<Tool>[] getTools() throws IOException {
        final Set<Class<Tool>> result = getClassesInPackage(
                CLI_PACKAGE,
                new ClassFilter() {
                    @Override
                    public boolean accept(Class clazz) {
                        return !clazz.equals(Tool.class) && Tool.class.isAssignableFrom(clazz);
                    }
                }
        );
        return result.toArray( new Class[result.size()] );
    }

    /**
     * List of {@link ExtractorPlugin} classes declared within the classpath.
     *
     * @return not <code>null</code> list of plugin classes.
     * @throws IOException
     */
    public synchronized Class<ExtractorPlugin>[] getPlugins() throws IOException {
        final Set<Class<ExtractorPlugin>> result = getClassesInPackage(
                PLUGINS_PACKAGE,
                new ClassFilter() {
                    @Override
                    public boolean accept(Class clazz) {
                        return !clazz.equals(ExtractorPlugin.class) && ExtractorPlugin.class.isAssignableFrom(clazz);
                    }
                }
        );
        return result.toArray( new Class[result.size()] );
    }

    /**
     * Configures a new list of extractors containing the extractors declared in <code>initialExtractorGroup</code>
     * and also the extractors detected in classpath specified by <code>pluginLocations</code>.
     *
     * @param initialExtractorGroup initial list of extractors.
     * @param pluginLocations
     * @return full list of extractors.
     * @throws java.io.IOException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public synchronized ExtractorGroup configureExtractors(
            final ExtractorGroup initialExtractorGroup,
            final File... pluginLocations
    ) throws IOException, IllegalAccessException, InstantiationException {
        if(initialExtractorGroup == null) throw new NullPointerException("inExtractorGroup cannot be null");

        final StringBuilder report = new StringBuilder();
        try {
            report.append("\nLoading plugins from locations {\n");
            for (File pluginLocation : pluginLocations) {
                report.append(pluginLocation.getAbsolutePath()).append('\n');
            }
            report.append("}\n");

            final Throwable[] errors = loadFiles(pluginLocations);
            if (errors.length > 0) {
                report.append("The following errors occurred while loading plugins {\n");
                for (Throwable error : errors) {
                    report.append(error);
                    report.append("\n\n\n");
                }
                report.append("}\n");
            }

            final Class<ExtractorPlugin>[] extractorPluginClasses = getPlugins();
            if (extractorPluginClasses.length == 0) {
                report.append("\n=== No plugins have been found.===\n");
                return initialExtractorGroup;
            } else {
                report.append("\nThe following plugins have been found {\n");
                final List<ExtractorFactory<?>> newFactoryList = new ArrayList<ExtractorFactory<?>>();
                for (Class<ExtractorPlugin> extractorPluginClass : extractorPluginClasses) {
                    final ExtractorPlugin extractorPlugin = extractorPluginClass.newInstance();
                    newFactoryList.add(extractorPlugin.getExtractorFactory());
                    report.append(
                            extractorPlugin.getExtractorFactory().getExtractorName()
                    ).append("\n");
                }
                report.append("}\n");

                for(ExtractorFactory extractorFactory : initialExtractorGroup) {
                    newFactoryList.add(extractorFactory);
                }
                return new ExtractorGroup(newFactoryList);
            }
        } finally {
            logger.info(report.toString());
        }
    }

    /**
     * Configures a new list of extractors containing the extractors declared in <code>initialExtractorGroup</code>
     * and also the extractors detected in classpath specified by the default configuration.
     *
     * @param initialExtractorGroup initial list of extractors.
     * @return full list of extractors.
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public synchronized ExtractorGroup configureExtractors(ExtractorGroup initialExtractorGroup)
    throws IOException, InstantiationException, IllegalAccessException {
        final String pluginDirs = DefaultConfiguration.singleton().getPropertyOrFail(PLUGIN_DIRS_PROPERTY);
        final File[] pluginLocations = getPluginLocations(pluginDirs);
        return configureExtractors(initialExtractorGroup, pluginLocations);
    }

    /**
     * Returns an extractor group containing both the default extractors declared by the
     * {@link org.apache.any23.extractor.ExtractorRegistry} and the {@link ExtractorPlugin}s.
     *
     * @param pluginLocations optional list of plugin locations.
     * @return a not <code>null</code> and not empty extractor group.
     * @throws java.io.IOException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public synchronized ExtractorGroup getApplicableExtractors(File... pluginLocations)
    throws IOException, IllegalAccessException, InstantiationException {
        final ExtractorGroup defaultExtractors = ExtractorRegistry.getInstance().getExtractorGroup();
        return configureExtractors(defaultExtractors, pluginLocations);
    }

    /**
     * Filters classes by criteria within a <i>JAR</i>.
     *
     * @param jarFile file addressing the JAR.
     * @param packageName name of package to scan.
     * @param filter filter class, all returned classes must extend the specified one.
     * @param result list for writing result.
     * @throws java.io.IOException
     */
    protected <T> void loadClassesInPackageFromJAR(
            File jarFile,
            String packageName,
            ClassFilter filter,
            Set<Class<T>> result
    ) throws IOException {
        loadJAR(jarFile);
        packageName = packageName.replaceAll("\\.", "/");
        JarInputStream jarInputStream = new JarInputStream(new FileInputStream(jarFile));
        JarEntry jarEntry;
        while (true) {
            try {
                jarEntry = jarInputStream.getNextJarEntry();
            } catch (IOException ioe) {
                throw new IllegalStateException("Error while accessing JAR.", ioe);
            }
            if (jarEntry == null) {
                break;
            }
            final String jarEntryName = jarEntry.getName();
            if (jarEntryName.startsWith(packageName) && isValidClassName(jarEntryName)) {
                final String classEntry = jarEntryName.replaceAll("/", "\\.");
                final String classStr = classEntry.substring(0, classEntry.indexOf(".class"));
                final Class clazz;
                try {
                    clazz = Class.forName(classStr, true, dynamicClassLoader);
                } catch (ClassNotFoundException cnfe) {
                    throw new IllegalStateException("Error while creating class.", cnfe);
                }
                if (filter == null || filter.accept(clazz)) {
                    result.add(clazz);
                }
            }
        }
    }

    /**
     * Filters classes by criteria within a <i>class dir</i>.
     *
     * @param classDir class directory.
     * @param packageName name of package to scan.
     * @param filter filter class, all returned classes must extend the specified one.
     * @param result list for writing result.
     * @param <T> class types.
     * @throws MalformedURLException
     */
    protected <T> void loadClassesInPackageFromDir(
            File classDir,
            final String packageName,
            final ClassFilter filter,
            Set<Class<T>> result
    ) throws MalformedURLException {
        if(packageName != null && packageName.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid packageName filter '" + packageName + "'");
        }
        loadClassDir(classDir);
        final int PREFIX_LENGTH = classDir.getAbsolutePath().length();
        File[] classFiles = FileUtils.listFilesRecursively(
                classDir,
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        if (!isValidClassName(name)) return false;
                        if (packageName == null) return true;
                        final String absolutePath = dir.getAbsolutePath();
                        if (absolutePath.length() <= PREFIX_LENGTH) return false;
                        return
                                absolutePath
                                        .substring(PREFIX_LENGTH + 1)
                                        .replaceAll("/", "\\.")
                                        .startsWith(packageName);
                    }
                }
        );
        final int classDirPathLength = classDir.getAbsolutePath().length();
        for (File classFile : classFiles) {
            final Class clazz;
            try {
                String className =
                        classFile
                                .getAbsolutePath()
                                .substring(classDirPathLength + 1);
                className = className.substring(0, className.length() - ".class".length()).replaceAll("/", "\\.");
                clazz = Class.forName(className, true, dynamicClassLoader);
            } catch (ClassNotFoundException cnfe) {
                throw new IllegalStateException("Error while instantiating class.", cnfe);
            }
            if (filter == null || filter.accept(clazz)) {
                result.add(clazz);
            }
        }
    }

    /**
     * Filters classes by criteria within the initialization <i>classpath</i>.
     *
     * @param packageName name of package to scan.
     * @param filter filter class, all returned classes must extend the specified one.
     * @param result list for writing result.
     * @param <T>
     * @throws IOException
     */
    protected <T> void loadClassesInPackageFromClasspath(
            final String packageName,
            final ClassFilter filter,
            Set<Class<T>> result
    ) throws IOException {
        final String[] classpathEntries = getClasspathEntries();
        for (String classPathEntry : classpathEntries) {
            if(classPathEntry.trim().length() == 0) continue;
            final File codePath = new File(URLDecoder.decode(classPathEntry, "UTF-8"));
            if( ! codePath.exists() ) continue;
            if (codePath.isDirectory()) {
                loadClassesInPackageFromDir(codePath, packageName, filter, result);
            } else {
                loadClassesInPackageFromJAR(codePath, packageName, filter, result);
            }
        }
    }

    /**
     * @return the classpath entries.
     */
    private String[] getClasspathEntries() {
        final String classpath          = System.getProperty("java.class.path");
        assert classpath != null : "Class path is null.";
        final String classpathSeparator = System.getProperty("path.separator");
        assert classpathSeparator != null : "Class path separator is null.";
        return classpath.split("\\" + classpathSeparator);
    }

    /**
     * Checks if the class name is valid.
     *
     * @param clazzName
     * @return
     */
    private boolean isValidClassName(String clazzName) {
        return clazzName.endsWith(".class") && ! clazzName.contains("$");
    }

    /**
     * Converts a column separated list of dirs in a list of files.
     *
     * @param pluginDirsList
     * @return
     */
    private File[] getPluginLocations(String pluginDirsList) {
        final String[] locationsStr = pluginDirsList.split(PLUGIN_DIRS_LIST_SEPARATOR);
        final List<File> locations = new ArrayList<File>();
        for(String locationStr : locationsStr) {
            final File location = new File(locationStr);
            if( ! location.exists()) {
                throw new IllegalArgumentException(
                        String.format("Plugin location '%s' cannot be found.", locationStr)
                );
            }
            locations.add(location);
        }
        return locations.toArray(new File[locations.size()]);
    }

    /**
     * Dynamic local file class loader.
     */
    private class DynamicClassLoader extends URLClassLoader {

        private final Set<String> addedURLs = new HashSet<String>();

        private final List<File> jars;

        private final List<File> dirs;

        public DynamicClassLoader(URL[] urls) {
            super(urls);
            jars = new ArrayList<File>();
            dirs = new ArrayList<File>();
        }

        public DynamicClassLoader() {
            this(new URL[0]);
        }

        public boolean addClassDir(File classDir) {
            final String urlPath = "file://" + classDir.getAbsolutePath() + "/";
            try {
                if( addURL(urlPath) ) {
                    dirs.add(classDir);
                    return true;
                }
                return false;
            } catch (MalformedURLException murle) {
                throw new RuntimeException("Invalid dir URL.", murle);
            }
        }

        public boolean addJAR(File jar) {
            final String urlPath = "jar:file://" + jar.getAbsolutePath() + "!/";
            try {
                if (addURL(urlPath)) {
                    jars.add(jar);
                    return true;
                }
                return false;
            } catch (MalformedURLException murle) {
                throw new RuntimeException("Invalid JAR URL.", murle);
            }
        }

        private boolean addURL(String urlPath) throws MalformedURLException {
            if(addedURLs.contains(urlPath)) {
                return false;
            }
            super.addURL(new URL(urlPath));
            addedURLs.add(urlPath);
            return true;
        }
    }

}
