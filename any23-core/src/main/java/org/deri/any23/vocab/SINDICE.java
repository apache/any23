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
import java.util.HashMap;
import java.util.Map;

/**
 * This class models an internal <i>Sindice</i> Vocabulary to describe
 * resource domains and Microformat nesting relationships.
 * See the <a href="http://developers.any23.org/extraction.html">Any23 extraction notes</a>.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 */
public class SINDICE extends Vocabulary {

    public static final String DOMAIN = "domain";

    public static final String NESTING = "nesting";

    public static final String NESTING_ORIGINAL = "nesting_original";

    public static final String NESTING_STRUCTURED = "nesting_structured";

    public static final String SIZE = "size";

    public static final String DATE = "date";

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://vocab.sindice.net/";

    /**
     * The namespace of the vocabulary as a URI.
     */
    public static final URI NAMESPACE = createResource(NS);

    /**
     * This property expresses the DNS domain of the resource on which
     * it is applied. It is intended to be used to keep track of the domain provenance
     * of each resource.
     */
    public static final URI domain = createProperty(DOMAIN);

    /**
     * This property links a resource with a <i>blank node</i> that represents
     * a nested <i>Microformat</i> node.
     */
    public static final URI nesting = createProperty(NESTING);

    /**
     * This property is used to keep track of the original nested <i>RDF property</i>.
     */
    public static final URI nesting_original = createProperty(NESTING_ORIGINAL);

    /**
     * This property links the resource with a <i>node</i> representing the nested <i>Microformat</i>
     * 
     */
    public static final URI nesting_structured = createProperty(NESTING_STRUCTURED);

    /**
     * Size meta property indicating the number of triples within the returned dataset.
     */
    public static final URI size = createProperty(SIZE);

    /**
     * Date meta property indicating the data generation time.
     */
    public static final URI date = createProperty(DATE);


    private static Map<String, URI> localNamesMap;

    /**
     * Returns a resource defined within this vocabulary.
     *
     * @param name resource name.
     * @return the URI associated to such resource.
     */
    public static URI getResource(String name) {
        URI res = localNamesMap.get(name);
        if (null == res)
            throw new RuntimeException(
                    String.format(
                            "heck, you are using a non existing URI: %s",
                            name
                    )
            );
        return res;
    }

    /**
     * Returns a property defined within this vocabulary.
     *
     * @param name resource name.
     * @return the URI associated to such resource.
     */
    public static URI getProperty(String name) {
        return getResource(name);
    }

    private static URI createResource(String string) {
        URI res = createURI(NS + string);
        if(localNamesMap == null) {
            localNamesMap = new HashMap<String, URI>(10);
        }
        localNamesMap.put(string, res);
        return res;
    }

    private static URI createProperty(String string) {
        URI res = createURI(NS + string);
        if(localNamesMap == null) {
            localNamesMap = new HashMap<String, URI>(10);
        }
        localNamesMap.put(string, res);
        return res;
    }

    private SINDICE(){}

}