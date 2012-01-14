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

package org.apache.any23.mime;

import junit.framework.Assert;
import org.apache.any23.mime.purifier.WhiteSpacesPurifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Test case for {@link TikaMIMETypeDetector} class.
 *
 * @author juergen
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class TikaMIMETypeDetectorTest {

    private static final String PLAIN  = "text/plain";
    private static final String HTML   = "text/html";
    private static final String XML    = "application/xml";
    private static final String TRIX   = "application/trix";
    private final static String XHTML  = "application/xhtml+xml";
    private final static String RDFXML = "application/rdf+xml";
    private final static String TURTLE = "application/x-turtle";
    private final static String N3     = "text/rdf+n3";
    private final static String NQuads = "text/rdf+nq";
    private final static String CSV    = "text/csv";

    private TikaMIMETypeDetector detector;

    @Before
    public void setUp() throws Exception {
        detector = new TikaMIMETypeDetector(new WhiteSpacesPurifier());
    }

    @After
    public void tearDown() throws Exception {
        detector = null;
    }

    @Test
    public void testN3Detection() throws IOException {
        assertN3Detection("<http://example.org/path> <http://foo.com> <http://example.org/Document/foo#> .");
        assertN3Detection("_:bnode1 <http://foo.com> _:bnode2 .");
        assertN3Detection("<http://www.example.com> <http://purl.org/dc/elements/1.1/title> \"x\" .");
        assertN3Detection("<http://www.example.com> <http://purl.org/dc/elements/1.1/title> \"x\"@it .");
        assertN3Detection("<http://www.example.com> <http://purl.org/dc/elements/1.1/title> \"x\"^^<http://xxx.net> .");
        assertN3Detection("<http://www.example.com> <http://purl.org/dc/elements/1.1/title> \"x\"^^xsd:integer .");

        // Wrong N3 line '.'
        assertN3DetectionFail("" +
                "<http://wrong.example.org/path> <http://wrong.foo.com> . <http://wrong.org/Document/foo#>"
        );
        // NQuads is not mislead with N3.
        assertN3DetectionFail(
            "<http://example.org/path> <http://foo.com> <http://dom.org/Document/foo#> <http://path/to/graph> ."
        );
    }

    @Test
    public void testNQuadsDetection() throws IOException {
        assertNQuadsDetection(
                "<http://www.ex.eu> <http://foo.com> <http://example.org/Document/foo#> <http://path.to.graph> ."
        );
        assertNQuadsDetection(
                "_:bnode1 <http://foo.com> _:bnode2 <http://path.to.graph> ."
        );
        assertNQuadsDetection(
                "<http://www.ex.eu> <http://purl.org/dc/elements/1.1/title> \"x\" <http://path.to.graph> ."
        );
        assertNQuadsDetection(
                "<http://www.ex.eu> <http://purl.org/dc/elements/1.1/title> \"x\"@it <http://path.to.graph> ."
        );
        assertNQuadsDetection(
                "<http://www.ex.eu> <http://dd.cc.org/1.1/p> \"xxx\"^^<http://www.sp.net/a#tt> <http://path.to.graph> ."
        );
        assertNQuadsDetection(
                "<http://www.ex.eu> <http://purlo.org/1.1/title> \"yyy\"^^xsd:datetime <http://path.to.graph> ."
        );

        // Wrong NQuads line.
        assertNQuadsDetectionFail(
                "<http://www.wrong.com> <http://wrong.com/1.1/tt> \"x\"^^<http://xxx.net/int> . <http://path.to.graph>"
        );
        // N3 is not mislead with NQuads.
        assertNQuadsDetectionFail(
                "<http://example.org/path> <http://foo.com> <http://example.org/Document/foo#> ."
        );
    }

    /* BEGIN: by content. */

    @Test
    public void testDetectRSS1ByContent() throws Exception {
        detectMIMEtypeByContent("application/rdf+xml", "src/test/resources/application/rss1");
    }

    @Test
    public void testDetectRSS2ByContent() throws Exception {
        detectMIMEtypeByContent("application/rss+xml", "src/test/resources/application/rss2");
    }

    @Test
    public void testDetectRDFN3ByContent() throws Exception {
        detectMIMEtypeByContent("text/n3", "src/test/resources/application/rdfn3");
    }

    @Test
    public void testDetectRDFNQuadsByContent() throws Exception {
        detectMIMEtypeByContent("text/nq", "src/test/resources/application/nquads");
    }

    @Test
    public void testDetectRDFXMLByContent() throws Exception {
        detectMIMEtypeByContent("application/rdf+xml", "src/test/resources/application/rdfxml");
    }

    @Test
    public void testDetectTriXByContent() throws Exception {
        detectMIMEtypeByContent("application/trix", "src/test/resources/application/trix");
    }

    @Test
    public void testDetectAtomByContent() throws Exception {
        detectMIMEtypeByContent("application/atom+xml", "src/test/resources/application/atom");
    }

    @Test
    public void testDetectHTMLByContent() throws Exception {
        detectMIMEtypeByContent("text/html", "src/test/resources/text/html");
    }

    @Test
    public void testDetectRDFaByContent() throws Exception {
        detectMIMEtypeByContent("application/xhtml+xml", "src/test/resources/application/rdfa");
    }

    @Test
    public void testDetectXHTMLByContent() throws Exception {
        detectMIMEtypeByContent("application/xhtml+xml", "src/test/resources/application/xhtml");
    }

    @Test
    public void testDetectWSDLByContent() throws Exception {
        detectMIMEtypeByContent("application/x-wsdl", "src/test/resources/application/wsdl");
    }

    @Test
    public void testDetectZIPByContent() throws Exception {
        detectMIMEtypeByContent("application/zip", "src/test/resources/application/zip");
    }

    @Test
    public void testDetectCSVByContent() throws Exception {
        detectMIMEtypeByContent("text/csv", "src/test/resources/org/apache/any23/extractor/csv/");
    }

    /* END: by content. */

    /* BEGIN: by content metadata. */

    @Test
    public void testDetectContentPlainByMeta() throws IOException {
        detectMIMETypeByMimeTypeHint("text/plain", "text/plain");
    }

    @Test
    public void testDetectTextRDFByMeta() throws IOException {
        detectMIMETypeByMimeTypeHint("application/rdf+xml", "text/rdf");
    }

    @Test
    public void testDetectTextN3ByMeta() throws IOException {
        detectMIMETypeByMimeTypeHint(N3, "text/rdf+n3");
    }

    @Test
    public void testDetectTextNQuadsByMeta() throws IOException {
        detectMIMETypeByMimeTypeHint(NQuads, "text/rdf+nq");
    }

    @Test
    public void testDetectTextTurtleByMeta() throws IOException {
        detectMIMETypeByMimeTypeHint(TURTLE, "text/turtle");
    }

    @Test
    public void testDetectRDFXMLByMeta() throws IOException {
        detectMIMETypeByMimeTypeHint(RDFXML, "application/rdf+xml");
    }

    @Test
    public void testDetectXMLByMeta() throws IOException {
        detectMIMETypeByMimeTypeHint(XML, "application/xml");
    }

    @Test
    public void testDetectTriXByMeta() throws IOException {
        detectMIMETypeByMimeTypeHint(TRIX, "application/trix");
    }

    @Test
    public void testDetectExtensionN3ByMeta() throws IOException {
        detectMIMETypeByMimeTypeHint(PLAIN, "text/plain");
    }

    @Test
    public void testDetectXHTMLByMeta() throws IOException {
        detectMIMETypeByMimeTypeHint(XHTML, "application/xhtml+xml");
    }

    @Test
    public void testDetectTextHTMLByMeta() throws IOException {
        detectMIMETypeByMimeTypeHint(HTML, "text/html");
    }

    @Test
    public void testDetectTextPlainByMeta() throws IOException {
        detectMIMETypeByMimeTypeHint(PLAIN, "text/plain");
    }

    @Test
    public void testDetectApplicationXMLByMeta() throws IOException {
        detectMIMETypeByMimeTypeHint(XML, "application/xml");
    }

    @Test
    public void testDetectApplicationCSVByMeta() throws IOException {
        detectMIMETypeByMimeTypeHint(CSV, "text/csv");
    }

    /* END: by content metadata. */

    /* BEGIN: by content and name. */

    @Test
    public void testRDFXMLByContentAndName() throws Exception {
        detectMIMETypeByContentAndName("application/rdf+xml", "src/test/resources/application/rdfxml");
    }

    @Test
    public void testTriXByContentAndName() throws Exception {
        detectMIMETypeByContentAndName("application/trix", "src/test/resources/application/trix");
    }

    @Test
    public void testRSS1ByContentAndName() throws Exception {
        detectMIMETypeByContentAndName("application/rdf+xml", "src/test/resources/application/rss1");
    }

    @Test
    public void testRSS2ByContentAndName() throws Exception {
        detectMIMETypeByContentAndName("application/rss+xml", "src/test/resources/application/rss2");
    }

    @Test
    public void testDetectRDFN3ByContentAndName() throws Exception {
        detectMIMETypeByContentAndName("text/n3", "src/test/resources/application/rdfn3");
    }

    @Test
    public void testDetectRDFNQuadsByContentAndName() throws Exception {
        detectMIMETypeByContentAndName("text/rdf+nq", "src/test/resources/application/nquads");
    }

    @Test
    public void testAtomByContentAndName() throws Exception {
        detectMIMETypeByContentAndName("application/atom+xml", "src/test/resources/application/atom");
    }

    @Test
    public void testHTMLByContentAndName() throws Exception {
        detectMIMETypeByContentAndName("text/html", "src/test/resources/text/html");
    }

    @Test
    public void testXHTMLByContentAndName() throws Exception {
        detectMIMETypeByContentAndName("application/xhtml+xml", "src/test/resources/application/xhtml");
    }

     @Test
    public void testWSDLByContentAndName() throws Exception {
        detectMIMETypeByContentAndName("application/x-wsdl", "src/test/resources/application/wsdl");
    }

    @Test
    public void testZipByContentAndName() throws Exception {
        detectMIMETypeByContentAndName("application/zip", "src/test/resources/application/zip");
    }

    @Test
    public void testRDFaByContentAndName() throws Exception {
        detectMIMETypeByContentAndName("application/xhtml+xml", "src/test/resources/application/rdfa");
    }

    @Test
    public void testCSVByContentAndName() throws Exception {
        detectMIMETypeByContentAndName("text/csv","src/test/resources/org/apache/any23/extractor/csv");
    }

    /* END: by content and name. */

    private void assertN3Detection(String n3Exp) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream( n3Exp.getBytes() );
        Assert.assertTrue( TikaMIMETypeDetector.checkN3Format(bais) );
    }

    private void assertN3DetectionFail(String n3Exp) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream( n3Exp.getBytes() );
        Assert.assertFalse( TikaMIMETypeDetector.checkN3Format(bais) );
    }

    private void assertNQuadsDetection(String n4Exp) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream( n4Exp.getBytes() );
        Assert.assertTrue( TikaMIMETypeDetector.checkNQuadsFormat(bais) );
    }

    private void assertNQuadsDetectionFail(String n4Exp) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream( n4Exp.getBytes() );
        Assert.assertFalse( TikaMIMETypeDetector.checkNQuadsFormat(bais) );
    }

    /**
     * Checks the detection of a specific MIME based on content analysis.
     *
     * @param expectedMimeType the expected mime type.
     * @param testDir the target file.
     * @throws IOException
     */
    private void detectMIMEtypeByContent(String expectedMimeType, String testDir)
    throws IOException {
        File f = new File(testDir);
        String detectedMimeType;
        for (File test : f.listFiles()) {
            if (test.getName().startsWith(".")) continue;
            InputStream is = getInputStream(test);
            detectedMimeType = detector.guessMIMEType(
                    null,
                    is,
                    null
            ).toString();
            if (test.getName().startsWith("error"))
                Assert.assertNotSame(expectedMimeType, detectedMimeType);
            else {
                Assert.assertEquals(
                        String.format("Error in mimetype detection for file %s", test.getAbsolutePath()),
                        expectedMimeType,
                        detectedMimeType
                );
            }
            is.close();
        }
    }

    /**
     * Verifies the detection of a specific MIME based on content, filename and metadata MIME type.
     *
     * @param expectedMimeType
     * @param contentTypeHeader
     * @throws IOException
     */
    private void detectMIMETypeByMimeTypeHint(String expectedMimeType, String contentTypeHeader)
    throws IOException {
        String detectedMimeType = detector.guessMIMEType(
                null,
                null,
                MIMEType.parse(contentTypeHeader)
        ).toString();
        Assert.assertEquals(expectedMimeType, detectedMimeType);
    }

    /**
     * Verifies the detection of a specific MIME based on content and filename.
     *
     * @param expectedMimeType
     * @param testDir
     * @throws IOException
     */
    private void detectMIMETypeByContentAndName(String expectedMimeType, String testDir) throws IOException {
        File f = new File(testDir);
        String detectedMimeType;
        for (File test : f.listFiles()) {
            if (test.getName().startsWith(".")) continue;
            InputStream is = getInputStream(test);
            detectedMimeType = detector.guessMIMEType(test.getName(), is, null).toString();
            if (test.getName().startsWith("error"))
                Assert.assertNotSame(expectedMimeType, detectedMimeType);
            else {
                Assert.assertEquals(
                        String.format("Error while detecting mimetype in file %s", test),
                        expectedMimeType,
                        detectedMimeType
                );
            }
            is.close();
        }
    }

    /**
     * @param file the file to be load.
     * @return the input stream containing the file.
     * @throws IOException
     */
    private InputStream getInputStream(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        while (fis.read(buffer) != -1) {
            bos.write(buffer);
        }
        fis.close();
        InputStream bais;
        bais = new ByteArrayInputStream(bos.toByteArray());
        return bais;
    }
    
}
