package org.deri.any23.cli;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.deri.any23.Any23;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractorRegistry;
import org.deri.any23.writer.NTriplesWriter;
import org.deri.any23.writer.TripleHandler;

public class SingleExtractorTest {

	public static void main(String[] args) throws IOException, ExtractionException {
		
		if (args.length < 2) {
			System.err.println("Usage: SingleExtractorTest <extractorname> (<url>|<file>)");
			System.exit(1);
		}

		String extractorName = args[0];
		if (!ExtractorRegistry.get().isRegisteredName(extractorName)) {
			System.err.println("Unregistered extractor name: " + extractorName);
		}
		
		String uri = args[1];
		if (!uri.matches("[a-zA-Z0-9]+:.*")) {
			// looks like a filename rather than a URI, so convert to file: URI
			uri = new File(uri).toURI().toString();
		}
		uri = new URL(args[1].trim()).toString();

		TripleHandler output = new NTriplesWriter(System.out);

		if (!new Any23(extractorName).extract(uri, output)) {
			System.err.println("Extractor not suitable for content");
			System.exit(2);
		}
	}
}
