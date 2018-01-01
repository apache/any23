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

package org.apache.any23.servlet;

import org.apache.any23.http.HTTPClient;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.source.FileDocumentSource;
import org.apache.any23.source.StringDocumentSource;
import org.apache.any23.util.StringUtils;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * Test case for {@link Servlet} class.
 */
// TODO: some test verifications are not strict enough.
//       The assertContainsTag() doesn't verify the entire output content.
public class ServletTest {

    private static String content;
    private static String acceptHeader;
    private static String requestedIRI;

    private ServletTester tester;

    @Before
    public void setUp() throws Exception {
        tester = new ServletTester();
        tester.setContextPath("/");
        tester.addServlet(TestableServlet.class, "/*");
        tester.start();
        content = "test";
        acceptHeader = null;
        requestedIRI = null;
    }

    @After
    public void tearDown() throws Exception {
        tester.stop();
        tester = null;
    }

    @Test
    public void testGETOnlyFormat() throws Exception {
        HttpTester response = doGetRequest("/xml");
        Assert.assertEquals(404, response.getStatus());
        assertContains("Missing IRI", response.getContent());
    }

    @Test
    public void testGETWrongFormat() throws Exception {
        HttpTester response = doGetRequest("/dummy/foo.com");
        Assert.assertEquals(400, response.getStatus());
        assertContains("Invalid format", response.getContent());
    }

    @Test
    public void testGETInvalidIRI() throws Exception {
        HttpTester response = doGetRequest("/xml/mailto:richard@cyganiak.de");
        Assert.assertEquals(400, response.getStatus());
        assertContains("Invalid input IRI", response.getContent());
    }

    @Test
    public void testGETWorks() throws Exception {
        content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
        HttpTester response = doGetRequest("/nt/foo.com/bar.html");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("http://foo.com/bar.html", requestedIRI);
        String res = response.getContent();
        assertContains(
                "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2006/vcard/ns#VCard>",
                res
        );
    }

    @Test
    public void testGETAddsHTTPScheme() throws Exception {
        content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
        HttpTester response = doGetRequest("/nt/foo.com");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("http://foo.com", requestedIRI);
    }

    @Test
    public void testGETIncludesQueryString() throws Exception {
        content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
        HttpTester response = doGetRequest("/nt/http://foo.com?id=1");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("http://foo.com?id=1", requestedIRI);
    }

    @Test
    public void testGETwithIRIinParam() throws Exception {
        content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
        HttpTester response = doGetRequest("/nt?uri=http://foo.com?id=1");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("http://foo.com?id=1", requestedIRI);
    }

    @Test
    public void testGETwithFormatAndIRIinParam() throws Exception {
        content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
        HttpTester response = doGetRequest("/?format=nt&uri=http://foo.com?id=1");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("http://foo.com?id=1", requestedIRI);
    }

    @Test
    public void testGETwithURLDecoding() throws Exception {
        content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
        HttpTester response = doGetRequest("/nt/http%3A%2F%2Ffoo.com");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("http://foo.com", requestedIRI);
    }

    @Test
    public void testGETwithURLDecodingInParam() throws Exception {
        content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
        HttpTester response = doGetRequest("/nt?uri=http%3A%2F%2Ffoo.com");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("http://foo.com", requestedIRI);
    }

    @Test
    public void testPOSTNothing() throws Exception {
        HttpTester response = doPostRequest("/", "", null);
        Assert.assertEquals(400, response.getStatus());
        assertContains("Invalid POST request", response.getContent());
    }

    @Test
    public void testPOSTWorks() throws Exception {
        content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
        HttpTester response = doPostRequest("/", "format=nt&uri=http://foo.com", "application/x-www-form-urlencoded");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("http://foo.com", requestedIRI);
        String res = response.getContent();
        assertContains("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2006/vcard/ns#VCard>", res);
    }

    @Test
    public void testPOSTWorksWithParametersOnContentType() throws Exception {
        content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
        HttpTester response = doPostRequest(
                "/",
                "format=nt&uri=http://foo.com",
                "application/x-www-form-urlencoded;charset=UTF-8"
        );
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("http://foo.com", requestedIRI);
        String res = response.getContent();
        assertContains(
                "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2006/vcard/ns#VCard>",
                res
        );
    }

    @Test
    public void testPOSTBodyWorks() throws Exception {
        String body = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
        HttpTester response = doPostRequest("/nt", body, "text/html");
        Assert.assertEquals(200, response.getStatus());
        String res = response.getContent();
        assertContains(
                "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2006/vcard/ns#VCard>",
                res
        );
        Assert.assertNull(requestedIRI);
    }

    @Test
    public void testPOSTBodyInParamWorks() throws Exception {
        String body = URLEncoder.encode("<html><body><div class=\"vcard fn\">Joe</div></body></html>", "utf-8");
        HttpTester response = doPostRequest("/", "format=nt&body=" + body,
                "application/x-www-form-urlencoded");
        Assert.assertEquals(200, response.getStatus());
        String res = response.getContent();
        assertContains(
                "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2006/vcard/ns#VCard>",
                res
        );
        Assert.assertNull(requestedIRI);
    }

    @Test
    public void testPOSTonlyIRI() throws Exception {
        content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
        HttpTester response = doPostRequest("/", "uri=http://foo.com", "application/x-www-form-urlencoded");
        Assert.assertEquals(200, response.getStatus());
        String res = response.getContent();
        assertContains("a vcard:VCard", res);
    }

    @Test
    public void testPOSTonlyFormat() throws Exception {
        HttpTester response = doPostRequest("/", "format=rdf", "application/x-www-form-urlencoded");
        Assert.assertEquals(400, response.getStatus());
        assertContains("uri", response.getContent());
    }

    /**
     * This test has been disabled in order to avoid external resources dependencies
     * @throws Exception
     */
    @Test
    public void testGETwithURLEncoding() throws Exception {
        content = null;
        HttpTester response = doGetRequest("/best/http://semanticweb.org/wiki/Knud_M%C3%B6ller");
        Assert.assertEquals(200, response.getStatus());
    }

     /**
     * This test has been disabled in order to avoid external resources dependencies
     * @throws Exception
     */
    @Test
    public void testGETwithURLEncodingWithQuery() throws Exception {
        content = null;
        HttpTester response = doGetRequest("/best/http://semanticweb.org/wiki/Knud_M%C3%B6ller?appo=xxx");
        Assert.assertEquals(200, response.getStatus());
    }

     /**
     * This test has been disabled in order to avoid external resources dependencies
     * @throws Exception
     */
    @Test
    public void testGETwithURLEncodingWithFragment() throws Exception {
        content = null;
        HttpTester response = doGetRequest("/best/http://semanticweb.org/wiki/Knud_M%C3%B6ller#abcde");
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void testCorrectBaseIRI() throws Exception {
        content = "@prefix foaf: <http://xmlns.com/foaf/0.1/> . <> a foaf:Document .";
        HttpTester response = doGetRequest("/nt/foo.com/test.n3");
        Assert.assertEquals(200, response.getStatus());
        assertContains("<http://foo.com/test.n3>", response.getContent());
    }

    @Test
    public void testDefaultBaseIRIinPOST() throws Exception {
        String body = "@prefix foaf: <http://xmlns.com/foaf/0.1/> . <> a foaf:Document .";
        HttpTester response = doPostRequest("/nt", body, "text/rdf+n3;charset=utf-8");
        Assert.assertEquals(200, response.getStatus());
        assertContains("<" + Servlet.DEFAULT_BASE_IRI + ">", response.getContent());
    }

    @Test
    public void testPOSTwithoutContentType() throws Exception {
        String body = "@prefix foaf: <http://xmlns.com/foaf/0.1/> . <http://example.com/asdf> a foaf:Document .";
        HttpTester response = doPostRequest("/nt", body, null);
        Assert.assertEquals(400, response.getStatus());
        assertContains("Content-Type", response.getContent());
    }

    @Test
    public void testPOSTwithContentTypeParam() throws Exception {
        String body = URLEncoder.encode("<http://foo.bar> <http://foo.bar> <http://foo.bar> .", "utf-8");
        HttpTester response = doPostRequest("/", "format=nt&body=" + body + "&type=application/x-foobar",
                "application/x-www-form-urlencoded");
        Assert.assertEquals(415, response.getStatus());
    }

    @Test
    public void testPOSTbodyMissingFormat() throws Exception {
        HttpTester response = doPostRequest(
                "/",
                "<html><body><div class=\"vcard fn\">Joe</div></body></html>", "text/html"
        );
        Assert.assertEquals(200, response.getStatus());
        String res = response.getContent();
        assertContains("a vcard:VCard", res);
    }

    @Test
    public void testContentNegotiationDefaultsToTurtle() throws Exception {
        content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
        HttpTester response = doGetRequest("/best/http://foo.com");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("http://foo.com", requestedIRI);
        assertContains("a vcard:VCard", response.getContent());
    }

    @Test
    public void testContentNegotiationForWildcardReturnsTurtle() throws Exception {
        content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
        acceptHeader = "*/*";
        HttpTester response = doGetRequest("/best/http://foo.com");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("http://foo.com", requestedIRI);
        assertContains("a vcard:VCard", response.getContent());
    }

    @Test
    public void testContentNegotiationForUnacceptableFormatReturns406() throws Exception {
        content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
        acceptHeader = "image/jpeg";
        HttpTester response = doGetRequest("/best/http://foo.com");
        Assert.assertEquals(406, response.getStatus());
        Assert.assertNull(requestedIRI);
    }

    @Test
    public void testContentNegotiationForTurtle() throws Exception {
        content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
        acceptHeader = "text/turtle";
        HttpTester response = doGetRequest("/best/http://foo.com");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("http://foo.com", requestedIRI);
        assertContains("a vcard:VCard", response.getContent());
    }

    @Test
    public void testContentNegotiationForTurtleAlias() throws Exception {
        content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
        acceptHeader = "application/x-turtle";
        HttpTester response = doGetRequest("/best/http://foo.com");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("http://foo.com", requestedIRI);
        assertContains("a vcard:VCard", response.getContent());
    }

    @Test
    public void testContentNegotiationForRDFXML() throws Exception {
        content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
        acceptHeader = "application/rdf+xml";
        HttpTester response = doGetRequest("/best/http://foo.com");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("http://foo.com", requestedIRI);
        assertContains("<rdf1:RDF", response.getContent());
    }

    @Test
    public void testContentNegotiationForNTriples() throws Exception {
        content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
        acceptHeader = "text/plain";
        HttpTester response = doGetRequest("/best/http://foo.com");
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("http://foo.com", requestedIRI);
        assertContains("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", response.getContent());
    }

    @Test
    public void testResponseWithReport() throws Exception {
        content = new FileDocumentSource(
                new File("src/test/resources/org/apache/any23/servlet/missing-og-namespace.html")
        ).readStream();
        acceptHeader = "text/plain";
        HttpTester response = doGetRequest("/best/http://foo.com?validation-mode=validate-fix&report=on");
        Assert.assertEquals(200, response.getStatus());
        final String content = response.getContent();
        assertContainsTag("response"        , content);
        assertContainsTag("extractors"      , content);
        assertContainsTag("report"          , content);
        assertContainsTag("message", true, 1 , content);
        assertContainsTag("error"  , true, 1 , content);
        assertContainsTag("error"  , true, 1 , content);
        assertContainsTag("validationReport", content);
        assertContainsTag("errors"          , content);
        assertContainsTag("issues"          , content);
        assertContainsTag("ruleActivations" , content);
        assertContainsTag("data", content);
    }

    @Test
    public void testJSONResponseFormat() throws Exception {
        String body = "<http://sub/1> <http://pred/1> \"123\"^^<http://datatype> <http://graph/1>.";
        HttpTester response = doPostRequest("/json", body, "application/n-quads");
        Assert.assertEquals(200, response.getStatus());
        final String EXPECTED_JSON =
                "[" +
                "{ \"type\" : \"uri\", \"value\" : \"http://sub/1\"}, " +
                "\"http://pred/1\", " +
                "{\"type\" : \"literal\", \"value\" : \"123\", \"lang\" : null, \"datatype\" : \"http://datatype\"}, " +
                "\"http://graph/1\"" +
                "]";
        assertContains(EXPECTED_JSON, response.getContent());
    }

    @Test
    public void testTriXResponseFormat() throws Exception {
        String body = "<http://sub/1> <http://pred/1> \"123\"^^<http://datatype> <http://graph/1>.";
        HttpTester response = doPostRequest("/trix", body, "application/n-quads");
        Assert.assertEquals(200, response.getStatus());
        final String content = response.getContent();
        assertContainsTag("graph" , false, 1, content);
        assertContainsTag("uri"   , false, 3, content);
        assertContainsTag("triple", false, 1, content);
    }

    private HttpTester doGetRequest(String path) throws Exception {
        return doRequest(path, "GET");
    }

    private HttpTester doPostRequest(String path, String content, String contentType) throws Exception {
        HttpTester response = new HttpTester();
        HttpTester request = new HttpTester();

        request.setMethod("POST");
        request.setVersion("HTTP/1.0");
        request.setHeader("Host", "tester");
        request.setContent(content);
        if (contentType != null) {
            request.setHeader("Content-Type", contentType);
        }
        request.setURI(path);
        response.parse(tester.getResponses(request.generate()));
        return response;
    }

    private HttpTester doRequest(String path, String method) throws Exception {
        HttpTester request = new HttpTester();
        HttpTester response = new HttpTester();

        request.setMethod(method);
        request.setVersion("HTTP/1.0");
        request.setHeader("Host", "tester");
        if (acceptHeader != null) {
            request.setHeader("Accept", acceptHeader);
        }

        request.setURI(path);
        response.parse(tester.getResponses(request.generate()));
        return response;
    }

    private void assertContains(String expected, String container) {
        if(expected.length() == 0)
            throw new IllegalArgumentException("expected string must contains at lease one char.");
        if (container.contains(expected)) return;
        Assert.fail("expected '" + expected + "' to be contained in '" + container + "'");
    }

    private void assertContainsTag(String tag, boolean inline, int occurrences, String container) {
        if (inline) {
            Assert.assertEquals(
                    String.format("Cannot find inline tag %s %d times", tag, occurrences),
                    occurrences,
                    StringUtils.countOccurrences(container, "<" + tag + "/>")
            );
        } else {
            Assert.assertEquals(
                    String.format("Cannot find open tag %s %d times", tag, occurrences),
                    occurrences,
                    StringUtils.countOccurrences(container, "<" + tag + ">") +
                    StringUtils.countOccurrences(container, "<" + tag + " ")
            );
            Assert.assertEquals(
                    String.format("Cannot find close tag %s %d times", tag, occurrences),
                    occurrences,
                    StringUtils.countOccurrences(container, "</" + tag + ">")
            );
        }
    }

    private void assertContainsTag(String tag, String container) {
        assertContainsTag(tag, false, 1, container);
    }

    /**
     * Test purpose servlet implementation.
     */
    public static class TestableServlet extends Servlet {

        @Override
        protected DocumentSource createHTTPDocumentSource(HTTPClient httpClient, String uri)
                throws IOException, URISyntaxException {
            requestedIRI = uri;
            if(content != null) {
                return new StringDocumentSource(content, uri);
            } else {
                return super.createHTTPDocumentSource(httpClient, uri);
            }
        }

    }
}
