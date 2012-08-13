/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.any23.writer;

import org.apache.any23.extractor.ExtractionContext;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Implementation of <i>JSON</i> format writer.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class JSONWriter implements FormatWriter {

    private final PrintStream ps;

    private boolean documentStarted = false;

    private boolean firstArrayElemWritten = false;
    private boolean firstObjectWritten    = false;

    public JSONWriter(OutputStream os) {
        if(os == null) {
            throw new NullPointerException("Output stream cannot be null.");
        }
        this.ps = new PrintStream(new BufferedOutputStream(os));
    }

    public void startDocument(URI documentURI) throws TripleHandlerException {
        if(documentStarted) {
            throw new IllegalStateException("Document already started.");
        }
        documentStarted = true;

        firstArrayElemWritten = false;
        ps.print("{ \"quads\" : [");
    }

    public void openContext(ExtractionContext context) throws TripleHandlerException {
        // Empty.
    }

    public void receiveTriple(Resource s, URI p, Value o, URI g, ExtractionContext context)
    throws TripleHandlerException {
        validateDocumentStarted();

        if(firstArrayElemWritten) {
            ps.print(", ");
        } else {
            firstArrayElemWritten = true;
        }
        firstObjectWritten    = false;
        
        ps.print('[');

        if(s instanceof URI) {
            printExplicitURI(s.stringValue(), ps);
        } else {
            printBNode(s.stringValue(), ps);
        }

        printURI(p.stringValue(), ps);

         if(o instanceof URI) {
            printExplicitURI(o.stringValue(), ps);
        } else if(o instanceof BNode) {
            printBNode(o.stringValue(), ps);
        } else {
            printLiteral((Literal) o, ps);
        }

        printURI(g == null ? null : g.stringValue(), ps);

        ps.print(']');
    }

    public void receiveNamespace(String prefix, String uri, ExtractionContext context)
    throws TripleHandlerException {
        // Empty.
    }

    public void closeContext(ExtractionContext context) throws TripleHandlerException {
        // Empty.
    }

    public void endDocument(URI documentURI) throws TripleHandlerException {
        validateDocumentStarted();
        ps.print("]}");
        documentStarted = false;
    }

    public void setContentLength(long contentLength) {
        // Empty.
    }

    public void close() throws TripleHandlerException {
        ps.close();
    }

    private void validateDocumentStarted() {
       if(!documentStarted) {
            throw new IllegalStateException("Document didn't start.");
        }
    }

    private void printURI(String uri, PrintStream ps) {
        printValue(uri, ps);
    }

    private void printExplicitURI(String uri, PrintStream ps) {
        printValue("uri", uri, ps);
    }

    private void printBNode(String bnode, PrintStream ps) {
        printValue("bnode", bnode, ps);
    }

    private void printCommaIfNeeded(PrintStream ps) {
        if(firstObjectWritten) {
            ps.print(", ");
        } else {
            firstObjectWritten = true;
        }
    }

    private void printLiteral(Literal literal, PrintStream ps) {
        printCommaIfNeeded(ps);

        ps.print('{');

        ps.print("\"type\" : \"literal\"");

        ps.print(", ");

        ps.print("\"value\" : ");
        ps.print('"');
        ps.print(literal.stringValue());
        ps.print('"');

        ps.print(", ");

        ps.print("\"lang\" : ");
        final String language = literal.getLanguage();
        if (language != null) {
            ps.print('"');
            ps.print(literal.getLanguage());
            ps.print('"');
        } else {
            ps.print("null");
        }

        ps.print(", ");

        ps.print("\"datatype\" : ");
        final URI datatype = literal.getDatatype();
        if(datatype != null) {
        ps.print('"');
        ps.print(datatype.stringValue());
        ps.print('"');
        } else {
            ps.print("null");
        }

        ps.print('}');
    }

    private void printValue(String type, String value, PrintStream ps) {
        printCommaIfNeeded(ps);

        ps.print("{ \"type\" : \"");
        ps.print(type);
        ps.print("\", \"value\" : ");
        if (value != null) {
            ps.print('"');
            ps.print(value);
            ps.print('"');
        } else {
            ps.print("null");
        }
        ps.print('}');
    }

    private void printValue(String value, PrintStream ps) {
        printCommaIfNeeded(ps);

        if (value != null) {
            ps.print('"');
            ps.print(value);
            ps.print('"');
        } else {
            ps.print("null");
        }
    }

    @Override
    public boolean isAnnotated() {
        return false; // TODO: add annotation support.
    }

    @Override
    public void setAnnotated(boolean f) {
        // Empty.
    }
}
