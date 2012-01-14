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

import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.WebCrawler;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * A basic <em>site crawler</em> to extract semantic content
 * of small/medium size sites.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class SiteCrawler {

    public static final String DEFAULT_PAGE_FILTER_RE =
        ".*(\\.(" +
                    "css|js"                            +
                    "|bmp|gif|jpe?g|png|tiff?"          +
                    "|mid|mp2|mp3|mp4|wav|wma"          +
                    "|avi|mov|mpeg|ram|m4v|wmv|rm|smil" +
                    "|pdf"        +
                    "|swf"        +
                    "|zip|rar|gz" +
                    "|xml|txt"    +
        "))$";

    /**
     * Default number of crawler instances.
     */
    public static final int DEFAULT_NUM_OF_CRAWLERS = 10;

    /**
     * Default crawler implementation.
     */
    public static final Class<? extends WebCrawler> DEFAULT_WEB_CRAWLER = DefaultWebCrawler.class;

    /**
     * Default filter applied to skip contents.
     */
    public final Pattern defaultFilters = Pattern.compile(DEFAULT_PAGE_FILTER_RE);

    /**
     * The crawler threads controller.
     */
    private final CrawlController controller;

    /**
     * Crawler listeners.
     */
    private final List<CrawlerListener> listeners = new ArrayList<CrawlerListener>();

    /**
     * Actual number of crawler instances.
     */
    private int numOfCrawlers = DEFAULT_NUM_OF_CRAWLERS;

    /**
     * Actual web crawler.
     */
    private Class<? extends WebCrawler> webCrawler = DEFAULT_WEB_CRAWLER;

    /**
     * Max allowed depth, <code>-1</code> means no limit.
     */
    private int maxDepth = -1;

    /**
     *  Max allowed pages, <code>-1</code> means no limit.
     */
    private int maxPages = -1;

    /**
     * Subsequent call politeness delay, <code>-1</code> means no limit.
     */
    private int politenessDelay = -1;

    /**
     * Internal executor service.
     */
    private ExecutorService service;

    /**
     * Constructor.
     *
     * @param storageFolder location used to store the temporary data structures used by the crawler.
     */
    public SiteCrawler(File storageFolder) {
        try {
            controller = new CrawlController( storageFolder.getAbsolutePath() );
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while initializing crawler controller.", e);
        }
    }

    /**
     * @return number of crawler instances.
     */
    public int getNumOfCrawlers() {
        return numOfCrawlers;
    }

    /**
     * Sets the number of crawler instances.
     *
     * @param n an integer &gt;= 0.
     */
    public void setNumOfCrawlers(int n) {
        if(n <=0) throw new IllegalArgumentException("Invalid number of crawlers, must be > 0 .");
        this.numOfCrawlers = n;
    }

    public Class<? extends WebCrawler> getWebCrawler() {
        return webCrawler;
    }

    /**
     * Sets the actual crawler clas.
     *
     * @param c a not <code>class</code>.
     */
    public void setWebCrawler(Class<? extends WebCrawler> c) {
        if(c == null) throw new NullPointerException("c cannot be null.");
        this.webCrawler = c;
    }

    /**
     * @return the max allowed crawl depth, <code>-1</code> means no limit.
     */
    public int getMaxDepth() {
        return maxDepth;
    }

    /**
     * Sets the maximum depth.
     *
     * @param maxDepth maximum allowed depth. <code>-1</code> means no limit.
     */
    public void setMaxDepth(int maxDepth) {
        if(maxDepth < -1 || maxDepth == 0) throw new IllegalArgumentException("Invalid maxDepth, must be -1 or > 0");
        if(maxDepth > 0) try {
            controller.setMaximumCrawlDepth(maxDepth);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while setting maxDepth.", e);
        }
        this.maxDepth = maxDepth;
    }

    /**
     * @return max number of allowed pages.
     */
    public int getMaxPages() {
        return maxPages;
    }

    /**
     * Sets the maximum collected pages.
     *
     * @param maxPages maximum allowed pages. <code>-1</code> means no limit.
     */
    public void setMaxPages(int maxPages) {
        if(maxPages < -1 || maxPages == 0) throw new IllegalArgumentException("Invalid maxPages, must be -1 or > 0");
        if(maxPages > 0) controller.setMaximumPagesToFetch(maxPages);
        this.maxPages = maxPages;
    }

    /**
     * @return the politeness delay in milliseconds.
     */
    public int getPolitenessDelay() {
        return politenessDelay;
    }

    /**
     * Sets the politeness delay. <code>-1</code> means no politeness.
     *
     * @param millis delay in milliseconds.
     */
    public void setPolitenessDelay(int millis) {
        if(millis < -1) throw new IllegalArgumentException("Invalid politenessDelay, must be >= -1");
        if(millis >= 0) controller.setPolitenessDelay(millis);
        this.politenessDelay = millis;
    }

    /**
     * Registers a {@link CrawlerListener} to this crawler.
     *
     * @param listener
     */
    public void addListener(CrawlerListener listener) {
        listeners.add(listener);
    }

    /**
     * Deregisters a {@link CrawlerListener} from this crawler.
     *
     * @param listener
     */
    public void removeListener(CrawlerListener listener) {
        listeners.remove(listener);
    }

    /**
     * Starts the crawling process.
     *
     * @param seed the starting URL for the crawler process.
     * @param filters filters to be applied to the crawler process. Can be <code>null</code>.
     * @param wait if <code>true</code> the process will wait for the crawler termination.
     * @throws Exception
     */
    public synchronized void start(
            final URL seed, final Pattern filters, final boolean wait
    ) throws Exception {
        SharedData.setCrawlData(seed.toExternalForm(), filters, Collections.synchronizedList(listeners) );
        controller.addSeed(seed.toExternalForm());
        final Runnable internalRunnable = new Runnable() {
            @Override
            public void run() {
                controller.start(getWebCrawler(), getNumOfCrawlers());
            }
        };
        if(wait) {
            internalRunnable.run();
        } else {
            if(service != null) throw new IllegalStateException("Another service seems to run.");
            service = Executors.newSingleThreadExecutor();
            service.execute(internalRunnable);
        }
    }

    /**
     * Starts the crawler process with the {@link #defaultFilters}.
     *
     * @param seed the starting URL for the crawler process.
     * @param wait if <code>true</code> the process will wait for the crawler termination.
     * @throws Exception
     */
    public void start(final URL seed, final boolean wait) throws Exception {
        start(seed, defaultFilters, wait);
    }

    /**
     * Interrupts the crawler process if started with <code>wait</code> flag == <code>false</code>.
     */
    public synchronized void stop() {
        service.shutdownNow();
    }

}
