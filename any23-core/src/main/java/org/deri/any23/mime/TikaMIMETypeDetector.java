/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.mime;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.turtle.TurtleParser;
import org.openrdf.model.Statement;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * Implementation of {@link org.deri.any23.mime.MIMETypeDetector} based on
 * <a href="http://lucene.apache.org/tika/">Apache Tika</a>.
 *
 * @author Michele Mostarda (michele.mostarda@gmail.com)
 */
public class TikaMIMETypeDetector implements MIMETypeDetector {

    private static final String RESOURCE_NAME = "/org/deri/any23/mime/tika-config.xml";

    /**
     * N3 triple pattern.
     */
    private static final Pattern triplePattern        = Pattern.compile("<.*>\\s*<.*>\\s*<.*>\\s*\\."  );

    /**
     * N3 triple literal pattern.
     */
    private static final Pattern tripleLiteralPattern = Pattern.compile("<.*>\\s*<.*>\\s*\".*\"\\s*\\.");

    private static final FakeRDFHandler FAKE_RDF_HANDLER = new FakeRDFHandler();

    private static TikaConfig config = null;
    private static MimeTypes types;

    /**
     * Checks if the stream contains <i>N3</i> triple patterns.
     *
     * @param is input stream to be verified.
     * @return <code>true</code> if <i>N3</i> patterns are detected, <code>false</code> otherwise.
     * @throws IOException
     */
    public static boolean checkN3Format(InputStream is) throws IOException {
        String sample = extractDataSample(is);
        return triplePattern.matcher(sample).find() || tripleLiteralPattern.matcher(sample).find();
    }

    /**
     * Checks if the stream contains <i>Turtle</i> triple patterns.
     *
     * @param is input stream to be verified.
     * @return <code>true</code> if <i>Turtle</i> patterns are detected, <code>false</code> otherwise.
     * @throws IOException
     */
    public static boolean checkTurtleFormat(InputStream is) throws IOException {
        String sample = extractDataSample(is);
        TurtleParser turtleParser = new TurtleParser();
        turtleParser.setDatatypeHandling(RDFParser.DatatypeHandling.VERIFY);
        turtleParser.setStopAtFirstError(true);
        turtleParser.setVerifyData(true);
        turtleParser.setRDFHandler(FAKE_RDF_HANDLER);
        ByteArrayInputStream bais = new ByteArrayInputStream( sample.getBytes() );
        try {
            turtleParser.parse(bais, "");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String extractDataSample(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        final int MAX_SIZE = 1024 * 2;
        int c;
        boolean insideBlock = false;
        int read = 0;
        br.mark(MAX_SIZE);
        try {
            while ((c = br.read()) != -1) {
                read++;
                if (read > MAX_SIZE) {
                    break;
                }
                if ('<' == c) {
                    insideBlock = true;
                } else if ('>' == c) {
                    insideBlock = false;
                } else if ('"' == c) {
                    insideBlock = !insideBlock;
                }
                sb.append((char) c);
                if (!insideBlock && '.' == c) {
                    break;
                }
            }
        } finally {
            is.reset();
            br.reset();
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        new TikaMIMETypeDetector();
    }

    private final Tika tika = new Tika();

    public TikaMIMETypeDetector() {
        InputStream is = getResourceAsStream();
        if (config == null)
            try {
                config = new TikaConfig(is);
            } catch (Exception e) {
                throw new RuntimeException("Error while loading Tika configuration.", e);
            }
        if (types == null)
            types = config.getMimeRepository();
    }

    public MIMEType guessMIMEType(
            String fileName,
            InputStream input,
            MIMEType mimeTypeFromMetadata
    ) {

        Metadata meta = new Metadata();
        if (mimeTypeFromMetadata != null)
            meta.set(Metadata.CONTENT_TYPE, mimeTypeFromMetadata.getFullType());
        if (fileName != null)
            meta.set(Metadata.RESOURCE_NAME_KEY, fileName);

        String type;
        try {
            String mt = getMimeType(input, meta);
            if( !MimeTypes.OCTET_STREAM.equals(mt) ) {
                type = mt;
            } else {
                if( checkN3Format(input) ) {
                    type = "text/n3";
                } else if( checkTurtleFormat(input) ) {
                    type = "application/turtle";
                } else {
                    type = MimeTypes.OCTET_STREAM; 
                }
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Error while retrieving mime type.", ioe);
        }
        return MIMEType.parse(type);
    }

     /**
      * Loads the <code>Tika</code> configuration file.
      *
      * @return the input stream containing the configuration.
      */
     private InputStream getResourceAsStream() {
         InputStream result;
         result = TikaMIMETypeDetector.class.getResourceAsStream(RESOURCE_NAME);
         if (result == null) {
             result = TikaMIMETypeDetector.class.getClassLoader().getResourceAsStream(RESOURCE_NAME);
             if (result == null) {
                 result = ClassLoader.getSystemResourceAsStream(RESOURCE_NAME);
             }
         }
         return result;
     }

    /**
     * Automatically detects the MIME type of a document based on magic
     * markers in the stream prefix and any given metadata hints.
     * <p/>
     * The given stream is expected to support marks, so that this method
     * can reset the stream to the position it was in before this method
     * was called.
     *
     * @param stream   document stream
     * @param metadata metadata hints
     * @return MIME type of the document
     * @throws IOException if the document stream could not be read
     */
    private String getMimeType(InputStream stream, final Metadata metadata) throws IOException {
        if (stream != null) {
            final String type = tika.detect(stream);
            if (
                    type != null
                            &&
                    !type.equals(MimeTypes.OCTET_STREAM)
                            &&
                    !type.equals(MimeTypes.PLAIN_TEXT)
            ) {
                return type;
            }
        }

        // Get type based on metadata hint (if available).
        String typename = metadata.get(Metadata.CONTENT_TYPE);
        if (typename != null) {
            try {
                MimeType type = types.forName(typename);
                if (type != null && !type.toString().equals(MimeTypes.OCTET_STREAM)) {
                    return type.toString();
                }
            }
            catch (MimeTypeException mte) {
                // Malformed type name, ignore.
            }
        }

        // Get type based on resourceName hint (if available)
        String resourceName = metadata.get(Metadata.RESOURCE_NAME_KEY);
        if (resourceName != null) {
            MimeType type = types.getMimeType(resourceName);
            if (type != null) {
                return type.toString();
            }
        }

        // Finally, use the default type if no matches found
        try {
            return types.forName(MimeTypes.OCTET_STREAM).toString();
        } catch (MimeTypeException e) {
            // Should never happen
            return null;
        }
    }

    /**
     * Fake implementation of {@link org.openrdf.rio.RDFHandler}.
     */
    private static class FakeRDFHandler implements RDFHandler {

        public void startRDF() throws RDFHandlerException {}

        public void endRDF() throws RDFHandlerException {}

        public void handleNamespace(String s, String s1) throws RDFHandlerException {}

        public void handleStatement(Statement statement) throws RDFHandlerException {}

        public void handleComment(String s) throws RDFHandlerException {}
        
    }

}

