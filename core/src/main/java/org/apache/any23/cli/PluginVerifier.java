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

import java.io.File;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.mime.MIMEType;
import org.apache.any23.plugin.Any23PluginManager;
import org.apache.any23.plugin.Author;
import org.apache.any23.plugin.ExtractorPlugin;
import org.kohsuke.MetaInfServices;

/**
 * Commandline utility to verify the <b>Any23</b> plugins
 * and extract basic information.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@MetaInfServices
@ToolRunner.Description("Utility for plugin management verification.")
public class PluginVerifier implements Tool {

    private Any23PluginManager pluginManager = Any23PluginManager.getInstance();

    public static void main(String[] args) throws MalformedURLException {
        System.exit( new PluginVerifier().run(args) );
    }

    public int run(String[] args) {
        if(args.length != 1) {
            printHelp("Invalid argument.");
            return 1;
        }

        final File pluginsDir = new File(args[0]);
        if(!pluginsDir.isDirectory()) {
            printHelp("<plugins-dir> must be a valid dir.");
            return 2;
        }

        final Iterator<ExtractorPlugin> plugins;
        try{
            pluginManager.loadJARDir(pluginsDir);
            plugins = pluginManager.getExtractors();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return 3;
        }
        while (plugins.hasNext()) {
            System.out.println("-----------------------------");
            printPluginData(plugins.next().getClass(), System.out);
            System.out.println("-----------------------------");
        }
        return 0;
    }

    private void printHelp(String msg) {
        System.err.println("***ERROR: " + msg);
        System.err.println("Usage: " + this.getClass().getSimpleName() + " <plugins-dir>");
    }

    private String getMimeTypesStr(Collection<MIMEType> mimeTypes) {
        final StringBuilder sb = new StringBuilder();
        for(MIMEType mt : mimeTypes) {
            sb.append(mt).append(' ');
        }
        return sb.toString();
    }

    private void printPluginData(Class<? extends ExtractorPlugin> extractorPlugin, PrintStream ps) {
        final Author authorAnnotation = extractorPlugin.getAnnotation(Author.class);
        final ExtractorPlugin instance;
        try {
            instance = extractorPlugin.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Error while instantiating plugin.", e);
        }
        final ExtractorFactory<?> extractorFactory = instance.getExtractorFactory();
        ps.printf("Plugin class     : %s\n", extractorPlugin.getClass());
        ps.printf("Plugin author    : %s\n", authorAnnotation == null ? "<unknown>" : authorAnnotation.name());
        ps.printf("Plugin factory   : %s\n", extractorFactory.getClass());
        ps.printf("Plugin mime-types: %s\n", getMimeTypesStr( extractorFactory.getSupportedMIMETypes() ));
    }

}
