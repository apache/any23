/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.vocab;

import org.openrdf.model.URI;

/**
 * The <a href="http://ramonantonio.net/doac/0.1/">Description Of A Career</a> vocabulary.
 */
public class DOAC extends Vocabulary {

    public static final String NS = "http://ramonantonio.net/doac/0.1/#";

    public static final URI summary             = createURI(NS, "summary");
    public static final URI end_date            = createURI(NS, "end-date");
    public static final URI publication         = createURI(NS, "publication");
    public static final URI title               = createURI(NS, "title");
    public static final URI reference           = createURI(NS, "reference");
    public static final URI language            = createURI(NS, "language");
    public static final URI experience          = createURI(NS, "experience");
    public static final URI organization        = createURI(NS, "organization");
    public static final URI affiliation         = createURI(NS, "affiliation");
    public static final URI writes              = createURI(NS, "writes");
    public static final URI start_date          = createURI(NS, "start-date");
    public static final URI education           = createURI(NS, "education");
    public static final URI skill               = createURI(NS, "skill");
    public static final URI referer             = createURI(NS, "referer");
    public static final URI isco88_code         = createURI(NS, "isco88-code");
    public static final URI speaks              = createURI(NS, "speaks");
    public static final URI reads               = createURI(NS, "reads");
    public static final URI Publication         = createURI(NS, "Publication");
    public static final URI reference_type      = createURI(NS, "reference-type");
    public static final URI Education           = createURI(NS, "Education");
    public static final URI OrganisationalSkill = createURI(NS, "OrganisationalSkill");
    public static final URI PrimarySchool       = createURI(NS, "PrimarySchool");
    public static final URI Reference           = createURI(NS, "Reference");
    public static final URI DrivingSkill        = createURI(NS, "DrivingSkill");
    public static final URI Degree              = createURI(NS, "Degree");
    public static final URI LanguageSkill       = createURI(NS, "LanguageSkill");
    public static final URI Skill               = createURI(NS, "Skill");
    public static final URI SecondarySchool     = createURI(NS, "SecondarySchool");
    public static final URI Course              = createURI(NS, "Course");
    public static final URI Experience          = createURI(NS, "Experience");
    public static final URI SocialSkill         = createURI(NS, "SocialSkill");
    public static final URI ComputerSkill       = createURI(NS, "ComputerSkill");
    public static final URI LanguageLevel       = createURI(NS, "LanguageLevel");

    private DOAC(){}

}
