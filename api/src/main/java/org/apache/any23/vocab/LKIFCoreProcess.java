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
public class LKIFCoreProcess extends Vocabulary {
	
    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/process.owl#";
    
    private static LKIFCoreProcess instance;
    
    public static LKIFCoreProcess getInstance() {
      if(instance == null) {
          instance = new LKIFCoreProcess();
      }
      return instance;
  }
    //////////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/process.owl */
    //////////////////////////////////////////////////////////
    // CLASSES
    public final URI Change                                 = createClass(NS, "Change");
    public final URI Continuation                           = createClass(NS, "Continuation");
    public final URI Initiation                             = createClass(NS, "Initiation");
    public final URI Mental_Process                         = createClass(NS, "Mental_Process");
    public final URI Physical_Object                        = createClass(NS, "Physical_Object");
    public final URI Physical_Process                       = createClass(NS, "Physical_Process");
    public final URI Process                                = createClass(NS, "Process");
    public final URI Termination                            = createClass(NS, "Termination");
    
    // RESOURCES
    
    // PROPERTIES
    public final URI created_by                              = createProperty(NS, "created_by");
    public final URI creation                                = createProperty(NS, "creation");
    public final URI participant                             = createProperty(NS, "participant");
    public final URI participant_in                          = createProperty(NS, "participant_in");
    public final URI requirement                             = createProperty(NS, "requirement");
    public final URI requirement_of                          = createProperty(NS, "requirement_of");
    public final URI resource                                = createProperty(NS, "resource");
    public final URI resource_for                            = createProperty(NS, "resource_for ");
    public final URI result                                  = createProperty(NS, "result");
    public final URI result_of                               = createProperty(NS, "result_of");
    
    private LKIFCoreProcess() {
      super(NS);
    }

}
