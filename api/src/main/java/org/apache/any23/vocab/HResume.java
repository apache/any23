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
 * @author Nisala Nirmana
 *
 */
public class HResume extends Vocabulary {

    public static final String NS = SINDICE.NS + "hresume/";

    private static HResume instance;

    public static HResume getInstance() {
        if(instance == null) {
            instance = new HResume();
        }
        return instance;
    }

    public URI Resume  = createClass(NS, "Resume");
    public URI education   = createClass(NS, "education");
    public URI experience = createClass(NS, "experience");
    public URI contact = createClass(NS, "contact");
    public URI affiliation = createClass(NS, "affiliation");


    public URI name  = createProperty(NS, "name");
    public URI summary   = createProperty(NS, "summary");
    public URI skill   = createProperty(NS, "skill");


    private HResume() {
        super(NS);
    }
}
