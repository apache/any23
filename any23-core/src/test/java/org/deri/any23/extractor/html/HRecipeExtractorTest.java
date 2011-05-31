package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.vocab.HRECIPE;
import org.deri.any23.vocab.SINDICE;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;

/**
 * Test case for {@link HRecipeExtractor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class HRecipeExtractorTest extends AbstractExtractorTestCase {

    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return HRecipeExtractor.factory;
    }

    @Test
	public void testNoMicroformats() throws RepositoryException {
		assertExtracts("html/html-without-uf.html");
        assertModelNotEmpty();
        assertStatementsSize(null, null, null, 2);
        assertStatementsSize(SINDICE.getProperty(SINDICE.DATE), (Value) null, 1);
        assertStatementsSize(SINDICE.getProperty(SINDICE.SIZE), (Value) null, 1);
	}

    @Test
    public void testExtraction() throws RepositoryException {
        assertExtracts("microformats/hrecipe/01-spec.html");
        Assert.assertFalse(conn.isEmpty());
        assertStatementsSize(RDF.TYPE, HRECIPE.Recipe, 1);
        assertStatementsSize(RDF.TYPE, HRECIPE.Ingredient, 3);
        assertStatementsSize(RDF.TYPE, HRECIPE.Duration, 2);
        assertStatementsSize(RDF.TYPE, HRECIPE.Nutrition, 2);
        assertStatementsSize(HRECIPE.fn,            (String) null, 1);
        assertStatementsSize(HRECIPE.yield,         (String) null, 1);
        assertStatementsSize(HRECIPE.instructions,  (String) null, 1);
        assertStatementsSize(HRECIPE.photo,         (String) null, 1);
        assertStatementsSize(HRECIPE.summary,       (String) null, 1);
        assertStatementsSize(HRECIPE.author,        (String) null, 2);
        assertStatementsSize(HRECIPE.published,     (String) null, 1);
        assertStatementsSize(HRECIPE.tag,           (String) null, 2);
    }

}
