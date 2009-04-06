package org.deri.any23.extractor.rdfa;


import java.io.IOException;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.SingleDocumentExtraction;
import org.deri.any23.extractor.html.AbstractMicroformatTestCase;
import org.deri.any23.extractor.html.HTMLFixture;
import org.deri.any23.vocab.DCTERMS;
import org.deri.any23.writer.RepositoryWriter;
import org.openrdf.repository.RepositoryException;

public class RDFaExtractorTest extends AbstractMicroformatTestCase {

	public void testDummy() throws RepositoryException {
		assertExtracts("dummy");
		assertContains(DCTERMS.creator, "Alice");
		assertContains(DCTERMS.title, "The trouble with Bob");
	}

	@Override
	protected void extract(String filename) throws ExtractionException, IOException {
		SingleDocumentExtraction ex = new SingleDocumentExtraction(
				new HTMLFixture("rdfa/"+filename+".html").getOpener(), 
				baseURI.toString(), RDFaExtractor.factory, new RepositoryWriter(conn));
		ex.setMIMETypeDetector(null);
		ex.run();
	}
}
