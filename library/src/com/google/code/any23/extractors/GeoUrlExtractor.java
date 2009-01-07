package com.google.code.any23.extractors;
import java.net.URI;


import com.google.code.any23.HTMLDocument;
import com.google.code.any23.vocab.FOAF;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Extractor for "ICBM coordinates" provided as META headers in the head
 * of an HTML page.
 * 
 * @author Gabriele Renzi
 */
public class GeoUrlExtractor extends MicroformatExtractor {
	public GeoUrlExtractor(URI baseURI, HTMLDocument document) {
		super(baseURI, document);
	}
	public static void main(String[] args) {
		doExtraction(new GeoUrlExtractor(URI.create("http://the-current-url"), getDocumentFromArgs(args)));
	}
	public boolean extractTo(Model model) {
		String NS  = "http://www.w3.org/2003/01/geo/wgs84_pos#";
		// ICBM is the preferred method, if two values are available it is meaningless to read both
		String props = document.find("//META[@name=\"ICBM\" or @name=\"geo.position\"]/@content");
		if ("".equals(props)) 
			return false;
		// dummy blank node.. how do we relate it?
		Resource subject = model.createResource();
		String[] coords = props.split("[;,]");
		float lat, lon;
		try {
			lat = Float.parseFloat(coords[0]);
			lon = Float.parseFloat(coords[1]);
		} catch (NumberFormatException nfe) {
			return false;
		}

		// TODO:
		// GR: this is fugly, I expect that there is a better way
		model.add(subject, FOAF.page, absolutizeURI(baseURI.toString()));
		model.add(subject, model.createProperty(NS, "lat"), model.createTypedLiteral(lat));
		model.add(subject, model.createProperty(NS, "lon"), model.createTypedLiteral(lon));
		return true;

	}

	@Override
	public String getFormatName() {
		// TODO ok, not really right but this thing is unnamed
		return "GEOURL";
	}
}
