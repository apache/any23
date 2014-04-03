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
 * The <a href="http://xmlns.com/foaf/spec/">Friend Of A Friend</a> vocabulary.
 */
public class FOAF extends Vocabulary {

    public static final String NS = "http://xmlns.com/foaf/0.1/";

    private static FOAF instance;

    public static FOAF getInstance() {
        if(instance == null) {
            instance = new FOAF();
        }
        return instance;
    }

    // Properties.
    public final URI topic_interest          = createProperty(NS, "topic_interest");
    public final URI phone                   = createProperty(NS, "phone");
    public final URI icqChatID               = createProperty(NS, "icqChatID");
    public final URI yahooChatID             = createProperty(NS, "yahooChatID");
    public final URI member                  = createProperty(NS, "member");
    public final URI givenname               = createProperty(NS, "givenname");
    public final URI birthday                = createProperty(NS, "birthday");
    public final URI img                     = createProperty(NS, "img");
    public final URI name                    = createProperty(NS, "name");
    public final URI maker                   = createProperty(NS, "maker");
    public final URI tipjar                  = createProperty(NS, "tipjar");
    public final URI membershipClass         = createProperty(NS, "membershipClass");
    public final URI accountName             = createProperty(NS, "accountName");
    public final URI mbox_sha1sum            = createProperty(NS, "mbox_sha1sum");
    public final URI geekcode                = createProperty(NS, "geekcode");
    public final URI interest                = createProperty(NS, "interest");
    public final URI depicts                 = createProperty(NS, "depicts");
    public final URI knows                   = createProperty(NS, "knows");
    public final URI homepage                = createProperty(NS, "homepage");
    public final URI firstName               = createProperty(NS, "firstName");
    public final URI surname                 = createProperty(NS, "surname");
    public final URI isPrimaryTopicOf        = createProperty(NS, "isPrimaryTopicOf");
    public final URI page                    = createProperty(NS, "page");
    public final URI accountServiceHomepage  = createProperty(NS, "accountServiceHomepage");
    public final URI depiction               = createProperty(NS, "depiction");
    public final URI fundedBy                = createProperty(NS, "fundedBy");
    public final URI title                   = createProperty(NS, "title");
    public final URI weblog                  = createProperty(NS, "weblog");
    public final URI logo                    = createProperty(NS, "logo");
    public final URI workplaceHomepage       = createProperty(NS, "workplaceHomepage");
    public final URI based_near              = createProperty(NS, "based_near");
    public final URI thumbnail               = createProperty(NS, "thumbnail");
    public final URI primaryTopic            = createProperty(NS, "primaryTopic");
    public final URI aimChatID               = createProperty(NS, "aimChatID");
    public final URI made                    = createProperty(NS, "made");
    public final URI workInfoHomepage        = createProperty(NS, "workInfoHomepage");
    public final URI currentProject          = createProperty(NS, "currentProject");
    public final URI holdsAccount            = createProperty(NS, "holdsAccount");
    public final URI publications            = createProperty(NS, "publications");
    public final URI sha1                    = createProperty(NS, "sha1");
    public final URI gender                  = createProperty(NS, "gender");
    public final URI mbox                    = createProperty(NS, "mbox");
    public final URI myersBriggs             = createProperty(NS, "myersBriggs");
    public final URI plan                    = createProperty(NS, "plan");
    public final URI pastProject             = createProperty(NS, "pastProject");
    public final URI schoolHomepage          = createProperty(NS, "schoolHomepage");
    public final URI family_name             = createProperty(NS, "family_name");
    public final URI msnChatID               = createProperty(NS, "msnChatID");
    public final URI theme                   = createProperty(NS, "theme");
    public final URI topic                   = createProperty(NS, "topic");
    public final URI dnaChecksum             = createProperty(NS, "dnaChecksum");
    public final URI nick                    = createProperty(NS, "nick");
    public final URI jabberID                = createProperty(NS, "jabberID");

    // Resources.
    public final URI Person                  = createClass(NS, "Person");
    public final URI PersonalProfileDocument = createClass(NS, "PersonalProfileDocument");
    public final URI Project                 = createClass(NS, "Project");
    public final URI OnlineChatAccount       = createClass(NS, "OnlineChatAccount");
    public final URI OnlineAccount           = createClass(NS, "OnlineAccount");
    public final URI Agent                   = createClass(NS, "Agent");
    public final URI Group                   = createClass(NS, "Group");
    public final URI OnlineGamingAccount     = createClass(NS, "OnlineGamingAccount");
    public final URI OnlineEcommerceAccount  = createClass(NS, "OnlineEcommerceAccount");
    public final URI Document                = createClass(NS, "Document");
    public final URI Organization            = createClass(NS, "Organization");
    public final URI Image                   = createClass(NS, "Image");

    private FOAF(){
        super(NS);
    }
    
}
