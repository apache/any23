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

package org.apache.any23.source;

import java.io.IOException;
import java.io.InputStream;

/**
 * A source of input streams. Mostly intended for
 * situations where opening of an input stream is
 * to be delayed.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public interface DocumentSource {

    /**
     * Returns the input stream for accessing the content of the document.
     *
     * @return not <code>null</code> input stream for accessing document data.
     * @throws IOException if there is an error opening the
     * {@link org.apache.any23.source.DocumentSource} {@link java.io.InputStream}
     */
    InputStream openInputStream() throws IOException;

    /**
     * @return a string describing the content type of the provided document.
     */
    public String getContentType();

    /**
     * @return the size of the content length in bytes.
     */
    public long getContentLength();

    /**
     * @return the actual, final, canonical IRI if redirects occur.
     */
    public String getDocumentIRI();

    /**
     * A value of <i>false</i> indicates that the document
     * resides remotely, and that multiple successive accesses
     * to it should be avoided by copying it to local storage.
     * This can also be used for sources that do not support
     * multiple calls to {@link #openInputStream()}.
     * @return true if the {@link org.apache.any23.source.DocumentSource} is
     * cached locally.
     */
    public boolean isLocal();
}
