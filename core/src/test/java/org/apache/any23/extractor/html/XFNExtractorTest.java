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
import org.apache.any23.vocab.FOAF;
import org.apache.any23.vocab.SINDICE;
import org.apache.any23.vocab.XFN;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryException;

/**
 *
 * Reference Test class for the {@link XFNExtractor} extractor.
 *
 */
public class XFNExtractorTest extends AbstractExtractorTestCase {

    private static final FOAF    vFOAF    = FOAF.getInstance();
    private static final SINDICE vSINDICE = SINDICE.getInstance();
    private static final XFN     vXFN     = XFN.getInstance();

    private final static IRI bobsHomepage = baseIRI;

    private final static IRI alicesHomepage = RDFUtils.iri("http://alice.example.com/");

    private final static IRI charliesHomepage = RDFUtils.iri("http://charlie.example.com/");

    protected ExtractorFactory<?> getExtractorFactory() {
        return new XFNExtractorFactory();
    }

    @Test
    public void testNoMicroformats() throws RepositoryException {
        assertExtract("/html/html-without-uf.html");
        assertModelEmpty();
    }

    @Test
    public void testLinkWithoutRel() throws RepositoryException {
        assertExtract("/microformats/xfn/no-rel.html");
        assertModelEmpty();
    }

    @Test
    public void testNoXFNRel() throws RepositoryException {
        assertExtract("/microformats/xfn/no-valid-rel.html");
        assertModelEmpty();
    }

    @Test
    public void testDetectPresenceOfXFN() throws RepositoryException {
        assertExtract("/microformats/xfn/simple-me.html");
    }

    @Test
    public void testSimpleMeLink() throws RepositoryException {
        assertExtract("/microformats/xfn/simple-me.html");
        Resource person = findExactlyOneBlankSubject(RDF.TYPE, vFOAF.Person);
        assertContains(person, vXFN.mePage, baseIRI);
        assertContains(person, vXFN.mePage, bobsHomepage);
    }

    @Test
    public void testRelativeIRIisResolvedAgainstBase() throws RepositoryException {
        assertExtract("/microformats/xfn/with-relative-uri.html");
        assertContains(null, vXFN.mePage, RDFUtils.iri("http://bob.example.com/foo"));
    }

    @Test
    public void testParseTagSoup() throws RepositoryException {
        assertExtract("/microformats/xfn/tagsoup.html");
        Resource person = findExactlyOneBlankSubject(RDF.TYPE, vFOAF.Person);
        assertContains(person, vXFN.mePage, baseIRI);
    }

    @Test
    public void testSimpleFriend() throws RepositoryException {
        assertExtract("/microformats/xfn/simple-friend.html");
        Resource bob = findExactlyOneBlankSubject(vXFN.mePage, baseIRI);
        Resource alice = findExactlyOneBlankSubject(vXFN.mePage, alicesHomepage);
        assertContains(bob, RDF.TYPE, vFOAF.Person);
        assertContains(alice, RDF.TYPE, vFOAF.Person);
        Assert.assertFalse(alice.equals(bob));
        assertContains(bob, vXFN.friend, alice);
        assertContains(baseIRI, vXFN.getExtendedProperty("friend"), alicesHomepage);
    }

    @Test
    public void testFriendAndSweetheart() throws RepositoryException {
        assertExtract("/microformats/xfn/multiple-rel.html");
        Resource bob = findExactlyOneBlankSubject(vXFN.mePage, baseIRI);
        Resource alice = findExactlyOneBlankSubject(vXFN.mePage, alicesHomepage);
        assertContains(bob, vXFN.friend, alice);
        assertContains(baseIRI, vXFN.getExtendedProperty("friend"), alicesHomepage);

        assertContains(bob, vXFN.sweetheart, alice);
        assertContains(baseIRI, vXFN.getExtendedProperty("sweetheart"), alicesHomepage);
    }

    @Test
    public void testMultipleFriends() throws RepositoryException {
        assertExtract("/microformats/xfn/multiple-friends.html");
        Resource bob = findExactlyOneBlankSubject(vXFN.mePage, baseIRI);
        Resource alice = findExactlyOneBlankSubject(vXFN.mePage, alicesHomepage);
        Resource charlie = findExactlyOneBlankSubject(vXFN.mePage, charliesHomepage);
        assertContains(bob, vXFN.friend, alice);
        assertContains(baseIRI, vXFN.getExtendedProperty("friend"), alicesHomepage);

        assertContains(bob, vXFN.friend, charlie);
        assertContains(baseIRI, vXFN.getExtendedProperty("friend"), charliesHomepage);
    }

    @Test
    public void testSomeLinksWithoutRel() throws RepositoryException {
        assertExtract("/microformats/xfn/some-links-without-rel.html");
        assertNotContains(null, null, alicesHomepage);
        assertContains   (null, null, charliesHomepage);
    }

    @Test
    public void testForSomeReasonICantBeMyOwnSweetheart() throws RepositoryException {
        assertExtract("/microformats/xfn/me-and-sweetheart.html");
        assertModelEmpty();
    }

    @Test
    public void testIgnoreExtraSpacesInRel() throws RepositoryException {
        assertExtract("/microformats/xfn/strip-spaces.html");
        assertContains(null, vXFN.mePage, baseIRI);
    }

    @Test
    public void testMixedCaseATag() throws RepositoryException {
        assertExtract("/microformats/xfn/mixed-case.html");
        assertContains(null, vXFN.mePage, baseIRI);
    }
    @Test
    public void testUpcaseHREF() throws RepositoryException {
        assertExtract("/microformats/xfn/upcase-href.html");
        assertContains(null, vXFN.mePage, baseIRI);
    }
}
