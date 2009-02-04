package org.deri.any23.extractor;

import java.util.ArrayList;
import java.util.Collection;

import org.deri.any23.mime.MIMEType;
import org.deri.any23.rdf.Prefixes;

public class SimpleExtractorFactory<T extends Extractor<?>> implements ExtractorFactory<T> {
	
	public static <S extends Extractor<?>> ExtractorFactory<S> create(String name, Prefixes prefixes, 
			Collection<String> supportedMIMETypes, String exampleInput, Class<S> extractorClass) {
		return new SimpleExtractorFactory<S>(name, prefixes, supportedMIMETypes, exampleInput, extractorClass);
	}
	
	private final String name;
	private final Prefixes prefixes;
	private final Collection<MIMEType> supportedMIMETypes = new ArrayList<MIMEType>();
	private final String exampleInput;
	private final Class<T> extractorClass;
	
	private SimpleExtractorFactory(String name, Prefixes prefixes, 
			Collection<String> supportedMIMETypes, String exampleInput,
			Class<T> extractorClass) {
		this.name = name;
		this.prefixes = (prefixes == null) ? Prefixes.EMPTY : prefixes;
		for (String type: supportedMIMETypes) {
			this.supportedMIMETypes.add(MIMEType.parse(type));
		}
		this.exampleInput = exampleInput;
		this.extractorClass = extractorClass;
	}
	
	public String getExtractorName() {
		return name;
	}
	
	public Prefixes getPrefixes() {
		return prefixes;
	}
	
	public Collection<MIMEType> getSupportedMIMETypes() {
		return supportedMIMETypes;
	}

	public T createExtractor() {
		try {
			return extractorClass.newInstance();
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		} catch (InstantiationException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public String getExampleInput() {
		return exampleInput;
	}
}
