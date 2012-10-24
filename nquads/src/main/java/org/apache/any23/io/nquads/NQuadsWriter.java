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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * A UTF-8 aware <i>N-Quads</i> implementation of an {@link org.openrdf.rio.RDFWriter}.
 * See the format specification <a href="http://sw.deri.org/2008/07/n-quads/">here</a>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class NQuadsWriter extends org.openrdf.rio.nquads.NQuadsWriter {

    public NQuadsWriter(OutputStream os) {
        super( new OutputStreamWriter(os, Charset.forName("UTF-8")) );
    }

    public NQuadsWriter(Writer writer) {
        super( writer );
    }
}
