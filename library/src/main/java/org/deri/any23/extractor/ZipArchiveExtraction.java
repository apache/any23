package org.deri.any23.extractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.deri.any23.mime.MIMEType;
import org.deri.any23.mime.MIMETypeDetector;
import org.deri.any23.stream.InputStreamCache;
import org.deri.any23.stream.InputStreamOpener;
import org.deri.any23.stream.ZipFileOpener;
import org.deri.any23.writer.TripleHandler;
import org.w3c.dom.Document;

public class ZipArchiveExtraction {
	private final URI documentURI;
	private final ExtractorGroup extractors;
	private final TripleHandler output;
	private InputStreamCache cache = null;
	private InputStreamOpener inputOpener = null;
	private MIMETypeDetector detector = null;
	private ExtractorGroup matchingExtractors = null;
	private MIMEType detectedMIMEType = null;
	private Document tagSoupDOM = null;

	public ZipArchiveExtraction( String documentURI, ExtractorFactory<?> factory, TripleHandler output) {
		this(documentURI, 
				new ExtractorGroup(Collections.<ExtractorFactory<?>>singletonList(factory)), 
				output);
		this.setMIMETypeDetector(null);
	}
	
	public ZipArchiveExtraction(String documentURI, ExtractorGroup extractors, TripleHandler output) {
		try {
			this.documentURI =new URI(documentURI);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Invalid URI: " + documentURI, e);
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

	public void run() {
		ZipInputStream zis = null;
		try {
			System.out.println(documentURI);
			zis = new ZipInputStream(new FileInputStream(new File(documentURI)));
			ZipEntry entry = null;
			
			while ((entry = zis.getNextEntry()) != null) {
				if(entry.isDirectory()) {continue;}
				try {
					final String baseuri = URLDecoder.decode(entry.getName(), "utf-8");
					final SingleDocumentExtraction ex = new SingleDocumentExtraction(new ZipFileOpener(zis), baseuri, extractors, output);
					ex.setMIMETypeDetector(detector);
					ex.setStreamCache(cache);
					ex.run();		
				} catch (final UnsupportedEncodingException uex) {
					System.err.println(uex.getClass().getSimpleName()+" "+uex.getMessage()+" for "+entry.getName());
				} catch (final IOException ioex) {
					System.err.println(ioex.getClass().getSimpleName()+" "+ioex.getMessage()+" for "+entry.getName());
				}catch (final ExtractionException eex) {
					System.err.println(eex.getClass().getSimpleName()+" "+eex.getMessage()+" for "+entry.getName());
				}
			}
		}catch (final FileNotFoundException fnfex) {
			System.err.println(fnfex.getClass().getSimpleName()+" "+fnfex.getMessage()+" for "+documentURI);
		} catch (final IOException ioex) {
			System.err.println(ioex.getClass().getSimpleName()+" "+ioex.getMessage()+" for "+documentURI);
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
//	
//	private void runExtractor(Extractor<?> extractor) throws ExtractionException, IOException {
//		ExtractionResultImpl result = new ExtractionResultImpl(documentURI.toString(), output);
//		try {
//			if (extractor instanceof BlindExtractor) {
//				((BlindExtractor) extractor).run(documentURI, result);
//			} else if (extractor instanceof ContentExtractor) {
//				((ContentExtractor) extractor).run(getInputStream(), result);
//			} else if (extractor instanceof TagSoupDOMExtractor) {
//				((TagSoupDOMExtractor) extractor).run(getTagSoupDOM(), result);
//			} else {
//				throw new RuntimeException("Extractor type not supported: " + extractor.getClass());
//			}
//		} finally {
//			result.close();
//		}
//	}
//	
//	private InputStream getInputStream() throws IOException {
//		if (cache == null) {
//			cache = new InputStreamCacheMem();
//		}
//		if (inputOpener == null) {
//			inputOpener = cache.cache(in);
//		}
//		return inputOpener.openInputStream();		
//	}
//	
//	private Document getTagSoupDOM() throws IOException {
//		if (tagSoupDOM == null) {
//			tagSoupDOM = new TagSoupParser(getInputStream(), documentURI.toString()).getDOM();
//		}
//		return tagSoupDOM;
//	}
}
