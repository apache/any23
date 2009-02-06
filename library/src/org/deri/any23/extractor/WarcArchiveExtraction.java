package org.deri.any23.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Iterator;

import org.archive.io.ArchiveRecord;
import org.archive.io.ArchiveRecordHeader;
import org.archive.io.warc.WARCConstants;
import org.archive.io.warc.WARCReader;
import org.archive.io.warc.WARCReaderFactory;
import org.deri.any23.extractor.Extractor.BlindExtractor;
import org.deri.any23.extractor.Extractor.ContentExtractor;
import org.deri.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.deri.any23.extractor.html.TagSoupParser;
import org.deri.any23.mime.MIMEType;
import org.deri.any23.mime.MIMETypeDetector;
import org.deri.any23.stream.InputStreamCache;
import org.deri.any23.stream.InputStreamCacheMem;
import org.deri.any23.stream.InputStreamOpener;
import org.deri.any23.stream.WARCFileOpener;
import org.deri.any23.writer.TripleHandler;
import org.w3c.dom.Document;

public class WarcArchiveExtraction {
	private final URI documentURI;
	private final ExtractorGroup extractors;
	private final TripleHandler output;
	private InputStreamCache cache = null;
	private InputStreamOpener inputOpener = null;
	private MIMETypeDetector detector = null;
	private ExtractorGroup matchingExtractors = null;
	private MIMEType detectedMIMEType = null;
	private Document tagSoupDOM = null;

	public WarcArchiveExtraction(String documentURI, ExtractorFactory<?> factory, final TripleHandler output) {
		this(documentURI, 
				new ExtractorGroup(Collections.<ExtractorFactory<?>>singletonList(factory)), 
				output);
		this.setMIMETypeDetector(null);
	}
	
	public WarcArchiveExtraction(String documentURI, ExtractorGroup extractors, TripleHandler output) {
		try {
			this.documentURI = new URI(documentURI);
		} catch (URISyntaxException ex) {
			throw new IllegalArgumentException("Invalid URI: " + documentURI, ex);
		}
		this.extractors = extractors;
		this.output = output;
	}
	
	public void setStreamCache(final InputStreamCache cache) {
		this.cache = cache;
	}
	
	public void setMIMETypeDetector(final MIMETypeDetector detector) {
		this.detector = detector;
	}

	public void run() {
		WARCReader wr;
		boolean success = false;
		try {
			wr = WARCReaderFactory.get(documentURI.toURL());
			ArchiveRecordHeader header;
			ArchiveRecord rec =null;
			final Iterator<ArchiveRecord> iter = wr.iterator();
			while(iter.hasNext()) {
				 rec = iter.next();
				 header = rec.getHeader();
				//we need only the repsonse warc entry
				if(((String)header.getHeaderValue(WARCConstants.HEADER_KEY_TYPE)).equalsIgnoreCase(WARCConstants.RESPONSE)){
					try {
						final String baseuri = URLDecoder.decode(header.getUrl(), "utf-8");
						final SingleDocumentExtraction ex = new SingleDocumentExtraction(new WARCFileOpener(rec), baseuri, extractors, output);
						ex.setMIMETypeDetector(detector);
						ex.setStreamCache(cache);
						ex.run();
						success = (success & ex.hasMatchingExtractors());		
					}catch(final Exception ex) {
						System.err.println(ex.getClass().getSimpleName()+" "+ex.getMessage()+" for "+rec.getHeader().getUrl()+" in "+documentURI);
					}
				}
			}
		} catch (final IOException e) {
			System.err.println(e.getClass().getName() + " " +e.getMessage()+" while processing "+documentURI);
		}
	}
	
//	public String getDetectedMIMEType() throws IOException {
//		filterExtractorsByMIMEType();
//		return detectedMIMEType.toString();
//	}
//	
//	public boolean hasMatchingExtractors() throws IOException {
//		filterExtractorsByMIMEType();
//		return !matchingExtractors.isEmpty();
//	}
//	
//	private void filterExtractorsByMIMEType() throws IOException {
//		if (matchingExtractors != null) return;	// has already been run
//		
//		if (detector == null || extractors.allExtractorsSupportAllContentTypes()) {
//			matchingExtractors = extractors;
//			return;
//		}
//		detectedMIMEType = detector.guessMIMEType(
//				documentURI.getPath(), getInputStream(), null);
//		matchingExtractors = extractors.filterByMIMEType(detectedMIMEType);
//	}
////	
////	private void runExtractor(Extractor<?> extractor) throws ExtractionException, IOException {
////		ExtractionResultImpl result = new ExtractionResultImpl(documentURI.toString(), output);
////		try {
////			if (extractor instanceof BlindExtractor) {
////				((BlindExtractor) extractor).run(documentURI, result);
////			} else if (extractor instanceof ContentExtractor) {
////				((ContentExtractor) extractor).run(getInputStream(), result);
////			} else if (extractor instanceof TagSoupDOMExtractor) {
////				((TagSoupDOMExtractor) extractor).run(getTagSoupDOM(), result);
////			} else {
////				throw new RuntimeException("Extractor type not supported: " + extractor.getClass());
////			}
////		} finally {
////			result.close();
////		}
////	}
////	
////	private InputStream getInputStream() throws IOException {
////		if (cache == null) {
////			cache = new InputStreamCacheMem();
////		}
////		if (inputOpener == null) {
////			inputOpener = cache.cache(in);
////		}
////		return inputOpener.openInputStream();		
////	}
////	
////	private Document getTagSoupDOM() throws IOException {
////		if (tagSoupDOM == null) {
////			tagSoupDOM = new TagSoupParser(getInputStream(), documentURI.toString()).getDOM();
////		}
////		return tagSoupDOM;
////	}
}
