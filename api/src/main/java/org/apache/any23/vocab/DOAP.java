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
 * The <a href="https://github.com/edumbill/doap/wiki">Description Of A Project</a> vocabulary.
 * 
 * @author lewismc
 */
public class DOAP extends Vocabulary {

    public static final String NS = "http://usefulinc.com/ns/doap#";

    private static DOAP instance;
	
    public static DOAP getInstance() {
        if(instance == null) {
            instance = new DOAP();
        }
        return instance;
    }
	
    //Resources
    public final URI Project                = createClass(NS, "Project");
    public final URI Version                = createClass(NS, "Version");
    public final URI Specification          = createClass(NS, "Specification");
    public final URI Repository             = createClass(NS, "Repository");
    public final URI SVNRepository          = createClass(NS, "SVNRepository");
    public final URI BKRepository           = createClass(NS, "BKRepository");
    public final URI CVSRepository          = createClass(NS, "CVSRepository");
    public final URI ArchRepository         = createClass(NS, "ArchRepository");
    public final URI BazaarBranch           = createClass(NS, "BazaarBranch");
    public final URI GitRepository          = createClass(NS, "GitRepository");
    public final URI HgRepository           = createClass(NS, "HgRepository");
    public final URI DarcsRepository        = createClass(NS, "DarcsRepository");
	
    //Properties
    public final URI name                   = createProperty(NS, "name");
    public final URI homepage               = createProperty(NS, "homepage");
    public final URI old_homepage           = createProperty(NS, "old-homepage"); 
    public final URI created                = createProperty(NS, "created");
    public final URI shortdesc              = createProperty(NS, "shortdesc");
    public final URI description            = createProperty(NS, "description");
    public final URI release                = createProperty(NS, "release");
    public final URI mailing_list           = createProperty(NS, "mailing-list"); 
    public final URI category               = createProperty(NS, "category");
    public final URI license                = createProperty(NS, "license");
    public final URI repository             = createProperty(NS, "repository");
    public final URI anon_root              = createProperty(NS, "anon-root");
    public final URI browse                 = createProperty(NS, "browse");
    public final URI module                 = createProperty(NS, "module");
    public final URI location               = createProperty(NS, "location");
    public final URI download_page          = createProperty(NS, "download-page");
    public final URI download_mirror        = createProperty(NS, "download-mirror"); 
    public final URI revision               = createProperty(NS, "revision");
    public final URI file_release           = createProperty(NS, "file-release"); 
    public final URI wiki                   = createProperty(NS, "wiki");
    public final URI bug_database           = createProperty(NS, "bug-database"); 
    public final URI screenshots            = createProperty(NS, "screenshots");
    public final URI maintainer             = createProperty(NS, "maintainer");
    public final URI developer              = createProperty(NS, "developer");
    public final URI documenter             = createProperty(NS, "documenter");
    public final URI translator             = createProperty(NS, "translator");
    public final URI tester                 = createProperty(NS, "tester");
    public final URI helper                 = createProperty(NS, "helper");
    public final URI programming_language   = createProperty(NS, "programming-language"); 
    public final URI os                     = createProperty(NS, "os");
    public final URI implement              = createProperty(NS, "implement");
    public final URI service_endpoint       = createProperty(NS, "service-endpoint"); 
    public final URI language               = createProperty(NS, "language");
    public final URI vendor                 = createProperty(NS, "vendor");
    public final URI platform               = createProperty(NS, "platform");
    public final URI audience               = createProperty(NS, "audience");
    public final URI blog                   = createProperty(NS, "blog");

    private DOAP(){
        super(NS);
    }
}
