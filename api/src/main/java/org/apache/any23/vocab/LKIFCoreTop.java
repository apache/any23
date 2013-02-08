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
public class LKIFCoreTop extends Vocabulary {
	
    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/lkif-top.owl#";
    
    private static LKIFCoreTop instance;
    
    public static LKIFCoreTop getInstance() {
      if(instance == null) {
          instance = new LKIFCoreTop();
      }
      return instance;
  }
    /////////////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/lkif-top.owl */
    /////////////////////////////////////////////////////////////
    // CLASSES
    public final URI Abstract_Entity                  = createClass(NS, "Abstract_Entity");
    public final URI Mental_Entity                    = createClass(NS, "Mental_Entity");
    public final URI Mental_Object                    = createClass(NS, "Mental_Object");
    public final URI Occurrence                       = createClass(NS, "Occurrence");
    public final URI Physical_Entity                  = createClass(NS, "Physical_Entity");
    public final URI Spatio_Temporal_Occurrence       = createClass(NS, "Spatio_Temporal_Occurrence");

    // RESOURCES
    
    // PROPERTIES
    
    private LKIFCoreTop() {
      super(NS);
    }

}
