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

package org.apache.any23.extractor;

import org.apache.any23.extractor.rdf.JSONLDExtractorFactory;
import org.apache.any23.extractor.rdf.NQuadsExtractorFactory;
import org.apache.any23.extractor.rdf.NTriplesExtractorFactory;
import org.apache.any23.extractor.rdf.RDFXMLExtractorFactory;
import org.apache.any23.extractor.rdf.TriXExtractorFactory;
import org.apache.any23.extractor.rdf.TurtleExtractorFactory;
import org.apache.any23.extractor.rdfa.RDFa11ExtractorFactory;
import org.apache.any23.mime.MIMEType;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.Assert;
import org.apache.any23.extractor.example.ExampleExtractor;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.writer.CountingTripleHandler;
import org.junit.Test;
import org.eclipse.rdf4j.model.IRI;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Tests the <i>extraction</i> scenario.
 */
public class ExtractionAPITest {

    private static final String exampleDoc = "http://example.com/";
    private static final IRI uri = RDFUtils.iri(exampleDoc);

    @Test
    public void testDirectInstantiation() throws Exception {
        CountingTripleHandler out   = new CountingTripleHandler();
        ExampleExtractor extractor  = new ExampleExtractor();
        ExtractionContext extractionContext = new ExtractionContext("extractor-name", uri);
        ExtractionResultImpl writer = new ExtractionResultImpl(extractionContext, extractor, out);
        extractor.run(ExtractionParameters.newDefault(), extractionContext, uri, writer);
        writer.close();
        Assert.assertEquals(1, out.getCount());
    }

    private static void test(ExtractorFactory<?> factory, RDFFormat... formats) {
        List<String> mimetypes = factory.getSupportedMIMETypes().stream()
                .map(MIMEType::getFullType).collect(Collectors.toList());

        Assert.assertEquals(formats[0].getDefaultMIMEType(), mimetypes.get(0));

        for (RDFFormat format : formats) {
            for (String mimeType : format.getMIMETypes()) {
                if (mimeType.endsWith("/xml")) {
                    //TODO: xml mimetypes are commented out in RDFXML extractor. Why?
                    continue;
                }
                Assert.assertTrue(mimeType, mimetypes.contains(mimeType));
            }
        }
    }

    @Test
    public void testMimetypes() {
        test(new JSONLDExtractorFactory(), RDFFormat.JSONLD);
        test(new NTriplesExtractorFactory(), RDFFormat.NTRIPLES);
        test(new NQuadsExtractorFactory(), RDFFormat.NQUADS);
        test(new TurtleExtractorFactory(), RDFFormat.TURTLE, RDFFormat.N3);
        test(new RDFXMLExtractorFactory(), RDFFormat.RDFXML);
        test(new TriXExtractorFactory(), RDFFormat.TRIX);
        test(new RDFa11ExtractorFactory(), RDFFormat.RDFA);
    }
    
}
