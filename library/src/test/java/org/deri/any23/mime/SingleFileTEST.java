/**
 * 
 */
package org.deri.any23.mime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;



import junit.framework.TestCase;

/**
 * @author Juergen Umbrich (juergen.umbrich@deri.org)
 *
 */
public class SingleFileTEST extends TestCase{
	private TikaMIMETypeDetector _identifer;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		_identifer = new TikaMIMETypeDetector();
	}
	
	public void testFile() throws Exception {
		String fileName = "src/test/resources/application/rdfxml/physics.owl";
		InputStream is = getInputStream(new File(fileName)); 
		//http://archive.astro.umd.edu/ivoa-onto/src/main/resources/physics.owl
		//  application/rdfxml/physics.owl
		System.out.println(_identifer.guessMIMEType(fileName,is,null));
		
		
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
		byte [] buffer = new byte[4096];
		while(fis.read(buffer)!=-1) {
			bos.write(buffer);
		}
		fis.close();
		InputStream bais = new ByteArrayInputStream(bos.toByteArray());
		return bais;
	}

}
