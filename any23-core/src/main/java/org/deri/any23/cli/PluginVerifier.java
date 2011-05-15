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

import net.xeoh.plugins.base.annotations.meta.Author;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.mime.MIMEType;
import org.deri.any23.plugin.Any23PluginManager;
import org.deri.any23.plugin.ExtractorPlugin;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;

/**
 * Commandline utility to verify the <b>Any23</b> plugins
 * and extract basic information.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class PluginVerifier {

    private Any23PluginManager pluginManager = Any23PluginManager.getInstance();

    public static void main(String[] args) {
        new PluginVerifier().run(args);
    }

    private void printHelp(String msg) {
        System.err.println("***ERROR: " + msg);
        System.err.println("Usage: " + this.getClass().getSimpleName() + " <plugins-dir>");
        System.exit(1);
    }

    private void run(String[] args) {
        if(args.length != 1) {
            printHelp("Invalid argument.");
        }

        final File pluginsDir = new File(args[0]);
        if(!pluginsDir.isDirectory()) {
            printHelp("<plugins-dir> must be a valid dir.");
        }

        pluginManager.loadPlugins(pluginsDir);
        final ExtractorPlugin[] plugins = pluginManager.getExtractorPlugins();
        for(ExtractorPlugin p : plugins) {
            System.out.println("-----------------------------");
            printPluginData(p, System.out);
            System.out.println("-----------------------------");
        }
    }

    private String getMimeTypesStr(Collection<MIMEType> mimeTypes) {
        final StringBuilder sb = new StringBuilder();
        for(MIMEType mt : mimeTypes) {
            sb.append(mt).append(' ');
        }
        return sb.toString();
    }

    private void printPluginData(ExtractorPlugin extractorPlugin, PrintStream ps) {
        final Author authorAnnotation = extractorPlugin.getClass().getAnnotation(Author.class);
        final ExtractorFactory extractorFactory = extractorPlugin.getExtractorFactory();
        ps.printf("Plugin class     : %s\n", extractorPlugin.getClass());
        ps.printf("Plugin author    : %s\n", authorAnnotation == null ? "<unknown>" : authorAnnotation.name());
        ps.printf("Plugin factory   : %s\n", extractorFactory.getClass());
        ps.printf("Plugin mime-types: %s\n", getMimeTypesStr( extractorFactory.getSupportedMIMETypes() ));
    }

}
