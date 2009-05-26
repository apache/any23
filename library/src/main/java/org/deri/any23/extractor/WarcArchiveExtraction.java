package org.deri.any23.extractor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Iterator;

import org.archive.io.ArchiveRecord;
import org.archive.io.ArchiveRecordHeader;
import org.archive.io.warc.WARCConstants;
import org.archive.io.warc.WARCReader;
import org.archive.io.warc.WARCReaderFactory;
import org.deri.any23.mime.MIMETypeDetector;
import org.deri.any23.stream.InputStreamCache;
import org.deri.any23.stream.WARCFileOpener;
import org.deri.any23.writer.TripleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarcArchiveExtraction {
	private final static Logger logger = LoggerFactory.getLogger(WarcArchiveExtraction.class);
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
//		try {
		try {
			wr = WARCReaderFactory.get(documentURI.toURL());
			ArchiveRecordHeader header;
			ArchiveRecord rec =null;
			final Iterator<ArchiveRecord> iter = wr.iterator();
			while(iter.hasNext()) {
				try {
					rec = iter.next();
					header = rec.getHeader();
					//we need only the repsonse warc entry
					if(((String)header.getHeaderValue(WARCConstants.HEADER_KEY_TYPE)).equalsIgnoreCase(WARCConstants.RESPONSE)){
						final SingleDocumentExtraction ex = new SingleDocumentExtraction(new WARCFileOpener(rec), extractors, output);
						
						ex.setMIMETypeDetector(detector);
						ex.setStreamCache(cache);
						ex.run();	
					}
				} catch (UnsupportedEncodingException e1) {
					logger.warn("doc:"+documentURI+" warc-headerURI: "+rec.getHeader().getUrl(), e1);
				} catch (ExtractionException e) {
					logger.warn("doc:"+documentURI+" warc-headerURI: "+rec.getHeader().getUrl(), e);
				} catch (IOException e) {
					logger.warn("doc:"+documentURI+" warc-headerURI: "+rec.getHeader().getUrl(), e);
				} catch(RuntimeException ru){
					logger.error("RuntimeException thrown by SingleDocumentExtraction.run()! "
							+ "doc:"+documentURI+" warc-headerURI: "+rec.getHeader().getUrl(), ru);
				}
			}
		} catch (MalformedURLException e2) {
			logger.warn("doc:"+documentURI, e2);
		} catch (IOException e2) {
			logger.warn("doc:",e2);
		}
	}
}