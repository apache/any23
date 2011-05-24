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
import org.deri.any23.util.RDFHelper;
import org.deri.any23.vocab.FOAF;
import org.deri.any23.vocab.SINDICE;
import org.deri.any23.vocab.XFN;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;

/**
 *
 * Reference Test class for the {@link org.deri.any23.extractor.html.XFNExtractor} extractor.
 *
 */
public class XFNExtractorTest extends AbstractExtractorTestCase {

    private final static URI bobsHomepage = baseURI;

    private final static URI alicesHomepage = RDFHelper.uri("http://alice.example.com/");

    private final static URI charliesHomepage = RDFHelper.uri("http://charlie.example.com/");

    protected ExtractorFactory<?> getExtractorFactory() {
        return XFNExtractor.factory;
    }

    @Test
    public void testNoMicroformats() throws RepositoryException {
        assertExtracts("html/html-without-uf.html");
        assertModelNotEmpty();
        assertStatementsSize(null, null, null, 2);
        assertStatementsSize(SINDICE.getProperty(SINDICE.DATE), (Value) null, 1);
        assertStatementsSize(SINDICE.getProperty(SINDICE.SIZE), (Value) null, 1);
    }

    @Test
    public void testLinkWithoutRel() throws RepositoryException {
        assertExtracts("microformats/xfn/no-rel.html");
        assertModelNotEmpty();
        assertStatementsSize(null, null, null, 2);
        assertStatementsSize(SINDICE.getProperty(SINDICE.DATE), (Value) null, 1);
        assertStatementsSize(SINDICE.getProperty(SINDICE.SIZE), (Value) null, 1);
    }

    @Test
    public void testNoXFNRel() throws RepositoryException {
        assertExtracts("microformats/xfn/no-valid-rel.html");
        assertModelNotEmpty();
        assertStatementsSize(null, null, null, 2);
        assertStatementsSize(SINDICE.getProperty(SINDICE.DATE), (Value) null, 1);
        assertStatementsSize(SINDICE.getProperty(SINDICE.SIZE), (Value) null, 1);
    }

    @Test
    public void testDetectPresenceOfXFN() throws RepositoryException {
        assertExtracts("microformats/xfn/simple-me.html");
    }

    @Test
    public void testSimpleMeLink() throws RepositoryException {
        assertExtracts("microformats/xfn/simple-me.html");
        Resource person = findExactlyOneBlankSubject(RDF.TYPE, FOAF.Person);
        assertContains(person, XFN.mePage, baseURI);
        assertContains(person, XFN.mePage, bobsHomepage);
    }

    @Test
    public void testRelativeURIisResolvedAgainstBase() throws RepositoryException {
        assertExtracts("microformats/xfn/with-relative-uri.html");
        assertContains(null, XFN.mePage, RDFHelper.uri("http://bob.example.com/foo"));
    }

    @Test
    public void testParseTagSoup() throws RepositoryException {
        assertExtracts("microformats/xfn/tagsoup.html");
        Resource person = findExactlyOneBlankSubject(RDF.TYPE, FOAF.Person);
        assertContains(person, XFN.mePage, baseURI);
    }

    @Test
    public void testSimpleFriend() throws RepositoryException {
        assertExtracts("microformats/xfn/simple-friend.html");
        Resource bob = findExactlyOneBlankSubject(XFN.mePage, baseURI);
        Resource alice = findExactlyOneBlankSubject(XFN.mePage, alicesHomepage);
        assertContains(bob, RDF.TYPE, FOAF.Person);
        assertContains(alice, RDF.TYPE, FOAF.Person);
        Assert.assertFalse(alice.equals(bob));
        assertContains(bob, XFN.friend, alice);
        assertContains(baseURI, XFN.getExtendedProperty("friend"), alicesHomepage);
    }

    @Test
    public void testFriendAndSweetheart() throws RepositoryException {
        assertExtracts("microformats/xfn/multiple-rel.html");
        Resource bob = findExactlyOneBlankSubject(XFN.mePage, baseURI);
        Resource alice = findExactlyOneBlankSubject(XFN.mePage, alicesHomepage);
        assertContains(bob, XFN.friend, alice);
        assertContains(baseURI, XFN.getExtendedProperty("friend"), alicesHomepage);

        assertContains(bob, XFN.sweetheart, alice);
        assertContains(baseURI, XFN.getExtendedProperty("sweetheart"), alicesHomepage);
    }

    @Test
    public void testMultipleFriends() throws RepositoryException {
        assertExtracts("microformats/xfn/multiple-friends.html");
        Resource bob = findExactlyOneBlankSubject(XFN.mePage, baseURI);
        Resource alice = findExactlyOneBlankSubject(XFN.mePage, alicesHomepage);
        Resource charlie = findExactlyOneBlankSubject(XFN.mePage, charliesHomepage);
        assertContains(bob, XFN.friend, alice);
        assertContains(baseURI, XFN.getExtendedProperty("friend"), alicesHomepage);

        assertContains(bob, XFN.friend, charlie);
        assertContains(baseURI, XFN.getExtendedProperty("friend"), charliesHomepage);
    }

    @Test
    public void testSomeLinksWithoutRel() throws RepositoryException {
        assertExtracts("microformats/xfn/some-links-without-rel.html");
        Assert.assertFalse(conn.hasStatement(null, null, alicesHomepage, false));
        assertContains(null, null, charliesHomepage);
    }

    @Test
    public void testForSomeReasonICantBeMyOwnSweetheart() throws RepositoryException {
        assertExtracts("microformats/xfn/me-and-sweetheart.html");
        assertModelNotEmpty();
        assertStatementsSize(null, null, null, 2);
        assertStatementsSize(SINDICE.getProperty(SINDICE.DATE), (Value) null, 1);
        assertStatementsSize(SINDICE.getProperty(SINDICE.SIZE), (Value) null, 1);
    }

    @Test
    public void testIgnoreExtraSpacesInRel() throws RepositoryException {
        assertExtracts("microformats/xfn/strip-spaces.html");
        assertContains(null, XFN.mePage, baseURI);
    }

    @Test
    public void testMixedCaseATag() throws RepositoryException {
        assertExtracts("microformats/xfn/mixed-case.html");
        assertContains(null, XFN.mePage, baseURI);
    }
    @Test
    public void testUpcaseHREF() throws RepositoryException {
        assertExtracts("microformats/xfn/upcase-href.html");
        assertContains(null, XFN.mePage, baseURI);
    }
}
