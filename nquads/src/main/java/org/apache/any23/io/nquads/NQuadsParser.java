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

import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * <i>N-Quads</i> parser implementation based on the
 * {@link org.openrdf.rio.RDFParser} interface.
 * See the format specification <a href="http://sw.deri.org/2008/07/n-quads/">here</a>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @see org.openrdf.rio.RDFParser
 */
public class NQuadsParser extends org.openrdf.rio.nquads.NQuadsParser {

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

}
