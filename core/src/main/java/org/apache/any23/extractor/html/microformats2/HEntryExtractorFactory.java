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


package org.apache.any23.extractor.html.microformats2;

import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.SimpleExtractorFactory;
import org.apache.any23.rdf.PopularPrefixes;
import org.apache.any23.rdf.Prefixes;

import java.util.Arrays;

/**
 * Extractor for the <a href="http://microformats.org/wiki/h-entry">h-entry</a>
 * microformat.
 *
 * @author Nisala Nirmana
 */
public class HEntryExtractorFactory extends SimpleExtractorFactory<HEntryExtractor> implements
        ExtractorFactory<HEntryExtractor> {

    public static final String NAME = "html-mf2-h-entry";

    public static final Prefixes PREFIXES = PopularPrefixes.createSubset("rdf", "hentry");

    private static final ExtractorDescription descriptionInstance = new HEntryExtractorFactory();

    public HEntryExtractorFactory() {
        super(
                HEntryExtractorFactory.NAME,
                HEntryExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                "example-mf2-h-entry.html");
    }

    @Override
    public HEntryExtractor createExtractor() {
        return new HEntryExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
