package org.deri.any23.servlet;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import junit.framework.TestCase;

import org.deri.any23.http.HTTPClient;
import org.deri.any23.source.DocumentSource;
import org.deri.any23.source.StringDocumentSource;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

public class ServletTest extends TestCase {
	ServletTester tester;
	static String content;
	static String acceptHeader;
	static String requestedURI;

	protected void setUp() throws Exception {
		super.setUp();
		tester = new ServletTester();
		tester.setContextPath("/");
		tester.addServlet(TestableServlet.class, "/*");
		tester.start();
		content = "test";
		acceptHeader = null;
		requestedURI = null;
	}

	protected void tearDown() throws Exception {
		tester.stop();
		tester = null;
		super.tearDown();
	}

	public void testGETOnlyFormat() throws Exception {
		HttpTester response = doGetRequest("/xml");
		assertEquals(404, response.getStatus());
		assertContains("Missing URI", response.getContent());
	}

	public void testGETWrongFormat() throws Exception {
		HttpTester response = doGetRequest("/dummy/foo.com");
		assertEquals(400, response.getStatus());
		assertContains("Invalid format", response.getContent());
	}

	public void testGETInvalidURI() throws Exception {
		HttpTester response = doGetRequest("/xml/mailto:richard@cyganiak.de");
		assertEquals(400, response.getStatus());
		assertContains("Invalid input URI", response.getContent());
	}

	public void testGETWorks() throws Exception {
		content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
		HttpTester response = doGetRequest("/nt/foo.com/bar.html");
		assertEquals(200, response.getStatus());
		assertEquals("http://foo.com/bar.html", requestedURI);
		String res = response.getContent();
		assertContains("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2006/vcard/ns#VCard>", res);
	}
	
	public void testGETAddsHTTPScheme() throws Exception {
		content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
		HttpTester response = doGetRequest("/nt/foo.com");
		assertEquals(200, response.getStatus());
		assertEquals("http://foo.com", requestedURI);
	}
	
	public void testGETIncludesQueryString() throws Exception {
		content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
		HttpTester response = doGetRequest("/nt/http://foo.com?id=1");
		assertEquals(200, response.getStatus());
		assertEquals("http://foo.com?id=1", requestedURI);
	}
	
	public void testGETwithURIinParam() throws Exception {
		content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
		HttpTester response = doGetRequest("/nt?uri=http://foo.com?id=1");
		assertEquals(200, response.getStatus());
		assertEquals("http://foo.com?id=1", requestedURI);
	}

	public void testGETwithFormatAndURIinParam() throws Exception {
		content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
		HttpTester response = doGetRequest("/?format=nt&uri=http://foo.com?id=1");
		assertEquals(200, response.getStatus());
		assertEquals("http://foo.com?id=1", requestedURI);
	}
	
	public void testGETwithURLDecoding() throws Exception {
		content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
		HttpTester response = doGetRequest("/nt/http%3A%2F%2Ffoo.com");
		assertEquals(200, response.getStatus());
		assertEquals("http://foo.com", requestedURI);
	}
	
	public void testGETwithURLDecodingInParam() throws Exception {
		content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
		HttpTester response = doGetRequest("/nt?uri=http%3A%2F%2Ffoo.com");
		assertEquals(200, response.getStatus());
		assertEquals("http://foo.com", requestedURI);
	}

	public void testPOSTNothing() throws Exception {
		HttpTester response = doPostRequest("/", "", null);
		assertEquals(400, response.getStatus());
		assertContains("Invalid POST request", response.getContent());
	}

	public void testPOSTWorks() throws Exception {
		content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
		HttpTester response = doPostRequest("/", "format=nt&uri=http://foo.com", "application/x-www-form-urlencoded");
		assertEquals(200, response.getStatus());
		assertEquals("http://foo.com", requestedURI);
		String res = response.getContent();
		assertContains("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2006/vcard/ns#VCard>", res);
	}
	
	public void testPOSTBodyWorks() throws Exception {
		String body = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
		HttpTester response = doPostRequest("/nt", body, "text/html");
		assertEquals(200, response.getStatus());
		String res = response.getContent();
		assertContains("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2006/vcard/ns#VCard>", res);
		assertNull(requestedURI);
	}
	
	public void testPOSTBodyInParamWorks() throws Exception {
		String body = URLEncoder.encode("<html><body><div class=\"vcard fn\">Joe</div></body></html>", "utf-8");
		HttpTester response = doPostRequest("/", "format=nt&body=" + body, 
				"application/x-www-form-urlencoded");
		assertEquals(200, response.getStatus());
		String res = response.getContent();
		assertContains("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2006/vcard/ns#VCard>", res);
		assertNull(requestedURI);
	}
	
	public void testPOSTonlyURI() throws Exception {
		content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
		HttpTester response = doPostRequest("/", "uri=http://foo.com", "application/x-www-form-urlencoded");
		assertEquals(200, response.getStatus());
		String res = response.getContent();
		assertContains("a vcard:VCard", res);
	}

	public void testPOSTonlyFormat() throws Exception {
		HttpTester response = doPostRequest("/", "format=rdf", "application/x-www-form-urlencoded");
		assertEquals(400, response.getStatus());
		assertContains("uri", response.getContent());
	}

	public void testCorrectBaseURI() throws Exception {
		content = "@prefix foaf: <http://xmlns.com/foaf/0.1/> . <> a foaf:Document .";
		HttpTester response = doGetRequest("/nt/foo.com/test.n3");
		assertEquals(200, response.getStatus());
		assertContains("<http://foo.com/test.n3>", response.getContent());
	}

	public void testDefaultBaseURIinPOST() throws Exception {
		String body = "@prefix foaf: <http://xmlns.com/foaf/0.1/> . <> a foaf:Document .";
		HttpTester response = doPostRequest("/nt", body, "text/rdf+n3;charset=utf-8");
		assertEquals(200, response.getStatus());
		assertContains("<" + Servlet.DEFAULT_BASE_URI + ">", response.getContent());
	}
	
	public void testPOSTwithoutContentType() throws Exception {
		String body = "@prefix foaf: <http://xmlns.com/foaf/0.1/> . <http://example.com/asdf> a foaf:Document .";
		HttpTester response = doPostRequest("/nt", body, null);
		assertEquals(400, response.getStatus());
		assertContains("Content-Type", response.getContent());
	}

	public void testPOSTwithContentTypeParam() throws Exception {
		String body = URLEncoder.encode("<http://foo.bar> <http://foo.bar> <http://foo.bar> .", "utf-8");
		HttpTester response = doPostRequest("/", "format=nt&body=" + body + "&type=application/x-foobar", 
				"application/x-www-form-urlencoded");
		assertEquals(415, response.getStatus());
	}
	
	public void testPOSTbodyMissingFormat() throws Exception {
		HttpTester response = doPostRequest("/", "<html><body><div class=\"vcard fn\">Joe</div></body></html>", "text/html");
		assertEquals(200, response.getStatus());
		String res = response.getContent();
		assertContains("a vcard:VCard", res);
	}
	
	public void testNoExtractableTriples() throws Exception {
		HttpTester response = doPostRequest("/n3", "<html><body>asdf</body></html>", "text/html");
		assertEquals(204, response.getStatus());
		assertNull(response.getContent());
	}
	
	public void testContentNegotiationDefaultsToTurtle() throws Exception {
		content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
		HttpTester response = doGetRequest("/best/http://foo.com");
		assertEquals(200, response.getStatus());
		assertEquals("http://foo.com", requestedURI);
		assertContains("a vcard:VCard", response.getContent());
	}
	
	public void testContentNegotiationForWildcardReturnsTurtle() throws Exception {
		content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
		acceptHeader = "*/*";
		HttpTester response = doGetRequest("/best/http://foo.com");
		assertEquals(200, response.getStatus());
		assertEquals("http://foo.com", requestedURI);
		assertContains("a vcard:VCard", response.getContent());
	}
	
	public void testContentNegotiationForUnacceptableFormatReturns406() throws Exception {
		content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
		acceptHeader = "image/jpeg";
		HttpTester response = doGetRequest("/best/http://foo.com");
		assertEquals(406, response.getStatus());
		assertNull(requestedURI);
	}
	
	public void testContentNegotiationForTurtle() throws Exception {
		content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
		acceptHeader = "text/turtle";
		HttpTester response = doGetRequest("/best/http://foo.com");
		assertEquals(200, response.getStatus());
		assertEquals("http://foo.com", requestedURI);
		assertContains("a vcard:VCard", response.getContent());
	}
	
	public void testContentNegotiationForTurtleAlias() throws Exception {
		content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
		acceptHeader = "application/x-turtle";
		HttpTester response = doGetRequest("/best/http://foo.com");
		assertEquals(200, response.getStatus());
		assertEquals("http://foo.com", requestedURI);
		assertContains("a vcard:VCard", response.getContent());
	}
	
	public void testContentNegotiationForRDFXML() throws Exception {
		content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
		acceptHeader = "application/rdf+xml";
		HttpTester response = doGetRequest("/best/http://foo.com");
		assertEquals(200, response.getStatus());
		assertEquals("http://foo.com", requestedURI);
		assertContains("<rdf:RDF", response.getContent());
	}
	
	public void testContentNegotiationForNTriples() throws Exception {
		content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
		acceptHeader = "text/plain";
		HttpTester response = doGetRequest("/best/http://foo.com");
		assertEquals(200, response.getStatus());
		assertEquals("http://foo.com", requestedURI);
		assertContains("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", response.getContent());
	}
	
	private HttpTester doGetRequest(String path) throws IOException, Exception {
		return doRequest(path, "GET");
	}

	private HttpTester doPostRequest(String path, String content, String contentType)
			throws IOException, Exception {
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

	private HttpTester doRequest(String path, String method)
			throws IOException, Exception {
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

	private void assertContains(String expected, String actual) {
		if (actual.contains(expected))
			return;
		fail("expected <" + expected + "> to be contained in <" + actual + ">");
	}

	public static class TestableServlet extends Servlet {
		private static final long serialVersionUID = -4439511819287286586L;

		@Override
		protected DocumentSource createHTTPDocumentSource(HTTPClient httpClient, String uri) 
		throws IOException, URISyntaxException {
			requestedURI = uri;
			return new StringDocumentSource(content, uri);
		}
	}
}
