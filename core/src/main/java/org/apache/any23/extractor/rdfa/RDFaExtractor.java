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
import org.apache.any23.extractor.rdf.RDFParserFactory;
import org.eclipse.rdf4j.rio.RDFParser;
import org.semarglproject.vocab.RDFa;

/**
 * {@link org.apache.any23.extractor.Extractor} implementation for
 * <a href="http://www.w3.org/TR/rdfa-syntax/">RDFa 1.0</a> specification.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Hans Brende (hansbrende@apache.org)
 */
public class RDFaExtractor extends BaseRDFaExtractor {

    /**
     * @deprecated since 2.4. This extractor has never supported these settings. Use {@link #RDFaExtractor()} instead.
     * @param verifyDataType has no effect
     * @param stopAtFirstError has no effect
     */
    @Deprecated
    public RDFaExtractor(boolean verifyDataType, boolean stopAtFirstError) {
        this();
    }

    /**
     * @deprecated since 2.4. This extractor has never supported this setting. Do not use.
     * @param stopAtFirstError has no effect
     */
    @Deprecated
    @Override
    public void setStopAtFirstError(boolean stopAtFirstError) {
        super.setStopAtFirstError(stopAtFirstError);
    }

    /**
     * @deprecated since 2.4. This extractor has never supported this setting. Do not use.
     * @param verifyDataType has no effect
     */
    @Deprecated
    @Override
    public void setVerifyDataType(boolean verifyDataType) {
        super.setVerifyDataType(verifyDataType);
    }

    public RDFaExtractor() {
        super(RDFa.VERSION_10);
    }

    @Override
    public ExtractorDescription getDescription() {
        return RDFaExtractorFactory.getDescriptionInstance();
    }

    /**
     * @deprecated since 2.4. This extractor no longer wraps an RDF4J {@link RDFParser}. Do not use this method.
     * @param extractionContext the extraction context
     * @param extractionResult the extraction result
     * @return a {@link RDFParser}
     */
    @Override
    @Deprecated
    protected RDFParser getParser(ExtractionContext extractionContext, ExtractionResult extractionResult) {
        return RDFParserFactory.getInstance().getRDFa10Parser(
                isVerifyDataType(), isStopAtFirstError(), extractionContext, extractionResult
        );
    }
}