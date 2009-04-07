package org.deri.any23.stream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StringOpener implements InputStreamOpener {
	private final String in;
	private final String encoding;

	public StringOpener(String in) {
		this(in, null);
	}
	
	public StringOpener(String in, String encoding) {
		this.in = in;
		this.encoding = encoding;
	}

	public InputStream openInputStream() throws IOException {
		if (encoding == null) {
			return new ByteArrayInputStream(in.getBytes());
		}
		return new ByteArrayInputStream(in.getBytes(encoding));
	}
}
