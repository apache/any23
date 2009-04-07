package com.google.code.any23.extractors;


import org.deri.any23.extractor.html.AbstractMicroformatTestCase;
import org.deri.any23.extractor.html.HReviewExtractor;
import org.deri.any23.extractor.html.HTMLDocument;
import org.deri.any23.extractor.html.HTMLFixture;
import org.deri.any23.vocab.REVIEW;
import org.deri.any23.vocab.VCARD;

import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class HReviewExtractorTest extends AbstractMicroformatTestCase {

	public void testNoMicroformats() {
		assertNotExtracts("../html-without-uf");
		assertModelEmpty();
	}
	
	public void test01Basic() {
		assertExtracts("01-spec");
		assertModelNotEmpty();
		assertNotContains(REVIEW.type, null);

		assertStatementsSize(RDF.type, REVIEW.Review, 1);
		// reviewer, item
		assertStatementsSize(RDF.type, VCARD.VCard, 0);
		// there is one address in the item vcard
		assertStatementsSize(RDF.type, VCARD.Address, 0);

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
		assertNotContains(VCARD.fn, null);
		assertNotContains(VCARD.fn, null);
		assertNotContains(VCARD.locality, null);
		assertNotContains(VCARD.organization_name, null);

	}

	public void test02RatedTags() {
		// TODO
		// this has rated tags, which we currently ignore,
		// so we get a kind of undefined behaviour, sorry
		
		assertExtracts("02-spec-2");
		assertModelNotEmpty();
		assertStatementsSize(RDF.type, REVIEW.Review, 1);
		// reviewer, item
		assertStatementsSize(REVIEW.reviewer, null, 1);
		assertStatementsSize(REVIEW.hasReview, null, 1);
		assertStatementsSize(RDF.type, VCARD.VCard, 0);
		// there is one address in the item vcard
		assertStatementsSize(RDF.type, VCARD.Address, 0);

		ResIterator reviews = model.listSubjectsWithProperty(RDF.type,
				REVIEW.Review);
		while (reviews.hasNext()) {
			Resource review = reviews.nextResource();
			assertContains(review, REVIEW.rating,"18");
			assertContains(review, REVIEW.title, "Cafe Borrone");
			assertContains(review, REVIEW.date, "20050428T2130-0700");

			// TODO keep html
			assertContains(
					REVIEW.text,
					"This \n    cafe\n    "+
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
	}

	public void test03NoHcardForItem() {
		// TODO
		// this has rated tags, which we currently ignore,
		// so we get a kind of undefined behaviour, sorry
		
		assertExtracts("03-spec-3");
		assertModelNotEmpty();
		assertStatementsSize(RDF.type, REVIEW.Review, 1);
		assertStatementsSize(REVIEW.reviewer, null, 1);



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
	}
	

	public void test04NoHcardForItem() {
		// TODO
		// this has rated tags, which we currently ignore,
		// so we get a kind of undefined behaviour, sorry
		
		assertExtracts("04-spec-4");
		assertModelNotEmpty();
		assertStatementsSize(RDF.type, REVIEW.Review, 1);
		// reviewer, no item
		assertStatementsSize(REVIEW.reviewer, null, 1);

		assertStatementsSize(RDF.type, VCARD.VCard, 0);


		ResIterator reviews = model.listSubjectsWithProperty(RDF.type,
				REVIEW.Review);
		while (reviews.hasNext()) {
			Resource review = reviews.nextResource();
			assertContains(review, REVIEW.rating,"4");
			assertNotContains(REVIEW.title, null);
			assertContains(review, REVIEW.date, "20050418");
			// TODO keep html
//			dumpModel();
			assertContains(
					REVIEW.text,
					   "This movie has great music and visuals.");
			assertStatementsSize(REVIEW.hasReview, review, 1);
			ResIterator iter = model.listSubjectsWithProperty(REVIEW.hasReview, review);
			while(iter.hasNext()) {
				Resource subj = iter.nextResource();
				assertContains(subj, REVIEW.fn, "Ying Xiong (HERO)");
				assertContains(subj, REVIEW.url, model.createResource("http://www.imdb.com/title/tt0299977/"));
			}
		}
	}

	
	
	protected boolean extract(String name) {
		HTMLDocument doc = new HTMLFixture("hreview/"+name+".html", true).getHTMLDocument();
		assertNotNull(doc);
		return new HReviewExtractor(baseURI, doc).extractTo(model);
	}

}
