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

import org.deri.any23.extractor.Extractor.BlindExtractor;
import org.deri.any23.extractor.Extractor.ContentExtractor;
import org.deri.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.deri.any23.extractor.html.TagSoupParser;
import org.deri.any23.mime.MIMEType;
import org.deri.any23.mime.MIMETypeDetector;
import org.deri.any23.rdf.Any23ValueFactoryWrapper;
import org.deri.any23.source.DocumentSource;
import org.deri.any23.source.LocalCopyFactory;
import org.deri.any23.source.MemCopyFactory;
import org.deri.any23.writer.TripleHandler;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.Collections;

/**
 *
 * This class acts as facade where all the extractors were called on a single document.
 *
 */
public class SingleDocumentExtraction {

    private final static Logger log = LoggerFactory.getLogger(SingleDocumentExtraction.class);

    private final DocumentSource in;

    // TODO (low) should be final
    private URI documentURI;
    
    private final ExtractorGroup extractors;

    private final TripleHandler output;

    private LocalCopyFactory copyFactory = null;

    private DocumentSource localDocumentSource = null;

    private MIMETypeDetector detector = null;

    private ExtractorGroup matchingExtractors = null;

    private MIMEType detectedMIMEType = null;

    private Document tagSoupDOM = null;

    public SingleDocumentExtraction(DocumentSource in, ExtractorFactory<?> factory, TripleHandler output) {
        this(in, new ExtractorGroup(Collections.<ExtractorFactory<?>>singletonList(factory)),
                output);
        this.setMIMETypeDetector(null);
    }

    public SingleDocumentExtraction(DocumentSource in, ExtractorGroup extractors, TripleHandler output) {
        this.in = in;
        this.extractors = extractors;
        this.output = output;
    }

    public void setLocalCopyFactory(LocalCopyFactory copyFactory) {
        this.copyFactory = copyFactory;
    }

    public void setMIMETypeDetector(MIMETypeDetector detector) {
        this.detector = detector;
    }

    /**
     *
     * Triggers the execution of all the {@link org.deri.any23.extractor.Extractor} registered to this class.
     *
     * @throws ExtractionException
     * @throws IOException
     */
    public void run() throws ExtractionException, IOException {
        ensureHasLocalCopy();
        try {
            this.documentURI = new Any23ValueFactoryWrapper(ValueFactoryImpl.getInstance()).createURI(in.getDocumentURI());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid URI: " + in.getDocumentURI(), ex);
        }
        log.info("Processing " + this.documentURI);
        filterExtractorsByMIMEType();

        StringBuffer sb = new StringBuffer("Extractors ");
        for (ExtractorFactory<?> factory : matchingExtractors) {
            sb.append(factory.getExtractorName());
            sb.append(' ');
        }
        sb.append("match " + documentURI);
        log.debug(sb.toString());

        // byte[] buffer = new byte[100];
        // int l = getInputStream().read(buffer);
        // log.debug("Content: " + new String(buffer, 0, l));

        // Invoke all extractors
        output.startDocument(documentURI);
        output.setContentLength(in.getContentLength());
        for (ExtractorFactory<?> factory : matchingExtractors) {
            runExtractor(factory.createExtractor());
        }
        output.endDocument(documentURI);
    }

    public String getDetectedMIMEType() throws IOException {
        filterExtractorsByMIMEType();
        return detectedMIMEType.toString();
    }

    public boolean hasMatchingExtractors() throws IOException {
        filterExtractorsByMIMEType();
        return !matchingExtractors.isEmpty();
    }

    private void filterExtractorsByMIMEType() throws IOException {
        if (matchingExtractors != null) return;    // has already been run

        if (detector == null || extractors.allExtractorsSupportAllContentTypes()) {
            matchingExtractors = extractors;
            return;
        }
        ensureHasLocalCopy();
        detectedMIMEType = detector.guessMIMEType(
                java.net.URI.create(documentURI.stringValue()).getPath(), localDocumentSource.openInputStream(),
                MIMEType.parse(localDocumentSource.getContentType()));
        log.debug("detected media type: " + detectedMIMEType);
        matchingExtractors = extractors.filterByMIMEType(detectedMIMEType);
    }

    /**
     * Triggers the execution of a specific {@link org.deri.any23.extractor.Extractor}.
     * 
     * @param extractor the {@link org.deri.any23.extractor.Extractor} to be executed.
     * @throws ExtractionException
     * @throws IOException
     */
    private void runExtractor(Extractor<?> extractor) throws ExtractionException, IOException {
        log.debug("Running " + extractor.getDescription().getExtractorName() + " on " + documentURI);
        long startTime = System.currentTimeMillis();
        ExtractionResultImpl result = new ExtractionResultImpl(documentURI, extractor, output);
        try {
            if (extractor instanceof BlindExtractor) {
                ((BlindExtractor) extractor).run(documentURI, documentURI, result);
            } else if (extractor instanceof ContentExtractor) {
                ensureHasLocalCopy();
                ((ContentExtractor) extractor).run(localDocumentSource.openInputStream(), documentURI, result);
            } else if (extractor instanceof TagSoupDOMExtractor) {
                ((TagSoupDOMExtractor) extractor).run(getTagSoupDOM(), documentURI, result);
            } else {
                throw new RuntimeException("Extractor type not supported: " + extractor.getClass());
            }
        } catch (ExtractionException ex) {
            log.info(extractor.getDescription().getExtractorName() + ": " + ex.getMessage());
            throw ex;
        } finally {
            result.close();
            long elapsed = System.currentTimeMillis() - startTime;
            log.debug("Completed " + extractor.getDescription().getExtractorName() + ", " + elapsed + "ms");
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

    private Document getTagSoupDOM() throws IOException {
        if (tagSoupDOM == null) {
            ensureHasLocalCopy();
            tagSoupDOM = new TagSoupParser(localDocumentSource.openInputStream(), documentURI.stringValue()).getDOM();
        }
        return tagSoupDOM;
    }
}
