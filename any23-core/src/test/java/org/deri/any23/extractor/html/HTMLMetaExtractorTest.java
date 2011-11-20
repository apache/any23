/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.vocab.SINDICE;
import org.junit.Test;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;

/**
 * Reference Test class for the {@link HTMLMetaExtractor} extractor.
 *
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class HTMLMetaExtractorTest extends AbstractExtractorTestCase {

    private static final SINDICE vSINDICE = SINDICE.getInstance();

    protected ExtractorFactory<?> getExtractorFactory() {
        return HTMLMetaExtractor.factory;
    }

    @Test
    public void testExtractPageMeta() throws RepositoryException {
        assertExtracts("html/html-head-meta-extractor.html");
        assertModelNotEmpty();
        assertStatementsSize(null, null, null, 7);
        assertContains(
                new URIImpl("http://bob.example.com/"),
                new URIImpl("http://purl.org/dc/elements/1.1/title"),
                "XHTML+RDFa example",
                "en"
        );
         assertContains(
                new URIImpl("http://bob.example.com/"),
                new URIImpl("http://purl.org/dc/elements/1.1/language"),
                "en",
                "en"
        );
        assertContains(
                new URIImpl("http://bob.example.com/"),
                new URIImpl("http://purl.org/dc/elements/1.1/subject"),
                "XHTML+RDFa, semantic web",
                "en"
        );
        assertContains(
                new URIImpl("http://bob.example.com/"),
                new URIImpl("http://purl.org/dc/elements/1.1/format"),
                "application/xhtml+xml",
                "en"
        );
        assertContains(
                new URIImpl("http://bob.example.com/"),
                new URIImpl("http://purl.org/dc/elements/1.1/description"),
                "Example for Extensible Hypertext Markup Language + Resource Description Framework – in – attributes.",
                "en"
        );
        assertContains(
                new URIImpl("http://bob.example.com/"),
                new URIImpl("http://vocab.sindice.net/robots"),
                "index, follow",
                "en"
        );
        assertContains(
                new URIImpl("http://bob.example.com/"),
                new URIImpl("http://vocab.sindice.net/content-language"),
                "en",
                "en"
        );
    }

    @Test
    public void testNoMeta() throws RepositoryException {
        assertExtracts("html/html-head-link-extractor.html");
        assertModelEmpty();
    }
    
}
