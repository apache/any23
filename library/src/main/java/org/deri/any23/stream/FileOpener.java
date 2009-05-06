package org.deri.any23.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileOpener implements InputStreamOpener {
	private final File file;
	private final String uri;
	
	public FileOpener(File file) {
		this.file = file;
		this.uri = file.toURI().toString();
	}
	
	public FileOpener(File file, String baseURI) {
		this.file = file;
		this.uri = baseURI;
	}
	
	public InputStream openInputStream() throws IOException {
		return new FileInputStream(file);
	}

	@Override
	public long getContentLength() {
		return file.length();
	}
	
	@Override
	public String getDocumentURI() {
		return uri;
	}
}
