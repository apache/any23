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

package org.deri.any23.http;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.deri.any23.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Opens an {@link InputStream} on an HTTP URI. Is configured
 * with sane values for timeouts, default headers and so on.
 *
 * @author Paolo Capriotti
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class DefaultHTTPClient implements HTTPClient {

    private static final int DEFAULT_TIMEOUT =
            Configuration.instance().getPropertyIntOrFail("any23.http.client.timeout");
    
    private static final int DEFAULT_MAX_CONNECTIONS =
            Configuration.instance().getPropertyIntOrFail("any23.http.client.max.connections");

    private final MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();

    private String userAgent;

    private String accept;

    private HttpClient client = null;

    private long _contentLength = -1;

    private String actualDocumentURI = null;

    private String contentType = null;

    public void init(String userAgent, String acceptHeader) {
        this.userAgent = userAgent;
        this.accept = acceptHeader;
    }

    /**
     *
     * Opens an {@link java.io.InputStream} from a given URI.
     * It follows redirects.
     *
     * @param uri to be opened
     * @return {@link java.io.InputStream}
     * @throws IOException
     */
    public InputStream openInputStream(String uri) throws IOException {

        GetMethod method = null;

        try {
            ensureClientInitialized();
            String uriStr = null;
            try {
                URI uriObj = new URI(uri);
                // [scheme:][//authority][path][?query][#fragment]
                final String path = uriObj.getPath();
                final String query = uriObj.getQuery();
                final String fragment = uriObj.getFragment();
                uriStr = String.format(
                        "%s://%s%s%s%s%s%s",
                        uriObj.getScheme(),
                        uriObj.getAuthority(),
                        path != null ? URLEncoder.encode(path, "UTF-8").replaceAll("%2F", "/") : "",
                        query == null ? "" : "?",
                        query != null ? URLEncoder.encode(query, "UTF-8")
                                .replaceAll("%3D", "=")
                                .replaceAll("%26", "&") 
                            :
                            "",
                        fragment == null ? "" : "#",
                        fragment != null ? URLEncoder.encode(fragment, "UTF-8") : ""
                );
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Invalid URI string.", e);
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
            actualDocumentURI = method.getURI().toString();
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

    public String getActualDocumentURI() {
        return actualDocumentURI;
    }

    public String getContentType() {
        return contentType;
    }

    protected int getConnectionTimeout() {
        return DEFAULT_TIMEOUT;
    }

    protected int getSoTimeout() {
        return DEFAULT_TIMEOUT;
    }

    private void ensureClientInitialized() {
        if (client != null) return;
        client = new HttpClient(manager);
        HttpConnectionManager connectionManager = client.getHttpConnectionManager();
        HttpConnectionManagerParams params = connectionManager.getParams();
        params.setConnectionTimeout(DEFAULT_TIMEOUT);
        params.setSoTimeout(DEFAULT_TIMEOUT);
        params.setMaxTotalConnections(DEFAULT_MAX_CONNECTIONS);

        HostConfiguration hostConf = client.getHostConfiguration();
        List<Header> headers = new ArrayList<Header>();
        headers.add(new Header("User-Agent", userAgent));
        if (accept != null) {
            headers.add(new Header("Accept", accept));
        }
        headers.add(new Header("Accept-Language", "en-us,en-gb,en,*;q=0.3"));
        headers.add(new Header("Accept-Charset", "utf-8,iso-8859-1;q=0.7,*;q=0.5"));
        // headers.add(new Header("Accept-Encoding", "x-gzip, gzip"));
        hostConf.getParams().setParameter("http.default-headers", headers);
    }

}