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

package org.apache.any23.extractor;

import org.apache.any23.configuration.Configuration;
import org.apache.any23.configuration.DefaultConfiguration;
import org.apache.any23.encoding.EncodingDetector;
import org.apache.any23.encoding.TikaEncodingDetector;
import org.apache.any23.extractor.html.DocumentReport;
import org.apache.any23.extractor.html.HTMLDocument;
import org.apache.any23.extractor.html.MicroformatExtractor;
import org.apache.any23.extractor.html.TagSoupParser;
import org.apache.any23.mime.MIMEType;
import org.apache.any23.mime.MIMETypeDetector;
import org.apache.any23.rdf.Any23ValueFactoryWrapper;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.source.LocalCopyFactory;
import org.apache.any23.source.MemCopyFactory;
import org.apache.any23.validator.EmptyValidationReport;
import org.apache.any23.validator.ValidatorException;
import org.apache.any23.vocab.SINDICE;
import org.apache.any23.writer.CompositeTripleHandler;
import org.apache.any23.writer.CountingTripleHandler;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.apache.any23.extractor.Extractor.BlindExtractor;
import org.apache.any23.extractor.Extractor.ContentExtractor;
import org.apache.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.any23.extractor.TagSoupExtractionResult.PropertyPath;
import static org.apache.any23.extractor.TagSoupExtractionResult.ResourceRoot;

/**
 * This class acts as facade where all the extractors were called on a single document.
 */
public class SingleDocumentExtraction {

    private static final SINDICE vSINDICE = SINDICE.getInstance();

    private final static Logger log = LoggerFactory.getLogger(SingleDocumentExtraction.class);

    private final Configuration configuration;

    private final DocumentSource in;

    private URI documentURI;
    
    private final ExtractorGroup extractors;

    private final TripleHandler output;

    private final EncodingDetector encoderDetector;

    private LocalCopyFactory copyFactory = null;

    private DocumentSource localDocumentSource = null;

    private MIMETypeDetector detector = null;

    private ExtractorGroup matchingExtractors = null;

    private MIMEType detectedMIMEType = null;

    private DocumentReport documentReport = null;

    private ExtractionParameters tagSoupDOMRelatedParameters = null;

    private String parserEncoding = null;

    /**
     * Builds an extractor by the specification of document source,
     * list of extractors and output triple handler.
     *
     * @param configuration configuration applied during extraction.
     * @param in input document source.
     * @param extractors list of extractors to be applied.
     * @param output output triple handler.
     */
    public SingleDocumentExtraction(
            Configuration configuration, DocumentSource in, ExtractorGroup extractors, TripleHandler output
    ) {
        if(configuration == null) throw new NullPointerException("configuration cannot be null.");
        if(in == null)            throw new NullPointerException("in cannot be null.");
        this.configuration = configuration;
        this.in = in;
        this.extractors = extractors;

        List<TripleHandler> tripleHandlers = new ArrayList<TripleHandler>();
        tripleHandlers.add(output);
        tripleHandlers.add(new CountingTripleHandler());
        this.output = new CompositeTripleHandler(tripleHandlers);
        this.encoderDetector = new TikaEncodingDetector();
    }

    /**
     * Builds an extractor by the specification of document source,
     * extractors factory and output triple handler.
     *
     * @param configuration configuration applied during extraction.
     * @param in input document source.
     * @param factory the extractors factory.
     * @param output output triple handler.
     */
    public SingleDocumentExtraction(
            Configuration configuration, DocumentSource in, ExtractorFactory<?> factory, TripleHandler output
    ) {
        this(
                configuration,
                in,
                new ExtractorGroup(Collections.<ExtractorFactory<?>>singletonList(factory)),
                output
        );
        this.setMIMETypeDetector(null);
    }

    /**
     * Builds an extractor by the specification of document source,
     * extractors factory and output triple handler, using the
     * {@link org.apache.any23.configuration.DefaultConfiguration}.
     *
     * @param in input document source.
     * @param factory the extractors factory.
     * @param output output triple handler.
     */
    public SingleDocumentExtraction(
        DocumentSource in, ExtractorFactory<?> factory, TripleHandler output
    ) {
        this(
                DefaultConfiguration.singleton(),
                in,
                new ExtractorGroup(Collections.<ExtractorFactory<?>>singletonList(factory)),
                output
        );
        this.setMIMETypeDetector(null);
    }

    /**
     * Sets the internal factory for generating the document local copy,
     * if <code>null</code> the {@link org.apache.any23.source.MemCopyFactory} will be used.
     *
     * @param copyFactory local copy factory.
     * @see org.apache.any23.source.DocumentSource
     */
    public void setLocalCopyFactory(LocalCopyFactory copyFactory) {
        this.copyFactory = copyFactory;
    }

    /**
     * Sets the internal mime type detector,
     * if <code>null</code> mimetype detection will
     * be skipped and all extractors will be activated.
     *
     * @param detector detector instance.
     */
    public void setMIMETypeDetector(MIMETypeDetector detector) {
        this.detector = detector;
    }

    /**
     * Triggers the execution of all the {@link Extractor}
     * registered to this class using the specified extraction parameters.
     *
     * @param extractionParameters the parameters applied to the run execution.
     * @return the report generated by the extraction.
     * @throws ExtractionException if an error occurred during the data extraction.
     * @throws IOException if an error occurred during the data access.
     */
    public SingleDocumentExtractionReport run(ExtractionParameters extractionParameters)
    throws ExtractionException, IOException {
        if(extractionParameters == null) {
            extractionParameters = ExtractionParameters.newDefault(configuration);
        }

        final String contextURI = extractionParameters.getProperty(ExtractionParameters.EXTRACTION_CONTEXT_URI_PROPERTY);
        ensureHasLocalCopy();
        try {
            this.documentURI = new Any23ValueFactoryWrapper(
                    ValueFactoryImpl.getInstance()
            ).createURI( "?".equals(contextURI) ? in.getDocumentURI() : contextURI);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid URI: " + in.getDocumentURI(), ex);
        }
        if(log.isInfoEnabled()) {
            log.info("Processing " + this.documentURI);
        }
        filterExtractorsByMIMEType();

        if(log.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer("Extractors ");
            for (ExtractorFactory<?> factory : matchingExtractors) {
                sb.append(factory.getExtractorName());
                sb.append(' ');
            }
            sb.append("match ").append(documentURI);
            log.debug(sb.toString());
        }

        // Invoke all extractors.
        try {
            output.startDocument(documentURI);
        } catch (TripleHandlerException e) {
            log.error(String.format("Error starting document with URI %s", documentURI));
            throw new ExtractionException(String.format("Error starting document with URI %s", documentURI),
                    e
            );
        }
        output.setContentLength(in.getContentLength());
        // Create the document context.
        final List<ResourceRoot> resourceRoots = new ArrayList<ResourceRoot>();
        final List<PropertyPath> propertyPaths = new ArrayList<PropertyPath>();
        final Map<String,Collection<IssueReport.Issue>> extractorToIssues =
            new HashMap<String,Collection<IssueReport.Issue>>();
        try {
            final String documentLanguage = extractDocumentLanguage(extractionParameters);
            for (ExtractorFactory<?> factory : matchingExtractors) {
                final Extractor extractor = factory.createExtractor();
                final SingleExtractionReport er = runExtractor(
                        extractionParameters,
                        documentLanguage,
                        extractor
                );
                resourceRoots.addAll( er.resourceRoots );
                propertyPaths.addAll( er.propertyPaths );
                extractorToIssues.put(factory.getExtractorName(), er.issues);
            }
        } catch(ValidatorException ve) {
            throw new ExtractionException("An error occurred during the validation phase.", ve);
        }

        // Resource consolidation.
        final boolean addDomainTriples = extractionParameters.getFlag(ExtractionParameters.METADATA_DOMAIN_PER_ENTITY_FLAG);
        final ExtractionContext consolidationContext;
        if(extractionParameters.getFlag(ExtractionParameters.METADATA_NESTING_FLAG)) {
            // Consolidation with nesting.
            consolidationContext = consolidateResources(resourceRoots, propertyPaths, addDomainTriples, output);
        } else {
            consolidationContext = consolidateResources(resourceRoots, addDomainTriples, output);
        }

        // Adding time/size meta triples.
        if (extractionParameters.getFlag(ExtractionParameters.METADATA_TIMESIZE_FLAG)) {
            try {
                addExtractionTimeSizeMetaTriples(consolidationContext);
            } catch (TripleHandlerException e) {
                throw new ExtractionException(
                        String.format(
                                "Error while adding extraction metadata triples document with URI %s", documentURI
                        ),
                        e
                );
            }
        }

        try {
            output.endDocument(documentURI);
        } catch (TripleHandlerException e) {
            log.error(String.format("Error ending document with URI %s", documentURI));
            throw new ExtractionException(String.format("Error ending document with URI %s", documentURI),
                    e
            );
        }

        return new SingleDocumentExtractionReport(
                documentReport == null
                        ?
                EmptyValidationReport.getInstance() : documentReport.getReport(),
                extractorToIssues
        );
    }

    /**
     * Triggers the execution of all the {@link Extractor}
     * registered to this class using the <i>default</i> extraction parameters.
     *
     * @throws IOException
     * @throws ExtractionException
     * @return the extraction report.
     */
    public SingleDocumentExtractionReport run() throws IOException, ExtractionException {
        return run(ExtractionParameters.newDefault(configuration));
    }

    /**
     * Returns the detected mimetype for the given {@link org.apache.any23.source.DocumentSource}.
     *
     * @return string containing the detected mimetype.
     * @throws IOException if an error occurred while accessing the data.
     */
    public String getDetectedMIMEType() throws IOException {
        filterExtractorsByMIMEType();
        return  detectedMIMEType == null ? null : detectedMIMEType.toString();
    }

    /**
     * Check whether the given {@link org.apache.any23.source.DocumentSource} content activates of not at least an extractor.
     *
     * @return <code>true</code> if at least an extractor is activated, <code>false</code> otherwise.
     * @throws IOException
     */
    public boolean hasMatchingExtractors() throws IOException {
        filterExtractorsByMIMEType();
        return !matchingExtractors.isEmpty();
    }

    /**
     * @return the list of all the activated extractors for the given {@link org.apache.any23.source.DocumentSource}.
     */
    public List<Extractor> getMatchingExtractors() {
        final List<Extractor> extractorsList = new ArrayList<Extractor>();
        for(ExtractorFactory extractorFactory : matchingExtractors) {
            extractorsList.add( extractorFactory.createExtractor() );
        }
        return extractorsList;
    }

    /**
     * @return the configured parsing encoding.
     */
    public String getParserEncoding() {
        if(this.parserEncoding == null) {
            this.parserEncoding = detectEncoding();
        }
        return this.parserEncoding;
    }

    /**
     * Sets the document parser encoding.
     *
     * @param encoding parser encoding.
     */
    public void setParserEncoding(String encoding) {
        this.parserEncoding = encoding;
        documentReport = null;
    }

    /**
     * Chech whether the given {@link org.apache.any23.source.DocumentSource} is an <b>HTML</b> document.
     *
     * @return <code>true</code> if the document source is an HTML document.
     * @throws IOException if an error occurs while accessing data.
     */
    private boolean isHTMLDocument() throws IOException {
        filterExtractorsByMIMEType();
        return ! matchingExtractors.filterByMIMEType( MIMEType.parse("text/html") ).isEmpty();
    }

    /**
     * Extracts the document language where possible.
     *
     * @param extractionParameters extraction parameters to be applied to determine the document language.
     * @return the document language if any, <code>null</code> otherwise.
     * @throws java.io.IOException if an error occurs during the document analysis.
     * @throws org.apache.any23.validator.ValidatorException
     */
    private String extractDocumentLanguage(ExtractionParameters extractionParameters)
    throws IOException, ValidatorException {
        if( ! isHTMLDocument() ) {
            return null;
        }
        final HTMLDocument document;
        try {
            document = new HTMLDocument( getTagSoupDOM(extractionParameters).getDocument() );
        } catch (IOException ioe) {
            log.debug("Cannot extract language from document.", ioe);
            return null;
        }
        return document.getDefaultLanguage();
    }

    /**
     * Generates a list of extractors that can be applied to the given document.
     *
     * @throws IOException
     */
    private void filterExtractorsByMIMEType()
    throws IOException {
        if (matchingExtractors != null) return;  // has already been run.

        if (detector == null || extractors.allExtractorsSupportAllContentTypes()) {
            matchingExtractors = extractors;
            return;
        }
        ensureHasLocalCopy();
        detectedMIMEType = detector.guessMIMEType(
                java.net.URI.create(documentURI.stringValue()).getPath(),
                localDocumentSource.openInputStream(),
                MIMEType.parse(localDocumentSource.getContentType())
        );
        log.debug("detected media type: " + detectedMIMEType);
        matchingExtractors = extractors.filterByMIMEType(detectedMIMEType);
    }

    /**
     * Triggers the execution of a specific {@link Extractor}.
     * 
     * @param extractionParameters the parameters used for the extraction.
     * @param extractor the {@link Extractor} to be executed.
     * @throws ExtractionException if an error specific to an extractor happens.
     * @throws IOException if an IO error occurs during the extraction.
     * @return the roots of the resources that have been extracted.
     * @throws org.apache.any23.validator.ValidatorException if an error occurs during validation.
     */
    private SingleExtractionReport runExtractor(
            final ExtractionParameters extractionParameters,
            final String documentLanguage,
            final Extractor<?> extractor
    ) throws ExtractionException, IOException, ValidatorException {
        if(log.isDebugEnabled()) {
            log.debug("Running " + extractor.getDescription().getExtractorName() + " on " + documentURI);
        }
        long startTime = System.currentTimeMillis();
        final ExtractionContext extractionContext = new ExtractionContext(
                extractor.getDescription().getExtractorName(),
                documentURI,
                documentLanguage
        );
        final ExtractionResultImpl extractionResult = new ExtractionResultImpl(extractionContext, extractor, output);
        try {
            if (extractor instanceof BlindExtractor) {
                final BlindExtractor blindExtractor = (BlindExtractor) extractor;
                blindExtractor.run(extractionParameters, extractionContext, documentURI, extractionResult);
            } else if (extractor instanceof ContentExtractor) {
                ensureHasLocalCopy();
                final ContentExtractor contentExtractor = (ContentExtractor) extractor;
                contentExtractor.run(
                        extractionParameters,
                        extractionContext,
                        localDocumentSource.openInputStream(),
                        extractionResult
                );
            } else if (extractor instanceof TagSoupDOMExtractor) {
                final TagSoupDOMExtractor tagSoupDOMExtractor = (TagSoupDOMExtractor) extractor;
                final DocumentReport documentReport = getTagSoupDOM(extractionParameters);
                tagSoupDOMExtractor.run(
                        extractionParameters,
                        extractionContext,
                        documentReport.getDocument(),
                        extractionResult
                );
            } else {
                throw new IllegalStateException("Extractor type not supported: " + extractor.getClass());
            }
            return
                new SingleExtractionReport(
                    extractionResult.getIssues(),
                    new ArrayList<ResourceRoot>( extractionResult.getResourceRoots() ),
                    new ArrayList<PropertyPath>( extractionResult.getPropertyPaths() )
                );
        } catch (ExtractionException ex) {
            if(log.isDebugEnabled()) {
                log.debug(extractor.getDescription().getExtractorName() + ": " + ex.getMessage());
            }
            throw ex;
        } finally {
            // Logging result error report.
            if(log.isDebugEnabled() && extractionResult.hasIssues() ) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                extractionResult.printReport(new PrintStream(baos));
                log.debug(baos.toString());
            }
            extractionResult.close();

            long elapsed = System.currentTimeMillis() - startTime;
            if(log.isDebugEnabled()) {
                log.debug("Completed " + extractor.getDescription().getExtractorName() + ", " + elapsed + "ms");
            }
        }
    }

    /**
     * Forces the retrieval of the document data.
     *
     * @throws IOException
     */
    private void ensureHasLocalCopy() throws IOException {
        if (localDocumentSource != null) return;
        if (in.isLocal()) {
            localDocumentSource = in;
            return;
        }
        if (copyFactory == null) {
            copyFactory = new MemCopyFactory();
        }
        localDocumentSource = copyFactory.createLocalCopy(in);
    }

    /**
     * Returns the DOM of the given document source (that must be an HTML stream)
     * and the report of eventual fixes applied on it.
     *
     * @param extractionParameters parameters to be used during extraction.
     * @return document report.
     * @throws IOException if an error occurs during data access.
     * @throws ValidatorException if an error occurs during validation.
     */
    private DocumentReport getTagSoupDOM(ExtractionParameters extractionParameters)
    throws IOException, ValidatorException {
        if (documentReport == null || !extractionParameters.equals(tagSoupDOMRelatedParameters)) {
            ensureHasLocalCopy();
            final InputStream is = new BufferedInputStream( localDocumentSource.openInputStream() );
            is.mark(Integer.MAX_VALUE);
            final String candidateEncoding = getParserEncoding();
            is.reset();
            final TagSoupParser tagSoupParser = new TagSoupParser(
                    is,
                    documentURI.stringValue(),
                    candidateEncoding
            );
            if(extractionParameters.isValidate()) {
                documentReport = tagSoupParser.getValidatedDOM( extractionParameters.isFix() );
            } else {
                documentReport = new DocumentReport( EmptyValidationReport.getInstance(), tagSoupParser.getDOM() );
            }
            tagSoupDOMRelatedParameters = extractionParameters;
        }
        return documentReport;
    }

    /**
     * Detects the encoding of the local document source input stream.
     * 
     * @return a valid encoding value.
     */
    private String detectEncoding() {
        try {
            ensureHasLocalCopy();
            InputStream is = new BufferedInputStream(localDocumentSource.openInputStream());
            String encoding = this.encoderDetector.guessEncoding(is);
            is.close();
            return encoding;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while trying to detect the input encoding.", e);
        }
    }

    /**
     * This function verifies if the <i>candidateSub</i> list of strings
     * is a prefix of <i>list</i>.
     *
     * @param list a list of strings.
     * @param candidateSub a list of strings.
     * @return <code>true</code> if <i>candidateSub</i> is a sub path of <i>list</i>,
     *         <code>false</code> otherwise.
     */
    private boolean subPath(String[] list, String[] candidateSub) {
        if(candidateSub.length > list.length) {
            return false;
        }
        for(int i = 0; i < candidateSub.length; i++) {
            if( ! candidateSub[i].equals(list[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds for every resource root node a page domain triple.
     *
     * @param resourceRoots list of resource roots.
     * @param context extraction context to produce triples.
     * @throws ExtractionException
     */
    private void addDomainTriplesPerResourceRoots(List<ResourceRoot> resourceRoots, ExtractionContext context)
    throws ExtractionException {
        try {
            // Add source Web domains to every resource root.
            String domain;
            try {
                domain = new java.net.URI(in.getDocumentURI()).getHost();
            } catch (URISyntaxException urise) {
                throw new IllegalArgumentException(
                        "An error occurred while extracting the host from the document URI.",
                        urise
                );
            }
            if (domain != null) {
                for (ResourceRoot resourceRoot : resourceRoots) {
                    output.receiveTriple(
                            resourceRoot.getRoot(),
                            vSINDICE.getProperty(SINDICE.DOMAIN),
                            ValueFactoryImpl.getInstance().createLiteral(domain),
                            null,
                            context
                    );
                }
            }
        } catch (TripleHandlerException e) {
            throw new ExtractionException("Error while writing triple triple.", e);
        } finally {
            try {
                output.closeContext(context);
            } catch (TripleHandlerException e) {
                throw new ExtractionException("Error while closing context.", e);
            }
        }
    }

    /**
     * @return an extraction context specific for consolidation triples.
     */
    private ExtractionContext createExtractionContext() {
        return new ExtractionContext(
                "consolidation-extractor",
                documentURI,
                UUID.randomUUID().toString()
        );
    }

    /**
     * Detect the nesting relationship among different
     * Microformats and explicit them adding connection triples.
     *
     * @param resourceRoots
     * @param propertyPaths
     * @param context
     * @throws TripleHandlerException
     */
    private void addNestingRelationship(
            List<ResourceRoot> resourceRoots,
            List<PropertyPath> propertyPaths,
            ExtractionContext context
    ) throws TripleHandlerException {
        ResourceRoot currentResourceRoot;
        PropertyPath currentPropertyPath;
        for (int r = 0; r < resourceRoots.size(); r++) {
            currentResourceRoot = resourceRoots.get(r);
            for (int p = 0; p < propertyPaths.size(); p++) {
                currentPropertyPath = propertyPaths.get(p);
                Class<? extends MicroformatExtractor> currentResourceRootExtractor = currentResourceRoot.getExtractor();
                Class<? extends MicroformatExtractor> currentPropertyPathExtractor = currentPropertyPath.getExtractor();
                // Avoid wrong nesting relationships.
                if (currentResourceRootExtractor.equals(currentPropertyPathExtractor)) {
                    continue;
                }
                // Avoid self declaring relationships
                if(MicroformatExtractor.includes(currentPropertyPathExtractor, currentResourceRootExtractor)) {
                    continue;
                }
                if (subPath(currentResourceRoot.getPath(), currentPropertyPath.getPath())) {
                    createNestingRelationship(currentPropertyPath, currentResourceRoot, output, context);
                }
            }
        }
    }

    /**
     * This method consolidates the graphs extracted from the same document.
     * In particular it adds:
     * <ul>
     *   <li>for every microformat root node a triple indicating the original Web page domain;</li>
     *   <li>triples indicating the nesting relationship among a microformat root and property paths of
     *       other nested microformats.
     *   </li>
     * </ul>
     * @param resourceRoots list of RDF nodes representing roots of
     *        extracted microformat graphs and the corresponding HTML paths.
     * @param propertyPaths list of RDF nodes representing property subjects, property URIs and the HTML paths
     *        from which such properties have been extracted. 
     * @param addDomainTriples
     * @param output a triple handler event collector.
     * @return
     * @throws ExtractionException
     */
    private ExtractionContext consolidateResources(
            List<ResourceRoot> resourceRoots,
            List<PropertyPath> propertyPaths,
            boolean addDomainTriples,
            TripleHandler output
    ) throws ExtractionException {
        final ExtractionContext context = createExtractionContext();

        try {
            output.openContext(context);
        } catch (TripleHandlerException e) {
            throw new ExtractionException(
                    String.format("Error starting document with URI %s", documentURI),
                    e
            );
        }

        try {
            if(addDomainTriples) {
                addDomainTriplesPerResourceRoots(resourceRoots, context);
            }
            addNestingRelationship(resourceRoots, propertyPaths, context);
        } catch (TripleHandlerException the) {
            throw new ExtractionException("Error while writing triple triple.", the);
        } finally {
            try {
                output.closeContext(context);
            } catch (TripleHandlerException e) {
                throw new ExtractionException("Error while closing context.", e);
            }
        }

        return context;
    }

    /**
     * This method consolidates the graphs extracted from the same document.
     * In particular it adds:
     * <ul>
     *   <li>for every microformat root node a triple indicating the original Web page domain;</li>
     * </ul>
     * @param resourceRoots list of RDF nodes representing roots of
     *        extracted microformat graphs and the corresponding HTML paths.
     *        from which such properties have been extracted.
     * @param addDomainTriples
     * @param output a triple handler event collector.
     * @return
     * @throws ExtractionException
     */
    private ExtractionContext consolidateResources(
            List<ResourceRoot> resourceRoots,
            boolean addDomainTriples,
            TripleHandler output
    ) throws ExtractionException {
        final ExtractionContext context = createExtractionContext();

        try {
            output.openContext(context);
        } catch (TripleHandlerException e) {
            throw new ExtractionException(
                    String.format("Error starting document with URI %s", documentURI),
                    e
            );
        }

        try {
            if(addDomainTriples) {
                addDomainTriplesPerResourceRoots(resourceRoots, context);
            }
        } finally {
            try {
                output.closeContext(context);
            } catch (TripleHandlerException the) {
                throw new ExtractionException("Error while closing context.", the);
            }
        }

        return context;
    }

    /**
     * Adds metadata triples containing the number of extracted triples
     * and the extraction timestamp.
     *
     * @param context
     * @throws TripleHandlerException
     */
    private void addExtractionTimeSizeMetaTriples(ExtractionContext context)
    throws TripleHandlerException {
        // adding extraction date
        String xsdDateTimeNow = RDFUtils.toXSDDateTime(new Date());
        output.receiveTriple(
                new URIImpl(documentURI.toString()),
                vSINDICE.getProperty(SINDICE.DATE),
                ValueFactoryImpl.getInstance().createLiteral(xsdDateTimeNow),
                null,
                context
        );

        // adding number of extracted triples
        int numberOfTriples = 0;
        CompositeTripleHandler cth = (CompositeTripleHandler) output;
        for (TripleHandler th : cth.getChilds()) {
            if (th instanceof CountingTripleHandler) {
                numberOfTriples = ((CountingTripleHandler) th).getCount();
            }
        }
        output.receiveTriple(
                new URIImpl(documentURI.toString()),
                vSINDICE.getProperty(SINDICE.SIZE),
                ValueFactoryImpl.getInstance().createLiteral(numberOfTriples + 1), // the number of triples plus itself
                null,
                context
        );
    }

    /**
     * Creates a nesting relationship triple.
     * 
     * @param from the property containing the nested microformat.
     * @param to the root to the nested microformat.
     * @param th the triple handler.
     * @param ec the extraction context used to add such information.
     * @throws org.apache.any23.writer.TripleHandlerException
     */
    private void createNestingRelationship(
            PropertyPath from,
            ResourceRoot to,
            TripleHandler th,
            ExtractionContext ec
    ) throws TripleHandlerException {
        final BNode fromObject = from.getObject();
        final String bNodeHash = from.getProperty().stringValue() + ( fromObject == null ? "" : fromObject.getID() );
        BNode bnode = RDFUtils.getBNode(bNodeHash);
        th.receiveTriple(bnode, vSINDICE.getProperty(SINDICE.NESTING_ORIGINAL), from.getProperty(), null, ec );
        th.receiveTriple(
                bnode,
                vSINDICE.getProperty(SINDICE.NESTING_STRUCTURED),
                from.getObject() == null ? to.getRoot() : from.getObject(),
                null,
                ec
        );
        th.receiveTriple(
                from.getSubject(),
                vSINDICE.getProperty(SINDICE.NESTING),
                bnode,
                null,
                ec
        );
    }

    /**
     * Entity detection report.
     */
    private class SingleExtractionReport {
        private final Collection<IssueReport.Issue> issues;
        private final List<ResourceRoot>            resourceRoots;
        private final List<PropertyPath>            propertyPaths;

        public SingleExtractionReport(
                Collection<IssueReport.Issue>  issues,
                List<ResourceRoot> resourceRoots,
                List<PropertyPath> propertyPaths
        ) {
            this.issues        = issues;
            this.resourceRoots = resourceRoots;
            this.propertyPaths = propertyPaths;
        }
    }

}
