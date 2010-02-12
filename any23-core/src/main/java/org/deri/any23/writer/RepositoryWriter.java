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

package org.deri.any23.writer;

import org.deri.any23.extractor.ExtractionContext;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * A <i>Sesame repository</i> triple writer.
 *
 * @see org.openrdf.repository.Repository
 */
public class RepositoryWriter implements TripleHandler {

    private final RepositoryConnection conn;
    private final Resource overrideContext;

    public RepositoryWriter(RepositoryConnection conn) {
        this(conn, null);
    }

    public RepositoryWriter(RepositoryConnection conn, Resource overrideContext) {
        this.conn = conn;
        this.overrideContext = overrideContext;
    }

    public void startDocument(URI documentURI) {
        // ignore
    }

    public void openContext(ExtractionContext context) {
        // ignore
    }

    public void receiveTriple(
            Resource s,
            URI p,
            Value o,
          ExtractionContext context
    ) {
        try {
            conn.add(
                conn.getValueFactory().createStatement(s, p, o),
                getContextResource(context.getDocumentURI())
            );
        } catch (RepositoryException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void receiveNamespace(
            String prefix,
            String uri,
            ExtractionContext context
    ) {
        try {
            conn.setNamespace(prefix, uri);
        } catch (RepositoryException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void closeContext(ExtractionContext context) {
        // ignore
    }

    public void close() {
        // ignore
    }

    public void endDocument(URI documentURI) {
        // ignore
    }

    public void setContentLength(long contentLength) {
        //ignore
    }

    private Resource getContextResource(Resource fromExtractor) {
        if (overrideContext != null) {
            return overrideContext;
        }
        return fromExtractor;
    }
}
