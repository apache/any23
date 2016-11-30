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
package org.apache.any23.extractor.html;

import org.apache.any23.extractor.ExtractorFactory;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests attept to extract statements from single and multiple 
 * JSON-LD snippets embedded in HTML head nodes. 
 */
public class EmbeddedJSONLDExtractorTest extends AbstractExtractorTestCase {

  @Test
  @Ignore("Skip until the EmbeddedJSONLDExtractor implementation is fixed")
  public void testEmbeddedJSONLDInHead() throws Exception {
    assertExtract("/html/html-embedded-jsonld-extractor.html");
    assertModelNotEmpty();
    assertStatementsSize(null, null, null, 3);
  }

  @Test
  @Ignore("Skip until the EmbeddedJSONLDExtractor implementation is fixed")
  public void testSeveralEmbeddedJSONLDInHead() throws Exception {
    assertExtract("/html/html-embedded-jsonld-extractor-multiple.html");
    assertModelNotEmpty();
    assertStatementsSize(null, null, null, 7);
  }

  @Override
  protected ExtractorFactory<?> getExtractorFactory() {
    return new EmbeddedJSONLDExtractorFactory();
  }

}
