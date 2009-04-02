package org.sindice.rdfizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.Format;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deri.any23.extractor.html.AdrExtractor;
import org.deri.any23.extractor.html.HTMLDocument;
import org.sindice.rdfizer.extractors.MicroformatExtractor;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

import com.google.code.any23.extractors.GeoExtractor;
import com.google.code.any23.extractors.HCalendarExtractor;
import com.google.code.any23.extractors.HCardExtractor;
import com.google.code.any23.extractors.HListingExtractor;
import com.google.code.any23.extractors.HResumeExtractor;
import com.google.code.any23.extractors.HReviewExtractor;
import com.google.code.any23.extractors.RDFMerger;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author Gabriele Renzi
 */
public class SindiceTripleHandler {
	
	public static List<String> runExtractionAndReturnFormats(InputStream, Writer, String format) {
		// @@2 TODO
	}
	
	
	public static final Log LOG = LogFactory
	.getLog(SindiceTripleHandler.class);
	private URI baseURI;
	private final ArrayList<String> formats = new ArrayList<String>(0);
	private final HTMLDocument document;
	private final static String GENERIC_MICROFORMAT = "MICROFORMAT";

	/**
	 * @param url The base URI of the document
	 * @param root The document (or a part of it)
	 */
	public SindiceTripleHandler(URL url, Node root) {
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
//		MicroformatExtractor rdfaExtractor = new RDFaExtractor(baseURI, document);
//		if (rdfaExtractor.extractTo(model)) {
//			formats.add(rdfaExtractor.getFormatName());
//		}
		if (!formats.isEmpty()) {
//			new TitleExtractor(baseURI, document).extractTo(model);
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
		SindiceTripleHandler fizer = new SindiceTripleHandler(new URL("http://foo.com"), (DocumentFragment) new HTMLParser(fs, true).getDocumentNode());
//		fizer.getText(new PrintWriter(System.out), Format.N3);
		
	}
	
	
	
	/**
	 * Returns a list of MicroformatExtractors that will be run sequentially over the HTMLDocument
	 * @param uri rhe base uri
	 * @param doc the document 
	 */
	public MicroformatExtractor[] getMicroformatExtractors(URI uri, HTMLDocument doc) {
		// this could be metaprogrammed, but it's fugly
		MicroformatExtractor[] instances = { //new XFNExtractor(uri, doc),
											new HCardExtractor(uri, doc), 
											new HCalendarExtractor(uri, doc),
											new HReviewExtractor(uri, doc), 
											//new ICBMExtractor(uri, doc), 
											new GeoExtractor(uri, doc),
											new AdrExtractor(uri, doc), 
											//new LicenseExtractor(uri, doc),
											new HListingExtractor(uri, doc),
											new HResumeExtractor(uri, doc)
											};

		return instances;
	}
}
