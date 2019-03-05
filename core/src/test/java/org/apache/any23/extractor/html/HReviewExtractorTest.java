/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.any23.extractor.html;

import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.vocab.DCTerms;
import org.apache.any23.vocab.Review;
import org.apache.any23.vocab.SINDICE;
import org.apache.any23.vocab.VCard;
import org.junit.Test;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reference Test class for the {@link HReviewExtractor} extractor.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 */
public class HReviewExtractorTest extends AbstractExtractorTestCase {

	private static final DCTerms vDCTERMS = DCTerms.getInstance();
	private static final Review vREVIEW = Review.getInstance();
	private static final SINDICE vSINDICE = SINDICE.getInstance();
	private static final VCard vVCARD = VCard.getInstance();

	private static final Logger logger = LoggerFactory
			.getLogger(HReviewExtractorTest.class);

	protected ExtractorFactory<?> getExtractorFactory() {
		return new HReviewExtractorFactory();
	}

	@Test
	public void testNoMicroformats() throws Exception {
		assertExtract("/html/html-without-uf.html");
		assertModelEmpty();
	}

	@Test
	public void test01Basic() throws Exception {
		assertExtract("/microformats/hreview/01-spec.html");
		assertModelNotEmpty();

		assertStatementsSize(RDF.TYPE, vREVIEW.Review, 1);

		// reviewer, item
		assertStatementsSize(RDF.TYPE, vVCARD.VCard, 0);

		// there is one address in the item vcard
		assertStatementsSize(RDF.TYPE, vVCARD.Address, 0);

		RepositoryResult<Statement> reviews = getStatements(null, RDF.TYPE,
				vREVIEW.Review);

		try {
			while (reviews.hasNext()) {

				Resource review = reviews.next().getSubject();
				logger.debug(review.stringValue());

				assertContains(review, vREVIEW.rating, "5");
				assertContains(review, vREVIEW.title,
						"Crepes on Cole is awesome");
				assertContains(review, vDCTERMS.date, "20050418T2300-0700");

				assertContains(
						vREVIEW.text,
						"Crepes on Cole is one of the best little \n"
								+ "      creperies in San Francisco.\n      "
								+ "Excellent food and service. Plenty of tables in a variety of sizes\n"
								+ "      for parties large and small.  "
								+ "Window seating makes for excellent\n      "
								+ "people watching to/from the N-Judah which stops right outside.\n"
								+ "      I've had many fun social gatherings here, as well as gotten\n"
								+ "      plenty of work done thanks to neighborhood WiFi.");

				assertContains(null, vREVIEW.hasReview, review);

			}
		} finally {
			reviews.close();
		}

		assertNotContains(vVCARD.locality, null);
		assertNotContains(vVCARD.organization_name, null);

	}

	@Test
	public void test02RatedTags() throws Exception {

		assertExtract("/microformats/hreview/02-spec-2.html");
		assertModelNotEmpty();

		assertStatementsSize(RDF.TYPE, vREVIEW.Review, 1);

		// reviewer, item
		assertStatementsSize(vREVIEW.reviewer, (Value) null, 1);
		assertStatementsSize(vREVIEW.hasReview, (Value) null, 1);
		assertStatementsSize(RDF.TYPE, vVCARD.VCard, 0);

		// there is one address in the item vcard
		assertStatementsSize(RDF.TYPE, vVCARD.Address, 0);

		RepositoryResult<Statement> reviews = getStatements(null, RDF.TYPE,
				vREVIEW.Review);

		try {
			while (reviews.hasNext()) {
				Resource review = reviews.next().getSubject();
				assertContains(review, vREVIEW.rating, "18");
				assertContains(review, vREVIEW.title, "Cafe Borrone");
				assertContains(review, vDCTERMS.date, "20050428T2130-0700");

				assertContains(
						vREVIEW.text,
						"This \n    cafe\n    "
								+ "is a welcoming oasis on "
								+ "the Peninsula.\n    "
								+ "It even has a fountain outside which nearly eliminates\n    "
								+ "the sounds of El Camino traffic.  "
								+ "Next door to a superb indy bookstore,\n    "
								+ "Cafe Borrone is an ideal spot to grab a\n    coffee\n    or "
								+ "a meal to accompany a newly purchased book or imported periodical.\n"
								+ "    Soups and\n    sandwich\n    specials rotate daily.  "
								+ "The corn chowder with croutons and big chunks of cheese\n    "
								+ "goes especially well with a freshly toasted mini-baguette.  "
								+ "Evenings are\n    often crowded and may require sharing a table "
								+ "with a perfect stranger.\n    "
								+ "Espresso\n    afficionados will appreciate the\n    Illy coffee.\n    "
								+ "Noise levels can vary from peaceful in the late mornings to nearly overwhelming on\n"
								+ "    jazz band nights.");

				assertContains(null, vREVIEW.hasReview, review);
				assertContains(vREVIEW.type, "business");

			}

		} finally {
			reviews.close();
		}

	}

	@Test
	public void test03NoHcardForItem() throws Exception {

		assertExtract("/microformats/hreview/03-spec-3.html");
		assertModelNotEmpty();

		assertStatementsSize(RDF.TYPE, vREVIEW.Review, 1);
		assertStatementsSize(vREVIEW.reviewer, (Value) null, 1);

		RepositoryResult<Statement> reviews = getStatements(null, RDF.TYPE,
				vREVIEW.Review);

		try {

			while (reviews.hasNext()) {

				Resource review = reviews.next().getSubject();

				assertContains(review, vREVIEW.rating, "5");
				assertNotContains(vREVIEW.title, null);
				assertContains(review, vDCTERMS.date, "200502");

				assertContains(
						vREVIEW.text,
						"\"The people thought they were just being rewarded for "
								+ "treating others\n       as they like to be treated, for "
								+ "obeying stop signs and curing diseases,\n       for mailing "
								+ "letters with the address of the sender... Don't wake me,\n "
								+ "      I plan on sleeping in...\"\n     \n     \"Nothing Better\""
								+ " is a great track on this album, too...");

				RepositoryResult<Statement> reviewSubjects = getStatements(
						null, vREVIEW.hasReview, review);

				try {
					while (reviewSubjects.hasNext()) {
						Resource reviewSubject = reviewSubjects.next()
								.getSubject();
						assertContains(reviewSubject, vVCARD.fn,
								"The Postal Service: Give Up");
						assertContains(
								reviewSubject,
								vVCARD.url,
								RDFUtils.iri("http://www.amazon.com/exec/obidos/ASIN/B000089CJI/"));
						assertContains(
								reviewSubject,
								vVCARD.photo,
								RDFUtils.iri("http://images.amazon.com/images/P/B000089CJI.01._SCTHUMBZZZ_.jpg"));
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
	public void test04NoHcardForItem() throws Exception {

		assertExtract("/microformats/hreview/04-spec-4.html");
		assertModelNotEmpty();

		assertStatementsSize(RDF.TYPE, vREVIEW.Review, 1);
		// reviewer, no item
		assertStatementsSize(vREVIEW.reviewer, (Value) null, 1);

		assertStatementsSize(RDF.TYPE, vVCARD.VCard, 0);

		RepositoryResult<Statement> reviews = getStatements(null, RDF.TYPE,
				vREVIEW.Review);

		try {

			while (reviews.hasNext()) {

				Resource review = reviews.next().getSubject();

				assertContains(review, vREVIEW.rating, "4");
				assertNotContains(vREVIEW.title, null);
				assertContains(review, vDCTERMS.date, "20050418");

				assertContains(vREVIEW.text,
						"This movie has great music and visuals.");

				assertStatementsSize(vREVIEW.hasReview, review, 1);

				RepositoryResult<Statement> reviewSubjects = getStatements(
						null, vREVIEW.hasReview, review);

				try {
					while (reviewSubjects.hasNext()) {
						Resource reviewSubject = reviewSubjects.next()
								.getSubject();
						assertContains(reviewSubject, vVCARD.fn,
								"Ying Xiong (HERO)");
						assertContains(
								reviewSubject,
								vVCARD.url,
								RDFUtils.iri("http://www.imdb.com/title/tt0299977/"));
					}

				} finally {
					reviewSubjects.close();
				}

			}

		} finally {
			reviews.close();
		}

	}

	/**
	 * This test is the same defined in
	 * {@link HReviewExtractorTest#test04NoHcardForItem} but assess the behavior
	 * in presence of a <i>Microformat</i> name with a different letter
	 * capitalization.
	 *
	 * @throws Exception if there is an error asserting the test data.
	 */
	@Test
	public void testCaseSensitiveness() throws Exception {
		assertExtract("/microformats/hreview/05-spec.html");
		assertModelNotEmpty();
		assertStatementsSize(RDF.TYPE, vREVIEW.Review, 1);
		// reviewer, no item
		assertStatementsSize(vREVIEW.reviewer, (Value) null, 1);

		assertStatementsSize(RDF.TYPE, vVCARD.VCard, 0);

		RepositoryResult<Statement> reviews = getStatements(null, RDF.TYPE,
				vREVIEW.Review);

		try {

			while (reviews.hasNext()) {

				Resource review = reviews.next().getSubject();

				assertContains(review, vREVIEW.rating, "4");
				assertNotContains(vREVIEW.title, null);
				assertContains(review, vDCTERMS.date, "20050418");

				assertContains(vREVIEW.text,
						"This movie has great music and visuals.");

				assertStatementsSize(vREVIEW.hasReview, review, 1);

				RepositoryResult<Statement> reviewSubjects = getStatements(
						null, vREVIEW.hasReview, review);

				try {
					while (reviewSubjects.hasNext()) {
						Resource reviewSubject = reviewSubjects.next()
								.getSubject();
						assertContains(reviewSubject, vVCARD.fn,
								"Ying Xiong (HERO)");
						assertContains(
								reviewSubject,
								vVCARD.url,
								RDFUtils.iri("http://www.imdb.com/title/tt0299977/"));
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
