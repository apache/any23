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

package org.deri.any23.extractor.rdf;

import org.deri.any23.extractor.ErrorReporter;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.parser.NQuadsParser;
import org.deri.any23.rdf.Any23ValueFactoryWrapper;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.ParseErrorListener;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.ntriples.NTriplesParser;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.openrdf.rio.turtle.TurtleParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * This factory provides a common logic for creating and configuring correctly
 * any RDF parser used within the library.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class RDFParserFactory {

    private static final Logger logger = LoggerFactory.getLogger(RDFParserFactory.class);

    private static RDFParserFactory instance;

    public static RDFParserFactory getInstance() {
        if(instance == null) {
            instance = new RDFParserFactory();
        }
        return instance;
    }

    /**
     * Returns a new instance of a configured {@link org.openrdf.rio.turtle.TurtleParser}.
     *
     * @param verifyDataType data verification enable if <code>true</code>.
     * @param stopAtFirstError the parser stops at first error if <code>true</code>.
     * @param extractionResult the output extraction result.
     * @return a new instance of a configured Turtle parser.
     */
    public TurtleParser getTurtleParserInstance(
            boolean verifyDataType,
            boolean stopAtFirstError,
            final ExtractionResult extractionResult
    ) {
        if (extractionResult == null) {
            throw new NullPointerException("extractionResult cannot be null.");
        }
        final TurtleParser parser = new ExtendedTurtleParser();
        configureParser(parser, verifyDataType, stopAtFirstError, extractionResult);
        return parser;
    }

    /**
     * Returns a new instance of a configured {@link org.openrdf.rio.rdfxml.RDFXMLParser}.
     *
     * @param verifyDataType data verification enable if <code>true</code>.
     * @param stopAtFirstError the parser stops at first error if <code>true</code>.
     * @param extractionResult the output extraction result.
     * @return a new instance of a configured RDFXML parser.
     */
    public RDFXMLParser getRDFXMLParser(
            boolean verifyDataType,
            boolean stopAtFirstError,
            final ExtractionResult extractionResult
    ) {
        final RDFXMLParser parser = new RDFXMLParser();
        configureParser(parser, verifyDataType, stopAtFirstError, extractionResult);
        return parser;
    }

    /**
     * Returns a new instance of a configured {@link org.openrdf.rio.ntriples.NTriplesParser}.
     *
     * @param verifyDataType data verification enable if <code>true</code>.
     * @param stopAtFirstError the parser stops at first error if <code>true</code>.
     * @param extractionResult the output extraction result.
     * @return a new instance of a configured NTriples parser.
     */
    public NTriplesParser getNTriplesParser(
            boolean verifyDataType,
            boolean stopAtFirstError,
            final ExtractionResult extractionResult
    ) {
        final NTriplesParser parser = new NTriplesParser();
        configureParser(parser, verifyDataType, stopAtFirstError, extractionResult);
        return parser;
    }

    /**
     * Returns a new instance of a configured {@link org.deri.any23.parser.NQuadsParser}.
     *
     * @param verifyDataType data verification enable if <code>true</code>.
     * @param stopAtFirstError the parser stops at first error if <code>true</code>.
     * @param extractionResult the output extraction result.
     * @return a new instance of a configured NQuads parser.
     */
    public NQuadsParser getNQuadsParser(
            boolean verifyDataType,
            boolean stopAtFirstError,
            final ExtractionResult extractionResult
    ) {
        final NQuadsParser parser = new NQuadsParser();
        configureParser(parser, verifyDataType, stopAtFirstError, extractionResult);
        return parser;
    }

    /**
     * Configures the given parser on the specified extraction result
     * setting the policies for data verification and error handling.
     *
     * @param parser the parser to be configured.
     * @param verifyDataType enables the data verification.
     * @param stopAtFirstError enables the tolerant error handling.
     * @param extractionResult the extraction result used to collect the parsed data.
     */
    private void configureParser(
            RDFParser parser,
            boolean verifyDataType,
            boolean stopAtFirstError,
            final ExtractionResult extractionResult
    ) {
        parser.setDatatypeHandling(
            verifyDataType ? RDFParser.DatatypeHandling.VERIFY : RDFParser.DatatypeHandling.IGNORE
        );
        parser.setStopAtFirstError(stopAtFirstError);
        parser.setParseErrorListener( new InternalParseErrorListener(extractionResult) );
        parser.setValueFactory(
                new Any23ValueFactoryWrapper(
                        ValueFactoryImpl.getInstance(),
                        extractionResult,
                        extractionResult.getDocumentContext().getDefaultLanguage()
                )
        );
        parser.setRDFHandler(new RDFHandlerAdapter(extractionResult));
    }

    /**
     * Internal listener used to trace <i>RDF</i> parse errors.
     */
    private class InternalParseErrorListener implements ParseErrorListener {

        private final ErrorReporter extractionResult;

        public InternalParseErrorListener(ErrorReporter er) {
            extractionResult = er;
        }

        public void warning(String msg, int lineNo, int colNo) {
            try {
                extractionResult.notifyError(ExtractionResult.ErrorLevel.WARN, msg, lineNo, colNo);
            } catch (Exception e) {
                notifyExceptionInNotification(e);
            }
        }

        public void error(String msg, int lineNo, int colNo) {
            try {
                extractionResult.notifyError(ExtractionResult.ErrorLevel.ERROR, msg, lineNo, colNo);
            } catch (Exception e) {
                notifyExceptionInNotification(e);
            }
        }

        public void fatalError(String msg, int lineNo, int colNo) {
            try {
                extractionResult.notifyError(ExtractionResult.ErrorLevel.FATAL, msg, lineNo, colNo);
            } catch (Exception e) {
                notifyExceptionInNotification(e);
            }
        }

        private void notifyExceptionInNotification(Exception e) {
            if (logger != null) {
                logger.error("An exception occurred while notifying an error.", e);
            }
        }
    }

    /**
     * This extended Turtle parser sets the default namespace to the base URI
     * before the parsing.
     */
    private class ExtendedTurtleParser extends TurtleParser {
        @Override
        public void parse(Reader reader, String baseURI)
        throws IOException, RDFParseException, RDFHandlerException {
            setNamespace("", baseURI);
            super.parse(reader, baseURI);
        }

        @Override
        public void parse(InputStream in, String baseURI)
        throws IOException, RDFParseException, RDFHandlerException {
            setNamespace("", baseURI);
            super.parse(in, baseURI);
        }
    }
}
