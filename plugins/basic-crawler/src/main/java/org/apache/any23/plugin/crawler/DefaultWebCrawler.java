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
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Default {@link WebCrawler} implementation.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultWebCrawler extends WebCrawler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultWebCrawler.class);

    /**
     * Shared data reference.
     */
    private final SharedData sharedData = SharedData.getInstance();

    /**
     * Page filter pattern.
     */
    private final Pattern pattern = sharedData.getPattern();

    /**
     * Override this method to specify whether the given URL should be visited or not.
     */

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        if (!super.shouldVisit(referringPage, url))
            return false;
        if (url.getURL() == null)
            return false;
        final String href = url.getURL().toLowerCase();
        if (!href.startsWith(sharedData.getSeed()))
            return false;
        return pattern == null || !pattern.matcher(href).matches();
    }

    /**
     * Override this method to implement the single page processing logic.
     */
    @Override
    public void visit(Page page) {
        logger.trace("Visiting page: " + page.getWebURL().getURL());
        sharedData.notifyPage(page);
    }

}

