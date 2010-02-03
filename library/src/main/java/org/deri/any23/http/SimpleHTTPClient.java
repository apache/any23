/**
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
 *
 */

package org.deri.any23.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Opens an {@link InputStream} to an HTTP URI using
 * Java's URL class. Not recommended for general use
 * because it doesn't allow setting of user agent,
 * accept headers and so on.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class SimpleHTTPClient implements HTTPClient {

    private int _contentLength = -1;

    public void init(String userAgent, String acceptHeader) {
        // TODO (medium): fix this
        // we're bad, ignore
    }

    public InputStream openInputStream(String uri) throws IOException {
        if (!uri.toLowerCase().startsWith("http:")) {
            throw new IllegalArgumentException("Not an http:// URI: " + uri);
        }
        HttpURLConnection conn = (HttpURLConnection) new URL(uri).openConnection();
        conn.connect();
        _contentLength = conn.getContentLength();
        return conn.getInputStream();
    }

    public void close() {
        // TODO (high): fix this
        // do nothing
    }

    public long getContentLength() {
        return _contentLength;
    }

    public String getActualDocumentURI() {
        return null;
    }

    public String getContentType() {
        // we're bad, just return nothing
        // TODO (medium): fix this
        return null;
    }
}
