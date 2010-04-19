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

package org.deri.any23.extractor.rdf;

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

    private static RDFFormat RDF_FORMAT =
            RDFFormat.register("N-Quads", "text/nquads", "nq", Charset.forName("UTF-8"));

    private ParseLocationListener locationListener;

    private RDFHandler rdfHandler;

    private int row, col, mark;

    public NQuadsParser() {}

    public RDFFormat getRDFFormat() {
        return RDF_FORMAT;
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

    private void nextRow() {
        col = 0;
        row++;
        if(locationListener != null) {
            locationListener.parseLocationUpdate(row, col);
        }
    }

    private void nextCol() {
        col++;
        if(locationListener != null) {
            locationListener.parseLocationUpdate(row, col);
        }
    }

    private char readChar(BufferedInputStream bis) throws IOException {
        nextCol();
        return (char) bis.read();
    }

    private void mark(BufferedInputStream bis) {
        mark = col;
        bis.mark(Integer.MAX_VALUE);
    }

    private void reset(BufferedInputStream bis) throws IOException {
        col = mark;
        bis.reset();
    }

    private boolean parseLine(BufferedInputStream bis)
    throws IOException, RDFParseException, RDFHandlerException {
        consumeSpaces(bis);

        mark(bis);
        char c = readChar(bis);
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

    private void consumeSpaces(BufferedInputStream bis) throws IOException {
        char c;
        do {
            mark(bis);
            c = readChar(bis);
        } while (c == ' ' || c ==  '\t');
        reset(bis);
    }

    private void parseDot(BufferedInputStream bis) throws IOException {
        assertChar(bis, '.');
    }                                                                                           

    private void assertChar(BufferedInputStream bis, char c) throws IOException {
        if( readChar(bis) != c) {
            throw new IllegalArgumentException(
                    String.format("Unexpected char at location %s %s, expected '%s'", row, col, c)
            );
        }
    }

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

        StringBuilder sb = new StringBuilder();
        while(true) {
            c = readChar(bis);
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

    private URI parseGraph(BufferedInputStream bis) throws IOException, RDFParseException {
        return parseURI(bis);
    }

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

    private URI parsePredicate(BufferedInputStream bis) throws IOException, RDFParseException {
        return parseURI(bis);
    }

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

    class LiteralAttribute {
        final boolean isLang;
        final String value;

        LiteralAttribute(boolean lang, String value) {
            isLang = lang;
            this.value = value;
        }
    }

}
