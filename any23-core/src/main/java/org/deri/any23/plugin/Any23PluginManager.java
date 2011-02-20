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

package org.deri.any23.plugin;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import org.deri.any23.Configuration;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.ExtractorGroup;
import org.deri.any23.extractor.ExtractorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The <i>Any23PluginManager</i> is responsible for loading {@link org.deri.any23.Any23}
 * {ExtractorPlugin}s at startup.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Any23PluginManager {

    /**
     * Property where look for plugins.
     */
    public static final String PLUGIN_DIRS_PROPERTY = "any23.plugin.dirs";

    /**
     * Plugins list separator.
     */
    public static final String PLUGIN_DIRS_LIST_SEPARATOR = ":";

    private static final Logger logger = LoggerFactory.getLogger(Any23PluginManager.class);

    private static Any23PluginManager instance;

    /**
     * @return a singleton instance of {@link Any23PluginManager}.
     */
    public static synchronized Any23PluginManager getInstance() {
        if(instance == null) {
            instance = new Any23PluginManager();
        }
        return instance;
    }

    private final PluginManager pluginManager;

    private Any23PluginManager() {
        pluginManager = PluginManagerFactory.createPluginManager();
    }

    /**
     * Loafs all the plugins detected within the given list of JAR files.
     *
     * @param jarFiles list of JAR files containing plugins.
     * @return list of errors raised when loading plugins.
     */
    public Throwable[] loadPlugins(File... jarFiles) {
        final List<Throwable> errors = new ArrayList<Throwable>();
        for(final File jarFile : jarFiles) {
            try {
                if(!jarFile.exists()) {
                    throw new IllegalArgumentException(
                            String.format("File '%s' doesn't exist.", jarFile.getAbsolutePath())
                    );
                }
                pluginManager.addPluginsFrom( jarFile.toURI() );
            } catch (Throwable t) {
                errors.add(t);
            }
        }
        return errors.toArray( new Throwable[errors.size()] );
    }

    /**
     * @return the list of {@link ExtractorPlugin}s detected in classpath.
     */
    // TODO: discover how to retrieve more plugins implementing the same interface.
    public ExtractorPlugin[] getExtractorPlugins() {
        final ExtractorPlugin extractorPlugin = pluginManager.getPlugin(ExtractorPlugin.class);
        return extractorPlugin == null ?  new ExtractorPlugin[]{} : new ExtractorPlugin[] {extractorPlugin};
    }

    /**
     * Configures a new list of extractors containing the extractors declared in <code>inExtractorGroup</code>
     * and also the extractors detected in classpath.
     *
     * @param inExtractorGroup initial list of extractors.
     * @return full list of extractors.
     */
    public ExtractorGroup configureExtractors(final ExtractorGroup inExtractorGroup) {
        final String pluginDirs = Configuration.instance().getProperty(PLUGIN_DIRS_PROPERTY, null);
        if(pluginDirs == null) {
            logger.info( String.format("Property '%s' is not set, no plugins will be loaded.", PLUGIN_DIRS_PROPERTY));
            return inExtractorGroup;
        }

        final StringBuilder report = new StringBuilder();
        try {
            final File[] pluginLocations = getPluginLocations(pluginDirs);
            report.append("\nLoading plugins from locations {\n");
            for (File pluginLocation : pluginLocations) {
                report.append(pluginLocation.getAbsolutePath()).append('\n');
            }
            report.append("}\n");

            final Throwable[] errors = loadPlugins(pluginLocations);
            if (errors.length > 0) {
                report.append("The following errors occurred while loading plugins {\n");
                for (Throwable error : errors) {
                    report.append(error);
                    report.append("\n\n\n");
                }
                report.append("}\n");
            }

            final ExtractorPlugin[] extractorPlugins = getExtractorPlugins();
            if (extractorPlugins.length == 0) {
                report.append("\n=== No plugins have been found.===\n");
                return inExtractorGroup;
            } else {
                report.append("\nThe following plugins have been found {\n");
                final List<ExtractorFactory<?>> newFactoryList = new ArrayList<ExtractorFactory<?>>();
                for (ExtractorPlugin extractorPlugin : extractorPlugins) {
                    newFactoryList.add(extractorPlugin.getExtractorFactory());
                    report.append(
                            extractorPlugin.getExtractorFactory().getExtractorName()
                    ).append("\n");
                }
                report.append("}\n");

                for(ExtractorFactory extractorFactory : inExtractorGroup) {
                    newFactoryList.add(extractorFactory);
                }
                return new ExtractorGroup(newFactoryList);
            }
        } finally {
            logger.info(report.toString());
        }
    }

    /**
     * Returns an extractor group containing both the default extractors declared by the
     * {@link ExtractorRegistry} and the {@link ExtractorPlugin}s.
     *
     * @return a not <code>null</code> and not empty extractor group.
     */
    public ExtractorGroup getApplicableExtractors() {
        final ExtractorGroup defaultExtractors = ExtractorRegistry.getInstance().getExtractorGroup();
        return configureExtractors(defaultExtractors);
    }

    /**
     * Shuts down all the active plugins and the plugin manager.
     */
    public void shutDown() {
        pluginManager.shutdown();
    }

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
        return locations.toArray( new File[locations.size()] );
    }

}
