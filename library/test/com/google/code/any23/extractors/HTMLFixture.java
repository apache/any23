package com.google.code.any23.extractors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.deri.any23.extractor.html.HTMLDocument;
import org.sindice.rdfizer.HTMLParser;
import org.w3c.dom.Node;


public class HTMLFixture {
	private final String filename;
	private final boolean fragment;

	public HTMLFixture(String filename, boolean fragment) {
		this.filename = filename;
		this.fragment = fragment;
	}

	public HTMLFixture(String filename) {
		this(filename, false);
	}
	
	private InputStream getInputStream() {
		try {
			File file = new File(
					System.getProperty("test.data", "test") + 
					"/html/" + filename);
			if (!file.exists())
				throw new AssertionError("the file "+file.getPath()+" does not exist");
			return new FileInputStream(file);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public Node getDOM() {
		return new HTMLParser(getInputStream(), true).getDocumentNode();
	}
	
	public HTMLDocument getHTMLDocument() {
		return new HTMLDocument(new HTMLParser(getInputStream(), fragment).getDocumentNode());
	}
}
