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
 * The <a href="http://www.w3.org/1999/xhtml/vocab/">XHTML</a> vocabulary.
 */
public class XHTML extends Vocabulary {

    public static final String NS = "http://www.w3.org/1999/xhtml/vocab#";

    public static final URI license    = createURI(NS, "license"   );
    public static final URI meta       = createURI(NS, "meta"      );
    public static final URI alternate  = createURI(NS, "alternate" );
    public static final URI stylesheet = createURI(NS, "stylesheet");

    private XHTML(){}
    
}
