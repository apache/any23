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

import org.apache.any23.cli.Crawler;
import org.apache.any23.cli.Tool;
import org.apache.any23.extractor.ExtractorGroup;
import org.apache.any23.extractor.ExtractorRegistryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration test for plugins.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class PluginIT {

    private static final int NUM_OF_EXTRACTORS_INCL_OPENIE = 37;
    
    private static final int NUM_OF_EXTRACTORS_EXCL_OPENIE = 36;

    private static final String PLUGIN_DIR = "target/plugins-build/";

    private static final File HTML_SCRAPER_TARGET_DIR       = new File(PLUGIN_DIR + "html-scraper/target/classes");
    private static final File HTML_SCRAPER_DEPENDENCY_DIR   = new File(PLUGIN_DIR + "html-scraper/target/dependency");

    private static final File OFFICE_SCRAPER_TARGET_DIR     = new File(PLUGIN_DIR + "office-scraper/target/classes");
    private static final File OFFICE_SCRAPER_DEPENDENCY_DIR = new File(PLUGIN_DIR + "office-scraper/target/dependency");

    private static final File CRAWLER_TARGET_DIR     = new File(PLUGIN_DIR + "basic-crawler/target/classes");
    private static final File CRAWLER_DEPENDENCY_DIR = new File(PLUGIN_DIR + "basic-crawler/target/dependency");

    private static final File OPENIE_TARGET_DIR     = new File(PLUGIN_DIR + "openie/target/classes");
    private static final File OPENIE_DEPENDENCY_DIR = new File(PLUGIN_DIR + "openie/target/dependency");

    private Any23PluginManager manager;

    @Before
    public void before() {
        manager = Any23PluginManager.getInstance();
    }

    @After
    public void after() {
        manager = null;
    }

    /**
     * {@link org.apache.any23.extractor.Extractor} plugins detection testing.
     *
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Test
    public void testDetectExtractorPlugins() throws IOException, InstantiationException, IllegalAccessException {
        final ExtractorGroup extractorGroup = manager.getApplicableExtractors(
                new ExtractorRegistryImpl(),
                HTML_SCRAPER_TARGET_DIR,
                HTML_SCRAPER_DEPENDENCY_DIR,
                OFFICE_SCRAPER_TARGET_DIR,
                OFFICE_SCRAPER_DEPENDENCY_DIR,
                OPENIE_TARGET_DIR,
                OPENIE_DEPENDENCY_DIR
        );
        try {
          Class.forName("org.apache.any23.plugin.extractor.openie.OpenIEExtractor", false, this.getClass().getClassLoader());
          assertEquals("Did not find the number of expected extractors", NUM_OF_EXTRACTORS_INCL_OPENIE ,
                  extractorGroup.getNumOfExtractors()
          );
        } catch (ClassNotFoundException e) {
          assertEquals("Did not find the number of expected extractors", NUM_OF_EXTRACTORS_EXCL_OPENIE ,
                  extractorGroup.getNumOfExtractors()
          );
        }
    }

    /**
     * {@link Tool} plugins detection testing.
     *
     * @throws IOException
     */
    @Test
    public void testDetectCLIPlugins() throws IOException {
        final Iterator<Tool> tools = manager.getApplicableTools(CRAWLER_TARGET_DIR, CRAWLER_DEPENDENCY_DIR);
        final Set<String> toolClasses = new HashSet<String>();
        Tool tool;
        while(tools.hasNext()) {
            tool = tools.next();
            assertTrue("Found duplicate tool.", toolClasses.add(tool.getClass().getName()));
        }
        assertTrue(
                String.format(
                        "Expected [%s] plugin to be detected, but it is not found in the built classpath.",
                        Crawler.class.getName()
                ),
                toolClasses.contains(Crawler.class.getName())
        );
        assertEquals(7, toolClasses.size()); // core CLIs
    }

}
