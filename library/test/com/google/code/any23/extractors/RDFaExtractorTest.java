package com.google.code.any23.extractors;


import org.sindice.rdfizer.extractors.RDFaExtractor;

import com.hp.hpl.jena.vocabulary.DC;

public class RDFaExtractorTest extends AbstractMicroformatTestCase {

	

	public void testDummy() {
		assertExtracts("dummy");
		assertContains(DC.creator, "Alice");
		assertContains(DC.title, "The trouble with Bob");
	}

	@Override
	protected boolean extract(String name) {
		return new RDFaExtractor(baseURI, new HTMLFixture("rdfa/"+name+".html")
		.getHTMLDocument()).extractTo(model);
	}

	
}
