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
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import org.apache.any23.extractor.html.TagSoupParser;
import org.apache.any23.http.DefaultHTTPClient;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.source.FileDocumentSource;
import org.apache.any23.source.HTTPDocumentSource;
import org.apache.any23.util.StreamUtils;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Command line <i>Microdata</i> parser, accepting both files and URLs and
 * returing a <i>JSON</i> representation of the extracted metadata as described at
 * <a href="http://www.w3.org/TR/microdata/#json">Microdata JSON Specification</a>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@Parameters( commandNames = { "microdata" },  commandDescription = "Commandline Tool for extracting Microdata from file/HTTP source.")
public class MicrodataParser extends BaseTool {

    private static final Pattern HTTP_DOCUMENT_PATTERN = Pattern.compile("^https?://.*");

    private static final Pattern FILE_DOCUMENT_PATTERN = Pattern.compile("^file:(.*)$");

    @Parameter(
       arity = 1,
       description = "Input document URL, {http://path/to/resource.html|file:/path/to/localFile.html}",
       converter = MicrodataParserDocumentSourceConverter.class
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
        InputStream documentInputInputStream = null;
        try {
            final DocumentSource documentSource = document.get(0);
            documentInputInputStream = documentSource.openInputStream();
            final TagSoupParser tagSoupParser = new TagSoupParser(
                    documentInputInputStream,
                    documentSource.getDocumentIRI()
            );
            org.apache.any23.extractor.microdata.MicrodataParser.getMicrodataAsJSON(tagSoupParser.getDOM(), out);
        } finally {
            if (documentInputInputStream != null) StreamUtils.closeGracefully(documentInputInputStream);
        }
    }

    public static final class MicrodataParserDocumentSourceConverter implements IStringConverter<DocumentSource> {

        @Override
        public DocumentSource convert( String value ) {
            final Matcher httpMatcher = HTTP_DOCUMENT_PATTERN.matcher(value);
            if (httpMatcher.find()) {
                try {
                    return new HTTPDocumentSource(DefaultHTTPClient.createInitializedHTTPClient(), value);
                } catch ( URISyntaxException e ) {
                    throw new ParameterException("Invalid source IRI: '" + value + "'");
                }
            }
            final Matcher fileMatcher = FILE_DOCUMENT_PATTERN.matcher(value);
            if (fileMatcher.find()) {
                return new FileDocumentSource( new File( fileMatcher.group(1) ) );
            }
            throw new ParameterException("Invalid source protocol: '" + value + "'");
        }

    }

}
