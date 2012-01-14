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

import java.util.List;
import java.util.regex.Pattern;

/**
 * This class hosts shared data structures accessible
 * to all the {@link DefaultWebCrawler} instances
 * run by the {@link SiteCrawler}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class SharedData {

    /**
     * Singleton instance.
     */
    private static SharedData instance;

    /**
     * Crawl seed.
     */
    private final String seed;

    /**
     * Crawl page filter pattern.
     */
    private final Pattern pattern;

    /**
     * List of crawler listeners.
     */
    private final List<CrawlerListener> listeners;

//    /**
//     * Output triple handler.
//     */
//    private final TripleHandler tripleHandler;

    /**
     * @return the singleton instance.
     */
    protected static SharedData getInstance() {
        if(instance == null) throw new IllegalStateException("The configuration has not yet initialized.");
        return instance;
    }

    /**
     * Initializes the crawler data.
     *
     * @param seed crawler seed.
     * @param regex page filter regex.
     * @param listeners the listeners to be notified of the crawler activity.
     */
    protected static void setCrawlData(String seed, Pattern regex, List<CrawlerListener> listeners) {
        instance = new SharedData(seed, regex, listeners);
    }

    /**
     * Internal constructor.
     *
     * @param seed
     * @param pattern
     * @param listeners
     */
    private SharedData(String seed, Pattern pattern, List<CrawlerListener> listeners) {
        if(seed == null || seed.trim().length() == 0)
            throw new IllegalArgumentException(
                String.format("Invalid seed '%s'", seed)
            );

        this.seed      = seed;
        this.pattern   = pattern;
        this.listeners = listeners;
    }

    /**
     * @return crawl seed.
     */
    protected String getSeed() {
        return seed;
    }

    /**
     * @return page filter pattern.
     */
    protected Pattern getPattern() {
        return pattern;
    }

    /**
     * Notifies all listeners that a page has been discovered.
     *
     * @param page the discovered page.
     */
    protected void notifyPage(Page page) {
        for(CrawlerListener listener : listeners) {
            listener.visitedPage(page);
        }
    }

}
