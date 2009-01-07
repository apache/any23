/**
 * 
 */
package com.google.code.any23;

import java.io.IOException;

import org.apache.commons.httpclient.HttpMethod;


public class MockFetcher extends Fetcher {

	private String format;
	private String content;

	public MockFetcher(String format, String content) {
		this.format = format;
		this.content = content;
	}

	public HttpMethod get(String arg0, Type arg1) throws IOException {
		HttpMethod res = new MockMethod(format, content);
		return res;
	}

}