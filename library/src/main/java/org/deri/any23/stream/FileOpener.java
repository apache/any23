package org.deri.any23.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileOpener implements InputStreamOpener {
	private final File file;
	
	public FileOpener(File file) {
		this.file = file;
	}
	
	public InputStream openInputStream() throws IOException {
		return new FileInputStream(file);
	}

	@Override
	public long getContentLength() {
		return file.length();
	}
	
	
}
