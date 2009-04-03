 package org.deri.any23.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.deri.any23.extractor.Extractor.BlindExtractor;
import org.deri.any23.extractor.Extractor.ContentExtractor;
import org.deri.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.deri.any23.extractor.html.TagSoupParser;
import org.deri.any23.mime.MIMEType;
import org.deri.any23.mime.MIMETypeDetector;
import org.deri.any23.stream.InputStreamCache;
import org.deri.any23.stream.InputStreamCacheMem;
import org.deri.any23.stream.InputStreamOpener;
import org.deri.any23.writer.TripleHandler;
import org.w3c.dom.Document;

public class SingleDocumentExtraction {
	private final InputStreamOpener in;
	private final URI documentURI;
	private final ExtractorGroup extractors;
	private final TripleHandler output;
	private InputStreamCache cache = null;
	private InputStreamOpener inputOpener = null;
	private MIMETypeDetector detector = null;
	private ExtractorGroup matchingExtractors = null;
	private MIMEType detectedMIMEType = null;
	private Document tagSoupDOM = null;

	public SingleDocumentExtraction(InputStreamOpener in, String documentURI, ExtractorFactory<?> factory, TripleHandler output) {
		this(in, documentURI, 
				new ExtractorGroup(Collections.<ExtractorFactory<?>>singletonList(factory)), 
				output);
		this.setMIMETypeDetector(null);
	}
	
	public SingleDocumentExtraction(InputStreamOpener in, String documentURI, ExtractorGroup extractors, TripleHandler output) {
		this.in = in;
		try {
			this.documentURI = new URI(URIUtil.encodeQuery(documentURI));
		} catch (URISyntaxException ex) {
			throw new IllegalArgumentException("Invalid URI: " + documentURI, ex);
		} catch (URIException e) {
			throw new IllegalArgumentException("Malformed URL: " + documentURI, e);
		}
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
		filterExtractorsByMIMEType();
		// Invoke all extractors
		for (ExtractorFactory<?> factory : matchingExtractors) {
			runExtractor(factory.createExtractor());
		}
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
				documentURI.getPath(), getInputStream(), null);
		matchingExtractors = extractors.filterByMIMEType(detectedMIMEType);
	}
	
	private void runExtractor(Extractor<?> extractor) throws ExtractionException, IOException {
		ExtractionResultImpl result = new ExtractionResultImpl(documentURI.toString(), output);
		try {
			if (extractor instanceof BlindExtractor) {
				((BlindExtractor) extractor).run(documentURI, result);
			} else if (extractor instanceof ContentExtractor) {
				((ContentExtractor) extractor).run(getInputStream(), result);
			} else if (extractor instanceof TagSoupDOMExtractor) {
				((TagSoupDOMExtractor) extractor).run(getTagSoupDOM(), result);
			} else {
				throw new RuntimeException("Extractor type not supported: " + extractor.getClass());
			}
		} finally {
			result.close();
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
			tagSoupDOM = new TagSoupParser(getInputStream(), documentURI.toString()).getDOM();
		}
		return tagSoupDOM;
	}
}
