package org.deri.any23.extractor.html;

import junit.framework.Assert;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.rdf.RDFUtils;
import org.deri.any23.vocab.SINDICE;
import org.deri.any23.vocab.VCARD;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * {@link org.deri.any23.extractor.html.HCardExtractor} test case.
 */
public class HCardExtractorTest extends AbstractExtractorTestCase {

    private static final SINDICE vSINDICE = SINDICE.getInstance();
    private static final VCARD   vVCARD   = VCARD.getInstance();

    protected ExtractorFactory<?> getExtractorFactory() {
        return HCardExtractor.factory;
    }

    @Test
	public void testEMailNotUriReal() throws RepositoryException {
		assertExtracts("microformats/hcard/17-email-not-uri.html");
		assertDefaultVCard();
		assertJohn();
		assertContains(vVCARD.email, RDFUtils.uri("mailto:john@example.com"));
	}

    @Test
    public void testTel() throws RepositoryException {
		assertExtracts("microformats/hcard/21-tel.html");
		assertDefaultVCard();
		String[] tels = {
                "+1.415.555.1231", "+1.415.555.1235",
				"+1.415.555.1236", "+1.415.555.1237", "+1.415.555.1238",
				"+1.415.555.1239", "+1.415.555.1240", "+1.415.555.1241",
				"+1.415.555.1242", "+1.415.555.1243"
        };
		for (String tel : tels) {
			assertContains(vVCARD.tel, RDFUtils.uri("tel:" + tel));
		}
		Resource telResource = RDFUtils.uri("tel:+14155551233");
		assertContains(vVCARD.fax, telResource);
		assertContains(vVCARD.workTel, telResource);
		assertContains(vVCARD.homeTel, telResource);
		assertJohn();
	}

    @Test
    public void testAbbrTitleEverything() throws RepositoryException {
		assertExtracts("microformats/hcard/23-abbr-title-everything.html");
		assertDefaultVCard();

		assertContains(vVCARD.fn, "John Doe");
		assertContains(vVCARD.nickname, "JJ");

		assertContains(vVCARD.given_name, "Jonathan");
		assertContains(vVCARD.additional_name, "John");
		assertContains(vVCARD.family_name, "Doe-Smith");
		assertContains(vVCARD.honorific_suffix, "Medical Doctor");

		assertContains(vVCARD.title, "President");
		assertContains(vVCARD.role, "Chief");
		assertContains(vVCARD.tz, "-0700");
		assertContains(vVCARD.bday, "2006-04-04");
		assertContains(vVCARD.tel, RDFUtils.uri("tel:415.555.1234"));
		assertContains(vVCARD.uid, "abcdefghijklmnopqrstuvwxyz");
		assertContains(vVCARD.class_, "public");
		assertContains(vVCARD.note, "this is a note");
		assertContains(vVCARD.organization_name, "Intellicorp");
		assertContains(vVCARD.organization_unit, "Intelligence");
		
		// We define the property in this extractor _but_ we do not parse it.
		assertContains(vVCARD.geo, (Resource) null);
		// Thus we do not cointain these.
		// The interaction is in @link RDFMergerTest.java
		assertNotContains(RDF.TYPE, vVCARD.Location);
		assertNotContains(null, vVCARD.latitude, "37.77");
		assertNotContains(null, vVCARD.longitude, "-122.41");

		//see above
		assertContains(vVCARD.adr, (Resource) null);
		assertNotContains(RDF.TYPE, vVCARD.Address);
		assertNotContains(null, vVCARD.post_office_box, "Box 1234");
		assertNotContains(null, vVCARD.extended_address, "Suite 100");
		assertNotContains(null, vVCARD.street_address, "123 Fake Street");
		assertNotContains(null, vVCARD.locality, "San Francisco");
		assertNotContains(null, vVCARD.region, "California");
		assertNotContains(null, vVCARD.postal_code, "12345-6789");
		assertNotContains(null, vVCARD.country_name, "United States of America");
		assertNotContains(null, vVCARD.addressType, "work");
	}

    @Test
    public void testGeoAbbr() throws RepositoryException {
		assertExtracts("microformats/hcard/25-geo-abbr.html");
		assertModelNotEmpty();
		assertContains(vVCARD.fn, "Paradise");
         assertContains(RDF.TYPE, vVCARD.Organization);
		 assertContains(vVCARD.organization_name, "Paradise");
		// See above: geo property yes, gteo blank node no.
		assertContains(vVCARD.geo, (Resource) null);
		assertNotContains(RDF.TYPE, vVCARD.Location);
		assertNotContains(null, vVCARD.latitude, "30.267991");
		assertNotContains(null, vVCARD.longitude, "-97.739568");
	}

    @Test
    public void testAncestors() throws RepositoryException {
		assertExtracts("microformats/hcard/26-ancestors.html");
		assertModelNotEmpty();

		assertContains(vVCARD.fn, "John Doe");
		assertNotContains(
                null,
                vVCARD.fn,
				"Mister Jonathan John Doe-Smith Medical Doctor"
        );
		assertContains(vVCARD.nickname, "JJ");
		assertNotContains(RDF.TYPE, vVCARD.Address);
		assertContains(vVCARD.tz, "-0700");
		assertContains(vVCARD.title, "President");
		assertContains(vVCARD.role, "Chief");
		assertContains(vVCARD.organization_name, "Intellicorp");
		assertContains(vVCARD.organization_unit, "Intelligence");

		assertContains(vVCARD.tel, RDFUtils.uri("tel:415.555.1234"));
		assertContains(vVCARD.uid, "abcdefghijklmnopqrstuvwxyz");
		assertContains(vVCARD.note, "this is a note");
		assertContains(vVCARD.class_, "public");

		assertNotContains(RDF.TYPE, vVCARD.Location);
		assertContains(vVCARD.geo, (Resource) null);
		assertNotContains(null, vVCARD.latitude, "37.77");
		assertNotContains(null, vVCARD.longitude, "-122.41");

		assertContains(RDF.TYPE, vVCARD.Name);
		assertContains(vVCARD.additional_name, "John");
		assertContains(vVCARD.given_name, "Jonathan");
		assertContains(vVCARD.family_name, "Doe-Smith");
		assertContains(vVCARD.honorific_prefix, "Mister");
		assertContains(vVCARD.honorific_suffix, "Medical Doctor");

		assertNotContains(null, vVCARD.post_office_box, "Box 1234");
		assertNotContains(null, vVCARD.extended_address, "Suite 100");
		assertNotContains(null, vVCARD.street_address, "123 Fake Street");
		assertNotContains(null, vVCARD.locality, "San Francisco");
		assertNotContains(null, vVCARD.region, "California");
		assertNotContains(null, vVCARD.postal_code, "12345-6789");
		assertNotContains(null, vVCARD.country_name, "United States of America");
		assertNotContains(null, vVCARD.addressType, "work");
	}


    @Test
	public void testfnOrg() throws RepositoryException {
        assertExtracts("microformats/hcard/30-fn-org.html");
        assertModelNotEmpty();
        assertStatementsSize(RDF.TYPE, vVCARD.VCard, 4);
        RepositoryResult<Statement> repositoryResult = getStatements(null, RDF.TYPE, vVCARD.VCard);
        try {
            while (repositoryResult.hasNext()) {
                Resource card = repositoryResult.next().getSubject();
                Assert.assertNotNull(findObject(card, vVCARD.fn));
                String name = findObjectAsLiteral(card, vVCARD.fn);

                Assert.assertNotNull(findObject(card, vVCARD.org));
                Resource org = findObjectAsResource(card, vVCARD.org);
                Assert.assertNotNull(findObject(org, vVCARD.organization_name));

                if (name.equals("Dan Connolly")) {
                    Assert.assertNotNull(findObject(card, vVCARD.n));
                    Assert.assertFalse(name.equals(org.stringValue()));
                }
            }
        } finally {
            repositoryResult.close();
        }
    }

    @Test
    public void testInclude() throws RepositoryException {
        assertExtracts("microformats/hcard/31-include.html");
        assertModelNotEmpty();
        assertStatementsSize(RDF.TYPE, vVCARD.VCard, 3);
        assertStatementsSize(vVCARD.email, (Value) null, 3);

        RepositoryResult<Statement> statements = getStatements(null, RDF.TYPE, vVCARD.VCard);
        try {
            while (statements.hasNext()) {
                Resource vcard = statements.next().getSubject();

                Assert.assertNotNull(findObject(vcard, vVCARD.fn));
                Assert.assertEquals("Brian Suda", findObjectAsLiteral(vcard, vVCARD.fn));

                Assert.assertNotNull(findObject(vcard, vVCARD.url));
                String url = findObjectAsResource(vcard, vVCARD.url).stringValue();
                Assert.assertEquals("http://suda.co.uk/", url);

                Resource name = findObjectAsResource(vcard, vVCARD.n);
                Assert.assertEquals(
                        "Brian",
                        findObjectAsLiteral(name, vVCARD.given_name)
                );
                Assert.assertEquals(
                        "Suda",
                        findObjectAsLiteral(name, vVCARD.family_name)
                );

                //Included data.
                Assert.assertNotNull(findObject(vcard  , vVCARD.email));
                String mail = findObjectAsLiteral(vcard, vVCARD.email);
                Assert.assertEquals("mailto:correct@example.com", mail);
            }
        } finally {
            statements.close();
        }
    }

    @Test
    public void testHeader() throws RepositoryException {
        assertExtracts("microformats/hcard/32-header.html");
        assertModelNotEmpty();
        // check fn, name, family, nick.
        assertJohn();

        RepositoryResult<Statement> statements = getStatements(null, RDF.TYPE, vVCARD.VCard);
        try {
            Resource example = RDFUtils.uri("http://example.org/");
            while (statements.hasNext()) {
                Resource card = statements.next().getSubject();
                Assert.assertNotNull( findObject(card, vVCARD.fn) );

                String fn = findObjectAsLiteral(card, vVCARD.fn);
                if ("Jane Doe".equals(fn)) {
                    assertNotFound(card, vVCARD.org);
                } else {
                    Assert.assertTrue("John Doe".equals(fn) || "Brian Suda".equals(fn));

                    Assert.assertNotNull( findObject(card, vVCARD.url));
                    Assert.assertEquals(example, findObjectAsResource(card, vVCARD.url));

                    Assert.assertNotNull( findObject(card, vVCARD.org) );
                    Resource org = findObjectAsResource(card, vVCARD.org);
                    assertContains(org, RDF.TYPE, vVCARD.Organization);
                    Assert.assertNotNull(org);
                    Assert.assertNotNull( findObject(card, vVCARD.org) );
                    Assert.assertNotNull( findObject(org , vVCARD.organization_name) );
                    Assert.assertEquals(
                            "example.org",
                            findObjectAsLiteral(org, vVCARD.organization_name)
                    );
                }
            }
            // Just to be sure there are no spurious statements.
            // assertStatementsSize(VCARD.org, null, 2);
            assertStatementsSize(vVCARD.url, example, 2);
        } finally {
            statements.close();
        }
    }

    @Test
	public void testAreaFull() throws RepositoryException {
		assertExtracts("microformats/hcard/33-area.html");
		assertModelNotEmpty();
		assertStatementsSize(RDF.TYPE, vVCARD.VCard, 5);

		RepositoryResult<Statement> statements = getStatements(null, RDF.TYPE, vVCARD.VCard);
		while (statements.hasNext()) {
			Resource vcard = statements.next().getSubject();
			final Value fnValue = findObject(vcard, vVCARD.fn);
            Assert.assertNotNull(fnValue);
			String fn = fnValue.stringValue();
			final Value vcardValue = findObject(vcard, vVCARD.url);
            Assert.assertNotNull(vcardValue);
			String url = vcardValue.stringValue();
			final Value emailValue = findObject(vcard, vVCARD.email);
            Assert.assertNotNull(emailValue);
			String mail = emailValue.stringValue();
			Assert.assertEquals("Joe Public", fn);
			Assert.assertEquals("http://example.com/", url);
			Assert.assertEquals("mailto:joe@example.com", mail);
		}
	}

    @Test
    public void testCategories() throws RepositoryException {
		assertExtracts("microformats/hcard/36-categories.html");
		assertModelNotEmpty();
		assertContains(vVCARD.given_name, "Joe");
		assertContains(vVCARD.given_name, "john");
		assertContains(vVCARD.family_name, "doe");
		assertContains(vVCARD.family_name, "User");
		assertContains(vVCARD.fn, "john doe");
		assertContains(vVCARD.fn, "Joe User");

        assertContains(vVCARD.category, "C1");
        assertContains(vVCARD.category, "C2a");
        assertContains(vVCARD.category, "C4");
        assertContains(vVCARD.category, "User");
		String[] cats = {
                 "C3", "C5", "C6", "C7", "C9", "luser", "D1", "D2", "D3"
        };
		for (String cat : cats)
			assertContains(vVCARD.category, "http://example.com/tag/" + cat);

		assertNotContains(null, vVCARD.category, "D4");
	}

    @Test
    public void testSingleton() throws RepositoryException {
		// this tests probably tests that e just get the first fn and so on
		assertExtracts("microformats/hcard/37-singleton.html");
		assertModelNotEmpty();
		assertStatementsSize(vVCARD.fn, (Value) null, 1);
		assertContains(vVCARD.fn, "john doe 1");

		assertStatementsSize(RDF.TYPE, vVCARD.Name, 1);
		assertStatementsSize(vVCARD.given_name,  (Value) null, 1);
		assertContains(vVCARD.given_name, "john");
		assertStatementsSize(vVCARD.family_name, (Value) null, 1);
		assertContains(vVCARD.family_name, "doe");
		assertStatementsSize(vVCARD.sort_string, (Value) null, 1);
		assertContains(vVCARD.sort_string, "d");

		assertStatementsSize(vVCARD.bday, (Value) null, 1);
		assertContains(vVCARD.bday, "20060707");
		assertStatementsSize(vVCARD.rev, (Value) null, 1);
		assertContains(vVCARD.rev, "20060707");
		assertStatementsSize(vVCARD.class_, (Value) null, 1);
		assertContains(vVCARD.class_, "public");
		assertStatementsSize(vVCARD.tz, (Value) null, 1);
		assertContains(vVCARD.tz, "+0600");

		// Why 0? because the extractor does not look at geo uF!
		assertStatementsSize(RDF.TYPE, vVCARD.Location, 0);
		assertStatementsSize(vVCARD.geo, (Value) null, 2);

		assertNotContains(null, vVCARD.latitude, "123.45");
		assertNotContains(null, vVCARD.longitude, "67.89");

		assertStatementsSize(vVCARD.uid, (Value) null, 1);
		assertContains(vVCARD.uid, "unique-id-1");
	}

    @Test
    public void testUidFull() throws RepositoryException {
        assertExtracts("microformats/hcard/38-uid.html");
        assertModelNotEmpty();
        assertStatementsSize(RDF.TYPE, vVCARD.VCard, 4);
        RepositoryResult<Statement> statements = getStatements(null, RDF.TYPE, vVCARD.VCard);

        try {
            while (statements.hasNext()) {
                Resource vcard = statements.next().getSubject();
                Assert.assertNotNull( findObject(vcard, vVCARD.fn) );
                String fn =  findObjectAsLiteral(vcard, vVCARD.fn);
                Assert.assertEquals("Ryan King", fn);

                Assert.assertNotNull( findObject(vcard,vVCARD.n) );
                Resource n = findObjectAsResource(vcard, vVCARD.n);
                Assert.assertNotNull(n);
                Assert.assertNotNull(findObject(n, vVCARD.given_name) );
                Assert.assertEquals("Ryan",  findObjectAsLiteral( n, vVCARD.given_name) );
                Assert.assertNotNull( findObject(n, vVCARD.family_name) );
                Assert.assertEquals("King", findObjectAsLiteral(n, vVCARD.family_name) );

                Assert.assertNotNull( findObject(vcard, vVCARD.url) );
                Resource url = findObjectAsResource(vcard, vVCARD.url);

                Assert.assertNotNull( findObject(vcard, vVCARD.uid) );
                String uid = findObjectAsLiteral(vcard, vVCARD.uid);

                Assert.assertEquals("http://theryanking.com/contact/", url.stringValue() );
                Assert.assertEquals("http://theryanking.com/contact/", uid);
            }
        } finally {
            statements.close();
        }
    }

    @Test
	public void testRomanianWikipedia() throws RepositoryException {
		assertExtracts("microformats/hcard/40-fn-inside-adr.html");
		assertModelNotEmpty();
		assertStatementsSize(RDF.TYPE, vVCARD.VCard, 1);
		RepositoryResult<Statement> statements = getStatements(null, RDF.TYPE, vVCARD.VCard);

        try {
            while (statements.hasNext()) {
                Resource card = statements.next().getSubject();
                Assert.assertNotNull( findObject(card, vVCARD.fn) );
                String fn = findObjectAsLiteral( card, vVCARD.fn);
                Assert.assertEquals("Berlin", fn);

                Assert.assertNotNull( findObject(card, vVCARD.org) );
                Resource org =  findObjectAsResource(card, vVCARD.org);
                assertContains(org, RDF.TYPE, vVCARD.Organization);
                Assert.assertNotNull(org);
                Assert.assertNotNull( findObject(card, vVCARD.org) );
                Assert.assertNotNull( findObject(org, vVCARD.organization_name) );
                Assert.assertEquals("Berlin", findObjectAsLiteral(org, vVCARD.organization_name));

            }
        } finally {
            statements.close();
        }
    }

    @Test
    public void testNoMicroformats() throws RepositoryException, IOException, ExtractionException {
		extract("html/html-without-uf.html");
		assertModelEmpty();
	}

    @Test
    public void testBasic() throws RepositoryException {
        assertExtracts("microformats/hcard/01-tantek-basic.html");
        assertModelNotEmpty();
        assertContains(RDF.TYPE, vVCARD.VCard);
        // assertContains(RDF.TYPE, vVCARD.Organization);
        assertContains(RDF.TYPE, vVCARD.Name);
        // assertContains(vVCARD.organization_name, "Technorati");
        Resource person = findExactlyOneBlankSubject(
                vVCARD.fn,
                RDFUtils.literal("Tantek Celik")
        );
        Assert.assertNotNull(person);
        Resource org = findExactlyOneBlankSubject(
                vVCARD.organization_name,
                RDFUtils.literal("Technorati")
        );
        Assert.assertNotNull(org);
        assertContains(
                person,
                vVCARD.url,
                RDFUtils.uri("http://tantek.com/")
        );
        assertContains(person, vVCARD.n,   (Resource) null);
        assertContains(person, vVCARD.org, (Resource) null);
    }

    @Test
    public void testMultipleclassNamesOnVCard() throws RepositoryException {
		assertExtracts("microformats/hcard/02-multiple-class-names-on-vcard.html");
		assertModelNotEmpty();
		assertStatementsSize(RDF.TYPE, vVCARD.VCard, 4);
		Resource name;
		RepositoryResult<Statement> statements = getStatements(null, RDF.TYPE, vVCARD.VCard);
		while (statements.hasNext()) {
			name = statements.next().getSubject();
			assertContains(name, vVCARD.fn, "Ryan King");
		}
	}

    @Test
	public void testImpliedNames() throws RepositoryException {
		String[] ns = {
                "Ryan King",
                "King",
                "Ryan",

                "Ryan King",
                "King",
				"Ryan",

                "Ryan King",
                "King",
                "Ryan",

                "Brian Suda",
                "Suda",
				"Brian",

                "King, Ryan",
                "King",
                "Ryan",

                "King, R",
                "King",
                "R",

				"King R",
                "R",
                "King",

                "R King",
                "King",
                "R",

                "King R.",
                "R.",
                "King",

                "Jesse James Garrett",
                "Garrett",
                "Jesse",

                "Thomas Vander Wall",
                "Wall",
                "Thomas"
        };
		List<String> NAMES = Arrays.asList(ns);
		assertExtracts("microformats/hcard/03-implied-n.html");
		assertModelNotEmpty();

        RepositoryResult<Statement> statements = getStatements(null, vVCARD.fn, null);
        Resource vcard;
        int count = 0;
        try {
            while (statements.hasNext()) {
                vcard = statements.next().getSubject();
                assertContains(vcard, RDF.TYPE, vVCARD.VCard);
                Resource name = findObjectAsResource(vcard, vVCARD.n);

                final String objLiteral = findObjectAsLiteral(vcard, vVCARD.fn);
                int idx = NAMES.indexOf(objLiteral);
                Assert.assertTrue( String.format("not in names: '%s'", objLiteral), idx >= 0);
                Assert.assertEquals(
                        NAMES.get(idx + 1),
                        findObjectAsLiteral(name, vVCARD.family_name)
                );
                Assert.assertEquals(
                        NAMES.get(idx + 2),
                        findObjectAsLiteral(name, vVCARD.given_name)
                );
                count++;
            }
        } finally {
            statements.close();
        }
        Assert.assertEquals(10, count);
    }

    @Test
    public void testIgnoreUnknowns() throws RepositoryException {
		assertExtracts("microformats/hcard/04-ignore-unknowns.html");
		assertDefaultVCard();
		assertContains(vVCARD.fn, "Ryan King");
		assertContains(vVCARD.n, (Resource) null);
		assertContains(null, "Ryan");
		assertContains(vVCARD.given_name, "Ryan");
		assertContains(vVCARD.family_name, "King");
	}

    @Test
    public void testMailto1() throws RepositoryException {
		assertExtracts("microformats/hcard/05-mailto-1.html");
		assertDefaultVCard();
		assertContains(vVCARD.fn, "Ryan King");
		assertContains(RDF.TYPE, vVCARD.Name);

		assertContains(
                vVCARD.email,
				RDFUtils.uri("mailto:ryan@technorati.com")
        );

		assertContains(vVCARD.given_name , "Ryan");
		assertContains(vVCARD.family_name, "King");
	}

    @Test
    public void testMailto2() throws RepositoryException {
		assertExtracts("microformats/hcard/06-mailto-2.html");
		assertDefaultVCard();
		assertContains(vVCARD.fn, "Brian Suda");

		assertContains(
                vVCARD.email,
				RDFUtils.uri("mailto:brian@example.com")
        );
		assertContains(vVCARD.given_name, "Brian");
		assertContains(vVCARD.family_name, "Suda");
	}

    @Test
    public void testRelativeUrl() throws RepositoryException {
		assertExtracts("microformats/hcard/07-relative-url.html");
		assertDefaultVCard();
		assertJohn();
		assertContains( vVCARD.url, RDFUtils.uri(baseURI + "home/blah") );
	}

    @Test
    public void testRelativeUrlBase() throws RepositoryException {
		assertExtracts("microformats/hcard/08-relative-url-base.html");
		assertDefaultVCard();
		assertContains(vVCARD.url, RDFUtils.uri(baseURI + "home/blah"));
		assertJohn();
	}

    @Test
    public void testRelativeUrlXmlBase1() throws RepositoryException {
		assertExtracts("microformats/hcard/09-relative-url-xmlbase-1.html");
		assertDefaultVCard();
		assertContains(vVCARD.url, RDFUtils.uri((baseURI + "home/blah")));
		assertJohn();
	}

    @Test
    public void testRelativeUrlXmlBase2() throws RepositoryException {
		assertExtracts("microformats/hcard/10-relative-url-xmlbase-2.html");
		assertDefaultVCard();
		assertContains(vVCARD.url, RDFUtils.uri((baseURI + "home/blah")));
		assertJohn();
	}

    @Test
    public void testMultipleUrls() throws RepositoryException {
		assertExtracts("microformats/hcard/11-multiple-urls.html");
		assertDefaultVCard();
		assertContains(vVCARD.url, RDFUtils.uri(("http://example.com/foo")));
		assertContains(vVCARD.url, RDFUtils.uri(("http://example.com/bar")));

		assertJohn();
	}

    @Test
    public void testImageSrc() throws RepositoryException {
		assertExtracts("microformats/hcard/12-img-src-url.html");
		assertDefaultVCard();
		assertJohn();
	}

    @Test
    public void testPhotoLogo() throws RepositoryException {
		assertExtracts("microformats/hcard/13-photo-logo.html");
		assertDefaultVCard();
		assertContains(vVCARD.photo, RDFUtils.uri(("http://example.org/picture1.png")));
		assertContains(vVCARD.photo, RDFUtils.uri(("http://example.org/picture2.png")));
		assertContains(vVCARD.logo , RDFUtils.uri(("http://example.org/picture1.png")));
		assertContains(vVCARD.logo , RDFUtils.uri(("http://example.org/picture2.png")));
		assertJohn();
	}

    @Test
    public void testImgSrcDataUrl() throws RepositoryException {
		assertExtracts("microformats/hcard/14-img-src-data-url.html");
		assertDefaultVCard();
		Resource data = RDFUtils.uri(
                          "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAMAAAAp4XiDAAAABGdBTUEAAK/"
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
						+ "IAUuuxdZWQvILQABBmSxMjBj5EpcWgACCMoFOYYSpZyHQHgMIMACt2hmoVEikCQAAAABJRU5ErkJggg=="
        );

		assertContains(vVCARD.photo, data);
		assertContains(vVCARD.logo, data);
		assertJohn();
	}

    @Test
    public void testHonorificAdditionalSingle() throws RepositoryException {
		assertExtracts("microformats/hcard/15-honorific-additional-single.html");
		assertDefaultVCard();
		assertContains(vVCARD.fn, "Mr. John Maurice Doe, Ph.D.");

		assertContains(vVCARD.honorific_prefix, "Mr.");
		assertContains(vVCARD.honorific_suffix, "Ph.D.");

		assertContains(vVCARD.given_name, "John");
		assertContains(vVCARD.additional_name, "Maurice");
		assertContains(vVCARD.family_name, "Doe");
	}

    @Test
    public void testHonorificAdditionalMultiple() throws RepositoryException {
		assertExtracts("microformats/hcard/16-honorific-additional-multiple.html");
		assertDefaultVCard();
		assertContains(vVCARD.honorific_prefix, "Mr.");
		assertContains(vVCARD.honorific_prefix, "Dr.");

		assertContains(vVCARD.honorific_suffix, "Ph.D.");
		assertContains(vVCARD.honorific_suffix, "J.D.");

		assertContains(vVCARD.given_name, "John");
		assertContains(vVCARD.additional_name, "Maurice");
		assertContains(vVCARD.additional_name, "Benjamin");
		assertContains(vVCARD.family_name, "Doe");

		assertContains(
                vVCARD.fn,
				"Mr. Dr. John Maurice Benjamin Doe Ph.D., J.D."
        );
	}

    @Test
    public void testEMailNotUri() throws RepositoryException {
		assertExtracts("microformats/hcard/17-email-not-uri.html");
		assertDefaultVCard();
		assertJohn();
		assertContains( vVCARD.email, RDFUtils.uri("mailto:john@example.com") );
	}

	@Test
    public void testObjectDataHttpUri() throws RepositoryException {
		assertExtracts("microformats/hcard/18-object-data-http-uri.html");
		assertDefaultVCard();
		assertJohn();
	}

    @Test
    public void testObjectDataDataUri() throws RepositoryException {
		assertExtracts("microformats/hcard/19-object-data-data-uri.html");
		assertDefaultVCard();
        assertJohn();

		assertContains(vVCARD.photo, (Resource) null);
		assertContains(vVCARD.logo , (Resource) null);
	}

    @Test
    public void testImgAlt() throws RepositoryException {
		assertExtracts("microformats/hcard/20-image-alt.html");
		assertDefaultVCard();
		Resource uri = RDFUtils.uri("http://example.com/foo.png");
		assertContains(vVCARD.photo, uri);
		assertContains(vVCARD.logo, uri);
		assertJohn();
	}

    @Test
    public void testAdr() throws RepositoryException {
		assertExtracts("microformats/hcard/22-adr.html");
		assertDefaultVCard();
		assertJohn();
		assertStatementsSize(RDF.TYPE, vVCARD.Address, 0);
	}

    @Test
    public void testBirthDayDate() throws RepositoryException {
		assertExtracts("microformats/hcard/27-bday-date.html");
		assertModelNotEmpty();
		assertContains(vVCARD.fn         , "john doe");
		assertContains(vVCARD.given_name , "john");
		assertContains(vVCARD.family_name, "doe");
		assertContains(vVCARD.bday       , "2000-01-01");
	}

    @Test
    public void testBirthDayDateTime() throws RepositoryException {
		assertExtracts("microformats/hcard/28-bday-datetime.html");
		assertModelNotEmpty();
		assertContains(vVCARD.fn         , "john doe");
		assertContains(vVCARD.given_name , "john");
		assertContains(vVCARD.family_name, "doe");
		assertContains(vVCARD.bday       , "2000-01-01T00:00:00");
	}

    @Test
    public void testBirthDayDateTimeTimeZone() throws RepositoryException {
		assertExtracts("microformats/hcard/29-bday-datetime-timezone.html");
		assertModelNotEmpty();
		assertContains(vVCARD.fn, "john doe");
		assertContains(vVCARD.given_name, "john");
		assertContains(vVCARD.family_name, "doe");
		assertContains(vVCARD.bday, "2000-01-01T00:00:00-0800");
	}

    @Test
    public void testArea() throws RepositoryException {
        assertExtracts("microformats/hcard/33-area.html");
        assertModelNotEmpty();
        assertStatementsSize(RDF.TYPE, vVCARD.VCard, 5);
        RepositoryResult<Statement> statements = getStatements(null, RDF.TYPE, vVCARD.VCard);
        try {
            while (statements.hasNext()) {
                Resource vcard = statements.next().getSubject();

                Assert.assertNotNull(findObject(vcard, vVCARD.fn));
                Assert.assertEquals("Joe Public", findObjectAsLiteral(vcard, vVCARD.fn));
                Assert.assertNotNull(findObject(vcard, vVCARD.url));
                String url = findObjectAsLiteral(vcard, vVCARD.url);
                Assert.assertNotNull(findObject(vcard, vVCARD.email));
                String mail = findObjectAsLiteral(vcard, vVCARD.email);
                Assert.assertEquals("http://example.com/", url);
                Assert.assertEquals("mailto:joe@example.com", mail);
            }
        } finally {
            statements.close();
        }

        // Check that there are 4 organizations.
        assertStatementsSize(RDF.TYPE, vVCARD.Organization, 4);
        statements = getStatements(null, RDF.TYPE, vVCARD.Organization);
        try {
            while (statements.hasNext()) {
                Resource org = statements.next().getSubject();
                assertContains(null, vVCARD.org, org);
                Assert.assertNotNull( findObject(org, vVCARD.organization_name) );
                Assert.assertEquals("Joe Public", findObjectAsLiteral(org, vVCARD.organization_name) );
            }
        } finally {
            statements.close();
        }
    }

    @Test
    public void testNotes() throws RepositoryException {
        final String[] NOTES = {"Note 1", "Note 3", "Note 4 with a ; and a , to be escaped"};

        assertExtracts("microformats/hcard/34-notes.html");
        assertModelNotEmpty();
        RepositoryResult<Statement> statements = getStatements(null, RDF.TYPE, vVCARD.VCard);
        try {
            while (statements.hasNext()) {
                Resource vcard = statements.next().getSubject();
                String fn   = findObjectAsLiteral(vcard, vVCARD.fn);
                String mail = findObjectAsLiteral(vcard, vVCARD.email);
                Assert.assertEquals("Joe Public", fn);
                Assert.assertEquals("mailto:joe@example.com", mail);
            }
        } finally {
            statements.close();
        }
        for(String note : NOTES) {
            assertContains(vVCARD.note, note);
        }
    }

    @Test
    public void testIncludePattern() throws RepositoryException {
        assertExtracts("microformats/hcard/35-include-pattern.html");
        assertModelNotEmpty();
        assertStatementsSize(RDF.TYPE, vVCARD.VCard, 3);

        RepositoryResult<Statement> statements = getStatements(null, RDF.TYPE, vVCARD.Name);
        try {
            while (statements.hasNext()) {
                Resource name = statements.next().getSubject();
                Assert.assertNotNull(findObject(name, vVCARD.given_name));
                String gn = findObjectAsLiteral(name, vVCARD.given_name);
                Assert.assertEquals("James", gn);
                Assert.assertNotNull(findObject(name, vVCARD.family_name));
                String fn = findObjectAsLiteral(name, vVCARD.family_name);
                Assert.assertEquals("Levine", fn);
            }
        } finally {
            statements.close();
        }

        assertStatementsSize(RDF.TYPE, vVCARD.Organization, 2);
        statements = getStatements(null, RDF.TYPE, vVCARD.Organization);
        try {
            while (statements.hasNext()) {
                Resource org = statements.next().getSubject();
                Assert.assertNotNull(findObject(org, vVCARD.organization_name));
                Assert.assertEquals("SimplyHired", findObjectAsLiteral(org, vVCARD.organization_name));

                RepositoryResult<Statement> statements2 = getStatements(null, vVCARD.org, org);
                try {
                    while (statements2.hasNext()) {
                        Resource vcard = statements2.next().getSubject();
                        Assert.assertNotNull(findObject(vcard, vVCARD.title));
                        Assert.assertEquals("Microformat Brainstormer", findObjectAsLiteral(vcard, vVCARD.title));
                    }
                } finally {
                    statements2.close();
                }
            }
        } finally {
            statements.close();
        }
    }

    @Test
    public void testUid() throws RepositoryException {
		assertExtracts("microformats/hcard/38-uid.html");
		assertModelNotEmpty();
		assertStatementsSize(RDF.TYPE, vVCARD.VCard, 4);
		RepositoryResult<Statement> iter = getStatements(null, RDF.TYPE, vVCARD.VCard);
		while (iter.hasNext()) {
			Resource vcard = iter.next().getSubject();
			Assert.assertNotNull( findObject(vcard, vVCARD.fn) );
			String fn = findObjectAsLiteral(vcard, vVCARD.fn);
			Assert.assertNotNull( findObject(vcard, vVCARD.url) );
			String url =  findObjectAsLiteral(vcard, vVCARD.url);
			Assert.assertNotNull( findObject(vcard, vVCARD.uid) );
			String uid = findObjectAsLiteral(vcard, vVCARD.uid);
			Assert.assertEquals("Ryan King", fn);
			Assert.assertEquals("http://theryanking.com/contact/",url);
			Assert.assertEquals("http://theryanking.com/contact/", uid);

		}
	}

    @Test
    public void testIgnoreChildren() throws RepositoryException {
		assertExtracts("microformats/hcard/41-ignore-children.html");
		assertModelNotEmpty();
		assertStatementsSize(RDF.TYPE, vVCARD.VCard, 1);
		assertContains(vVCARD.fn, "Melanie Kl\u00f6\u00df");
		assertContains(vVCARD.email, RDFUtils.uri("mailto:mkloes@gmail.com"));
		assertContains(vVCARD.adr,(Resource) null);
		assertNotContains(null, vVCARD.postal_code,"53127");
		assertNotContains(null, vVCARD.locality,"Bonn");
		assertNotContains(null, vVCARD.street_address,"Ippendorfer Weg. 24");
		assertNotContains(null, vVCARD.country_name,"Germany");
	}

    /**
     * Tests that the HCardName data is not cumulative and is cleaned up at each
     * extraction.
     *
     * @throws RepositoryException
     */
    @Test
    public void testCumulativeHNames() throws RepositoryException {
        assertExtracts("microformats/hcard/linkedin-michelemostarda.html");
        assertModelNotEmpty();
        assertStatementsSize(vVCARD.given_name, "Michele"  , 7);
        assertStatementsSize(vVCARD.family_name, "Mostarda", 7);
    }

	private void assertDefaultVCard() throws RepositoryException {
		assertModelNotEmpty();
		assertStatementsSize(RDF.TYPE, vVCARD.VCard, 1);
	}

    private void assertJohn() throws RepositoryException {
		assertContains(vVCARD.fn, "John Doe");
		assertContains(vVCARD.given_name, "John");
		assertContains(vVCARD.family_name, "Doe");
	}

}
