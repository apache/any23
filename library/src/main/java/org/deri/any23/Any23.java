package org.deri.any23;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.ExtractorGroup;
import org.deri.any23.extractor.ExtractorRegistry;
import org.deri.any23.extractor.SingleDocumentExtraction;
import org.deri.any23.extractor.WarcArchiveExtraction;
import org.deri.any23.extractor.ZipArchiveExtraction;
import org.deri.any23.http.AcceptHeaderBuilder;
import org.deri.any23.http.DefaultHTTPClient;
import org.deri.any23.http.HTTPClient;
import org.deri.any23.mime.MIMEType;
import org.deri.any23.mime.MIMETypeDetector;
import org.deri.any23.mime.TikaMIMETypeDetector;
import org.deri.any23.stream.FileOpener;
import org.deri.any23.stream.HTTPGetOpener;
import org.deri.any23.stream.InputStreamCache;
import org.deri.any23.stream.InputStreamCacheMem;
import org.deri.any23.stream.InputStreamOpener;
import org.deri.any23.stream.StringOpener;
import org.deri.any23.writer.TripleHandler;


/**
 * A facade with convenience methods for typical Any23 extraction
 * operations.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Any23 {
	private final static Logger logger = Logger.getLogger(Any23.class.getCanonicalName());
	
	public static final String VERSION = "0.2-dev";
	
	private final ExtractorGroup factories;
	private InputStreamCache streamCache;
	private MIMETypeDetector mimeTypeDetector = new TikaMIMETypeDetector();	// can be overridden by setter
	private String userAgent = null;
	private HTTPClient httpClient = new DefaultHTTPClient();
	private boolean httpClientInitialized = false;
	
	public Any23() {
		this((String[]) null);
	}
	
	public Any23(String... extractorNames) {
		factories = (extractorNames == null)
				? ExtractorRegistry.get().getExtractorGroup()
				: ExtractorRegistry.get().getExtractorGroup(Arrays.asList(extractorNames));
		setCacheFactory(new InputStreamCacheMem());
	}
	
	public void setHTTPUserAgent(String userAgent) {
		if (httpClientInitialized) {
			throw new IllegalStateException("Cannot change HTTP configuration after client has been initialized");
		}
		this.userAgent = userAgent;
	}
	
	public void setHTTPClient(HTTPClient httpClient) {
		if (httpClientInitialized) {
			throw new IllegalStateException("Cannot change HTTP configuration after client has been initialized");
		}
		this.httpClient = httpClient;
	}
	
	public void setCacheFactory(InputStreamCache cache) {
		this.streamCache = cache;
	}
	
	public void setMIMETypeDetector(MIMETypeDetector detector) {
		this.mimeTypeDetector = detector;
	}
	
	public boolean extract(String in, String documentURI, TripleHandler outputHandler)
	throws IOException, ExtractionException {
		return extract(new StringOpener(in), documentURI, outputHandler);
	}
	
	public boolean extract(String in, String encoding, String documentURI, TripleHandler outputHandler)
	throws IOException, ExtractionException {
		return extract(new StringOpener(in, encoding), documentURI, outputHandler);
	}
	
	public boolean extract(File file, TripleHandler outputHandler) 
	throws IOException, ExtractionException {
		return extract(file, file.toURI().toString(), outputHandler);
	}
	
	public boolean extract(File file, String documentURI, TripleHandler outputHandler)
	throws IOException, ExtractionException {
		return extract(new FileOpener(file), documentURI, outputHandler);
	}
	
	// Will follow redirects
	public boolean extract(String documentURI, TripleHandler outputHandler)
	throws IOException, ExtractionException {
		try {
			
			if (documentURI.toLowerCase().startsWith("file:")) {
				return extract(new File(new URI(documentURI)), outputHandler);
			}
			if(documentURI.toLowerCase().startsWith("http:")) {
				if (!httpClientInitialized) {
					if (userAgent == null) {
						throw new IOException("Must call " + Any23.class.getSimpleName() + 
								".setHTTPUserAgent(String) before extracting from HTTP URI");
					}
					httpClient.init(userAgent, getAcceptHeader());
					httpClientInitialized = true;
				}
				String normalizedURI = new URI(documentURI).normalize().toString();
				return extract(new HTTPGetOpener(httpClient, normalizedURI), normalizedURI, outputHandler);
			}
		} catch (URISyntaxException ex) {
			throw new ExtractionException(ex);
		}
		return false;
	}

	/**
	 * @param documentURI
	 * @param outputHandler
	 * @return - true by default
	 */
	public boolean extractWARCFile(final String documentURI, final TripleHandler outputHandler) {
		try{
			WarcArchiveExtraction ex = new WarcArchiveExtraction(documentURI, factories, outputHandler);
			ex.setMIMETypeDetector(mimeTypeDetector);
			ex.run();
			return true;
		}
		catch(Exception e){
			logger.log(Level.WARNING,"",e);
			return false;
		}
	}



	/**
	 * @param documentURI
	 * @param outputHandler 
	 */
	public boolean extractZipFile(final String documentURI, final TripleHandler outputHandler) {
		ZipArchiveExtraction ex = new ZipArchiveExtraction(documentURI, factories, outputHandler);
		ex.setMIMETypeDetector(mimeTypeDetector);
		ex.run();
		return true;
		
	}

	public boolean extract(InputStreamOpener in, String documentURI, TripleHandler outputHandler) 
	throws IOException, ExtractionException {
		SingleDocumentExtraction ex = new SingleDocumentExtraction(in, documentURI, factories, outputHandler);
		ex.setMIMETypeDetector(mimeTypeDetector);
		ex.setStreamCache(streamCache);
		ex.run();
		return ex.hasMatchingExtractors();
	}
	
	private String getAcceptHeader() {
		Collection<MIMEType> mimeTypes = new ArrayList<MIMEType>();
		for (ExtractorFactory<?> factory: factories) {
			mimeTypes.addAll(factory.getSupportedMIMETypes());
		}
		return new AcceptHeaderBuilder(mimeTypes).getAcceptHeader();
	}
}
