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

import org.deri.any23.Any23;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.ExtractorGroup;
import org.deri.any23.source.StringDocumentSource;
import org.deri.any23.util.StreamUtils;
import org.deri.any23.writer.NQuadsWriter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

/**
 * Test case for {@link Any23PluginManager}
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Any23PluginManagerTest {

    /**
     * The plugin is build by the maven-invoker-plugin, see note
     * 'Assemblies the HTML Scraper plugin' in module POM.
     */
    private static final String TEST_PLUGIN_LOCATION =
            "./target/plugins/target/html-scraper-1.0.0-SNAPSHOT-extractor-plugin.jar";

    private Any23PluginManager pluginManager;

    @Before
    public void setUp() {
        pluginManager = Any23PluginManager.getInstance();
    }

    @After
    public void tearDown() {
        pluginManager.shutDown();
        pluginManager = null;
    }

    @Test
    public void testLoadPlugins() {
        final Throwable[] errors = pluginManager.loadPlugins(
                new File(TEST_PLUGIN_LOCATION) );
        for(Throwable error : errors) {
            error.printStackTrace();
        }
        Assert.assertEquals("Unexpected error.", 0, errors.length);
    }

    @Test
    public void testGetExtractorPlugins() {
        testLoadPlugins();
        final ExtractorPlugin[] extractorPlugins = pluginManager.getExtractorPlugins();
        Assert.assertNotNull(extractorPlugins);
        Assert.assertEquals("Unexpected number of plugins.", 1, extractorPlugins.length);
    }

    @Test
    public void testConfigureExtractors() {
        System.setProperty(
                Any23PluginManager.PLUGIN_DIRS_PROPERTY,
                TEST_PLUGIN_LOCATION
        );
        final ExtractorGroup eg = pluginManager.configureExtractors(
                new ExtractorGroup( Collections.<ExtractorFactory<?>>emptyList())
        );

        Assert.assertFalse(eg.isEmpty());
        final Iterator<ExtractorFactory<?>> iter = eg.iterator();
        final ExtractorFactory ef = iter.next();
        Assert.assertNotNull(ef);
        Assert.assertFalse(iter.hasNext());
        Assert.assertEquals("html-scraper", ef.getExtractorName());
    }

    @Test
    public void testHTMLScraper() throws IOException, ExtractionException {
        final Any23 any23 = new Any23( pluginManager.getApplicableExtractors() );
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final String in = StreamUtils.asString(
                this.getClass().getResourceAsStream("html-scraper-integration-test.html")
        );
        any23.extract(
                new StringDocumentSource(in, "http://html/scaper/integration/test"),
                new NQuadsWriter(baos)
        );

        final String nQuadsStr = baos.toString();
        Assert.assertTrue(nQuadsStr.contains("<http://vocab.sindice.net/pagecontent/de>"));
        Assert.assertTrue(nQuadsStr.contains("<http://vocab.sindice.net/pagecontent/ae>"));
        Assert.assertTrue(nQuadsStr.contains("<http://vocab.sindice.net/pagecontent/lce>"));
        Assert.assertTrue(nQuadsStr.contains("<http://vocab.sindice.net/pagecontent/ce>"));
    }

}
