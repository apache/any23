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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.mime.MIMEType;
import org.apache.any23.plugin.Any23PluginManager;
import org.apache.any23.plugin.Author;
import java.io.File;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Commandline utility to verify the <b>Any23</b> plugins
 * and extract basic information.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@Parameters(commandNames = { "verify" }, commandDescription = "Utility for plugin management verification.")
public class PluginVerifier extends BaseTool {

    private Any23PluginManager pluginManager = Any23PluginManager.getInstance();

    @Parameter(
        description = "plugins-dir",
        converter = FileConverter.class
    )
    private List<File> pluginsDirs = new LinkedList<>();

    private PrintStream out = System.out;

    @Override
    PrintStream getOut() {
        return out;
    }

    @Override
    void setOut(PrintStream out) {
        this.out = out;
    }

    public void run() throws Exception {
        if (pluginsDirs.isEmpty()) {
            throw new IllegalArgumentException("No plugin directory specified.");
        }

        final File pluginsDir = pluginsDirs.get(0);
        if (!pluginsDir.isDirectory()) {
            throw new IllegalArgumentException("<plugins-dir> must be a valid dir.");
        }

        pluginManager.loadJARDir(pluginsDir);

        final Iterator<ExtractorFactory> plugins = pluginManager.getExtractors();

        while (plugins.hasNext()) {
            printPluginData(plugins.next(), out);
            out.println("------------------------------------------------------------------------");
        }
    }

    private String getMimeTypesStr(Collection<MIMEType> mimeTypes) {
        final StringBuilder sb = new StringBuilder();
        for (MIMEType mt : mimeTypes) {
            sb.append(mt).append(' ');
        }
        return sb.toString();
    }

    private void printPluginData(ExtractorFactory<?> extractorFactory, PrintStream ps) {
        final Author authorAnnotation = extractorFactory.getClass().getAnnotation(Author.class);
        ps.printf(Locale.ROOT, "Plugin author    : %s%n", authorAnnotation == null ? "<unknown>" : authorAnnotation.name());
        ps.printf(Locale.ROOT, "Plugin factory   : %s%n", extractorFactory.getClass());
        ps.printf(Locale.ROOT, "Plugin mime-types: %s%n", getMimeTypesStr(extractorFactory.getSupportedMIMETypes()));
    }

}
