package org.deri.any23.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

public class ZipFileOpener implements InputStreamOpener {
	private ZipInputStream	_zis;
	private String uri;

	/**
	 * @param zis
	 */
	public ZipFileOpener(ZipInputStream zis, String uri) {
		_zis = zis;
		this.uri = uri;
	}

	public InputStream openInputStream() throws IOException {
		return _zis;
	}

	@Override
	public long getContentLength() {
		return -1;
	}
	
	public String getDocumentURI() {
		return uri;
	}
}
