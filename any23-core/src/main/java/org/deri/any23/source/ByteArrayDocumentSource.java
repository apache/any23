/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    public ByteArrayDocumentSource(InputStream inputStream, String documentURI, String contentType)
    throws IOException {
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
