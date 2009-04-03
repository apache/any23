package org.deri.any23.mime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;



import junit.framework.TestCase;

public class TestGuessMIMEType extends TestCase {
	private TikaMIMETypeDetector _identifer;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		_identifer = new TikaMIMETypeDetector();
	}
	
	public void testContentPlain() {
		runTest("application/rdf+xml", "text/plain", "foo.rdf",true);
	}
	
	public void testContentTextRdf() {
		runTest("application/rdf+xml", "text/rdf", "foo",true);
	}
	
	public void testContentTextN3() {
		runTest(Format.N3, "text/rdf+n3", "foo");
	}	
	
	public void testContentTextTurtle() {
		runTest(Format.TURTLE, "text/turtle", "foo");
	}	
	
	public void testContent() {
		runTest(Format.RDFXML, "application/rdf+xml", "foo");
	}
	
	public void testContentXml() {
		runTest(Format.RDFXML, "application/xml", "foo.rdf");
	}
	
	public void testExtensionN3() {
		runTest(Format.N3, "text/plain", "foo.n3");
	}
	
	public void testXmlAndNoExtension() {
		runTest(Format.RDFXML, "application/xml", "foo");
	}
	public void testTextXmlAndNoExtension() {
		runTest(Format.RDFXML, "text/xml", "foo");
	}

	public void testTextHtmAndNoExtension() {
		runTest(Format.XHTML, "text/html", "foo");
	}
	
	public void testTextPlainAndExtensions() {
		runTest(Format.XHTML, "text/plain", "foo.html");
		runTest(Format.XHTML, "text/plain", "foo.htm");
		runTest(Format.XHTML, "text/plain", "foo.xhtml");
	}

	public void testApplicationXmlAndExtensions() {
		runTest(Format.XHTML, "application/xml", "foo.html");
		runTest(Format.XHTML, "application/xml", "foo.htm");
		runTest(Format.XHTML, "application/xml", "foo.xhtml");
	}
	
	private void assertGuess(RDFizer.Format f, String a, String b) {
		runTest(f, rover.guess(a,b));
	}
	
	private void runTest(String expectedMimeType,String contentTypeHeader, String testDir, boolean b)  {
		try{
		File f = new File(testDir);
		if(f.getName().startsWith("."))return;
		
		System.err.println(" Test for mime type: "+expectedMimeType);
		String detectedMimeType = null;
		InputStream is=null;
		if(f.exists())
		 is = getInputStream(f);
		detectedMimeType = _identifer.guessMIMEType(f.getName(),is, MIMEType.parse(contentTypeHeader)).toString();	
		if(b) System.out.println("  "+f.getName()+"     >> "+detectedMimeType);
		if(f.getName().startsWith("error"))
			assertNotSame(expectedMimeType, detectedMimeType);
		else {	
			assertEquals(expectedMimeType, detectedMimeType);
		}
		is.close();
		detectedMimeType = null;
		System.err.println(" < Success> \n------------------------------------\n");
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * @param string
	 * @param string2
	 * @throws IOException 
	 */
	private void runTest(final String expectedMimeType, String contentTypeHeader, String file) throws IOException {
		runTest(expectedMimeType,contentTypeHeader,file,false);
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
