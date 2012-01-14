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

import org.apache.any23.Any23;
import org.apache.any23.configuration.Configuration;
import org.apache.any23.configuration.DefaultConfiguration;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.SingleDocumentExtraction;
import org.apache.any23.filter.IgnoreAccidentalRDFa;
import org.apache.any23.filter.IgnoreTitlesOfEmptyDocuments;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.util.LogUtils;
import org.apache.any23.writer.BenchmarkTripleHandler;
import org.apache.any23.writer.LoggingTripleHandler;
import org.apache.any23.writer.ReportingTripleHandler;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.apache.any23.writer.WriterRegistry;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.apache.any23.extractor.ExtractionParameters.ValidationMode;

/**
 * A default rover implementation. Goes and fetches a URL using an hint
 * as to what format should require, then tries to convert it to RDF.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @author Gabriele Renzi
 */
@ToolRunner.Description("Any23 Command Line Tool.")
public class Rover implements Tool {

    private static final String[] FORMATS = WriterRegistry.getInstance().getIdentifiers();
    private static final int DEFAULT_FORMAT_INDEX = 0;

    private static final Logger logger = LoggerFactory.getLogger(Rover.class);

    private Options options;

    private CommandLine commandLine;

    private boolean verbose = false;

    private PrintStream outputStream;
    private TripleHandler tripleHandler;
    private ReportingTripleHandler reportingTripleHandler;
    private BenchmarkTripleHandler benchmarkTripleHandler;

    private ExtractionParameters eps;
    private Any23 any23;

    protected boolean isVerbose() {
        return verbose;
    }

    public static void main(String[] args) {
        System.exit( new Rover().run(args) );
    }

    public int run(String[] args) {
        try {
            final String[] uris = configure(args);
            performExtraction(uris);
            return 0;
        } catch (Exception e) {
            System.err.println( e.getMessage() );
            final int exitCode = e instanceof ExitCodeException ? ((ExitCodeException) e).exitCode : 1;
            if(verbose) e.printStackTrace(System.err);
            return exitCode;
        }
    }

    protected CommandLine getCommandLine() {
        if(commandLine == null) throw new IllegalStateException("Rover must be configured first.");
        return commandLine;
    }

    protected String[] configure(String[] args) throws Exception {
        final CommandLineParser parser = new PosixParser();
        options = createOptions();
        commandLine = parser.parse(options, args);

        if (commandLine.hasOption("h")) {
            printHelp();
            throw new ExitCodeException(0);
        }

        if (commandLine.hasOption('v')) {
            verbose = true;
            LogUtils.setVerboseLogging();
        } else {
            LogUtils.setDefaultLogging();
        }

        if (commandLine.getArgs().length < 1) {
            printHelp();
            throw new IllegalArgumentException("Expected at least 1 argument.");
        }

        final String[] inputURIs = argumentsToURIs(commandLine.getArgs());
        final String[] extractorNames = getExtractors(commandLine);

        try {
            outputStream  = getOutputStream(commandLine);
            tripleHandler = getTripleHandler(commandLine, outputStream);
            tripleHandler = decorateWithLogHandler(commandLine, tripleHandler);
            tripleHandler = decorateWithStatisticsHandler(commandLine, tripleHandler);

            benchmarkTripleHandler =
                    tripleHandler instanceof BenchmarkTripleHandler ? (BenchmarkTripleHandler) tripleHandler : null;

            tripleHandler = decorateWithAccidentalTriplesFilter(commandLine, tripleHandler);

            reportingTripleHandler = new ReportingTripleHandler(tripleHandler);
            eps = getExtractionParameters(commandLine);
            any23 = createAny23(extractorNames);

            return inputURIs;
        } catch (Exception e) {
            closeStreams();
            throw e;
        }
    }

    protected Options createOptions() {
        final Options options = new Options();
        options.addOption(
                new Option("v", "verbose", false, "Show debug and progress information.")
        );
        options.addOption(
                new Option("h", "help", false, "Print this help.")
        );
        options.addOption(
                new Option("e", true, "Specify a comma-separated list of extractors, e.g. rdf-xml,rdf-turtle.")
        );
        options.addOption(
                new Option("o", "output", true, "Specify Output file (defaults to standard output).")
        );
        options.addOption(
                new Option(
                        "f",
                        "Output format",
                        true,
                        "[" +  printFormats(FORMATS, DEFAULT_FORMAT_INDEX) + "]"
                )
        );
        options.addOption(
                new Option("t", "notrivial", false, "Filter trivial statements (e.g. CSS related ones).")
        );
        options.addOption(
                new Option("s", "stats", false, "Print out extraction statistics.")
        );
        options.addOption(
                new Option("l", "log", true, "Produce log within a file.")
        );
        options.addOption(
                new Option("p", "pedantic", false, "Validate and fixes HTML content detecting commons issues.")
        );
        options.addOption(
                new Option("n", "nesting", false, "Disable production of nesting triples.")
        );
        options.addOption(
                new Option("d", "defaultns", true, "Override the default namespace used to produce statements.")
        );
        return options;
    }

    protected void performExtraction(DocumentSource documentSource) {
        performExtraction(any23, eps, documentSource, reportingTripleHandler);
    }

    protected void performExtraction(String[] inputURIs) throws URISyntaxException, IOException {
        try {
            final long start = System.currentTimeMillis();
            for (String inputURI : inputURIs) {
                performExtraction( any23.createDocumentSource(inputURI) );
            }
            final long elapsed = System.currentTimeMillis() - start;

            if (benchmarkTripleHandler != null) {
                System.err.println(benchmarkTripleHandler.report());
            }

            logger.info("Extractors used: " + reportingTripleHandler.getExtractorNames());
            logger.info(reportingTripleHandler.getTotalTriples() + " triples, " + elapsed + "ms");
        } finally {
            closeStreams();
        }
    }

    protected String printReports() {
        final StringBuilder sb = new StringBuilder();
        if(benchmarkTripleHandler != null) sb.append( benchmarkTripleHandler.report() ).append('\n');
        if(reportingTripleHandler != null) sb.append( reportingTripleHandler.printReport() ).append('\n');
        return sb.toString();
    }

    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("[{<url>|<file>}]+", options, true);
    }

    private String printFormats(String[] formats, int defaultIndex) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < formats.length; i++) {
            sb.append(formats[i]);
            if(i == defaultIndex) sb.append(" (default)");
            if(i < formats.length - 1) sb.append(", ");
        }
        return sb.toString();
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
        String format = FORMATS[DEFAULT_FORMAT_INDEX];
        if (cl.hasOption(FORMAT_OPTION)) {
            format = cl.getOptionValue(FORMAT_OPTION).toLowerCase();
        }
        try {
            return WriterRegistry.getInstance().getWriterInstanceByIdentifier(format, os);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Invalid option value '%s' for option %s", format, FORMAT_OPTION)
            );
        }
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
        final Configuration configuration = DefaultConfiguration.singleton();
        final ExtractionParameters extractionParameters =
                cl.hasOption('p')
                        ?
                new ExtractionParameters(configuration, ValidationMode.ValidateAndFix, nestingDisabled)
                        :
                new ExtractionParameters(configuration, ValidationMode.None          , nestingDisabled);
        if( cl.hasOption('d') ) {
            extractionParameters.setProperty(
                    SingleDocumentExtraction.EXTRACTION_CONTEXT_URI_PROPERTY,
                    cl.getOptionValue('d')
            );
        }
        return extractionParameters;
    }

    private Any23 createAny23(String[] extractorNames) {
        Any23 any23 = (extractorNames == null || extractorNames.length == 0)
                ? new Any23()
                : new Any23(extractorNames);
        any23.setHTTPUserAgent(Any23.DEFAULT_HTTP_CLIENT_USER_AGENT + "/" + Any23.VERSION);
        return any23;
    }

    private void performExtraction(
            Any23 any23, ExtractionParameters eps, DocumentSource documentSource, TripleHandler th
    ) {
        try {
            if (! any23.extract(eps, documentSource, th).hasMatchingExtractors()) {
                throw new ExitCodeException("No suitable extractors found.", 2);
            }
        } catch (ExtractionException ex) {
            throw new ExitCodeException("Exception while extracting metadata.", ex, 3);
        } catch (IOException ex) {
            throw new ExitCodeException("Exception while producing output.", ex, 4);
        }
    }

    private void closeHandler() {
        if(tripleHandler == null) return;
        try {
            tripleHandler.close();
        } catch (TripleHandlerException the) {
            throw new ExitCodeException("Error while closing TripleHandler", the, 5);
        }
    }

    private void closeStreams() {
             closeHandler();
            if(outputStream != null) outputStream.close();
    }

    protected class ExitCodeException extends RuntimeException {

        private final int exitCode;

        public ExitCodeException(String message, Throwable cause, int exitCode) {
            super(message, cause);
            this.exitCode = exitCode;
        }
        public ExitCodeException(String message, int exitCode) {
            super(message);
            this.exitCode = exitCode;
        }
        public ExitCodeException(int exitCode) {
            super();
            this.exitCode = exitCode;
        }

        protected int getExitCode() {
            return exitCode;
        }
    }

}
