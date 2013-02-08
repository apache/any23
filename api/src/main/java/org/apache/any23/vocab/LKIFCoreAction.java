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
public class LKIFCoreAction extends Vocabulary {
	
    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/action.owl#";
    
    private static LKIFCoreAction instance;
    
    public static LKIFCoreAction getInstance() {
      if(instance == null) {
          instance = new LKIFCoreAction();
      }
      return instance;
  }
    /////////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/action.owl */
    /////////////////////////////////////////////////////////
    // CLASSES
    public final URI Action                 = createClass(NS, "Action");
    public final URI Agent                  = createClass(NS, "Agent");
    public final URI Artifact               = createClass(NS, "Artifact");
    public final URI Collaborative_Plan     = createClass(NS, "Collaborative_Plan");
    public final URI Creation_C             = createClass(NS, "Creation");
    public final URI Natural_Object         = createClass(NS, "Natural_Object");
    public final URI Organisation           = createClass(NS, "Organisation");
    public final URI Person                 = createClass(NS, "Person");
    public final URI Personal_Plan          = createClass(NS, "Personal_Plan");
    public final URI Plan                   = createClass(NS, "Plan");
    public final URI Reaction               = createClass(NS, "Reaction");
    public final URI Transaction            = createClass(NS, "Transaction");
    public final URI Mental_Object          = createClass(NS, "Mental_Object");
    public final URI Change                 = createClass(NS, "Change");
    public final URI Physical_Object        = createClass(NS, "Physical_Object");
    public final URI Process                = createClass(NS, "Process");
    
    // RESOURCES
    
    // PROPERTIES
    public final URI actor                   = createProperty(NS, "actor");
    public final URI actor_in                = createProperty(NS, "actor_in");
    public final URI direct_part             = createProperty(NS, "direct_part");
    public final URI member                  = createProperty(NS, "member");
    public final URI part                    = createProperty(NS, "part");
    public final URI creation_P              = createProperty(NS, "creation");
    public final URI participant             = createProperty(NS, "participant");
    public final URI participant_in          = createProperty(NS, "participant_in");
    public final URI result_of               = createProperty(NS, "result_of");
    
    private LKIFCoreAction() {
      super(NS);
    }

}
