package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.vocab.HRECIPE;
import org.deri.any23.vocab.SINDICE;
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

    private static final SINDICE vSINDICE = SINDICE.getInstance();
    private static final HRECIPE vHRECIPE = HRECIPE.getInstance();

    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return HRecipeExtractor.factory;
    }

    @Test
	public void testNoMicroformats() throws RepositoryException {
		assertExtracts("html/html-without-uf.html");
        assertModelEmpty();
	}

    @Test
    public void testExtraction() throws RepositoryException {
        assertExtracts("microformats/hrecipe/01-spec.html");
        assertModelNotEmpty();
        assertStatementsSize(RDF.TYPE, vHRECIPE.Recipe    , 1);
        assertStatementsSize(RDF.TYPE, vHRECIPE.Ingredient, 3);
        assertStatementsSize(RDF.TYPE, vHRECIPE.Duration  , 2);
        assertStatementsSize(RDF.TYPE, vHRECIPE.Nutrition , 2);
        assertStatementsSize(vHRECIPE.fn,            (String) null, 1);
        assertStatementsSize(vHRECIPE.yield,         (String) null, 1);
        assertStatementsSize(vHRECIPE.instructions,  (String) null, 1);
        assertStatementsSize(vHRECIPE.photo,         (String) null, 1);
        assertStatementsSize(vHRECIPE.summary,       (String) null, 1);
        assertStatementsSize(vHRECIPE.author,        (String) null, 2);
        assertStatementsSize(vHRECIPE.published,     (String) null, 1);
        assertStatementsSize(vHRECIPE.tag,           (String) null, 2);
    }

}
