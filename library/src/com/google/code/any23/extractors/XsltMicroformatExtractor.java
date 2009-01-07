package com.google.code.any23.extractors;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.google.code.any23.HTMLDocument;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.JenaException;

public abstract class XsltMicroformatExtractor extends MicroformatExtractor {
	protected Transformer transformer;
    protected String xsltFile;

	// GR: yeah, call-super is an anti pattern, but at least is obvious
	public XsltMicroformatExtractor(URI baseURI, HTMLDocument document, String xsltFile) {
		super(baseURI, document);
		this.xsltFile = xsltFile;
		try {
			StreamSource xsltStream = getXsltStream();
			this.transformer = TransformerFactory.newInstance().newTransformer(xsltStream);
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new RuntimeException(e);
		}
	}
	
	private StreamSource getXsltStream() {
		// GR: see above
		if (null==xsltFile) 
			throw new RuntimeException("xsltFile must be set but it's currently null!");
		InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(xsltFile);
		if (null==resourceAsStream)
			throw new RuntimeException("Resource for '" + xsltFile + "' is null, maybe the XSLT file is not bundled in the jar?");
		
		return new StreamSource(resourceAsStream);
	}

	public boolean extractTo(Model model) {
		long previous = model.size();
		DOMSource source = new DOMSource(document.getDocument());
//		System.out.println(new DOMSerializerImpl().writeToString(document.getDocument()));
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
		//GR TODO: there must be a better way
		String output = sw.getBuffer().toString();
//		System.out.println(output);
		Reader reader= new StringReader(output);
		try {
			model.read(reader, baseURI.toString(), "RDF/XML");
		} catch(JenaException e) {
			return false;
		}
		if (model.size() == previous) {
			return false;
		}
		return true;
	}

}