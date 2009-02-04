package com.google.code.any23.extractors;

import java.net.URI;

import org.deri.any23.extractor.html.HTMLDocument;
import org.deri.any23.vocab.VCARD;
import org.w3c.dom.Node;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Extractor for the <a href="http://microformats.org/wiki/adr">adr</a> 
 * microformat.
 * 
 * @author Gabriele Renzi
 */
public class AdrExtractor extends EntityBasedMicroformatExtractor {

	public AdrExtractor(URI baseURI, HTMLDocument document) {
		super(baseURI, document, "adr");
	}

	private static final String[] addressFields = {
		   "post-office-box",
		   "extended-address",
		   "street-address",
		   "locality",
		   "region",
		   "country-name",
		   "postal-code"
		};
	
	public boolean extractEntity(Node _node, Model model) {
		if(null==_node)
			return false;
		//try lat & lon
		HTMLDocument doc = new HTMLDocument(_node);
		Resource adr = getBlankNodeFor(model, _node);
		adr.addProperty(RDF.type, VCARD.Address);
		for(String field: addressFields) {
			String[] values = doc.getPluralTextField(field);
			for(String val: values)
				conditionallyAddStringProperty(adr, VCARD.getProperty(field), val);
		}
		String[] types = doc.getPluralTextField("type");
		for(String val: types)
			conditionallyAddStringProperty(adr, VCARD.addressType, val);
		return true;
	}

	@Override
	public String getFormatName() {
		return "ADR";
	}
}

