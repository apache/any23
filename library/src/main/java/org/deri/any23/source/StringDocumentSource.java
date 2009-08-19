package org.deri.any23.source;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StringDocumentSource implements DocumentSource {
	private final String in;
	private final String contentType;
	private final String encoding;
	private final String uri;
	
	public StringDocumentSource(String in, String uri) {
		this(in, uri, null, null);
	}
	
	public StringDocumentSource(String in, String uri, String contentType, String encoding) {
		this.in = in;
		this.uri = uri;
		this.contentType = contentType;
		this.encoding = encoding;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		if (encoding == null) {
			return new ByteArrayInputStream(in.getBytes());
		}
		return new ByteArrayInputStream(in.getBytes(encoding));
	}

	@Override
	public long getContentLength() {
		return in.length();
	}
	
	@Override
	public String getDocumentURI() {
		return uri;
	}
	
	@Override
	public String getContentType() {
		return contentType;
	}
	
	@Override
	public boolean isLocal() {
		return true;
	}
}
