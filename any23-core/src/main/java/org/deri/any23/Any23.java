/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionParameters;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.ExtractorGroup;
import org.deri.any23.extractor.ExtractorRegistry;
import org.deri.any23.extractor.SingleDocumentExtraction;
import org.deri.any23.extractor.SingleDocumentExtractionReport;
import org.deri.any23.http.AcceptHeaderBuilder;
import org.deri.any23.http.DefaultHTTPClient;
import org.deri.any23.http.HTTPClient;
import org.deri.any23.mime.MIMEType;
import org.deri.any23.mime.MIMETypeDetector;
import org.deri.any23.mime.TikaMIMETypeDetector;
import org.deri.any23.source.DocumentSource;
import org.deri.any23.source.FileDocumentSource;
import org.deri.any23.source.HTTPDocumentSource;
import org.deri.any23.source.LocalCopyFactory;
import org.deri.any23.source.MemCopyFactory;
import org.deri.any23.source.StringDocumentSource;
import org.deri.any23.writer.TripleHandler;
import org.deri.any23.writer.TripleHandlerException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


/**
 * A facade with convenience methods for typical <i>Any23</i> extraction
 * operations.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class Any23 {

    // NOTE: there's also a version string in pom.xml, they should match.
    public static final String VERSION = "0.5.0-SNAPSHOT";

    private final ExtractorGroup factories;
    private LocalCopyFactory streamCache;
    private MIMETypeDetector mimeTypeDetector = new TikaMIMETypeDetector(); // Can be overridden by setter.
    private String userAgent = null;
    private HTTPClient httpClient = new DefaultHTTPClient();
    private boolean httpClientInitialized = false;

    /**
     * Constructor.
     */
    public Any23() {
        this((String[]) null);
    }

    /**
     * Constructor that allows the specification of a list of extractors.
     *
     * @param extractorNames list of extractor's names.
     */
    public Any23(String... extractorNames) {
        factories = (extractorNames == null)
                ? ExtractorRegistry.getInstance().getExtractorGroup()
                : ExtractorRegistry.getInstance().getExtractorGroup(Arrays.asList(extractorNames));
        setCacheFactory(new MemCopyFactory());
    }

    /**
     * Sets the <i>HTTP Header User Agent</i>,
     * see <i>RFC 2616-14.43</i>.
     *
     * @param userAgent text describing the user agent.
     */
    public void setHTTPUserAgent(String userAgent) {
        if(userAgent == null || userAgent.trim().length() == 0) {
            throw new IllegalArgumentException( String.format("Invalid user agent: '%s'", userAgent) );
        }
        if (httpClientInitialized) {
            throw new IllegalStateException("Cannot change HTTP configuration after client has been initialized");
        }
        this.userAgent = userAgent;
    }

    /**
     * Returns the <i>HTTP Header User Agent</i>,
     * see <i>RFC 2616-14.43</i>.
     *
     * @return text describing the user agent.
     */
    public String getHTTPUserAgent() {
        return this.userAgent;
    }

    /**
     * Allows to set the {@link org.deri.any23.http.HTTPClient} implementation
     * used to retrieve contents. The default instance is {@link org.deri.any23.http.DefaultHTTPClient}.
     *
     * @param httpClient a valid client instance.
     * @throws IllegalStateException if invoked after client has been initialized.
     */
    public void setHTTPClient(HTTPClient httpClient) {
        if(httpClient == null) {
            throw new NullPointerException("httpClient cannot be null.");
        }
        if (httpClientInitialized) {
            throw new IllegalStateException("Cannot change HTTP configuration after client has been initialized");
        }
        this.httpClient = httpClient;
    }

    /**
     * Returns the current {@link org.deri.any23.http.HTTPClient} implementation.
     *
     * @return instance of HTTPClient.
     * @throws IOException if the HTTP client has not initialized.
     */
    public HTTPClient getHTTPClient() throws IOException {
        if (!httpClientInitialized) {
            if (userAgent == null) {
                throw new IOException("Must call " + Any23.class.getSimpleName() +
                        ".setHTTPUserAgent(String) before extracting from HTTP URI");
            }
            httpClient.init(userAgent, getAcceptHeader());
            httpClientInitialized = true;
        }
        return httpClient;
    }

    /**
     * Allows to set a {@link org.deri.any23.source.LocalCopyFactory} instance.
     *
     * @param cache valid cache instance.
     */
    public void setCacheFactory(LocalCopyFactory cache) {
        if(cache == null) {
            throw new NullPointerException("cache cannot be null.");
        }
        this.streamCache = cache;
    }

    /**
     * Allows to set an instance of {@link org.deri.any23.mime.MIMETypeDetector}.
     *
     * @param detector a valid detector instance, if <code>null</code> all the detectors
     *        will be used.
     */
    public void setMIMETypeDetector(MIMETypeDetector detector) {
        this.mimeTypeDetector = detector;
    }

    /**
     * Performs metadata extraction from the content of the given
     * <code>in</code> document source, sending the generated events
     * to the specified <code>outputHandler</code>.
     *
     * @param eps the extraction parameters to be applied.
     * @param in the input document source.
     * @param outputHandler handler responsible for collecting of the extracted metadata.
     * @param encoding explicit encoding see
     *        <a href="http://www.iana.org/assignments/character-sets">available encodings</a>.
     * @return <code>true</code> if some extraction occurred, <code>false</code> otherwise.
     * @throws IOException
     * @throws ExtractionException
     */
    public ExtractionReport extract(
            ExtractionParameters eps,
            DocumentSource in,
            TripleHandler outputHandler,
            String encoding
    ) throws IOException, ExtractionException {
        final SingleDocumentExtraction ex = new SingleDocumentExtraction(in, factories, outputHandler);
        ex.setMIMETypeDetector(mimeTypeDetector);
        ex.setLocalCopyFactory(streamCache);
        ex.setParserEncoding(encoding);
        final SingleDocumentExtractionReport sder = ex.run(eps);
        try {
            outputHandler.close();
        } catch (TripleHandlerException e) {
            throw new ExtractionException("Error closing the triple handler", e);
        }
        return new ExtractionReport(
                ex.hasMatchingExtractors(),
                ex.getParserEncoding(),
                ex.getDetectedMIMEType(),
                sder.getValidationReport()
        );
    }

    /**
     * Performs metadata extraction on the <code>in</code> string
     * associated to the <code>documentURI</code> URI, declaring
     * <code>contentType</code> and <code>encoding</code>.
     * The generated events are sent to the specified <code>outputHandler</code>.
     *
     * @param in raw data to be analyzed.
     * @param documentURI URI from which the raw data has been extracted.
     * @param contentType declared data content type.
     * @param encoding declared data encoding.
     * @param outputHandler handler responsible for collecting of the extracted metadata.
     * @return <code>true</code> if some extraction occurred, <code>false</code> otherwise.
     * @throws IOException
     * @throws ExtractionException
     */
    public ExtractionReport extract(
            String in,
            String documentURI,
            String contentType,
            String encoding,
            TripleHandler outputHandler
    ) throws IOException, ExtractionException {
        return extract(new StringDocumentSource(in, documentURI, contentType, encoding), outputHandler);
    }

    /**
     * Performs metadata extraction on the <code>in</code> string
     * associated to the <code>documentURI</code> URI, sending the generated
     * events to the specified <code>outputHandler</code>.
     *
     * @param in raw data to be analyzed.
     * @param documentURI URI from which the raw data has been extracted.
     * @param outputHandler handler responsible for collecting of the extracted metadata.
     * @return <code>true</code> if some extraction occurred, <code>false</code> otherwise.
     * @throws IOException
     * @throws ExtractionException
     */
    public ExtractionReport extract(String in, String documentURI, TripleHandler outputHandler)
    throws IOException, ExtractionException {
        return extract(new StringDocumentSource(in, documentURI), outputHandler);
    }

    /**
     * Performs metadata extraction from the content of the given <code>file</code>
     * sending the generated events to the specified <code>outputHandler</code>.
     *
     * @param file file containing raw data.
     * @param outputHandler handler responsible for collecting of the extracted metadata.
     * @return <code>true</code> if some extraction occurred, <code>false</code> otherwise.
     * @throws IOException
     * @throws ExtractionException
     */
    public ExtractionReport extract(File file, TripleHandler outputHandler)
    throws IOException, ExtractionException {
        return extract(new FileDocumentSource(file), outputHandler);
    }

    /**
     * Performs metadata extraction from the content of the given <code>documentURI</code>
     * sending the generated events to the specified <code>outputHandler</code>.
     * If the <i>URI</i> is replied with a redirect, the last will be followed.
     *
     * @param eps the parameters to be applied to the extraction.
     * @param documentURI the URI from which retrieve document.
     * @param outputHandler handler responsible for collecting of the extracted metadata.
     * @return <code>true</code> if some extraction occurred, <code>false</code> otherwise.
     * @throws IOException
     * @throws ExtractionException
     */
    public ExtractionReport extract(ExtractionParameters eps, String documentURI, TripleHandler outputHandler)
    throws IOException, ExtractionException {
        try {
            if (documentURI.toLowerCase().startsWith("file:")) {
                return extract(eps, new FileDocumentSource(new File(new URI(documentURI))), outputHandler);
            }
            if (documentURI.toLowerCase().startsWith("http:") || documentURI.toLowerCase().startsWith("https:")) {
                return extract(eps, new HTTPDocumentSource(getHTTPClient(), documentURI), outputHandler);
            }
            throw new ExtractionException("Not a valid absolute URI: " + documentURI);
        } catch (URISyntaxException ex) {
            throw new ExtractionException("Error while extracting data from document URI.", ex);
        }
    }

    /**
     * Performs metadata extraction from the content of the given <code>documentURI</code>
     * sending the generated events to the specified <code>outputHandler</code>.
     * If the <i>URI</i> is replied with a redirect, the last will be followed.
     *
     * @param documentURI the URI from which retrieve document.
     * @param outputHandler handler responsible for collecting of the extracted metadata.
     * @return <code>true</code> if some extraction occurred, <code>false</code> otherwise.
     * @throws IOException
     * @throws ExtractionException
     */
    public ExtractionReport extract(String documentURI, TripleHandler outputHandler)
    throws IOException, ExtractionException {
        return extract((ExtractionParameters) null, documentURI, outputHandler);
    }

    /**
     * Performs metadata extraction from the content of the given
     * <code>in</code> document source, sending the generated events
     * to the specified <code>outputHandler</code>.
     *
     * @param in the input document source.
     * @param outputHandler handler responsible for collecting of the extracted metadata.
     * @param encoding explicit encoding see
     *        <a href="http://www.iana.org/assignments/character-sets">available encodings</a>.
     * @return <code>true</code> if some extraction occurred, <code>false</code> otherwise.
     * @throws IOException
     * @throws ExtractionException
     */
    public ExtractionReport extract(DocumentSource in, TripleHandler outputHandler, String encoding)
    throws IOException, ExtractionException {
        return extract(null, in, outputHandler, encoding);
    }

    /**
     * Performs metadata extraction from the content of the given
     * <code>in</code> document source, sending the generated events
     * to the specified <code>outputHandler</code>.
     *
     * @param in the input document source.
     * @param outputHandler handler responsible for collecting of the extracted metadata.
     * @return <code>true</code> if some extraction occurred, <code>false</code> otherwise.
     * @throws IOException
     * @throws ExtractionException
     */
    public ExtractionReport extract(DocumentSource in, TripleHandler outputHandler)
    throws IOException, ExtractionException {
        return extract(null, in, outputHandler, null);
    }

    /**
     * Performs metadata extraction from the content of the given
     * <code>in</code> document source, sending the generated events
     * to the specified <code>outputHandler</code>.
     *
     * @param eps the parameters to be applied for the extraction phase.
     * @param in the input document source.
     * @param outputHandler handler responsible for collecting of the extracted metadata.
     * @return <code>true</code> if some extraction occurred, <code>false</code> otherwise.
     * @throws IOException
     * @throws ExtractionException
     */
    public ExtractionReport extract(ExtractionParameters eps, DocumentSource in, TripleHandler outputHandler)
    throws IOException, ExtractionException {
        return extract(eps, in, outputHandler, null);
    }

    private String getAcceptHeader() {
        Collection<MIMEType> mimeTypes = new ArrayList<MIMEType>();
        for (ExtractorFactory<?> factory : factories) {
            mimeTypes.addAll(factory.getSupportedMIMETypes());
        }
        return new AcceptHeaderBuilder(mimeTypes).getAcceptHeader();
    }
    
}
