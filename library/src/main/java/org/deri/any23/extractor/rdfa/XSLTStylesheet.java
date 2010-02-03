/**
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
 * TODO (high): XSLTStylesheet should have better error handling
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

    /**
     * Applies the XSLT transformation
     * @param document where apply the transformation
     * @param output the {@link java.io.Writer} where write on
     */
    public synchronized void applyTo(Document document, Writer output) {
        try {
            transformer.transform(new DOMSource(document, document.getBaseURI()), new StreamResult(output));
        } catch (TransformerException e) {
            // TODO (high): Figure out when this can be thrown
            log.info("Exception in XSLTStylesheet.applyTo; details follow", e);
            log.info("Input DOM node:", document);
            log.info("Input DOM node getBaseURI:", document.getBaseURI());
            log.info("Output writer:", output);
            throw new RuntimeException("Exception occured during XSLT transformation", e);
        }
    }
    
}