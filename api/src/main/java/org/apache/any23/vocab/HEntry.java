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

import org.eclipse.rdf4j.model.IRI;

/**
 * Vocabulary to map the <a href="http://microformats.org/wiki/hentry">h-entry</a> microformat.
 *
 * @author Nisala Nirmana
 */
public class HEntry extends Vocabulary {

    public static final String NS = SINDICE.NS + "hentry/";

    private static HEntry instance;

    public static HEntry getInstance() {
        if(instance == null) {
            instance = new HEntry();
        }
        return instance;
    }

    public IRI Entry  = createClass(NS, "Entry");
    public IRI author   = createClass(NS, "author");
    public IRI location = createClass(NS, "location");


    public IRI name  = createProperty(NS, "name");
    public IRI summary   = createProperty(NS, "summary");
    public IRI content   = createProperty(NS, "content");
    public IRI published   = createProperty(NS, "published");
    public IRI updated   = createProperty(NS, "updated");
    public IRI category   = createProperty(NS, "category");
    public IRI url   = createProperty(NS, "url");
    public IRI uid  = createProperty(NS, "uid");
    public IRI syndication   = createProperty(NS, "syndication");
    public IRI in_reply_to   = createProperty(NS, "in-reply-to");

    private HEntry() {
        super(NS);
    }

}
