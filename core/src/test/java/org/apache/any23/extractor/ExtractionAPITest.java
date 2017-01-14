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

import junit.framework.Assert;
import org.apache.any23.extractor.example.ExampleExtractor;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.writer.CountingTripleHandler;
import org.junit.Test;
import org.eclipse.rdf4j.model.IRI;

/**
 * Tests the <i>extraction</i> scenario.
 */
public class ExtractionAPITest {

    private static final String exampleDoc = "http://example.com/";
    private static final IRI uri           = RDFUtils.iri(exampleDoc);

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
    
}
