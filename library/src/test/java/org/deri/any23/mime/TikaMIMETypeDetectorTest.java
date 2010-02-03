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

package org.deri.any23.mime;

import junit.framework.Assert;
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
 * Test case for {@link org.deri.any23.mime.TikaMIMETypeDetector} class.
 *
 * @author juergen
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class TikaMIMETypeDetectorTest {

    private static final String PLAIN  = "text/plain";
    private static final String HTML   = "text/html";
    private static final String XML    = "application/xml";
    private final static String XHTML  = "application/xhtml+xml"; //TODO: high -  Add test.
    private final static String RDFXML = "application/rdf+xml";
    private final static String TURTLE = "application/x-turtle";
    private final static String N3     = "text/rdf+n3";

    private TikaMIMETypeDetector detector;

    @Before
    public void setUp() throws Exception {
        detector = new TikaMIMETypeDetector();
    }

    @After
    public void tearDown() throws Exception {
        detector = null;
    }

    @Test
    public void testDetectByContent() throws IOException {
        InputStream is = getInputStream(new File("src/test/resources/application/rdfxml/physics.owl"));
        String detectedMimeType = detector.guessMIMEType(null, is, null).toString();
        System.err.println(detectedMimeType);
    }

    @Test
    public void testDetectRSS1ByContent() throws Exception {
        detectMIMEtypeByContent("application/rdf+xml", "src/test/resources/application/rss1");
    }

    @Test
    public void testDetectRSS2ByContent() throws Exception {
        detectMIMEtypeByContent("application/rss+xml", "src/test/resources/application/rss2");
    }

    @Test
    public void testDetectRDFXMLByContent() throws Exception {
        detectMIMEtypeByContent("application/rdf+xml", "src/test/resources/application/rdfxml");
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
    public void testDetectContentPlain() throws IOException {
        detectMIMETypeByContentNameAndMeta("text/plain", "text/plain", "foo.rdf");
    }

    @Test
    public void testDetectContentTextRDF() throws IOException {
        detectMIMETypeByContentNameAndMeta("application/rdf+xml", "text/rdf", "foo");
    }

    @Test
    public void testDetectContentTextN3() throws IOException {
        detectMIMETypeByContentNameAndMeta(N3, "text/rdf+n3", "foo");
    }

    @Test
    public void testDetectContentTextTurtle() throws IOException {
        detectMIMETypeByContentNameAndMeta(TURTLE, "text/turtle", "foo");
    }

    @Test
    public void testDetectContentRDFXML() throws IOException {
        detectMIMETypeByContentNameAndMeta(RDFXML, "application/rdf+xml", "foo");
    }

    @Test
    public void testDetectContentXML() throws IOException {
        detectMIMETypeByContentNameAndMeta(XML, "application/xml", "foo.rdf");
    }

    @Test
    public void testDetectExtensionN3() throws IOException {
        detectMIMETypeByContentNameAndMeta(PLAIN, "text/plain", "foo.n3");
    }

    @Test
    public void testDetectXMLAndNoExtension() throws IOException {
        detectMIMETypeByContentNameAndMeta(XML, "application/xml", "foo");
    }

    @Test
    public void testDetectTextHTMLAndNoExtension() throws IOException {
        detectMIMETypeByContentNameAndMeta(HTML, "text/html", "foo");
    }

    @Test
    public void testDetectTextPlainAndExtensions() throws IOException {
        detectMIMETypeByContentNameAndMeta(PLAIN, "text/plain", "foo.html");
        detectMIMETypeByContentNameAndMeta(PLAIN, "text/plain", "foo.htm");
        detectMIMETypeByContentNameAndMeta(PLAIN, "text/plain", "foo.xhtml");
    }

    @Test
    public void testDetectApplicationXMLAndExtensions() throws IOException {
        detectMIMETypeByContentNameAndMeta(XML, "application/xml", "foo.html");
        detectMIMETypeByContentNameAndMeta(XML, "application/xml", "foo.htm");
        detectMIMETypeByContentNameAndMeta(XML, "application/xml", "foo.xhtml");
    }

    @Test
    public void testRDFXML() throws Exception {
        detectMIMETypeByContentAndName("application/rdf+xml", "src/test/resources/application/rdfxml");
    }

    @Test
    public void testRSS2() throws Exception {
        detectMIMETypeByContentAndName("application/rss+xml", "src/test/resources/application/rss2");
    }

    @Test
    public void testRSS1() throws Exception {
        detectMIMETypeByContentAndName("application/rdf+xml", "src/test/resources/application/rss1");
    }

    @Test
    public void testAtom() throws Exception {
        detectMIMETypeByContentAndName("application/atom+xml", "src/test/resources/application/atom");
    }

    @Test
    public void testHTML() throws Exception {
        detectMIMETypeByContentAndName("text/html", "src/test/resources/text/html");
    }

    @Test
    public void testXHTML() throws Exception {
        detectMIMETypeByContentAndName("application/xhtml+xml", "src/test/resources/application/xhtml");
    }

    @Test
    public void testWSDL() throws Exception {
        detectMIMETypeByContentAndName("application/x-wsdl", "src/test/resources/application/wsdl");
    }

    @Test
    public void testZip() throws Exception {
        detectMIMETypeByContentAndName("application/zip", "src/test/resources/application/zip");
    }

    @Test
    public void testRDFa() throws Exception {
        detectMIMETypeByContentAndName("application/xhtml+xml", "src/test/resources/application/rdfa");
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
                Assert.assertEquals(expectedMimeType, detectedMimeType);
            }
            is.close();
        }
    }

    /**
     * Verifies the detection of a specific MIME based on content, filename and metadata MIME type.
     *
     * @param expectedMimeType
     * @param contentTypeHeader
     * @param fileName
     * @throws IOException
     */
    private void detectMIMETypeByContentNameAndMeta(String expectedMimeType, String contentTypeHeader, String fileName)
    throws IOException {
        File f = new File(fileName);
        if (f.getName().startsWith(".")) return;

        InputStream is = null;
        if (f.exists()) is = getInputStream(f);

        String detectedMimeType = detector.guessMIMEType(
                f.getName(),
                is,
                MIMEType.parse(contentTypeHeader)
        ).toString();

        if (f.getName().startsWith("error"))
            Assert.assertNotSame(expectedMimeType, detectedMimeType);
        else {
            Assert.assertEquals(expectedMimeType, detectedMimeType);
        }
        if (is != null)
            is.close();
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
                Assert.assertEquals(expectedMimeType, detectedMimeType);
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
