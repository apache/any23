package org.deri.any23.source;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.deri.any23.http.HTTPClient;

public class HTTPDocumentSource implements DocumentSource {
	private final HTTPClient client;
	private String uri;
	private InputStream unusedInputStream = null;
	private boolean loaded = false;
	
	public HTTPDocumentSource(HTTPClient client, String uri) throws URISyntaxException {
		this.client = client;
		this.uri = normalize(uri);
	}

	private String normalize(String uri) throws URISyntaxException {
		return new URI(uri).normalize().toString();
	}
	
	private void ensureOpen() throws IOException {
		if (loaded) return;
		loaded = true;
		unusedInputStream = client.openInputStream(uri);
		if (client.getActualDocumentURI() != null) {
			uri = client.getActualDocumentURI();
		}
	}
	
	@Override
	public InputStream openInputStream() throws IOException {
		ensureOpen();
		if (unusedInputStream != null) {
			InputStream temp = unusedInputStream;
			unusedInputStream = null;
			return temp;
		}
		return client.openInputStream(uri);
	}

	@Override
	public long getContentLength() {
		return client.getContentLength();
	}
	
	@Override
	public String getDocumentURI() {
		return uri;
	}
	
	@Override
	public String getContentType() {
		return client.getContentType();
	}
	
	@Override
	public boolean isLocal() {
		return false;
	}
}
