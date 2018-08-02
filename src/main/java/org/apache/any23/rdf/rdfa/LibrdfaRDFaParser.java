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
package org.apache.any23.rdf.rdfa;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.apache.any23.rdf.librdfa.RdfaParser;
import org.apache.any23.rdf.rdfa.utils.LibraryLoader;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFParser;

/**
 *
 * @author Julio Caguano
 */
public class LibrdfaRDFaParser extends AbstractRDFParser {

    static {
        try {
            LibraryLoader.loadLibrary("rdfaJava");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public RDFFormat getRDFFormat() {
        return RDFFormat.RDFA;
    }

    @Override
    public void parse(InputStream in, String baseURI) throws IOException, RDFParseException, RDFHandlerException {
        if (in == null) {
            throw new IllegalArgumentException("Input stream cannot be 'null'");
        }
        if (baseURI == null) {
            throw new IllegalArgumentException("Base URI cannot be 'null'");
        }

        RdfaParser parser = new RdfaParser(baseURI);
        parser.init();

        LibrdfaFilter filter = new LibrdfaFilter(in);
        parser.setCallback(filter);

        filter.setHandler(rdfHandler);
        filter.setValueFactory(valueFactory);

        int status = parser.parse();

        parser.delCallback();
        filter.delete();
    }

    @Override
    public void parse(Reader reader, String baseURI) throws IOException, RDFParseException, RDFHandlerException {
        if (reader == null) {
            throw new IllegalArgumentException("Input stream cannot be 'null'");
        }
        if (baseURI == null) {
            throw new IllegalArgumentException("Base URI cannot be 'null'");
        }

        RdfaParser parser = new RdfaParser(baseURI);
        parser.init();
        LibrdfaFilter filter = new LibrdfaFilter(reader);
        parser.setCallback(filter);

        filter.setHandler(rdfHandler);
        filter.setValueFactory(valueFactory);

        parser.parse();

        parser.delCallback();
        parser.delete();
    }

}
