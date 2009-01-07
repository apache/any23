package com.google.code.any23;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

public class Fetcher {
	
  public enum Type {
	    RDF,
	    RDF_OR_HTML,
	    HTML,
	    DEFAULT;
	  }

  /**
   * Use a static inner class to perform lazy thread safe initialization of the
   * HttpClient instance. This is more efficient than double checked locking.
   * See <a href=
   * "http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html"
   * >here</a> for a short explanation of this technique.
   * 
   */
  public static final class HttpClientInitializer {
    private static final MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
    private static final HttpClient client = new HttpClient(manager);
    private static final int DEFAULT_TIMEOUT = 5000;
    private static final int DEFAULT_TOTAL_CONNECTIONS = 5;
    private static final String USER_AGENT = "Anything To Triples (any23) Fetcher";

    static {
      HttpConnectionManager connectionManager = client.getHttpConnectionManager();
      HttpConnectionManagerParams params = connectionManager.getParams();
      params.setConnectionTimeout(DEFAULT_TIMEOUT);
      params.setSoTimeout(DEFAULT_TIMEOUT);
      // params.setSendBufferSize(BUFFER_SIZE);
      // params.setReceiveBufferSize(BUFFER_SIZE);
      params.setMaxTotalConnections(DEFAULT_TOTAL_CONNECTIONS);

      HostConfiguration hostConf = client.getHostConfiguration();
      List<Header> headers = new ArrayList<Header>();
      // Set the User Agent in the header
      headers.add(new Header("User-Agent", USER_AGENT));
      // prefer English
      headers.add(new Header("Accept-Language", "en-us,en-gb,en;q=0.7,*;q=0.3"));
      // prefer UTF-8
      headers.add(new Header("Accept-Charset", "utf-8,ISO-8859-1;q=0.7,*;q=0.7"));
      // accept gzipped content
      // headers.add(new Header("Accept-Encoding", "x-gzip, gzip"));
      hostConf.getParams().setParameter("http.default-headers", headers);

      // HTTP proxy server details
      // -------------------------
      // hostConf.setProxy(proxyHost, proxyPort);
      //
      // if (proxyUsername.length() > 0) {
      //
      // AuthScope proxyAuthScope = getAuthScope(
      // this.proxyHost, this.proxyPort, this.proxyRealm);
      //
      // NTCredentials proxyCredentials = new NTCredentials(
      // this.proxyUsername, this.proxyPassword,
      // this.agentHost, this.proxyRealm);
      //
      // client.getState().setProxyCredentials(
      // proxyAuthScope, proxyCredentials);
      // }

    }

  }

  private static HttpClient getClient() {
    return HttpClientInitializer.client;
  }

  /**
   * Perform a GET request. The recommended usage of this method is as follows:
   * 
   * <pre>{@code
   * HttpMethod method = fetcher.get("http://renaud.delbru.fr/rdf/foaf", SindiceFetcher.Type.RDF);
   * InputStream in = null;
   * try {   
   *   in = method.method.getResponseBodyAsStream();
   *   // do something with the input stream
   * } catch(IOException e) {
   *   // handle exceptions
   * } finally {
   *   if (in != null)
   *     in.close();
   *   method.releaseConnection();
   * }
   * }</pre>
   * 
   * @param url the URL to fetch
   * @param contentType symbolic content type to use for the request
   * @return a method object
   * @throws IOException if an error occurs during fetching
   * @see HttpMethod
   */
  public HttpMethod get(String url, Type contentType) throws IOException {
    GetMethod method = new GetMethod(url);
    method.setFollowRedirects(true);
    switch (contentType) {
    case RDF:
      method.setRequestHeader("Accept",
                              "application/rdf+xml, application/xml; q=0.3,"
                                  + "text/xml; q=0.2, */*; q=0.1");
      break;
    case HTML:
      method.setRequestHeader("Accept", "text/html");
      break;
    case RDF_OR_HTML:
      method.setRequestHeader("Accept",
                              "application/rdf+xml, application/xhtml+xml;q=0.3, text/xml;q=0.2,"
                                  + "application/xml;q=0.2, text/html;q=0.3, text/plain;q=0.1, text/n3,"
                                  + "text/rdf+n3;q=0.5, application/x-turtle;q=0.2, text/turtle;q=1");
      break;
    case DEFAULT:
    default:
      // don't set any accept header
      break;
    }
    getClient().executeMethod(method);
    return method;
  }

  /**
   * Convenience method to fetch a text resource with the default content type.
   * 
   * @param url the URL of the text resource to fetch
   * @return Contents of the document at the URL
   * @throws IOException if an error occurs during fetching, or if the resource
   *           is not textual
   * @see #getString(String, Type)
   */
  public String getString(String url) throws IOException {
    return getString(url, Type.DEFAULT);
  }

  /**
   * Release all static resources help by the SindiceFetcher class. Call this
   * method only if you are sure you will not be using SindiceFetcher again in your
   * application, like for example when shutting down a Servlet context.
   */
  public static void shutdown() {
    HttpClientInitializer.manager.shutdown();
  }

  /**
   * Convenience method to perform a GET request using the default content type.
   * 
   * @param url the URL to fetch
   * @return a method object
   * @throws IOException if an error occurs during fetching
   * @see HttpMethod
   */
  public HttpMethod get(String url) throws IOException {
    return get(url, Type.DEFAULT);
  }

  /**
   * Convenience method to fetch a text resource. There's no need to perform any
   * cleanup when using this method.
   * 
   * @param url the URL of the text resource to fetch
   * @param contentType symbolic content type to use for the request
   * @return the text resource
   * @throws IOException if an error occurs during fetching, or if the resource
   *           is not textual
   */
  public String getString(String url, Type contentType) throws IOException {
    HttpMethod method = null;
    try {
      method = get(url, contentType);
      return method.getResponseBodyAsString();
    } finally {
      if (method != null)
        method.releaseConnection();
    }
  }
}
