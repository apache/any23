/*
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
 */

package org.deri.any23.extractor.html;

import org.cyberneko.html.parsers.DOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * Parses an {@link java.io.InputStream}
 * into an <io>HTML DOM</i> tree using a <i>TagSoup</i> parser.
 * <p/>
 * <strong>Note:</strong> The resulting <i>DOM</i> tree will not be namespace
 * aware, and all element names will be upper case, while attributes
 * will be lower case. This is because the
 * <a href="http://nekohtml.sourceforge.net/">NekoHTML</a> based <i>TagSoup</i> parser
 * by default uses the <a href="http://xerces.apache.org/xerces2-j/dom.html">Xerces HTML DOM</a>
 * implementation, which doesn't support namespaces and forces uppercase element names. This works
 * with the <i>RDFa XSLT Converter</i> and with </i>XPath</i>, so we left it this way.
 * <p/>
 * TODO #10 We should pass encoding from the Content-Type HTTP header if available.
 * TODO #10 Check if NekoHTML's encoding handling is sane.
 *
 * @author Richard Cyganiak (richard at cyganiak dot de)
 */
public class TagSoupParser {

    private final static Logger log = LoggerFactory.getLogger(TagSoupParser.class);

    private final InputStream input;

    private final String documentURI;

    private final String encoding;
    
    private Document result = null;

    public TagSoupParser(InputStream input, String documentURI) {
        this.input = input;
        this.documentURI = documentURI;
        this.encoding = null;
    }

    public TagSoupParser(InputStream input, String documentURI, String encoding) {
        if(!Charset.isSupported(encoding))
            throw new UnsupportedCharsetException(String.format("Charset %s is not supported", encoding));

        this.input = input;
        this.documentURI = documentURI;
        this.encoding = encoding;
    }

    public Document getDOM() throws IOException {
        if (result == null) {
            long startTime = System.currentTimeMillis();
            try {
                result = parse();
            } catch (SAXException ex) {
                // should not happen, it's a tag soup parser
                throw new RuntimeException("Shouldn not happen, it's a tag soup parser", ex);
            } catch (TransformerException ex) {
                // should not happen, it's a tag soup parser
                throw new RuntimeException("Shouldn not happen, it's a tag soup parser", ex);
            } catch (NullPointerException ex) {
                if (ex.getStackTrace()[0].getClassName().equals("java.io.Reader")) {
                    throw new RuntimeException("Bug in NekoHTML, try upgrading to newer release!", ex);
                } else {
                    throw ex;
                }
            } finally {
                long elapsed = System.currentTimeMillis() - startTime;
                log.debug("Parsed " + documentURI + " with NekoHTML, " + elapsed + "ms");
            }
        }
        result.setDocumentURI(documentURI);
        return result;
    }

    private Document parse() throws IOException, SAXException, TransformerException {

        DOMParser parser = new DOMParser();
        parser.setFeature("http://xml.org/sax/features/namespaces", false);

        if(this.encoding != null)
            parser.setProperty("http://cyberneko.org/html/properties/default-encoding", this.encoding);

        parser.parse(new InputSource(input));
        return parser.getDocument();
    }
    
}