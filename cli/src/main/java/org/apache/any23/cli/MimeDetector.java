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

package org.apache.any23.cli;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.apache.any23.http.DefaultHTTPClient;
import org.apache.any23.http.DefaultHTTPClientConfiguration;
import org.apache.any23.http.HTTPClient;
import org.apache.any23.mime.MIMEType;
import org.apache.any23.mime.MIMETypeDetector;
import org.apache.any23.mime.TikaMIMETypeDetector;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.source.FileDocumentSource;
import org.apache.any23.source.HTTPDocumentSource;
import org.apache.any23.source.StringDocumentSource;

import java.io.File;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

/**
 * Commandline tool to detect <b>MIME Type</b>s from
 * file, HTTP and direct input sources.
 * The implementation of this tool is based on {@link org.apache.any23.mime.TikaMIMETypeDetector}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@Parameters(commandNames = { "mimes" }, commandDescription = "MIME Type Detector Tool.")
public class MimeDetector extends BaseTool {

    public static final String FILE_DOCUMENT_PREFIX   = "file://";

    public static final String INLINE_DOCUMENT_PREFIX = "inline://";

    public static final String URL_DOCUMENT_RE        = "^https?://.*";

    @Parameter(
       arity = 1,
       description = "Input document URL, {http://path/to/resource.html|file:///path/to/local.file|inline:// some inline content}",
       converter = MimeDetectorDocumentSourceConverter.class
    )
    private List<DocumentSource> document = new LinkedList<DocumentSource>();

    private PrintStream out = System.out;

    @Override
    PrintStream getOut() {
        return out;
    }

    @Override
    void setOut(PrintStream out) {
        this.out = out;
    }

    public void run() throws Exception {
        if (document.isEmpty()) {
            throw new IllegalArgumentException("No input document URL specified");
        }

        final DocumentSource documentSource = document.get(0);
        final MIMETypeDetector detector = new TikaMIMETypeDetector();
        final MIMEType mimeType = detector.guessMIMEType(
                documentSource.getDocumentIRI(),
                documentSource.openInputStream(),
                MIMEType.parse(documentSource.getContentType())
        );
        out.println(mimeType);
    }

    public static final class MimeDetectorDocumentSourceConverter implements IStringConverter<DocumentSource> {

        @Override
        public DocumentSource convert( String document ) {
            if (document.startsWith(FILE_DOCUMENT_PREFIX)) {
                return new FileDocumentSource( new File( document.substring(FILE_DOCUMENT_PREFIX.length()) ) );
            }
            if (document.startsWith(INLINE_DOCUMENT_PREFIX)) {
                return new StringDocumentSource( document.substring(INLINE_DOCUMENT_PREFIX.length()), "" );
            }
            if (document.matches(URL_DOCUMENT_RE)) {
                final HTTPClient client = new DefaultHTTPClient();
                client.init( DefaultHTTPClientConfiguration.singleton() );
                try {
                    return new HTTPDocumentSource(client, document);
                } catch ( URISyntaxException e ) {
                    throw new IllegalArgumentException("Invalid source IRI: '" + document + "'");
                }
            }
            throw new IllegalArgumentException("Unsupported protocol for document " + document);
        }

    }

}
