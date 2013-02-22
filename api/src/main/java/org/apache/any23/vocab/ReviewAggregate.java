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

public class ReviewAggregate extends Vocabulary {
    private static ReviewAggregate instance;

    public static ReviewAggregate getInstance() {
        if(instance == null) {
            instance = new ReviewAggregate();
        }
        return instance;
    }
    
    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://purl.org/stuff/revagg#";

    /**
     * The namespace of the vocabulary as a URI.
     */
    public final URI NAMESPACE = createURI(NS);
    
    /**
     * Number of usefulness votes (integer).
     */
    public final URI votes = createProperty("votes");
    
    /**
     * Number of usefulness reviews (integer).
     */
    public final URI count = createProperty("count");
    
    /**
     * Optional
     */
    public final URI average = createProperty("average");
    
    public final URI worst = createProperty("worst");
    
    public final URI best = createProperty("best");

    
     /**
     * An agg review of a work.
     */
    public final URI ReviewAggregate = createProperty("ReviewAggregate");

    private URI createProperty(String localName) {
        return createProperty(NS, localName);
    }
    
    private ReviewAggregate(){
        super(NS);
    }
}
