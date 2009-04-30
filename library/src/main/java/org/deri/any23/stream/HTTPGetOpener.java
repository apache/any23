package org.deri.any23.stream;

import java.io.IOException;
import java.io.InputStream;

import org.deri.any23.http.HTTPClient;

public class HTTPGetOpener implements InputStreamOpener {
	private final HTTPClient client;
	private final String uri;
	
	public HTTPGetOpener(HTTPClient client, String uri) {
		this.client = client;
		this.uri = uri;
	}
	
	public InputStream openInputStream() throws IOException {
		return client.openInputStream(uri);
	}

	@Override
	public long getContentLength() {
		return client.getContentLength();
	}
}
