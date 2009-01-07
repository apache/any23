package com.google.code.any23;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Takes an RDF input and just returns it again in the specified format
 * @author Gabriele Renzi
 *
 */
public class PlainRDFizer implements RDFizer {

	private RDFizer.Format type;
	private InputStream input;
	private String url;
	
	/**
	 * @param base the url where this document was found
	 * @param input  the stream where the document can be read
	 * @param format the serialization format of the input
	 */
	public PlainRDFizer(URL base, InputStream input, RDFizer.Format format) {
		this.input = input;
		this.type = format;
		this.url = base.toString();
	}

	public boolean getText(Writer writer, RDFizer.Format format) throws IOException {	
		Model model = ModelFactory.createDefaultModel();
		model.read(input, url, type.toString());
		model.write(writer, format.toString());
		model.close();
		return true;
	}

	public String[] getFormats() {
		return new String[]{"RDF"};
	}

}
