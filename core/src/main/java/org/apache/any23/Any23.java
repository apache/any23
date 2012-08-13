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

package org.apache.any23;

import org.apache.any23.configuration.Configuration;
import org.apache.any23.configuration.DefaultConfiguration;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.ExtractorGroup;
import org.apache.any23.extractor.ExtractorRegistry;
import org.apache.any23.extractor.ExtractorRegistryImpl;
import org.apache.any23.extractor.SingleDocumentExtraction;
import org.apache.any23.extractor.SingleDocumentExtractionReport;
import org.apache.any23.http.AcceptHeaderBuilder;
import org.apache.any23.http.DefaultHTTPClient;
import org.apache.any23.http.DefaultHTTPClientConfiguration;
import org.apache.any23.http.HTTPClient;
import org.apache.any23.mime.MIMEType;
import org.apache.any23.mime.MIMETypeDetector;
import org.apache.any23.mime.TikaMIMETypeDetector;
import org.apache.any23.mime.purifier.WhiteSpacesPurifier;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.source.FileDocumentSource;
import org.apache.any23.source.HTTPDocumentSource;
import org.apache.any23.source.LocalCopyFactory;
import org.apache.any23.source.MemCopyFactory;
import org.apache.any23.source.StringDocumentSource;
import org.apache.any23.writer.TripleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /**
     * Any23 core library version.
     * NOTE: there's also a version string in pom.xml, they should match.
     */
    public static final String VERSION = DefaultConfiguration.singleton().getPropertyOrFail("any23.core.version");

    /**
     * Default HTTP User Agent defined in default configuration.
     */
    public static final String DEFAULT_HTTP_CLIENT_USER_AGENT = DefaultConfiguration.singleton().getPropertyOrFail(
            "any23.http.user.agent.default"
    );

    protected static final Logger logger = LoggerFactory.getLogger(Any23.class);

    private final Configuration configuration;
    private final String        defaultUserAgent;

    private MIMETypeDetector mimeTypeDetector = new TikaMIMETypeDetector( new WhiteSpacesPurifier() );

    private HTTPClient httpClient = new DefaultHTTPClient();

    private boolean httpClientInitialized = false;

    private final ExtractorGroup factories;
    private LocalCopyFactory     streamCache;
    private String               userAgent;

    /**
     * Constructor that allows the specification of a
     * custom configuration and of a list of extractors.
     *
     * @param configuration configuration used to build the <i>Any23</i> instance.
     * @param extractorGroup the group of extractors to be applied.
     */
    public Any23(Configuration configuration, ExtractorGroup extractorGroup) {
        if(configuration == null) throw new NullPointerException("configuration must be not null.");
        this.configuration = configuration;
        logger.info( configuration.getConfigurationDump() );

        this.defaultUserAgent = configuration.getPropertyOrFail("any23.http.user.agent.default");

        this.factories = (extractorGroup == null)
                ? ExtractorRegistryImpl.getInstance().getExtractorGroup()
                : extractorGroup;
        setCacheFactory(new MemCopyFactory());
    }

    /**
     * Constructor that allows the specification of a list of extractors.
     *
     * @param extractorGroup the group of extractors to be applied.
     */
    public Any23(ExtractorGroup extractorGroup) {
        this(DefaultConfiguration.singleton(), extractorGroup);
    }

    /**
     * Constructor that allows the specification of a
     * custom configuration and of list of extractor names.
     *
     * @param extractorNames list of extractor's names.
     */
    public Any23(Configuration configuration, String... extractorNames) {
        this(
                configuration,
                extractorNames == null
                        ?
                null
                        :
                ExtractorRegistryImpl.getInstance().getExtractorGroup( Arrays.asList(extractorNames))
        );
    }

    /**
     * Constructor that allows the specification of a list of extractor names.
     *
     * @param extractorNames list of extractor's names.
     */
    public Any23(String... extractorNames) {
        this( DefaultConfiguration.singleton(), extractorNames );
    }

    /**
     * Constructor accepting {@link Configuration}.
     */
    public Any23(Configuration configuration) {
        this(configuration, (String[]) null);
    }

    /**
     * Constructor with default configuration.
     */
    public Any23() {
        this( DefaultConfiguration.singleton() );
    }

    /**
     * Sets the <i>HTTP Header User Agent</i>,
     * see <i>RFC 2616-14.43</i>.
     *
     * @param userAgent text describing the user agent.
     */
    public void setHTTPUserAgent(String userAgent) {
        if (httpClientInitialized) {
            throw new IllegalStateException("Cannot change HTTP configuration after client has been initialized");
        }
        if(userAgent == null) {
            userAgent = defaultUserAgent;
        }
        if(userAgent.trim().length() == 0) {
            throw new IllegalArgumentException( String.format("Invalid user agent: '%s'", userAgent) );
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
     * Allows to set the {@link org.apache.any23.http.HTTPClient} implementation
     * used to retrieve contents. The default instance is {@link org.apache.any23.http.DefaultHTTPClient}.
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
     * Returns the current {@link org.apache.any23.http.HTTPClient} implementation.
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
            httpClient.init( new DefaultHTTPClientConfiguration(this.getAcceptHeader()) );
            httpClientInitialized = true;
        }
        return httpClient;
    }

    /**
     * Allows to set a {@link org.apache.any23.source.LocalCopyFactory} instance.
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
     * Allows to set an instance of {@link org.apache.any23.mime.MIMETypeDetector}.
     *
     * @param detector a valid detector instance, if <code>null</code> all the detectors
     *        will be used.
     */
    public void setMIMETypeDetector(MIMETypeDetector detector) {
        this.mimeTypeDetector = detector;
    }

    /**
     * Returns the most appropriate {@link DocumentSource} for the given<code>documentURI</code>.
     *
     * @param documentURI the document <i>URI</i>.
     * @return a new instance of DocumentSource.
     * @throws URISyntaxException if an error occurs while parsing the <code>documentURI</code> as a <i>URI</i>.
     * @throws IOException if an error occurs while initializing the internal {@link org.apache.any23.http.HTTPClient}.
     */
    public DocumentSource createDocumentSource(String documentURI) throws URISyntaxException, IOException {
        if(documentURI == null) throw new NullPointerException("documentURI cannot be null.");
        if (documentURI.toLowerCase().startsWith("file:")) {
            return new FileDocumentSource( new File(new URI(documentURI)) );
        }
        if (documentURI.toLowerCase().startsWith("http:") || documentURI.toLowerCase().startsWith("https:")) {
            return new HTTPDocumentSource(getHTTPClient(), documentURI);
        }
        throw new IllegalArgumentException(
                String.format("Unsupported protocol for document URI: '%s' .", documentURI)
        );
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
     * @throws org.apache.any23.extractor.ExtractionException
     */
    public ExtractionReport extract(
            ExtractionParameters eps,
            DocumentSource in,
            TripleHandler outputHandler,
            String encoding
    ) throws IOException, ExtractionException {
        final SingleDocumentExtraction ex = new SingleDocumentExtraction(configuration, in, factories, outputHandler);
        ex.setMIMETypeDetector(mimeTypeDetector);
        ex.setLocalCopyFactory(streamCache);
        ex.setParserEncoding(encoding);
        final SingleDocumentExtractionReport sder = ex.run(eps);
        return new ExtractionReport(
                ex.getMatchingExtractors(),
                ex.getParserEncoding(),
                ex.getDetectedMIMEType(),
                sder.getValidationReport(),
                sder.getExtractorToIssues()
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
            return extract(eps, createDocumentSource(documentURI), outputHandler);
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
