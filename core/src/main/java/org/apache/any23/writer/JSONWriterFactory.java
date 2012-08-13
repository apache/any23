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

package org.apache.any23.writer;

import java.io.OutputStream;

import org.kohsuke.MetaInfServices;
import org.openrdf.rio.RDFFormat;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
@MetaInfServices
public class JSONWriterFactory implements WriterFactory {

    public static final String MIME_TYPE = "text/json";
    public static final String IDENTIFIER = "json";

    /**
     * 
     */
    public JSONWriterFactory() {
    }

    @Override
    public RDFFormat getRdfFormat() {
        throw new RuntimeException(
                "TODO: Implement an RDFFormat for this RDF JSON serialisation format");
    }

    @Override
    public String getIdentifier() {
        return JSONWriterFactory.IDENTIFIER;
    }

    @Override
    public String getMimeType() {
        return JSONWriterFactory.MIME_TYPE;
    }

    @Override
    public FormatWriter getRdfWriter(OutputStream os) {
        return new JSONWriter(os);
    }

}
