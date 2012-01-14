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

package org.deri.any23.extractor.rdfa;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionParameters;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.Extractor;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

/**
 * {@link org.deri.any23.extractor.Extractor} implementation for
 * <a href="http://www.w3.org/TR/rdfa-syntax/">RDFa 1.1</a> specification.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class RDFa11Extractor implements Extractor.TagSoupDOMExtractor {

    public final static String NAME = "html-rdfa11";

    public final static ExtractorFactory<RDFa11Extractor> factory =
            SimpleExtractorFactory.create(
                    NAME,
                    null,
                    Arrays.asList("text/html;q=0.3", "application/xhtml+xml;q=0.3"),
                    "example-rdfa11.html",
                    RDFa11Extractor.class
            );

    private final RDFa11Parser parser;

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
     public RDFa11Extractor(boolean verifyDataType, boolean stopAtFirstError) {
         this.parser = new RDFa11Parser();
         this.verifyDataType   = verifyDataType;
         this.stopAtFirstError = stopAtFirstError;
     }

     /**
      * Default constructor, with no verification of data types and not stop at first error.
      */
     public RDFa11Extractor() {
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

     public void setStopAtFirstError(boolean stopAtFirstError) {
         this.stopAtFirstError = stopAtFirstError;
     }

     public void run(
             ExtractionParameters extractionParameters,
             ExtractionContext extractionContext,
             Document in,
             ExtractionResult out
     ) throws IOException, ExtractionException {
         try {
             parser.processDocument( new URL(extractionContext.getDocumentURI().toString() ), in, out );
         } catch (RDFa11ParserException rpe) {
             throw new ExtractionException("Error while performing extraction.", rpe);
         }
     }

     /**
      * @return the {@link org.deri.any23.extractor.ExtractorDescription} of this extractor
      */
     public ExtractorDescription getDescription() {
         return factory;
     }

}
