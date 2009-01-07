package com.google.code.any23.extractors;

import java.net.URI;

import org.w3c.dom.Node;

import com.google.code.any23.HTMLDocument;
import com.google.code.any23.vocab.VCARD;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Extractor for the <a href="http://microformats.org/wiki/geo">Geo</a>
 * microformat.
 * 
 * @author Gabriele Renzi
 */
public class GeoExtractor extends EntityBasedMicroformatExtractor {

	public GeoExtractor(URI baseURI, HTMLDocument document) {
		super(baseURI, document, "geo");
	}

	public boolean extractEntity(Node _node, Model model) {
		if(null==_node)
			return false;
		//try lat & lon
		HTMLDocument node = new HTMLDocument(_node);
		String lat = node.getSingularTextField("latitude");
		String lon = node.getSingularTextField("longitude");
		if("".equals(lat) || "".equals(lon)) {
			String[] both =node.getSingularUrlField("geo").split(";");
			if (both.length!=2)
				return false;
			lat = both[0];
			lon = both[1];
		}
		Resource geo = getBlankNodeFor(model, _node);
		geo.addProperty(RDF.type, VCARD.Location);
		geo.addProperty(VCARD.latitude, lat);
		geo.addProperty(VCARD.longitude, lon);
		return true;
	}

	@Override
	public String getFormatName() {
		return "GEO";
	}
}
