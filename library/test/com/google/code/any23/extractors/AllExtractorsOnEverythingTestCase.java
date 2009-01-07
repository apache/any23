package com.google.code.any23.extractors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import junit.framework.TestCase;

import org.w3c.dom.DocumentFragment;

import com.google.code.any23.HTMLParser;
import com.google.code.any23.HTMLRDFizer;
import com.google.code.any23.RDFizer.Format;

/*
 * a smoke test, if something explodes it means problems, but is not really reliable by itself
 */
public class AllExtractorsOnEverythingTestCase extends TestCase {
	public void testEverything() throws IOException {

		File root = new File(System.getProperty("test.data",
				"test")
				+ "/html/");
		File[] dirs = root.listFiles();
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.matches(".*html$")&& 
					   !name.matches(".*(me-and-sweetheart|no-valid-rel|no-rel|empty-statcvs).html") && false
					   ;
			}
		};
		for (File dir : dirs) {
			if (dir.isDirectory())
				for (File file : dir.listFiles(filter)) {
					DocumentFragment doc = (DocumentFragment) new HTMLParser(new FileInputStream(file), true).getDocumentNode();
					StringWriter buf = new StringWriter();
					boolean res = new HTMLRDFizer(new URL("http://foo.com"),doc).getText(buf, Format.TURTLE) ;
					assertTrue(file.toString(),res);
				}
		}
	}
}
