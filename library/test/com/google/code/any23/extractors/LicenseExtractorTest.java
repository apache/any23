package com.google.code.any23.extractors;



import com.google.code.any23.extractors.LicenseExtractor;
import com.google.code.any23.vocab.MISSING;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;


public class LicenseExtractorTest extends AbstractMicroformatTestCase {
	private final static Resource thePage = ResourceFactory.createResource(baseURI.toString());
	private Literal ccBy;
	private Literal apache;
	public void setUp() {
		model = ModelFactory.createDefaultModel();
		ccBy = model
				.createLiteral("http://creativecommons.org/licenses/by/2.0/");
		apache = model
		.createLiteral("http://www.apache.org/licenses/LICENSE-2.0");

	}

	public void testOnlyCc() {
		assertExtracts("ccBy");
		assertContains(thePage, MISSING.DCTerms.license, ccBy);
		assertNotContains(thePage, MISSING.DCTerms.license, apache);

	}
// useless
	public void testOnlyApache() {
		assertExtracts("apache");
		assertNotContains(thePage, MISSING.DCTerms.license, ccBy);
		assertContains(thePage, MISSING.DCTerms.license, apache);
	}


	public void testMultipleLicenses() {
		assertExtracts("multiple");
		assertContains(thePage, MISSING.DCTerms.license, ccBy);
		assertContains(thePage, MISSING.DCTerms.license, apache);
	}

	public void testMultipleEmptyHref() {
		assertExtracts("multiple-empty-href");
		assertNotContains(thePage, MISSING.DCTerms.license, "");
		assertContains(thePage, MISSING.DCTerms.license, apache);
	}

	
	public void testEmpty() {
		assertNotExtracts("empty");
		assertModelEmpty();
	}

	protected boolean extract(String filename) {
		return new LicenseExtractor(baseURI, new HTMLFixture("license/"+filename+".html", true)
				.getHTMLDocument()).extractTo(model);
	}

	public void testMixedCaseTitleTag() {
		assertExtracts("multiple-mixed-case");
		assertContains(thePage, MISSING.DCTerms.license, ccBy);
		assertContains(thePage, MISSING.DCTerms.license, apache);
	}

}

