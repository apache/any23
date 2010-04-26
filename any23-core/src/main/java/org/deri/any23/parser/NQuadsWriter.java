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

package org.deri.any23.parser;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * <i>N-Quads</i> implementation of an {@link org.openrdf.rio.RDFWriter}.
 * See the format specification <a href="http://sw.deri.org/2008/07/n-quads/">here</a>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class NQuadsWriter implements RDFWriter {

    /**
     * The table maintaining namespaces.
     */
    private Map<String,String> namespaceTable;

    /**
     * The output writer.
     */
    private Writer writer;

    /**
     * Maintain the started status.
     */
    private boolean started = false;

    public NQuadsWriter(OutputStream os) {
        this( new OutputStreamWriter(os) );
    }

    public NQuadsWriter(Writer w) {
        if(w == null) {
            throw new NullPointerException("the writer cannot be null.");
        }
        writer = w;
    }

    public RDFFormat getRDFFormat() {
        return NQuads.FORMAT;
    }

    public void startRDF() throws RDFHandlerException {
        if(started) {
            throw new IllegalStateException("Parsing already started.");
        }
        started = true;
    }

    public void endRDF() throws RDFHandlerException {
        if(!started) {
            throw new IllegalStateException("Parsing never started.");
        }

        try {
            writer.flush();
        } catch (IOException ioe) {
            throw new RDFHandlerException("Error while flushing writer.", ioe);
        } finally {
            started = false;
            if(namespaceTable != null) {
                namespaceTable.clear();
            }
        }
    }

    public void handleNamespace(String ns, String uri) throws RDFHandlerException {
        if(!started) {
            throw new IllegalStateException("Parsing never started.");
        }

        if(namespaceTable == null) {
            namespaceTable = new HashMap<String, String>();
        }
        namespaceTable.put(ns, uri);
    }

    public void handleStatement(Statement statement) throws RDFHandlerException {
        if(!started) {
            throw new IllegalStateException("Cannot handle statement without start parsing first.");
        }

        try {
            printSubject(statement);
            printSpace();
            printPredicate(statement);
            printSpace();
            printObject(statement);
            printSpace();
            printGraph(statement);
            printCloseStatement();
        } catch (IOException ioe) {
            throw new RDFHandlerException("An error occurred while printing statement.", ioe);
        }
    }

    public void handleComment(String comment) throws RDFHandlerException {
        // Empty.
    }

    /**
     * Prints out a space.
     *
     * @throws IOException
     */
    private void printSpace() throws IOException {
        writer.append(' ');
    }

    /**
     * Prints out the close statement.
     * 
     * @throws IOException
     */
    private void printCloseStatement() throws IOException {
        writer.append(" .\n");
    }

    /**
     * Prints out a URI string, replacing the existing prefix if found.
     * 
     * @param uriString the URI string.
     * @throws IOException
     */
    private void printURI(String uriString) throws IOException {
        int splitIdx = 0;
        String namespace = null;
        if(namespaceTable != null) {
            splitIdx = uriString.indexOf(':');
            if (splitIdx > 0) {
                String prefix = uriString.substring(0, splitIdx);
                namespace = namespaceTable.get(prefix);
            }
        }

        if (namespace != null) {
            writer.append('<');
            writer.append(namespace);
            writer.append(uriString.substring(splitIdx));
            writer.append('>');
        } else {
            writer.append('<');
            writer.append(uriString);
            writer.append('>');
        }
    }

    /**
     * Prints out a URI, replacing the existing prefix if found.
     *
     * @param uri the URI value.
     * @throws IOException
     */
    private void printURI(Value uri) throws IOException {
        printURI( uri.stringValue() );
    }

    /**
     * Prints out the bnode.
     *
     * @param v bnode value.
     * @throws IOException
     */
    private void printBNode(Value v) throws IOException {
        writer.append("_:");
        writer.append(v.stringValue());
    }

    /**
     * Prints out the subject.
     * 
     * @param s
     * @throws IOException
     */
    private void printSubject(Statement s) throws IOException {
        Resource r = s.getSubject();
        if( r instanceof URI) {
            printURI(r);
        } else {
            printBNode(r);
        }
    }

    /**
     * Prints out the predicate.
     *
     * @param s
     * @throws IOException
     */
    private void printPredicate(Statement s) throws IOException {
        printURI( s.getPredicate() );
    }

    /**
     * Prints out the object, handling all the logic to manage
     * the literal data type / language attribute.
     *
     * @param s
     * @throws IOException
     */
    private void printObject(Statement s) throws IOException {
        Value v = s.getObject();
        if(v instanceof BNode) {
            printBNode(v);
            return;
        }
        if(v instanceof URI) {
            printURI(v);
            return;
        }

        Literal l = (Literal) v;
        writer.append('"');
        writer.append( l.getLabel());
        writer.append('"');

        String language = l.getLanguage();
        if(language != null) {
            writer.append('@');
            writer.append(language);
            return;
        }

        URI datatype = l.getDatatype();
        if(datatype != null) {
            writer.append("^^");
            printURI( datatype.stringValue() );
        }
    }

    /**
     * Prints out the graph.
     *
     * @param s
     * @throws IOException
     */
    private void printGraph(Statement s) throws IOException {
        Resource graph = s.getContext();
        if(graph != null) {
            printURI( s.getContext() );
        }
    }

}
