package org.deri.any23.servlet;

import java.io.IOException;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.deri.any23.Any23;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.servlet.Servlet;
import org.deri.any23.stream.StringOpener;
import org.deri.any23.writer.TripleHandler;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

public class ServletTest extends TestCase {
    ServletTester tester;
    static String content;
  
    public static class TestableServlet extends Servlet {
		private static final long serialVersionUID = -4439511819287286586L;
		@Override
		protected void doExtract(String url, TripleHandler output) throws ExtractionException, IOException {
			Any23 runner = new Any23();
			runner.extract(new StringOpener(content, url), output);
    	}
    }
   

    protected void setUp() throws Exception {
        super.setUp();
        tester=new ServletTester();
        tester.setContextPath("/");
        tester.addServlet(TestableServlet.class, "/rdfizer/*");
        tester.start();
        content=null;
    }
    protected void tearDown() throws Exception {
        tester.stop();
        tester=null;
        super.tearDown();
    }
    
    public void testNothing() throws Exception {
        HttpTester response = doGetRequest("/rdfizer/");
        assertNull(content);
        assertTrue(response.getMethod()==null);
        assertEquals(404,response.getStatus());
        assertEquals("Invalid request, try /format/some-url.com",response.getContent());
    }

    public void testPostNothing() throws Exception {
        HttpTester response = doPostRequest("/rdfizer/","");
        assertNull(content);
        assertTrue(response.getMethod()==null);
        assertEquals(404,response.getStatus());
        assertEquals("Invalid Format, try one of rdf,n3, turtle or ntriples",response.getContent());
    }
    
    public void testPostNothingToWrongUrl() throws Exception {
        HttpTester response = doPostRequest("/rdfizer/rdf","");
        assertNull(content);
        assertTrue(response.getMethod()==null);
        assertEquals(404,response.getStatus());
        assertEquals("Invalid Format, try one of rdf,n3, turtle or ntriples",response.getContent());
    }
    
    public void testPostOnlyUrl() throws Exception {
        HttpTester response = doPostRequest("/rdfizer/","url=miao");
        assertNull(content);
        assertTrue(response.getMethod()==null);
        assertEquals(404,response.getStatus());
        assertEquals("Invalid Format, try one of rdf,n3, turtle or ntriples",response.getContent());
    }
    
    public void testPostOnlyFormat() throws Exception {
        HttpTester response = doPostRequest("/rdfizer/","format=rdf");
        assertNull(content);
        assertTrue(response.getMethod()==null);
        assertEquals(404,response.getStatus());
        assertEquals("Invalid URL, I'm sorry",response.getContent());
    }
    
    public void testPostOk() throws Exception {
        content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
        HttpTester response = doPostRequest("/rdfizer/","format=rdf&url=http://foo.com");
        assertTrue(response.getMethod()==null);
        assertEquals(200,response.getStatus());
		String res = response.getContent();
		Pattern p = Pattern.compile("rdf:RDF",Pattern.MULTILINE);
		assertTrue(p.matcher(res).find());
		p = Pattern.compile("http://www.w3.org/2006/vcard/ns#VCard");
		assertTrue(p.matcher(res).find());
		p = Pattern.compile("<j.1:fn>Joe</j.1:fn>");
		assertTrue(p.matcher(res).find());
    }
    
    
    private HttpTester doGetRequest(String path) throws IOException, Exception {
        return doRequest(path, "GET");
	}
    
    private HttpTester doPostRequest(String path, String content) throws IOException, Exception {
        HttpTester response = new HttpTester();

		HttpTester request = new HttpTester();
     
		request.setMethod("POST");
		request.setVersion("HTTP/1.0");
		request.setHeader("Host","tester");
		request.setContent(content);
		request.setHeader("Content-Type", "application/x-www-form-urlencoded");
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
		request.setHeader("Host","tester");
		
		request.setURI(path);
        response.parse(tester.getResponses(request.generate()));
		return response;
	}
   
    public void testOnlyFormat() throws Exception {
        HttpTester response = doGetRequest("/rdfizer/xml");
        assertTrue(response.getMethod()==null);
        assertEquals(404,response.getStatus());
        assertEquals("Invalid request, try /format/some-url.com",response.getContent());
    }
    
    public void testWrongFormat() throws Exception {
        HttpTester response = doGetRequest("/rdfizer/dummy/foo.com");
        assertTrue(response.getMethod()==null);
        assertEquals(404,response.getStatus());
        assertEquals("Invalid Format, try one of rdf,n3, turtle or ntriples",response.getContent());
    }
    
    public void testWorks() throws Exception {
        content = "<html><body><div class=\"vcard fn\">Joe</div></body></html>";
    	HttpTester response = doGetRequest("/rdfizer/rdf/foo.com/bar.html");
        assertTrue(response.getMethod()==null);
        assertEquals(200,response.getStatus());
		String res = response.getContent();
		Pattern p = Pattern.compile("rdf:RDF",Pattern.MULTILINE);
		assertTrue(p.matcher(res).find());
		p = Pattern.compile("http://www.w3.org/2006/vcard/ns#VCard");
		assertTrue(p.matcher(res).find());
		p = Pattern.compile("<j.1:fn>Joe</j.1:fn>");
		assertTrue(p.matcher(res).find());	
    }
}
