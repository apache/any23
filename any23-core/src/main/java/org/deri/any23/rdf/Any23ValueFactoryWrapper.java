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

package org.deri.any23.rdf;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Any23 specialization of the {@link org.openrdf.model.ValueFactory}.
 * It provides a wrapper to instantiate RDF objects.
 */
public class Any23ValueFactoryWrapper implements ValueFactory {

    private static final Logger logger = LoggerFactory.getLogger(Any23ValueFactoryWrapper.class);

    private final ValueFactory _vFactory;

    public Any23ValueFactoryWrapper(final ValueFactory vFactory) {
        _vFactory = vFactory;
    }

    public BNode createBNode() {
        return _vFactory.createBNode();
    }

    public BNode createBNode(String arg0) {
        if (arg0 == null) return null;
        return _vFactory.createBNode(arg0);
    }

    public Literal createLiteral(String arg0) {
        if (arg0 == null) return null;
        return _vFactory.createLiteral(arg0);
    }

    public Literal createLiteral(boolean arg0) {
        return _vFactory.createLiteral(arg0);
    }

    public Literal createLiteral(byte arg0) {
        return _vFactory.createLiteral(arg0);
    }

    public Literal createLiteral(short arg0) {
        return _vFactory.createLiteral(arg0);
    }

    public Literal createLiteral(int arg0) {
        return _vFactory.createLiteral(arg0);
    }

    public Literal createLiteral(long arg0) {
        return _vFactory.createLiteral(arg0);
    }

    public Literal createLiteral(float arg0) {
        return _vFactory.createLiteral(arg0);
    }

    public Literal createLiteral(double arg0) {
        return _vFactory.createLiteral(arg0);
    }

    public Literal createLiteral(XMLGregorianCalendar arg0) {
        return _vFactory.createLiteral(arg0);
    }

    public Literal createLiteral(String arg0, String arg1) {
        if (arg0 == null) return null;
        return _vFactory.createLiteral(arg0, arg1);
    }

    public Literal createLiteral(String arg0, URI arg1) {
        if (arg0 == null) return null;
        return _vFactory.createLiteral(arg0, arg1);
    }

    public Statement createStatement(Resource arg0, URI arg1, Value arg2) {
        if (arg0 == null || arg1 == null || arg2 == null) return null;
        return _vFactory.createStatement(arg0, arg1, arg2);
    }

    public Statement createStatement(Resource arg0, URI arg1, Value arg2,
                                     Resource arg3) {
        if (arg0 == null || arg1 == null || arg2 == null) return null;
        return _vFactory.createStatement(arg0, arg1, arg2, arg3);
    }

    /**
     * @param arg0
     * @return a valid sesame URI or null if any exception occured
     */
    public URI createURI(String arg0) {
        if (arg0 == null) return null;
        return _vFactory.createURI(RDFUtility.fixURIWithException(arg0));
    }

    /**
     * @return a valid sesame URI or null if any exception occured
     */
    public URI createURI(String arg0, String arg1) {
        if (arg0 == null || arg1 == null) return null;
        return _vFactory.createURI(RDFUtility.fixURIWithException(arg0), arg1);
    }

    /**
     * Fixes typical errors in URIs, and resolves relative URIs against a base URI.
     *
     * @param uri     A URI, relative or absolute, can have typical syntax errors
     * @param baseURI A base URI to use for resolving relative URIs
     * @return An absolute URI, sytnactically valid, or null if not fixable
     */
    public URI resolveURI(String uri, java.net.URI baseURI) {
        try {
            return _vFactory.createURI(baseURI.resolve(RDFUtility.fixURIWithException(uri)).toString());
        } catch (IllegalArgumentException ex) {
            logger.warn(ex.getMessage());
            return null;
        }
    }

    /**
     * @param uri
     * @return a valid sesame URI or null if any exception occured
     */
    public URI fixURI(String uri) {
        try {
            return _vFactory.createURI(RDFUtility.fixURIWithException(uri));
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return null;
        }
    }

    /**
     * Helper method to conditionally add a schema to a URI unless it's there, or null if link is empty.
     */
    public URI fixLink(String link, String defaultSchema) {
        if (link == null) return null;
        link = fixWhiteSpace(link);
        if ("".equals(link)) return null;
        if (defaultSchema != null && !link.startsWith(defaultSchema + ":")) {
            link = defaultSchema + ":" + link;
        }
        return fixURI(link);
    }

    public String fixWhiteSpace(String name) {
        return name.replaceAll("\\s+", " ").trim();
    }

}
