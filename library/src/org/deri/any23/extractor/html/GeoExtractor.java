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
 * Extractor for the <a href="http://microformats.org/wiki/geo">Geo</a>
 * microformat.
 * 
 * @author Gabriele Renzi
 */
public class GeoExtractor extends EntityBasedMicroformatExtractor {

	protected String getBaseClassName() {
		return "geo";
	}
	
	protected boolean extractEntity(Node node, ExtractionContext context) {
		if (null == node) return false;
		//try lat & lon
		String lat = document.getSingularTextField("latitude");
		String lon = document.getSingularTextField("longitude");
		if("".equals(lat) || "".equals(lon)) {
			String[] both = document.getSingularUrlField("geo").split(";");
			if (both.length != 2) return false;
			lat = both[0];
			lon = both[1];
		}
		BNode geo = getBlankNodeFor(node);
		out.writeTriple(geo, RDF.TYPE, VCARD.Location, context);
		conditionallyAddStringProperty(geo, VCARD.latitude, lat);
		conditionallyAddStringProperty(geo, VCARD.longitude, lon);
		return true;
	}

	public ExtractorDescription getDescription() {
		return factory;
	}
	
	public final static ExtractorFactory<GeoExtractor> factory = 
		SimpleExtractorFactory.create(
				"html-mf-geo",
				PopularPrefixes.createSubset("rdf", "vcard"),
				Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
				null,
				GeoExtractor.class);
}
