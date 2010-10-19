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
import org.deri.any23.extractor.Extractor;
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
 * handling <a href="http://sw.deri.org/2008/07/n-quads/">N-Quads</a> format.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class NQuadsExtractor implements Extractor.ContentExtractor {

    public final static ExtractorFactory<NQuadsExtractor> factory =
        SimpleExtractorFactory.create(
                "rdf-nq",
                null,
                Arrays.asList(
                        "text/rdf+nq;q=0.1",
                        "text/nq;q=0.1",
                        "text/nquads;q=0.1",
                        "text/n-quads;q=0.1"
                ),
                "example-nquads.nq",
                NQuadsExtractor.class
        );

    private boolean verifyDataType;
    private boolean stopAtFirstError;

    public NQuadsExtractor(boolean verifyDataType, boolean stopAtFirstError) {
        this.verifyDataType = verifyDataType;
        this.stopAtFirstError = stopAtFirstError;
    }

    public NQuadsExtractor() {
        this(false, false);
    }

    public boolean isVerifyDataType() {
        return verifyDataType;
    }

    public void setVerifyDataType(boolean verifyDataType) {
        this.verifyDataType = verifyDataType;
    }

    public boolean isStopAtFirstError() {
        return stopAtFirstError;
    }

    public void setStopAtFirstError(boolean b) {
        stopAtFirstError = b;
    }

    public void run(InputStream in, URI documentURI, ExtractionResult out)
    throws IOException, ExtractionException {
         try {
            RDFParser parser =
                    RDFParserFactory.getInstance().getNQuadsParser(verifyDataType, stopAtFirstError, out);
            parser.parse(in, documentURI.stringValue());
        } catch (RDFHandlerException ex) {
            throw new RuntimeException("Should not happen, RDFHandlerAdapter does not throw this", ex);
        } catch (RDFParseException ex) {
            throw new ExtractionException("Error while parsing RDF document.", ex, out);
        }
    }

    public ExtractorDescription getDescription() {
        return factory;
    }

}
