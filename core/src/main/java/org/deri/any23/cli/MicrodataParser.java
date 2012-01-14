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

import org.deri.any23.extractor.html.TagSoupParser;
import org.deri.any23.http.DefaultHTTPClient;
import org.deri.any23.source.DocumentSource;
import org.deri.any23.source.FileDocumentSource;
import org.deri.any23.source.HTTPDocumentSource;
import org.deri.any23.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Command line <b>Microdata</i> parser, accepting both files and URLs and
 * returing a <i>JSON</i> representation of the extracted metadata as described at
 * <a href="http://www.w3.org/TR/microdata/#json">Microdata JSON Specification</a>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@ToolRunner.Description("Commandline Tool for extracting Microdata from file/HTTP source.")
public class MicrodataParser implements Tool {

    private static final String HTTP_DOCUMENT_SOURCE = "^https?://.*";
    private static final String FILE_DOCUMENT_SOURCE = "^file:(.*)$";

    public static void main(String[] args) throws URISyntaxException, IOException {
        System.exit( new MicrodataParser().run(args) );
    }

    public int run(String[] args) {
        if(args.length != 1) {
            System.err.println("USAGE: {http://path/to/resource.html|file:/path/to/local.file}");
            return 1;
        }
        InputStream documentInputInputStream = null;
        try {
            final DocumentSource documentSource = getDocumentSource(args[0]);
            documentInputInputStream = documentSource.openInputStream();
            final TagSoupParser tagSoupParser = new TagSoupParser(
                    documentInputInputStream,
                    documentSource.getDocumentURI()
            );
            org.deri.any23.extractor.microdata.MicrodataParser.getMicrodataAsJSON(tagSoupParser.getDOM(), System.out);
        } catch (Exception e) {
            System.err.println("***ERROR: " + e.getMessage());
            e.printStackTrace();
            return 1;
        } finally {
            if(documentInputInputStream != null) StreamUtils.closeGracefully(documentInputInputStream);
        }
        return 0;
    }

    private DocumentSource getDocumentSource(String source) throws URISyntaxException {
        final Matcher httpMatcher = Pattern.compile(HTTP_DOCUMENT_SOURCE).matcher(source);
        if(httpMatcher.find()) {
            return new HTTPDocumentSource(new DefaultHTTPClient(), source);
        }
        final Matcher fileMatcher = Pattern.compile(FILE_DOCUMENT_SOURCE).matcher(source);
        if(fileMatcher.find()) {
            return new FileDocumentSource( new File( fileMatcher.group(1) ) );
        }
        throw new IllegalArgumentException("Invalid source protocol: '" + source + "'");
    }

}
