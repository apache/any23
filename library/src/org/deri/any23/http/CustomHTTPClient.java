package org.deri.any23.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Opens an {@link InputStream} on an HTTP URI by using
 * the provided Apache Commons HTTP client.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class CustomHTTPClient implements HTTPClient {
	private final HttpClient client;
	
	public CustomHTTPClient(HttpClient client) {
		this.client = client;
	}
	
	public InputStream openInputStream(String uri) throws IOException {
		GetMethod method = new GetMethod(uri);
		method.setFollowRedirects(true);
		client.executeMethod(method);
		return method.getResponseBodyAsStream();
	}

	public void close() {
		// do nothing
	}
}
