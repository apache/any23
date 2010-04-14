package org.deri.any23.extractor;

import org.deri.any23.extractor.html.HTMLFixture;
import org.deri.any23.mime.TikaMIMETypeDetector;
import org.deri.any23.writer.CompositeTripleHandler;
import org.deri.any23.writer.RDFXMLWriter;
import org.deri.any23.writer.RepositoryWriter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Test case for {@link org.deri.any23.extractor.SingleDocumentExtraction}. 
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class SingleDocumentExtractionTest {

    private static final Logger logger = LoggerFactory.getLogger(SingleDocumentExtractionTest.class);

    private SingleDocumentExtraction singleDocumentExtraction;

    private ExtractorGroup extractorGroup;

    private Sail store;

    private RepositoryConnection conn;

    RepositoryWriter repositoryWriter;

    ByteArrayOutputStream baos;

    RDFXMLWriter rdfxmlWriter;

    @Before
    public void setUp() throws RepositoryException, SailException {
        extractorGroup = ExtractorRegistry.getInstance().getExtractorGroup();
        store = new MemoryStore();
        store.initialize();
        conn = new SailRepository(store).getConnection();
    }

    @After
    public void tearDown() throws SailException, RepositoryException {
        rdfxmlWriter.close();
        repositoryWriter.close();
        logger.info( baos.toString() );

        singleDocumentExtraction = null;
        extractorGroup = null;
        conn.close();
        conn = null;
        store.shutDown();
        store = null;
    }

    /**
     * Tests the existence of the domain triples.
     *
     * @throws IOException
     * @throws ExtractionException
     * @throws RepositoryException
     */
    @Test
    public void testMicroformatDomains() throws IOException, ExtractionException, RepositoryException {
        singleDocumentExtraction = getInstance("microformats/microformat-domains.html");
        singleDocumentExtraction.run();
        logStorageContent();
        assertTripleCount(SingleDocumentExtraction.DOMAIN_PROPERTY, "nested.test.com", 1);
    }

    /**
     * Tests the nested microformat relationships.
     *
     * @throws IOException
     * @throws ExtractionException
     * @throws RepositoryException
     */
    @Test
    public void testNestedMicroformats() throws IOException, ExtractionException, RepositoryException {
        singleDocumentExtraction = getInstance("microformats/nested-microformats.html");
        singleDocumentExtraction.run();
        logStorageContent();
        assertTripleCount(SingleDocumentExtraction.DOMAIN_PROPERTY, "nested.test.com", 2);
        //TODO: test nesting properties.
    }

    private SingleDocumentExtraction getInstance(String file) {
        baos = new ByteArrayOutputStream();
        rdfxmlWriter = new RDFXMLWriter(baos);
        repositoryWriter = new RepositoryWriter(conn);

        final CompositeTripleHandler cth = new CompositeTripleHandler();
        cth.addChild(rdfxmlWriter);
        cth.addChild(repositoryWriter);

        SingleDocumentExtraction instance =  new SingleDocumentExtraction(
                new HTMLFixture(file).getOpener("http://nested.test.com"),
                extractorGroup,
                cth
        );
        instance.setMIMETypeDetector( new TikaMIMETypeDetector() );
        return instance;
    }

    /**
     * Logs the storage content.
     * 
     * @throws RepositoryException
     */
    private void logStorageContent() throws RepositoryException {
        RepositoryResult<Statement> result = conn.getStatements(null, null, null, false);
        while (result.hasNext()) {
            Statement statement = result.next();
            logger.info( statement.toString() );
        }
    }

    /**
     * Asserts that the triple pattern is present within the storage exactly n times.
     * 
     * @param predicate
     * @param value
     * @param occurrences
     * @throws RepositoryException
     */
    private void assertTripleCount(URI predicate, String value, int occurrences) throws RepositoryException {
        RepositoryResult<Statement> statements = conn.getStatements(
                null, predicate, ValueFactoryImpl.getInstance().createLiteral(value), false
        );
        int count = 0;
        while (statements.hasNext()) {
            statements.next();
            count++;
        }
        Assert.assertEquals(
                String.format("Cannot find triple (* %s %s) %d times", predicate, value, occurrences),
                occurrences,
                count
        );
    }


}
