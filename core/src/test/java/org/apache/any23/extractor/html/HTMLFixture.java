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

package org.apache.any23.extractor.html;

import org.apache.any23.source.DocumentSource;
import org.apache.any23.source.FileDocumentSource;
import org.junit.Assert;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * This class is a wrapper around an HTML document providing a simple facade.
 */
public class HTMLFixture {

    private final File file;

    public HTMLFixture(File file) {
        Assert.assertNotNull("Test resource file was null", file);
        Assert.assertTrue("Test resource file does not exist", file.exists());
        this.file = file;
    }

    private File getFile() {
        return file;
    }

    /**
     * 
     * @param baseIRI the base IRI to use for the DocumentSource
     * @param optimizeMimeTypeAndExtractorSelection if you wish to optimize 
     * SingleDocumentExtraction extractor matching and mimetype detection by 
     * trusting the input DocumentSource content type. See 
     * https://issues.apache.org/jira/projects/ANY23/issues/ANY23-43
     * @return the document source which is actually a {@link FileDocumentSource}
     */
    public DocumentSource getOpener(String baseIRI, Boolean optimizeMimeTypeAndExtractorSelection) {
        if (optimizeMimeTypeAndExtractorSelection) {
            return new FileDocumentSource(getFile(), baseIRI, "text/html");
        } else {
            return new FileDocumentSource(getFile(), baseIRI);
        }
    }

    /**
     * @return the DOM root {@link org.w3c.dom.Node} of the whole document.
     */
    public Node getDOM() {
        try {
            return new TagSoupParser(new FileInputStream(getFile()), "http://example.org/").getDOM();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @return an {@link HTMLDocument} object of the whole HTML document.
     */
    public HTMLDocument getHTMLDocument() {
        return new HTMLDocument(getDOM());
    }
}
