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
import org.deri.any23.vocab.DCTERMS;
import org.deri.any23.vocab.REVIEW;
import org.deri.any23.vocab.VCARD;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

/**
 * Reference Test class for the {@link org.deri.any23.extractor.html.HReviewExtractor} extractor.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 */
public class HReviewExtractorTest extends AbstractExtractorTestCase {

        protected ExtractorFactory<?> getExtractorFactory() {
        return HReviewExtractor.factory;
    }

    @Test
	public void testNoMicroformats() throws RepositoryException {
		assertExtracts("html/html-without-uf.html");
        Assert.assertTrue(conn.isEmpty());
	}

    @Test
	public void test01Basic() throws RepositoryException {
		assertExtracts("microformats/hreview/01-spec.html");
        Assert.assertFalse(conn.isEmpty());

        assertStatementsSize(RDF.TYPE, REVIEW.Review, 1);

        // reviewer, item
		assertStatementsSize(RDF.TYPE, VCARD.VCard, 0);


        // there is one address in the item vcard
		assertStatementsSize(RDF.TYPE, VCARD.Address, 0);

        RepositoryResult<Statement> reviews = conn.getStatements(null, RDF.TYPE, REVIEW.Review, false);

        try {
            while (reviews.hasNext()) {

                Resource review = reviews.next().getSubject();
                System.out.println(review.stringValue());

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
        } finally {
            reviews.close();
        }

		assertNotContains(VCARD.locality, null);
		assertNotContains(VCARD.organization_name, null);

	}

    @Test
	public void test02RatedTags() throws RepositoryException {
		
		assertExtracts("microformats/hreview/02-spec-2.html");
		Assert.assertFalse(conn.isEmpty());

		assertStatementsSize(RDF.TYPE, REVIEW.Review, 1);

        // reviewer, item
		assertStatementsSize(REVIEW.reviewer, (Value)null, 1);
		assertStatementsSize(REVIEW.hasReview, (Value) null, 1);
		assertStatementsSize(RDF.TYPE, VCARD.VCard, 0);

        // there is one address in the item vcard
		assertStatementsSize(RDF.TYPE, VCARD.Address, 0);

        RepositoryResult<Statement> reviews = conn.getStatements(null, RDF.TYPE, REVIEW.Review, false);

        try {
            while (reviews.hasNext()) {
                Resource review = reviews.next().getSubject();
                assertContains(review, REVIEW.rating, "18");
                assertContains(review, REVIEW.title, "Cafe Borrone");
                assertContains(review, DCTERMS.date, "20050428T2130-0700");

                assertContains(
                        REVIEW.text,
                        "This \n    cafe\n    " +
                                "is a welcoming oasis on " +
                                "the Peninsula.\n    " +
                                "It even has a fountain outside which nearly eliminates\n    " +
                                "the sounds of El Camino traffic.  " +
                                "Next door to a superb indy bookstore,\n    " +
                                "Cafe Borrone is an ideal spot to grab a\n    coffee\n    or " +
                                "a meal to accompany a newly purchased book or imported periodical.\n" +
                                "    Soups and\n    sandwich\n    specials rotate daily.  " +
                                "The corn chowder with croutons and big chunks of cheese\n    " +
                                "goes especially well with a freshly toasted mini-baguette.  " +
                                "Evenings are\n    often crowded and may require sharing a table " +
                                "with a perfect stranger.\n    " +
                                "Espresso\n    afficionados will appreciate the\n    Illy coffee.\n    " +
                                "Noise levels can vary from peaceful in the late mornings to nearly overwhelming on\n" +
                                "    jazz band nights."
                );

                assertContains(null, REVIEW.hasReview, review);
                assertContains(REVIEW.type, "business");

            }

        } finally {
            reviews.close();
        }

	}

    @Test
	public void test03NoHcardForItem() throws RepositoryException {

        assertExtracts("microformats/hreview/03-spec-3.html");
        Assert.assertFalse(conn.isEmpty());

        assertStatementsSize(RDF.TYPE, REVIEW.Review, 1);
        assertStatementsSize(REVIEW.reviewer, (Value) null, 1);

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

                RepositoryResult<Statement> reviewSubjects = conn.getStatements(null, REVIEW.hasReview, review, false);

                try {
                    while (reviewSubjects.hasNext()) {
                        Resource reviewSubject = reviewSubjects.next().getSubject();
                        assertContains(reviewSubject, VCARD.fn, "The Postal Service: Give Up");
                        assertContains(reviewSubject, VCARD.url,
                                RDFHelper.uri("http://www.amazon.com/exec/obidos/ASIN/B000089CJI/"));
                        assertContains(reviewSubject, VCARD.photo,
                                RDFHelper.uri("http://images.amazon.com/images/P/B000089CJI.01._SCTHUMBZZZ_.jpg"));
                    }
                } finally {
                    reviewSubjects.close();
                }

            }

        } finally {
            reviews.close();
        }

	}

    @Test
	public void test04NoHcardForItem() throws RepositoryException {
		
		assertExtracts("microformats/hreview/04-spec-4.html");
        Assert.assertFalse(conn.isEmpty());

        assertStatementsSize(RDF.TYPE, REVIEW.Review, 1);
		// reviewer, no item
		assertStatementsSize(REVIEW.reviewer, (Value) null, 1);

		assertStatementsSize(RDF.TYPE, VCARD.VCard, 0);


        RepositoryResult<Statement> reviews = conn.getStatements(null, RDF.TYPE, REVIEW.Review, false);

        try {

            while (reviews.hasNext()) {

                Resource review = reviews.next().getSubject();

                assertContains(review, REVIEW.rating, "4");
                assertNotContains(REVIEW.title, null);
                assertContains(review, DCTERMS.date, "20050418");

                assertContains(
                        REVIEW.text,
                        "This movie has great music and visuals.");
                
                assertStatementsSize(REVIEW.hasReview, review, 1);

                RepositoryResult<Statement> reviewSubjects = conn.getStatements(null,REVIEW.hasReview, review, false);

                try {
                    while(reviewSubjects.hasNext()) {
                        Resource reviewSubject = reviewSubjects.next().getSubject();
                        assertContains(reviewSubject, VCARD.fn, "Ying Xiong (HERO)");
				        assertContains(reviewSubject, VCARD.url, RDFHelper.uri("http://www.imdb.com/title/tt0299977/"));
                    }

                } finally {
                    reviewSubjects.close();
                }


            }

        } finally {
            reviews.close();
        }

	}

}
