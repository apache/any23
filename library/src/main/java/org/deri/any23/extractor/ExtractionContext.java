package org.deri.any23.extractor;

import org.openrdf.model.URI;

// TODO Maybe this shouldn't store the documentURI because that's already known
//   in every place where this class is used. The requirements are not clear

//   enough to make a decision.
public class ExtractionContext {
    private final String extractorName;
    private final URI documentURI;
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
        return "ExtractionContext(" + uniqueID + ")";
    }
}