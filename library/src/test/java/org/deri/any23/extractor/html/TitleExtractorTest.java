/**
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
 *
 */

package org.deri.any23.extractor.html;

import org.junit.Assert;
import org.junit.Test;

import org.deri.any23.Helper;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.vocab.DCTERMS;
import org.openrdf.model.Literal;
import org.openrdf.repository.RepositoryException;

public class TitleExtractorTest extends AbstractMicroformatTestCase {
    private Literal helloLiteral = Helper.literal("Hello World!");

    protected ExtractorFactory<?> getExtractorFactory() {
        return TitleExtractor.factory;
    }

    @Test
    public void testExtractPageTitle() throws RepositoryException {
        assertExtracts("xfn/simple-me.html");
        Assert.assertTrue(conn.hasStatement(baseURI, DCTERMS.title, helloLiteral, false));
    }

    @Test
    public void testStripSpacesFromTitle() throws RepositoryException {
        assertExtracts("xfn/strip-spaces.html");
        Assert.assertTrue(conn.hasStatement(baseURI, DCTERMS.title, helloLiteral, false));
    }

    @Test
    public void testNoPageTitle() throws RepositoryException {
        assertExtracts("xfn/tagsoup.html");
        assertModelEmpty();
    }

    @Test
    public void testMixedCaseTitleTag() throws RepositoryException {
        assertExtracts("xfn/mixed-case.html");
        Assert.assertTrue(conn.hasStatement(baseURI, DCTERMS.title, helloLiteral, false));
    }
    
}
