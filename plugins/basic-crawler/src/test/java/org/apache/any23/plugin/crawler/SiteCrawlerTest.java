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

package org.apache.any23.plugin.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import org.apache.any23.Any23OnlineTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

/**
 * Test case for {@link SiteCrawler}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class SiteCrawlerTest extends Any23OnlineTestBase {

    public static final Logger logger = LoggerFactory.getLogger(SiteCrawlerTest.class);

    /**
     * Tests the main crawler use case.
     *
     * @throws Exception if there is an error asserting test data
     */
    @Test
    public void testSiteCrawling() throws Exception {
        assumeOnlineAllowed();

        File tmpFile = File.createTempFile("site-crawler-test", ".storage");
        tmpFile.delete();

        final SiteCrawler controller = new SiteCrawler(tmpFile);
        controller.setMaxPages(100);
        logger.info("Crawler4j: Setting max num of pages to: " + controller.getMaxPages());
        controller.setPolitenessDelay(500);
        logger.info("Crawler4j: Setting Politeness delay to: " + controller.getPolitenessDelay() + "ms");

        final Set<String> distinctPages = new HashSet<String>();
        controller.addListener(new CrawlerListener() {
            @Override
            public void visitedPage(Page page) {
                distinctPages.add( page.getWebURL().getURL() );
                Iterator<String> it = distinctPages.iterator();
                while (it.hasNext()) {
                    logger.info("Crawler4j: Fetching page - " + it.next());
                }
            }
        });

        controller.start( new URL("http://any23.apache.org/"), false);

        synchronized (this) {
            this.wait(15 * 1000);
        }
        controller.stop();

        logger.info("Distinct pages: " + distinctPages.size());
        Assert.assertTrue("Expected some page crawled.", distinctPages.size() > 0);
    }

}
