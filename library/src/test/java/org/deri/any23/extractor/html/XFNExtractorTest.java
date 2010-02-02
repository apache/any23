package org.deri.any23.extractor.html;

import org.junit.Assert;
import org.junit.Test;

import org.deri.any23.Helper;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.vocab.FOAF;
import org.deri.any23.vocab.XFN;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;

public class XFNExtractorTest extends AbstractMicroformatTestCase {
    private final static URI bobsHomepage = baseURI;
    private final static URI alicesHomepage = Helper.uri("http://alice.example.com/");
    private final static URI charliesHomepage = Helper.uri("http://charlie.example.com/");

    protected ExtractorFactory<?> getExtractorFactory() {
        return XFNExtractor.factory;
    }

    @Test
    public void testNoMicroformats() throws RepositoryException {
        assertExtracts("html-without-uf.html");
        Assert.assertTrue(conn.isEmpty());
    }

    @Test
    public void testLinkWithoutRel() throws RepositoryException {
        assertExtracts("xfn/no-rel.html");
        Assert.assertTrue(conn.isEmpty());
    }

    @Test
    public void testNoXFNRel() throws RepositoryException {
        assertExtracts("xfn/no-valid-rel.html");
        Assert.assertTrue(conn.isEmpty());
    }

    @Test
    public void testDetectPresenceOfXFN() throws RepositoryException {
        assertExtracts("xfn/simple-me.html");
    }

    @Test
    public void testSimpleMeLink() throws RepositoryException {
        assertExtracts("xfn/simple-me.html");
        Resource person = findExactlyOneBlankSubject(RDF.TYPE, FOAF.Person);
        assertContains(person, XFN.mePage, baseURI);
        assertContains(person, XFN.mePage, bobsHomepage);
    }

    @Test
    public void testRelativeURIisResolvedAgainstBase() throws RepositoryException {
        assertExtracts("xfn/with-relative-uri.html");
        assertContains(null, XFN.mePage, Helper.uri("http://bob.example.com/foo"));
    }

    @Test
    public void testParseTagSoup() throws RepositoryException {
        assertExtracts("xfn/tagsoup.html");
        Resource person = findExactlyOneBlankSubject(RDF.TYPE, FOAF.Person);
        assertContains(person, XFN.mePage, baseURI);
    }

    @Test
    public void testSimpleFriend() throws RepositoryException {
        assertExtracts("xfn/simple-friend.html");
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
        assertExtracts("xfn/multiple-rel.html");
        Resource bob = findExactlyOneBlankSubject(XFN.mePage, baseURI);
        Resource alice = findExactlyOneBlankSubject(XFN.mePage, alicesHomepage);
        assertContains(bob, XFN.friend, alice);
        assertContains(baseURI, XFN.getExtendedProperty("friend"), alicesHomepage);

        assertContains(bob, XFN.sweetheart, alice);
        assertContains(baseURI, XFN.getExtendedProperty("sweetheart"), alicesHomepage);
    }

    @Test
    public void testMultipleFriends() throws RepositoryException {
        assertExtracts("xfn/multiple-friends.html");
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
        assertExtracts("xfn/some-links-without-rel.html");
        Assert.assertFalse(conn.hasStatement(null, null, alicesHomepage, false));
        assertContains(null, null, charliesHomepage);
    }

    @Test
    public void testForSomeReasonICantBeMyOwnSweetheart() throws RepositoryException {
        assertExtracts("xfn/me-and-sweetheart.html");
        assertModelEmpty();
    }

    @Test
    public void testIgnoreExtraSpacesInRel() throws RepositoryException {
        assertExtracts("xfn/strip-spaces.html");
        assertContains(null, XFN.mePage, baseURI);
    }

    @Test
    public void testMixedCaseATag() throws RepositoryException {
        assertExtracts("xfn/mixed-case.html");
        assertContains(null, XFN.mePage, baseURI);
    }
    @Test
    public void testUpcaseHREF() throws RepositoryException {
        assertExtracts("xfn/upcase-href.html");
        assertContains(null, XFN.mePage, baseURI);
    }
}
