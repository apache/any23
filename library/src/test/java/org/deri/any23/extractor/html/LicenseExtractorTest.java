package org.deri.any23.extractor.html;

import org.deri.any23.Helper;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.vocab.XHTML;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;

public class LicenseExtractorTest extends AbstractMicroformatTestCase {
    private URI ccBy = Helper.uri("http://creativecommons.org/licenses/by/2.0/");
    private URI apache = Helper.uri("http://www.apache.org/licenses/LICENSE-2.0");

    public ExtractorFactory<?> getExtractorFactory() {
        return LicenseExtractor.factory;
    }

    public void testOnlyCc() throws RepositoryException {
        assertExtracts("license/ccBy.html");
        assertContains(baseURI, XHTML.license, ccBy);
        assertNotContains(baseURI, XHTML.license, apache);
    }

    public void testOnlyApache() throws RepositoryException {
        assertExtracts("license/apache.html");
        assertNotContains(baseURI, XHTML.license, ccBy);
        assertContains(baseURI, XHTML.license, apache);
    }

    public void testMultipleLicenses() throws RepositoryException {
        assertExtracts("license/multiple.html");
        assertContains(baseURI, XHTML.license, ccBy);
        assertContains(baseURI, XHTML.license, apache);
    }

    public void testMultipleEmptyHref() throws RepositoryException {
        assertExtracts("license/multiple-empty-href.html");
        assertNotContains(baseURI, XHTML.license, "");
        assertContains(baseURI, XHTML.license, apache);
    }

    public void testEmpty() throws RepositoryException {
        assertExtracts("license/empty.html");
        assertModelEmpty();
    }

    public void testMixedCaseTitleTag() throws RepositoryException {
        assertExtracts("license/multiple-mixed-case.html");
        assertContains(baseURI, XHTML.license, ccBy);
        assertContains(baseURI, XHTML.license, apache);
    }
}

