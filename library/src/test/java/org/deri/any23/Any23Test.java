/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23;

import junit.framework.Assert;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.source.FileDocumentSource;
import org.deri.any23.source.StringDocumentSource;
import org.deri.any23.vocab.DCTERMS;
import org.deri.any23.writer.NTriplesWriter;
import org.deri.any23.writer.RepositoryWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
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
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;

/**
 * Test case for {@link org.deri.any23.Any23} facade.
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 */
public class Any23Test {

    private static final Logger logger = LoggerFactory.getLogger(Any23Test.class);

    private String url = "http://bob.com";

    @Test
    public void testTTLDetection() throws Exception {
        assertReads("<a> <b> <c> .", "rdf-turtle");
    }

    @Test
    public void testN3Detection() throws Exception {
        assertReads("<Bob><brothers>(<Jim><Mark>).", "rdf-turtle");
    }

    @Test
    public void testNTRIPDetection() throws Exception {
        assertReads(
                "<http://example.org/path> <http://foo.com> <http://example.org/Document/foo#> .",
                "rdf-nt"
        );
    }

    @Test
    public void testHTMLDetection() throws Exception {
        assertReads("<html><body><div class=\"vcard fn\">Joe</div></body></html>");
    }

    /**
     * This tests the behavior of <i>Any23</i> to execute the extraction explicitly specyfing the charset
     * encoding of the input.
     *
     * @throws ExtractionException
     * @throws IOException
     * @throws SailException
     * @throws RepositoryException
     */
    @Test
    public void testExplicitEncoding() throws ExtractionException, IOException, SailException, RepositoryException {
        assertEncodingBehavior("UTF-8");    
    }

    /**
     * This tests the behavior of <i>Any23</i> to perform the extraction without passing it any charset encoding.
     * The encoding is therefore guessed using {@link org.deri.any23.encoding.TikaEncodingDetector} class.
     *
     * @throws ExtractionException
     * @throws IOException
     * @throws SailException
     * @throws RepositoryException
     */
    @Test
    public void testImplicitEncoding() throws ExtractionException, IOException, SailException, RepositoryException {
        assertEncodingBehavior(null);
    }

    private void assertEncodingBehavior(String encoding) throws IOException, ExtractionException, RepositoryException, SailException {
        FileDocumentSource fileDocumentSource;
        Any23 any23;
        RepositoryConnection conn;
        RepositoryWriter repositoryWriter;

        fileDocumentSource = new FileDocumentSource(new File("src/test/resources/html/encoding-test.html"));
        any23 = new Any23();
        Sail store = new MemoryStore();
        store.initialize();
        conn = new SailRepository(store).getConnection();
        repositoryWriter = new RepositoryWriter(conn);
        Assert.assertTrue(any23.extract(fileDocumentSource, repositoryWriter, encoding));

        RepositoryResult<Statement> statements = conn.getStatements(null, DCTERMS.title, null, false);
        try {
            while (statements.hasNext()) {
                Statement statement = statements.next();
                printStatement(statement);
                org.junit.Assert.assertTrue(statement.getObject().stringValue().contains("Knud M\u00F6ller"));
            }
        } finally {
            statements.close();
        }

        fileDocumentSource = null;
        any23 = null;
        conn.close();
        repositoryWriter.close();
    }

    private void printStatement(Statement statement) {
        logger.info(String.format("%s\t%s\t%s",
                statement.getSubject(),
                statement.getPredicate(),
                statement.getObject()));
    }

    private void assertReads(String content, String... parsers) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Any23 runner = new Any23(parsers.length == 0 ? null : parsers);
        if (parsers.length != 0) {
            runner.setMIMETypeDetector(null);   // Use all the provided extractors.
        }
        runner.extract(new StringDocumentSource(content, url), new NTriplesWriter(out));
        String result = out.toString("us-ascii");
        Assert.assertNotNull(result);
        Assert.assertTrue(result.length() > 10);
    }
}
