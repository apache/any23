package org.deri.any23.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Opens an {@link InputStream} to an HTTP URI using
 * Java's URL class. Not recommended for general use
 * because it doesn't allow setting of user agent,
 * accept headers and so on.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class SimpleHTTPClient implements HTTPClient {

	private int _contentLength=-1;

	public void init(String userAgent, String acceptHeader) {
		// we're bad, ignore
	}
	
	public InputStream openInputStream(String uri) throws IOException {
		if (!uri.toLowerCase().startsWith("http:")) {
			throw new IllegalArgumentException("Not an http:// URI: " + uri);
		}
		HttpURLConnection conn =(HttpURLConnection) new URL(uri).openConnection(); 
		conn.connect();
		_contentLength= conn.getContentLength();
		return conn.getInputStream();
	}

	public void close() {
		// do nothing
	}

	@Override
	public long getContentLength() {
		return _contentLength;
	}
}
