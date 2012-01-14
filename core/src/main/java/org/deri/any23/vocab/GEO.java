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

package org.deri.any23.vocab;

import org.openrdf.model.URI;

/**
 * The <a href="http://www.geonames.org/ontology/">GEO Names</a> vocabulary.
 */
public class GEO extends Vocabulary {

    public static final String NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";

    private static Vocabulary instance;

    public static Vocabulary getInstance() {
        if(instance == null) {
            instance = new GEO();
        }
        return instance;
    }

    // Resources.
    public final URI Point = createClass(NS, "Point");

    // Properties
    public final URI lat = createProperty(NS, "lat" );
    public final URI lon = createProperty(NS, "long");

    private GEO(){
        super(NS);
    }

}
