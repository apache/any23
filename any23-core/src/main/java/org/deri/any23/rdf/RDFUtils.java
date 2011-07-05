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

import org.deri.any23.util.MathUtils;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Basic class providing a set of utility methods when dealing with <i>RDF</i>.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 */
public class RDFUtils {

    private static final ValueFactory valueFactory = ValueFactoryImpl.getInstance();

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
        if (fixed.matches("https?://[a-zA-Z0-9.-]+(:[0-9+])?")) {
            fixed = fixed + "/";
        }
        return fixed;
    }

    /**
     * This method allows to obtain an <a href="http://www.w3.org/TR/xmlschema-2/#date">XML Schema</a> compliant date
     * providing a textual representation of a date and textual a pattern for parsing it.
     *
     * @param dateToBeParsed the String containing the date.
     * @param format the pattern as descibed in {@link java.text.SimpleDateFormat}
     * @return a {@link String} representing the date
     * @throws java.text.ParseException
     * @throws javax.xml.datatype.DatatypeConfigurationException
     */
    public static String getXSDDate(String dateToBeParsed, String format)
    throws ParseException, DatatypeConfigurationException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = simpleDateFormat.parse(dateToBeParsed);
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        XMLGregorianCalendar xml = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        xml.setTimezone(0);
        return xml.toString();
    }

    /**
     * Prints a <code>date</code> to the XSD datetime format.
     *
     * @param date date to be printed.
     * @return the string representation of the input date.
     */
    public static String toXSDDateTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        String s = simpleDateFormat.format(date);
        StringBuilder sb = new StringBuilder(s);
        sb.insert(22, ':');
        return sb.toString();
    }

    /**
     * Tries to fix a potentially broken relative or absolute URI.
     *
     * <p/>
     * These appear to be good rules:
     * Remove whitespace or '\' or '"' in beginning and end
     * Replace space with %20
     * Drop the triple if it matches this regex (only protocol): ^[a-zA-Z0-9]+:(//)?$
     * Drop the triple if it matches this regex: ^javascript:
     * Truncate ">.*$ from end of lines (Neko didn't quite manage to fix broken markup)
     * Drop the triple if any of these appear in the URL: <>[]|*{}"<>\
     * <p/>
     *
     * @param unescapedURI uri string to be unescaped.
     * @return the unescaped string.
     */
    public static String fixURIWithException(String unescapedURI) {
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
        if (escapedURI.matches("[<>\\[\\]|\\*\\{\\}\"\\\\]"))
            throw new IllegalArgumentException("Invalid character in URI: " + unescapedURI);

        return escapedURI;
    }

    public static URI uri(String uri) {
        return valueFactory.createURI(uri);
    }

    public static URI uri(String namespace, String localName) {
        return valueFactory.createURI(namespace, localName);
    }

    public static Literal literal(String s) {
        return valueFactory.createLiteral(s);
    }

    public static Literal literal(String s, String l) {
        return valueFactory.createLiteral(s, l);
    }

    public static Literal literal(String s, URI datatype) {
        return valueFactory.createLiteral(s, datatype);
    }

    public static BNode getBNode(String id) {
        return valueFactory.createBNode(
            "node" + MathUtils.md5(id)
        );
    }

    public static Statement triple(Resource s, URI p, Value o) {
        return valueFactory.createStatement(s, p, o);
    }

    public static Statement quad(Resource s, URI p, Value o, Resource g) {
        return valueFactory.createStatement(s, p, o, g);
    }

    public static Value toRDF(String s) {
        if ("a".equals(s)) return RDF.TYPE;
        if (s.matches("[a-z0-9]+:.*")) {
            return PopularPrefixes.get().expand(s);
        }
        return valueFactory.createLiteral(s);
    }

    public static Statement toTriple(String s, String p, String o) {
        return valueFactory.createStatement((Resource) toRDF(s), (URI) toRDF(p), toRDF(o));
    }

     public static boolean isAbsoluteURI(String href) {
        try {
            new URIImpl(href.trim());
            new java.net.URI(href.trim());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
     }

    private RDFUtils() {}

}
