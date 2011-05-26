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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.deri.any23.Any23;
import org.deri.any23.Configuration;
import org.deri.any23.LogUtil;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionParameters;
import org.deri.any23.filter.IgnoreAccidentalRDFa;
import org.deri.any23.filter.IgnoreTitlesOfEmptyDocuments;
import org.deri.any23.writer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A default rover implementation. Goes and fetches a URL using an hint
 * as to what format should require, then tries to convert it to RDF.
 *
 * @author Gabriele Renzi
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Rover {

    private static final String USER_AGENT_NAME = Configuration.instance().getPropertyOrFail("any23.http.user.agent.name");

    //output writer constants
    private static final String TURTLE = "turtle";

    private static final String QUAD    = "quad";

    private static final String NTRIPLE = "ntriples";

    private static final String RDFXML  = "rdfxml";

    private static final String URIS  = "uris";    

    private static final Logger logger = LoggerFactory.getLogger(Rover.class);

    private static Options options;

    public static void main(String[] args) {
        options = new Options();
        options.addOption(
                new Option(
                        "f",
                        "Output format",
                        true,
                        "[" + TURTLE + " (default), " + NTRIPLE + ", " + RDFXML + ", " + QUAD + ", " + URIS + "]")
        );
        options.addOption(new Option("e", true, "comma-separated list of extractors, e.g. rdf-xml,rdf-turtle"));
        options.addOption(new Option("o", "output", true, "ouput file (defaults to stdout)"));
        options.addOption(new Option("p", "pedantic", false, "validates and fixes HTML content detecting commons issues"));
        options.addOption(new Option("t", "notrivial", false, "filter trivial statements"));
        options.addOption(new Option("n", "nesting", false, "disable production of nesting triples"));
        options.addOption(new Option("s", "stats", false, "print out statistics of Any23"));
        options.addOption(new Option("l", "log", true, "logging, please specify a file"));
        options.addOption(new Option("v", "verbose", false, "show progress and debug information"));
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        if (cmd.hasOption('v')) {
            LogUtil.setVerboseLogging();
        } else {
            LogUtil.setDefaultLogging();
        }

        if (cmd.hasOption("h")) {
            printHelp();
            System.exit(0);
        }
        if (cmd.getArgs().length != 1) {
            printHelp();
            System.exit(-1);
        }

        String inputURI = argumentToURI(cmd.getArgs()[0]);

        String[] extractorNames = null;
        if (cmd.hasOption('e')) {
            extractorNames = cmd.getOptionValue('e').split(",");
        }

        String format = TURTLE;
        if (cmd.hasOption("f")) {
            format = cmd.getOptionValue("f");
        }
        TripleHandler outputHandler;
        if (TURTLE.equalsIgnoreCase(format)) {
            outputHandler = new TurtleWriter(System.out);
        } else if (NTRIPLE.equalsIgnoreCase(format)) {
            outputHandler = new NTriplesWriter(System.out);
        } else if (QUAD.equalsIgnoreCase(format)) {
            outputHandler = new NQuadsWriter(System.out);
        } else if (URIS.equalsIgnoreCase(format)) {
            outputHandler = new URIListWriter(System.out);
        }
        else {
            outputHandler = new RDFXMLWriter(System.out);
        }

        BenchmarkTripleHandler benchmark = null;
        if (cmd.hasOption('s')) {
            benchmark = new BenchmarkTripleHandler(outputHandler);
            outputHandler = benchmark;
        }
        if (cmd.hasOption('l')) {
            File logFile = new File(cmd.getOptionValue('l'));
            try {
                outputHandler = new LoggingTripleHandler(outputHandler, new PrintWriter(logFile));
            } catch (FileNotFoundException ex) {
                System.err.println("Could not write to " + logFile + ": " + ex.getMessage());
                System.exit(1);
            }
        }
        ReportingTripleHandler reporter = new ReportingTripleHandler(outputHandler);
        outputHandler = reporter;
        if (cmd.hasOption('t')) {
            outputHandler = new IgnoreAccidentalRDFa(
                    new IgnoreTitlesOfEmptyDocuments(outputHandler),
                    true // suppress stylesheet triples.
            );
        }

        final boolean nestingDisabled = !cmd.hasOption('n');

        final ExtractionParameters eps =
                cmd.hasOption('p')
                        ?
                new ExtractionParameters(true, true, nestingDisabled)
                        :
                new ExtractionParameters(false, false, nestingDisabled);

        long start = System.currentTimeMillis();
        Any23 any23 = (extractorNames == null || extractorNames.length == 0) ? new Any23() : new Any23(extractorNames);
        any23.setHTTPUserAgent(USER_AGENT_NAME + "/" + Any23.VERSION);
        try {
            if ( ! any23.extract(eps, inputURI, outputHandler).hasMatchingExtractors() ) {
                System.err.println("No suitable extractors");
                System.exit(2);
            }
        } catch (ExtractionException ex) {
            logger.debug("Exception in Any23", ex);
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
            System.exit(3);
        } catch (IOException ex) {
            logger.debug("Exception in Any23", ex);
            System.err.println(ex.getMessage());
            System.exit(4);
        }
        try {
            outputHandler.close();
        } catch (TripleHandlerException e) {
            logger.debug("Exception in Any23", e);
            System.err.println(e.getMessage());
            System.exit(4);
        }
        if (benchmark != null) {
            System.err.println(benchmark.report());
        }
        logger.debug("Extractors used: " + reporter.getExtractorNames());
        long elapsed = System.currentTimeMillis() - start;
        logger.info(reporter.getTotalTriples() + " triples, " + elapsed + "ms");
    }

    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("{<url>|<file>}", options, true);
    }

    private static String argumentToURI(String arg) {
        arg = arg.trim();
        if (arg.toLowerCase().startsWith("http:") || arg.toLowerCase().startsWith("https:")) {
            try {
                return new URL(arg).toString();
            } catch (MalformedURLException ex) {
                System.err.println("Malformed URL: " + ex + "(" + ex.getMessage() + ")");
                System.exit(-1);
            }
        }
        File f = new File(arg);
        if (!f.exists()) {
            System.err.println(f.toString() + ": No such file");
            System.exit(-1);
        }
        if (f.isDirectory()) {
            System.err.println(f.toString() + " is a directory");
            System.exit(-1);
        }
        return f.toURI().toString();
    }
    
}
