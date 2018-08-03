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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.any23.rdf.librdfa.Callback;
import org.apache.any23.rdf.librdfa.rdfa;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFHandler;

/**
 *
 * @author Julio Caguano
 */
public class LibrdfaFilter extends Callback {

    private BufferedReader bis = null;
    private int len = 0;
    private RDFHandler handler;
    private ValueFactory valueFactory;

    public LibrdfaFilter(InputStream is) {
        super();
        bis = new BufferedReader(new InputStreamReader(is));
    }

    public LibrdfaFilter(Reader reader) {
        super();
        bis = new BufferedReader(reader);
    }

    @Override
    public void default_graph(String subject, String predicate, String object, int object_type, String datatype, String language) {
        IRI s = valueFactory.createIRI(subject);
        IRI p = valueFactory.createIRI(predicate);
        Value o = null;

        if (object_type == rdfa.RDF_TYPE_IRI) { // 1
            o = valueFactory.createIRI(object);
        } else if (object_type == rdfa.RDF_TYPE_PLAIN_LITERAL) { // 2
            o = valueFactory.createLiteral(object);
        } else if (object_type == rdfa.RDF_TYPE_XML_LITERAL) { // 3
            o = valueFactory.createLiteral(object, RDF.XMLLITERAL);
        } else if (object_type == rdfa.RDF_TYPE_TYPED_LITERAL) { // 4
            if (datatype != null) {
                IRI dt = valueFactory.createIRI(datatype);
                o = valueFactory.createLiteral(object, dt);
            } else {
                o = valueFactory.createLiteral(object, language);
            }
        }
        if (handler != null && o != null) {
            Statement stmt = valueFactory.createStatement(s, p, o);
            handler.handleStatement(stmt);
        } else {
            System.err.println("VALIDATE: S=" + subject + "P=" + predicate + "O=" + object + "OT=" + object_type + "DT=" + datatype + "LANG=" + language);
        }
    }

    @Override
    public void processor_graph(String subject, String predicate, String object, int object_type, String datatype, String language) {
        if (handler != null && rdfa.RDF_TYPE_NAMESPACE_PREFIX == object_type) { // 0
            handler.handleNamespace(predicate, object);
        } else {
            System.out.println("Processor: S=" + subject + "\tP=" + predicate + "\tO=" + object + "\tOT=" + object_type + "\tDT:" + datatype + "\tLANG=" + language);
        }
    }

    @Override
    public String fill_data(long buffer_length) {
        char[] d = new char[(int) buffer_length];

        try {
            len = bis.read(d, 0, (int) buffer_length);
        } catch (IOException ex) {
            Logger.getLogger(LibrdfaFilter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new String(d);
    }

    @Override
    public long fill_len() {
        if (len == -1) {
            return 0;
        }
        return len;
    }

    public RDFHandler getHandler() {
        return handler;
    }

    public void setHandler(RDFHandler handler) {
        this.handler = handler;
    }

    public ValueFactory getValueFactory() {
        return valueFactory;
    }

    public void setValueFactory(ValueFactory valueFactory) {
        this.valueFactory = valueFactory;
    }

}
