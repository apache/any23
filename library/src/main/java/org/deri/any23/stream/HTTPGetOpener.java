package org.deri.any23.stream;

import java.io.IOException;
import java.io.InputStream;

import org.deri.any23.http.HTTPClient;

public class HTTPGetOpener implements InputStreamOpener {
	private final HTTPClient client;
	private String uri;
	
	public HTTPGetOpener(HTTPClient client, String uri) {
		this.client = client;
		this.uri = uri;
	}
	
	public InputStream openInputStream() throws IOException {
		InputStream result = client.openInputStream(uri);
		// TODO Ultimate ugliness, the result of getDocumentURI() changes if openInputStream is called first.
		if (client.getActualDocumentURI() != null) {
			uri = client.getActualDocumentURI();
		}
		return result;
	}

	@Override
	public long getContentLength() {
		return client.getContentLength();
	}
	
	public String getDocumentURI() {
		return uri;
	}
}
