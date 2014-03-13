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
package org.apache.any23.extractor.akn;

import java.io.IOException;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.ExtractorDescription;
import org.w3c.dom.Document;

/**
 * Extractor for the <a href="http://www.akomtantoso.org">Akoma Ntoso</a>
 * XML Format.
 * @author lewismc
 *
 */
public class AKNExtractor implements Extractor.TagSoupDOMExtractor {

  @Override
  public void run(ExtractionParameters extractionParameters, ExtractionContext context, Document in,
      ExtractionResult out) throws IOException, ExtractionException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public ExtractorDescription getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

}
