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
 * Vocabulary to map the <a href="http://microformats.org/wiki/hcard">h-card</a> microformat.
 *
 * @author Nisala Nirmana
 */
public class HCard extends Vocabulary {
    public static final String NS = SINDICE.NS + "hcard/";

    private static HCard instance;

    public static HCard getInstance() {
        if(instance == null) {
            instance = new HCard();
        }
        return instance;
    }

    public URI Card  = createClass(NS, "Card");
    public URI Address   = createClass(NS, "Address");
    public URI Geo = createClass(NS, "Geo");


    public URI name  = createProperty(NS, "name");
    public URI honorific_prefix   = createProperty(NS, "honorific-prefix");
    public URI given_name   = createProperty(NS, "given-name");
    public URI additional_name   = createProperty(NS, "additional-name");
    public URI family_name   = createProperty(NS, "family-name");
    public URI sort_string   = createProperty(NS, "sort-string");
    public URI honorific_suffix   = createProperty(NS, "honorific-suffix");
    public URI nickname  = createProperty(NS, "nickname");
    public URI email   = createProperty(NS, "email");
    public URI logo   = createProperty(NS, "logo");
    public URI photo  = createProperty(NS, "photo");
    public URI url   = createProperty(NS, "url");
    public URI uid   = createProperty(NS, "uid");
    public URI category   = createProperty(NS, "category");
    public URI tel  = createProperty(NS, "tel");
    public URI note   = createProperty(NS, "note");
    public URI bday   = createProperty(NS, "bday");
    public URI key  = createProperty(NS, "key");
    public URI org   = createProperty(NS, "org");
    public URI job_title   = createProperty(NS, "job-title");
    public URI role   = createProperty(NS, "role");
    public URI impp   = createProperty(NS, "impp");
    public URI sex  = createProperty(NS, "sex");
    public URI gender_identity   = createProperty(NS, "gender-identity");
    public URI anniversary   = createProperty(NS, "anniversary");
    public URI geo   = createProperty(NS, "geo");
    public URI adr   = createProperty(NS, "adr");

    public URI street_address  = createProperty(NS, "street-address");
    public URI extended_address   = createProperty(NS, "extended-address");
    public URI locality   = createProperty(NS, "locality");
    public URI region   = createProperty(NS, "region");
    public URI postal_code   = createProperty(NS, "postal-code");
    public URI country_name   = createProperty(NS, "country-name");

    public URI latitude   = createProperty(NS, "latitude");
    public URI longitude   = createProperty(NS, "longitude");
    public URI altitude   = createProperty(NS, "altitude");

    private HCard() {
        super(NS);
    }
}
