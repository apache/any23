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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 * @see org.deri.any23.writer.TripleHandler
 * @see org.deri.any23.extractor.ExtractionContext
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class ExtractionResultImpl implements ExtractionResult {

    private static final DocumentContext DEFAULT_DOCUMENT_CONTEXT = new DocumentContext(null); 

    private final DocumentContext documentContext;

    private final URI documentURI;

    private final Extractor<?> extractor;

    private final TripleHandler tripleHandler;

    private final ExtractionContext context;

    private final Collection<ExtractionResult> subResults = new ArrayList<ExtractionResult>();

    private final Set<Object> knownContextIDs = new HashSet<Object>();

    private boolean isClosed = false;

    private boolean isInitialized = false;

    private List<Error> errors;

    public ExtractionResultImpl(
            DocumentContext documentContext,
            URI documentURI,
            Extractor<?> extractor,
            TripleHandler tripleHandler,
            Object contextID
    ) {
        this.documentContext = documentContext; 
        this.documentURI     = documentURI;
        this.extractor       = extractor;
        this.tripleHandler   = tripleHandler;
        this.context = new ExtractionContext(
                extractor.getDescription().getExtractorName(),
                documentURI,
                ((contextID == null) ? null : Integer.toHexString(contextID.hashCode()))
        );
        knownContextIDs.add(contextID);
    }

    public ExtractionResultImpl(
            DocumentContext documentContext,
            URI documentURI,
            Extractor<?> extractor,
            TripleHandler tripleHandler
    ) {
        this(documentContext, documentURI, extractor, tripleHandler, null);
    }

    public ExtractionResultImpl(
            URI documentURI,
            Extractor<?> extractor,
            TripleHandler tripleHandler
    ) {
        this(DEFAULT_DOCUMENT_CONTEXT, documentURI, extractor, tripleHandler, null);
    }

    public boolean hasErrors() {
        return errors != null;
    }

    public int getErrorsCount() {
        return errors == null ? 0 : errors.size();
    }

    public void printErrorsReport(PrintStream ps) {
        ps.print(String.format("Context: %s [errors: %d] {\n", context, getErrorsCount()));
        if (errors != null) {
            for (Error error : errors) {
                ps.print(error.toString());
                ps.print("\n");
            }
        }
        // Printing sub results.
        for (ExtractionResult er : subResults) {
            er.printErrorsReport(ps);
        }
        ps.print("}\n");
    }

    public Collection<Error> getErrors() {
        return errors == null ? Collections.<Error>emptyList() : Collections.unmodifiableList(errors);
    }

    public ExtractionResult openSubResult(Object contextID) {
        if (knownContextIDs.contains(contextID)) {
            throw new IllegalArgumentException("Duplicate contextID: " + contextID);
        }
        checkOpen();
        ExtractionResult result =
                new ExtractionResultImpl(documentContext, documentURI, extractor, tripleHandler, contextID);
        subResults.add(result);
        return result;
    }

    public DocumentContext getDocumentContext() {
        return documentContext;
    }

    public ExtractionContext getExtractionContext() {
        return context;
    }

    public void writeTriple(Resource s, URI p, Value o) {
        if (s == null || p == null || o == null) return;
        // Check for mal-constructed literals or BNodes, Sesame does not catch this.
        if (s.stringValue() == null || p.stringValue() == null || o.stringValue() == null) return;
        checkOpen();
        tripleHandler.receiveTriple(s, p, o, context);
    }

    public void writeNamespace(String prefix, String uri) {
        checkOpen();
        tripleHandler.receiveNamespace(prefix, uri, context);
    }

    public void notifyError(ErrorLevel level, String msg, int row, int col) {
        if(errors == null) {
            errors = new ArrayList<Error>();
        }
        errors.add( new Error(level, msg, row, col) );
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
        if(errors != null) {
            errors.clear();
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
