package com.google.code.any23;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.code.any23.extractors.AdrExtractor;
import com.google.code.any23.extractors.GeoExtractor;
import com.google.code.any23.extractors.GeoUrlExtractor;
import com.google.code.any23.extractors.HCalendarExtractor;
import com.google.code.any23.extractors.HCardExtractor;
import com.google.code.any23.extractors.HListingExtractor;
import com.google.code.any23.extractors.HResumeExtractor;
import com.google.code.any23.extractors.HReviewExtractor;
import com.google.code.any23.extractors.LicenseExtractor;
import com.google.code.any23.extractors.MicroformatExtractor;
import com.google.code.any23.extractors.RDFMerger;
import com.google.code.any23.extractors.RDFaExtractor;
import com.google.code.any23.extractors.TitleExtractor;
import com.google.code.any23.extractors.XFNExtractor;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;



/**
 * The RDFizer that transforms an HTML Document in RDF by using the Extractors infrastructure 
 * @author Gabriele Renzi
 *
 */
public class HTMLRDFizer implements RDFizer {
	public static final Log LOG = LogFactory
	.getLog(HTMLRDFizer.class);
	private URI baseURI;
	private final ArrayList<String> formats = new ArrayList<String>(0);
	private final HTMLDocument document;
	private final static String GENERIC_MICROFORMAT = "MICROFORMAT";

	/**
	 * @param url The base URI of the document
	 * @param root The document (or a part of it)
	 */
	public HTMLRDFizer(URL url, Node root) {
		try {
			this.baseURI = url.toURI();
		} catch (URISyntaxException e) {
			throw new RuntimeException(
					"everything is fucked up, something that is a URL is not a URI",
					e);
		}
		this.document = new HTMLDocument(root);
	}
	
	public String[] getFormats() {
		return formats.toArray(new String[formats.size()]);
	}
	
	public boolean getText(Writer writer, Format format) throws IOException {
		Model model = ModelFactory.createDefaultModel();

		for (MicroformatExtractor e : getMicroformatExtractors(baseURI, document)) {
			try {
				if (e.extractTo(model))
					formats.add(e.getFormatName());
			} catch (Exception ex) {
				// TODO: handle this correctly, avoid catchall
				if (LOG.isWarnEnabled())
					LOG.warn(e, ex);
			}
		}
		
		if (!formats.isEmpty()) {
			formats.add(GENERIC_MICROFORMAT);
		}

		// RDFa is RDF not a microformat, and same for eRDF etc.. in the future
		MicroformatExtractor rdfaExtractor = new RDFaExtractor(baseURI, document);
		if (rdfaExtractor.extractTo(model)) {
			formats.add(rdfaExtractor.getFormatName());
		}
		if (!formats.isEmpty()) {
			new TitleExtractor(baseURI, document).extractTo(model);
			new RDFMerger(baseURI, document).extractTo(model);
			model.write(writer, format.toString());
		}

		model.close();
		return !formats.isEmpty();
	}

	
	/**
	 * An utility method for quick testing
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		FileInputStream fs;
		try {
			fs= new FileInputStream(new File("/home/rff/Desktop/PDI.html"));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		HTMLRDFizer fizer = new HTMLRDFizer(new URL("http://foo.com"), (DocumentFragment) new HTMLParser(fs, true).getDocumentNode());
		fizer.getText(new PrintWriter(System.out), Format.N3);
		
	}
	
	
	
	/**
	 * Returns a list of MicroformatExtractors that will be run sequentially over the HTMLDocument
	 * @param uri rhe base uri
	 * @param doc the document 
	 */
	public MicroformatExtractor[] getMicroformatExtractors(URI uri, HTMLDocument doc) {
		// this could be metaprogrammed, but it's fugly
		MicroformatExtractor[] instances = { new XFNExtractor(uri, doc),
											new HCardExtractor(uri, doc), 
											new HCalendarExtractor(uri, doc),
											new HReviewExtractor(uri, doc), 
											new GeoUrlExtractor(uri, doc), 
											new GeoExtractor(uri, doc),
											new AdrExtractor(uri, doc), 
											new LicenseExtractor(uri, doc),
											new HListingExtractor(uri, doc),
											new HResumeExtractor(uri, doc)
											};

		return instances;
	}
	
	
	
	/**
	 * Returns a list of links taken from the head of the html document.
	 * Used in the Sindice crawler infrastucture, kept here for sanity
	 * @return a map where the key is the link and the value is the anchor text
	 */
	public Map<String,String> getAlternateOutlinks() {
		Map<String, String> result = new HashMap<String,String>();
		
		String anchorText = ""; // <link> does not have text
		NodeList links = document.findAll("//LINK["
				+ "@type='application/rdf+xml' or"
				+ "@type='application/x-turtle' or" + "@type='text/turtle' or "
				+ "@type='text/rdf+n3'" + "]/@href");
		for (int i = 0; i < links.getLength(); i++) {
			String href = links.item(i).getTextContent();
			String uri = baseURI.resolve(href).toString();
				result.put(uri, anchorText);
		}
		return result;

	}
}
