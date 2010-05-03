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

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResultImpl;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.util.RDFHelper;
import org.deri.any23.vocab.DCTERMS;
import org.deri.any23.vocab.FOAF;
import org.deri.any23.vocab.REVIEW;
import org.deri.any23.vocab.VCARD;
import org.deri.any23.writer.RepositoryWriter;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Reference Test class for various mixed extractors.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 *
 * @see org.deri.any23.extractor.html.GeoExtractor
 * @see org.deri.any23.extractor.html.AdrExtractor
 * @see org.deri.any23.extractor.html.HCardExtractor
 * @see org.deri.any23.extractor.html.HReviewExtractor
 */
public class RDFMergerTest extends AbstractExtractorTestCase {

    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return null;
    }

    @Test
	public void testNoMicroformats() throws RepositoryException, ExtractionException, IOException {
		extract("html-without-uf.html");
		Assert.assertTrue(conn.isEmpty());
	}

    @Test
    public void test01XFNFoaf() throws RepositoryException {
		assertExtracts("mixed/01-xfn-foaf.html");
        Assert.assertFalse(conn.isEmpty());
        assertStatementsSize(RDF.TYPE, VCARD.VCard, 1);
        Resource vcard = findExactlyOneBlankSubject(RDF.TYPE, VCARD.VCard);
        RepositoryResult<Statement> statements = conn.getStatements(null, FOAF.topic, vcard, false);

        try {
            while(statements.hasNext()) {
                Statement statement = statements.next();
                Resource person = statement.getSubject();
                Resource blank = findExactlyOneBlankSubject(OWL.SAMEAS, person);
		        assertContains(blank, RDF.TYPE, FOAF.Person);

            }

        } finally {
            statements.close();
        }


		
	}

    @Test
	public void testAbbrTitleEverything() throws ExtractionException, IOException, RepositoryException {
		extractHCardAndRelated("microformats/hcard/23-abbr-title-everything.html");

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
		assertContains(VCARD.tel, RDFHelper.uri("tel:415.555.1234"));
		assertContains(VCARD.uid, "abcdefghijklmnopqrstuvwxyz");
		assertContains(VCARD.class_, "public");
		assertContains(VCARD.note, "this is a note");
		assertContains(VCARD.organization_name, "Intellicorp");
		assertContains(VCARD.organization_unit, "Intelligence");
        assertContains(RDF.TYPE, VCARD.Location);
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

    @Test
    public void testAdr() throws ExtractionException, IOException, RepositoryException {
		extractHRevAndRelated("microformats/hcard/22-adr.html");

		assertStatementsSize(RDF.TYPE, VCARD.Address, 4);

		Map<String,String[]> addresses = new HashMap<String,String[]>(4);
        addresses.put(
                "1233 Main St.",
                new String[]{
                        "United States of America",
                        "Beverly Hills",
                        "90210",
                        "California"});
        addresses.put(
                "1232 Main St.",
                new String[]{
                        "United States of America",
                        "Beverly Hills",
                        "90210",
                        "California"});
        addresses.put(
                "1234 Main St.",
                new String[]{
                        "United States of America",
                        "Beverly Hills",
                        "90210",
                        "California"
                });
        addresses.put(
                "1231 Main St.",
                new String[]{
                        "United States of America",
                        "Beverly Hills",
                        "90210",
                        "California"});
        addresses.put(
                "Suite 100",
                new String[]{
                        "United States of America",
                        "Beverly Hills",
                        "90210",
                        "California"
                });

        RepositoryResult<Statement> statements = conn.getStatements(null, RDF.TYPE, VCARD.Address, false);

        try {
            while (statements.hasNext()) {
                Resource adr = statements.next().getSubject();
                RepositoryResult<Statement> innerStatements = conn.getStatements(adr, VCARD.street_address, null, false);
                try {
                    while (innerStatements.hasNext()) {
                        Value innerValue = innerStatements.next().getObject();
                        assertContains(adr, VCARD.country_name, addresses.get(innerValue.stringValue())[0]);
                        assertContains(adr, VCARD.locality, addresses.get(innerValue.stringValue())[1]);
                        assertContains(adr, VCARD.postal_code, addresses.get(innerValue.stringValue())[2]);
                        assertContains(adr, VCARD.region, addresses.get(innerValue.stringValue())[3]);
                    }

                } finally {
                    innerStatements.close();
                }

            }

        } finally {
            statements.close();
        }

		assertContains(VCARD.post_office_box, "PO Box 1234");
        assertContains(VCARD.addressType, "home");
	}

    @Test
	public void testGeoAbbr() throws ExtractionException, IOException, RepositoryException {
		extractHCardAndRelated("microformats/hcard/25-geo-abbr.html");
		Assert.assertFalse(conn.isEmpty());
		assertContains(VCARD.fn, "Paradise");
		assertContains(RDF.TYPE, VCARD.Organization);
		assertContains(VCARD.organization_name, "Paradise");
		assertContains(RDF.TYPE, VCARD.Location);
		assertContains(VCARD.geo, (Resource) null);
		assertContains(VCARD.latitude, "30.267991");
		assertContains(VCARD.longitude, "-97.739568");
	}

    @Test
	public void testAncestors() throws ExtractionException, IOException, RepositoryException {
		extractHCardAndRelated("microformats/hcard/26-ancestors.html");
        Assert.assertFalse(conn.isEmpty());
		
        assertContains(VCARD.fn, "John Doe");
		assertNotContains(null, VCARD.fn,
		    "Mister Jonathan John Doe-Smith Medical Doctor");
		assertContains(VCARD.nickname, "JJ");
		assertContains(RDF.TYPE, VCARD.Address);
		assertContains(VCARD.tz, "-0700");
		assertContains(VCARD.title, "President");
		assertContains(VCARD.role, "Chief");
		assertContains(VCARD.organization_name, "Intellicorp");
		assertContains(VCARD.organization_unit, "Intelligence");

		assertContains(VCARD.tel, RDFHelper.uri("tel:415.555.1234"));
		assertContains(VCARD.uid, "abcdefghijklmnopqrstuvwxyz");
		assertContains(VCARD.note, "this is a note");
		assertContains(VCARD.class_, "public");

		assertContains(RDF.TYPE, VCARD.Location);
		assertContains(VCARD.geo, (Resource) null);
		assertContains(null, VCARD.latitude, "37.77");
		assertContains(null, VCARD.longitude, "-122.41");

		assertContains(RDF.TYPE, VCARD.Name);
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

    @Test
    public void testSingleton() throws ExtractionException, IOException, RepositoryException {
        extractHCardAndRelated("microformats/hcard/37-singleton.html");
        Assert.assertFalse(conn.isEmpty());
        assertStatementsSize(VCARD.fn, (Value) null, 1);
        assertContains(VCARD.fn, "john doe 1");
        assertStatementsSize(RDF.TYPE, VCARD.Name, 1);
        assertStatementsSize(VCARD.given_name, (Value) null, 1);
        assertContains(VCARD.given_name, "john");
        assertStatementsSize(VCARD.family_name, (Value) null, 1);
        assertContains(VCARD.family_name, "doe");
        assertStatementsSize(VCARD.sort_string, (Value) null, 1);
        assertContains(VCARD.sort_string, "d");
        assertStatementsSize(VCARD.bday, (Value) null, 1);
        assertContains(VCARD.bday, "20060707");
        assertStatementsSize(VCARD.rev, (Value) null, 1);
        assertContains(VCARD.rev, "20060707");
        assertStatementsSize(VCARD.class_, (Value) null, 1);
        assertContains(VCARD.class_, "public");
        assertStatementsSize(VCARD.tz, (Value) null, 1);
        assertContains(VCARD.tz, "+0600");
        // 2 uf, one of them outside the card
        assertStatementsSize(RDF.TYPE, VCARD.Location, 2);
        // one is actually used
        assertStatementsSize(VCARD.geo, (Value) null, 2);
        assertContains(VCARD.latitude, "123.45");
        assertContains(VCARD.longitude, "67.89");
        assertStatementsSize(VCARD.uid, (Value) null, 1);
        assertContains(VCARD.uid, "unique-id-1");
    }

    @Test
	public void test01Basic() throws ExtractionException, IOException, RepositoryException {
		extractHRevAndRelated("microformats/hreview/01-spec.html");
        Assert.assertFalse(conn.isEmpty());

		assertStatementsSize(RDF.TYPE, REVIEW.Review, 1);
		// reviewer, item
		assertStatementsSize(RDF.TYPE, VCARD.VCard, 2);
		// there is one address in the item vcard
		assertStatementsSize(RDF.TYPE, VCARD.Address, 1);

        RepositoryResult<Statement> reviews = conn.getStatements(null, RDF.TYPE, REVIEW.Review, false);

        try {
            while(reviews.hasNext()) {
                Resource review = reviews.next().getSubject();
                assertContains(review, REVIEW.rating, "5");
                assertContains(review, REVIEW.title, "Crepes on Cole is awesome");
			    assertContains(review, DCTERMS.date, "20050418T2300-0700");
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
        }
        finally {
            reviews.close();
        }

		// generic checks that vcards are correct, improve
		assertContains(VCARD.fn, "Crepes on Cole");
		assertContains(VCARD.fn, "Tantek");
		assertContains(VCARD.locality, "San Francisco");
		assertContains(VCARD.organization_name, "Crepes on Cole");

	}
    
    @Test
	public void test02RatedTags() throws ExtractionException, IOException, RepositoryException {
		extractHRevAndRelated("microformats/hreview/02-spec-2.html");

		assertStatementsSize(REVIEW.reviewer, (Value) null, 1);
		assertStatementsSize(REVIEW.hasReview, (Value) null, 1);
		assertModelNotEmpty();
		assertStatementsSize(RDF.TYPE, REVIEW.Review, 1);
		// reviewer, item
		assertStatementsSize(RDF.TYPE, VCARD.VCard, 2);
		// there is one address in the item vcard
		assertStatementsSize(RDF.TYPE, VCARD.Address, 1);

        RepositoryResult<Statement> reviews = conn.getStatements(null, RDF.TYPE, REVIEW.Review, false);

        try {
            while (reviews.hasNext()) {
                Resource review = reviews.next().getSubject();
                assertContains(review, REVIEW.rating, "18");
                assertContains(review, REVIEW.title, "Cafe Borrone");
                assertContains(review, DCTERMS.date, "20050428T2130-0700");
                assertContains(null, REVIEW.hasReview, review);
                assertContains(REVIEW.type, "business");
            }

        } finally {
            reviews.close();
        }

		// generic checks that vcards are correct, improve
		assertContains(VCARD.fn, "Cafe Borrone");
		assertContains(VCARD.fn, "anonymous");
		assertContains(VCARD.organization_name, "Cafe Borrone");
        
	}

    @Test
	public void test03NoHcardForItem() throws ExtractionException, IOException, RepositoryException {
		extractHRevAndRelated("microformats/hreview/03-spec-3.html");

        Assert.assertFalse(conn.isEmpty());
        assertStatementsSize(RDF.TYPE, REVIEW.Review, 1);
		assertStatementsSize(RDF.TYPE, VCARD.VCard, 1);

        RepositoryResult<Statement> reviews = conn.getStatements(null, RDF.TYPE, REVIEW.Review, false);

        try {
            while (reviews.hasNext()) {
                Resource review = reviews.next().getSubject();
                assertContains(review, REVIEW.rating, "5");
                assertNotContains(REVIEW.title, null);
                assertContains(review, DCTERMS.date, "200502");

                assertContains(
                        REVIEW.text,
                        "\"The people thought they were just being rewarded for " +
                                "treating others\n       as they like to be treated, for " +
                                "obeying stop signs and curing diseases,\n       for mailing " +
                                "letters with the address of the sender... Don't wake me,\n " +
                                "      I plan on sleeping in...\"\n     \n     \"Nothing Better\"" +
                                " is a great track on this album, too...");

                RepositoryResult<Statement> whatHasAReview = conn.getStatements(null, REVIEW.hasReview, review, false);

                try {
                    while(whatHasAReview.hasNext()) {
                        Resource subject = whatHasAReview.next().getSubject();
                        assertContains(subject, VCARD.fn, "The Postal Service: Give Up");
				        assertContains(subject, VCARD.url, RDFHelper.uri("http://www.amazon.com/exec/obidos/ASIN/B000089CJI/"));
				        assertContains(subject, VCARD.photo, RDFHelper.uri("http://images.amazon.com/images/P/B000089CJI.01._SCTHUMBZZZ_.jpg"));	
                    }

                } finally {
                    whatHasAReview.close();
                }

            }

        } finally {
            reviews.close();
        }

        assertContains(VCARD.fn, "Adam Rifkin");
		assertContains(VCARD.url, RDFHelper.uri("http://ifindkarma.com/blog/"));
	}

	@Override
	protected void extract(String filename) throws ExtractionException, IOException {

        File file = new File(
                System.getProperty("test.data", "src/test/resources") +
                        "/html/" + filename);

        Document document = new TagSoupParser(new FileInputStream(file), baseURI.stringValue()).getDOM();
        HCardExtractor hCardExtractor = HCardExtractor.factory.createExtractor();
        hCardExtractor.run(document, baseURI, new ExtractionResultImpl(baseURI,
                hCardExtractor, new RepositoryWriter(conn)));
        XFNExtractor xfnExtractor = XFNExtractor.factory.createExtractor();
                xfnExtractor.run(document, baseURI, new ExtractionResultImpl(baseURI,
                hCardExtractor, new RepositoryWriter(conn)));
	}

    private void extractHCardAndRelated(String filename) throws IOException, ExtractionException {
        File file = new File(
                System.getProperty("test.data", "src/test/resources/") + filename);

        Document document = new TagSoupParser(new FileInputStream(file), baseURI.stringValue()).getDOM();
        HCardExtractor hCardExtractor = HCardExtractor.factory.createExtractor();
        hCardExtractor.run(document, baseURI, new ExtractionResultImpl(baseURI,
                hCardExtractor, new RepositoryWriter(conn)));

        GeoExtractor geoExtractor = GeoExtractor.factory.createExtractor();
        geoExtractor.run(document, baseURI, new ExtractionResultImpl(baseURI,
                geoExtractor, new RepositoryWriter(conn)));

        AdrExtractor adrExtractor = AdrExtractor.factory.createExtractor();
        adrExtractor.run(document, baseURI, new ExtractionResultImpl(baseURI,
                adrExtractor, new RepositoryWriter(conn)));

    }

    private void extractHRevAndRelated(String filename) throws ExtractionException, IOException {
        extractHCardAndRelated(filename);
        File file = new File(
                System.getProperty("test.data", "src/test/resources/") + filename);
        Document document = new TagSoupParser(new FileInputStream(file), baseURI.stringValue()).getDOM();
        HReviewExtractor hReviewExtractor = HReviewExtractor.factory.createExtractor();
        hReviewExtractor.run(document, baseURI, new ExtractionResultImpl(baseURI,
                hReviewExtractor, new RepositoryWriter(conn)));
    }

}
