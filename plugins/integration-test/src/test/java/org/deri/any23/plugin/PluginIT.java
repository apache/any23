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

import org.deri.any23.extractor.ExtractorGroup;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

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

    @Test
    public void testGetApplicableExtractors() throws IOException, InstantiationException, IllegalAccessException {
        final ExtractorGroup extractorGroup = manager.getApplicableExtractors(
                HTML_SCRAPER_TARGET_DIR,
                HTML_SCRAPER_DEPENDENCY_DIR,  // Required to satisfy class dependencies.
                OFFICE_SCRAPER_TARGET_DIR,
                OFFICE_SCRAPER_DEPENDENCY_DIR // Required to satisfy class dependencies.
        );
        Assert.assertEquals(
                NUM_OF_EXTRACTORS + 2,        // HTMLScraper Plugin, OfficeScraper Plugin.
                extractorGroup.getNumOfExtractors()
        );
    }

}
