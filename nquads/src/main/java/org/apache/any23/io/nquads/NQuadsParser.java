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
import org.openrdf.rio.helpers.NTriplesParserSettings;
import org.openrdf.rio.helpers.RDFParserBase;
import org.openrdf.rio.ntriples.NTriplesUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

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
        return RDFFormat.NQUADS;
    }

    public void parse(Reader reader, String baseURI)
    throws IOException, RDFParseException, RDFHandlerException {
        if(reader == null) {
            throw new NullPointerException("reader cannot be null.");
        }
        if(baseURI == null) {
            throw new NullPointerException("baseURI cannot be null.");
        }
        
        try {
            row = col = 1;

            locationListener = getParseLocationListener();
            rdfHandler = getRDFHandler();

            setBaseURI(baseURI);

            final BufferedReader br = new BufferedReader( reader );
            if( rdfHandler != null ) {
                rdfHandler.startRDF();
            }
            while( parseLine(br) ) {
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

    public synchronized void parse(InputStream is, String baseURI)
    throws IOException, RDFParseException, RDFHandlerException {
        if(is == null) {
            throw new NullPointerException("inputStream cannot be null.");
        }
        if(baseURI == null) {
            throw new NullPointerException("baseURI cannot be null.");
        }
        
        // NOTE: Sindice needs to be able to support UTF-8 native N-Quads documents, so the charset cannot be US-ASCII
        this.parse(new InputStreamReader(is, Charset.forName("UTF-8")), baseURI);
    }

    /**
     * Moves to the next row, resets the column.
     */
    private void nextRow() {
        col = 1;
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
     * @param br
     * @return the next read char.
     * @throws IOException
     */
    private char readChar(BufferedReader br) throws IOException {
        final int c = br.read();
        if(c == -1) {
            throw new EOS();
        }
        nextCol();
        return (char) c;
    }

    /**
     * Reads an unicode char with pattern <code>\\uABCD</code>.
     *
     * @param br input reader.
     * @return read char.
     * @throws IOException
     * @throws RDFParseException
     */
    private char readUnicode(BufferedReader br) throws IOException, RDFParseException {
        final char[] unicodeSequence = new char[4];
        for(int i = 0; i < unicodeSequence.length; i++) {
            unicodeSequence[i] = readChar(br);
        }
        final String unicodeCharStr = new String(unicodeSequence);
        try {
            return (char) Integer.parseInt(unicodeCharStr, 16);
        } catch (NumberFormatException nfe) {
            reportError("Error while converting unicode char '\\u" + unicodeCharStr + "'", row, col, NTriplesParserSettings.IGNORE_NTRIPLES_INVALID_LINES);
            throw new IllegalStateException();
        }
    }

    /**
     * Marks the buffered input stream with the current location.
     *
     * @param br
     */
    private void mark(BufferedReader br) throws IOException {
        mark = col;
        br.mark(5);
    }

    /**
     * Resets the buffered input stream and update the new location.
     *
     * @param br
     * @throws IOException
     */
    private void reset(BufferedReader br) throws IOException {
        col = mark;
        br.reset();
        if(locationListener != null) {
            locationListener.parseLocationUpdate(row, col);
        }
    }

    /**
     * Asserts to read a specific char.
     *
     * @param br
     * @param c
     * @throws IOException
     */
    private void assertChar(BufferedReader br, char c) throws IOException, RDFParseException {
        final char read = readChar(br);
        if(read != c) {
            throw new RDFParseException(
                    String.format("Unexpected char '%s', expected '%s'", read, c),
                    row, col
            );
        }
    }

    /**
     * Consumes the reader until the next carriage return.
     *
     * @param br
     * @throws IOException
     */
    private void consumeBrokenLine(BufferedReader br) throws IOException {
        char c;
        while (true) {
            mark(br);
            c = readChar(br);
            if (c == '\n') {
                return;
            }
        }
    }

    /**
     * Parsers an <i>NQuads</i> line.
     *
     * @param br input stream reader containing NQuads.
     * @return <code>false</code> if the parsing completed, <code>true</code> otherwise.
     * @throws IOException
     * @throws RDFParseException
     * @throws RDFHandlerException
     */
    private boolean parseLine(BufferedReader br)
    throws IOException, RDFParseException, RDFHandlerException {

        if(!consumeSpacesAndNotEOS(br)) {
            return false;
        }

        // Consumes empty line or line comment.
        try {
            if(consumeEmptyLine(br)) return true;
            if( consumeComment(br) ) return true;
        } catch (EOS eos) {
            return false;
        }

        final Resource sub;
        final URI      pred;
        final Value    obj;
        final URI      context;
        try {
            sub = parseSubject(br);
            consumeSpaces(br);
            pred = parsePredicate(br);
            consumeSpaces(br);
            obj = parseObject(br);
            consumeSpaces(br);
            context = parseContextAndOrDot(br);
        } catch (EOS eos) {
            reportFatalError("Unexpected end of stream.", row, col);
            throw new IllegalStateException();
        } catch (Exception e) {
            if(super.stopAtFirstError()) {
                if(e instanceof RDFParseException)
                    throw (RDFParseException) e;
                else
                    throw new RDFParseException(e, row, col);
            } else { // Remove rest of broken line and report error.
                consumeBrokenLine(br);
                reportError(e.getMessage(), row, col, NTriplesParserSettings.IGNORE_NTRIPLES_INVALID_LINES);
                return true;
            }
        }

        assert sub  != null : "Subject cannot be null.";
        assert pred != null : "Predicate cannot be null.";
        assert obj  != null : "Object cannot be null.";
        notifyStatement(sub, pred, obj, context);

        if(!consumeSpacesAndNotEOS(br)) {
            return false;
        }
        return readChar(br) == '\n';
    }

    /**
     * Consumes the line if empty (contains just a carriage return).
     *
     * @param br input NQuads stream.
     * @return <code>true</code> if the line is empty.
     * @throws IOException if an error occurs while consuming stream.
     */
    private boolean consumeEmptyLine(BufferedReader br) throws IOException {
        char c;
        mark(br);
        c = readChar(br);
        if (c == '\n') {
            return true;
        } else {
            reset(br);
            return false;
        }
    }

    /**
     * Consumes all subsequent spaces and returns true, if End Of Stream is reached instead returns false.
     * @param br input NQuads stream reader.
     * @return <code>true</code> if there are other chars to be consumed.
     * @throws IOException if an error occurs while consuming stream.
     */
    private boolean consumeSpacesAndNotEOS(BufferedReader br) throws IOException {
        try {
            consumeSpaces(br);
            return true;
        } catch (EOS eos) {
            return false;
        }
    }

    /**
     * Consumes a comment if any.
     *
     * @param br input NQuads stream reader.
     * @return <code>true</code> if comment has been consumed, false otherwise.
     * @throws IOException
     */
    private boolean consumeComment(BufferedReader br) throws IOException {
        char c;
        mark(br);
        c = readChar(br);
        if (c == '#') {
            mark(br);
            while (readChar(br) != '\n');
            mark(br);
            return true;
        } else {
            reset(br);
            return false;
        }
    }

    /**
     * Notifies the parsed statement to the {@link RDFHandler}.
     *
     * @param sub
     * @param pred
     * @param obj
     * @param context
     * @throws RDFParseException
     * @throws RDFHandlerException
     */
    private void notifyStatement(Resource sub, URI pred, Value obj, URI context)
    throws RDFParseException, RDFHandlerException {
        Statement statement = super.createStatement(sub, pred, obj, context);
        if (rdfHandler != null) {
            try {
                rdfHandler.handleStatement(statement);
            } catch (RDFHandlerException rdfhe) {
                reportFatalError(rdfhe);
                throw rdfhe;
            }
        }
    }

    /**
     * Consumes spaces until a non space char is detected.
     *
     * @param br input stream reader from which consume spaces.
     * @throws IOException
     */
    private void consumeSpaces(BufferedReader br) throws IOException {
        char c;
        while(true) {
            mark(br);
            c = readChar(br);
            if(c == ' ' || c == '\r' || c == '\f' || c == '\t') {
                mark(br);
            } else {
                break;
            }
        }
        reset(br);
    }

    /**
     * Consumes the dot at the end of NQuads line.
     *
     * @param br
     * @throws IOException
     */
    private void parseDot(BufferedReader br) throws IOException, RDFParseException {
        assertChar(br, '.');
    }

    /**
     * Parses a URI enclosed within &lt; and &gt; brackets.
     * @param br
     * @return the parsed URI.
     * @throws IOException
     * @throws RDFParseException
     */
    private URI parseURI(BufferedReader br) throws IOException, RDFParseException {
        assertChar(br, '<');

        StringBuilder sb = new StringBuilder();
        char c;
        while(true) {
            c = readChar(br);
            if(c != '>') {
                sb.append(c);
            } else {
                break;
            }
        }
        mark(br);

        try {
            // TODO - LOW: used to unescape \\uXXXX unicode chars. Unify with #printEscaped().
            String uriStr = NTriplesUtil.unescapeString( sb.toString() );
            URI uri;
            if(uriStr.charAt(0) == '#') {
                uri = super.resolveURI(uriStr);
            } else {
                uri = super.createURI(uriStr);
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
     * @param br the buffered input stream.
     * @return the generated bnode.
     * @throws IOException
     * @throws RDFParseException
     */
    private BNode parseBNode(BufferedReader br) throws IOException, RDFParseException {
        assertChar(br, '_');
        assertChar(br, ':');

        char c;
        StringBuilder sb = new StringBuilder();
        while(true) {
            c = readChar(br);
            if(c != ' ' && c != '<') {
                sb.append(c);
                mark(br);
            } else {
                break;
            }
        }
        reset(br);

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
     * @param br
     * @return the literal attribute.
     * @throws IOException
     */
    private LiteralAttribute parseLiteralAttribute(BufferedReader br) throws IOException, RDFParseException {
        char c = readChar(br);
        if(c != '^' && c != '@') {
            reset(br);
            return null;
        }

        boolean isLang = true;
        if(c == '^') {
            isLang = false;
            assertChar(br, '^');
        }

        final String attribute;
        if (isLang) { // Read until space or context begin.
            final StringBuilder sb = new StringBuilder();
            while (true) {
                c = readChar(br);
                if (c != ' ' && c != '<') {
                    mark(br);
                    sb.append(c);
                } else {
                    reset(br);
                    break;
                }
            }
            attribute = sb.toString();
        }  else {
            attribute = parseURI(br).stringValue();
        }

        return new LiteralAttribute(isLang, attribute);
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
                return value;
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
     * Prints the escaped version of the given char c.
     *
     * @param c escaped char.
     * @param sb output string builder.
     */
    private void printEscaped(char c, StringBuilder sb) {
        if(c == 'b') {
            sb.append('\b');
            return;
        }
        if(c == 'f') {
            sb.append('\f');
            return;
        }
        if(c == 'n') {
            sb.append('\n');
            return;
        }
        if(c == 'r') {
            sb.append('\r');
            return;
        }
        if(c == 't') {
            sb.append('\t');
            return;
        }
    }

    /**
     * Parses a literal.
     *
     * @param br
     * @return the parsed literal.
     * @throws IOException
     * @throws RDFParseException
     */
    private Value parseLiteral(BufferedReader br) throws IOException, RDFParseException {
        assertChar(br, '"');

        char c;
        boolean escaped = false;
        StringBuilder sb = new StringBuilder();
        while(true) {
            c = readChar(br);
            if( c == '\\' ) {
                if(escaped) {
                    escaped = false;
                    sb.append(c);
                } else {
                    escaped = true;
                }
                continue;
            } else if(c == '"' && !escaped) {
                break;
            }
            if(escaped) {
                if(c == 'u') {
                    char unicodeChar = readUnicode(br);
                    sb.append(unicodeChar);
                } else {
                    printEscaped(c, sb);
                }
                escaped = false;
            } else {
                sb.append(c);
            }
        }
        mark(br);

        LiteralAttribute lt = parseLiteralAttribute(br);

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
                reportError( String.format("Error while parsing literal type '%s'", lt.value), row, col , NTriplesParserSettings.IGNORE_NTRIPLES_INVALID_LINES);
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
     * @param br
     * @return the corresponding URI object.
     * @throws IOException
     * @throws RDFParseException
     */
    private Resource parseSubject(BufferedReader br) throws IOException, RDFParseException {
        mark(br);
        char c = readChar(br);
        reset(br);
        if( c == '<' ) {
            return parseURI(br);
        } else {
            return parseBNode(br);
        }
    }

    /**
     * Parses the predicate URI.
     *
     * @param br
     * @return the corresponding URI object.
     * @throws IOException
     * @throws RDFParseException
     */
    private URI parsePredicate(BufferedReader br) throws IOException, RDFParseException {
        return parseURI(br);
    }

    /**
     * Parses the the object sequence.
     *
     * @param br
     * @return the corresponding URI object.
     * @throws IOException
     * @throws RDFParseException
     */
    private Value parseObject(BufferedReader br) throws IOException, RDFParseException {
        mark(br);
        char c = readChar(br);
        reset(br);
        if( c == '<' ) {
            return parseURI(br);
        } else if( c == '_') {
            return parseBNode(br);
        } else {
            return parseLiteral(br);
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
     * Parses the context if any.
     *
     * @param br
     * @return the context URI or null if not found.
     * @throws IOException
     * @throws RDFParseException
     */
    private URI parseContextAndOrDot(BufferedReader br) throws IOException, RDFParseException {
        mark(br);
        final char c = readChar(br);
        reset(br);
        if(c == '<') {
            final URI context = parseURI(br);
            consumeSpaces(br);
            parseDot(br);
            return context;
        } else {
            parseDot(br);
            return null;
        }
    }

    /**
     * Defines the End Of Stream exception.
     */
    class EOS extends IOException {}

}
