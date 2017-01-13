/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.any23.http;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Opens an {@link InputStream} on an HTTP IRI. Is configured
 * with sane values for timeouts, default headers and so on.
 *
 * @author Paolo Capriotti
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class DefaultHTTPClient implements HTTPClient {

    private static final Pattern ESCAPED_PATTERN = Pattern.compile("%[0-9a-f]{2}",Pattern.CASE_INSENSITIVE);

    private final MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();

    private HTTPClientConfiguration configuration;

    private HttpClient client = null;

    private long _contentLength = -1;

    private String actualDocumentIRI = null;

    private String contentType = null;

    public static final boolean isUrlEncoded(String url) {
        return ESCAPED_PATTERN.matcher(url).find();
    }

    /**
     * Creates a {@link DefaultHTTPClient} instance already initialized
     *
     * @return populated {@link org.apache.any23.http.DefaultHTTPClient}
     */
    public static DefaultHTTPClient createInitializedHTTPClient() {
        final DefaultHTTPClient defaultHTTPClient = new DefaultHTTPClient();
        defaultHTTPClient.init( DefaultHTTPClientConfiguration.singleton() );
        return defaultHTTPClient;
    }

    public void init(HTTPClientConfiguration configuration) {
        if(configuration == null) throw new NullPointerException("Illegal configuration, cannot be null.");
        this.configuration = configuration;
    }

    /**
     *
     * Opens an {@link java.io.InputStream} from a given IRI.
     * It follows redirects.
     *
     * @param uri to be opened
     * @return {@link java.io.InputStream}
     * @throws IOException if there is an error opening the {@link java.io.InputStream}
     * located at the URI.
     */
    public InputStream openInputStream(String uri) throws IOException {
        GetMethod method = null;
        try {
            ensureClientInitialized();
            String uriStr;
            try {
                URI uriObj = new URI(uri, isUrlEncoded(uri));
                // [scheme:][//authority][path][?query][#fragment]
                uriStr = uriObj.toString();
            } catch (URIException e) {
                throw new IllegalArgumentException("Invalid IRI string.", e);
            }
            method = new GetMethod(uriStr);
            method.setFollowRedirects(true);
            client.executeMethod(method);
            _contentLength = method.getResponseContentLength();
            final Header contentTypeHeader = method.getResponseHeader("Content-Type");
            contentType = contentTypeHeader == null ? null : contentTypeHeader.getValue();
            if (method.getStatusCode() != 200) {
                throw new IOException(
                        "Failed to fetch " + uri + ": " + method.getStatusCode() + " " + method.getStatusText()
                );
            }
            actualDocumentIRI = method.getURI().toString();
            byte[] response = method.getResponseBody();

            return new ByteArrayInputStream(response);
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }
    }

    /**
     * Shuts down the connection manager.
     */
    public void close() {
        manager.shutdown();
    }

    public long getContentLength() {
        return _contentLength;
    }

    public String getActualDocumentIRI() {
        return actualDocumentIRI;
    }

    public String getContentType() {
        return contentType;
    }

    protected int getConnectionTimeout() {
        return configuration.getDefaultTimeout();
    }

    protected int getSoTimeout() {
        return configuration.getDefaultTimeout();
    }

    private void ensureClientInitialized() {
        if(configuration == null) throw new IllegalStateException("client must be initialized first.");
        if (client != null) return;
        client = new HttpClient(manager);
        HttpConnectionManager connectionManager = client.getHttpConnectionManager();
        HttpConnectionManagerParams params = connectionManager.getParams();
        params.setConnectionTimeout(configuration.getDefaultTimeout());
        params.setSoTimeout(configuration.getDefaultTimeout());
        params.setMaxTotalConnections(configuration.getMaxConnections());

        HostConfiguration hostConf = client.getHostConfiguration();
        List<Header> headers = new ArrayList<Header>();
        headers.add(new Header("User-Agent", configuration.getUserAgent()));
        if (configuration.getAcceptHeader() != null) {
            headers.add(new Header("Accept", configuration.getAcceptHeader()));
        }
        headers.add(new Header("Accept-Language", "en-us,en-gb,en,*;q=0.3")); //TODO: this must become parametric.
        headers.add(new Header("Accept-Charset", "utf-8,iso-8859-1;q=0.7,*;q=0.5"));
        // headers.add(new Header("Accept-Encoding", "x-gzip, gzip"));
        hostConf.getParams().setParameter("http.default-headers", headers);
    }

}