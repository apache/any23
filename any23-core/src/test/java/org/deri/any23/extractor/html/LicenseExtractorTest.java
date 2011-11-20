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
import org.deri.any23.rdf.RDFUtils;
import org.deri.any23.vocab.SINDICE;
import org.deri.any23.vocab.XHTML;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryException;

/**
 *
 * Reference Test class for the {@link org.deri.any23.extractor.html.LicenseExtractor} extractor.
 *
 */
public class LicenseExtractorTest extends AbstractExtractorTestCase {

    private static final SINDICE vSINDICE = SINDICE.getInstance();
    private static final XHTML   vXHTML   = XHTML.getInstance();

    private URI ccBy = RDFUtils.uri("http://creativecommons.org/licenses/by/2.0/");
    
    private URI apache = RDFUtils.uri("http://www.apache.org/licenses/LICENSE-2.0");

    public ExtractorFactory<?> getExtractorFactory() {
        return LicenseExtractor.factory;
    }

    @Test
    public void testOnlyCc() throws RepositoryException {
        assertExtracts("microformats/license/ccBy.html");
        assertContains(baseURI, vXHTML.license, ccBy);
        assertNotContains(baseURI, vXHTML.license, apache);
    }

    @Test
    public void testOnlyApache() throws RepositoryException {
        assertExtracts("microformats/license/apache.html");
        assertNotContains(baseURI, vXHTML.license, ccBy);
        assertContains(baseURI, vXHTML.license, apache);
    }

    @Test
    public void testMultipleLicenses() throws RepositoryException {
        assertExtracts("microformats/license/multiple.html");
        assertContains(baseURI, vXHTML.license, ccBy);
        assertContains(baseURI, vXHTML.license, apache);
    }

    @Test
    public void testMultipleEmptyHref() throws RepositoryException {
        assertExtracts("microformats/license/multiple-empty-href.html");
        assertNotContains(baseURI, vXHTML.license, "");
        assertContains(baseURI, vXHTML.license, apache);
    }

    @Test
    public void testEmpty() throws RepositoryException {
        assertExtracts("microformats/license/empty.html");
        assertModelEmpty();
    }

    @Test
    public void testMixedCaseTitleTag() throws RepositoryException {
        assertExtracts("microformats/license/multiple-mixed-case.html");
        assertContains(baseURI, vXHTML.license, ccBy);
        assertContains(baseURI, vXHTML.license, apache);
    }

}

