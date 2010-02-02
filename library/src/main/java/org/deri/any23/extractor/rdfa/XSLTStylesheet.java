package org.deri.any23.extractor.rdfa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.Writer;

/**
 * An XSLT stylesheet loaded from an InputStream, can be applied
 * to DOM trees and writes the result to a {@link Writer}.
 * <p/>
 * TODO: XSLTStylesheet should have better error handling
 *
 * @author Gabriele Renzi
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class XSLTStylesheet {
    private final static Logger log = LoggerFactory.getLogger(XSLTStylesheet.class);

    private final Transformer transformer;

    public XSLTStylesheet(InputStream xsltFile) {
        try {
            transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsltFile));
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException("Should not happen, we use the default configuration", e);
        }
    }

    public synchronized void applyTo(Document document, Writer output) {
        try {
            transformer.transform(new DOMSource(document, document.getBaseURI()), new StreamResult(output));
        } catch (TransformerException e) {    // TODO: Figure out when this can be thrown
            log.info("Exception in XSLTStylesheet.applyTo; details follow", e);
            log.info("Input DOM node:", document);
            log.info("Input DOM node getBaseURI:", document.getBaseURI());
            log.info("Output writer:", output);
            throw new RuntimeException("Exception occured during XSLT transformation", e);
        }
    }
}