package com.google.code.any23.extractors;

import java.util.Arrays;
import java.util.List;


import com.google.code.any23.extractors.HCardExtractor;
import com.google.code.any23.vocab.FOAF;
import com.google.code.any23.vocab.VCARD;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class HCardExtractorTest extends AbstractMicroformatTestCase {
	//TODO test org/fn conflict
	protected boolean extract(String filename) {
		return new HCardExtractor(baseURI, new HTMLFixture("hcard/"
				+ filename + ".html", true).getHTMLDocument()).extractTo(model);
	}

	
	public void testInferredPerson() {
		assertExtracts("23-abbr-title-everything");
		assertDefaultVCard();
		assertStatementsSize(FOAF.topic, null, 1);
		Resource card = model.listSubjectsWithProperty(RDF.type, VCARD.VCard).nextResource();
		Resource person = card.getProperty(FOAF.topic).getResource();

		assertEquals(person.getProperty(FOAF.name).getString(), card.getProperty(VCARD.fn).getString());
		assertEquals(person.getProperty(FOAF.name).getString(), card.getProperty(VCARD.fn).getString());
	}

	
	
	public void testEMailNotUriReal() {
		assertExtracts("17-email-not-uri");
		assertDefaultVCard();
		assertJohn();
		assertContains(VCARD.email, model
				.createResource("mailto:john@example.com"));
	}

	public void testTel() {
		assertExtracts("21-tel");
		assertDefaultVCard();
		// TODO massively broken because the most widely used ontology is broken by design, so we wait for a rewrite
		// that takes into account the infinite amount of types of tel, adr and mail
		String[] tels = { "+1.415.555.1231", "+1.415.555.1235",
				"+1.415.555.1236", "+1.415.555.1237", "+1.415.555.1238",
				"+1.415.555.1239", "+1.415.555.1240", "+1.415.555.1241",
				"+1.415.555.1242", "+1.415.555.1243" };
		for (String tel : tels) {
			assertContains(VCARD.tel, model.createResource("tel:" + tel));
		}
		Resource telResource = model.createResource("tel:+14155551233");
		assertContains(VCARD.fax, telResource);
		assertContains(VCARD.workTel, telResource);
		assertContains(VCARD.homeTel, telResource);
		assertJohn();
	}

	public void testAbbrTitleEverything() {
		assertExtracts("23-abbr-title-everything");
		assertDefaultVCard();

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

		
		// we define the property in this extractor _but_ we do not parse it
		assertContains(VCARD.geo, (Resource) null);
		// thus we do not cointain these
		// The interaction is in @link RDFMergerTest.java
		assertNotContains(RDF.type, VCARD.Location);
		assertNotContains(null, VCARD.latitude, "37.77");
		assertNotContains(null, VCARD.longitude, "-122.41");

		//see above
		assertContains(VCARD.adr, (Resource) null);
		assertNotContains(RDF.type, VCARD.Address);
		assertNotContains(null, VCARD.post_office_box, "Box 1234");
		assertNotContains(null, VCARD.extended_address, "Suite 100");
		assertNotContains(null, VCARD.street_address, "123 Fake Street");
		assertNotContains(null, VCARD.locality, "San Francisco");
		assertNotContains(null, VCARD.region, "California");
		assertNotContains(null, VCARD.postal_code, "12345-6789");
		assertNotContains(null, VCARD.country_name, "United States of America");
		assertNotContains(null, VCARD.addressType, "work");
	}

	public void testGeoAbbr() {
		assertExtracts("25-geo-abbr");
		assertModelNotEmpty();
		assertContains(VCARD.fn, "Paradise");
		assertContains(RDF.type, VCARD.Organization);
		assertContains(VCARD.organization_name, "Paradise");
		// see above: geo property yes, gteo blank node no
		assertContains(VCARD.geo, (Resource) null);

		assertNotContains(RDF.type, VCARD.Location);
		assertNotContains(null, VCARD.latitude, "30.267991");
		assertNotContains(null, VCARD.longitude, "-97.739568");
	}

	public void testAncestors() {
		assertExtracts("26-ancestors");
		assertModelNotEmpty();

		assertContains(VCARD.fn, "John Doe");
		assertNotContains(null, VCARD.fn,
				"Mister Jonathan John Doe-Smith Medical Doctor");
		assertContains(VCARD.nickname, "JJ");
		assertNotContains(RDF.type, VCARD.Address);
		assertContains(VCARD.tz, "-0700");
		assertContains(VCARD.title, "President");
		assertContains(VCARD.role, "Chief");
		assertContains(VCARD.organization_name, "Intellicorp");
		assertContains(VCARD.organization_unit, "Intelligence");

		assertContains(VCARD.tel, model.createResource("tel:415.555.1234"));
		assertContains(VCARD.uid, "abcdefghijklmnopqrstuvwxyz");
		assertContains(VCARD.note, "this is a note");
		assertContains(VCARD.class_, "public");

		assertNotContains(RDF.type, VCARD.Location);
		assertContains(VCARD.geo, (Resource) null);
		assertNotContains(null, VCARD.latitude, "37.77");
		assertNotContains(null, VCARD.longitude, "-122.41");

		assertContains(RDF.type, VCARD.Name);
		assertContains(VCARD.additional_name, "John");
		assertContains(VCARD.given_name, "Jonathan");
		assertContains(VCARD.family_name, "Doe-Smith");
		assertContains(VCARD.honorific_prefix, "Mister");
		assertContains(VCARD.honorific_suffix, "Medical Doctor");

		assertNotContains(null, VCARD.post_office_box, "Box 1234");
		assertNotContains(null, VCARD.extended_address, "Suite 100");
		assertNotContains(null, VCARD.street_address, "123 Fake Street");
		assertNotContains(null, VCARD.locality, "San Francisco");
		assertNotContains(null, VCARD.region, "California");
		assertNotContains(null, VCARD.postal_code, "12345-6789");
		assertNotContains(null, VCARD.country_name, "United States of America");
		assertNotContains(null, VCARD.addressType, "work");
	}

	// solved in PJ
	public void testfnOrg() {
		assertExtracts("30-fn-org");
		assertModelNotEmpty();
		assertStatementsSize(RDF.type, VCARD.VCard, 5);
		StmtIterator iter = model.listStatements(null, RDF.type, VCARD.VCard);
		while (iter.hasNext()) {
			Resource card = iter.nextStatement().getSubject();
			assertNotNull(card.getProperty(VCARD.fn));
			String name = card.getProperty(VCARD.fn).getString();

			assertNotNull(card.getProperty(VCARD.org));
			String org = card.getProperty(VCARD.org).getResource()
					.getRequiredProperty(VCARD.organization_name).getString();

			if (name.equals("Dan Connolly")) {
				assertNotNull(card.getProperty(VCARD.n));
				assertFalse(name.equals(org));
			} else {
				assertNull(card.getProperty(VCARD.n));
				assertEquals(name, org);
			}
		}
	}

	public void testInclude() {
		assertExtracts("31-include");
		assertModelNotEmpty();
		assertStatementsSize(RDF.type, VCARD.VCard, 3);
		assertStatementsSize(VCARD.email, null, 3);

		ResIterator iter = model
				.listSubjectsWithProperty(RDF.type, VCARD.VCard);
		while (iter.hasNext()) {
			Resource vcard = iter.nextResource();

			assertTrue(vcard.hasProperty(VCARD.fn));
			assertEquals("Brian Suda", vcard.getProperty(VCARD.fn).getString());

			assertTrue(vcard.hasProperty(VCARD.url));
			String url = vcard.getProperty(VCARD.url).getResource().getURI();
			assertEquals("http://suda.co.uk/", url);

			Resource name = vcard.getProperty(VCARD.n).getResource();
			assertEquals("Brian", name.getProperty(VCARD.given_name)
					.getString());
			assertEquals("Suda", name.getProperty(VCARD.family_name)
					.getString());

			//include'd data
			assertTrue(vcard.hasProperty(VCARD.email));
			String mail = vcard.getProperty(VCARD.email).getResource().getURI();
			assertEquals("mailto:correct@example.com", mail);
		}
	}

	public void testHeader() {
		assertExtracts("32-header");
		assertModelNotEmpty();
		// check fn, name, family, nick
		assertJohn();

		ResIterator iter = model
				.listSubjectsWithProperty(RDF.type, VCARD.VCard);
		Resource example = model.createResource("http://example.org/");
		while (iter.hasNext()) {
			Resource card = iter.nextResource();
			assertTrue(card.hasProperty(VCARD.fn));

			String fn = card.getProperty(VCARD.fn).getString();
			if ("Jane Doe".equals(fn)) {
				assertFalse(card.hasProperty(VCARD.url));
				assertFalse(card.hasProperty(VCARD.org));
			} else {
				assertTrue("John Doe".equals(fn) || "Brian Suda".equals(fn));

				assertTrue(card.hasProperty(VCARD.url));
				assertEquals(example, card.getProperty(VCARD.url).getResource());

				assertTrue(card.hasProperty(VCARD.org));
				Resource org = card.getProperty(VCARD.org).getResource();
				assertContains(org, RDF.type, VCARD.Organization);
				assertNotNull(org);
				assertTrue(card.hasProperty(VCARD.org));
				assertTrue(org.hasProperty(VCARD.organization_name));
				assertEquals("example.org", org.getProperty(
						VCARD.organization_name).getString());
			}
		}
		//just to be sure there are no spurious statements
		assertStatementsSize(VCARD.org, null, 2);
		assertStatementsSize(VCARD.url, example, 2);
	}

	public void testAreaFull() {
		assertExtracts("33-area");
		assertModelNotEmpty();
		assertStatementsSize(RDF.type, VCARD.VCard, 5);

		StmtIterator iter = model.listStatements(null, RDF.type, VCARD.VCard);
		while (iter.hasNext()) {
			Resource vcard = (Resource) iter.nextStatement().getSubject();
			assertNotNull(vcard.getProperty(VCARD.fn));
			String fn = vcard.getProperty(VCARD.fn).getString();
			assertNotNull(vcard.getProperty(VCARD.url));
			String url = vcard.getProperty(VCARD.url).getResource().getURI();
			assertNotNull(vcard.getProperty(VCARD.email));
			String mail = vcard.getProperty(VCARD.email).getResource().getURI();
			assertEquals("Joe Public", fn);
			assertEquals("http://example.com/", url);
			assertEquals("mailto:joe@example.com", mail);
		}
	}

	public void testCategories() {
		assertExtracts("36-categories");
		assertModelNotEmpty();
		assertContains(VCARD.given_name, "Joe");
		assertContains(VCARD.given_name, "john");
		assertContains(VCARD.family_name, "doe");
		assertContains(VCARD.family_name, "User");
		assertContains(VCARD.fn, "john doe");
		assertContains(VCARD.fn, "Joe User");

		String[] cats = { "C1", "C2a", "C3", "C4", "C5", "C6", "C7", "C9",
				"luser", "User", "D1", "D2", "D3" };
		for (String cat : cats)
			assertContains(VCARD.category, cat);
		assertNotContains(null, VCARD.category, "D4");
	}

	public void testSingleton() {
		// this tests probably tests that e just get the first fn and so on
		assertExtracts("37-singleton");
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

		// why 0? because the extractor does not look at geo uF!
		assertStatementsSize(RDF.type, VCARD.Location, 0);
		// one is actually used
		assertStatementsSize(VCARD.geo, (Resource) null, 1);

		assertNotContains(null, VCARD.latitude, "123.45");
		assertNotContains(null, VCARD.longitude, "67.89");

		assertStatementsSize(VCARD.uid, null, 1);
		assertContains(VCARD.uid, "unique-id-1");
	}

	public void testUidFull() {
		assertExtracts("38-uid");
		assertModelNotEmpty();
		assertStatementsSize(RDF.type, VCARD.VCard, 4);
		StmtIterator iter = model.listStatements(null, RDF.type, VCARD.VCard);

		while (iter.hasNext()) {
			Resource vcard = (Resource) iter.nextStatement().getSubject();
			assertNotNull(vcard.getProperty(VCARD.fn));
			String fn = vcard.getProperty(VCARD.fn).getString();
			assertEquals("Ryan King", fn);

			assertNotNull(vcard.getProperty(VCARD.n));
			Resource n = vcard.getProperty(VCARD.n).getResource();
			assertNotNull(n);
			assertNotNull(n.getProperty(VCARD.given_name));
			assertEquals("Ryan", n.getProperty(VCARD.given_name).getString());
			assertNotNull(n.getProperty(VCARD.family_name));
			assertEquals("King", n.getProperty(VCARD.family_name).getString());

			assertNotNull(vcard.getProperty(VCARD.url));
			Resource url = vcard.getProperty(VCARD.url).getResource();

			assertNotNull(vcard.getProperty(VCARD.uid));
			String uid = vcard.getProperty(VCARD.uid).getString();

			assertEquals("http://theryanking.com/contact/", url.getURI());
			assertEquals("http://theryanking.com/contact/", uid);

		}
	}

	public void testRomanianWikipedia() {
		assertExtracts("40-fn-inside-adr");
		assertModelNotEmpty();
		assertStatementsSize(RDF.type, VCARD.VCard, 1);
		StmtIterator iter = model.listStatements(null, RDF.type, VCARD.VCard);

		while (iter.hasNext()) {
			Resource card = (Resource) iter.nextStatement().getSubject();
			assertNotNull(card.getProperty(VCARD.fn));
			String fn = card.getProperty(VCARD.fn).getString();
			assertEquals("Berlin", fn);

			assertTrue(card.hasProperty(VCARD.org));
			Resource org = card.getProperty(VCARD.org).getResource();
			assertContains(org, RDF.type, VCARD.Organization);
			assertNotNull(org);
			assertTrue(card.hasProperty(VCARD.org));
			assertTrue(org.hasProperty(VCARD.organization_name));
			assertEquals("Berlin", org.getProperty(VCARD.organization_name)
					.getString());

		}

	}

	/************* from superclass */

	public void testNoMicroformats() {
		assertNotExtracts("../html-without-uf");
		assertModelEmpty();
	}

	public void testBasic() {
		assertExtracts("01-tantek-basic");
		assertModelNotEmpty();
		assertContains(RDF.type, VCARD.VCard);
		assertContains(RDF.type, VCARD.Organization);
		assertContains(RDF.type, VCARD.Name);
		//dumpModel();
		assertContains(VCARD.organization_name, "Technorati");
		Resource person = findExactlyOneBlankSubject(VCARD.fn, model
				.createLiteral("Tantek Celik"));
		assertNotNull(person);
		Resource org = findExactlyOneBlankSubject(VCARD.organization_name,
				model.createLiteral("Technorati"));
		assertNotNull(org);
		assertContains(person, VCARD.url, model
				.createResource("http://tantek.com/"));
		assertContains(person, VCARD.n, (Resource) null);
		assertContains(person, VCARD.org, (Resource) null);

	}

	public void testMultipleclassNamesOnVCard() {
		assertExtracts("02-multiple-class-names-on-vcard");
		assertModelNotEmpty();
		// assertContains( VCARD.organization_name, "Technorati");
		assertStatementsSize(RDF.type, VCARD.VCard, 4);
		Resource name;
		StmtIterator iter = model.listStatements(null, RDF.type, VCARD.VCard);
		while (iter.hasNext()) {
			name = (Resource) iter.nextStatement().getSubject();
			assertContains(name, VCARD.fn, "Ryan King");
		}
	}

	public void testImpliedN() {
		// mighty hack:
		// fn, family, king
		String[] ns = { "Ryan King", "King", "Ryan", "Ryan King", "King",
				"Ryan", "Ryan King", "King", "Ryan", "Brian Suda", "Suda",
				"Brian", "King, Ryan", "King", "Ryan", "King, R", "King", "R",
				"King R", "King", "R", "King R.", "King", "R.",
				"Jesse James Garret", "", "", "Thomas Vander Wall", "", "" };
		List<String> NAMES = Arrays.asList(ns);
		assertExtracts("03-implied-n");
		assertModelNotEmpty();
		// assertContains( VCARD.organization_name, "Technorati");

		StmtIterator iter = model.listStatements((Resource) null, VCARD.fn,
				(Resource) null);
		assertEquals(10, iter.toSet().size());
		iter.close();
		Resource vcard;

		while (iter.hasNext()) {
			vcard = (Resource) iter.nextStatement().getSubject();
			assertContains(vcard, RDF.type, VCARD.VCard);
			Resource name = (Resource) vcard.getProperty(VCARD.n).getObject()
					.as(Resource.class);
			int idx = NAMES.indexOf(vcard.getProperty(VCARD.fn).getLiteral()
					.getString());
			assertTrue("not in names", idx >= 0);
			assertEquals(NAMES.get(idx + 1), name
					.getProperty(VCARD.family_name).getLiteral().getString());
			assertEquals(NAMES.get(idx + 2), name.getProperty(VCARD.given_name)
					.getLiteral().getString());
		}
		iter.close();
	}

	public void testIgnoreUnknowns() {
		assertExtracts("04-ignore-unknowns");
		assertDefaultVCard();
		assertContains(VCARD.fn, "Ryan King");
		assertContains(VCARD.n, (Resource) null);
		assertContains(null, "Ryan");
		assertContains(VCARD.given_name, "Ryan");
		assertContains(VCARD.family_name, "King");
	}

	public void testMailto1() {
		assertExtracts("05-mailto-1");
		assertDefaultVCard();
		assertContains(VCARD.fn, "Ryan King");
		assertContains(RDF.type, VCARD.Name);

		assertContains(VCARD.email, model
				.createResource("mailto:ryan@technorati.com"));

		assertContains(VCARD.given_name, "Ryan");
		assertContains(VCARD.family_name, "King");
	}

	public void testMailto2() {
		assertExtracts("06-mailto-2");
		assertDefaultVCard();
		assertContains(VCARD.fn, "Brian Suda");

		assertContains(VCARD.email, model
				.createResource("mailto:brian@example.com"));
		assertContains(VCARD.given_name, "Brian");
		assertContains(VCARD.family_name, "Suda");
	}

	public void testRelativeUrl() {
		assertExtracts("07-relative-url");
		assertDefaultVCard();
		assertJohn();
		assertContains(VCARD.url, model.createResource(baseURI + "home/blah"));
	}

	public void testRelativeUrlBase() {
		assertExtracts("08-relative-url-base");
		assertDefaultVCard();
		assertContains(VCARD.url, model.createResource(baseURI + "home/blah"));
		assertJohn();
	}

	public void testRelativeUrlXmlBase1() {
		assertExtracts("09-relative-url-xmlbase-1");
		assertDefaultVCard();
		assertContains(VCARD.url, model.createResource(baseURI + "home/blah"));
		assertJohn();
	}

	public void testRelativeUrlXmlBase2() {
		assertExtracts("10-relative-url-xmlbase-2");
		assertDefaultVCard();
		assertContains(VCARD.url, model.createResource(baseURI + "home/blah"));
		assertJohn();
	}

	public void testMultipleUrls() {
		assertExtracts("11-multiple-urls");
		assertDefaultVCard();
		assertContains(VCARD.url, model
				.createResource("http://example.com/foo"));
		assertContains(VCARD.url, model
				.createResource("http://example.com/bar"));

		assertJohn();
	}

	public void testImageSrc() {
		assertExtracts("12-img-src-url");
		assertDefaultVCard();
		assertJohn();
	}

	public void testPhotoLogo() {
		assertExtracts("13-photo-logo");
		assertDefaultVCard();
		assertContains(VCARD.photo, model
				.createResource("http://example.org/picture.png"));
		assertContains(VCARD.logo, model
				.createResource("http://example.org/picture.png"));
		assertJohn();
	}

	public void testImgSrcDataUrl() {
		assertExtracts("14-img-src-data-url");
		assertDefaultVCard();
		Resource data = model
				.createResource("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAMAAAAp4XiDAAAABGdBTUEAAK/"
						+ "INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAASUExURf///8zMzJmZmWZmZ"
						+ "jMzMwAAAPOPemkAAAM1SURBVHjaYmBgYGBkYQUBFkYWFiCPCchixQAMCCZAACF0MAMVM4K4TFh0IGsBCC"
						+ "AkOxhYmBnAAKaHhZkZmxaAAGJgYIbpYGBihGgBWsTMzMwE4jIhaWGAYoAAYmCECDExYAcwGxkg5oNIgAB"
						+ "igDqLARdgZmGB2wICrKwAAcSA3xKgIxlZ0PwCEEAMBCxhgHoWSQtAADFAAxgfYEJ1GEAAQbQw4tUCsocB"
						+ "YQVAADEgu4uRkREeUCwszEwwLhOKLQABhNDCBA4aSDgwwhIAJKqYUPwCEEAMUK/AUwnc9aywJMCI7DAgA"
						+ "AggBohZ8JTBhGIJzCoWZL8ABBCYidAB8RUjWppkYUG2BSCAGMDqEMZiswUtXgACiAHsFYixTMywGGLGpgU"
						+ "WYgABxAA2mQkWCMyMqFoYmdD8ACQAAogBHJHMrCxg1cyIiICmCkYWDFsAAgiihYmZCewFFpR0BfI3LLch+"
						+ "QUggBiQ0iQjEyMDmh54qCBlUIAAYsCRJsElADQvgWKTlRGeKwECiAF3XgGmMEYQYADZzcoA9z5AAMG9RQC"
						+ "AtEC9DxBADFiyFyMjVi0wABBAWLQwQdIiuhYGWJIACCBg+KKUJ9BoBRdS2LQALQMIIGDQIEmwAO1kYcVWH"
						+ "CDZAhBAqFqYmOAxj2YNtAwDAYAAYmDEiBYWzHKKkRERYiwAAYSphZEZwxZGZiZQVEJTJkAAMTCyokc7M5o"
						+ "ORlC5wcoEjxeAAAJqQXU0UB6W5WFmABMtEzMi1wEEEFAbE0YyAUuzMMEsYQalMkQSBQggUDmNPU3C9IA4L"
						+ "CxI+QUggEBiKOU8yExgqccCL3chnkPKlQABhGo6ejHBDKmdUHMlQAAhhQvQaGZGkBIkjcAMywLmI+VKgAB"
						+ "CSowsTJhZkhlWXiBpAQggYBqBZl9GVOdBcz0LZqEEEEAMqLULMBLg1THWog9IAwQQA0qiZcRW5aPbAhBAD"
						+ "Cg1El4tMAAQQAxoiZYZXnTh1AIQQAzo2QlYpDDjcBgrxGEAAcSAJTthswmiBUwDBBC2GpkZJTaRvQ+mAQK"
						+ "IAUuuxdZWQvILQABBmSxMjBj5EpcWgACCMoFOYYSpZyHQHgMIMACt2hmoVEikCQAAAABJRU5ErkJggg==");

		assertContains(VCARD.photo, data);
		assertContains(VCARD.logo, data);
		assertJohn();
	}

	protected void assertJohn() {
		assertContains(VCARD.fn, "John Doe");
		assertContains(VCARD.given_name, "John");
		assertContains(VCARD.family_name, "Doe");
	}

	public void testHonorificAdditionalSingle() {
		assertExtracts("15-honorific-additional-single");
		assertDefaultVCard();
//		dumpModel();
		assertContains(VCARD.fn, "Mr. John Maurice Doe, Ph.D.");

		assertContains(VCARD.honorific_prefix, "Mr.");
		assertContains(VCARD.honorific_suffix, "Ph.D.");

		assertContains(VCARD.given_name, "John");
		assertContains(VCARD.additional_name, "Maurice");
		assertContains(VCARD.family_name, "Doe");
	}

	public void testHonorificAdditionalMultiple() {
		assertExtracts("16-honorific-additional-multiple");
		assertDefaultVCard();
		assertContains(VCARD.honorific_prefix, "Mr.");
		assertContains(VCARD.honorific_prefix, "Dr.");

		assertContains(VCARD.honorific_suffix, "Ph.D.");
		assertContains(VCARD.honorific_suffix, "J.D.");

		assertContains(VCARD.given_name, "John");
		assertContains(VCARD.additional_name, "Maurice");
		assertContains(VCARD.additional_name, "Benjamin");
		assertContains(VCARD.family_name, "Doe");

		assertContains(VCARD.fn,
				"Mr. Dr. John Maurice Benjamin Doe Ph.D., J.D.");
	}

	public void testEMailNotUri() {
		assertExtracts("17-email-not-uri");
		assertDefaultVCard();
		assertJohn();
		// does not work here but is tested and working in the POJO extractor
		//assertContains(VCARD.email, "john@example.com");
	}

	public void testObjectDataHttpUri() {
		assertExtracts("18-object-data-http-uri");
		assertDefaultVCard();
		assertJohn();
	}

	public void testObjectDataDataUri() {
		assertExtracts("19-object-data-data-uri");
		assertDefaultVCard();

		// Resource uri= model.createResource("uri:http://example.com/foo.png");
		// assertContains(VCARD.photo, uri);
		// assertContains(VCARD.logo, uri);
		// assertContains(VCARD.url, uri);

		assertJohn();
	}

	public void testImgAlt() {
		assertExtracts("20-image-alt");
		assertDefaultVCard();
		Resource uri = model.createResource("http://example.com/foo.png");
		assertContains(VCARD.photo, uri);
		assertContains(VCARD.logo, uri);
		assertJohn();
	}

	public void testAdr() {
		assertExtracts("22-adr");
		assertDefaultVCard();
		assertJohn();
		assertStatementsSize(RDF.type, VCARD.Address, 0);
	}

	public void testMultipleValueConcat() {
		assertNotExtracts("24-multiple-value-concat");
		assertModelEmpty();
	}

	public void testBirthDayDate() {
		assertExtracts("27-bday-date");
		assertModelNotEmpty();
		assertContains(VCARD.fn, "john doe");
		assertContains(VCARD.given_name, "john");
		assertContains(VCARD.family_name, "doe");
		assertContains(VCARD.bday, "2000-01-01");
	}

	public void testBirthDayDateTime() {
		assertExtracts("28-bday-datetime");
		assertModelNotEmpty();
		assertContains(VCARD.fn, "john doe");
		assertContains(VCARD.given_name, "john");
		assertContains(VCARD.family_name, "doe");
		assertContains(VCARD.bday, "2000-01-01T00:00:00");
	}

	public void testBirthDayDateTimeTimeZone() {
		assertExtracts("29-bday-datetime-timezone");
		assertModelNotEmpty();
		assertContains(VCARD.fn, "john doe");
		assertContains(VCARD.given_name, "john");
		assertContains(VCARD.family_name, "doe");
		assertContains(VCARD.bday, "2000-01-01T00:00:00-0800");
	}

	public void testArea() {
		assertExtracts("33-area");
		assertModelNotEmpty();
		assertStatementsSize(RDF.type, VCARD.VCard, 5);
		//		dumpModel();
		StmtIterator iter = model.listStatements(null, RDF.type, VCARD.VCard);
		while (iter.hasNext()) {
			Resource vcard = (Resource) iter.nextStatement().getSubject();
			
			assertTrue(vcard.hasProperty(VCARD.fn));
			assertEquals("Joe Public", vcard.getProperty(VCARD.fn).getString());
			assertNotNull(vcard.getProperty(VCARD.url));
			String url = vcard.getProperty(VCARD.url).getResource().getURI();
			assertNotNull(vcard.getProperty(VCARD.email));
			String mail = vcard.getProperty(VCARD.email).getResource().getURI();
			assertEquals("http://example.com/", url);
			assertEquals("mailto:joe@example.com", mail);
		}
		iter.close();
		
		// check that there are 4 organizations
		assertStatementsSize(RDF.type, VCARD.Organization, 4);
		iter = model.listStatements(null, RDF.type, VCARD.Organization);
		while (iter.hasNext()) {
			Resource org = iter.nextStatement().getSubject();
			assertContains(null, VCARD.org, org);
			
			assertTrue(org.hasProperty(VCARD.organization_name));
			assertEquals("Joe Public", org.getProperty(VCARD.organization_name).getString());
		}
		
	}

	public void testNotes() {
		assertExtracts("34-notes");
		assertModelNotEmpty();
		StmtIterator iter = model.listStatements(null, RDF.type, VCARD.VCard);
		//		dumpModel();
		while (iter.hasNext()) {
			Resource vcard =  iter.nextStatement().getSubject();
			String fn = vcard.getProperty(VCARD.fn).getString();
			String mail = vcard.getProperty(VCARD.email).getResource().getURI();
			assertEquals("Joe Public", fn);

			assertEquals("mailto:joe@example.com", mail);
		}
		// TODO;
		//		assertContains(VCARD.note, "Note 1Note 3Note 4 with a ; and a , to be escaped");
	}

	public void testIncludePattern() {
		assertExtracts("35-include-pattern");
		assertModelNotEmpty();
		assertStatementsSize(RDF.type, VCARD.VCard, 3);

		ResIterator iter = model.listSubjectsWithProperty(RDF.type, VCARD.Name);
		while (iter.hasNext()) {
			Resource name =  iter.nextResource();
			assertTrue(name.hasProperty(VCARD.given_name));
			String gn = name.getProperty(VCARD.given_name).getString();
			assertEquals("James", gn);
			assertTrue(name.hasProperty(VCARD.family_name));
			String fn = name.getProperty(VCARD.family_name).getString();
			assertEquals("Levine", fn);
		}
		iter.close();
		
		assertStatementsSize(RDF.type, VCARD.Organization, 2);
		iter = model.listSubjectsWithProperty(RDF.type, VCARD.Organization);
		while (iter.hasNext()) {
			Resource org = iter.nextResource();
			assertTrue(org.hasProperty(VCARD.organization_name));
			assertEquals("SimplyHired",org.getProperty(VCARD.organization_name).getString());
			
			ResIterator ri = model.listSubjectsWithProperty(VCARD.org, org);
			while(ri.hasNext()) {
				Resource vcard = ri.nextResource();	
				assertTrue(vcard.hasProperty(VCARD.title));
				assertEquals("Microformat Brainstormer",vcard.getProperty(VCARD.title).getString());
			}
			ri.close();
		
		}
	
	}

	public void testUid() {
		assertExtracts("38-uid");
		assertModelNotEmpty();
		assertStatementsSize(RDF.type, VCARD.VCard, 4);
		StmtIterator iter = model.listStatements(null, RDF.type, VCARD.VCard);
		while (iter.hasNext()) {
			Resource vcard = (Resource) iter.nextStatement().getSubject();
			assertTrue(vcard.hasProperty(VCARD.fn));
			String fn = vcard.getProperty(VCARD.fn).getString();
			assertTrue(vcard.hasProperty(VCARD.url));
			String url = vcard.getProperty(VCARD.url).getResource().getURI();
			assertTrue(vcard.hasProperty(VCARD.uid));
			String uid = vcard.getProperty(VCARD.uid).getString();
			assertEquals("Ryan King", fn);
			assertEquals("http://theryanking.com/contact/",url);
			assertEquals("http://theryanking.com/contact/", uid);

		}
	}
	
	public void testIgnoreChildren() {
		assertExtracts("41-ignore-children");
		assertModelNotEmpty();
		assertStatementsSize(RDF.type, VCARD.VCard, 1);
		assertContains(VCARD.fn, "Melanie Kl\u00f6\u00df");
		assertContains(VCARD.email, model.createResource("mailto:mkloes@gmail.com"));
		assertContains(VCARD.adr,(Resource) null);
		assertNotContains(null, VCARD.postal_code,"53127");
		assertNotContains(null, VCARD.locality,"Bonn");
		assertNotContains(null, VCARD.street_address,"Ippendorfer Weg. 24");
		assertNotContains(null, VCARD.country_name,"Germany");		
		assertNotContains(null, VCARD.url, (Resource) null);
		
	}

	protected void assertDefaultVCard() {
		assertModelNotEmpty();
		assertStatementsSize(RDF.type, VCARD.VCard, 1);
	}

}
