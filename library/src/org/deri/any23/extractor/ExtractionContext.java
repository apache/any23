package org.deri.any23.extractor;

import org.deri.any23.rdf.Prefixes;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;

// TODO: equals() ignores the prefixes, probably should remove it
public class ExtractionContext implements ExtractorDescription {	
	private final ExtractorDescription extractor;
	private final URI documentURI;
	private final String localID;
	private final String uniqueID;
	private final URI uniqueID2;
	private final Prefixes prefixes;
	
	public ExtractionContext(ExtractorDescription extractor, URI documentURI, Prefixes contextPrefixes) {
		this(extractor, documentURI, contextPrefixes, null);
	}
	
	public ExtractionContext(ExtractorDescription extractor, URI documentURI, Prefixes contextPrefixes, String localID) {
		this.extractor = extractor;
		this.documentURI = documentURI;
		this.localID = localID;
		this.uniqueID = "urn:x-any23:" + getExtractorName() + ":" +
				(localID == null ? "" : localID) + ":" + documentURI;
		this.uniqueID2 = ValueFactoryImpl.getInstance().createURI(uniqueID);
		if (contextPrefixes == null || contextPrefixes.isEmpty()) {
			this.prefixes = extractor.getPrefixes();
		} else {
			this.prefixes = new Prefixes(extractor.getPrefixes());
			this.prefixes.add(contextPrefixes);
		}
	}
	
	public String getExtractorName() {
		return extractor.getExtractorName();
	}

	public Prefixes getPrefixes() {
		return prefixes;
	}
	
	public URI getDocumentURI() {
		return documentURI;
	}
	
	public boolean isDocumentContext() {
		return localID == null;
	}
	
	public String getLocalID() {
		return localID;
	}
	
	public String getUniqueID() {
		return uniqueID;
	}
	
	// Remove either this or getUniqueID()
	public URI getUniqueIDasURI() {
		return uniqueID2;
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