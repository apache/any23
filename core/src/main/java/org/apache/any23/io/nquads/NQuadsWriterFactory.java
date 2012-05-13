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

import org.kohsuke.MetaInfServices;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;

import java.io.OutputStream;
import java.io.Writer;

/**
 * Implementation of {@link RDFWriterFactory} for <code>NQuads</code>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@MetaInfServices
public class NQuadsWriterFactory implements RDFWriterFactory {

    @Override
    public RDFFormat getRDFFormat() {
        return NQuads.FORMAT;
    }

    @Override
    public RDFWriter getWriter(OutputStream outputStream) {
        return new NQuadsWriter(outputStream);
    }

    @Override
    public RDFWriter getWriter(Writer writer) {
        return new NQuadsWriter(writer);
    }

}
