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
import org.apache.commons.cli.PosixParser;
import org.deri.any23.Any23;
import org.deri.any23.LogUtil;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionParameters;
import org.deri.any23.filter.IgnoreAccidentalRDFa;
import org.deri.any23.filter.IgnoreTitlesOfEmptyDocuments;
import org.deri.any23.writer.BenchmarkTripleHandler;
import org.deri.any23.writer.LoggingTripleHandler;
import org.deri.any23.writer.NQuadsWriter;
import org.deri.any23.writer.NTriplesWriter;
import org.deri.any23.writer.RDFXMLWriter;
import org.deri.any23.writer.ReportingTripleHandler;
import org.deri.any23.writer.TripleHandler;
import org.deri.any23.writer.TripleHandlerException;
import org.deri.any23.writer.TurtleWriter;
import org.deri.any23.writer.URIListWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import static org.deri.any23.extractor.ExtractionParameters.ValidationMode;

/**
 * A default rover implementation. Goes and fetches a URL using an hint
 * as to what format should require, then tries to convert it to RDF.
 *
 * @author Gabriele Renzi
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@ToolRunner.Description("Any23 Command Line Tool.")
public class Rover implements Tool {

    // Supported formats.
    private static final String TURTLE_FORMAT  = "turtle";
    private static final String NTRIPLE_FORMAT = "ntriples";
    private static final String RDFXML_FORMAT  = "rdfxml";
    private static final String NQUADS_FORMAT  = "nquads";
    private static final String URIS_FORMAT    = "uris";

    private static final String DEFAULT_FORMAT = TURTLE_FORMAT;

    private static final Logger logger = LoggerFactory.getLogger(Rover.class);

    private static Options options;

    public static void main(String[] args) {
        System.exit( new Rover().run(args) );
    }

    public int run(String[] args) {
        final CommandLineParser parser = new PosixParser();
        final CommandLine commandLine;

        boolean verbose = false;
        try {
            options = createOptions();
            commandLine = parser.parse(options, args);

            if (commandLine.hasOption("h")) {
                printHelp();
                return 0;
            }

            if (commandLine.hasOption('v')) {
                verbose = true;
                LogUtil.setVerboseLogging();
            } else {
                LogUtil.setDefaultLogging();
            }

            if (commandLine.getArgs().length < 1) {
                printHelp();
                throw new IllegalArgumentException("Expected at least 1 argument.");
            }

            final String[] inputURIs      = argumentsToURIs(commandLine.getArgs());
            final String[] extractorNames = getExtractors(commandLine);

            PrintStream outputStream    = null;
            TripleHandler tripleHandler = null;
            try {
                outputStream  = getOutputStream(commandLine);

                tripleHandler = getTripleHandler(commandLine, outputStream);

                tripleHandler = decorateWithLogHandler(commandLine, tripleHandler);

                tripleHandler = decorateWithStatisticsHandler(commandLine, tripleHandler);
                final BenchmarkTripleHandler benchmarkTripleHandler =
                        tripleHandler instanceof BenchmarkTripleHandler ? (BenchmarkTripleHandler) tripleHandler : null;

                tripleHandler = decorateWithAccidentalTriplesFilter(commandLine, tripleHandler);

                final ReportingTripleHandler reportingTripleHandler = new ReportingTripleHandler(tripleHandler);

                final ExtractionParameters eps = getExtractionParameters(commandLine);

                final Any23 any23 = createAny23(extractorNames);

                final long start = System.currentTimeMillis();
                for(String inputURI : inputURIs) {
                    performExtraction(any23, eps, inputURI, reportingTripleHandler);
                }
                final long elapsed = System.currentTimeMillis() - start;

                closeAll(tripleHandler, outputStream);

                if (benchmarkTripleHandler != null) {
                    System.err.println( benchmarkTripleHandler.report() );
                }

                logger.info("Extractors used: " + reportingTripleHandler.getExtractorNames());
                logger.info(reportingTripleHandler.getTotalTriples() + " triples, " + elapsed + "ms");
            } finally {
                closeAll(tripleHandler, outputStream);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            final int exitCode = e instanceof SpecificExitException ? ((SpecificExitException) e).exitCode : 1;
            if(verbose) e.printStackTrace(System.err);
            return exitCode;
        }
        return 0;
    }

    private Options createOptions() {
        final Options options = new Options();
        options.addOption(
                new Option("v", "verbose", false, "show debug and progress information")
        );
        options.addOption(
                new Option("h", "help", false, "print this help")
        );
        options.addOption(
                new Option("e", true, "comma-separated list of extractors, e.g. rdf-xml,rdf-turtle")
        );
        options.addOption(
                new Option("o", "output", true, "output file (defaults to standard output)")
        );
        options.addOption(
                new Option(
                        "f",
                        "Output format",
                        true,
                        "[" +
                                TURTLE_FORMAT  + " (default), " +
                                NTRIPLE_FORMAT + ", " +
                                RDFXML_FORMAT  + ", " +
                                NQUADS_FORMAT  + ", " +
                                URIS_FORMAT    +
                        "]"
                )
        );
        options.addOption(
                new Option("t", "notrivial", false, "filter trivial statements")
        );
        options.addOption(
                new Option("s", "stats", false, "print out extraction statistics")
        );
        options.addOption(
                new Option("l", "log", true, "produces log within a file")
        );
        options.addOption(
                new Option("p", "pedantic", false, "validate and fixes HTML content detecting commons issues")
        );
        options.addOption(
                new Option("n", "nesting", false, "disable production of nesting triples")
        );
        return options;
    }

    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("[{<url>|<file>}]+", options, true);
    }

    private String argumentToURI(String uri) {
        uri = uri.trim();
        if (uri.toLowerCase().startsWith("http:") || uri.toLowerCase().startsWith("https:")) {
            try {
                return new URL(uri).toString();
            } catch (MalformedURLException murle) {
                throw new IllegalArgumentException(String.format("Invalid URI: '%s'", uri), murle);
            }
        }

        final File f = new File(uri);
        if (!f.exists()) {
            throw new IllegalArgumentException(String.format("No such file: [%s]", f.getAbsolutePath()));
        }
        if (f.isDirectory()) {
            throw new IllegalArgumentException(String.format("Found a directory: [%s]", f.getAbsolutePath()));
        }
        return f.toURI().toString();
    }

    protected String[] argumentsToURIs(String[] args) {
        final String[] uris = new String[args.length];
        for(int i = 0; i < args.length; i++) {
            uris[i] = argumentToURI(args[i]);
        }
        return uris;
    }

    private String[] getExtractors(CommandLine cl) {
         if (cl.hasOption('e')) {
             return cl.getOptionValue('e').split(",");
         }
         return null;
     }

    private PrintStream openPrintStream(String fileName) {
        final File file = new File(fileName);
        try {
            return new PrintStream(file);
        } catch (FileNotFoundException fnfe) {
            throw new IllegalArgumentException("Cannot open file '" + file.getAbsolutePath() + "'", fnfe);
        }
    }

    private PrintStream getOutputStream(CommandLine cl) {
        if (cl.hasOption("o")) {
            final String fileName = cl.getOptionValue("o");
            return openPrintStream(fileName);
        } else {
            return System.out;
        }
    }

    private TripleHandler getTripleHandler(CommandLine cl, OutputStream os) {
        final String FORMAT_OPTION = "f";
        String format = DEFAULT_FORMAT;
        if (cl.hasOption(FORMAT_OPTION)) {
            format = cl.getOptionValue(FORMAT_OPTION);
        }
        final TripleHandler outputHandler;
        if (TURTLE_FORMAT.equalsIgnoreCase(format)) {
            outputHandler = new TurtleWriter(os);
        } else if (NTRIPLE_FORMAT.equalsIgnoreCase(format)) {
            outputHandler = new NTriplesWriter(os);
        } else if (RDFXML_FORMAT.equalsIgnoreCase(format)) {
            outputHandler = new RDFXMLWriter(os);
        } else if (NQUADS_FORMAT.equalsIgnoreCase(format)) {
            outputHandler = new NQuadsWriter(os);
        } else if (URIS_FORMAT.equalsIgnoreCase(format)) {
            outputHandler = new URIListWriter(os);
        } else {
            throw new IllegalArgumentException(
                    String.format("Invalid option value '%s' for option %s", format, FORMAT_OPTION)
            );
        }
        return outputHandler;
    }

    private TripleHandler decorateWithAccidentalTriplesFilter(CommandLine cl, TripleHandler in) {
        if (cl.hasOption('t')) {
            return new IgnoreAccidentalRDFa(
                    new IgnoreTitlesOfEmptyDocuments(in),
                    true    // suppress stylesheet triples.
            );
        }
        return in;
    }

    private TripleHandler decorateWithStatisticsHandler(CommandLine cl, TripleHandler in) {
        if (cl.hasOption('s')) {
            return new BenchmarkTripleHandler(in);
        }
        return in;
    }

    private TripleHandler decorateWithLogHandler(CommandLine cl, TripleHandler in) {
        if (cl.hasOption('l')) {
            File logFile = new File(cl.getOptionValue('l'));
            try {
                return new LoggingTripleHandler(in, new PrintWriter(logFile));
            } catch (FileNotFoundException fnfe) {
                throw new IllegalArgumentException( String.format("Could not write to log file [%s]", logFile), fnfe );
            }
        }
        return in;
    }

    private ExtractionParameters getExtractionParameters(CommandLine cl) {
        final boolean nestingDisabled = ! cl.hasOption('n');
        return
                cl.hasOption('p')
                        ?
                new ExtractionParameters(ValidationMode.ValidateAndFix, nestingDisabled)
                        :
                new ExtractionParameters(ValidationMode.None          , nestingDisabled);
    }

    private Any23 createAny23(String[] extractorNames) {
        Any23 any23 = (extractorNames == null || extractorNames.length == 0)
                ? new Any23()
                : new Any23(extractorNames);
        any23.setHTTPUserAgent(Any23.DEFAULT_HTTP_CLIENT_USER_AGENT + "/" + Any23.VERSION);
        return any23;
    }

    private void performExtraction(
            Any23 any23, ExtractionParameters eps, String target, TripleHandler th
    ) {
        try {
            if (! any23.extract(eps, target, th).hasMatchingExtractors()) {
                throw new SpecificExitException("No suitable extractors found.", 2);
            }
        } catch (ExtractionException ex) {
            throw new SpecificExitException("Exception while extracting metadata.", ex, 3);
        } catch (IOException ex) {
            throw new SpecificExitException("Exception while producing output.", ex, 4);
        }
    }

    private void closeHandler(TripleHandler th) {
        if(th == null) return;
        try {
            th.close();
        } catch (TripleHandlerException the) {
            throw new SpecificExitException("Error while closing TripleHandler", the, 5);
        }
    }

    private void closeAll(TripleHandler th, PrintStream os) {
             closeHandler(th);
            if(os != null) os.close();
    }

    private class SpecificExitException extends RuntimeException {

        private final int exitCode;

        public SpecificExitException(String message, Throwable cause, int exitCode) {
            super(message, cause);
            this.exitCode = exitCode;
        }
        public SpecificExitException(String message, int exitCode) {
            super(message);
            this.exitCode = exitCode;
        }
    }

}
