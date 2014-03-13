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

import java.util.Arrays;

import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.SimpleExtractorFactory;
import org.apache.any23.rdf.PopularPrefixes;
import org.apache.any23.rdf.Prefixes;
import org.kohsuke.MetaInfServices;

/**
 * @author lewismc
 *
 */
@MetaInfServices(ExtractorFactory.class)
public class AKNExtractorFactory extends SimpleExtractorFactory<AKNExtractor> implements
        ExtractorFactory<AKNExtractor> {

    private static final ExtractorDescription descriptionInstance = new AKNExtractorFactory();
    private static final String NAME = "akomaNtoso";
    private static final Prefixes PREFIXES = PopularPrefixes.createSubset("akn", "AKN", "AKOMA");
    
    public AKNExtractorFactory() {
        super(AKNExtractorFactory.NAME, 
                AKNExtractorFactory.PREFIXES);
    }
    
    @Override
    public AKNExtractor createExtractor() {
        return new AKNExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
