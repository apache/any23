package org.deri.any23.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

/**
 * Opens an {@link InputStream} on an HTTP URI. Is configured
 * with sane values for timeouts, default headers and so on.
 *
 * @author Paolo Capriotti
 * @author Richard Cyganiak (richard@cyganiak.de)
 *         <p/>
 *         TODO: content length and actual document URI could be messed up in multithreaded situations
 */
public class DefaultHTTPClient implements HTTPClient {
    private static final int DEFAULT_TIMEOUT = 5000;
    private static final int DEFAULT_TOTAL_CONNECTIONS = 5;

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

    private void ensureClientInitialized() {
        if (client != null) return;
        client = new HttpClient(manager);
        HttpConnectionManager connectionManager = client.getHttpConnectionManager();
        HttpConnectionManagerParams params = connectionManager.getParams();
        params.setConnectionTimeout(DEFAULT_TIMEOUT);
        params.setSoTimeout(DEFAULT_TIMEOUT);
        params.setMaxTotalConnections(DEFAULT_TOTAL_CONNECTIONS);

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

    protected int getConnectionTimeout() {
        return DEFAULT_TIMEOUT;
    }

    protected int getSoTimeout() {
        return DEFAULT_TIMEOUT;
    }

    // Will follow redirects
    /* (non-Javadoc)
    * @see org.deri.any23.http.HTTPClient#openInputStream(java.lang.String)
    */

    public InputStream openInputStream(String uri) throws IOException {

        GetMethod method = null;

        try {
            ensureClientInitialized();
            method = new GetMethod(uri);
            method.setFollowRedirects(true);
            client.executeMethod(method);
            _contentLength = method.getResponseContentLength();
            contentType = method.getResponseHeader("Content-Type").getValue();
            if (method.getStatusCode() != 200) {
                throw new IOException("Failed to fetch " + uri + ": " + method.getStatusCode() + " " + method.getStatusText());
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

    /* (non-Javadoc)
    * @see org.deri.any23.http.HTTPClient#close()
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
}