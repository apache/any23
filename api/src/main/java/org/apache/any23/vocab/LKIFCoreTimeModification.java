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
public class LKIFCoreTimeModification extends Vocabulary {
	
    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/time-modification.owl#";
    
    private static LKIFCoreTimeModification instance;
    
    public static LKIFCoreTimeModification getInstance() {
      if(instance == null) {
          instance = new LKIFCoreTimeModification();
      }
      return instance;
  }
    ////////////////////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/time-modification.owl */
    ////////////////////////////////////////////////////////////////////
    // CLASSES
    public final URI Semantic_Annotation               = createClass(NS, "Semantic_Annotation");
    public final URI Modification                      = createClass(NS, "Modification");
    public final URI Transposition                     = createClass(NS, "Transposition");
    public final URI Ultractivity                      = createClass(NS, "Ultractivity");
    public final URI Annulment                         = createClass(NS, "Annulment");
    public final URI End_efficacy                      = createClass(NS, "End_efficacy");
    public final URI Efficacy_Modification             = createClass(NS, "Efficacy_Modification ");
    public final URI Modification_of_System            = createClass(NS, "Modification_of_System ");
    public final URI Dynamic_Temporal_Entity           = createClass(NS, "Dynamic_Temporal_Entity ");
    public final URI Remaking                          = createClass(NS, "Remaking ");
    public final URI Application                       = createClass(NS, "Application ");
    public final URI Ratification                      = createClass(NS, "Ratification ");
    public final URI Textual_Modification              = createClass(NS, "Textual_Modification ");
    public final URI Prorogation_in_Force              = createClass(NS, "Prorogation_in_Force ");
    public final URI Application_Date                  = createClass(NS, "Application_Date ");
    public final URI Retroactivity                     = createClass(NS, "Retroactivity ");
    public final URI Modification_of_Term              = createClass(NS, "Modification_of_Term ");
    public final URI Efficacy_Interval                 = createClass(NS, "Efficacy_Interval ");
    public final URI Start_Efficacy                    = createClass(NS, "Start_Efficacy ");
    public final URI Substitution                      = createClass(NS, "Substitution ");
    public final URI Temporal_Modification             = createClass(NS, "Temporal_Modification ");
    public final URI Suspension                        = createClass(NS, "Suspension ");
    public final URI In_Force_Modification             = createClass(NS, "In_Force_Modification ");
    public final URI Publication_Date                  = createClass(NS, "Publication_Date ");
    public final URI Exception                         = createClass(NS, "Exception ");
    public final URI Modification_of_Meaning           = createClass(NS, "Modification_of_Meaning ");
    public final URI Static_Temporal_Entity            = createClass(NS, "Static_Temporal_Entity ");
    public final URI End_in_Force                      = createClass(NS, "End_in_Force ");
    public final URI Start_in_Force                    = createClass(NS, "Start_in_Force ");
    public final URI Integration                       = createClass(NS, "Integration ");
    public final URI Application_Interval              = createClass(NS, "Application_Interval ");
    public final URI Interpretation                    = createClass(NS, "Interpretation ");
    public final URI Deregulation                      = createClass(NS, "Deregulation ");
    public final URI In_Force_Interval                 = createClass(NS, "In_Force_Interval ");
    public final URI Repeal                            = createClass(NS, "Repeal ");
    public final URI Modification_of_Scope             = createClass(NS, "Modification_of_Scope ");
    public final URI Delivery_Date                     = createClass(NS, "Delivery_Date ");
    public final URI Enter_in_Force_Date               = createClass(NS, "Enter_in_Force_Date ");
    public final URI Variation                         = createClass(NS, "Variation ");
    public final URI Existence_Date                    = createClass(NS, "Existence_Date ");
    public final URI Relocation                        = createClass(NS, "Relocation ");
    public final URI Prorogation_Efficacy              = createClass(NS, "Prorogation_Efficacy ");
    public final URI Extension                         = createClass(NS, "Extension ");
    public final URI Renewal                           = createClass(NS, "Renewal ");
    
    // RESOURCES
    
    // PROPERTIES
    public final URI initial_date                      = createProperty(NS, "initial_date");
    public final URI in_force                          = createProperty(NS, "in_force");
    public final URI final_date_of                     = createProperty(NS, "final_date_of");
    public final URI efficacy                          = createProperty(NS, "efficacy");
    public final URI initial_date_of                   = createProperty(NS, "initial_date_of");
    public final URI produce_efficacy_modification     = createProperty(NS, "produce_efficacy_modification");
    public final URI duration                          = createProperty(NS, "duration");
    public final URI final_date                        = createProperty(NS, "final_date");
    public final URI application                       = createProperty(NS, "application");
    public final URI date                              = createProperty(NS, "date");
    public final URI produce_textual_modification      = createProperty(NS, "produce_textual_modification");
    public final URI produce_inforce_modification      = createProperty(NS, "produce_inforce_modification");
    
    private LKIFCoreTimeModification() {
      super(NS);
    }

}
