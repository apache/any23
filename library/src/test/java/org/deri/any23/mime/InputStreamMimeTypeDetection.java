package org.deri.any23.mime;


import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author juergen
 *         <p/>
 *         TODO Add a method runTest(expectedMIMEType, headerMIMEType, url, contentFile) plus some invocations
 */
public class InputStreamMimeTypeDetection extends TestCase {

    private TikaMIMETypeDetector _identifer;

    /* (non-Javadoc)
      * @see junit.framework.TestCase#setUp()
      */
    protected void setUp() throws Exception {
        super.setUp();
        _identifer = new TikaMIMETypeDetector();
    }

    /* (non-Javadoc)
      * @see junit.framework.TestCase#tearDown()
      */
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void testRDFXML() throws Exception {
        runTest("application/rdf+xml", "src/test/resources/application/rdfxml", true);
    }

    public void testRSS2() throws Exception {
        runTest("application/rss+xml", "src/test/resources/application/rss2", true);
    }

    public void testRSS1() throws Exception {
        runTest("application/rdf+xml", "src/test/resources/application/rss1", true);
    }

    public void testAtom() throws Exception {
        runTest("application/atom+xml", "src/test/resources/application/atom", true);
    }

    public void testHTML() throws Exception {
        runTest("text/html", "src/test/resources/text/html", true);
    }

    public void testXHTML() throws Exception {
        runTest("application/xhtml+xml", "src/test/resources/application/xhtml", true);
    }

    public void testWSDL() throws Exception {
        runTest("application/x-wsdl", "src/test/resources/application/wsdl", true);
    }

    public void testZip() throws Exception {
        runTest("application/zip", "src/test/resources/application/zip", true);
    }

    public void testRDFa() throws Exception {
        runTest("application/xhtml+xml", "src/test/resources/application/rdfa", true);
    }


    /**
     * @param string
     * @param string2
     * @param b
     * @throws IOException
     */
    private void runTest(String expectedMimeType, String testDir, boolean b) throws IOException {
//		System.err.println(" test mime type: "+expectedMimeType +" in folder " +testDir);

        File f = new File(testDir);
        String detectedMimeType = null;
        for (File test : f.listFiles()) {
//		    System.out.println(test);
            if (test.getName().startsWith(".")) continue;
            InputStream is = getInputStream(test);
            detectedMimeType = _identifer.guessMIMEType(null, is, null).toString();
//			if(b) System.out.println("  "+test.getName()+"     >> "+detectedMimeType);
            if (test.getName().startsWith("error"))
                assertNotSame(expectedMimeType, detectedMimeType);
            else {
                assertEquals(expectedMimeType, detectedMimeType);
            }
            is.close();
            detectedMimeType = null;
        }
//		System.err.println(" < Success> \n------------------------------------\n");
    }

    /**
     * @param src/test
     * @return
     * @throws IOException
     */
    private InputStream getInputStream(File test) throws IOException {
        FileInputStream fis = new FileInputStream(test);
        //fis do not support mark and reset, which is required for the identifier
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        while (fis.read(buffer) != -1) {
            bos.write(buffer);
        }
        fis.close();
        InputStream bais = new ByteArrayInputStream(bos.toByteArray());
		return bais;
	}
}
