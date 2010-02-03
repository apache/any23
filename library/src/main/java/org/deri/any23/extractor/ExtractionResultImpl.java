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

import org.deri.any23.rdf.Prefixes;
import org.deri.any23.writer.TripleHandler;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

// TODO: #6 - Comments are out of date
/**
 * <p/>
 * A default implementation of {@link ExtractionResult}; it receives
 * extraction output from one {@link Extractor} working on one document,
 * and passes the output on to a {@link TripleHandler}. It deals with
 * details such as creation of {@link ExtractionContext} objects
 * and closing any open contexts at the end of extraction.
 * <p/>
 * The {@link #close()} method must be invoked after the extractor has
 * finished processing.
 * <p/>
 * There is usually no need to provide additional implementations
 * of the ExtractionWriter interface.
 * <p/>
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */

/* TODO: #6 - Implementation doesn't ensure that openContext() is reported
 * to the tripleHandler for the document context. It's only reported
 * if the extractor actually requests the document context. That might
 * be bad, because the TripleHandler might want to know exactly which
 * extractors have been run on which files, for reporting purposes.
 */
public class ExtractionResultImpl implements ExtractionResult {

    private final URI documentURI;

    private final Extractor<?> extractor;

    private final TripleHandler tripleHandler;

    private final ExtractionContext context;

    private final Collection<ExtractionResult> subResults = new ArrayList<ExtractionResult>();

    private final Set<Object> knownContextIDs = new HashSet<Object>();

    private boolean isClosed = false;

    private boolean isInitialized = false;

    public ExtractionResultImpl(URI documentURI, Extractor<?> extractor, TripleHandler tripleHandler) {
        this(documentURI, extractor, tripleHandler, null);
    }

    public ExtractionResultImpl(
            URI documentURI,
            Extractor<?> extractor,
            TripleHandler tripleHandler,
            Object contextID
    ) {
        this.documentURI = documentURI;
        this.extractor = extractor;
        this.tripleHandler = tripleHandler;
        this.context = new ExtractionContext(
                extractor.getDescription().getExtractorName(), documentURI,
                ((contextID == null) ? null : Integer.toHexString(contextID.hashCode())));
        knownContextIDs.add(contextID);
    }

    public ExtractionResult openSubResult(Object contextID) {
        if (knownContextIDs.contains(contextID)) {
            throw new IllegalArgumentException("Duplicate contextID: " + contextID);
        }
        checkOpen();
        ExtractionResult result = new ExtractionResultImpl(documentURI, extractor, tripleHandler, contextID);
        subResults.add(result);
        return result;
    }

    public void writeTriple(Resource s, URI p, Value o) {
        if (s == null || p == null || o == null) return;
        // Check for malconstructed literals or BNodes, Sesame does not catch this
        if (s.stringValue() == null || p.stringValue() == null || o.stringValue() == null) return;
        checkOpen();
        tripleHandler.receiveTriple(s, p, o, context);
    }

    public void writeNamespace(String prefix, String uri) {
        checkOpen();
        tripleHandler.receiveNamespace(prefix, uri, context);
    }

    public void close() {
        if (isClosed) return;
        isClosed = true;
        for (ExtractionResult subResult : subResults) {
            subResult.close();
        }
        if (isInitialized) {
            tripleHandler.closeContext(context);
        }
    }

    private void checkOpen() {
        if (!isInitialized) {
            isInitialized = true;
            tripleHandler.openContext(context);
            Prefixes prefixes = extractor.getDescription().getPrefixes();
            for (String prefix : prefixes.allPrefixes()) {
                tripleHandler.receiveNamespace(prefix, prefixes.getNamespaceURIFor(prefix), context);
            }
        }
        if (isClosed) {
            throw new IllegalStateException("Not open: " + context);
        }
    }

}
