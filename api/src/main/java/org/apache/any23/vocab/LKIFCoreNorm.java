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
public class LKIFCoreNorm extends Vocabulary {
	
    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/norm.owl#";
    
    private static LKIFCoreNorm instance;
    
    public static LKIFCoreNorm getInstance() {
      if(instance == null) {
          instance = new LKIFCoreNorm();
      }
      return instance;
  }
    ///////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/norm.owl */
    ///////////////////////////////////////////////////////
    // CLASSES
    public final URI Hohfeldian_Power                        = createClass(NS, "Hohfeldian_Power");
    public final URI Normatively_Qualified                   = createClass(NS, "Normatively_Qualified");
    public final URI Code_of_Conduct                         = createClass(NS, "Code_of_Conduct");
    public final URI Regulation                              = createClass(NS, "Regulation");
    public final URI Soft_Law                                = createClass(NS, "Soft_Law");
    public final URI Strictly_Disallowed                     = createClass(NS, "Strictly_Disallowed");
    public final URI Permissive_Right                        = createClass(NS, "Permissive_Right");
    public final URI Proclamation                            = createClass(NS, "Proclamation");
    public final URI Legal_Expression                        = createClass(NS, "Legal_Expression");
    public final URI Qualificatory_Expression                = createClass(NS, "Qualificatory_Expression");
    public final URI Enabling_Power                          = createClass(NS, "Enabling_Power");
    public final URI Existential_Expression                  = createClass(NS, "Existential_Expression");
    public final URI Persuasive_Precedent                    = createClass(NS, "Persuasive_Precedent");
    public final URI Belief_In_Violation                     = createClass(NS, "Belief_In_Violation");
    public final URI Strictly_Allowed                        = createClass(NS, "Strictly_Allowed");
    public final URI Legal_Doctrine                          = createClass(NS, "Legal_Doctrine");
    public final URI Resolution                              = createClass(NS, "Resolution");
    public final URI Evaluative_Expression                   = createClass(NS, "Evaluative_Expression");
    public final URI Liberty_Right                           = createClass(NS, "Liberty_Right");
    public final URI Declarative_Power                       = createClass(NS, "Declarative_Power");
    public final URI Contract                                = createClass(NS, "Contract");
    public final URI Custom                                  = createClass(NS, "Custom");
    public final URI Exclusionary_Right                      = createClass(NS, "Exclusionary_Right");
    public final URI International_Agreement                 = createClass(NS, "International_Agreement");
    public final URI Customary_Law                           = createClass(NS, "Customary_Law");
    public final URI Action_Power                            = createClass(NS, "Action_Power");
    public final URI Legal_Source                            = createClass(NS, "Legal_Source");
    public final URI Statute                                 = createClass(NS, "Statute");
    public final URI International_Arbitration               = createClass(NS, "International_Arbitration");
    public final URI Immunity                                = createClass(NS, "Immunity");
    public final URI Treaty                                  = createClass(NS, "Treaty");
    public final URI Mandatory_Precedent                     = createClass(NS, "Mandatory_Precedent");
    public final URI Code                                    = createClass(NS, "Code");
    public final URI Allowed                                 = createClass(NS, "Allowed");
    public final URI Observation_of_Violation                = createClass(NS, "Observation_of_Violation");
    public final URI Legal_Document                          = createClass(NS, "Legal_Document");
    public final URI Potestative_Expression                  = createClass(NS, "Potestative_Expression");
    public final URI Norm                                    = createClass(NS, "Norm");
    public final URI Potestative_Right                       = createClass(NS, "Potestative_Right");
    public final URI Allowed_And_Disallowed                  = createClass(NS, "Allowed_And_Disallowed");
    public final URI Obligation                              = createClass(NS, "Obligation");
    public final URI Disallowed_Intention                    = createClass(NS, "Disallowed_Intention");
    public final URI Permission                              = createClass(NS, "Permission");
    public final URI Liability_Right                         = createClass(NS, "Liability_Right");
    public final URI Right                                   = createClass(NS, "Right");
    public final URI Obliged                                 = createClass(NS, "Obliged");
    public final URI Non_binding_International_Agreement     = createClass(NS, "Non-binding_International_Agreement");
    public final URI Directive                               = createClass(NS, "Directive");
    public final URI Disallowed                              = createClass(NS, "Disallowed");
    public final URI Definitional_Expression                 = createClass(NS, "Definitional_Expression");
    public final URI Prohibition                             = createClass(NS, "Prohibition");
    public final URI Precedent                               = createClass(NS, "Precedent");
    public final URI Obligative_Right                        = createClass(NS, "Obligative_Right");
    
    // RESOURCES
    
    // PROPERTIES
    public final URI normatively_comparable                  = createProperty(NS, "normatively_comparable");
    public final URI normatively_equivalent_or_better        = createProperty(NS, "normatively_equivalent_or_better");
    public final URI disallows                               = createProperty(NS, "disallows");
    public final URI normatively_strictly_worse              = createProperty(NS, "normatively_strictly_worse");
    public final URI normatively_not_equivalent              = createProperty(NS, "normatively_not_equivalent");
    public final URI normatively_strictly_better             = createProperty(NS, "normatively_strictly_better");
    public final URI allowed_by                              = createProperty(NS, "allowed_by");
    public final URI disallowed_by                           = createProperty(NS, "disallowed_by");
    public final URI allows                                  = createProperty(NS, "allows");
    public final URI normatively_equivalent_or_worse         = createProperty(NS, "normatively_equivalent_or_worse");
    public final URI commands                                = createProperty(NS, "commands");
    public final URI commanded_by                            = createProperty(NS, "commanded_by");
    public final URI strictly_equivalent                     = createProperty(NS, "strictly_equivalent");
    
    private LKIFCoreNorm() {
      super(NS);
    }

}
