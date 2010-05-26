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
 * The <a href="http://xmlns.com/foaf/spec/">Fried Of A Friend</a> vocabulary.
 */
public class FOAF extends Vocabulary {

    public static final String NS = "http://xmlns.com/foaf/0.1/";

    // Properties.
    public static final URI topic_interest          = createURI(NS, "topic_interest");
    public static final URI phone                   = createURI(NS, "phone");
    public static final URI icqChatID               = createURI(NS, "icqChatID");
    public static final URI yahooChatID             = createURI(NS, "yahooChatID");
    public static final URI member                  = createURI(NS, "member");
    public static final URI givenname               = createURI(NS, "givenname");
    public static final URI birthday                = createURI(NS, "birthday");
    public static final URI img                     = createURI(NS, "img");
    public static final URI name                    = createURI(NS, "name");
    public static final URI maker                   = createURI(NS, "maker");
    public static final URI tipjar                  = createURI(NS, "tipjar");
    public static final URI membershipClass         = createURI(NS, "membershipClass");
    public static final URI accountName             = createURI(NS, "accountName");
    public static final URI mbox_sha1sum            = createURI(NS, "mbox_sha1sum");
    public static final URI geekcode                = createURI(NS, "geekcode");
    public static final URI interest                = createURI(NS, "interest");
    public static final URI depicts                 = createURI(NS, "depicts");
    public static final URI knows                   = createURI(NS, "knows");
    public static final URI homepage                = createURI(NS, "homepage");
    public static final URI firstName               = createURI(NS, "firstName");
    public static final URI surname                 = createURI(NS, "surname");
    public static final URI isPrimaryTopicOf        = createURI(NS, "isPrimaryTopicOf");
    public static final URI page                    = createURI(NS, "page");
    public static final URI accountServiceHomepage  = createURI(NS, "accountServiceHomepage");
    public static final URI depiction               = createURI(NS, "depiction");
    public static final URI fundedBy                = createURI(NS, "fundedBy");
    public static final URI title                   = createURI(NS, "title");
    public static final URI weblog                  = createURI(NS, "weblog");
    public static final URI logo                    = createURI(NS, "logo");
    public static final URI workplaceHomepage       = createURI(NS, "workplaceHomepage");
    public static final URI based_near              = createURI(NS, "based_near");
    public static final URI thumbnail               = createURI(NS, "thumbnail");
    public static final URI primaryTopic            = createURI(NS, "primaryTopic");
    public static final URI aimChatID               = createURI(NS, "aimChatID");
    public static final URI made                    = createURI(NS, "made");
    public static final URI workInfoHomepage        = createURI(NS, "workInfoHomepage");
    public static final URI currentProject          = createURI(NS, "currentProject");
    public static final URI holdsAccount            = createURI(NS, "holdsAccount");
    public static final URI publications            = createURI(NS, "publications");
    public static final URI sha1                    = createURI(NS, "sha1");
    public static final URI gender                  = createURI(NS, "gender");
    public static final URI mbox                    = createURI(NS, "mbox");
    public static final URI myersBriggs             = createURI(NS, "myersBriggs");
    public static final URI plan                    = createURI(NS, "plan");
    public static final URI pastProject             = createURI(NS, "pastProject");
    public static final URI schoolHomepage          = createURI(NS, "schoolHomepage");
    public static final URI family_name             = createURI(NS, "family_name");
    public static final URI msnChatID               = createURI(NS, "msnChatID");
    public static final URI theme                   = createURI(NS, "theme");
    public static final URI topic                   = createURI(NS, "topic");
    public static final URI dnaChecksum             = createURI(NS, "dnaChecksum");
    public static final URI nick                    = createURI(NS, "nick");
    public static final URI jabberID                = createURI(NS, "jabberID");

    // Classes.
    public static final URI Person                  = createURI(NS, "Person");
    public static final URI PersonalProfileDocument = createURI(NS, "PersonalProfileDocument");
    public static final URI Project                 = createURI(NS, "Project");
    public static final URI OnlineChatAccount       = createURI(NS, "OnlineChatAccount");
    public static final URI OnlineAccount           = createURI(NS, "OnlineAccount");
    public static final URI Agent                   = createURI(NS, "Agent");
    public static final URI Group                   = createURI(NS, "Group");
    public static final URI OnlineGamingAccount     = createURI(NS, "OnlineGamingAccount");
    public static final URI OnlineEcommerceAccount  = createURI(NS, "OnlineEcommerceAccount");
    public static final URI Document                = createURI(NS, "Document");
    public static final URI Organization            = createURI(NS, "Organization");
    public static final URI Image                   = createURI(NS, "Image");

    private FOAF(){}
    
}
