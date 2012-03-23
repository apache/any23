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

import static org.junit.Assert.*;

import org.apache.any23.cli.Crawler;
import org.apache.any23.cli.Tool;
import org.apache.any23.extractor.ExtractorGroup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Integration test for plugins.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class PluginIT {

    private static final int NUM_OF_EXTRACTORS = 23;

    private static final String PLUGIN_LOCATION = "target/plugins-build/";

    private static final File HTML_SCRAPER_TARGET_DIR     = new File(PLUGIN_LOCATION + "html-scraper/target/classes");
    private static final File HTML_SCRAPER_DEPENDENCY_DIR = new File(PLUGIN_LOCATION + "html-scraper/target/dependency");
    private static final File OFFICE_SCRAPER_TARGET_DIR     = new File(PLUGIN_LOCATION + "office-scraper/target/classes");
    private static final File OFFICE_SCRAPER_DEPENDENCY_DIR = new File(PLUGIN_LOCATION + "office-scraper/target/dependency");

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
     * <i>Extractor</i> plugins detection testing.
     *
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Test
    public void testDetectExtractorPlugins() throws IOException, InstantiationException, IllegalAccessException {
        final ExtractorGroup extractorGroup = manager.getApplicableExtractors(
                HTML_SCRAPER_TARGET_DIR,
                HTML_SCRAPER_DEPENDENCY_DIR,  // Required to satisfy class dependencies.
                OFFICE_SCRAPER_TARGET_DIR,
                OFFICE_SCRAPER_DEPENDENCY_DIR // Required to satisfy class dependencies.
        );
        assertEquals( NUM_OF_EXTRACTORS + 2,        // HTMLScraper Plugin, OfficeScraper Plugin.
                      extractorGroup.getNumOfExtractors()
        );
    }

    /**
     * <i>CLI</i> plugins detection testing.
     *
     * @throws IOException
     */
    @Test
    public void testDetectCLIPlugins() throws IOException {
        final Iterator<Tool> tools = manager.getTools();
        final Set<String> toolClasses = new HashSet<String>();
        Tool tool;
        while(tools.hasNext()) {
            tool = tools.next();
            assertTrue("Found duplicate tool.", toolClasses.add(tool.getClass().getName()));
        }
        assertTrue( "Expected " + Crawler.class.getName() + " plugin be detected, but not found int the built classpath",
                    toolClasses.contains( Crawler.class.getName() ) );
        assertEquals(7 + 1, toolClasses.size());
    }

}
