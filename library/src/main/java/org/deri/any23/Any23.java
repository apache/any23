package org.deri.any23;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.ExtractorGroup;
import org.deri.any23.extractor.ExtractorRegistry;
import org.deri.any23.extractor.SingleDocumentExtraction;
import org.deri.any23.http.AcceptHeaderBuilder;
import org.deri.any23.http.DefaultHTTPClient;
import org.deri.any23.http.HTTPClient;
import org.deri.any23.mime.MIMEType;
import org.deri.any23.mime.MIMETypeDetector;
import org.deri.any23.mime.TikaMIMETypeDetector;
import org.deri.any23.source.DocumentSource;
import org.deri.any23.source.FileDocumentSource;
import org.deri.any23.source.HTTPDocumentSource;
import org.deri.any23.source.LocalCopyFactory;
import org.deri.any23.source.MemCopyFactory;
import org.deri.any23.source.StringDocumentSource;
import org.deri.any23.writer.TripleHandler;


/**
 * A facade with convenience methods for typical Any23 extraction
 * operations.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class Any23 {
	//private final static Logger logger = Logger.getLogger(Any23.class.getCanonicalName());
	
	public static final String VERSION = "0.2-dev";
	
	private final ExtractorGroup factories;
	private LocalCopyFactory streamCache;
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
		setCacheFactory(new MemCopyFactory());
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
	
	public void setCacheFactory(LocalCopyFactory cache) {
		this.streamCache = cache;
	}
	
	public void setMIMETypeDetector(MIMETypeDetector detector) {
		this.mimeTypeDetector = detector;
	}
	
	public boolean extract(String in, String documentURI, TripleHandler outputHandler)
	throws IOException, ExtractionException {
		return extract(new StringDocumentSource(in, documentURI), outputHandler);
	}
	
	public boolean extract(String in, String documentURI, String contentType, String encoding, TripleHandler outputHandler)
	throws IOException, ExtractionException {
		return extract(new StringDocumentSource(in, documentURI, contentType, encoding), outputHandler);
	}
	
	public boolean extract(File file, TripleHandler outputHandler) 
	throws IOException, ExtractionException {
		return extract(file, file.toURI().toString(), outputHandler);
	}
	
	public boolean extract(File file, String documentURI, TripleHandler outputHandler)
	throws IOException, ExtractionException {
		return extract(new FileDocumentSource(file), outputHandler);
	}
	
	// Will follow redirects
	public boolean extract(String documentURI, TripleHandler outputHandler)
	throws IOException, ExtractionException {
		try {
			if (documentURI.toLowerCase().startsWith("file:")) {
				return extract(new FileDocumentSource(new File(new URI(documentURI))), outputHandler);
			}
			if(documentURI.toLowerCase().startsWith("http:") || documentURI.toLowerCase().startsWith("https:")) {
				return extract(new HTTPDocumentSource(getHTTPClient(), documentURI), outputHandler);
			}
		} catch (URISyntaxException ex) {
			throw new ExtractionException(ex);
		}
		return false;
	}

	public boolean extract(DocumentSource in, TripleHandler outputHandler) 
	throws IOException, ExtractionException {
		SingleDocumentExtraction ex = new SingleDocumentExtraction(in, factories, outputHandler);
		ex.setMIMETypeDetector(mimeTypeDetector);
		ex.setLocalCopyFactory(streamCache);
		ex.run();
		outputHandler.close();
		return ex.hasMatchingExtractors();
	}
	
	private String getAcceptHeader() {
		Collection<MIMEType> mimeTypes = new ArrayList<MIMEType>();
		for (ExtractorFactory<?> factory: factories) {
			mimeTypes.addAll(factory.getSupportedMIMETypes());
		}
		return new AcceptHeaderBuilder(mimeTypes).getAcceptHeader();
	}
	
	public HTTPClient getHTTPClient() throws IOException {
		if (!httpClientInitialized) {
			if (userAgent == null) {
				throw new IOException("Must call " + Any23.class.getSimpleName() + 
						".setHTTPUserAgent(String) before extracting from HTTP URI");
			}
			httpClient.init(userAgent, getAcceptHeader());
			httpClientInitialized = true;
		}
		return httpClient;
	}
}
