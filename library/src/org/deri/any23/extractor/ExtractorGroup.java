package org.deri.any23.extractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.deri.any23.mime.MIMEType;

public class ExtractorGroup implements Iterable<ExtractorFactory<?>> {
	private final Collection<ExtractorFactory<?>> factories;
	
	public ExtractorGroup(Collection<ExtractorFactory<?>> factories) {
		this.factories = factories;
	}
	
	public boolean isEmpty() {
		return factories.isEmpty();
	}
	
	public ExtractorGroup filterByMIMEType(MIMEType mimeType) {
//		@@@ wildcards, q values
		Collection<ExtractorFactory<?>> matching = new ArrayList<ExtractorFactory<?>>();
		for (ExtractorFactory<?> factory: factories) {
			if (supportsAllContentTypes(factory) || supports(factory, mimeType)) {
				matching.add(factory);
			}
		}
		return new ExtractorGroup(matching);
	}
	
	public Iterator<ExtractorFactory<?>> iterator() {
		return factories.iterator();
	}
	
	public boolean allExtractorsSupportAllContentTypes() {
		for (ExtractorFactory<?> factory: factories) {
			if (!supportsAllContentTypes(factory)) return false;
		}
		return true;
	}
	
	private boolean supportsAllContentTypes(ExtractorFactory<?> factory) {
		return factory.getSupportedMIMETypes().contains("*/*");
	}
	
	private boolean supports(ExtractorFactory<?> factory, MIMEType mimeType) {
		for (MIMEType supported: factory.getSupportedMIMETypes()) {
			if (supported.isAnyMajorType()) return true;
			if (supported.isAnySubtype() && supported.getMajorType().equals(mimeType.getMajorType())) return true;
			if (supported.getFullType().equals(mimeType.getFullType())) return true;
		}
		return false;
	}
}
