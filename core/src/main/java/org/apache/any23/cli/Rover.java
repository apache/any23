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
import org.apache.any23.Any23;
import org.apache.any23.configuration.Configuration;
import org.apache.any23.configuration.DefaultConfiguration;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionParameters.ValidationMode;
import org.apache.any23.filter.IgnoreAccidentalRDFa;
import org.apache.any23.filter.IgnoreTitlesOfEmptyDocuments;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.writer.BenchmarkTripleHandler;
import org.apache.any23.writer.LoggingTripleHandler;
import org.apache.any23.writer.ReportingTripleHandler;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.apache.any23.writer.WriterFactoryRegistry;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import static java.lang.String.format;

/**
 * A default rover implementation. Goes and fetches a URL using an hint
 * as to what format should require, then tries to convert it to RDF.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @author Gabriele Renzi
 */
@MetaInfServices
@Parameters(commandNames = { "rover" }, commandDescription = "Any23 Command Line Tool.")
public class Rover implements Tool {

    private static final List<String> FORMATS = WriterFactoryRegistry.getInstance().getIdentifiers();

    private static final int DEFAULT_FORMAT_INDEX = 0;

    private static final Logger logger = LoggerFactory.getLogger(Rover.class);

    @Parameter(
       names = { "-o", "--output" },
       description = "Specify Output file (defaults to standard output)",
       converter = PrintStreamConverter.class
    )
    private PrintStream outputStream = System.out;

    @Parameter(description = "input URIs {<url>|<file>}+", converter = ArgumentToURIConverter.class)
    protected List<String> inputURIs = new LinkedList<String>();

    @Parameter(names = { "-e", "--extractors" }, description = "a comma-separated list of extractors, e.g. rdf-xml,rdf-turtle")
    private List<String> extractors = new LinkedList<String>();

    @Parameter(names = { "-f", "--format" }, description = "the output format")
    private String format = FORMATS.get(DEFAULT_FORMAT_INDEX);

    @Parameter(
       names = { "-l", "--log" },
       description = "Produce log within a file.",
       converter = FileConverter.class
    )
    private File logFile = null;

    @Parameter(names = { "-s", "--stats" }, description = "Print out extraction statistics.")
    private boolean statistics;

    @Parameter(names = { "-t", "--notrivial" }, description = "Filter trivial statements (e.g. CSS related ones).")
    private boolean noTrivial;

    @Parameter(names = { "-p", "--pedantic" }, description = "Validate and fixes HTML content detecting commons issues.")
    private boolean pedantic;

    @Parameter(names = { "-n", "--nesting" }, description = "Disable production of nesting triples.")
    private boolean nestingDisabled;

    @Parameter(names = { "-d", "--defaultns" }, description = "Override the default namespace used to produce statements.")
    private String defaultns;

    // non parameters

    private TripleHandler tripleHandler;

    private ReportingTripleHandler reportingTripleHandler;

    private BenchmarkTripleHandler benchmarkTripleHandler;

    private Any23 any23;

    private ExtractionParameters extractionParameters;

    protected void configure() {
        try {
            tripleHandler = WriterFactoryRegistry.getInstance().getWriterInstanceByIdentifier(format, outputStream);
        } catch (Exception e) {
            throw new NullPointerException(
                    format("Invalid output format '%s', admitted values: %s",
                        format,
                        FORMATS
                    )
            );
        }

        if (logFile != null) {
            try {
                tripleHandler = new LoggingTripleHandler(tripleHandler, new PrintWriter(logFile));
            } catch (FileNotFoundException fnfe) {
                throw new IllegalArgumentException( format("Can not write to log file [%s]", logFile), fnfe );
            }
        }

        if (statistics) {
            benchmarkTripleHandler = new BenchmarkTripleHandler(tripleHandler);
            tripleHandler = benchmarkTripleHandler;
        }

        if (noTrivial) {
            tripleHandler = new IgnoreAccidentalRDFa(new IgnoreTitlesOfEmptyDocuments(tripleHandler),
                                                     true    // suppress stylesheet triples.
                                                     );
        }

        reportingTripleHandler = new ReportingTripleHandler(tripleHandler);

        final Configuration configuration = DefaultConfiguration.singleton();
        extractionParameters =
                pedantic
                        ?
                new ExtractionParameters(configuration, ValidationMode.ValidateAndFix, nestingDisabled)
                        :
                new ExtractionParameters(configuration, ValidationMode.None          , nestingDisabled);
        if (defaultns != null) {
            extractionParameters.setProperty(ExtractionParameters.EXTRACTION_CONTEXT_URI_PROPERTY,
                                             defaultns);
        }

        any23 = (extractors.isEmpty()) ? new Any23()
                                                   : new Any23(extractors.toArray(new String[extractors.size()]));
        any23.setHTTPUserAgent(Any23.DEFAULT_HTTP_CLIENT_USER_AGENT + "/" + Any23.VERSION);
    }

    protected String printReports() {
        final StringBuilder sb = new StringBuilder();
        if (benchmarkTripleHandler != null) sb.append( benchmarkTripleHandler.report() ).append('\n');
        if (reportingTripleHandler != null) sb.append( reportingTripleHandler.printReport() ).append('\n');
        return sb.toString();
    }

    protected void performExtraction(DocumentSource documentSource) throws Exception {
        if (!any23.extract(extractionParameters, documentSource, tripleHandler).hasMatchingExtractors()) {
            throw new IllegalStateException(format("No suitable extractors found for source %s", documentSource));
        }
    }

    protected void close() {
        if (tripleHandler != null) {
            try {
                tripleHandler.close();
            } catch (TripleHandlerException the) {
                throw new RuntimeException("Error while closing TripleHandler", the);
            }
        }

        if (outputStream != null && outputStream != System.out) { // TODO: low - find better solution to avoid closing system out.
            outputStream.close();
        }
    }

    public void run() throws Exception {
        if (inputURIs.isEmpty()) {
            throw new IllegalArgumentException("Expected at least 1 argument.");
        }

        configure();

        // perform conversions

        try {
            final long start = System.currentTimeMillis();
            for (String inputURI : inputURIs) {
                DocumentSource source = any23.createDocumentSource(inputURI);

                performExtraction( source );
            }
            final long elapsed = System.currentTimeMillis() - start;

            if (benchmarkTripleHandler != null) {
                System.err.println(benchmarkTripleHandler.report());
            }

            logger.info("Extractors used: " + reportingTripleHandler.getExtractorNames());
            logger.info(reportingTripleHandler.getTotalTriples() + " triples, " + elapsed + "ms");
        } finally {
            close();
        }
    }

    public static final class ArgumentToURIConverter implements IStringConverter<String> {

        @Override
        public String convert(String uri) {
            uri = uri.trim();
            if (uri.toLowerCase().startsWith("http:") || uri.toLowerCase().startsWith("https:")) {
                try {
                    return new URL(uri).toString();
                } catch (MalformedURLException murle) {
                    throw new ParameterException(format("Invalid URI: '%s': %s", uri, murle.getMessage()));
                }
            }

            final File f = new File(uri);
            if (!f.exists()) {
                throw new ParameterException(format("No such file: [%s]", f.getAbsolutePath()));
            }
            if (f.isDirectory()) {
                throw new ParameterException(format("Found a directory: [%s]", f.getAbsolutePath()));
            }
            return f.toURI().toString();
        }

    }

    public static final class PrintStreamConverter implements IStringConverter<PrintStream> {

        @Override
        public PrintStream convert( String value ) {
            final File file = new File(value);
            try {
                return new PrintStream(file);
            } catch (FileNotFoundException fnfe) {
                throw new ParameterException(format("Cannot open file '%s': %s", file, fnfe.getMessage()));
            }
        }

    }

}
