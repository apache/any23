package org.deri.any23.source;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

public class ZipFileDocumentSource implements DocumentSource {
	private ZipInputStream	_zis;
	private String uri;

	/**
	 * @param zis
	 */
	public ZipFileDocumentSource(ZipInputStream zis, String uri) {
		_zis = zis;
		this.uri = uri;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return _zis;
	}

	@Override
	public long getContentLength() {
		return -1;
	}

	@Override
	public String getDocumentURI() {
		return uri;
	}
	
	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public boolean isLocal() {
		return false;
	}
}
