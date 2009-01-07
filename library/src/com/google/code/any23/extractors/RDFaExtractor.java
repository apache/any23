package com.google.code.any23.extractors;

import java.net.URI;

import com.google.code.any23.HTMLDocument;




public class RDFaExtractor extends XsltMicroformatExtractor {

	public static void main(String[] args) {
		doExtraction(new RDFaExtractor(URI.create("http://foo.com"),getDocumentFromArgs(args)));
	}
	public RDFaExtractor(URI uri, HTMLDocument doc) {
		super(uri,doc, "xslt/rdfa.xslt");
	}
	@Override
	public String getFormatName() {
		return "RDFA";
	}
}
