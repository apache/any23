/*
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
 */

package org.deri.any23.extractor.html;

import org.deri.any23.source.DocumentSource;
import org.deri.any23.source.FileDocumentSource;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * This class is a wrapper around an HTML document providing a simply facade.
 */
public class HTMLFixture {

    private final String filename;

    public HTMLFixture(String filename) {
        this.filename = filename;
    }

    private File getFile() {
        File file = new File(
                System.getProperty("test.data", "src/test/resources/") + filename);
        if (!file.exists())
            throw new AssertionError("the file " + file.getPath() + " does not exist");
        return file;
    }

    public DocumentSource getOpener(String baseURI) {
        return new FileDocumentSource(getFile(), baseURI);
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
     * @return an {@link org.deri.any23.extractor.html.HTMLDocument} object of the whole HTML document.
     */
    public HTMLDocument getHTMLDocument() {
        return new HTMLDocument(getDOM());
    }
}
