package org.deri.any23.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

public class ZipFileOpener implements InputStreamOpener {
	
	
	private ZipInputStream	_zis;

	/**
	 * @param zis
	 */
	public ZipFileOpener(ZipInputStream zis) {
		_zis = zis;
	}

	public InputStream openInputStream() throws IOException {
		return _zis;
	}

	@Override
	public long getContentLength() {
		return -1;
	}
}
