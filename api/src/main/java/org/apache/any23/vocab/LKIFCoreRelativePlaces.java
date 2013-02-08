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
 * @author lewismc
 * An implementation of the <a href="https://github.com/RinkeHoekstra/lkif-core">lkif-core</a>
 * vocabulary which is a library of ontologies relevant for the legal domain. 
 * The library consists of 15 modules, each of which describes a set of closely 
 * related concepts from both legal and commonsense domains.
 *
 */
public class LKIFCoreRelativePlaces extends Vocabulary {
	
    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/relative-places.owl#";
    
    private static LKIFCoreRelativePlaces instance;
    
    public static LKIFCoreRelativePlaces getInstance() {
      if(instance == null) {
          instance = new LKIFCoreRelativePlaces();
      }
      return instance;
  }
    //////////////////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/relative-places.owl */
    //////////////////////////////////////////////////////////////////
    // CLASSES
    public final URI Absolute_Place                   = createClass(NS, "Absolute_Place");
    public final URI Comprehensive_Place              = createClass(NS, "Comprehensive_Place");
    public final URI Location_Complex                 = createClass(NS, "Location_Complex");
    public final URI Place                            = createClass(NS, "Place");
    public final URI Relative_Place                   = createClass(NS, "Relative_Place");
    
    // RESOURCES
    
    // PROPERTIES
    public final URI abut                             = createProperty(NS, "abut");
    public final URI connect                          = createProperty(NS, "connect");
    public final URI cover                            = createProperty(NS, "cover");
    public final URI covered_by                       = createProperty(NS, "covered_by");
    public final URI exactly_coincide                 = createProperty(NS, "exactly_coincide");
    public final URI externally_connect               = createProperty(NS, "externally_connect");
    public final URI in                               = createProperty(NS, "in");
    public final URI location_complex                 = createProperty(NS, "location_complex");
    public final URI location_complex_for             = createProperty(NS, "location_complex_for");
    public final URI meet                             = createProperty(NS, "meet");
    public final URI overlap                          = createProperty(NS, "overlap");
    public final URI partially_coincide               = createProperty(NS, "partially_coincide");
    public final URI relatively_fixed                 = createProperty(NS, "relatively_fixed");
    public final URI spatial_reference                = createProperty(NS, "spatial_reference");
    public final URI spatial_relation                 = createProperty(NS, "spatial_relation");
    
    private LKIFCoreRelativePlaces() {
      super(NS);
    }

}
