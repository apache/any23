package org.deri.any23.extractor.rdfa;


import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.html.AbstractMicroformatTestCase;
import org.deri.any23.vocab.DCTERMS;
import org.openrdf.repository.RepositoryException;

public class RDFaExtractorTest extends AbstractMicroformatTestCase {
	
	@Override
	protected ExtractorFactory<?> getExtractorFactory() {
		return RDFaExtractor.factory;
	}

	public void testSimple() throws RepositoryException {
		assertExtracts("rdfa/dummy.html");
		assertContains(DCTERMS.creator, "Alice");
		assertContains(DCTERMS.title, "The trouble with Bob");
	}
}
