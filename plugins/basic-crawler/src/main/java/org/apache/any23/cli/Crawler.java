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

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import org.apache.any23.plugin.crawler.CrawlerListener;
import org.apache.any23.plugin.crawler.SiteCrawler;
import org.apache.any23.source.StringDocumentSource;

import java.io.File;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.lang.String.format;

/**
 * Implementation of a <b>CLI crawler</b> based on
 * {@link Rover}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@Parameters(commandNames = "crawler", commandDescription = "Any23 Crawler Command Line Tool.")
public class Crawler extends Rover {

    private final Object roverLock = new Object();

    @Parameter(
       names = { "-pf", "--pagefilter" },
       description = "Regex used to filter out page URLs during crawling.",
       converter = PatterConverter.class
    )
    private Pattern pageFilter = Pattern.compile( SiteCrawler.DEFAULT_PAGE_FILTER_RE );

    @Parameter(
       names = { "-sf", "--storagefolder" },
       description = "Folder used to store crawler temporary data.",
       converter = FileConverter.class
    )
    private File storageFolder = new File(System.getProperty("java.io.tmpdir"), "crawler-metadata-" + UUID.randomUUID().toString());

    @Parameter(names = { "-nc", "--numcrawlers" }, description = "Sets the number of crawlers.")
    private int numCrawlers = SiteCrawler.DEFAULT_NUM_OF_CRAWLERS;

    @Parameter(names = { "-mp", "--maxpages" }, description = "Max number of pages before interrupting crawl.")
    private int maxPages = Integer.MAX_VALUE;

    @Parameter(names = { "-md", "--maxdepth" }, description = "Max allowed crawler depth.")
    private int maxDepth = Integer.MAX_VALUE;

    @Parameter(names = { "-pd", "--politenessdelay" }, description = "Politeness delay in milliseconds.")
    private int politenessDelay = Integer.MAX_VALUE;

    @Override
    public void run() throws Exception {
        super.configure();

        if (inputIRIs.size() != 1) {
            throw new IllegalArgumentException("Expected just one seed.");
        }
        final URL seed = new URL(inputIRIs.get( 0 ));

        if ( storageFolder.isFile() ) {
            throw new IllegalStateException( format( "Storage folder %s can not be a file, must be a directory",
                                                     storageFolder ) );
        }

        if ( !storageFolder.exists() ) {
            if ( !storageFolder.mkdirs() ) {
                throw new IllegalStateException(
                        format( "Storage folder %s can not be created, please verify you have enough permissions",
                                                         storageFolder ) );
            }
        }

        final SiteCrawler siteCrawler = new SiteCrawler( storageFolder );
        siteCrawler.setNumOfCrawlers( numCrawlers );
        siteCrawler.setMaxPages( maxPages );
        siteCrawler.setMaxDepth( maxDepth );
        siteCrawler.setPolitenessDelay(politenessDelay);

        siteCrawler.addListener(new CrawlerListener() {
            @Override
            public void visitedPage(Page page) {
                final String pageURL = page.getWebURL().getURL();
                System.err.println( format("Processing page: [%s]", pageURL) );

                final ParseData parseData = page.getParseData();
                if (parseData instanceof HtmlParseData) {
                    final HtmlParseData htmlParseData = (HtmlParseData) parseData;
                    try {
                        synchronized (roverLock) {
                            Crawler.super.performExtraction(
                                    new StringDocumentSource(
                                            htmlParseData.getHtml(),
                                            pageURL

                                    )
                            );
                        }
                    } catch (Exception e) {
                        System.err.println(format("Error while processing page [%s], error: %s .",
                                                  pageURL, e.getMessage())
                        );
                    }
                }
            }
        });

        Runtime.getRuntime().addShutdownHook( new Thread() {
            @Override
            public void run() {
                try {
                    System.err.println( Crawler.super.printReports() );
                    // siteCrawler.stop(); // TODO: cause shutdown hanging.
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        });
        siteCrawler.start(seed, pageFilter, true);
    }

    public static final class PatterConverter implements IStringConverter<Pattern> {

        @Override
        public Pattern convert( String value ) {
            try {
                return Pattern.compile( value );
            } catch (PatternSyntaxException pse) {
                throw new ParameterException( format("Invalid page filter, '%s' must be a regular expression.", value) );
            }
        }

    }

}
