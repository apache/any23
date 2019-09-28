/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.any23.extractor;

import org.apache.any23.AbstractAny23TestBase;
import org.apache.any23.configuration.DefaultConfiguration;
import org.apache.any23.configuration.ModifiableConfiguration;
import org.apache.any23.extractor.html.HTMLFixture;
import org.apache.any23.mime.TikaMIMETypeDetector;
import org.apache.any23.mime.purifier.WhiteSpacesPurifier;
import org.apache.any23.vocab.ICAL;
import org.apache.any23.vocab.Review;
import org.apache.any23.vocab.SINDICE;
import org.apache.any23.vocab.VCard;
import org.apache.any23.writer.CompositeTripleHandler;
import org.apache.any23.writer.RDFXMLWriter;
import org.apache.any23.writer.RepositoryWriter;
import org.apache.any23.writer.TripleHandlerException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.SailException;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Test case for {@link SingleDocumentExtraction}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
// TODO #20 - Solve issue that hreview item and vcard item have the same BNode due they have the same XPath DOM.
public class SingleDocumentExtractionTest extends AbstractAny23TestBase {

    private static final SINDICE vSINDICE = SINDICE.getInstance();
    private static final ICAL vICAL    = ICAL.getInstance();
    private static final Review  vREVIEW  = Review.getInstance();
    private static final VCard vVCARD   = VCard.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(SingleDocumentExtractionTest.class);

    private SingleDocumentExtraction singleDocumentExtraction;

    private ExtractorGroup extractorGroup;

    private Sail store;

    private RepositoryConnection conn;

    RepositoryWriter repositoryWriter;

    ByteArrayOutputStream baos;

    RDFXMLWriter rdfxmlWriter;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        extractorGroup = ExtractorRegistryImpl.getInstance().getExtractorGroup();
        store = new MemoryStore();
        store.init();
        conn = new SailRepository(store).getConnection();
    }

    @After
    public void tearDown() throws SailException, RepositoryException, TripleHandlerException {
        rdfxmlWriter.close();
        repositoryWriter.close();
        logger.debug(baos.toString());

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
     * @throws IOException if there is an error loading input data
     * @throws ExtractionException if an exception is raised during extraction
     * @throws RepositoryException if an error is encountered whilst loading content from a storage connection
     */
    @Test
    public void testMicroformatDomains() throws IOException, ExtractionException, RepositoryException {
        singleDocumentExtraction = getInstance("/microformats/microformat-domains.html");
        singleDocumentExtraction.run();
        logStorageContent();
        assertTripleCount(vSINDICE.getProperty(SINDICE.DOMAIN), "nested.test.com", 1);
    }

    /**
     * Tests the nested microformat relationships. This test verifies the first supported approach
     * for microformat nesting. Such approach foreseen to add a microformat HTML node within the
     * property of a container microformat.
     *
     * For further details see
     * {@link SingleDocumentExtraction}
     * consolidateResources(java.util.List, java.util.List, org.apache.any23.writer.TripleHandler)}
     *
     * @throws IOException if there is an error loading input data
     * @throws ExtractionException if an exception is raised during extraction
     * @throws RepositoryException if an error is encountered whilst loading content from a storage connection
     */
    @Test
    public void testNestedMicroformats() throws IOException, ExtractionException, RepositoryException {
        singleDocumentExtraction = getInstance("/microformats/nested-microformats-a1.html");
        singleDocumentExtraction.run();

        logStorageContent();

        assertTripleCount(vSINDICE.getProperty(SINDICE.DOMAIN), "nested.test.com", 2);
        assertTriple(vSINDICE.getProperty(SINDICE.NESTING), (Value) null);
        assertTriple(vSINDICE.getProperty(SINDICE.NESTING_ORIGINAL), vICAL.summary);
        assertTriple(vSINDICE.getProperty(SINDICE.NESTING_STRUCTURED), (Value) null);
    }

    /**
     * This test assess the absence of {@link SINDICE} <i>nesting</i> relationship,
     * since {@link org.apache.any23.extractor.html.HCardExtractor} declared a native nesting
     * with the {@link org.apache.any23.extractor.html.AdrExtractor}.
     *
     * @see org.apache.any23.extractor.html.annotations.Includes
     * @throws IOException if there is an error loading input data
     * @throws ExtractionException if an exception is raised during extraction
     * @throws RepositoryException if an error is encountered whilst loading content from a storage connection
     */
    @Test
    public void testNestedVCardAdr() throws IOException, ExtractionException, RepositoryException {
        singleDocumentExtraction = getInstance("/microformats/nested-microformats-a3.html");
        singleDocumentExtraction.run();

        logStorageContent();

         assertTripleCount(vSINDICE.getProperty(SINDICE.NESTING_ORIGINAL), (Value) null, 0);
         assertTripleCount(vSINDICE.getProperty(SINDICE.NESTING_STRUCTURED), (Value) null, 0);
    }

    /**
     *  Tests the nested microformat relationships. This test verifies the second supported approach
     * for microformat nesting. Such approach foreseen to use the same node attributes to declare both
     * a microformat container property and a nested microformat root class.
     *
     * For further details see
     * {@link SingleDocumentExtraction}
     * consolidateResources(java.util.List, java.util.List, org.apache.any23.writer.TripleHandler)}
     *
     * See also the <a href="http://www.google.com/support/webmasters/bin/answer.py?answer=146862">Nested Entities</a>
     * article that is linked by the official microformats.org doc page.
     *
     * @throws IOException if there is an error loading input data
     * @throws ExtractionException if an exception is raised during extraction
     * @throws RepositoryException if an error is encountered whilst loading content from a storage connection
     */
    @Test
    public void testNestedMicroformatsInduced() throws IOException, ExtractionException, RepositoryException {
        singleDocumentExtraction = getInstance("/microformats/nested-microformats-a2.html");
        singleDocumentExtraction.run();

        logStorageContent();

        assertTripleCount(vSINDICE.getProperty(SINDICE.DOMAIN), "nested.test.com", 2);
        assertTriple(vSINDICE.getProperty(SINDICE.NESTING), (Value) null);
        assertTriple(vSINDICE.getProperty(SINDICE.NESTING_ORIGINAL), vICAL.summary);
        assertTriple(vSINDICE.getProperty(SINDICE.NESTING_STRUCTURED), (Value) null);
    }

    /**
     * Tests the nested microformat relationships. This test verifies the behavior of the nested microformats
     * when the nesting relationship is handled by the microformat extractor itself (like the HReview that is
     * able to detect an inner VCard).
     *
     * @throws IOException if there is an error loading input data
     * @throws ExtractionException if an exception is raised during extraction
     * @throws RepositoryException if an error is encountered whilst loading content from a storage connection
     */
    @Test
    /* NOTE: The triple (bnode http://www.w3.org/2006/vcard/ns#url http://pizza.example.com) and
     *       (bnode http://vocab.sindice.net/nesting_original (structured) *) are printed out twice,
     *       once for every extractor. The RDFWriter doesn't remove the duplicates and some graph renderers
     *       show the triple property as double. Despite this the model contains it just once.
     */
    public void testNestedMicroformatsManaged() throws IOException, ExtractionException, RepositoryException {
        singleDocumentExtraction = getInstance("/microformats/nested-microformats-managed.html");
        singleDocumentExtraction.run();

        logStorageContent();

        assertTripleCount(vSINDICE.getProperty(SINDICE.DOMAIN), "nested.test.com", 3);
        assertTripleCount(vSINDICE.getProperty(SINDICE.NESTING), (Value) null, 1);
        assertTripleCount(vSINDICE.getProperty(SINDICE.NESTING_ORIGINAL), vREVIEW.hasReview, 1);

        assertTripleCount(vVCARD.url, (Value) null, 1);
        Value object = getTripleObject(null, vREVIEW.hasReview);
        assertTripleCount(vSINDICE.getProperty(SINDICE.NESTING_STRUCTURED), object           , 1);
        assertTripleCount(vSINDICE.getProperty(SINDICE.NESTING_ORIGINAL)  , vREVIEW.hasReview, 1);
    }

    private SingleDocumentExtraction getInstance(String file) throws FileNotFoundException, IOException {
        baos = new ByteArrayOutputStream();
        rdfxmlWriter = new RDFXMLWriter(baos);
        repositoryWriter = new RepositoryWriter(conn);

        final CompositeTripleHandler cth = new CompositeTripleHandler();
        cth.addChild(rdfxmlWriter);
        cth.addChild(repositoryWriter);

        final ModifiableConfiguration configuration = DefaultConfiguration.copy();
        configuration.setProperty("any23.extraction.metadata.domain.per.entity", "on");
        SingleDocumentExtraction instance =  new SingleDocumentExtraction(
                configuration,
                new HTMLFixture(copyResourceToTempFile(file)).getOpener("http://nested.test.com"),
                extractorGroup,
                cth
        );
        instance.setMIMETypeDetector( new TikaMIMETypeDetector(new WhiteSpacesPurifier()) );
        return instance;
    }

    /**
     * Logs the storage content.
     * @throws RepositoryException if an error is encountered whilst loading content from a storage connection
     */
    private void logStorageContent() throws RepositoryException {
        RepositoryResult<Statement> result = conn.getStatements(null, null, null, false);
        while (result.hasNext()) {
            Statement statement = result.next();
            logger.debug( statement.toString() );
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
    private void assertTripleCount(IRI predicate, Value value, int occurrences) throws RepositoryException {
        RepositoryResult<Statement> statements = conn.getStatements(
                null, predicate, value, false
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

    /**
     * Asserts that the triple pattern is present within the storage exactly n times.
     *
     * @param predicate
     * @param value
     * @param occurrences
     * @throws RepositoryException
     */
    private void assertTripleCount(IRI predicate, String value, int occurrences) throws RepositoryException {
        assertTripleCount(predicate, SimpleValueFactory.getInstance().createLiteral(value), occurrences);
    }

    /**
     * Asserts that a triple exists exactly once.
     *
     * @param predicate
     * @param value
     * @throws RepositoryException
     */
    private void assertTriple(IRI predicate, Value value) throws RepositoryException {
        assertTripleCount(predicate, value, 1);
    }

    /**
     * Asserts that a triple exists exactly once.
     *
     * @param predicate
     * @param value
     * @throws RepositoryException
     */
    @SuppressWarnings("unused")
    private void assertTriple(IRI predicate, String value) throws RepositoryException {
        assertTriple(predicate, SimpleValueFactory.getInstance().createLiteral(value) );
    }

    /**
     * Retrieves the triple object matching with the given pattern that is expected to be just one.
     * 
     * @param sub the triple subject, <code>null</code> for any.
     * @param prop the triple property, <code>null</code> for any.
     * @return the object of the unique triple matching the given pattern.
     * @throws RepositoryException if an error occurred during the search.
     */
    private Value getTripleObject(Resource sub, IRI prop) throws RepositoryException {
        RepositoryResult<Statement> statements = conn.getStatements(sub, prop, null, false);
        Assert.assertTrue(statements.hasNext());
        Statement statement = statements.next();
        Value value = statement.getObject();
        Assert.assertFalse( "Expected just one result.", statements.hasNext() );
        statements.close();
        return value;
    }

}
