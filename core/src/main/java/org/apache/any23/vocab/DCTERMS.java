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

package org.apache.any23.vocab;

import org.openrdf.model.URI;

/**
 * The <i>DCTERMS</code> vocabulary.
 * See <a href="http://dublincore.org/">Dublin Core</a>.
 */
public class DCTERMS extends Vocabulary {

    public static final String NS = "http://purl.org/dc/terms/";

    private static DCTERMS instance;

    public static DCTERMS getInstance() {
        if(instance == null) {
            instance = new DCTERMS();
        }
        return instance;
    }

    // Properties
    public final URI license = createProperty(NS, "license");
    public final URI title   = createProperty(NS, "title"  );
    public final URI creator = createProperty(NS, "creator");
    public final URI related = createProperty(NS, "related");
    public final URI date    = createProperty(NS, "date"   );
    public final URI source  = createProperty(NS, "source" );

    private DCTERMS(){
        super(NS);
    }
}
