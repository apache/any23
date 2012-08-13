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
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This writer simply produces a list of unique <i>URI</i> present in the
 * subject or in the object of every single extracted <i>RDF Statement</i>.
 * 
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class URIListWriter implements FormatWriter {

    private List<Resource> resources;

    private PrintStream printStream;

    private ExtractionContext extractionContext;

    private long contentLength;

    public URIListWriter(OutputStream outputStream) {
        this.resources = new ArrayList<Resource>();
        this.printStream = new PrintStream(outputStream);
    }

    public void startDocument(URI documentURI) throws TripleHandlerException {}

    public void openContext(ExtractionContext context) throws TripleHandlerException {
        this.extractionContext = context;
    }

    public void receiveTriple(Resource s, URI p, Value o, URI g, ExtractionContext context)
            throws TripleHandlerException {
        if(!this.resources.contains(s)) {
            this.resources.add(s);
            this.printStream.println(s.stringValue());
        }
        if(o instanceof Resource && !this.resources.contains(o)) {
            this.resources.add((Resource) o);
            this.printStream.println(o.stringValue());
        }
    }

    public void receiveNamespace(String prefix, String uri, ExtractionContext context)
            throws TripleHandlerException {
    }

    public void closeContext(ExtractionContext context) throws TripleHandlerException {
    }

    public void endDocument(URI documentURI) throws TripleHandlerException {
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public void close() throws TripleHandlerException {
        this.printStream.close();
    }

    @Override
    public boolean isAnnotated() {
        return false;
    }

    @Override
    public void setAnnotated(boolean f) {
        // Empty.
    }
}
