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

package org.apache.any23.extractor.rdfa;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.rdf.BaseRDFExtractor;
import org.apache.any23.extractor.rdf.RDFParserFactory;
import org.eclipse.rdf4j.rio.RDFParser;

/**
 * {@link org.apache.any23.extractor.Extractor} implementation for
 * <a href="http://www.w3.org/TR/rdfa-syntax/">RDFa 1.0</a> specification.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class RDFaExtractor extends BaseRDFExtractor {

    public RDFaExtractor(boolean verifyDataType, boolean stopAtFirstError) {
        super(verifyDataType, stopAtFirstError);
    }

    public RDFaExtractor() {
        this(false, false);
    }

    @Override
    public ExtractorDescription getDescription() {
        return RDFaExtractorFactory.getDescriptionInstance();
    }

    @Override
    protected RDFParser getParser(ExtractionContext extractionContext, ExtractionResult extractionResult) {
        return RDFParserFactory.getInstance().getRDFa10Parser(
                isVerifyDataType(), isStopAtFirstError(), extractionContext, extractionResult
        );
    }
}