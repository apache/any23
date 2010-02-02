package org.deri.any23.vocab;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

public class FOAF {
    public static final String NS = "http://xmlns.com/foaf/0.1/";

    private static final ValueFactory factory = ValueFactoryImpl.getInstance();

    // Properties
    public static final URI topic_interest = createURI("topic_interest");
    public static final URI phone = createURI("phone");
    public static final URI icqChatID = createURI("icqChatID");
    public static final URI yahooChatID = createURI("yahooChatID");
    public static final URI member = createURI("member");
    public static final URI givenname = createURI("givenname");
    public static final URI birthday = createURI("birthday");
    public static final URI img = createURI("img");
    public static final URI name = createURI("name");
    public static final URI maker = createURI("maker");
    public static final URI tipjar = createURI("tipjar");
    public static final URI membershipClass = createURI("membershipClass");
    public static final URI accountName = createURI("accountName");
    public static final URI mbox_sha1sum = createURI("mbox_sha1sum");
    public static final URI geekcode = createURI("geekcode");
    public static final URI interest = createURI("interest");
    public static final URI depicts = createURI("depicts");
    public static final URI knows = createURI("knows");
    public static final URI homepage = createURI("homepage");
    public static final URI firstName = createURI("firstName");
    public static final URI surname = createURI("surname");
    public static final URI isPrimaryTopicOf = createURI("isPrimaryTopicOf");
    public static final URI page = createURI("page");
    public static final URI accountServiceHomepage = createURI("accountServiceHomepage");
    public static final URI depiction = createURI("depiction");
    public static final URI fundedBy = createURI("fundedBy");
    public static final URI title = createURI("title");
    public static final URI weblog = createURI("weblog");
    public static final URI logo = createURI("logo");
    public static final URI workplaceHomepage = createURI("workplaceHomepage");
    public static final URI based_near = createURI("based_near");
    public static final URI thumbnail = createURI("thumbnail");
    public static final URI primaryTopic = createURI("primaryTopic");
    public static final URI aimChatID = createURI("aimChatID");
    public static final URI made = createURI("made");
    public static final URI workInfoHomepage = createURI("workInfoHomepage");
    public static final URI currentProject = createURI("currentProject");
    public static final URI holdsAccount = createURI("holdsAccount");
    public static final URI publications = createURI("publications");
    public static final URI sha1 = createURI("sha1");
    public static final URI gender = createURI("gender");
    public static final URI mbox = createURI("mbox");
    public static final URI myersBriggs = createURI("myersBriggs");
    public static final URI plan = createURI("plan");
    public static final URI pastProject = createURI("pastProject");
    public static final URI schoolHomepage = createURI("schoolHomepage");
    public static final URI family_name = createURI("family_name");
    public static final URI msnChatID = createURI("msnChatID");
    public static final URI theme = createURI("theme");
    public static final URI topic = createURI("topic");
    public static final URI dnaChecksum = createURI("dnaChecksum");
    public static final URI nick = createURI("nick");
    public static final URI jabberID = createURI("jabberID");

    // Classes
    public static final URI Person = createURI("Person");
    public static final URI PersonalProfileDocument = createURI("PersonalProfileDocument");
    public static final URI Project = createURI("Project");
    public static final URI OnlineChatAccount = createURI("OnlineChatAccount");
    public static final URI OnlineAccount = createURI("OnlineAccount");
    public static final URI Agent = createURI("Agent");
    public static final URI Group = createURI("Group");
    public static final URI OnlineGamingAccount = createURI("OnlineGamingAccount");
    public static final URI OnlineEcommerceAccount = createURI("OnlineEcommerceAccount");
    public static final URI Document = createURI("Document");
    public static final URI Organization = createURI("Organization");
    public static final URI Image = createURI("Image");

    private static URI createURI(String localName) {
        return factory.createURI(NS, localName);
    }
}
