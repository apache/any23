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

package org.apache.any23.io.nquads;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RioSetting;
import org.openrdf.rio.WriterConfig;
import org.openrdf.rio.helpers.NTriplesParserSettings;
import org.openrdf.rio.helpers.RDFWriterBase;
import org.openrdf.rio.ntriples.NTriplesUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <i>N-Quads</i> implementation of an {@link org.openrdf.rio.RDFWriter}.
 * See the format specification <a href="http://sw.deri.org/2008/07/n-quads/">here</a>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class NQuadsWriter extends RDFWriterBase implements RDFWriter {

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

    private WriterConfig writerConfig;

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
        return RDFFormat.NQUADS;
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
        namespaceTable.put(ns, NTriplesUtil.escapeString(uri) );
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
        try {
            writer.write("# ");
            writer.write(comment);
            writer.append('\n');
        } catch (IOException ioe) {
            throw new RDFHandlerException("An error occurred while printing comment.", ioe);
        }
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
     * @param uri the URI to print.
     * @throws IOException
     */
    private void printURI(URI uri) throws IOException {
        final String uriString = uri.stringValue();
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
            writer.append( NTriplesUtil.escapeString(uriString.substring(splitIdx)) );
            writer.append('>');
        } else {
            writer.append('<');
            writer.append( NTriplesUtil.escapeString(uriString) );
            writer.append('>');
        }
    }

    /**
     * Prints out the bnode.
     *
     * @param b bnode value.
     * @throws IOException
     */
    private void printBNode(BNode b) throws IOException {
        writer.append( NTriplesUtil.toNTriplesString(b) );
    }

    /**
     * Prints out the resource.
     *
     * @param r resource value.
     * @throws java.io.IOException
     */
    private void printResource(Resource r) throws IOException {
        if(r instanceof BNode) {
            printBNode((BNode) r);
        } else if(r instanceof URI) {
            printURI((URI) r);
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Prints out a literal value.
     *
     * @param l literal value.
     * @throws java.io.IOException
     */
    private void printLiteral(Literal l) throws IOException {
        writer.append( NTriplesUtil.toNTriplesString(l) );
    }

    /**
     * Prints out the subject.
     * 
     * @param s
     * @throws IOException
     */
    private void printSubject(Statement s) throws IOException {
        printResource( s.getSubject() );
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
        if(v instanceof Resource) {
            printResource((Resource) v);
            return;
        }
        printLiteral( (Literal) v );
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
            printResource( s.getContext() );
        }
    }

    @Override
    public Collection<RioSetting<?>> getSupportedSettings() {
        Set<RioSetting<?>> results = new HashSet<RioSetting<?>>(super.getSupportedSettings());
        
        results.add(NTriplesParserSettings.IGNORE_NTRIPLES_INVALID_LINES);
        
        return results;
    }

}
