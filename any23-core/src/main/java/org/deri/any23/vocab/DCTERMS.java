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

package org.deri.any23.vocab;

import org.openrdf.model.URI;

/**
 * The <i>DCTERMS</code> vocabulary.
 * See <a href="http://dublincore.org/">Dublin Core</a>.
 */
public class DCTERMS extends Vocabulary {

    public static final String NS = "http://purl.org/dc/terms/";

    // Properties
    public static final URI license = createURI(NS, "license");
    public static final URI title   = createURI(NS, "title"  );
    public static final URI creator = createURI(NS, "creator");
    public static final URI related = createURI(NS, "related");
    public static final URI date    = createURI(NS, "date"   );
    public static final URI source  = createURI(NS, "source" );

    private DCTERMS(){}
}
