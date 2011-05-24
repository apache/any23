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

package org.deri.any23.extractor;

import org.deri.any23.Configuration;
import org.deri.any23.encoding.EncodingDetector;
import org.deri.any23.encoding.TikaEncodingDetector;
import org.deri.any23.extractor.Extractor.BlindExtractor;
import org.deri.any23.extractor.Extractor.ContentExtractor;
import org.deri.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.deri.any23.extractor.html.DocumentReport;
import org.deri.any23.extractor.html.HTMLDocument;
import org.deri.any23.extractor.html.TagSoupParser;
import org.deri.any23.mime.MIMEType;
import org.deri.any23.mime.MIMETypeDetector;
import org.deri.any23.rdf.Any23ValueFactoryWrapper;
import org.deri.any23.source.DocumentSource;
import org.deri.any23.source.LocalCopyFactory;
import org.deri.any23.source.MemCopyFactory;
import org.deri.any23.util.RDFHelper;
import org.deri.any23.validator.EmptyValidationReport;
import org.deri.any23.validator.ValidatorException;
import org.deri.any23.vocab.SINDICE;
import org.deri.any23.writer.CompositeTripleHandler;
import org.deri.any23.writer.CountingTripleHandler;
import org.deri.any23.writer.TripleHandler;
import org.deri.any23.writer.TripleHandlerException;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.deri.any23.extractor.TagSoupExtractionResult.PropertyPath;
import static org.deri.any23.extractor.TagSoupExtractionResult.ResourceRoot;

/**
 * This class acts as facade where all the extractors were called on a single document.
 */
public class SingleDocumentExtraction {

    private final static Logger log = LoggerFactory.getLogger(SingleDocumentExtraction.class);

    private static final ExtractionParameters DEFAULT_EXTRACTION_PARAMETERS = new ExtractionParameters(
            false, false, true
    );

    private static final boolean extractMetadataTriples =
            Configuration.instance().getFlagProperty("any23.extraction.metadata");

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

    public SingleDocumentExtraction(DocumentSource in, ExtractorGroup extractors, TripleHandler output) {
        this.in = in;
        this.extractors = extractors;
        List<TripleHandler> tripleHandlers = new ArrayList<TripleHandler>();
        tripleHandlers.add(output);
        tripleHandlers.add(new CountingTripleHandler());
        this.output = new CompositeTripleHandler(tripleHandlers);
        this.encoderDetector = new TikaEncodingDetector();
    }

    public SingleDocumentExtraction(DocumentSource in, ExtractorFactory<?> factory, TripleHandler output) {
        this(in, new ExtractorGroup(Collections.<ExtractorFactory<?>>singletonList(factory)),
                output);
        this.setMIMETypeDetector(null);
    }

    public void setLocalCopyFactory(LocalCopyFactory copyFactory) {
        this.copyFactory = copyFactory;
    }

    public void setMIMETypeDetector(MIMETypeDetector detector) {
        this.detector = detector;
    }

    /**
     * Triggers the execution of all the {@link org.deri.any23.extractor.Extractor} registered to this class.
     *
     * @param extractionParameters the parameters applied to the run execution.
     * @throws ExtractionException
     * @throws IOException
     */
    public SingleDocumentExtractionReport run(ExtractionParameters extractionParameters)
    throws ExtractionException, IOException {
        if(extractionParameters == null) {
            extractionParameters = DEFAULT_EXTRACTION_PARAMETERS;
        }
        
        ensureHasLocalCopy();
        try {
            this.documentURI = new Any23ValueFactoryWrapper(
                    ValueFactoryImpl.getInstance()
            ).createURI( in.getDocumentURI() );
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
        try {
            final DocumentContext documentContext = new DocumentContext(
                    extractDocumentLanguage(extractionParameters)
            );
            for (ExtractorFactory<?> factory : matchingExtractors) {
                EntityReport er = runExtractor(extractionParameters, documentContext, factory.createExtractor());
                resourceRoots.addAll( er.resourceRoots );
                propertyPaths.addAll( er.propertyPaths );
            }
        } catch(ValidatorException ve) {
            throw new ExtractionException("An error occurred during the validation phase.", ve);
        }
        if(extractionParameters.isNestingEnabled()) {
            consolidateResources(resourceRoots, propertyPaths, output);
        } else {
            consolidateResources(resourceRoots, output);
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
                EmptyValidationReport.getInstance() : documentReport.getReport()
        );
    }

    public void run() throws IOException, ExtractionException {
        run(DEFAULT_EXTRACTION_PARAMETERS);
    }

    public String getDetectedMIMEType() throws IOException {
        filterExtractorsByMIMEType();
        return  detectedMIMEType == null ? null : detectedMIMEType.toString();
    }

    public boolean hasMatchingExtractors() throws IOException {
        filterExtractorsByMIMEType();
        return !matchingExtractors.isEmpty();
    }

    public String getParserEncoding() {
        if(this.parserEncoding == null) {
            this.parserEncoding = detectEncoding();
        }
        return this.parserEncoding;
    }

    public void setParserEncoding(String encoding) {
        this.parserEncoding = encoding;
        documentReport = null;
    }

    private boolean isHTMLDocument() throws IOException {
        filterExtractorsByMIMEType();
        return ! matchingExtractors.filterByMIMEType( MIMEType.parse("text/html") ).isEmpty();
    }

    /**
     * Extracts the document language where possible.
     *
     * @return the document language if any, <code>null</code> otherwise.
     * @throws java.io.IOException if an error occurs during the document analysis.
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
     * Triggers the execution of a specific {@link org.deri.any23.extractor.Extractor}.
     * 
     * @param extractionParameters the parameters used for the extraction.
     * @param documentContext the context of the current document under processing.
     * @param extractor the {@link org.deri.any23.extractor.Extractor} to be executed.
     * @throws ExtractionException if an error specific to an extractor happens.
     * @throws IOException if an IO error occurs during the extraction.
     * @return the roots of the resources that have been extracted.
     * @throws org.deri.any23.validator.ValidatorException if an error occurs during validation.
     */
    private EntityReport runExtractor(
            ExtractionParameters extractionParameters,
            DocumentContext documentContext,
            Extractor<?> extractor
    ) throws ExtractionException, IOException, ValidatorException {
        if(log.isDebugEnabled()) {
            log.debug("Running " + extractor.getDescription().getExtractorName() + " on " + documentURI);
        }
        long startTime = System.currentTimeMillis();
        ExtractionResultImpl result = new ExtractionResultImpl(documentContext, documentURI, extractor, output);
        try {
            if (extractor instanceof BlindExtractor) {
                final BlindExtractor blindExtractor = (BlindExtractor) extractor;
                blindExtractor.run(documentURI, documentURI, result);
            } else if (extractor instanceof ContentExtractor) {
                ensureHasLocalCopy();
                final ContentExtractor contentExtractor = (ContentExtractor) extractor;
                contentExtractor.run(localDocumentSource.openInputStream(), documentURI, result);
            } else if (extractor instanceof TagSoupDOMExtractor) {
                final TagSoupDOMExtractor tagSoupDOMExtractor = (TagSoupDOMExtractor) extractor;
                final DocumentReport documentReport = getTagSoupDOM(extractionParameters);
                tagSoupDOMExtractor.run(documentReport.getDocument(), documentURI, result);
            } else {
                throw new IllegalStateException("Extractor type not supported: " + extractor.getClass());
            }
            return
                new EntityReport(
                    new ArrayList<ResourceRoot>( result.getResourceRoots() ),
                    new ArrayList<PropertyPath>( result.getPropertyPaths() )
                );
        } catch (ExtractionException ex) {
            if(log.isInfoEnabled()) {
                log.info(extractor.getDescription().getExtractorName() + ": " + ex.getMessage());
            }
            throw ex;
        } finally {
            // Logging result error report.
            if( log.isInfoEnabled() && result.hasErrors() ) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                result.printErrorsReport( new PrintStream(baos) );
                log.info( baos.toString() );
            }
            result.close();

            long elapsed = System.currentTimeMillis() - startTime;
            if(log.isDebugEnabled()) {
                log.debug("Completed " + extractor.getDescription().getExtractorName() + ", " + elapsed + "ms");
            }
        }
    }

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
     * @param output a triple handler event collector.
     * @throws ExtractionException
     */
    private void consolidateResources(
            List<ResourceRoot> resourceRoots,
            List<PropertyPath> propertyPaths,
            TripleHandler output
    ) throws ExtractionException {
        final ExtractionContext context = new ExtractionContext(
                "consolidation-extractor",
                documentURI,
                UUID.randomUUID().toString()
        );

        try {
            output.openContext(context);
        } catch (TripleHandlerException e) {
            throw new ExtractionException(String.format("Error starting document with URI %s", documentURI),
                    e
            );
        }
        try {
            // Add source Web domains to every resource root.
            final String domain;
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
                            SINDICE.getProperty(SINDICE.DOMAIN),
                            ValueFactoryImpl.getInstance().createLiteral(domain),
                            null,
                            context
                    );
                }
            }

            // Detect the nesting relationship among different microformats and explicit them adding connection triples.
            ResourceRoot currentResourceRoot;
            PropertyPath currentPropertyPath;
            for (int r = 0; r < resourceRoots.size(); r++) {
                currentResourceRoot = resourceRoots.get(r);
                for (int p = 0; p < propertyPaths.size(); p++) {
                    currentPropertyPath = propertyPaths.get(p);
                    // Avoid wrong nesting relationships.
                    if (currentPropertyPath.getExtractor().equals(currentResourceRoot.getExtractor())) {
                        continue;
                    }
                    if (subPath(currentResourceRoot.getPath(), currentPropertyPath.getPath())) {
                        createNestingRelationship(currentPropertyPath, currentResourceRoot, output, context);
                    }
                }
            }
            if (extractMetadataTriples) {
                try {
                    addExtractionMetadataTriples(context);
                } catch (TripleHandlerException e) {
                    throw new ExtractionException(
                            String.format(
                                "Error while adding extraction metadata triples document with URI %s", documentURI
                            ),
                            e
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
     * This method consolidates the graphs extracted from the same document.
     * In particular it adds:
     * <ul>
     *   <li>for every microformat root node a triple indicating the original Web page domain;</li>
     * </ul>
     * @param resourceRoots list of RDF nodes representing roots of
     *        extracted microformat graphs and the corresponding HTML paths.
     *        from which such properties have been extracted.
     * @param output a triple handler event collector.
     * @throws ExtractionException
     */
    private void consolidateResources(
            List<ResourceRoot> resourceRoots,
            TripleHandler output
    ) throws ExtractionException {
        final ExtractionContext context = new ExtractionContext(
                "consolidation-extractor",
                documentURI,
                UUID.randomUUID().toString()
        );

        try {
            output.openContext(context);
        } catch (TripleHandlerException e) {
            throw new ExtractionException(String.format("Error starting document with URI %s", documentURI),
                    e
            );
        }
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
                            SINDICE.getProperty(SINDICE.DOMAIN),
                            ValueFactoryImpl.getInstance().createLiteral(domain),
                            null,
                            context
                    );
                }
            }
            try {
                addExtractionMetadataTriples(context);
            } catch (TripleHandlerException e) {
                log.error(String.format("Error while adding extraction medata triples document with URI %s", documentURI));
                throw new ExtractionException(String.format("Error while adding extraction medata triples document with URI %s", documentURI),
                        e
                );
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

    private void addExtractionMetadataTriples(ExtractionContext context) throws TripleHandlerException {
        // adding extraction date
        String xsdDateTimeNow = RDFHelper.toXSDDateTime(new Date());
        output.receiveTriple(
                new URIImpl(documentURI.toString()),
                SINDICE.getProperty(SINDICE.DATE),
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
                SINDICE.getProperty(SINDICE.SIZE),
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
     * @throws org.deri.any23.writer.TripleHandlerException
     */
    private void createNestingRelationship(PropertyPath from, ResourceRoot to, TripleHandler th, ExtractionContext ec)
    throws TripleHandlerException {
        final BNode fromObject = from.getObject();
        final String bNodeHash = from.getProperty().stringValue() + ( fromObject == null ? "" : fromObject.getID() );
        BNode bnode = RDFHelper.getBNode(bNodeHash);
        th.receiveTriple(bnode, SINDICE.getProperty(SINDICE.NESTING_ORIGINAL), from.getProperty(), null, ec );
        th.receiveTriple(
                bnode,
                SINDICE.getProperty(SINDICE.NESTING_STRUCTURED),
                from.getObject() == null ? to.getRoot() : from.getObject(),
                null,
                ec
        );
        th.receiveTriple(
                from.getSubject(),
                SINDICE.getProperty(SINDICE.NESTING),
                bnode,
                null,
                ec
        );
    }

    private class EntityReport {
        private final List<ResourceRoot> resourceRoots;
        private final List<PropertyPath> propertyPaths;

        public EntityReport(
                List<ResourceRoot> resourceRoots,
                List<PropertyPath> propertyPaths
        ) {
            this.resourceRoots = resourceRoots;
            this.propertyPaths = propertyPaths;
        }
    }

}
