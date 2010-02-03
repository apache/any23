/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.writer;

import java.io.OutputStream;

/**
 * <i>N3</i> notation writer.
 */
public class TurtleWriter extends RDFWriterTripleHandler implements FormatWriter {

    private final boolean useN3;

    /**
     * Constructor.
     *
     * @param out stream to write on.
     */
    public TurtleWriter(OutputStream out) {
        this(out, false);
    }

    public TurtleWriter(OutputStream out, boolean useN3) {
        super(new org.openrdf.rio.turtle.TurtleWriter(out));
        this.useN3 = useN3;
    }

    public String getMIMEType() {
        return useN3 ? "text/rdf+n3;charset=utf-8" : "text/turtle";
    }
    
}
