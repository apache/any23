package com.google.code.any23.extractors;




import com.google.code.any23.extractors.XFNExtractor;
import com.google.code.any23.vocab.FOAF;
import com.google.code.any23.vocab.XFN;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;

public class XFNExtractorTest extends AbstractMicroformatTestCase {
	private final static Resource thePage = ResourceFactory.createResource(baseURI.toString());
	private final static Resource bobsHomepage = ResourceFactory.createResource("http://bob.example.com/");
	private final static Resource alicesHomepage = ResourceFactory.createResource("http://alice.example.com/");
	private final static Resource charliesHomepage = ResourceFactory.createResource("http://charlie.example.com/");

	public void testNoMicroformats() {
		assertNotExtracts("../html-without-uf");
		assertTrue(model.isEmpty());
	}
	
	public void testLinkWithoutRel() {
		assertNotExtracts("no-rel");
		assertTrue(model.isEmpty());
	}
	
	public void testNoXFNRel() {
		assertNotExtracts("no-valid-rel");
		assertTrue(model.isEmpty());
	}
	
	public void testDetectPresenceOfXFN() {
		assertExtracts("simple-me");
	}
	
	public void testSimpleMeLink() {
		assertExtracts("simple-me");
		Resource person = findExactlyOneBlankSubject(RDF.type, FOAF.Person);
		assertContains(person, FOAF.isPrimaryTopicOf, thePage);
		assertContains(person, FOAF.isPrimaryTopicOf, bobsHomepage);
	}

	public void testRelativeURIisResolvedAgainstBase() {
		assertExtracts("with-relative-uri");
		assertContains(null, FOAF.isPrimaryTopicOf, model.createResource("http://bob.example.com/foo"));
	}

	public void testParseTagSoup() {
		assertExtracts("tagsoup");
		Resource person = findExactlyOneBlankSubject(RDF.type, FOAF.Person);
		assertContains(person, FOAF.isPrimaryTopicOf, thePage);
		assertContains(person, FOAF.isPrimaryTopicOf, bobsHomepage);
	}

	public void testSimpleFriend() {
		assertExtracts("simple-friend");
		Resource bob = findExactlyOneBlankSubject(FOAF.isPrimaryTopicOf, thePage);
		Resource alice = findExactlyOneBlankSubject(FOAF.isPrimaryTopicOf, alicesHomepage);
		assertContains(bob, RDF.type, FOAF.Person);
		assertContains(alice, RDF.type, FOAF.Person);
		assertFalse(alice.equals(bob));
		assertContains(bob, XFN.friend, alice);
		assertContainsEXFN(thePage, "friend", alicesHomepage);
	}

	public void testFriendAndSweetheart() {
		assertExtracts("multiple-rel");
		Resource bob = findExactlyOneBlankSubject(FOAF.isPrimaryTopicOf, thePage);
		Resource alice = findExactlyOneBlankSubject(FOAF.isPrimaryTopicOf, alicesHomepage);
		assertContains(bob, XFN.friend, alice);
		assertContainsEXFN(thePage, "friend", alicesHomepage);
		
		assertContains(bob, XFN.sweetheart, alice);
		assertContainsEXFN(thePage, "sweetheart", alicesHomepage);

	
	}

	public void testMultipleFriends() {
		assertExtracts("multiple-friends");
		Resource bob = findExactlyOneBlankSubject(FOAF.isPrimaryTopicOf, thePage);
		Resource alice = findExactlyOneBlankSubject(FOAF.isPrimaryTopicOf, alicesHomepage);
		Resource charlie = findExactlyOneBlankSubject(FOAF.isPrimaryTopicOf, charliesHomepage);
		assertContains(bob, XFN.friend, alice);
		assertContainsEXFN(thePage, "friend", alicesHomepage);

		assertContains(bob, XFN.friend, charlie);
		assertContainsEXFN(thePage, "friend", charliesHomepage);
	}
	
	public void testSomeLinksWithoutRel() {
		assertExtracts("some-links-without-rel");
		assertFalse(model.contains(null, null, alicesHomepage));
		assertContains(null, null, charliesHomepage);
	}

	public void testForSomeReasonICantBeMyOwnSweetheart() {
		assertNotExtracts("me-and-sweetheart");
		assertTrue(model.isEmpty());
	}

	public void testIgnoreExtraSpacesInRel() {
		assertExtracts("strip-spaces");
		assertContains(null, FOAF.isPrimaryTopicOf, thePage);
	}

	protected boolean extract(String filename) {
		return new XFNExtractor(baseURI, 
				new HTMLFixture("xfn/"+filename+".html", true).getHTMLDocument()).extractTo(model);
	}
	
	public void testMixedCaseATag() {
		assertExtracts("mixed-case");
		assertContains(null, FOAF.isPrimaryTopicOf, thePage);
	}
	public void testUpcaseHREF() {
		assertExtracts("upcase-href");
		assertContains(null, FOAF.isPrimaryTopicOf, thePage);
	}
}
