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
public class LKIFCoreExpression extends Vocabulary {
	
    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/expression.owl#";
    
    private static LKIFCoreExpression instance;
    
    public static LKIFCoreExpression getInstance() {
      if(instance == null) {
          instance = new LKIFCoreExpression();
      }
      return instance;
  }
    /////////////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/expression.owl */
    /////////////////////////////////////////////////////////////
    // CLASSES
    public final URI Argument                          = createClass(NS, "Argument");
    public final URI Assertion                         = createClass(NS, "Assertion");
    public final URI Assumption                        = createClass(NS, "Assumption");
    public final URI Belief                            = createClass(NS, "Belief");
    public final URI Cause                             = createClass(NS, "Cause");
    public final URI Communicated_Attitude             = createClass(NS, "Communicated_Attitude");
    public final URI Declaration                       = createClass(NS, "Declaration");
    public final URI Desire                            = createClass(NS, "Desire");
    public final URI Document                          = createClass(NS, "Document");
    public final URI Evaluative_Attitude               = createClass(NS, "Evaluative_Attitude");
    public final URI Evaluative_Proposition            = createClass(NS, "Evaluative_Proposition");
    public final URI Evidence                          = createClass(NS, "Evidence");
    public final URI Exception                         = createClass(NS, "Exception");
    public final URI Expectation                       = createClass(NS, "Expectation");
    public final URI Expression                        = createClass(NS, "Expression");
    public final URI Fact                              = createClass(NS, "Fact");
    public final URI Intention                         = createClass(NS, "Intention");
    public final URI Lie                               = createClass(NS, "Lie");
    public final URI Medium                            = createClass(NS, "Medium");
    public final URI Observation                       = createClass(NS, "Observation");
    public final URI Problem                           = createClass(NS, "Problem");
    public final URI Promise                           = createClass(NS, "Promise");
    public final URI Proposition                       = createClass(NS, "Proposition");
    public final URI Propositional_Attitude            = createClass(NS, "Propositional_Attitude");
    public final URI Qualification                     = createClass(NS, "Qualification");
    public final URI Qualified                         = createClass(NS, "Qualified");
    public final URI Reason                            = createClass(NS, "Reason");
    public final URI Speech_Act                        = createClass(NS, "Speech_Act");
    public final URI Statement_In_Writing              = createClass(NS, "Statement_In_Writing");
    public final URI Surprise                          = createClass(NS, "Surprise");
    
    // RESOURCES
    
    // PROPERTIES
    public final URI addressee                         = createProperty(NS, "addressee");
    public final URI asserted_by                       = createProperty(NS, "asserted_by");
    public final URI asserts                           = createProperty(NS, "asserts");
    public final URI attitude                          = createProperty(NS, "attitude");
    public final URI author                            = createProperty(NS, "author");
    public final URI bears                             = createProperty(NS, "bears");
    public final URI believed_by                       = createProperty(NS, "believed_by");
    public final URI believes                          = createProperty(NS, "believes");
    public final URI declares                          = createProperty(NS, "declares");
    public final URI declared_by                       = createProperty(NS, "declared_by");
    public final URI evaluated_by                      = createProperty(NS, "evaluated_by");
    public final URI evaluates                         = createProperty(NS, "evaluates");
    public final URI evaluatively_comparable           = createProperty(NS, "evaluatively_comparable");
    public final URI held_by                           = createProperty(NS, "held_by");
    public final URI holds                             = createProperty(NS, "holds");
    public final URI intended_by                       = createProperty(NS, "intended_by");
    public final URI intends                           = createProperty(NS, "intends");
    public final URI medium                            = createProperty(NS, "medium");
    public final URI observer                          = createProperty(NS, "observer");
    public final URI observes                          = createProperty(NS, "observes");
    public final URI promised_by                       = createProperty(NS, "promised_by");
    public final URI promises                          = createProperty(NS, "promises");
    public final URI qualified_by                      = createProperty(NS, "qualified_by");
    public final URI qualifies                         = createProperty(NS, "qualifies");
    public final URI qualitatively_comparable          = createProperty(NS, "qualitatively_comparable");
    public final URI stated_by                         = createProperty(NS, "stated_by");
    public final URI states                            = createProperty(NS, "states");
    public final URI towards                           = createProperty(NS, "towards");
    public final URI utterer                           = createProperty(NS, "utterer");
    public final URI utters                            = createProperty(NS, "utters");
    public final URI creation                          = createProperty(NS, "creation");
    public final URI counts_as                         = createProperty(NS, "counts_as");
    public final URI imposed_on                        = createProperty(NS, "imposed_on");
    public final URI played_by                         = createProperty(NS, "played_by");
    public final URI plays                             = createProperty(NS, "plays");
    
    private LKIFCoreExpression() {
      super(NS);
    }

}
