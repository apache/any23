/*
 * Copyright 2018 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.any23.extractor.rdfa;

import java.util.Arrays;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.SimpleExtractorFactory;
import org.apache.any23.rdf.Prefixes;

/**
 *
 * @author Julio Caguano
 */
public class LibRdfaExtractorFactory extends SimpleExtractorFactory<LibRdfaExtractor>
        implements ExtractorFactory<LibRdfaExtractor> {

    public static final String NAME = "html-librdfa";
    public static final Prefixes PREFIXES = null;

    private static final ExtractorDescription descriptionInstance = new LibRdfaExtractorFactory();

    public LibRdfaExtractorFactory() {
        super(LibRdfaExtractorFactory.NAME,
                LibRdfaExtractorFactory.PREFIXES,
                Arrays.asList("text/html;q=0.3", "application/xhtml+xml;q=0.3"),
                "example-rdfa11.html");
    }

    @Override
    public LibRdfaExtractor createExtractor() {
        return new LibRdfaExtractor();
    }

    public static ExtractorDescription getDescriptionInstance() {
        return descriptionInstance;
    }
}
