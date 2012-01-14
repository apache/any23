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

package org.deri.any23.extractor.rdf;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionParameters;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.Extractor.ContentExtractor;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.RDFParserBase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Concrete implementation of {@link org.deri.any23.extractor.Extractor.ContentExtractor}
 * handling NTriples <a href="http://www.w3.org/2001/sw/RDFCore/ntriples/">NTriples</a> format.
 */
public class NTriplesExtractor extends BaseRDFExtractor {

    public final static ExtractorFactory<NTriplesExtractor> factory =
            SimpleExtractorFactory.create(
                    "rdf-nt",
                    null,
                    Arrays.asList(
                            "text/nt;q=0.1",
                            "text/ntriples;q=0.1",
                            "text/plain;q=0.1"
                    ),
                    "example-ntriples.nt",
                    NTriplesExtractor.class
            );

    public NTriplesExtractor(boolean verifyDataType, boolean stopAtFirstError) {
        super(verifyDataType, stopAtFirstError);
    }

    /**
     * Default constructor, with no verification of data types and no stop at first error.
     */
    public NTriplesExtractor() {
        this(false, false);
    }

    public ExtractorDescription getDescription() {
        return factory;
    }

    @Override
    protected RDFParserBase getParser(ExtractionContext extractionContext, ExtractionResult extractionResult) {
        return RDFParserFactory.getInstance().getNTriplesParser(
                isVerifyDataType(), isStopAtFirstError(), extractionContext, extractionResult
        );
    }

}
