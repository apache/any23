/**
 * 
 */
package com.google.code.any23;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethodBase;

class MockMethod extends HttpMethodBase {

	private String format;
	private String content;

	public MockMethod(String format, String content) {
		this.format = format;
		this.content = content;
	}

	@Override
	public String getName() {
		return "name";
	}

	public int getStatusCode() {
		return 200;
	}

	@Override
	public Header getResponseHeader(String name) {
		Header header = new Header("Content-Type", format);
		return header;
	}

	public InputStream getResponseBodyAsStream() {
		return new ByteArrayInputStream(content.getBytes());
	}

}