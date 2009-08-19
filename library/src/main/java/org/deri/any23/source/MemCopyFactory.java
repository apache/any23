package org.deri.any23.source;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Creates local copies of {@link DocumentSources} by
 * reading them into an in-memory buffer. This allows opening
 * several input streams over the content at lower cost.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class MemCopyFactory implements LocalCopyFactory {
	private static final int TEMP_SIZE = 10000;
	
	public static byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] temp = new byte[TEMP_SIZE];
		while (true) {
			int bytes = in.read(temp);
			if (bytes == -1) break;
			out.write(temp, 0, bytes);
		}
		return out.toByteArray();
	}

	public DocumentSource createLocalCopy(final DocumentSource in) throws IOException {
		final byte[] buffer = toByteArray(in.openInputStream());
		final String uri = in.getDocumentURI();
		final String contentType = in.getContentType();
		return new DocumentSource() {
			@Override
			public InputStream openInputStream() throws IOException {
				return new ByteArrayInputStream(buffer);
			}
			@Override
			public long getContentLength() {
				return buffer.length;
			}
			@Override
			public String getDocumentURI() {
				return uri;
			}
			@Override
			public String getContentType() {
				return contentType;
			}
			@Override
			public boolean isLocal() {
				return true;
			}
		};
	}
}
