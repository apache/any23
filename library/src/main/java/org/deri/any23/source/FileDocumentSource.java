package org.deri.any23.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileDocumentSource implements DocumentSource {
    private final File file;
    private final String uri;

    public FileDocumentSource(File file) {
        this.file = file;
        this.uri = file.toURI().toString();
    }

    public FileDocumentSource(File file, String baseURI) {
        this.file = file;
        this.uri = baseURI;
    }

    public InputStream openInputStream() throws IOException {
        return new FileInputStream(file);
    }

    public long getContentLength() {
        return file.length();
    }

    public String getDocumentURI() {
        return uri;
    }

    public String getContentType() {
        return null;
    }

    public boolean isLocal() {
        return true;
    }
}
