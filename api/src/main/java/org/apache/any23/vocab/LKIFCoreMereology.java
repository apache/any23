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
public class LKIFCoreMereology extends Vocabulary {
	
    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/mereology.owl#";
    
    private static LKIFCoreMereology instance;
    
    public static LKIFCoreMereology getInstance() {
      if(instance == null) {
          instance = new LKIFCoreMereology();
      }
      return instance;
  }
    /////////////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/mereology.owl */
    /////////////////////////////////////////////////////////////
    // CLASSES
    public final URI Atom                        = createClass(NS, "Atom");
    public final URI Composition                 = createClass(NS, "Composition");
    public final URI Pair                        = createClass(NS, "Pair");
    public final URI Part                        = createClass(NS, "Part");
    public final URI Whole                       = createClass(NS, "Whole");
    
    // RESOURCES
    
    // PROPERTIES
    public final URI component                   = createProperty(NS, "component");
    public final URI component_of                = createProperty(NS, "component_of");
    public final URI composed_of                 = createProperty(NS, "composed_of");
    public final URI composes                    = createProperty(NS, "composes");
    public final URI contained_in                = createProperty(NS, "contained_in");
    public final URI contains                    = createProperty(NS, "contains");
    public final URI direct_part                 = createProperty(NS, "direct_part");
    public final URI direct_part_of              = createProperty(NS, "direct_part_of");
    public final URI member                      = createProperty(NS, "member");
    public final URI member_of                   = createProperty(NS, "member_of");
    public final URI part                        = createProperty(NS, "part");
    public final URI part_of                     = createProperty(NS, "part_of");
    public final URI strict_part                 = createProperty(NS, "strict_part");
    public final URI strict_part_of              = createProperty(NS, "strict_part_of");
    
    private LKIFCoreMereology() {
      super(NS);
    }

}
