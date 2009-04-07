package org.deri.any23.stream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Reads an {@link InputStream} into an in-memory buffer
 * and allows the creation of multiple {@link OutputStream}s
 * over the content. The implementation might delay reading
 * from the input until the data is actually needed.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class InputStreamCacheMem implements InputStreamCache {
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

	public InputStreamOpener cache(final InputStreamOpener in) {
		return new InputStreamOpener() {
			private byte[] buffer = null;
			public InputStream openInputStream() throws IOException {
				if (buffer == null) {
					buffer = toByteArray(in.openInputStream());
				}
				return new ByteArrayInputStream(buffer);
			}
		};
	}
}
