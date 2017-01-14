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

package org.apache.any23.extractor.example;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.vocab.FOAF;
import org.apache.any23.extractor.Extractor.BlindExtractor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.io.IOException;

/**
 * Example concrete implementation of {@link org.apache.any23.extractor.Extractor.BlindExtractor}.
 */
public class ExampleExtractor implements BlindExtractor {

    private static final FOAF vFOAF = FOAF.getInstance();

    @Override
    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            IRI documentIRI,
            ExtractionResult out
    )
    throws IOException, ExtractionException {
        out.writeTriple(documentIRI, RDF.TYPE, vFOAF.Document);
    }

    @Override
    public ExtractorDescription getDescription() {
        return ExampleExtractorFactory.getDescriptionInstance();
    }
}
