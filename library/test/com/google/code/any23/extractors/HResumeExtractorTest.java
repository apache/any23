package com.google.code.any23.extractors;

import java.util.HashSet;
import java.util.Set;

import org.deri.any23.extractor.html.AbstractMicroformatTestCase;
import org.deri.any23.extractor.html.AdrExtractor;
import org.deri.any23.extractor.html.HCalendarExtractor;
import org.deri.any23.extractor.html.HCardExtractor;
import org.deri.any23.extractor.html.HResumeExtractor;
import org.deri.any23.extractor.html.HTMLDocument;
import org.deri.any23.extractor.html.HTMLFixture;
import org.deri.any23.vocab.DOAC;
import org.deri.any23.vocab.FOAF;
import org.deri.any23.vocab.ICAL;
import org.deri.any23.vocab.VCARD;


import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class HResumeExtractorTest extends AbstractMicroformatTestCase {
	
	public void testNoMicroformats() {
		assertNotExtracts("../html-without-uf");
		assertModelEmpty();
	}
	
	public void testLinkedIn() {
		assertExtracts("steveganz");
		assertModelNotEmpty();
		assertStatementsSize(RDF.type, FOAF.Person, 1);
		Resource person = findExactlyOneBlankSubject(RDF.type, FOAF.Person);
		assertTrue(person.hasProperty(DOAC.summary));
		assertEquals("Steve Ganz is passionate about connecting people,\n" +
				"semantic markup, sushi, and disc golf - not necessarily in that order.\n" +
				"Currently obsessed with developing the user experience at LinkedIn,\n" +
				"Steve is a second generation Silicon Valley geek and a veteran web\n" +
				"professional who has been building human-computer interfaces since 1994.",
				     person.getRequiredProperty(DOAC.summary).getString());
		//usual trick: we have a connection but not data abvout the card
		assertTrue(person.hasProperty(FOAF.isPrimaryTopicOf));
		assertStatementsSize(RDF.type, VCARD.VCard, 0);
		
		assertStatementsSize(DOAC.experience, null, 7);
		assertStatementsSize(DOAC.education, null, 2);		
		assertStatementsSize(DOAC.affiliation, null, 8);
		
	}
	
	public void testLinkedInComplete() {
		HTMLDocument doc = new HTMLFixture("hresume/steveganz.html", true).getHTMLDocument();
		assertNotNull(doc);
		boolean found = new HResumeExtractor(baseURI, doc).extractTo(model);
		found = found && new HCardExtractor(baseURI, doc).extractTo(model);
		found = found && new HCalendarExtractor(baseURI, doc).extractTo(model);
		found = found && new AdrExtractor(baseURI, doc).extractTo(model);
		assertStatementsSize(RDF.type, FOAF.Person, 1);
		assertStatementsSize(RDF.type, ICAL.Vcalendar, 1);
		assertStatementsSize(RDF.type, VCARD.Address, 1);
		
		assertStatementsSize(DOAC.experience, null, 7);
		assertStatementsSize(DOAC.education, null, 2);	
		assertStatementsSize(DOAC.affiliation, null, 8);
		assertStatementsSize(RDF.type, VCARD.Organization, 17);

		
		
		StmtIterator iter = model.listStatements(null,VCARD.organization_name, (String) null);
		Set<String> set = new HashSet<String>();
		while(iter.hasNext())
			set.add(iter.nextStatement().getString());
		iter.close();
		String[] names = new String[] {
				"BayCHI member",
				"UsabilityPro member",
				"Web Standards Design + Development member",
				"Refresh - Promoting design, technology,and usability. member",
				"LinkedIn Corporation",
				"Printable Technologies",
				"McAfee, Inc.",
				"eBay Employees and Alumni Group member",
				"PDGA member",
				"3G Productions",
				"PayPal, an eBay Company",
				"PayPal Alumni on LinkedIn (PALs) member",
				"Collabria, Inc.",
				"Self-employed",
				"Leland High School",
				"Lee Strasberg Theatre and Film Institute",
				"South By Southwest member"};
		for(String name: names) 
			assertTrue("failure with"+name+"\nset is :"+set.toString(), set.remove(name));
		assertTrue(set.isEmpty());

		
		
		
		assertStatementsSize(RDF.type, ICAL.Vevent, 7+2);
		assertStatementsSize(RDF.type, VCARD.VCard, 7+2+8+1);
		
		
		Resource person = findExactlyOneBlankSubject(RDF.type, FOAF.Person);
		assertTrue(person.hasProperty(FOAF.isPrimaryTopicOf));
		Resource card = person.getProperty(FOAF.isPrimaryTopicOf).getResource();
		assertTrue(card.hasProperty(RDF.type));
		assertEquals(VCARD.VCard, card.getRequiredProperty(RDF.type).getResource());
		
		assertTrue(card.hasProperty(VCARD.fn));
		assertEquals("Steve Ganz", card.getRequiredProperty(VCARD.fn).getString());	
		assertEquals(model.createResource("http://steve.ganz.name/"), 
				card.getRequiredProperty(VCARD.url).getResource());
		assertEquals("Principal Web Developer at LinkedIn", 
				card.getRequiredProperty(VCARD.title).getString());
		assertContains(VCARD.family_name, "Ganz");
		assertContains(VCARD.given_name, "Steve");
		assertContains(VCARD.locality,"San Francisco Bay Area");


	}
	
	public void testAnt() {
		assertExtracts("ant");
		assertModelNotEmpty();
		//dumpModel();
		assertStatementsSize(RDF.type, FOAF.Person, 1);
		Resource person = findExactlyOneBlankSubject(RDF.type, FOAF.Person);
		assertTrue(person.hasProperty(DOAC.summary));
		assertEquals("Senior Systems\n              Analyst/Developer.\n              Experienced in the analysis, design and\n              implementation of distributed, multi-tier\n              applications using Microsoft\n              technologies.\n              Specialising in data capture applications on the\n              Web.",
				     person.getRequiredProperty(DOAC.summary).getString());
		//usual trick: we have a connection but not data abvout the card
		assertTrue(person.hasProperty(FOAF.isPrimaryTopicOf));
		assertStatementsSize(RDF.type, VCARD.VCard, 0);
		
		assertStatementsSize(DOAC.experience, null, 16);
		assertStatementsSize(DOAC.education, null, 2);		
		assertStatementsSize(DOAC.affiliation, null, 0);	
	}
	
	
	@Override
	protected boolean extract(String name) {
		HTMLDocument doc = new HTMLFixture("hresume/"+name+".html", true).getHTMLDocument();
		assertNotNull(doc);
		return new HResumeExtractor(baseURI, doc).extractTo(model);
	}
}
