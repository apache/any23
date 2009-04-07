package org.deri.any23;

import java.io.IOException;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.stream.InputStreamOpener;
import org.deri.any23.writer.TripleHandler;

public class BatchExtraction {

	public static interface ExtractionInput extends InputStreamOpener {
		String getDocumentURI();
	}
	
	private final Any23 runner;
	
	public BatchExtraction(Any23 runner) {
		this.runner = runner;
	}
	
	public void extractBatch(Iterable<ExtractionInput> sources, TripleHandler outputHandler) 
	throws IOException, ExtractionException {
		for (ExtractionInput source: sources) {
			runner.extract(source, source.getDocumentURI(), outputHandler);
		}
	}
}
