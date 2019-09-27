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

package org.apache.any23;

import org.apache.any23.extractor.ExtractorGroup;
import org.apache.any23.extractor.rdf.NTriplesExtractorFactory;
import org.apache.http.conn.ConnectTimeoutException;
import org.junit.Assert;
import org.apache.any23.configuration.Configuration;
import org.apache.any23.configuration.DefaultConfiguration;
import org.apache.any23.configuration.ModifiableConfiguration;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.microdata.MicrodataExtractor;
import org.apache.any23.filter.IgnoreAccidentalRDFa;
import org.apache.any23.filter.IgnoreTitlesOfEmptyDocuments;
import org.apache.any23.http.DefaultHTTPClient;
import org.apache.any23.http.DefaultHTTPClientConfiguration;
import org.apache.any23.http.HTTPClient;
import org.apache.any23.http.HTTPClientConfiguration;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.source.HTTPDocumentSource;
import org.apache.any23.source.StringDocumentSource;
import org.apache.any23.util.FileUtils;
import org.apache.any23.util.StreamUtils;
import org.apache.any23.util.StringUtils;
import org.apache.any23.vocab.DCTerms;
import org.apache.any23.writer.CompositeTripleHandler;
import org.apache.any23.writer.CountingTripleHandler;
import org.apache.any23.writer.NTriplesWriter;
import org.apache.any23.writer.RDFXMLWriter;
import org.apache.any23.writer.ReportingTripleHandler;
import org.apache.any23.writer.RepositoryWriter;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.apache.commons.io.IOUtils;
import org.junit.AssumptionViolatedException;
import org.junit.Test;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.apache.any23.extractor.ExtractionParameters.ValidationMode;

/**
 * Test case for {@link Any23} facade.
 * 
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 */
@SuppressWarnings("unchecked")
public class Any23Test extends Any23OnlineTestBase {

    private static final DCTerms vDCTERMS = DCTerms.getInstance();

    private static final String PAGE_URL = "http://bob.com";

    private static final Logger logger = LoggerFactory
            .getLogger(Any23Test.class);

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
                "rdf-nt");
    }

    @Test
    public void testHTMLBruteForceDetection() throws Exception {
        assertDetection("<html><body><div class=\"vcard fn\">Joe</div></body></html>");
    }

    /**
     * This tests the behavior of <i>Any23</i> to execute the extraction
     * explicitly specifying the charset encoding of the input.
     * 
     * @throws Exception if there is an error reading the input
     */
    @Test
    public void testExplicitEncoding() throws Exception {
        assertEncodingDetection("UTF-8", "/html/encoding-test.html",
                "Knud M\u00F6ller");
    }

    /**
     * This tests the behavior of <i>Any23</i> to perform the extraction without
     * passing it any charset encoding. The encoding is therefore guessed using
     * {@link org.apache.any23.encoding.TikaEncodingDetector} class.
     * 
     * @throws Exception if there is an error reading the input
     */
    @Test
    public void testImplicitEncoding() throws Exception {
        assertEncodingDetection(null, // The encoding will be auto detected.
                "/html/encoding-test.html", "Knud M\u00F6ller");
    }

    @Test
    public void testRDFXMLDetectionAndExtraction() throws Exception {
        String rdfXML = "<?xml version='1.0'?> "
                + "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' "
                + "xmlns:dc='http://purl.org/dc/elements/1.1/'>"
                + "<rdf:Description rdf:about='http://www.example.com'>"
                + "<dc:title>x</dc:title>" + "</rdf:Description>"
                + "</rdf:RDF>";
        assertDetectionAndExtraction(rdfXML);
    }

    @Test
    public void testNTriplesDetectionAndExtraction() throws Exception {
        String n3 = "<http://www.example.com> <http://purl.org/dc/elements/1.1/title> \"n3 . appo\" .";
        assertDetectionAndExtraction(n3);
    }

    @Test
    public void testNturtleDetectionAndExtraction() throws Exception {
        String nTurtle = "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n"
                + "@prefix dc: <http://purl.org/dc/elements/1.1/> .\n"
                + "@prefix ex: <http://example.org/stuff/1.0/> .\n"
                + "\n"
                + "<http://www.w3.org/TR/rdf-syntax-grammar>\n"
                + "  dc:title \"RDF/XML Syntax Specification (Revised)\" ;\n"
                + "  ex:editor [\n"
                + "    ex:fullname \"Dave Beckett\";\n"
                + "    ex:homePage <http://purl.org/net/dajobe/>\n" + "  ] .";
        assertDetectionAndExtraction(nTurtle);
    }

    /**
     * Tests out the first code snipped used in <i>Developer Manual</i>.
     * 
     * @throws Exception if there is an error reading the input
     */
    @Test
    public void testDemoCodeSnippet1() throws Exception {
        /* 1 */Any23 runner = new Any23();
        /* 2 */final String content = "@prefix foo: <http://example.org/ns#> .   "
                + "@prefix : <http://other.example.org/ns#> ."
                + "foo:bar foo: : .                          "
                + ":bar : foo:bar .                           ";
        // The second argument of StringDocumentSource() must be a valid IRI.
        /* 3 */DocumentSource source = new StringDocumentSource(content,
                "http://host.com/service");
        /* 4 */ByteArrayOutputStream out = new ByteArrayOutputStream();
        /* 5 */TripleHandler handler = new NTriplesWriter(out);
        try {
            /* 6 */runner.extract(source, handler);
        } finally {
            /* 7 */handler.close();
        }
        /* 8 */String nt = out.toString("UTF-8");

        /*
         * <http://example.org/ns#bar> <http://example.org/ns#>
         * <http://other.example.org/ns#> . <http://other.example.org/ns#bar>
         * <http://other.example.org/ns#> <http://example.org/ns#bar> .
         */
        logger.debug("nt: " + nt);
        Assert.assertTrue(nt.length() > 0);
    }

    /**
     * Tests out the second code snipped used in <i>Developer Manual</i>.
     * 
     * @throws Exception if there is an error reading the input
     */
    @Test
    public void testDemoCodeSnippet2() throws Exception {
        assumeOnlineAllowed();

        /* 1 */Any23 runner = new Any23();
        /* 2 */runner.setHTTPUserAgent("apache-any23-test-user-agent");
        /* 3 */HTTPClient httpClient = runner.getHTTPClient();
        /* 4 */DocumentSource source = new HTTPDocumentSource(httpClient,
                "http://dbpedia.org/resource/Trento");
        /* 5 */ByteArrayOutputStream out = new ByteArrayOutputStream();
        /* 6 */TripleHandler handler = new NTriplesWriter(out);
        try {
            /* 7 */runner.extract(source, handler);
        } finally {
            /* 8 */handler.close();
        }
        /* 9 */String n3 = out.toString("UTF-8");

        /*
         * <http://dbpedia.org/resource/Trent>
         * <http://dbpedia.org/ontology/wikiPageDisambiguates>
         * <http://dbpedia.org/resource/Trento> .
         * <http://dbpedia.org/resource/Andrea_Pozzo>
         * <http://dbpedia.org/ontology/birthPlace>
         * <http://dbpedia.org/resource/Trento> .
         * <http://dbpedia.org/resource/Union_for_Trentino>
         * <http://dbpedia.org/ontology/headquarter>
         * <http://dbpedia.org/resource/Trento> . [...]
         */
        logger.debug("n3: " + n3);
        Assert.assertTrue(n3.length() > 0);

        Assert.assertTrue(n3.contains("<http://dbpedia.org/resource/Trento> <http://dbpedia.org/property/mayor> \"Alessandro Andreatta\" ."));
    }

    /**
     * This test checks the extraction behavior when the library is used
     * programatically. This test is related to the issue #45, to verify the
     * different behaviors between Maven and Ant. The behavior was related to a
     * 2nd-level dependency introduced by Maven.
     * 
     * @throws org.apache.any23.extractor.ExtractionException if there is an error running extraction logic
     * @throws IOException if there is an error reading the input
     * @throws URISyntaxException if there is an error defining input URI's
     */
    @Test
    public void testProgrammaticExtraction() throws ExtractionException,
            IOException, URISyntaxException {
        Any23 any23 = new Any23();
        any23.setHTTPUserAgent("Any23-Servlet");
        any23.setHTTPClient(new DefaultHTTPClient() {
            @Override
            protected int getConnectionTimeout() {
                return 5000;
            }

            @Override
            protected int getSoTimeout() {
                return 2000;
            }
        });
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        TripleHandler handler = new NTriplesWriter(byteArrayOutputStream);
        TripleHandler rdfWriter = new IgnoreAccidentalRDFa(handler);
        ReportingTripleHandler reporting = new ReportingTripleHandler(rdfWriter);

        DocumentSource source = getDocumentSourceFromResource(
                "/html/rdfa/ansa_2010-02-26_12645863.html",
                "http://host.com/service");

        Assert.assertTrue(any23.extract(source, reporting)
                .hasMatchingExtractors());
        try {
            handler.close();
        } catch (TripleHandlerException e) {
            Assert.fail(e.getMessage());
        }

        final String bufferContent = byteArrayOutputStream.toString();
        logger.debug(bufferContent);
        Assert.assertSame("Unexpected number of triples.", 18,
                StringUtils.countNL(bufferContent));

    }

    /**
     * This test checks if a URL that is supposed to be GZIPPED is correctly
     * opened and parsed with the {@link Any23} facade.
     * 
     * @throws org.apache.any23.extractor.ExtractionException if there is an error running extraction logic
     * @throws IOException if there is an error reading the input
     * @throws URISyntaxException if there is an error defining input URI's
     */
    @Test
    public void testGZippedContent() throws IOException, URISyntaxException,
            ExtractionException {
        assumeOnlineAllowed();
        final Any23 runner = new Any23();
        runner.setHTTPUserAgent("apache-any23-test-user-agent");
        DocumentSource source = new HTTPDocumentSource(runner.getHTTPClient(),
                "https://dev.w3.org/html5/rdfa/");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TripleHandler handler = new NTriplesWriter(out);
        try {
            runner.extract(source, handler);
        } catch (ConnectTimeoutException e) {
            // This page is down as of 2019.09.14
            logger.error("Connection to " + source.getDocumentIRI() + " timed out; skipping test", e);
            throw new AssumptionViolatedException(e.getMessage());
        }
        String n3 = out.toString("UTF-8");
        logger.debug("N3 " + n3);
        Assert.assertTrue(n3.length() > 0);
    }

    @Test
    public void testExtractionParameters() throws IOException,
            ExtractionException, TripleHandlerException {
        // not quite sure if following triples should be extracted
        // ?doc <http://www.w3.org/1999/xhtml/vocab#icon> <https://any23.googlecode.com/favicon.ico> .
        // ?doc <http://www.w3.org/1999/xhtml/vocab#stylesheet> <https://any23.googlecode.com/design/style.css>  .

        final int EXPECTED_TRIPLES = 12;
        Any23 runner = new Any23();
        DocumentSource source = getDocumentSourceFromResource(
                "/org/apache/any23/validator/missing-og-namespace.html",
                "http://www.test.com");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        CountingTripleHandler cth1 = new CountingTripleHandler();
        NTriplesWriter ctw1 = new NTriplesWriter(baos);
        CompositeTripleHandler compositeTH1 = new CompositeTripleHandler();
        compositeTH1.addChild(cth1);
        compositeTH1.addChild(ctw1);
        try {
            runner.extract(
                    new ExtractionParameters(DefaultConfiguration.singleton(),
                            ValidationMode.NONE), source, compositeTH1);
        } finally {
            compositeTH1.close();
        }
        logger.debug(baos.toString());
        Assert.assertEquals("Unexpected number of triples.", EXPECTED_TRIPLES,
                cth1.getCount());

//        baos.reset();
//        CountingTripleHandler cth2 = new CountingTripleHandler();
//        NTriplesWriter ctw2 = new NTriplesWriter(baos);
//        CompositeTripleHandler compositeTH2 = new CompositeTripleHandler();
//        compositeTH2.addChild(cth2);
//        compositeTH2.addChild(ctw2);
//        runner.extract(
//                new ExtractionParameters(DefaultConfiguration.singleton(),
//                        ValidationMode.ValidateAndFix), source, compositeTH2);
//        logger.debug(baos.toString());
//        Assert.assertEquals("Unexpected number of triples.",
//                EXPECTED_TRIPLES + 5, cth2.getCount());
    }

    @Test
    public void testExtractionParametersWithNestingDisabled()
            throws IOException, ExtractionException, TripleHandlerException {
        final int EXPECTED_TRIPLES = 20;
        Any23 runner = new Any23();
        DocumentSource source = getDocumentSourceFromResource(
                "/microformats/nested-microformats-a1.html",
                "http://www.test.com");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        CountingTripleHandler cth1 = new CountingTripleHandler();
        RDFXMLWriter ctw1 = new RDFXMLWriter(baos);
        CompositeTripleHandler compositeTH1 = new CompositeTripleHandler();
        compositeTH1.addChild(cth1);
        compositeTH1.addChild(ctw1);
        runner.extract(
                new ExtractionParameters(DefaultConfiguration.singleton(),
                        ValidationMode.NONE, true), source, compositeTH1);
        compositeTH1.close();
        logger.debug("Out1: " + baos.toString());
        Assert.assertEquals("Unexpected number of triples.",
                EXPECTED_TRIPLES + 3, cth1.getCount());

        baos.reset();
        CountingTripleHandler cth2 = new CountingTripleHandler();
        NTriplesWriter ctw2 = new NTriplesWriter(baos);
        CompositeTripleHandler compositeTH2 = new CompositeTripleHandler();
        compositeTH2.addChild(cth2);
        compositeTH2.addChild(ctw2);
        runner.extract(
                new ExtractionParameters(DefaultConfiguration.singleton(),
                        ValidationMode.VALIDATE_AND_FIX, false), source,
                compositeTH2);
        compositeTH2.close();
        logger.debug("Out2: " + baos.toString());
        Assert.assertEquals("Unexpected number of triples.", EXPECTED_TRIPLES,
                cth2.getCount());
    }

    @Test
    public void testExceptionPropagation() throws IOException {
        Any23 any23 = new Any23();
        DocumentSource source = getDocumentSourceFromResource(
                "/application/turtle/geolinkeddata.ttl", "http://www.test.com");
        CountingTripleHandler cth1 = new CountingTripleHandler();
        try {
            any23.extract(source, cth1);
        } catch (ExtractionException e) {
            Assert.assertTrue(e.getCause() instanceof RDFParseException);
        }

    }

    /**
     * Test correct management of general <i>XML</i> content.
     * 
     * @throws org.apache.any23.extractor.ExtractionException if there is an error running extraction logic
     * @throws IOException if there is an error reading the input
     */
    @Test
    public void testXMLMimeTypeManagement() throws IOException,
            ExtractionException {
        final String documentIRI = "http://www.test.com/resource.xml";
        final String contentType = "application/xml";
        final String in = StreamUtils.asString(this.getClass()
                .getResourceAsStream("any23-xml-mimetype.xml"));
        final DocumentSource doc = new StringDocumentSource(in, documentIRI,
                contentType);
        final Any23 any23 = new Any23();
        final CountingTripleHandler cth = new CountingTripleHandler(false);
        final ReportingTripleHandler rth = new ReportingTripleHandler(cth);
        final ExtractionReport report = any23.extract(doc, rth);
        Assert.assertFalse(report.hasMatchingExtractors());
        Assert.assertEquals(0, cth.getCount());
    }

    /**
     * Test correct management of general <i>XML</i> content from <i>URL</i>
     * source.
     * 
     * @throws org.apache.any23.extractor.ExtractionException if there is an error running extraction logic
     * @throws IOException if there is an error reading the input
     */
    @Test
    public void testXMLMimeTypeManagementViaURL() throws IOException,
            ExtractionException {
        assumeOnlineAllowed();
        final Any23 any23 = new Any23();
        any23.setHTTPUserAgent("apache-any23-test-user-agent");
        HTTPClient client = any23.getHTTPClient();
        HTTPClientConfiguration configuration = new DefaultHTTPClientConfiguration("application/xml");
        client.init(configuration);
        final CountingTripleHandler cth = new CountingTripleHandler(false);
        final ReportingTripleHandler rth = new ReportingTripleHandler(cth);
        final ExtractionReport report = any23.extract(
                "http://www.legislation.gov.uk/ukpga/2015/17/section/4/data.xml", rth);
        Assert.assertFalse(report.hasMatchingExtractors());
        Assert.assertEquals(0, cth.getCount());
    }

    @Test
    public void testBlankNodesViaURL() throws IOException, ExtractionException {
        assumeOnlineAllowed();
        final Any23 any23 = new Any23();
        any23.setHTTPUserAgent("apache-any23-test-user-agent");
        final CountingTripleHandler cth = new CountingTripleHandler(false);
        final ReportingTripleHandler rth = new ReportingTripleHandler(cth);
        final ExtractionReport report = any23.extract(
                "https://www.w3.org/", rth);
        Assert.assertTrue(report.hasMatchingExtractors());
    }

    @Test
    public void testMicrodataSupport() throws Exception {
        final String htmlWithMicrodata = IOUtils.toString(getClass()
                .getResourceAsStream("/microdata/microdata-basic.html"),
                StandardCharsets.UTF_8);
        assertExtractorActivation(htmlWithMicrodata, MicrodataExtractor.class);
    }

    @Test
    public void testAbstractMethodErrorIssue186_1() throws IOException,
            ExtractionException {
        final Any23 runner = new Any23();
        final String content = FileUtils
                .readResourceContent("/html/rdfa/rdfa-issue186-1.xhtml");
        final DocumentSource source = new StringDocumentSource(content,
                "http://base.com");
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final TripleHandler handler = new NTriplesWriter(out);
        runner.extract(source, handler);
        String n3 = out.toString("UTF-8");
        logger.debug(n3);
    }

    @Test
    public void testAbstractMethodErrorIssue186_2() throws IOException,
            ExtractionException {
        final Any23 runner = new Any23();
        final String content = FileUtils
                .readResourceContent("/html/rdfa/rdfa-issue186-2.xhtml");
        final DocumentSource source = new StringDocumentSource(content,
                "http://richard.cyganiak.de/");
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final TripleHandler handler = new NTriplesWriter(out);
        runner.extract(source, handler);
        final String n3 = out.toString("UTF-8");
        logger.debug(n3);
    }

    @Test
    public void testModifiableConfiguration_issue183() throws Exception {
        final ModifiableConfiguration modifiableConf = DefaultConfiguration
                .copy();
        modifiableConf.setProperty("any23.extraction.metadata.timesize", "off");
        final Any23 any23 = new Any23(modifiableConf);

        final String content = FileUtils
                .readResourceContent("/rdf/rdf-issue183.ttl");
        final DocumentSource source = new StringDocumentSource(content,
                "http://base.com");
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final TripleHandler handler = new NTriplesWriter(out);
        any23.extract(source, handler);
        handler.close();
        final String n3 = out.toString("UTF-8");

        logger.debug(n3);
        Assert.assertFalse(
                "Should not contain triple with http://vocab.sindice.net/date",
                n3.contains("http://vocab.sindice.net/date"));
        Assert.assertFalse(
                "Should not contain triple with http://vocab.sindice.net/size",
                n3.contains("http://vocab.sindice.net/size"));
    }

    @Test
    public void testIssue415InvalidNTriples() throws Exception {
        NTriplesExtractorFactory factory = new NTriplesExtractorFactory();
        Any23 runner = new Any23(new ExtractorGroup(Collections.singleton(factory)));

        ExtractionReport report = runner.extract(
                IOUtils.resourceToString("/rdf/issue415.txt", StandardCharsets.UTF_8),
                "http://humanstxt.org/humans.txt",
                new CompositeTripleHandler());
        Assert.assertEquals("text/plain", report.getDetectedMimeType());
        Assert.assertEquals(0, report.getExtractorIssues(factory.getExtractorName()).size());
        Assert.assertEquals(0, report.getMatchingExtractors().size());
    }

    @Test
    public void testIssue415ValidNTriples() throws Exception {
        NTriplesExtractorFactory factory = new NTriplesExtractorFactory();
        Any23 runner = new Any23(new ExtractorGroup(Collections.singleton(factory)));

        CountingTripleHandler handler = new CountingTripleHandler();
        ExtractionReport report = runner.extract(
                IOUtils.resourceToString("/rdf/issue415-valid.txt", StandardCharsets.UTF_8),
                "http://humanstxt.org/humans.txt",
                handler);
        Assert.assertEquals("application/n-triples", report.getDetectedMimeType());
        Assert.assertEquals(0, report.getExtractorIssues(factory.getExtractorName()).size());
        Assert.assertEquals(1, report.getMatchingExtractors().size());
        Assert.assertEquals(1, handler.getCount());
    }

    /**
     * Performs detection and extraction on the given input string and return
     * the {@link ExtractionReport}.
     * 
     * @param in
     *            input string.
     * @return a populated {@link org.apache.any23.ExtractionReport}
     * @throws Exception if there is an error detecting mime type and running extraction
     */
    private ExtractionReport detectAndExtract(String in) throws Exception {
        Any23 any23 = new Any23();
        Configuration conf = DefaultConfiguration.copy();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ReportingTripleHandler outputHandler = new ReportingTripleHandler(
                new IgnoreAccidentalRDFa(new IgnoreTitlesOfEmptyDocuments(
                        new NTriplesWriter(out))));
        return any23.extract(new ExtractionParameters(conf, ValidationMode.VALIDATE_AND_FIX, null, null), 
            new StringDocumentSource(in, "http://host.com/path"), outputHandler, "UTF-8");
    }

    /**
     * Asserts that a list an {@link Extractor} has been activated for the given
     * input data.
     * 
     * @param in
     *            input data as string.
     * @throws IOException
     * @throws ExtractionException
     */
    private void assertDetectionAndExtraction(String in) throws Exception {
        final ExtractionReport extractionReport = detectAndExtract(in);
        Assert.assertTrue(
                "Detection and extraction failed, no matching extractors.",
                extractionReport.hasMatchingExtractors());
    }

    /**
     * Assert the correct activation of the given list of {@link Extractor}s for
     * the given input string.
     * 
     * @param in
     *            input data as string.
     * @param expectedExtractors
     * @throws IOException
     * @throws ExtractionException
     */
    private void assertExtractorActivation(String in,
            @SuppressWarnings("rawtypes") Class<? extends Extractor>... expectedExtractors) throws Exception {
        final ExtractionReport extractionReport = detectAndExtract(in);
        for (@SuppressWarnings("rawtypes") Class<? extends Extractor> expectedExtractorClass : expectedExtractors) {
            Assert.assertTrue(
                    String.format(
                            "Detection and extraction failed, expected extractor [%s] not found.",
                            expectedExtractorClass),
                    containsClass(extractionReport.getMatchingExtractors(),
                            expectedExtractorClass));
        }
    }

    /**
     * Asserts the correct encoding detection for a specified data.
     * 
     * @param encoding
     *            the expected specified encoding, if <code>null</code> will be
     *            auto detected.
     * @param input
     * @param expectedContent
     * @throws Exception
     */
    private void assertEncodingDetection(String encoding, String input, String expectedContent)
    throws Exception {
        DocumentSource fileDocumentSource = getDocumentSourceFromResource(input);
        Any23 any23;
        RepositoryConnection conn = null;
        RepositoryWriter repositoryWriter = null;
        
        any23 = new Any23();
        Repository store = new SailRepository(new MemoryStore());
        store.init();
        try
        {
            conn = store.getConnection();
            repositoryWriter = new RepositoryWriter(conn);
            Assert.assertTrue( any23.extract(fileDocumentSource, repositoryWriter, encoding).hasMatchingExtractors() );
    
            RepositoryResult<Statement> statements = conn.getStatements(null, vDCTERMS.title, null, false);
            try {
                while (statements.hasNext()) {
                    Statement statement = statements.next();
                    printStatement(statement);
                    Assert.assertTrue(statement.getObject().stringValue().contains(expectedContent));
                }
            } finally {
                statements.close();
            }
        }
        finally {
            if(conn != null) {
                conn.close();
            }
            if(repositoryWriter != null) {
                repositoryWriter.close();
            }
        }
        fileDocumentSource = null;
        any23 = null;
    }

    /**
     * Will try to detect the <i>content</i> trying sequentially with all
     * specified parser.
     * 
     * @param content
     * @param parsers
     * @throws Exception
     */
    private void assertDetection(String content, String... parsers)
            throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Any23 runner = new Any23(parsers.length == 0 ? null : parsers);
        if (parsers.length != 0) {
            runner.setMIMETypeDetector(null); // Use all the provided
                                              // extractors.
        }
        final NTriplesWriter tripleHandler = new NTriplesWriter(out);
        runner.extract(new StringDocumentSource(content, PAGE_URL),
                tripleHandler);
        tripleHandler.close();
        String result = out.toString("us-ascii");
        Assert.assertNotNull(result);
        Assert.assertTrue(result.length() > 10);
    }

    private void printStatement(Statement statement) {
        logger.debug(String.format("%s\t%s\t%s", statement.getSubject(),
                statement.getPredicate(), statement.getObject()));
    }

    private boolean containsClass(List<?> list, Class<?> clazz) {
        for (Object o : list) {
            if (o.getClass().equals(clazz)) {
                return true;
            }
        }
        return false;
    }

}
