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
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Concrete implementation of {@link org.deri.any23.extractor.Extractor.ContentExtractor}
 * handling NTriples <a href="http://www.w3.org/2001/sw/RDFCore/ntriples/">NTriples</a> format.
 */
public class NTriplesExtractor implements ContentExtractor {

    public final static ExtractorFactory<NTriplesExtractor> factory =
            SimpleExtractorFactory.create(
                    "rdf-nt",
                    null,
                    Arrays.asList("text/plain;q=0.1"),
                    "example-ntriples.nt",
                    NTriplesExtractor.class
            );


    private boolean verifyDataType;
    private boolean stopAtFirstError;

    /**
     * Constructor, allows to specify the validation and error handling policies.
     *
     * @param verifyDataType if <code>true</code> the data types will be verified,
     *         if <code>false</code> will be ignored.
     * @param stopAtFirstError if <code>true</code> the parser will stop at first parsing error,
     *        if <code>false</code> will ignore non blocking errors.
     */
    public NTriplesExtractor(boolean verifyDataType, boolean stopAtFirstError) {
        this.verifyDataType = verifyDataType;
        this.stopAtFirstError = stopAtFirstError;
    }

    /**
     * Default constructor, with no verification of data types and no stop at first error.
     */    
    public NTriplesExtractor() {
        this(false, false);
    }
    
    public void setStopAtFirstError(boolean f) {
        stopAtFirstError = f;
    }

    public boolean getStopAtFirstError() {
        return stopAtFirstError;
    }

    public boolean isVerifyDataType() {
        return verifyDataType;
    }

    public void setVerifyDataType(boolean verifyDataType) {
        this.verifyDataType = verifyDataType;
    }

    public void run(InputStream in, URI documentURI, final ExtractionResult out)
            throws IOException, ExtractionException {
        try {
            RDFParser parser =
                    RDFParserFactory.getInstance().getNTriplesParser(verifyDataType, stopAtFirstError, out);
            parser.parse(in, documentURI.stringValue());
        } catch (RDFHandlerException ex) {
            throw new RuntimeException("Should not happen, RDFHandlerAdapter does not throw this", ex);
        } catch (RDFParseException ex) {
            throw new ExtractionException("Error while parsing RDF document.", ex, out.getExtractionContext());
        }
    }

    public ExtractorDescription getDescription() {
        return factory;
    }

}
