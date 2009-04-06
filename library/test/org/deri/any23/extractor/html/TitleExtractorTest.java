package org.deri.any23.extractor.html;

import org.deri.any23.TestHelper;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.vocab.DCTERMS;
import org.openrdf.model.Literal;
import org.openrdf.repository.RepositoryException;

public class TitleExtractorTest extends AbstractMicroformatTestCase {
	private Literal helloLiteral = TestHelper.literal("Hello World!");
	
	protected ExtractorFactory<?> getExtractorFactory() {
		return TitleExtractor.factory;
	}
	
	public void testExtractPageTitle() throws RepositoryException {
		assertExtracts("xfn/simple-me.html");
		assertTrue(conn.hasStatement(baseURI, DCTERMS.title, helloLiteral, false));
	}

	public void testStripSpacesFromTitle() throws RepositoryException {
		assertExtracts("xfn/strip-spaces.html");
		assertTrue(conn.hasStatement(baseURI, DCTERMS.title, helloLiteral, false));
	}

	public void testNoPageTitle() throws RepositoryException {
		assertExtracts("xfn/tagsoup.html");
		assertModelEmpty();
	}

	public void testMixedCaseTitleTag() throws RepositoryException {
		assertExtracts("xfn/mixed-case.html");
		assertTrue(conn.hasStatement(baseURI, DCTERMS.title, helloLiteral, false));
	}
}
