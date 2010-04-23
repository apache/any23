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

import org.deri.any23.util.ReaderInputStream;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.datatypes.XMLDatatypeUtil;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.ParseLocationListener;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.helpers.RDFParserBase;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * <i>N-Quads</i> parser implementation based on the
 * {@link org.openrdf.rio.RDFParser} interface.
 * See the format specification <a href="http://sw.deri.org/2008/07/n-quads/">here</a>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @see org.openrdf.rio.RDFParser
 */
public class NQuadsParser extends RDFParserBase {

    /**
     * Location listener acquired when parsing started.
     */
    private ParseLocationListener locationListener;

    /**
     * RDF handler acquired when parsing started.
     */
    private RDFHandler rdfHandler;

    /**
     * Current row, col and marker trackers.
     */
    private int row, col, mark;

    public NQuadsParser() {}

    public RDFFormat getRDFFormat() {
        return NQuads.FORMAT;
    }

    public void parse(Reader reader, String s)
    throws IOException, RDFParseException, RDFHandlerException {
        ReaderInputStream readerInputStream = new ReaderInputStream(reader);
        parse(readerInputStream, s);
    }

    public synchronized void parse(InputStream is, String baseURI)
    throws IOException, RDFParseException, RDFHandlerException {
        if(is == null) {
            throw new NullPointerException("inputStream cannot be null.");
        }
        if(baseURI == null) {
            throw new NullPointerException("baseURI cannot be null.");
        }

        try {
            row = col = 1;

            locationListener = getParseLocationListener();
            rdfHandler = getRDFHandler();

            setBaseURI(baseURI);

            BufferedInputStream bis = new BufferedInputStream(is);
            if( rdfHandler != null ) {
                rdfHandler.startRDF();
            }
            while( parseLine(bis) ) {
                nextRow();
            }
        } finally {
            if(rdfHandler != null) {
                rdfHandler.endRDF();
            }
            clear();
            clearBNodeIDMap();
        }
    }

    /**
     * Moves to the next row, resets the column.
     */
    private void nextRow() {
        col = 0;
        row++;
        if(locationListener != null) {
            locationListener.parseLocationUpdate(row, col);
        }
    }

    /**
     * Moves to the next column.
     */
    private void nextCol() {
        col++;
        if(locationListener != null) {
            locationListener.parseLocationUpdate(row, col);
        }
    }

    /**
     * Reads the next char.
     *
     * @param bis
     * @return the next read char.
     * @throws IOException
     */
    private char readChar(BufferedInputStream bis) throws IOException {
        nextCol();
        return (char) bis.read();
    }

    /**
     * Marks the buffered input stream with the current location.
     *
     * @param bis
     */
    private void mark(BufferedInputStream bis) {
        mark = col;
        bis.mark(Integer.MAX_VALUE);
    }

    /**
     * Resets the buffered input stream and update the new location.
     * 
     * @param bis
     * @throws IOException
     */
    private void reset(BufferedInputStream bis) throws IOException {
        col = mark;
        bis.reset();
        if(locationListener != null) {
            locationListener.parseLocationUpdate(row, col);
        }
    }

    /**
     * Asserts to read a specific char.
     *
     * @param bis
     * @param c
     * @throws IOException
     */
    private void assertChar(BufferedInputStream bis, char c) throws IOException {
        if( readChar(bis) != c) {
            throw new IllegalArgumentException(
                    String.format("Unexpected char at location %s %s, expected '%s'", row, col, c)
            );
        }
    }

    /**
     * Parsers an <i>NQuads</i> line.
     * 
     * @param bis
     * @return <code>true</code> if the parsing completed, <code>false</code> otherwise.
     * @throws IOException
     * @throws RDFParseException
     * @throws RDFHandlerException
     */
    private boolean parseLine(BufferedInputStream bis)
    throws IOException, RDFParseException, RDFHandlerException {

        char c;

        // Check if the end of stream has been reached.
        mark(bis);
        c = readChar(bis);
        if(c == (char) -1) {
            return false;
        } else {
            reset(bis);
        }

        consumeSpaces(bis);

        mark(bis);
        c = readChar(bis);
        if(c == '\n') {
            return true;
        } else {
            reset(bis);
        }

        Resource sub = parseSubject(bis);
        consumeSpaces(bis);
        URI pred = parsePredicate(bis);
        consumeSpaces(bis);
        Value obj = parseObject(bis);
        consumeSpaces(bis);
        URI graph = parseGraph(bis);
        consumeSpaces(bis);
        parseDot(bis);
        consumeSpaces(bis);

        Statement statement = createStatement(sub, pred, obj, graph);
        if(rdfHandler != null) {
            try {
                rdfHandler.handleStatement(statement);
            } catch (RDFHandlerException rdfhe) {
                reportFatalError(rdfhe);
                throw rdfhe;
            }
        }

        return readChar(bis) == '\n';
    }

    /**
     * Consumes spaces until a non space char is detected.
     * 
     * @param bis
     * @throws IOException
     */
    private void consumeSpaces(BufferedInputStream bis) throws IOException {
        char c;
        do {
            mark(bis);
            c = readChar(bis);
        } while (c == ' ' || c == '\r' || c == '\f' || c == '\t');
        reset(bis);
    }

    /**
     * Consumes the dot at the end of NQuads line.
     * 
     * @param bis
     * @throws IOException
     */
    private void parseDot(BufferedInputStream bis) throws IOException {
        assertChar(bis, '.');
    }

    /**
     * Parses a URI encosed within &lt; and &gt; brackets.
     * @param bis
     * @return the parsed URI.
     * @throws IOException
     * @throws RDFParseException
     */
    private URI parseURI(BufferedInputStream bis) throws IOException, RDFParseException {
        assertChar(bis, '<');

        StringBuilder sb = new StringBuilder();
        char c;
        while(true) {
            c = readChar(bis);
            if(c != '>') {
                sb.append(c);
            } else {
                break;
            }
        }
        mark(bis);

        try {
            String uriStr = sb.toString();
            URI uri;
            if(uriStr.charAt(0) == '#') {
                uri = resolveURI(uriStr);
            } else {
                uri = createURI(uriStr);
            }
            return uri;
        } catch (RDFParseException rdfpe) {
            reportFatalError(rdfpe, row, col);
            throw rdfpe;
        }
    }

    /**
     * Parses a BNode.
     * 
     * @param bis the buffered input stream.
     * @return the generated bnode.
     * @throws IOException
     * @throws RDFParseException
     */
    private BNode parseBNode(BufferedInputStream bis) throws IOException, RDFParseException {
        assertChar(bis, '_');
        assertChar(bis, ':');

        char c;
        StringBuilder sb = new StringBuilder();
        while(true) {
            c = readChar(bis);
            if(c != ' ') {
                sb.append(c);
                mark(bis);
            } else {
                break;
            }
        }
        reset(bis);

        try {
            return createBNode( sb.toString() );
        } catch (RDFParseException rdfpe) {
            reportFatalError(rdfpe, row, col);
            throw rdfpe;
        }
    }

    /**
     * Parses a literal attribute that can be either the language or the data type.
     * 
     * @param bis
     * @return the literal attribute.
     * @throws IOException
     */
    private LiteralAttribute parseLiteralAttribute(BufferedInputStream bis) throws IOException {
        char c = readChar(bis);
        if(c == ' ') {
            reset(bis);
            return null;
        }

        boolean isLang = true;
        if(c == '^') {
            isLang = false;
            assertChar(bis, '^');
        }

        // Consuming eventual open URI.
        mark(bis);
        c = readChar(bis);
        if(c != '<') {
            reset(bis);
        }

        StringBuilder sb = new StringBuilder();
        while(true) {
            c = readChar(bis);
            if(c == '>') {
                mark(bis);
                continue;
            }
            if(c != ' ') {
                mark(bis);
                sb.append(c);
            } else {
                break;
            }
        }
        reset(bis);
        return new LiteralAttribute( isLang, sb.toString() );
    }

    /**
     * Validates and normalize the value of a literal on the basis of the datat ype handling policy and
     * the associated data type.
     *
     * @param value
     * @param datatype
     * @return the normalized data type. It depends on the data type handling policy and the specified data type.
     * @throws RDFParseException
     */
    private String validateAndNormalizeLiteral(String value, URI datatype) throws RDFParseException {
        DatatypeHandling dh = datatypeHandling();
        if(dh.equals( DatatypeHandling.IGNORE )) {
            return value;
        }

        if ( dh.equals(DatatypeHandling.VERIFY) ) {
            if( ! XMLDatatypeUtil.isBuiltInDatatype(datatype)){
                throw new RDFParseException( String.format("Unsupported datatype %s", datatype), row, col);
            }
            if( ! XMLDatatypeUtil.isValidValue(value, datatype) ) {
                throw new RDFParseException(
                        String.format("Illegal literal value '%s' with datatype %s", value, datatype.stringValue() ),
                        row, col
                );
            }
            return value;
        } else if( dh.equals(DatatypeHandling.NORMALIZE) ) {
            return XMLDatatypeUtil.normalize(value, datatype);
        } else {
            throw new IllegalArgumentException( String.format("Unsupported datatype handling: %s", dh) );
        }
    }

    /**
     * Parses a literal.
     *
     * @param bis
     * @return the parsed literal.
     * @throws IOException
     * @throws RDFParseException
     */
    private Value parseLiteral(BufferedInputStream bis) throws IOException, RDFParseException {
        assertChar(bis, '"');

        char c;
        boolean escaped = false;
        boolean justEscaped = false;
        StringBuilder sb = new StringBuilder();
        while(true) {
            c = readChar(bis);
            if( c == '\\' ) {
                escaped = true;
                continue;
            } else if(escaped) {
                escaped = false;
                justEscaped = true;
                sb.append(c);
            }
            if(justEscaped) {
                justEscaped = false;
                continue;
            }
            if(c != '"') {
                sb.append(c);
            } else {
                break;
            }
        }
        mark(bis);

        LiteralAttribute lt = parseLiteralAttribute(bis);

        final String value = sb.toString();
        if(lt == null) {
            return createLiteral(value, null, null);
        }else if(lt.isLang) {
            return createLiteral(
                    value,
                    lt.value,
                    null
            );
        } else {
            URI literalType = null;
            try {
                literalType = new URIImpl(lt.value);
            } catch (Exception e) {
                reportError( String.format("Error while parsing literal type '%s'", lt.value), row, col );
            }
            return createLiteral(
                    validateAndNormalizeLiteral(value, literalType),
                    null,
                    literalType
            );
        }
    }

    /**
     * Parses the subject sequence.
     *
     * @param bis
     * @return the corresponding URI object.
     * @throws IOException
     * @throws RDFParseException
     */
    private Resource parseSubject(BufferedInputStream bis) throws IOException, RDFParseException {
        mark(bis);
        char c = readChar(bis);
        reset(bis);
        if( c == '<' ) {
            return parseURI(bis);
        } else {
            return parseBNode(bis);
        }
    }

    /**
     * Parses the predicate URI.
     *
     * @param bis
     * @return the corresponding URI object.
     * @throws IOException
     * @throws RDFParseException
     */
    private URI parsePredicate(BufferedInputStream bis) throws IOException, RDFParseException {
        return parseURI(bis);
    }

    /**
     * Parses the the object sequence.
     *
     * @param bis
     * @return the corresponding URI object.
     * @throws IOException
     * @throws RDFParseException
     */
    private Value parseObject(BufferedInputStream bis) throws IOException, RDFParseException {
        mark(bis);
        char c = readChar(bis);
        reset(bis);
        if( c == '<' ) {
            return parseURI(bis);
        } else if( c == '_') {
            return parseBNode(bis);
        } else {
            return parseLiteral(bis);
        }
    }

    /**
     * Represents a literal with its attribute value that can be either a language or a data type.
     */
    class LiteralAttribute {
        final boolean isLang;
        final String value;

        LiteralAttribute(boolean lang, String value) {
            isLang = lang;
            this.value = value;
        }
    }

    /**
     * Parses the graph URI.
     *
     * @param bis
     * @return the corresponding URI object.
     * @throws IOException
     * @throws RDFParseException
     */
    private URI parseGraph(BufferedInputStream bis) throws IOException, RDFParseException {
        return parseURI(bis);
    }

}
