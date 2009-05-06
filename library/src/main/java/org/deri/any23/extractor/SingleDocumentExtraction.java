 package org.deri.any23.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.deri.any23.extractor.Extractor.BlindExtractor;
import org.deri.any23.extractor.Extractor.ContentExtractor;
import org.deri.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.deri.any23.extractor.html.TagSoupParser;
import org.deri.any23.mime.MIMEType;
import org.deri.any23.mime.MIMETypeDetector;
import org.deri.any23.rdf.Any23ValueFactoryWrapper;
import org.deri.any23.stream.InputStreamCache;
import org.deri.any23.stream.InputStreamCacheMem;
import org.deri.any23.stream.InputStreamOpener;
import org.deri.any23.writer.TripleHandler;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class SingleDocumentExtraction {
	private final static Logger log = LoggerFactory.getLogger(SingleDocumentExtraction.class);
	
	
	private final InputStreamOpener in;
	private URI documentURI;	// TODO should be final
	private final ExtractorGroup extractors;
	private final TripleHandler output;
	private InputStreamCache cache = null;
	private InputStreamOpener inputOpener = null;
	private MIMETypeDetector detector = null;
	private ExtractorGroup matchingExtractors = null;
	private MIMEType detectedMIMEType = null;
	private Document tagSoupDOM = null;

	public SingleDocumentExtraction(InputStreamOpener in, ExtractorFactory<?> factory, TripleHandler output) {
		this(in, new ExtractorGroup(Collections.<ExtractorFactory<?>>singletonList(factory)), 
				output);
		this.setMIMETypeDetector(null);
	}
	
	public SingleDocumentExtraction(InputStreamOpener in, ExtractorGroup extractors, TripleHandler output) {
		this.in = in;
		log.info("Processing " + in.getDocumentURI());
		this.extractors = extractors;
		this.output = output;
	}
	
	public void setStreamCache(InputStreamCache cache) {
		this.cache = cache;
	}
	
	public void setMIMETypeDetector(MIMETypeDetector detector) {
		this.detector = detector;
	}

	public void run() throws ExtractionException, IOException {
		getInputStream();	// TODO this is a hack to work around some ugliness in HTTPGetOpener
		try {
			this.documentURI = new Any23ValueFactoryWrapper(ValueFactoryImpl.getInstance()).createURI(in.getDocumentURI());
		} catch (Exception ex) {
			throw new IllegalArgumentException("Invalid URI: " + in.getDocumentURI(), ex);
		} 
		filterExtractorsByMIMEType();
		
		StringBuffer sb = new StringBuffer("Extractors ");
		for (ExtractorFactory<?> factory : matchingExtractors) {
			sb.append(factory.getExtractorName());
			sb.append(' ');
		}
		sb.append("match " + documentURI);
		log.debug(sb.toString());
		
//		byte[] buffer = new byte[100];
//		int l = getInputStream().read(buffer);
//		log.debug("Content: " + new String(buffer, 0, l));
		// Invoke all extractors
		output.startDocument(documentURI);
		output.setContentLength(in.getContentLength());
		for (ExtractorFactory<?> factory : matchingExtractors) {
			runExtractor(factory.createExtractor());
		}
		output.endDocument(documentURI);
	}
	
	public String getDetectedMIMEType() throws IOException {
		filterExtractorsByMIMEType();
		return detectedMIMEType.toString();
	}
	
	public boolean hasMatchingExtractors() throws IOException {
		filterExtractorsByMIMEType();
		return !matchingExtractors.isEmpty();
	}
	
	private void filterExtractorsByMIMEType() throws IOException {
		if (matchingExtractors != null) return;	// has already been run
		
		if (detector == null || extractors.allExtractorsSupportAllContentTypes()) {
			matchingExtractors = extractors;
			return;
		}
		detectedMIMEType = detector.guessMIMEType(
				java.net.URI.create(documentURI.stringValue()).getPath(), getInputStream(), null);
		log.debug("detected media type: " + detectedMIMEType);
		matchingExtractors = extractors.filterByMIMEType(detectedMIMEType);
	}
	
	private void runExtractor(Extractor<?> extractor) throws ExtractionException, IOException {
		log.debug("Running " + extractor.getDescription().getExtractorName() + " on " + documentURI);
		long startTime = System.currentTimeMillis();
		ExtractionResultImpl result = new ExtractionResultImpl(documentURI, extractor, output);
		try {
			if (extractor instanceof BlindExtractor) {
				((BlindExtractor) extractor).run(documentURI, documentURI, result);
			} else if (extractor instanceof ContentExtractor) {
				((ContentExtractor) extractor).run(getInputStream(), documentURI, result);
			} else if (extractor instanceof TagSoupDOMExtractor) {
				((TagSoupDOMExtractor) extractor).run(getTagSoupDOM(), documentURI, result);
			} else {
				throw new RuntimeException("Extractor type not supported: " + extractor.getClass());
			}
		} catch (ExtractionException ex) {
			log.info(extractor.getDescription().getExtractorName() + ": " + ex.getMessage());
			throw ex;
		} finally {
			result.close();
			long elapsed = System.currentTimeMillis() - startTime;
			log.debug("Completed " + extractor.getDescription().getExtractorName() + ", " + elapsed + "ms");
		}
	}
	
	private InputStream getInputStream() throws IOException {
		if (cache == null) {
			cache = new InputStreamCacheMem();
		}
		if (inputOpener == null) {
			inputOpener = cache.cache(in);
		}
		return inputOpener.openInputStream();		
	}
	
	private Document getTagSoupDOM() throws IOException {
		if (tagSoupDOM == null) {
			tagSoupDOM = new TagSoupParser(getInputStream(), documentURI.stringValue()).getDOM();
		}
		return tagSoupDOM;
	}
}
