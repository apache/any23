package org.deri.any23.vocab;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

public class DOAC {
    public static final String NS = "http://ramonantonio.net/doac/0.1/#";

    private static final ValueFactory factory = ValueFactoryImpl.getInstance();

    public static final URI summary = createURI("summary");
    public static final URI end_date = createURI("end-date");
    public static final URI publication = createURI("publication");
    public static final URI title = createURI("title");
    public static final URI reference = createURI("reference");
    public static final URI language = createURI("language");
    public static final URI experience = createURI("experience");
    public static final URI organization = createURI("organization");
    public static final URI affiliation = createURI("affiliation");
    public static final URI writes = createURI("writes");
    public static final URI start_date = createURI("start-date");
    public static final URI education = createURI("education");
    public static final URI skill = createURI("skill");
    public static final URI referer = createURI("referer");
    public static final URI isco88_code = createURI("isco88-code");
    public static final URI speaks = createURI("speaks");
    public static final URI reads = createURI("reads");
    public static final URI Publication = createURI("Publication");
    public static final URI reference_type = createURI("reference-type");
    public static final URI Education = createURI("Education");
    public static final URI OrganisationalSkill = createURI("OrganisationalSkill");
    public static final URI PrimarySchool = createURI("PrimarySchool");
    public static final URI Reference = createURI("Reference");
    public static final URI DrivingSkill = createURI("DrivingSkill");
    public static final URI Degree = createURI("Degree");
    public static final URI LanguageSkill = createURI("LanguageSkill");
    public static final URI Skill = createURI("Skill");
    public static final URI SecondarySchool = createURI("SecondarySchool");
    public static final URI Course = createURI("Course");
    public static final URI Experience = createURI("Experience");
    public static final URI SocialSkill = createURI("SocialSkill");
    public static final URI ComputerSkill = createURI("ComputerSkill");
    public static final URI LanguageLevel = createURI("LanguageLevel");

    private static URI createURI(String localName) {
        return factory.createURI(NS, localName);
    }
}
