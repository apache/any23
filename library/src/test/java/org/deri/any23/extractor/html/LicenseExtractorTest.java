package org.deri.any23.extractor.html;

import org.deri.any23.Helper;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.html.LicenseExtractor;
import org.deri.any23.vocab.DCTERMS;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;

public class LicenseExtractorTest extends AbstractMicroformatTestCase {
	private URI ccBy = Helper.uri("http://creativecommons.org/licenses/by/2.0/");
	private URI apache = Helper.uri("http://www.apache.org/licenses/LICENSE-2.0");

	public ExtractorFactory<?> getExtractorFactory() {
		return LicenseExtractor.factory;
	}
	
	public void testOnlyCc() throws RepositoryException {
		assertExtracts("ccBy");
		assertContains(baseURI, DCTERMS.license, ccBy);
		assertNotContains(baseURI, DCTERMS.license, apache);

	}
// useless
	public void testOnlyApache() throws RepositoryException {
		assertExtracts("apache");
		assertNotContains(baseURI, DCTERMS.license, ccBy);
		assertContains(baseURI, DCTERMS.license, apache);
	}

	public void testMultipleLicenses() throws RepositoryException {
		assertExtracts("multiple");
		assertContains(baseURI, DCTERMS.license, ccBy);
		assertContains(baseURI, DCTERMS.license, apache);
	}

	public void testMultipleEmptyHref() throws RepositoryException {
		assertExtracts("multiple-empty-href");
		assertNotContains(baseURI, DCTERMS.license, "");
		assertContains(baseURI, DCTERMS.license, apache);
	}

	
	public void testEmpty() throws RepositoryException {
		assertNotExtracts("empty");
		assertModelEmpty();
	}

	public void testMixedCaseTitleTag() throws RepositoryException {
		assertExtracts("multiple-mixed-case");
		assertContains(baseURI, DCTERMS.license, ccBy);
		assertContains(baseURI, DCTERMS.license, apache);
	}
}

