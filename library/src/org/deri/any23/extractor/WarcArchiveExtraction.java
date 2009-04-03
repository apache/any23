package org.deri.any23.extractor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.archive.io.ArchiveRecord;
import org.archive.io.ArchiveRecordHeader;
import org.archive.io.warc.WARCConstants;
import org.archive.io.warc.WARCReader;
import org.archive.io.warc.WARCReaderFactory;
import org.deri.any23.mime.MIMEType;
import org.deri.any23.mime.MIMETypeDetector;
import org.deri.any23.stream.InputStreamCache;
import org.deri.any23.stream.InputStreamOpener;
import org.deri.any23.stream.WARCFileOpener;
import org.deri.any23.writer.TripleHandler;
import org.w3c.dom.Document;

public class WarcArchiveExtraction {
	private final static Logger logger = Logger.getLogger(WarcArchiveExtraction.class.getCanonicalName());
	private final URI documentURI;
	private final ExtractorGroup extractors;
	private final TripleHandler output;
	private InputStreamCache cache = null;
	private MIMETypeDetector detector = null;
	

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
//			logger.log(Level.WARNING,"Invalid URI",ex);
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
						logger.log(Level.WARNING,"doc:"+documentURI+" headerURI: "+rec.getHeader().getUrl(),ex);
					}
				}
			}
		} catch (final IOException e) {
			logger.log(Level.WARNING,"doc:"+documentURI,e);
		}
	}
}