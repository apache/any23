package org.deri.any23.extractor;

import org.deri.any23.writer.NTriplesWriter;

import junit.framework.TestCase;

public class ZipArchiveExtractionTEST extends TestCase{
	
	
	public void testInit() throws Exception {
		
		String uri = "test/application/zip/4_entries.zip";
		ZipArchiveExtraction ex = new ZipArchiveExtraction(uri,ExtractorRegistry.get().getExtractorGroup(),new NTriplesWriter(System.out));	
		ex.run();
	}
}
