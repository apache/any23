package org.deri.any23.extractor.microdata;

import org.apache.log4j.Logger;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.html.AbstractExtractorTestCase;
import org.deri.any23.io.nquads.NQuadsParser;
import org.deri.any23.vocab.SINDICE;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reference test class for {@link MicrodataExtractor}.
 *
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class MicrodataExtractorTest extends AbstractExtractorTestCase {

    private static final Logger logger = Logger.getLogger(MicrodataExtractorTest.class);

    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return MicrodataExtractor.factory;
    }

    /**
     * Reference test for <a href="http://schema.org">Schema.org</a>.
     *
     * @throws RepositoryException
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testSchemaOrgNestedProps()
            throws RepositoryException, RDFHandlerException, IOException, RDFParseException, ExtractionException {
        extractAndVerifyAgainstNQuads(
                "microdata-nested.html",
                "microdata-nested-expected.nquads"
        );
        logger.debug(dumpModelToNQuads());
    }

    /**
     * Reference test as provided by <a href="http://googlewebmastercentral.blogspot.com/2010/03/microdata-support-for-rich-snippets.html">Google Rich Snippet for Microdata.</a>
     *
     * @throws RDFHandlerException
     * @throws RepositoryException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testMicrodataGoogleRichSnippet()
            throws RDFHandlerException, RepositoryException, IOException, RDFParseException {
        extractAndVerifyAgainstNQuads(
                "microdata-richsnippet.html",
                "microdata-richsnippet-expected.nquads"
        );
        logger.debug(dumpHumanReadableTriples());
    }

    /**
     * First reference test  for <a href="http://www.w3.org/TR/microdata/">Microdata Extraction algorithm</a>.
     *
     * @throws RDFHandlerException
     * @throws RepositoryException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testExample5221()
            throws RDFHandlerException, RepositoryException, IOException, RDFParseException {
        extractAndVerifyAgainstNQuads(
                "5.2.1-non-normative-example-1.html",
                "5.2.1-non-normative-example-1-expected.nquads"
        );
        logger.debug(dumpHumanReadableTriples());
    }

    /**
     * Second reference test  for <a href="http://www.w3.org/TR/microdata/">Microdata Extraction algorithm</a>.
     *
     * @throws RDFHandlerException
     * @throws RepositoryException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testExample5222()
            throws RDFHandlerException, RepositoryException, IOException, RDFParseException {
        extractAndVerifyAgainstNQuads(
                "5.2.1-non-normative-example-1.html",
                "5.2.1-non-normative-example-1-expected.nquads"
        );
        logger.debug(dumpHumanReadableTriples());
    }

    /**
     * First reference test for <a href="http://schema.org/">http://schema.org/</a>.
     *
     * @throws RDFHandlerException
     * @throws RepositoryException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testExampleSchemaOrg1()
            throws RDFHandlerException, RepositoryException, IOException, RDFParseException {
        extractAndVerifyAgainstNQuads(
                "schemaorg-example-1.html",
                "schemaorg-example-1-expected.nquads"
        );
        logger.debug(dumpHumanReadableTriples());
    }

    /**
     * Second reference test for <a href="http://schema.org/">http://schema.org/</a>.
     *
     * @throws RDFHandlerException
     * @throws RepositoryException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testExampleSchemaOrg2()
            throws RDFHandlerException, RepositoryException, IOException, RDFParseException {
        extractAndVerifyAgainstNQuads(
                "schemaorg-example-2.html",
                "schemaorg-example-2-expected.nquads"
        );
        logger.debug(dumpHumanReadableTriples());
    }

    private void extractAndVerifyAgainstNQuads(String actual, String expected)
    throws RepositoryException, RDFHandlerException, IOException, RDFParseException {
        assertExtracts("microdata/" + actual);
        assertModelNotEmpty();
        logger.debug( dumpModelToNQuads() );
        List<Statement> expectedStatements = loadResultStatement("microdata/" + expected);
        int actualStmtSize = getStatementsSize(null, null, null);
        Assert.assertEquals( expectedStatements.size(), actualStmtSize);
        for (Statement statement : expectedStatements) {
            if (!statement.getPredicate().equals(SINDICE.getInstance().date)) {
                assertContains(
                        statement.getSubject() instanceof BNode ? null : statement.getSubject(),
                        statement.getPredicate(),
                        statement.getObject() instanceof BNode ? null : statement.getObject()
                );
            }
        }
    }

    private List<Statement> loadResultStatement(String resultFilePath)
            throws RDFHandlerException, IOException, RDFParseException {
        NQuadsParser nQuadsParser = new NQuadsParser();
        TestRDFHandler rdfHandler = new TestRDFHandler();
        nQuadsParser.setRDFHandler(rdfHandler);
        File file = new File(
                System.getProperty("test.data", "src/test/resources/") + resultFilePath);
        nQuadsParser.parse(
                new FileReader(file),
                baseURI.toString()
        );
        return rdfHandler.getStatements();
    }

    public static class TestRDFHandler implements RDFHandler {

        private final List<Statement> statements = new ArrayList<Statement>();

        protected List<Statement> getStatements() {
            return statements;
        }

        public void startRDF() throws RDFHandlerException {
        }

        public void endRDF() throws RDFHandlerException {
        }

        public void handleNamespace(String s, String s1) throws RDFHandlerException {
            throw new UnsupportedOperationException();
        }

        public void handleStatement(Statement statement) throws RDFHandlerException {
            statements.add(statement);
        }

        public void handleComment(String s) throws RDFHandlerException {
            throw new UnsupportedOperationException();
        }
    }

}
