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

import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.vocab.DCTerms;
import org.apache.any23.vocab.SINDICE;
import org.junit.Test;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.repository.RepositoryException;

/**
 * Reference Test class for the {@link TitleExtractor} extractor.
 * 
 */
public class TitleExtractorTest extends AbstractExtractorTestCase {

    private static final DCTerms vDCTERMS = DCTerms.getInstance();
    private static final SINDICE vSINDICE = SINDICE.getInstance();

    private Literal helloLiteral = RDFUtils.literal("Hello World!");

    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return new TitleExtractorFactory();
    }

    @Test
    public void testExtractPageTitle() throws RepositoryException {
        assertExtract("/microformats/xfn/simple-me.html");
        assertContains(baseIRI, vDCTERMS.title, helloLiteral);
    }

    @Test
    public void testStripSpacesFromTitle() throws RepositoryException {
        assertExtract("/microformats/xfn/strip-spaces.html");
        assertContains(baseIRI, vDCTERMS.title, helloLiteral);
    }

    @Test
    public void testNoPageTitle() throws RepositoryException {
        assertExtract("/microformats/xfn/tagsoup.html");
        assertModelEmpty();
    }

    @Test
    public void testMixedCaseTitleTag() throws RepositoryException {
        assertExtract("/microformats/xfn/mixed-case.html");
        assertContains(baseIRI, vDCTERMS.title, helloLiteral);
    }

    /**
     * This test verifies that when present the default language this is adopted by the title literal.
     * 
     * @throws org.eclipse.rdf4j.repository.RepositoryException if an error is encountered whilst loading content from a storage connection
     */
    @Test
    public void testTitleWithDefaultLanguage() throws RepositoryException {
        assertExtract("/html/default-language.html");
        assertContains   (baseIRI, vDCTERMS.title, RDFUtils.literal("Welcome to mydomain.net", "en"));
        assertNotContains(baseIRI, vDCTERMS.title, RDFUtils.literal("Welcome to mydomain.net",(String) null));
    }
    
}
