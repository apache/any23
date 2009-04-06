package org.deri.any23.extractor.html;

import java.io.IOException;

import org.deri.any23.TestHelper;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.SingleDocumentExtraction;
import org.deri.any23.vocab.DCTERMS;
import org.deri.any23.writer.RepositoryWriter;
import org.openrdf.model.Literal;
import org.openrdf.repository.RepositoryException;

public class TitleExtractorTest extends AbstractMicroformatTestCase {
	private Literal helloLiteral = TestHelper.literal("Hello World!");
	
	protected void extract(String filename) throws IOException, ExtractionException {
		SingleDocumentExtraction ex = new SingleDocumentExtraction(
				new HTMLFixture(filename).getOpener(), 
				baseURI.toString(), TitleExtractor.factory, new RepositoryWriter(conn));
		ex.setMIMETypeDetector(null);
		ex.run();
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
