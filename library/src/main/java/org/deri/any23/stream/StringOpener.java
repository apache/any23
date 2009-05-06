package org.deri.any23.stream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StringOpener implements InputStreamOpener {
	private final String in;
	private final String encoding;
	private final String uri;
	
	public StringOpener(String in, String uri) {
		this(in, uri, null);
	}
	
	public StringOpener(String in, String uri, String encoding) {
		this.in = in;
		this.uri = uri;
		this.encoding = encoding;
	}

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
}
