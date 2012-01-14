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

package org.deri.any23.cli;

import org.deri.any23.configuration.DefaultConfiguration;
import org.deri.any23.http.DefaultHTTPClient;
import org.deri.any23.http.HTTPClient;
import org.deri.any23.http.HTTPClientConfiguration;
import org.deri.any23.mime.MIMEType;
import org.deri.any23.mime.MIMETypeDetector;
import org.deri.any23.mime.TikaMIMETypeDetector;
import org.deri.any23.source.DocumentSource;
import org.deri.any23.source.FileDocumentSource;
import org.deri.any23.source.HTTPDocumentSource;
import org.deri.any23.source.StringDocumentSource;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Commandline tool to detect <b>MIME Type</b>s from
 * file, HTTP and direct input sources.
 * The implementation of this tool is based on {@link TikaMIMETypeDetector}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@ToolRunner.Description("MIME Type Detector Tool.")
public class MimeDetector implements Tool{

    public static final String FILE_DOCUMENT_PREFIX   = "file://";
    public static final String INLINE_DOCUMENT_PREFIX = "inline://";
    public static final String URL_DOCUMENT_RE        = "^https?://.*";

    public static void main(String[] args) {
        System.exit( new MimeDetector().run(args) );
    }

    @Override
    public int run(String[] args) {
          if(args.length != 1) {
            System.err.println("USAGE: {http://path/to/resource.html|file:///path/to/local.file|inline:// some inline content}");
            return 1;
        }

        final String document = args[0];
        try {
            final DocumentSource documentSource = createDocumentSource(document);
            final MIMETypeDetector detector = new TikaMIMETypeDetector();
            final MIMEType mimeType = detector.guessMIMEType(
                    documentSource.getDocumentURI(),
                    documentSource.openInputStream(),
                    MIMEType.parse(documentSource.getContentType())
            );
            System.out.println(mimeType);
            return 0;
        } catch (Exception e) {
            System.err.print("Error while detecting MIME Type.");
            e.printStackTrace(System.err);
            return 1;
        }
    }

    private DocumentSource createDocumentSource(String document) throws URISyntaxException {
        if(document.startsWith(FILE_DOCUMENT_PREFIX)) {
            return new FileDocumentSource(
                    new File(
                            document.substring(FILE_DOCUMENT_PREFIX.length())
                    )
            );
        }
        if(document.startsWith(INLINE_DOCUMENT_PREFIX)) {
            return new StringDocumentSource(
                    document.substring(INLINE_DOCUMENT_PREFIX.length()),
                    ""
            );
        }
        if(document.matches(URL_DOCUMENT_RE)) {
            final HTTPClient client = new DefaultHTTPClient();
            // TODO: anonymous config class also used in Any23. centralize.
            client.init(new HTTPClientConfiguration() {
                public String getUserAgent() {
                    return DefaultConfiguration.singleton().getPropertyOrFail("any23.http.user.agent.default");
                }
                public String getAcceptHeader() {
                    return "";
                }
                public int getDefaultTimeout() {
                    return DefaultConfiguration.singleton().getPropertyIntOrFail("any23.http.client.timeout");
                }
                public int getMaxConnections() {
                    return DefaultConfiguration.singleton().getPropertyIntOrFail("any23.http.client.max.connections");
                }
            });
            return new HTTPDocumentSource(client, document);
        }
        throw new IllegalArgumentException("Unsupported protocol for document " + document);
    }

}
