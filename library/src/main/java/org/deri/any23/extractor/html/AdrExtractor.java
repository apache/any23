package org.deri.any23.extractor.html;

import java.util.Arrays;

import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.VCARD;
import org.openrdf.model.BNode;
import org.openrdf.model.vocabulary.RDF;
import org.w3c.dom.Node;

/**
 * Extractor for the <a href="http://microformats.org/wiki/adr">adr</a> 
 * microformat.
 * 
 * @author Gabriele Renzi
 */
public class AdrExtractor extends EntityBasedMicroformatExtractor {
	private static final String[] addressFields = {
		   "post-office-box",
		   "extended-address",
		   "street-address",
		   "locality",
		   "region",
		   "country-name",
		   "postal-code"
		};

	protected String getBaseClassName() {
		return "adr";
	}
	
	protected boolean extractEntity(Node node, ExtractionContext context) {
		if (null == node) return false;
		//try lat & lon
		BNode adr = getBlankNodeFor(node);
		out.writeTriple(adr, RDF.TYPE, VCARD.Address, context);
		for (String field: addressFields) {
			String[] values = document.getPluralTextField(field);
			for (String val: values) {
				conditionallyAddStringProperty(adr, VCARD.getProperty(field), val);
			}
		}
		String[] types = document.getPluralTextField("type");
		for (String val: types) {
			conditionallyAddStringProperty(adr, VCARD.addressType, val);
		}
		return true;
	}

	public ExtractorDescription getDescription() {
		return factory;
	}
	
	public final static ExtractorFactory<AdrExtractor> factory = 
		SimpleExtractorFactory.create(
				"html-mf-adr",
				PopularPrefixes.createSubset("rdf", "vcard"),
				Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
				null,
				AdrExtractor.class);
}

