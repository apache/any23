package com.google.code.any23.extractors;

import java.net.URI;

import org.w3c.dom.NodeList;

import com.google.code.any23.HTMLDocument;
import com.google.code.any23.vocab.MISSING;
import com.hp.hpl.jena.rdf.model.Model;


public class LicenseExtractor extends MicroformatExtractor {
	public LicenseExtractor(URI baseURI, HTMLDocument document) {
		super(baseURI, document);
	}

	public boolean extractTo(Model model) {
		NodeList nodes = document.findAll("//A[@rel='license']/@href");
		if (nodes.getLength()==0)
			return false;
		boolean found= false;
		for(int i=0; i < nodes.getLength();i++) {
			String link = nodes.item(i).getNodeValue();
			if (!"".equals(link)) {
				found |= true;
				model.add(model.createResource(baseURI.toString()), MISSING.DCTerms.license, link);
			}
		}
		return found;
	}


	@Override
	public String getFormatName() {
		return "LICENSE";
	}
}
