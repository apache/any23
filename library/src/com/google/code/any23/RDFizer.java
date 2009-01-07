package com.google.code.any23;

import java.io.IOException;
import java.io.Writer;

/**
 * The abstract interface representing something that transforms an input into RDF.
 * The usage of an implementing class should be on the lines of
 * <pre>{@code
 *   RDFizer fizer = new SomethingRdfizer(some, arguments);
 *   if (fizer.getText(System.out,Format.N3))
 *     System.out.println("found the formats:" + Arrays.toString(fizer.getFormats()));}
 * </pre>
 * 
 * @author Gabriele Renzi
 */
public interface RDFizer {
	
	/**
	 * The formats managed by the RDFizers, represents a reference for inputs and outputs
	 */
	public static enum Format {
		RDFXML("RDF/XML"),
		N3("N3"),
		TURTLE("TURTLE"),
		NTRIPLES("N-TRIPLES"), 
		HTML("HTML");
		
		private final String value;

		Format(String value){
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	/**
	 * Extracts the RDF and writes it to the writer
	 * 
	 * @param writer any writer
	 * @param format the output serialization format, Format.HTML should not be allowed
	 * @return true whether data were extracted and written to the writer
	 * @throws IOException
	 */
	public boolean getText(Writer writer, Format format) throws IOException;
	
	/**
	 * @return the formats found in the last extraction
	 */
	public String[] getFormats();
}
