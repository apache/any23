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
import org.deri.any23.filter.IgnoreAccidentalRDFa;
import org.deri.any23.filter.IgnoreTitlesOfEmptyDocuments;
import org.deri.any23.source.FileDocumentSource;
import org.deri.any23.source.StringDocumentSource;
import org.deri.any23.vocab.DCTERMS;
import org.deri.any23.writer.NTriplesWriter;
import org.deri.any23.writer.ReportingTripleHandler;
import org.deri.any23.writer.RepositoryWriter;
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
        assertDetection("<a> <b> <c> .", "rdf-turtle");
    }

    @Test
    public void testN3Detection1() throws Exception {
        assertDetection("<Bob><brothers>(<Jim><Mark>).", "rdf-turtle");
    }

    @Test
    public void testN3Detection2() throws Exception {
        assertDetection(
                "<http://example.org/path> <http://foo.com> <http://example.org/Document/foo#> .",
                "rdf-nt"
        );
    }

    @Test
    public void testHTMLBruteForceDetection() throws Exception {
        assertDetection("<html><body><div class=\"vcard fn\">Joe</div></body></html>");
    }

    /**
     * This tests the behavior of <i>Any23</i> to execute the extraction explicitly specifying the charset
     * encoding of the input.
     *
     * @throws ExtractionException
     * @throws IOException
     * @throws SailException
     * @throws RepositoryException
     */
    @Test
    public void testExplicitEncoding()
    throws ExtractionException, IOException, SailException, RepositoryException {
        assertEncodingDetection(
                "UTF-8",
                new File("src/test/resources/html/encoding-test.html"),
                "Knud M\u00F6ller"
        );
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
        assertEncodingDetection(
                null, // The encoding will be auto detected.
                new File("src/test/resources/html/encoding-test.html"),
                "Knud M\u00F6ller"
        );
    }

    @Test
    public void testRDFXMLDetectionAndExtraction() throws IOException, ExtractionException {
        String rdfXML =
                "<?xml version='1.0'?> " +
                "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' xmlns:dc='http://purl.org/dc/elements/1.1/'>" +
                "<rdf:Description rdf:about='http://www.example.com'>" +
                "<dc:title>x</dc:title>" +
                "</rdf:Description>" +
                "</rdf:RDF>";
        assertDetectionAndExtraction(rdfXML);
    }

    @Test
    public void testNTriplesDetectionAndExtraction() throws IOException, ExtractionException {
        String n3 = "<http://www.example.com> <http://purl.org/dc/elements/1.1/title> \"n3 . appo\" .";
        assertDetectionAndExtraction(n3);
    }

    @Test
    public void testNturtleDetectionAndExtraction() throws IOException, ExtractionException {
        String nTurtle =
                "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
                "@prefix dc: <http://purl.org/dc/elements/1.1/> .\n" +
                "@prefix ex: <http://example.org/stuff/1.0/> .\n" +
                "\n" +
                "<http://www.w3.org/TR/rdf-syntax-grammar>\n" +
                "  dc:title \"RDF/XML Syntax Specification (Revised)\" ;\n" +
                "  ex:editor [\n" +
                "    ex:fullname \"Dave Beckett\";\n" +
                "    ex:homePage <http://purl.org/net/dajobe/>\n" +
                "  ] .";
        assertDetectionAndExtraction(nTurtle);
    }

    private void assertDetectionAndExtraction(String in) throws IOException, ExtractionException {
        Any23 any23 = new Any23();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ReportingTripleHandler outputHandler = new ReportingTripleHandler(
                new IgnoreAccidentalRDFa(
                        new IgnoreTitlesOfEmptyDocuments(
                                new NTriplesWriter(out)
                        )
                )
        );
        Assert.assertTrue(
                "Detection and extraction failed.",
                any23.extract(in, "http://host.com/path", outputHandler)
        );
    }

    /**
     * Asserts the correct encoding detection for a specified data.
     *
     * @param encoding the expected specified encoding, if <code>null</code> will be auto detected.
     * @throws IOException
     * @throws ExtractionException
     * @throws RepositoryException
     * @throws SailException
     */
    private void assertEncodingDetection(String encoding, File input, String expectedContent)
    throws IOException, ExtractionException, RepositoryException, SailException {
        FileDocumentSource fileDocumentSource;
        Any23 any23;
        RepositoryConnection conn;
        RepositoryWriter repositoryWriter;

        fileDocumentSource = new FileDocumentSource(input);
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
                org.junit.Assert.assertTrue(statement.getObject().stringValue().contains(expectedContent));
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

    /**
     * Will try to detect the <i>content</i> trying sequentially with all
     * specified parser.
     *
     * @param content
     * @param parsers
     * @throws Exception
     */
    private void assertDetection(String content, String... parsers) throws Exception {
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
