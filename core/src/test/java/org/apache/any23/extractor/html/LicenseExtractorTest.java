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

import org.apache.any23.extractor.IssueReport;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.vocab.SINDICE;
import org.apache.any23.vocab.XHTML;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.repository.RepositoryException;

import java.util.Collection;

/**
 *
 * Reference Test class for the {@link LicenseExtractor} extractor.
 *
 */
public class LicenseExtractorTest extends AbstractExtractorTestCase {

    private static final SINDICE vSINDICE = SINDICE.getInstance();
    private static final XHTML   vXHTML   = XHTML.getInstance();

    private IRI ccBy = RDFUtils.iri("http://creativecommons.org/licenses/by/2.0/");
    
    private IRI apache = RDFUtils.iri("http://www.apache.org/licenses/LICENSE-2.0");

    public ExtractorFactory<?> getExtractorFactory() {
        return new LicenseExtractorFactory();
    }

    @Test
    public void testOnlyCc() throws RepositoryException {
        assertExtract("/microformats/license/ccBy.html");
        assertContains(baseIRI, vXHTML.license, ccBy);
        assertNotContains(baseIRI, vXHTML.license, apache);
    }

    @Test
    public void testOnlyApache() throws RepositoryException {
        assertExtract("/microformats/license/apache.html");
        assertNotContains(baseIRI, vXHTML.license, ccBy);
        assertContains(baseIRI, vXHTML.license, apache);
    }

    @Test
    public void testMultipleLicenses() throws RepositoryException {
        assertExtract("/microformats/license/multiple.html");
        assertContains(baseIRI, vXHTML.license, ccBy);
        assertContains(baseIRI, vXHTML.license, apache);
    }

    @Test
    public void testMultipleEmptyHref() throws RepositoryException {
        assertExtract("/microformats/license/multiple-empty-href.html", false);
        assertNotContains(baseIRI, vXHTML.license, "");
        assertContains(baseIRI, vXHTML.license, apache);
        
        final Collection<IssueReport.Issue> errors = getIssues();
        Assert.assertEquals(1, errors.size());
        IssueReport.Issue error = errors.iterator().next();
        Assert.assertTrue(error.getMessage().contains("Invalid license link detected"));
        Assert.assertEquals(IssueReport.IssueLevel.WARNING, error.getLevel());
    }

    @Test
    public void testEmpty() throws RepositoryException {
        assertExtract("/microformats/license/empty.html");
        assertModelEmpty();
    }

    @Test
    public void testMixedCaseTitleTag() throws RepositoryException {
        assertExtract("/microformats/license/multiple-mixed-case.html");
        assertContains(baseIRI, vXHTML.license, ccBy);
        assertContains(baseIRI, vXHTML.license, apache);
    }

}

