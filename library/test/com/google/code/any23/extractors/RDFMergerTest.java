package com.google.code.any23.extractors;

import java.util.HashMap;
import java.util.Map;


import com.google.code.any23.HTMLDocument;
import com.google.code.any23.extractors.AdrExtractor;
import com.google.code.any23.extractors.GeoExtractor;
import com.google.code.any23.extractors.HCardExtractor;
import com.google.code.any23.extractors.HReviewExtractor;
import com.google.code.any23.extractors.RDFMerger;
import com.google.code.any23.extractors.XFNExtractor;
import com.google.code.any23.vocab.FOAF;
import com.google.code.any23.vocab.REVIEW;
import com.google.code.any23.vocab.VCARD;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

public class RDFMergerTest extends AbstractMicroformatTestCase {

	public void testNoMicroformats() {
		assertNotExtracts("../html-without-uf");
		assertModelEmpty();
	}

	public void test01XFNFoaf() {
		assertExtracts("01-xfn-foaf");
		assertModelNotEmpty();
		assertStatementsSize(RDF.type, VCARD.VCard, 1);
		Property topic = FOAF.topic;
		// vard topic melanie
		// XFN uses isPrimaryTopicOf
		assertStatementsSize(topic, null, 1);
		Resource vcard = findExactlyOneBlankSubject(RDF.type, VCARD.VCard);
		Resource person = vcard.getRequiredProperty(FOAF.topic).getResource();
		String fn = vcard.getRequiredProperty(VCARD.fn).getString();
		String name = person.getRequiredProperty(FOAF.name).getString();
		assertEquals(fn, name);
		Resource blank = findExactlyOneBlankSubject(OWL.sameAs, person);
		assertContains(blank, RDF.type, FOAF.Person);
		assertContains(blank, FOAF.isPrimaryTopicOf, thePage);
	}

	public void testAbbrTitleEverything() {
		extractHCardAndRelated("hcard/23-abbr-title-everything.html");

		assertContains(VCARD.fn, "John Doe");
		assertContains(VCARD.nickname, "JJ");

		assertContains(VCARD.given_name, "Jonathan");
		assertContains(VCARD.additional_name, "John");
		assertContains(VCARD.family_name, "Doe-Smith");
		assertContains(VCARD.honorific_suffix, "Medical Doctor");

		assertContains(VCARD.title, "President");
		assertContains(VCARD.role, "Chief");
		assertContains(VCARD.tz, "-0700");
		assertContains(VCARD.bday, "2006-04-04");
		assertContains(VCARD.tel, model.createResource("tel:415.555.1234"));
		assertContains(VCARD.uid, "abcdefghijklmnopqrstuvwxyz");
		assertContains(VCARD.class_, "public");
		assertContains(VCARD.note, "this is a note");
		assertContains(VCARD.organization_name, "Intellicorp");
		assertContains(VCARD.organization_unit, "Intelligence");

		assertContains(RDF.type, VCARD.Location);
		assertContains(VCARD.geo, (Resource) null);
		assertContains(VCARD.latitude, "37.77");
		assertContains(VCARD.longitude, "-122.41");

		assertContains(VCARD.post_office_box, "Box 1234");
		assertContains(VCARD.extended_address, "Suite 100");
		assertContains(VCARD.street_address, "123 Fake Street");
		assertContains(VCARD.locality, "San Francisco");
		assertContains(VCARD.region, "California");
		assertContains(VCARD.postal_code, "12345-6789");
		assertContains(VCARD.country_name, "United States of America");
		assertContains(VCARD.addressType, "work");
	}

	public void testAdr() {
		extractHRevAndRelated("hcard/22-adr.html");

		assertStatementsSize(RDF.type, VCARD.Address, 4);
		Map<String,String[]> addresses = new HashMap<String,String[]>(4);
		addresses.put(
				"1233 Main St.",
				new String[] {
						"United States of America", 
						"Beverly Hills",
						"90210",
				"California"});
		addresses.put(
				"1231 Main St.",
				new String[] {
						"United States of America", 
						"Beverly Hills", 
						"90210",
				"California"});
		addresses.put(	
				"Suite 100",
				new String[] {
						"United States of America",
						"Beverly Hills",
						"90210",
						"California"
				});
		addresses.put(
				"1234 Main St.",
				new String[] {
						"United States of America",
						"Beverly Hills", 
						"90210",
						"California"
				});

		ResIterator iter = model.listSubjectsWithProperty(RDF.type, VCARD.Address);
		while (iter.hasNext()) {
			Resource adr = iter.nextResource();
			String street = adr.getRequiredProperty(VCARD.street_address).getString();
			assertNotNull(addresses.get(street));
			assertContains(adr, VCARD.country_name, addresses.get(street)[0]);
			assertContains(adr, VCARD.locality, addresses.get(street)[1]);
			assertContains(adr, VCARD.postal_code, addresses.get(street)[2]);
			assertContains(adr, VCARD.region, addresses.get(street)[3]);
		}
		iter.close();


		//additional things that should be there
		assertContains(VCARD.post_office_box, "PO Box 1234");
		// GR: can't do because the ontology does not have this and I don't want to namesquat
		//assertContains(VCARD.addressType, "home")
	}


	private void extractHCardAndRelated(String filename) {
		HTMLDocument doc = new HTMLFixture(filename, true).getHTMLDocument();
		new HCardExtractor(baseURI, doc).extractTo(model);
		new GeoExtractor(baseURI, doc).extractTo(model);
		new AdrExtractor(baseURI, doc).extractTo(model);
	}

	public void testGeoAbbr() {
		extractHCardAndRelated("hcard/25-geo-abbr.html");
		assertModelNotEmpty();
		assertContains(VCARD.fn, "Paradise");
		assertContains(RDF.type, VCARD.Organization);
		assertContains(VCARD.organization_name, "Paradise");
		assertContains(RDF.type, VCARD.Location);
		assertContains(VCARD.geo, (Resource) null);
		assertContains(VCARD.latitude, "30.267991");
		assertContains(VCARD.longitude, "-97.739568");
	}

	public void testAncestors() {
		extractHCardAndRelated("hcard/26-ancestors.html");
		assertModelNotEmpty();

		assertContains(VCARD.fn, "John Doe");
		assertNotContains(null, VCARD.fn,
		"Mister Jonathan John Doe-Smith Medical Doctor");
		assertContains(VCARD.nickname, "JJ");
		assertContains(RDF.type, VCARD.Address);
		assertContains(VCARD.tz, "-0700");
		assertContains(VCARD.title, "President");
		assertContains(VCARD.role, "Chief");
		assertContains(VCARD.organization_name, "Intellicorp");
		assertContains(VCARD.organization_unit, "Intelligence");

		assertContains(VCARD.tel, model.createResource("tel:415.555.1234"));
		assertContains(VCARD.uid, "abcdefghijklmnopqrstuvwxyz");
		assertContains(VCARD.note, "this is a note");
		assertContains(VCARD.class_, "public");

		assertContains(RDF.type, VCARD.Location);
		assertContains(VCARD.geo, (Resource) null);
		assertContains(null, VCARD.latitude, "37.77");
		assertContains(null, VCARD.longitude, "-122.41");

		assertContains(RDF.type, VCARD.Name);
		assertContains(VCARD.additional_name, "John");
		assertContains(VCARD.given_name, "Jonathan");
		assertContains(VCARD.family_name, "Doe-Smith");
		assertContains(VCARD.honorific_prefix, "Mister");
		assertContains(VCARD.honorific_suffix, "Medical Doctor");

		assertContains(VCARD.post_office_box, "Box 1234");
		assertContains(VCARD.extended_address, "Suite 100");
		assertContains(VCARD.street_address, "123 Fake Street");
		assertContains(VCARD.locality, "San Francisco");
		assertContains(VCARD.region, "California");
		assertContains(VCARD.postal_code, "12345-6789");
		assertContains(VCARD.country_name, "United States of America");
		assertContains(VCARD.addressType, "work");
	}

	public void testSingleton() {
		extractHCardAndRelated("hcard/37-singleton.html");

		assertModelNotEmpty();
		assertStatementsSize(VCARD.fn, null, 1);
		assertContains(VCARD.fn, "john doe 1");

		assertStatementsSize(RDF.type, VCARD.Name, 1);
		assertStatementsSize(VCARD.given_name, null, 1);
		assertContains(VCARD.given_name, "john");
		assertStatementsSize(VCARD.family_name, null, 1);
		assertContains(VCARD.family_name, "doe");
		assertStatementsSize(VCARD.sort_string, null, 1);
		assertContains(VCARD.sort_string, "d");

		assertStatementsSize(VCARD.bday, null, 1);
		assertContains(VCARD.bday, "20060707");
		assertStatementsSize(VCARD.rev, null, 1);
		assertContains(VCARD.rev, "20060707");
		assertStatementsSize(VCARD.class_, null, 1);
		assertContains(VCARD.class_, "public");
		assertStatementsSize(VCARD.tz, null, 1);
		assertContains(VCARD.tz, "+0600");
		// 2 uf, one of them outside the card
		assertStatementsSize(RDF.type, VCARD.Location, 2);
		// one is actually used
		assertStatementsSize(VCARD.geo, (Resource) null, 1);

		assertContains(VCARD.latitude, "123.45");
		assertContains(VCARD.longitude, "67.89");

		assertStatementsSize(VCARD.uid, null, 1);
		assertContains(VCARD.uid, "unique-id-1");
	}


	private void extractHRevAndRelated(String filename) {
		HTMLDocument doc = new HTMLFixture(filename, true).getHTMLDocument();
		extractHCardAndRelated(filename);
		new HReviewExtractor(baseURI, doc).extractTo(model);
	}

	public void test01Basic() {
		extractHRevAndRelated("hreview/01-spec.html");
		assertModelNotEmpty();
		assertNotContains(REVIEW.type, null);

		assertStatementsSize(RDF.type, REVIEW.Review, 1);
		// reviewer, item
		assertStatementsSize(RDF.type, VCARD.VCard, 2);
		// there is one address in the item vcard
		assertStatementsSize(RDF.type, VCARD.Address, 1);

		ResIterator reviews = model.listSubjectsWithProperty(RDF.type,
				REVIEW.Review);
		while (reviews.hasNext()) {
			Resource review = reviews.nextResource();

			assertContains(review, REVIEW.rating, "5");
			assertContains(review, REVIEW.title, "Crepes on Cole is awesome");
			assertContains(review, REVIEW.date, "20050418T2300-0700");

			// TODO keep html
			assertContains(
					REVIEW.text,
					"Crepes on Cole is one of the best little \n"
					+ "      creperies in San Francisco.\n      "
					+ "Excellent food and service. Plenty of tables in a variety of sizes\n"
					+ "      for parties large and small.  "
					+ "Window seating makes for excellent\n      "
					+ "people watching to/from the N-Judah which stops right outside.\n"
					+ "      I've had many fun social gatherings here, as well as gotten\n"
					+ "      plenty of work done thanks to neighborhood WiFi.");

			assertContains(null, REVIEW.hasReview, review);
		}
		// generic checks that vcards are correct, improve
		assertContains(VCARD.fn, "Crepes on Cole");
		assertContains(VCARD.fn, "Tantek");
		assertContains(VCARD.locality, "San Francisco");
		assertContains(VCARD.organization_name, "Crepes on Cole");

	}

	public void test02RatedTags() {
		extractHRevAndRelated("hreview/02-spec-2.html");

		assertStatementsSize(REVIEW.reviewer, null, 1);
		assertStatementsSize(REVIEW.hasReview, null, 1);
		assertModelNotEmpty();
		assertStatementsSize(RDF.type, REVIEW.Review, 1);
		// reviewer, item
		assertStatementsSize(RDF.type, VCARD.VCard, 2);
		// there is one address in the item vcard
		assertStatementsSize(RDF.type, VCARD.Address, 1);

		ResIterator reviews = model.listSubjectsWithProperty(RDF.type,
				REVIEW.Review);
		while (reviews.hasNext()) {
			Resource review = reviews.nextResource();
			assertContains(review, REVIEW.rating,"18");
			assertContains(review, REVIEW.title, "Cafe Borrone");
			assertContains(review, REVIEW.date, "20050428T2130-0700");
			assertContains(null, REVIEW.hasReview, review);
			assertContains(REVIEW.type, "business");
		}
		// generic checks that vcards are correct, improve
		assertContains(VCARD.fn, "Cafe Borrone");
		assertContains(VCARD.fn, "anonymous");
		assertContains(VCARD.organization_name, "Cafe Borrone");
	}

	public void test03NoHcardForItem() {
		extractHRevAndRelated("hreview/03-spec-3.html");

		assertModelNotEmpty();
		assertStatementsSize(RDF.type, REVIEW.Review, 1);
		assertStatementsSize(RDF.type, VCARD.VCard, 1);



		ResIterator reviews = model.listSubjectsWithProperty(RDF.type,
				REVIEW.Review);
		while (reviews.hasNext()) {
			Resource review = reviews.nextResource();
			assertContains(review, REVIEW.rating,"5");
			assertNotContains(REVIEW.title, null);
			assertContains(review, REVIEW.date, "200502");
			// TODO keep html
			assertContains(
					REVIEW.text,
					"\"The people thought they were just being rewarded for " +
					"treating others\n       as they like to be treated, for " +
					"obeying stop signs and curing diseases,\n       for mailing " +
					"letters with the address of the sender... Don't wake me,\n " +
					"      I plan on sleeping in...\"\n     \n     \"Nothing Better\"" +
			" is a great track on this album, too...");
			ResIterator iter = model.listSubjectsWithProperty(REVIEW.hasReview, review);
			while(iter.hasNext()) {
				Resource subj = iter.nextResource();
				assertContains(subj, REVIEW.fn, "The Postal Service: Give Up");
				assertContains(subj, REVIEW.url, model.createResource("http://www.amazon.com/exec/obidos/ASIN/B000089CJI/"));
				assertContains(subj, REVIEW.photo, "http://images.amazon.com/images/P/B000089CJI.01._SCTHUMBZZZ_.jpg");	
			}
		}
		assertContains(VCARD.fn, "Adam Rifkin");
		assertContains(VCARD.url, model.createResource("http://ifindkarma.com/blog/"));
	}


	@Override
	protected boolean extract(String filename) {
		HTMLDocument doc = new HTMLFixture("mixed/"+filename+".html", true).getHTMLDocument();
		boolean result =   new HCardExtractor(baseURI, doc).extractTo(model);
		result = result && new XFNExtractor(baseURI, doc).extractTo(model);
		result = result && new RDFMerger(baseURI, doc).extractTo(model);
		return result;
	}

}
