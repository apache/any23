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
package org.apache.any23.openie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.ExtractionResultImpl;
import org.apache.any23.plugin.extractor.openie.OpenIEExtractor;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.util.StreamUtils;
import org.apache.any23.writer.RDFXMLWriter;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.eclipse.rdf4j.model.IRI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lewismc
 *
 */
public class OpenIEExtractorTest {

    private static final Logger logger = LoggerFactory.getLogger(OpenIEExtractorTest.class);

    private OpenIEExtractor extractor;

    @Before
    public void setUp() throws Exception {
        extractor = new OpenIEExtractor();
    }

    @After
    public void tearDown() throws Exception {
        extractor = null;
    }

    @Test
    public void testExtractFromHTMLDocument() 
      throws IOException, ExtractionException, TripleHandlerException {
        final IRI uri = RDFUtils.iri("http://podaac.jpl.nasa.gov/aquarius");
        extract(uri, "/org/apache/any23/extractor/openie/example-openie.html");
    }
    
    public void extract(IRI uri, String filePath) 
      throws IOException, ExtractionException, TripleHandlerException {
      FileOutputStream fos = new FileOutputStream(File.createTempFile("OpenIEExtractorTest", "tmp"));
      final TripleHandler tHandler = new RDFXMLWriter(fos);
      final ExtractionContext extractionContext = new ExtractionContext("rdf-openie", uri);
      final ExtractionResult result = new ExtractionResultImpl(extractionContext, extractor, tHandler);
      try {
        extractor.run(
                ExtractionParameters.newDefault(),
                extractionContext,
                StreamUtils.inputStreamToDocument(this.getClass().getResourceAsStream(filePath)),
                result
        );
      } finally {
        logger.debug(fos.toString());
        tHandler.close();
        result.close();
      }
    }

}
