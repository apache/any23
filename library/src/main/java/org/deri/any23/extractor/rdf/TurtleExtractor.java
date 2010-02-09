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

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.Extractor.ContentExtractor;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.openrdf.model.URI;
import org.openrdf.rio.ParseErrorListener;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.turtle.TurtleParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 *
 * Concrete implementation of {@link org.deri.any23.extractor.Extractor.ContentExtractor} able to perform the
 * extraction on <a href="http://www.w3.org/TeamSubmission/turtle/">Turtle</a> documents.
 *
 */
public class TurtleExtractor implements ContentExtractor {

    public static final ExtractorFactory<TurtleExtractor> factory =
            SimpleExtractorFactory.create(
                    "rdf-turtle",
                    null,
                    Arrays.asList(
                            "text/rdf+n3",
                            "text/n3",
                            "application/n3",
                            "application/x-turtle",
                            "application/turtle",
                            "text/turtle"
                    ),
                    "example-turtle.ttl",
                    TurtleExtractor.class
            );

    private static final Logger logger = LoggerFactory.getLogger(TurtleExtractor.class);

    private boolean stopAtFirstError = true;

    public void run(InputStream in, URI documentURI, ExtractionResult out)
    throws IOException, ExtractionException {
        try {
            TurtleParser parser = new TurtleParser();
            parser.setDatatypeHandling( RDFParser.DatatypeHandling.VERIFY );
            parser.setStopAtFirstError(stopAtFirstError);
            parser.setParseErrorListener( new ParseErrorListener() {
                public void warning(String msg, int lineNo, int colNo) {
                    logger.warn( report(msg, lineNo, colNo) );
                }

                public void error(String msg, int lineNo, int colNo) {
                    logger.error( report(msg, lineNo, colNo) );
                }

                public void fatalError(String msg, int lineNo, int colNo) {
                    logger.error( report("FATAL: " + msg, lineNo, colNo) );
                }

                private String report(String msg, int lineNo, int colNo) {
                    return String.format("'%s [%d, %d]'", msg, lineNo, colNo);
                }
            });
            parser.setRDFHandler( new RDFHandlerAdapter(out) );
            parser.parse( in, documentURI.stringValue() );
        } catch (RDFHandlerException ex) {
            throw new RuntimeException("Should not happen, RDFHandlerAdapter does not throw this", ex);
        } catch (RDFParseException ex) {
            throw new ExtractionException(ex);
        }
    }

    public ExtractorDescription getDescription() {
        return factory;
    }

    public void setStopAtFirstError(boolean f) {
        stopAtFirstError = f;
    }

    public boolean getStopAtFirstError() {
        return stopAtFirstError;
    }

}
