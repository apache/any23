package com.google.code.any23.extractors;




import org.deri.any23.extractor.html.AbstractMicroformatTestCase;
import org.deri.any23.extractor.html.HListingExtractor;
import org.deri.any23.extractor.html.HTMLDocument;
import org.deri.any23.extractor.html.HTMLFixture;
import org.deri.any23.vocab.HLISTING;

import com.hp.hpl.jena.rdf.model.Resource;

import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;


public class HListingExtractorTest extends AbstractMicroformatTestCase {

	public void testNoMicroformats() {
		assertNotExtracts("../html-without-uf");
		assertTrue(model.isEmpty());
	}

	public void testListingWithouthContent() {
		assertExtracts("empty");
		assertModelNotEmpty();
	}

	public void testSingleAction() {
		assertExtracts("single-action");
		assertModelNotEmpty();
		assertContains(HLISTING.action, HLISTING.offer);
	}

	public void testMultipleActions() {
		assertExtracts("multiple-actions");
		assertModelNotEmpty();
		assertContains(HLISTING.action, HLISTING.offer);
		assertContains(HLISTING.action, HLISTING.sell);
	}


	public void testMultipleActionsNested() {
		assertExtracts("multiple-actions-nested");
		assertModelNotEmpty();
		assertContains(HLISTING.action, HLISTING.offer);
		assertContains(HLISTING.action, HLISTING.sell);
		assertContains(HLISTING.action, HLISTING.rent);
	}

	public void testActionsOutside() {
		assertExtracts("single-action-outside");
		assertModelNotEmpty();
		assertNotContains(HLISTING.action, HLISTING.offer);
	}

	public void testListerFn() {
		assertExtracts("actions-lister-fn");
		assertModelNotEmpty();
		assertContains(HLISTING.action, HLISTING.offer);
		assertContains(RDF.type, HLISTING.Lister);
		assertContains(HLISTING.listerName, "mike");
	}

	//TODO: improve tel handling, using type together with value
	public void testListerFnTel() {
		assertExtracts("actions-lister-fn-tel");
		assertModelNotEmpty();
		assertContains(HLISTING.action, HLISTING.offer);
		assertContains(HLISTING.listerName, "John Broker");
		assertContains(RDF.type, HLISTING.Lister);
		assertContains(HLISTING.tel, "(110) 555-1212");
	}

	public void testItemFn() {
		assertExtracts("item-fn");
		assertModelNotEmpty();
		assertContains(RDF.type, HLISTING.Item);
		assertContains(HLISTING.itemName, "Parking space");
	}


	public void testItemFnUrl() {
		assertExtracts("item-fn-url");
		assertModelNotEmpty();
		assertContains(RDF.type, HLISTING.Item);
//		dumpModel();
		assertContains(HLISTING.itemUrl, "http://item.com/");
		assertContains(HLISTING.itemName, "Parking space");
	}

	public void testItemPhotoImg() {
		assertExtracts("item-fn-url-photo-img");
		assertModelNotEmpty();
		assertContains(RDF.type, HLISTING.Item);
		assertContains(HLISTING.itemUrl, "http://item.com/");
		assertContains(HLISTING.itemName, "Parking space");
		//model.write(System.err);
		assertContains(HLISTING.itemPhoto, absolute("photo.jpg"));
	}	

	public void testItemPhotoHref() {
		assertExtracts("item-fn-photo-href");
		assertModelNotEmpty();
		assertContains(RDF.type, HLISTING.Item);
		assertContains(HLISTING.itemName, "Parking space");
		assertContains(HLISTING.itemPhoto, absolute("pic.jpg"));
	}	


	public void testKelkoo() {
		assertExtracts("kelkoo");
		assertModelNotEmpty();
		assertContains(RDF.type, HLISTING.Listing);
		assertContains(RDF.type, HLISTING.Item);
		assertContains(HLISTING.action, HLISTING.offer);
		//model.write(System.err);
		assertContains(HLISTING.itemName, "Benq MP622 - DLP Projector - 2700 ANSI lumens - XGA...");
		assertContains(HLISTING.description, (Resource)null);
		assertContains(RDF.type, HLISTING.Lister);
		assertContains(HLISTING.listerUrl, absolute("/m-4621623-pc-world-business.html"));
		assertContains(HLISTING.listerOrg, "PC World Business");
		assertContains(HLISTING.listerLogo, absolute("/data/merchantlogos/4621623/pcworld.gif"));
		assertContains(HLISTING.listerName, "PC World Business");
		assertContains(HLISTING.itemPhoto, "http://img.kelkoo.com/uk/medium/675/496/00117250662929509422269096808645163496675.jpg");
		assertContains(HLISTING.price,"\u00A3480.17");

	}	


	public void testKelkooFull() {
		//TODO improve by counting three different Listings
		assertExtracts("kelkoo-full");
		assertModelNotEmpty();
		//dumpModel();
		assertContains(RDF.type, HLISTING.Listing);
		assertContains(RDF.type, HLISTING.Item);
		assertContains(HLISTING.action, HLISTING.offer);
		assertContains(HLISTING.itemUrl, "http://bob.example.com/");
		assertContains(RDF.type, HLISTING.Lister);
		
		
		assertContains(HLISTING.itemName, "Hanro Touch Feeling Shape Bodysuit Underwear");
		assertContains(HLISTING.itemName, "Spanx Slim Cognito - Shaping Mid-Thigh Bodysuit") ;
		assertContains(HLISTING.itemName, "Spanx Spanx Slim Cognito High Leg Shaping...");
		
		assertContains(HLISTING.itemPhoto, "http://img.kelkoo.com/uk/medium/657/449/00162475823966154731749844283942320449657.jpg");
		assertContains(HLISTING.itemPhoto, "http://img.kelkoo.com/uk/medium/545/091/00154244199719224091151116421737036091545.jpg");
		assertContains(HLISTING.itemPhoto,"http://img.kelkoo.com/uk/medium/018/426/00156227992563192632349212375692442426018.jpg" );
		
		
		assertContains(HLISTING.listerLogo, "http://bob.example.com/data/merchantlogos/6957423/socksfox.gif");
		assertContains(HLISTING.listerLogo, 	"http://bob.example.com/data/merchantlogos/3590723/mytightsnew.gif");
        assertContains(HLISTING.listerLogo,  "http://bob.example.com/data/merchantlogos/2977501/pleaseonlinelogo88x311.gif") ;

		
		
		assertContains(HLISTING.listerName, "Socks Fox");
		assertContains(HLISTING.listerName, "My Tights");//"Spanx Slim Cognito - Shaping Mid-Thigh Bodysuit");
		assertContains(HLISTING.listerName, "Tightsplease" );

		
		assertContains(HLISTING.listerOrg, "Socks Fox");
		assertContains(HLISTING.listerOrg, "My Tights");
		assertContains(HLISTING.listerName, "Tightsplease" );
		
		assertContains(HLISTING.listerUrl, "http://bob.example.com/m-6957423-socks-fox.html");
		assertContains(HLISTING.listerUrl, "http://bob.example.com/m-3590723-my-tights.html");
		assertContains(HLISTING.listerUrl, "http://bob.example.com/m-2977501-tightsplease.html");
		
		assertContains(HLISTING.price,"\u00A380");
		assertContains(HLISTING.price,"\u00A347.95");
		assertContains(HLISTING.price,"\u00A354.99");
	}	

	
	// TODO: test this
	
	public void no() {
	assertContains(HLISTING.region, "item region");
	assertContains(HLISTING.locality, "item locality");
	assertContains(HLISTING.postalCode, "postal code");
	assertContains(HLISTING.postOfficeBox, "post office box");
	}

	public void testListerURL() {
		assertExtracts("actions-lister-url");
		assertModelNotEmpty();
		assertContains(HLISTING.action, HLISTING.offer);
		assertContains(HLISTING.listerName, "John Broker");
		assertContains(RDF.type, HLISTING.Lister);
		assertContains(HLISTING.listerUrl, "http://homepage.com");
	}

	public void testListerEmail() {
		assertExtracts("actions-lister-email");
		assertModelNotEmpty();
		assertContains(HLISTING.action, HLISTING.offer);
		assertContains(HLISTING.listerName, "John Broker");
		assertContains(RDF.type, HLISTING.Lister);
		assertContains(FOAF.mbox, "mailto:info@commerce.net");
	}	

	public void testListerEmailHref() {
		assertExtracts("actions-lister-email-href");
		assertModelNotEmpty();
		assertContains(HLISTING.action, HLISTING.offer);
		assertContains(RDF.type, HLISTING.Lister);
		assertContains(HLISTING.listerName, "John Broker");
//		dumpModel();
		assertContains(FOAF.mbox, "mailto:info@commerce.net");
	}	

	public void testDtListed() {
		assertExtracts("dtlisted-dtexpired");
		assertModelNotEmpty();
		assertNotContains(HLISTING.action, HLISTING.offer);
		assertContains(HLISTING.dtlisted, "2006-02-02");
	}	

	public void testDtExpired() {
		assertExtracts("dtlisted-dtexpired");
		assertModelNotEmpty();
		assertNotContains(HLISTING.action, HLISTING.offer);
		assertContains(HLISTING.dtexpired, "2006-04-01");
	}	

	public void testSummary() {
		assertExtracts("summary");
		assertModelNotEmpty();
		assertContains(HLISTING.summary, "summary stuff");
	}

	public void testDtListedAndExpired() {
		assertExtracts("dtlisted-dtexpired");
		assertModelNotEmpty();
		assertNotContains(HLISTING.action, HLISTING.offer);
		assertContains(HLISTING.dtlisted, "2006-02-02");
		assertContains(HLISTING.dtexpired, "2006-04-01");
	}	
	
	public void testPrice() {
		assertExtracts("price");
		assertModelNotEmpty();
		assertContains(HLISTING.price, "$215/qtr");
	}	
	
	public void testPriceAndDt() {
		assertExtracts("dtlisted-dtexpired");
		assertModelNotEmpty();
		assertContains(HLISTING.price, "$215/qtr");
		assertContains(HLISTING.dtlisted, "2006-02-02");
		assertContains(HLISTING.dtexpired, "2006-04-01");
	}	
	

	public void testPermalink() {
		assertExtracts("summary-bookmark");
		assertModelNotEmpty();
		assertContains(HLISTING.permalink, "http://livre.com/book");;
		assertContains(HLISTING.listerUrl, "http://livre.com/author");;

	}
	public void testComplexDescription() {
		assertExtracts("description-complex");
		assertModelNotEmpty();
		assertContains(HLISTING.description, 
		"BenQ today introduced two new additions to its renowned bus... + Show details");
	}	
	

	public void testDescription() {
		assertExtracts("description");
		assertModelNotEmpty();
		assertContains(HLISTING.description, "bla bla bla");
	}
	
	protected boolean extract(String filename) {
		HTMLDocument doc = new HTMLFixture("hlisting/"+filename+".html", true).getHTMLDocument();
		assertNotNull(doc);
		return new HListingExtractor(baseURI, doc).extractTo(model);
	}
}
