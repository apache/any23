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

import java.util.Arrays;

import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.SimpleExtractorFactory;
import org.apache.any23.rdf.PopularPrefixes;
import org.apache.any23.rdf.Prefixes;

/**
 *
 */
public class EmbeddedJSONLDExtractorFactory extends SimpleExtractorFactory<EmbeddedJSONLDExtractor> implements
        ExtractorFactory<EmbeddedJSONLDExtractor> {

    public static final String NAME = "html-embedded-jsonld";
    
    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("jsonld");

    private static final ExtractorDescription descriptionInstance = new EmbeddedJSONLDExtractorFactory();
    
    public EmbeddedJSONLDExtractorFactory() {
        super(
                EmbeddedJSONLDExtractorFactory.NAME, 
                EmbeddedJSONLDExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.02", "application/xhtml+xml;q=0.02"),
                "example-embedded-jsonld.html");
    }
    
    @Override
    public EmbeddedJSONLDExtractor createExtractor() {
        return new EmbeddedJSONLDExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
