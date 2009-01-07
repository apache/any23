package com.google.code.any23.extractors;




import com.google.code.any23.extractors.TitleExtractor;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.DC;

public class TitleExtractorTest extends AbstractMicroformatTestCase {
	private final static Resource thePage = ResourceFactory.createResource(baseURI.toString());
	private Literal helloLiteral;
	public void setUp() {
		model = ModelFactory.createDefaultModel();
		helloLiteral = model.createLiteral("Hello World!");
	}
	
	public void testExtractPageTitle() {
		assertExtracts("xfn/simple-me.html");
		assertTrue(model.contains(thePage, DC.title, helloLiteral));
	}

	public void testStripSpacesFromTitle() {
		assertExtracts("xfn/strip-spaces.html");
		assertTrue(model.contains(thePage, DC.title, helloLiteral));
	}

	public void testNoPageTitle() {
		assertNotExtracts("xfn/tagsoup.html");
		assertTrue(model.isEmpty());
	}

	protected boolean extract(String filename) {
		return new TitleExtractor(baseURI, new HTMLFixture(filename, true).getHTMLDocument()).extractTo(model);
	}
	
	public void testMixedCaseTitleTag() {
		assertExtracts("xfn/mixed-case.html");
		assertTrue(model.contains(thePage, DC.title, helloLiteral));
	}
}
