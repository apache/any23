package org.deri.any23.extractor.html;

import org.junit.Assert;
import org.junit.Test;

import org.deri.any23.Helper;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.vocab.DCTERMS;
import org.openrdf.model.Literal;
import org.openrdf.repository.RepositoryException;

public class TitleExtractorTest extends AbstractMicroformatTestCase {
    private Literal helloLiteral = Helper.literal("Hello World!");

    protected ExtractorFactory<?> getExtractorFactory() {
        return TitleExtractor.factory;
    }

    @Test
    public void testExtractPageTitle() throws RepositoryException {
        assertExtracts("xfn/simple-me.html");
        Assert.assertTrue(conn.hasStatement(baseURI, DCTERMS.title, helloLiteral, false));
    }

    @Test
    public void testStripSpacesFromTitle() throws RepositoryException {
        assertExtracts("xfn/strip-spaces.html");
        Assert.assertTrue(conn.hasStatement(baseURI, DCTERMS.title, helloLiteral, false));
    }

    @Test
    public void testNoPageTitle() throws RepositoryException {
        assertExtracts("xfn/tagsoup.html");
        assertModelEmpty();
    }

    @Test
    public void testMixedCaseTitleTag() throws RepositoryException {
        assertExtracts("xfn/mixed-case.html");
        Assert.assertTrue(conn.hasStatement(baseURI, DCTERMS.title, helloLiteral, false));
    }
    
}
