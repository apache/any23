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
 * The <a href="http://ramonantonio.net/doac/0.1/">Description Of A Career</a> vocabulary.
 */
public class DOAC extends Vocabulary {

    public static final String NS = "http://ramonantonio.net/doac/0.1/#";

    private static DOAC instance;

    public static DOAC getInstance() {
        if(instance == null) {
            instance = new DOAC();
        }
        return instance;
    }

    // Properties.
    public final URI summary             = createProperty(NS, "summary");
    public final URI end_date            = createProperty(NS, "end-date");
    public final URI publication         = createProperty(NS, "publication");
    public final URI title               = createProperty(NS, "title");
    public final URI reference           = createProperty(NS, "reference");
    public final URI language            = createProperty(NS, "language");
    public final URI experience          = createProperty(NS, "experience");
    public final URI organization        = createProperty(NS, "organization");
    public final URI affiliation         = createProperty(NS, "affiliation");
    public final URI writes              = createProperty(NS, "writes");
    public final URI start_date          = createProperty(NS, "start-date");
    public final URI education           = createProperty(NS, "education");
    public final URI skill               = createProperty(NS, "skill");
    public final URI referer             = createProperty(NS, "referer");
    public final URI isco88_code         = createProperty(NS, "isco88-code");
    public final URI speaks              = createProperty(NS, "speaks");
    public final URI reads               = createProperty(NS, "reads");
    public final URI reference_type      = createProperty(NS, "reference-type");

    // Resources.
    public final URI Publication         = createClass(NS, "Publication");
    public final URI Education           = createClass(NS, "Education");
    public final URI OrganisationalSkill = createClass(NS, "OrganisationalSkill");
    public final URI PrimarySchool       = createClass(NS, "PrimarySchool");
    public final URI Reference           = createClass(NS, "Reference");
    public final URI DrivingSkill        = createClass(NS, "DrivingSkill");
    public final URI Degree              = createClass(NS, "Degree");
    public final URI LanguageSkill       = createClass(NS, "LanguageSkill");
    public final URI Skill               = createClass(NS, "Skill");
    public final URI SecondarySchool     = createClass(NS, "SecondarySchool");
    public final URI Course              = createClass(NS, "Course");
    public final URI Experience          = createClass(NS, "Experience");
    public final URI SocialSkill         = createClass(NS, "SocialSkill");
    public final URI ComputerSkill       = createClass(NS, "ComputerSkill");
    public final URI LanguageLevel       = createClass(NS, "LanguageLevel");

    private DOAC(){
        super(NS);
    }

}
