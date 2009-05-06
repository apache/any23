package org.deri.any23.extractor.html;

import org.deri.any23.Helper;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.html.XFNExtractor;
import org.deri.any23.vocab.FOAF;
import org.deri.any23.vocab.XFN;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;

public class XFNExtractorTest extends AbstractMicroformatTestCase {
	private final static URI bobsHomepage = Helper.uri("http://bob.example.com/");
	private final static URI alicesHomepage = Helper.uri("http://alice.example.com/");
	private final static URI charliesHomepage = Helper.uri("http://charlie.example.com/");
	
	protected ExtractorFactory<?> getExtractorFactory() {
		return XFNExtractor.factory;
	}
	
	public void testNoMicroformats() throws RepositoryException {
		assertExtracts("html-without-uf.html");
		assertTrue(conn.isEmpty());
	}
	
	public void testLinkWithoutRel() throws RepositoryException {
		assertExtracts("xfn/no-rel.html");
		assertTrue(conn.isEmpty());
	}
	
	public void testNoXFNRel() throws RepositoryException {
		assertExtracts("xfn/no-valid-rel.html");
		assertTrue(conn.isEmpty());
	}
	
	public void testDetectPresenceOfXFN() throws RepositoryException {
		assertExtracts("xfn/simple-me.html");
	}
	
	public void testSimpleMeLink() throws RepositoryException {
		assertExtracts("xfn/simple-me.html");
		Resource person = findExactlyOneBlankSubject(RDF.TYPE, FOAF.Person);
		assertContains(person, FOAF.isPrimaryTopicOf, baseURI);
		assertContains(person, FOAF.isPrimaryTopicOf, bobsHomepage);
	}

	public void testRelativeURIisResolvedAgainstBase() throws RepositoryException {
		assertExtracts("xfn/with-relative-uri.html");
		assertContains(null, FOAF.isPrimaryTopicOf, Helper.uri("http://bob.example.com/foo"));
	}

	public void testParseTagSoup() throws RepositoryException {
		assertExtracts("xfn/tagsoup.html");
		Resource person = findExactlyOneBlankSubject(RDF.TYPE, FOAF.Person);
		assertContains(person, FOAF.isPrimaryTopicOf, baseURI);
		assertContains(person, FOAF.isPrimaryTopicOf, bobsHomepage);
	}

	public void testSimpleFriend() throws RepositoryException {
		assertExtracts("xfn/simple-friend.html");
		Resource bob = findExactlyOneBlankSubject(FOAF.isPrimaryTopicOf, baseURI);
		Resource alice = findExactlyOneBlankSubject(FOAF.isPrimaryTopicOf, alicesHomepage);
		assertContains(bob, RDF.TYPE, FOAF.Person);
		assertContains(alice, RDF.TYPE, FOAF.Person);
		assertFalse(alice.equals(bob));
		assertContains(bob, XFN.friend, alice);
		assertContains(baseURI, XFN.getExtendedProperty("friend"), alicesHomepage);
	}

	public void testFriendAndSweetheart() throws RepositoryException {
		assertExtracts("xfn/multiple-rel.html");
		Resource bob = findExactlyOneBlankSubject(FOAF.isPrimaryTopicOf, baseURI);
		Resource alice = findExactlyOneBlankSubject(FOAF.isPrimaryTopicOf, alicesHomepage);
		assertContains(bob, XFN.friend, alice);
		assertContains(baseURI, XFN.getExtendedProperty("friend"), alicesHomepage);
		
		assertContains(bob, XFN.sweetheart, alice);
		assertContains(baseURI, XFN.getExtendedProperty("sweetheart"), alicesHomepage);
	}

	public void testMultipleFriends() throws RepositoryException {
		assertExtracts("xfn/multiple-friends.html");
		Resource bob = findExactlyOneBlankSubject(FOAF.isPrimaryTopicOf, baseURI);
		Resource alice = findExactlyOneBlankSubject(FOAF.isPrimaryTopicOf, alicesHomepage);
		Resource charlie = findExactlyOneBlankSubject(FOAF.isPrimaryTopicOf, charliesHomepage);
		assertContains(bob, XFN.friend, alice);
		assertContains(baseURI, XFN.getExtendedProperty("friend"), alicesHomepage);

		assertContains(bob, XFN.friend, charlie);
		assertContains(baseURI, XFN.getExtendedProperty("friend"), charliesHomepage);
	}
	
	public void testSomeLinksWithoutRel() throws RepositoryException {
		assertExtracts("xfn/some-links-without-rel.html");
		assertFalse(conn.hasStatement(null, null, alicesHomepage, false));
		assertContains(null, null, charliesHomepage);
	}

	public void testForSomeReasonICantBeMyOwnSweetheart() throws RepositoryException {
		assertNotExtracts("xfn/me-and-sweetheart.html");
		assertTrue(conn.isEmpty());
	}

	public void testIgnoreExtraSpacesInRel() throws RepositoryException {
		assertExtracts("xfn/strip-spaces.html");
		assertContains(null, FOAF.isPrimaryTopicOf, baseURI);
	}

	public void testMixedCaseATag() throws RepositoryException {
		assertExtracts("xfn/mixed-case.html");
		assertContains(null, FOAF.isPrimaryTopicOf, baseURI);
	}
	
	public void testUpcaseHREF() throws RepositoryException {
		assertExtracts("xfn/upcase-href.html");
		assertContains(null, FOAF.isPrimaryTopicOf, baseURI);
	}
}
