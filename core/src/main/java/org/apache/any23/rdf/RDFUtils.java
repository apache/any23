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

package org.apache.any23.rdf;

import org.apache.any23.io.nquads.NQuadsParser;
import org.apache.any23.util.MathUtils;
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
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.ntriples.NTriplesParser;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.openrdf.rio.turtle.TurtleParser;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Basic class providing a set of utility methods when dealing with <i>RDF</i>.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class RDFUtils {

    /**
     * List of supported <i>RDF</i> parsers.
     */
    public enum Parser {
        RDFXML,
        Turtle,
        NTriples,
        NQuads
    }

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

        //    Remove starting and ending whitespace
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

    /**
     * Creates a {@link URI}.
     */
    public static URI uri(String uri) {
        return valueFactory.createURI(uri);
    }

    /**
     * Creates a {@link URI}.
     */
    public static URI uri(String namespace, String localName) {
        return valueFactory.createURI(namespace, localName);
    }

    /**
     * Creates a {@link Literal}.
     */
    public static Literal literal(String s) {
        return valueFactory.createLiteral(s);
    }

    /**
     * Creates a {@link Literal}.
     */
    public static Literal literal(boolean b) {
        return valueFactory.createLiteral(b);
    }

    /**
     * Creates a {@link Literal}.
     */
    public static Literal literal(byte b) {
        return valueFactory.createLiteral(b);
    }

    /**
     * Creates a {@link Literal}.
     */
    public static Literal literal(short s) {
        return valueFactory.createLiteral(s);
    }

    /**
     * Creates a {@link Literal}.
     */
    public static Literal literal(int i) {
        return valueFactory.createLiteral(i);
    }

    /**
     * Creates a {@link Literal}.
     */
    public static Literal literal(long l) {
        return valueFactory.createLiteral(l);
    }

    /**
     * Creates a {@link Literal}.
     */
    public static Literal literal(float f) {
        return valueFactory.createLiteral(f);
    }

    /**
     * Creates a {@link Literal}.
     */
    public static Literal literal(double d) {
        return valueFactory.createLiteral(d);
    }

    /**
     * Creates a {@link Literal}.
     */
    public static Literal literal(String s, String l) {
        return valueFactory.createLiteral(s, l);
    }

    /**
     * Creates a {@link Literal}.
     */
    public static Literal literal(String s, URI datatype) {
        return valueFactory.createLiteral(s, datatype);
    }

    /**
     * Creates a {@link BNode}.
     */
    // TODO: replace this with all occurrences of #getBNode()
    public static BNode bnode(String id) {
        return valueFactory.createBNode(id);
    }

    /**
     * @return a <code>bnode</code> with unique id.
     */
    public static BNode bnode() {
        return valueFactory.createBNode();
    }

    /**
     * Creates a {@link BNode}.
     */
    public static BNode getBNode(String id) {
        return valueFactory.createBNode(
            "node" + MathUtils.md5(id)
        );
    }

    /**
     * Creates a {@link Statement}.
     */
    public static Statement triple(Resource s, URI p, Value o) {
        return valueFactory.createStatement(s, p, o);
    }

    /**
     * Creates a statement of type: <code>toValue(s), toValue(p), toValue(o)</code>
     *
     * @param s subject.
     * @param p predicate.
     * @param o object.
     * @return a statement instance.
     */
    public static Statement triple(String s, String p, String o) {
        return valueFactory.createStatement((Resource) toValue(s), (URI) toValue(p), toValue(o));
    }

    /**
     * Creates a {@link Statement}.
     */
    public static Statement quad(Resource s, URI p, Value o, Resource g) {
        return valueFactory.createStatement(s, p, o, g);
    }

    /**
     * Creates a statement of type: <code>toValue(s), toValue(p), toValue(o), toValue(g)</code>
     */
    public static Statement quad(String s, String p, String o, String g) {
        return valueFactory.createStatement((Resource) toValue(s), (URI) toValue(p), toValue(o), (Resource) toValue(g));
    }

    /**
     * Creates a {@link Value}. If <code>s == 'a'</code> returns
     * an {@link RDF#TYPE}. If <code> s.matches('[a-z0-9]+:.*')</code>
     * expands the corresponding prefix using {@link PopularPrefixes}.
     *
     * @param s
     * @return a value instance.
     */
    public static Value toValue(String s) {
        if ("a".equals(s)) return RDF.TYPE;
        if (s.matches("[a-z0-9]+:.*")) {
            return PopularPrefixes.get().expand(s);
        }
        return valueFactory.createLiteral(s);
    }

    /**
     * Creates a new {@link RDFParser} instance.
     *
     * @param p parser type.
     * @return parser instance.
     * @throws IllegalArgumentException if parser is unsupported.
     */
    public static RDFParser getRDFParser(Parser p) {
        switch (p) {
            case RDFXML:
                return new RDFXMLParser();
            case Turtle:
                return new TurtleParser();
            case NTriples:
                return new NTriplesParser();
            case NQuads:
                return new NQuadsParser();
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Returns a parser type from the given extension.
     *
     * @param ext input extension.
     * @return parser matching the extension.
     * @throws IllegalArgumentException if no extension matches.
     */
    public static Parser getParserByExtension(String ext) {
        if("rdf".equals(ext)) {
            return Parser.RDFXML;
        }
        if("ttl".equals(ext)) {
            return Parser.Turtle;
        }
        if("nt".equals(ext)) {
            return Parser.NTriples;
        }
        if("nq".equals(ext)) {
            return Parser.NQuads;
        }
        throw new IllegalArgumentException("Unknown extension : " + ext);
    }

    /**
     * Parses the content of <code>is</code> input stream with the
     * specified parser <code>p</code> using <code>baseURI</code>.
     *
     * @param parser parser instance.
     * @param is input stream containing <code>RDF</data>.
     * @param baseURI base uri.
     * @return list of statements detected within the input stream.
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    public static Statement[] parseRDF(RDFParser parser, InputStream is, String baseURI)
    throws RDFHandlerException, IOException, RDFParseException {
        final BufferRDFHandler handler = new BufferRDFHandler();
        parser.setVerifyData(true);
        parser.setStopAtFirstError(true);
        parser.setPreserveBNodeIDs(true);
        parser.setRDFHandler(handler);
        parser.parse(is, baseURI);
        return handler.statements.toArray( new Statement[handler.statements.size()] );
    }

    /**
     * Parses the content of <code>is</code> input stream with the
     * specified parser instance <code>p</code> using <code>baseURI</code>.
     *
     * @param p parser type.
     * @param is input stream containing <code>RDF</data>.
     * @param baseURI base uri.
     * @return list of statements detected within the input stream.
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    public static Statement[] parseRDF(Parser p, InputStream is, String baseURI)
    throws RDFHandlerException, IOException, RDFParseException {
        final RDFParser parser = getRDFParser(p);
        return parseRDF(parser, is, baseURI);
    }

    /**
     * Parses the content of <code>is</code> input stream with the
     * specified parser <code>p</code> using <code>''</code> as base URI.
     *
     * @param p parser type.
     * @param is input stream containing <code>RDF</data>.
     * @return list of statements detected within the input stream.
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    public static Statement[] parseRDF(Parser p, InputStream is)
    throws RDFHandlerException, IOException, RDFParseException {
        return parseRDF(p, is, "");
    }

    /**
     * Parses the content of <code>in</code> string with the
     * specified parser <code>p</code> using <code>''</code> as base URI.
     *
     * @param p parser type.
     * @param in input string containing <code>RDF</data>.
     * @return list of statements detected within the input string.
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    public static Statement[] parseRDF(Parser p, String in)
    throws RDFHandlerException, IOException, RDFParseException {
        return parseRDF(p, new ByteArrayInputStream(in.getBytes()));
    }

    /**
     * Parses the content of the <code>resource</code> file
     * guessing the content format from the extension.
     *
     * @param resource resource name.
     * @return the statements declared within the resource file.
     * @throws java.io.IOException if an error occurs while reading file.
     * @throws org.openrdf.rio.RDFHandlerException if an error occurs while parsing file.
     * @throws org.openrdf.rio.RDFParseException if an error occurs while parsing file.
     */
    public static Statement[] parseRDF(String resource) throws RDFHandlerException, IOException, RDFParseException {
        final int extIndex = resource.lastIndexOf(".");
        if(extIndex == -1)
            throw new IllegalArgumentException("Error while detecting the extension in resource name " + resource);
        final String extension = resource.substring(extIndex + 1);
        return parseRDF( getParserByExtension(extension), RDFUtils.class.getResourceAsStream(resource) );
    }

    /**
     * Checks if <code>href</code> is absolute or not.
     *
     * @param href candidate URI.
     * @return <code>true</code> if <code>href</code> is absolute,
     *         <code>false</code> otherwise.
     */
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

    private static class BufferRDFHandler implements RDFHandler {

        private final List<Statement> statements = new ArrayList<Statement>();

        @Override
        public void startRDF() throws RDFHandlerException {
            // Empty.
        }

        @Override
        public void endRDF() throws RDFHandlerException {
            // Empty.
        }

        @Override
        public void handleNamespace(String s, String s1) throws RDFHandlerException {
            // Empty.
        }

        @Override
        public void handleStatement(Statement statement) throws RDFHandlerException {
            statements.add(statement);
        }

        @Override
        public void handleComment(String s) throws RDFHandlerException {
            // Empty.
        }

    }

}
