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

import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * Any23 specialization of the {@link org.openrdf.model.ValueFactory}.
 * It provides a wrapper to instantiate RDF objects.
 *
 * // TODO (high) Move our URI fixing methods to a separate utility class
 * 
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
        return _vFactory.createURI(fixURIWithException(arg0));
    }

    /**
     * @return a valid sesame URI or null if any exception occured
     */
    public URI createURI(String arg0, String arg1) {
        if (arg0 == null || arg1 == null) return null;
        return _vFactory.createURI(fixURIWithException(arg0), arg1);
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
            return _vFactory.createURI(baseURI.resolve(fixURIWithException(uri)).toString());
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
            return _vFactory.createURI(fixURIWithException(uri));
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return null;
        }
    }


    /**
     * Fixes typical errors in an absolute URI, such as unescaped spaces.
     *
     * @param uri An absolute URI, can have typical syntax errors
     * @return An absolute URI that is valid against the URI syntax
     * @throws IllegalArgumentException if URI is not fixable
     */
    public static String fixAbsoluteURI(String uri) {
        String fixed = fixURIWithException(uri);
        if (!fixed.matches("[a-zA-Z0-9]+:/.*")) throw new IllegalArgumentException("not a absolute URI: " + uri);
        // Add trailing slash if URI has only authority but no path.
        // TODO This might not be the best place for this. Have a normalize method somewhere?
        if (fixed.matches("https?://[a-zA-Z0-9.-]+(:[0-9+])?")) {
            fixed = fixed + "/";
        }
        return fixed;
    }

    /**
     * Tries to fix a potentially broken relative or absolute URI
     * <p/>
     * These appear to be good rules:
     * <p/>
     * Remove whitespace or '\' or '"' in beginning and end
     * Replace space with %20
     * Drop the triple if it matches this regex (only protocol): ^[a-zA-Z0-9]+:(//)?$
     * Drop the triple if it matches this regex: ^javascript:
     * Truncate ">.*$ from end of lines (Neko didn't quite manage to fix broken markup)
     * Drop the triple if any of these appear in the URL: <>[]|*{}"<>\
     */
    private static String fixURIWithException(String unescapedURI) {
        if (unescapedURI == null) throw new IllegalArgumentException("URI was null");

        //	Remove starting and ending whitespace
        String escapedURI = unescapedURI.trim();

        //Replace space with %20
        escapedURI = escapedURI.replaceAll(" ", "%20");

        //strip linebreaks
        escapedURI = escapedURI.replaceAll("\n", "");

        //'Remove starting  "\" or '"'
        if (escapedURI.startsWith("\\") || escapedURI.startsWith("\"")) escapedURI = escapedURI.substring(1);
        //Remove  ending   "\" or '"'
        if (escapedURI.endsWith("\\") || escapedURI.endsWith("\""))
            escapedURI = escapedURI.substring(0, escapedURI.length() - 1);

        //Drop the triple if it matches this regex (only protocol): ^[a-zA-Z0-9]+:/?/?$
        if (escapedURI.matches("^[a-zA-Z0-9]+:/?/?$"))
            throw new IllegalArgumentException("no authority in URI: " + unescapedURI);

        //Drop the triple if it matches this regex: ^javascript:
        if (escapedURI.matches("^javascript:"))
            throw new IllegalArgumentException("URI starts with javascript: " + unescapedURI);

		// stripHTML
        // escapedURI = escapedURI.replaceAll("\\<.*?\\>", "");

        //>.*$ from end of lines (Neko didn't quite manage to fix broken markup)
        escapedURI = escapedURI.replaceAll(">.*$", "");

        //Drop the triple if any of these appear in the URL: <>[]|*{}"<>\
        // TODO write a test for this
        if (escapedURI.matches("[<>\\[\\]|\\*\\{\\}\"\\\\]"))
            throw new IllegalArgumentException("Invalid character in URI: " + unescapedURI);

        return escapedURI;
    }

}
