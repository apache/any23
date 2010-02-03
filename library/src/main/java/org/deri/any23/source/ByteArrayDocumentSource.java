package org.deri.any23.source;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ByteArray implementation of {@link org.deri.any23.source.DocumentSource}.
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ByteArrayDocumentSource implements DocumentSource {

    private final byte[] bytes;

    private final String documentURI;

    private final String contentType;

    public ByteArrayDocumentSource(byte[] bytes, String documentURI, String contentType) {
        this.bytes = bytes;
        this.documentURI = documentURI;
        this.contentType = contentType;
    }

    public ByteArrayDocumentSource(InputStream inputStream, String documentURI, String contentType) throws IOException {
        this(MemCopyFactory.toByteArray(inputStream), documentURI, contentType);
    }

    public InputStream openInputStream() throws IOException {
        return new ByteArrayInputStream(bytes);
    }

    public long getContentLength() {
        return bytes.length;
    }

    public String getDocumentURI() {
        return documentURI;
    }

    public String getContentType() {
        return contentType;
    }

    public boolean isLocal() {
        return true;
    }
}
