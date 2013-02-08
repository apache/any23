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
public class LKIFCoreLegalAction extends Vocabulary {
	
    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.estrellaproject.org/lkif-core/legal-action.owl#";
    
    private static LKIFCoreLegalAction instance;
    
    public static LKIFCoreLegalAction getInstance() {
      if(instance == null) {
          instance = new LKIFCoreLegalAction();
      }
      return instance;
  }
    ////////////////////////////////////////////////////////////////
    /* http://www.estrellaproject.org/lkif-core/legal-action.owl# */
    ////////////////////////////////////////////////////////////////
    // CLASSES
    public final URI Limited_Company                = createClass(NS, "Limited_Company");
    public final URI Private_Legal_Person           = createClass(NS, "Private_Legal_Person");
    public final URI Society                        = createClass(NS, "Society");
    public final URI Natural_Person                 = createClass(NS, "Natural_Person");
    public final URI Mandate                        = createClass(NS, "Mandate");
    public final URI Corporation                    = createClass(NS, "Corporation");
    public final URI Legal_Person                   = createClass(NS, "Legal_Person");
    public final URI Public_Body                    = createClass(NS, "Public_Body");
    public final URI Foundation                     = createClass(NS, "Foundation");
    public final URI Co_operative                   = createClass(NS, "Co-operative"); 
    public final URI Legislative_Body               = createClass(NS, "Legislative_Body");
    public final URI Delegation                     = createClass(NS, "Delegation");
    public final URI Legal_Speech_Act               = createClass(NS, "Legal_Speech_Act");
    public final URI Public_Act                     = createClass(NS, "Public_Act");
    public final URI Company                        = createClass(NS, "Company");
    public final URI Decision                       = createClass(NS, "Decision");
    public final URI Public_Limited_Company         = createClass(NS, "Public_Limited_Company");
    public final URI Incorporated                   = createClass(NS, "Incorporated");
    public final URI Act_of_Law                     = createClass(NS, "Act_of_Law");
    public final URI Association                    = createClass(NS, "Association");
    public final URI Assignment                     = createClass(NS, "Assignment");
    public final URI Unincorporated                 = createClass(NS, "Unincorporated");
    
    // RESOURCES
    
    // PROPERTIES
    
    private LKIFCoreLegalAction() {
      super(NS);
    }

}
