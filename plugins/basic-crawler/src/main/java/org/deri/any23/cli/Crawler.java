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

package org.deri.any23.cli;

import edu.uci.ics.crawler4j.crawler.Page;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.deri.any23.plugin.crawler.CrawlerListener;
import org.deri.any23.plugin.crawler.SiteCrawler;
import org.deri.any23.source.StringDocumentSource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Implementation of a <b>CLI crawler</b> based on
 * {@link Rover}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@ToolRunner.Description("Any23 Crawler Command Line Tool.")
public class Crawler extends Rover {

    private final Object roverLock = new Object();

    public static void main(String[] args) {
        try {
            System.exit( new Crawler().run(args) );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int run(String[] args) {
        try {
            final String[] seeds = super.configure(args);
            if(seeds.length != 1) throw new IllegalArgumentException("Expected just one seed.");
            final URL seed = new URL(seeds[0]);

            final CommandLine commandLine = super.getCommandLine();

            final SiteCrawler siteCrawler = new SiteCrawler( getStorageFolder(commandLine) );

            final Pattern specifiedPageFilter = getPageFilter(commandLine);
            final Pattern pageFilter = specifiedPageFilter == null ? siteCrawler.defaultFilters : specifiedPageFilter;

            if(commandLine.hasOption("numcrawlers")) {
                siteCrawler.setNumOfCrawlers( parseInt(commandLine, "numcrawlers") );
            }
            if(commandLine.hasOption("maxpages")) {
                siteCrawler.setMaxPages(parseInt(commandLine, "maxpages"));
            }
            if(commandLine.hasOption("maxdepth")) {
                siteCrawler.setMaxDepth(parseInt(commandLine, "maxdepth"));
            }
            if (commandLine.hasOption("politenessdelay")) {
                final int politenessDelay = parseInt(commandLine, "politenessdelay");
                if(politenessDelay >= 0) siteCrawler.setPolitenessDelay(politenessDelay);
            }

            siteCrawler.addListener(new CrawlerListener() {
                @Override
                public void visitedPage(Page page) {
                    final String pageURL = page.getWebURL().getURL();
                    System.err.println( String.format("Processing page: [%s]", pageURL) );
                    try {
                        synchronized (roverLock) {
                            Crawler.super.performExtraction(
                                    new StringDocumentSource(
                                            page.getHTML(),
                                            pageURL

                                    )
                            );
                        }
                    } catch (Exception e) {
                        System.err.println(
                                String.format("Error while processing page [%s], error: %s .", pageURL, e.getMessage())
                        );
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
                        e.printStackTrace();
                    }
                }
            });
            siteCrawler.start(seed, pageFilter, true);
            return 0;
        } catch (Exception e) {
            if(super.isVerbose()) e.printStackTrace();
            if(e instanceof ExitCodeException) {
                return ((ExitCodeException) e).getExitCode();
            }
            return 1;
        }
    }

    @Override
    protected Options createOptions() {
        final Options roverOptions = super.createOptions();
        addCrawlerOptions(roverOptions);
        return roverOptions;
    }

    private void addCrawlerOptions(Options options) {
        options.addOption(
                new Option("pagefilter"     , true, "Regex used to filter out page URLs during crawling. Default: '" + SiteCrawler.DEFAULT_PAGE_FILTER_RE + "'")
        );
        options.addOption(
                new Option("storagefolder"  , true, "Folder used to store crawler temporary data. Default: [" + System.getProperty("java.io.tmpdir")  + "]")
        );
        options.addOption(
                new Option("numcrawlers"    , true, "Sets the number of crawlers. Default: " + SiteCrawler.DEFAULT_NUM_OF_CRAWLERS)
        );
        options.addOption(
                new Option("maxpages"       , true, "Max number of pages before interrupting crawl. Default: no limit.")
        );
        options.addOption(
                new Option("maxdepth"       , true, "Max allowed crawler depth. Default: no limit.")
        );
        options.addOption(
                new Option("politenessdelay", true, "Politeness delay in milliseconds. Default: no limit.")
        );
    }

    private Pattern getPageFilter(CommandLine commandLine) {
        if(commandLine.hasOption("pagefilter")) {
            try {
                return Pattern.compile( commandLine.getOptionValue("pagefilter") );
            } catch (PatternSyntaxException pse) {
                throw new ExitCodeException("Invalid page filter, must be a regular expression.", 6);
            }
        }
        return null;
    }

    private File getStorageFolder(CommandLine commandLine) throws IOException {
        if(commandLine.hasOption("storagefolder")) {
           final File candidate = new  File( commandLine.getOptionValue("storagefolder") );
           if(candidate.exists() && candidate.isFile())
               throw new IllegalArgumentException("The storage folder must be a directory.");
            return candidate;
        } else {
            final File tmpDir = File.createTempFile("crawler-metadata-" + UUID.randomUUID().toString(), "db");
            tmpDir.delete();
            return tmpDir;
        }
    }

    private int parseInt(CommandLine cl, String option) {
        final String value = cl.getOptionValue(option);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(String.format("Expected integer for %s found '%s' .", option, value));
        }
    }

}
