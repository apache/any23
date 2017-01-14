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
package org.apache.any23.extractor.rdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.ExtractionResultImpl;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.writer.RDFXMLWriter;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test case for {@link JSONLDExtractor}.
 *
 */
public class JSONLDExtractorTest {
  
  private static final Logger logger = LoggerFactory.getLogger(JSONLDExtractorTest.class);

  private JSONLDExtractor extractor;

  @Before
  public void setUp() throws Exception {
      extractor = new JSONLDExtractor();
  }

  @After
  public void tearDown() throws Exception {
      extractor = null;
  }

  @Test
  public void testExtractFromJSONLDDocument() 
    throws IOException, ExtractionException, TripleHandlerException {
      final IRI uri = RDFUtils.iri("http://host.com/place-example.jsonld");
      extract(uri, "/org/apache/any23/extractor/rdf/place-example.jsonld");
  }
  
  public void extract(IRI uri, String filePath) 
    throws IOException, ExtractionException, TripleHandlerException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final TripleHandler tHandler = new RDFXMLWriter(baos);
    final ExtractionContext extractionContext = new ExtractionContext("rdf-jsonld", uri);
    final ExtractionResult result = new ExtractionResultImpl(extractionContext, extractor, tHandler);
    extractor.setStopAtFirstError(false);
    try {
      extractor.run(
              ExtractionParameters.newDefault(),
              extractionContext,
              this.getClass().getResourceAsStream(filePath),
              result
      );
    } finally {
      logger.debug(baos.toString());
      tHandler.close();
      result.close();
    }
  }

}
