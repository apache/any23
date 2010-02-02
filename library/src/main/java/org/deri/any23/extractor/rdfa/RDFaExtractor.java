package org.deri.any23.extractor.rdfa;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.Extractor.TagSoupDOMExtractor;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.extractor.rdf.RDFHandlerAdapter;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

/**
 * Extractor for RDFa in HTML, based on Fabien Gadon's XSLT transform, found
 * <a href="http://ns.inria.fr/grddl/rdfa/">here</a>. It works by first
 * parsing the HTML using a tagsoup parser, then applies the XSLT to the
 * DOM tree, then parses the resulting RDF/XML.
 * <p/>
 * TODO: Add configuration option for wether to add standard HTML triples
 * (which result from rel="stylesheet" and such)
 *
 * @author Gabriele Renzi
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class RDFaExtractor implements TagSoupDOMExtractor {

    public final static String NAME = "html-rdfa";

    private final static String xsltFilename = "rdfa.xslt";

    private static XSLTStylesheet xslt = null;

    public final static ExtractorFactory<RDFaExtractor> factory =
            SimpleExtractorFactory.create(
                    NAME,
                    null,
                    Arrays.asList("text/html;q=0.3", "application/xhtml+xml;q=0.3"),
                    null,
                    RDFaExtractor.class
            );

    public void run(Document in, URI documentURI, ExtractionResult out)
            throws IOException, ExtractionException {
        StringWriter buffer = new StringWriter();
        getXSLT().applyTo(in, buffer);

        try {
            RDFParser parser = new RDFXMLParser();
            parser.setRDFHandler(new RDFHandlerAdapter(out));
            parser.parse(
                    new StringReader(buffer.getBuffer().toString()),
                    documentURI.stringValue());
        } catch (RDFHandlerException ex) {
            throw new RuntimeException("Should not happen, RDFHandlerAdapter does not throw RDFHandlerException", ex);
        } catch (RDFParseException ex) {
            throw new ExtractionException("Invalid RDF/XML produced by RDFa transform: " + ex.getMessage(), ex);
        }
    }

    private synchronized XSLTStylesheet getXSLT() {
        // Lazily initialized static instance, so we don't parse
        // the XSLT unless really necessary, and only once
        if (xslt == null) {
            InputStream in = RDFaExtractor.class.getResourceAsStream(xsltFilename);
            if (in == null) {
                throw new RuntimeException("Couldn't load '" + xsltFilename +
                        "', maybe the file is not bundled in the jar?");
            }
            xslt = new XSLTStylesheet(in);
        }
        return xslt;
    }

    public ExtractorDescription getDescription() {
        return factory;
    }

}
