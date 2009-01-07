package com.google.code.any23.extractors;

import java.net.URI;


import com.google.code.any23.HTMLDocument;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.DC;

/**
 * Extracts the value of the &lt;title&gt; element of an 
 * HTML or XHTML page. 
 * 
 * TODO: Should probably use dcterms:title not dc:title?
 * 
 * @version $Id$
 * @author Richard Cyganiak (richard at cyganiak dot de)
 */
public class TitleExtractor extends MicroformatExtractor {
	public TitleExtractor(URI baseURI, HTMLDocument document) {
		super(baseURI, document);
	}
	
	public boolean extractTo(Model model) {
		String title = document.find("/HTML/HEAD/TITLE/text()");
		if (title == null || "".equals(title)) {
			return false;
		}
		model.add(model.createResource(baseURI.toString()), DC.title, title.trim());
		return true;
	}


	@Override
	public String getFormatName() {
		return "";
	}
}
