package org.deri.any23.extractor;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

// passed to extractors
public interface ExtractionResult {
	
	// s, p, o can be null, then the triple will be silently ignored
	void writeTriple(Resource s, URI p, Value o);

	void writeNamespace(String prefix, String uri);

	// extractors should close their results as soon as possible, but
	// don't have to, the environment will close any remaining ones.
	// Implementations should be robust against multiple close()
	// invocations.
	void close();
	
	ExtractionResult openSubResult(Object context);
}