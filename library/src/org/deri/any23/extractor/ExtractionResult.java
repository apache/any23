package org.deri.any23.extractor;


import org.deri.any23.rdf.Prefixes;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

// passed to extractors
public interface ExtractionResult {
	
	String getDocumentURI();
	
	// typically invoked by extractor with "this"
	ExtractionContext getDocumentContext(Extractor<?> self);
	
	// typically invoked by extractor with "this"
	ExtractionContext getDocumentContext(Extractor<?> self, Prefixes contextPrefixes);
	
	// typically invoked by extractor with "this"
	ExtractionContext createContext(Extractor<?> self);
	
	// typically invoked by extractor with "this"
	ExtractionContext createContext(Extractor<?> self, Prefixes contextPrefixes);
	
	// extractors should close their contexts as soon as possible, but
	// don't have to, the environment will close any remaining contexts.
	// Don't close the default context.
	void closeContext(ExtractionContext context);

	// context must be the default context or one the extractor has opened
	void writeTriple(Resource s, URI p, Value o, ExtractionContext context);
	
	// context must be the default context or one the extractor has opened
	void setLabel(String label, ExtractionContext context);
}