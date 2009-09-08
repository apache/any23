package org.deri.any23.mime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

public class TestGuessMIMEType extends TestCase {
	private final static String N3 = "text/rdf+n3";
	private final static String TURTLE = "application/x-turtle";
	private final static String RDFXML = "application/rdf+xml";
	private final static String XHTML = "application/xhtml+xml";
	private static final String XML = "application/xml";
	private static final String HTML = "text/html";
	private static final String PLAIN = "text/plain";
	
	String detectedMimeType = null;
	
	
	private TikaMIMETypeDetector _identifer;

	protected void setUp() throws Exception {
		super.setUp();
		_identifer = new TikaMIMETypeDetector();
	}
	
	public void testContentPlain() {
		runTest("text/plain", "text/plain", "foo.rdf");
	}
	
	public void testContentTextRdf() {
		runTest("application/rdf+xml", "text/rdf", "foo");
	}
	
	public void testContentTextN3() throws IOException {
		runTest(N3, "text/rdf+n3", "foo");
	}	
	
	public void testContentTextTurtle() throws IOException {
		runTest(TURTLE, "text/turtle", "foo");
	}	
	
	public void testContent() throws IOException {
		runTest(RDFXML, "application/rdf+xml", "foo");
	}
	
	public void testContentXml() throws IOException {
		runTest(XML, "application/xml", "foo.rdf");
	}
	
	public void testExtensionN3() throws IOException {
		runTest("text/plain", "text/plain", "foo.n3");
	}
	
	public void testXmlAndNoExtension() throws IOException {
		runTest(XML, "application/xml", "foo");
	}
	
	public void testTextHtmAndNoExtension() throws IOException {
		runTest(HTML, "text/html", "foo");
	}
	
	public void testTextPlainAndExtensions() throws IOException {
		runTest("text/plain", "text/plain", "foo.html");
		runTest("text/plain", "text/plain", "foo.htm");
		runTest("text/plain", "text/plain", "foo.xhtml");
	}

	public void testApplicationXmlAndExtensions() throws IOException {
		runTest(XML, "application/xml", "foo.html");
		runTest(XML, "application/xml", "foo.htm");
		runTest(XML, "application/xml", "foo.xhtml");
	}
	
	private void runTest(String expectedMimeType,String contentTypeHeader, String fileName)  {
		try{
		File f = new File(fileName);
		if(f.getName().startsWith("."))return;
		
		InputStream is = null;
		if(f.exists()) is = getInputStream(f);
		
//		
//		System.err.println("\n Mime type test: "+expectedMimeType);
//		System.err.println("   Content type header: "+contentTypeHeader);
//		System.err.println("   Input file: "+f+" exisits: "+f.exists());
//		
		
		
		detectedMimeType = _identifer.guessMIMEType(f.getName(),is, MIMEType.parse(contentTypeHeader)).toString();	
		
//		System.err.println("  >> "+detectedMimeType);
		
		if(f.getName().startsWith("error"))
			assertNotSame(expectedMimeType, detectedMimeType);
		else {	
			assertEquals(expectedMimeType, detectedMimeType);
		}
		if(is!=null)
		    is.close();
		
		detectedMimeType = null;
//		System.err.println(" < Success> \n------------------------------------\n");
		}catch(Exception e){e.printStackTrace();}
	}


	/**
	 * @param test
	 * @return 
	 * @throws IOException 
	 */
	private InputStream getInputStream(File test) throws IOException {
		FileInputStream fis = new FileInputStream(test);
		//fis do not support mark and reset, which is required for the identifier
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte [] buffer = new byte[4096];
		while(fis.read(buffer)!=-1) {
			bos.write(buffer);
		}
		fis.close();
		InputStream bais = new ByteArrayInputStream(bos.toByteArray());
		return bais;
	}
}
