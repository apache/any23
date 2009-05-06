package org.deri.any23.extractor.html;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.deri.any23.extractor.html.HTMLDocument;
import org.deri.any23.extractor.html.TagSoupParser;
import org.deri.any23.stream.FileOpener;
import org.deri.any23.stream.InputStreamOpener;
import org.w3c.dom.Node;

public class HTMLFixture {
	private final String filename;

	public HTMLFixture(String filename) {
		this.filename = filename;
	}

	private File getFile() {
		File file = new File(
				System.getProperty("test.data", "src/test/resources") + 
				"/html/" + filename);
		if (!file.exists())
			throw new AssertionError("the file "+file.getPath()+" does not exist");
		return file;
	}

	public InputStreamOpener getOpener(String baseURI) {
		return new FileOpener(getFile(), baseURI);
	}
	
	public Node getDOM() {
		try {
			return new TagSoupParser(new FileInputStream(getFile()), "http://example.org/").getDOM();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public HTMLDocument getHTMLDocument() {
		return new HTMLDocument(getDOM());
	}
}
