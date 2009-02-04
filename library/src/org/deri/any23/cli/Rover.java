package org.deri.any23.cli;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.deri.any23.Any23;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.writer.NTriplesWriter;
import org.deri.any23.writer.RDFXMLWriter;
import org.deri.any23.writer.TripleHandler;
import org.deri.any23.writer.TurtleWriter;

/**
 * A default rover implementation. Goes and fetches a URL using an hint
 * as to what format should require, then tries to convert it to RDF.
 * 
 * @author Gabriele Renzi
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Rover {

	private static final String USER_AGENT_NAME = "Any23-CLI";
	/**
	 * A simple main for testing
	 * @param args a url and an optional format name such as TURTLE,N3,N-TRIPLES,RDF/XML
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException, ExtractionException {
		
		if (args.length < 1) {
			System.err.println("Usage: Rover (<url>|<file>) [TURTLE|N3|N-TRIPLES|RDFXML]");
			System.exit(1);
		}

		String uri = args[0];
		if (!uri.matches("[a-zA-Z0-9]+:.*")) {
			// looks like a filename rather than a URI, so convert to file: URI
			uri = new File(uri).toURI().toString();
		} else {
			uri = new URL(args[0].trim()).toString();
		}

		String format = "TURTLE";
		if (args.length > 1)
			format = args[1].toUpperCase();
		TripleHandler output = null;
		if ("TURTLE".equals(format) || "N3".equals(format) || "TTL".equals(format)) {
			output = new TurtleWriter(System.out);
		} else if ("N-TRIPLES".equals(format) || "N-TRIPLE".equals(format) || "NT".equals(format)) {
			output = new NTriplesWriter(System.out);
		} else if ("RDFXML".equals(format)) {
			output = new RDFXMLWriter(System.out);
		} else {
			System.err.println("Unsupported output format '" + format + "'");
			System.exit(1);
		}

		Any23 any23 = new Any23();
		any23.setHTTPUserAgent(USER_AGENT_NAME + "/" + Any23.VERSION);
		if (!any23.extract(uri, output)) {
			System.err.println("No suitable extractors");
			System.exit(2);
		}
	}
}
