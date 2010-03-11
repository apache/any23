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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Basic class providing a set of utility methods when dealing with <i>RDF</i>.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 */
public class RDFUtility {

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

}
