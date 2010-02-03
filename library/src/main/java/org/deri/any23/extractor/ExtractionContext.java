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
 */

package org.deri.any23.extractor;

import org.openrdf.model.URI;

/**
 * This class provides a context for the extraction.
 */
public class ExtractionContext {

    /**
     * Name of the extractor.
     */
    private final String extractorName;

    /**
     * URI of the document.
     *
     * TODO (high) Maybe this shouldn't store the documentURI because that's already known
     * in every place where this class is used. The requirements are not clear
     * enough to make a decision.
     */
    private final URI documentURI;

    /**
     * ID identifying the document.
     */
    private final String uniqueID;

    public ExtractionContext(String extractorName, URI documentURI) {
        this(extractorName, documentURI, null);
    }

    public ExtractionContext(String extractorName, URI documentURI, String localID) {
        this.extractorName = extractorName;
        this.documentURI = documentURI;
        this.uniqueID = "urn:x-any23:" + getExtractorName() + ":" +
                (localID == null ? "" : localID) + ":" + documentURI;
    }

    public String getExtractorName() {
        return extractorName;
    }

    public URI getDocumentURI() {
        return documentURI;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public int hashCode() {
        return uniqueID.hashCode();
    }

    public boolean equals(Object other) {
        if (!(other instanceof ExtractionContext)) return false;
        return ((ExtractionContext) other).uniqueID.equals(uniqueID);
    }

    public String toString() {
        return String.format("ExtractionContext(%s)", uniqueID);
    }
    
}