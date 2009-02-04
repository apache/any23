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
import org.deri.any23.mime.NaiveMIMETypeDetector;
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
	public static final String VERSION = "0.2-dev";
	
	private final ExtractorGroup factories;
	private InputStreamCache streamCache;
	private MIMETypeDetector mimeTypeDetector = new NaiveMIMETypeDetector();	// can be overridden by setter
	private String userAgent = null;
	private HTTPClient httpClient = null;
	
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
		this.userAgent = userAgent;
		if (httpClient != null) {
			httpClient.close();
		}
		httpClient = null;
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
			if (httpClient == null) {
				if (userAgent == null) {
					throw new IOException("Must call ExtractionRunner.setHTTPUserAgent(String) before extracting from HTTP URI");
				}
				httpClient = new DefaultHTTPClient(userAgent, getAcceptHeader());
			}
			String normalizedURI = new URI(documentURI).normalize().toString();
			return extract(new HTTPGetOpener(httpClient, normalizedURI), normalizedURI, outputHandler);
		} catch (URISyntaxException ex) {
			throw new ExtractionException(ex);
		}
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
