package org.deri.any23.source;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Creates local copies of {@link org.deri.any23.source.DocumentSource} by
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
        return new ByteArrayDocumentSource(in.openInputStream(), in.getDocumentURI(), in.getContentType());
    }
}
