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

package org.deri.any23.vocab;

import org.openrdf.model.URI;

/**
 * Vocabulary definitions from vcard.owl
 */
public class VCARD extends Vocabulary {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.w3.org/2006/vcard/ns#";

    private static VCARD instance;

    public static VCARD getInstance() {
        if(instance == null) {
            instance = new VCARD();
        }
        return instance;
    }

    /**
     * The namespace of the vocabulary as a URI.
     */
    public final URI NAMESPACE = createURI(NS);

    /**
     * An additional part of a person's name.
     */
    public final URI additional_name = createProperty("additional-name");

    /**
     * A postal or street address of a person.
     */
    public final URI adr = createProperty("adr");

    /**
     * A person that acts as one's agent.
     */
    public final URI agent = createProperty("agent");

    /**
     * The birthday of a person.
     */
    public final URI bday = createProperty("bday");

    /**
     * A category of a vCard.
     */
    public final URI category = createProperty("category");

    /**
     * A class (e.g., public, private, etc.) of a vCard.
     */
    public final URI class_ = createProperty("class");

    /**
     * The country of a postal address.
     */
    public final URI country_name = createProperty("country-name");

    /**
     * An email address.
     */
    public final URI email = createProperty("email");

    /**
     * The extended address of a postal address.
     */
    public final URI extended_address = createProperty("extended-address");

    /**
     * A family name part of a person's name.
     */
    public final URI family_name = createProperty("family-name");

    /**
     * A fax number of a person.
     */
    public final URI fax = createProperty("fax");

    /**
     * A formatted name of a person.
     */
    public final URI fn = createProperty("fn");

    /**
     * A geographic location associated with a person.
     */
    public final URI geo = createProperty("geo");

    /**
     * A given name part of a person's name.
     */
    public final URI given_name = createProperty("given-name");

    /**
     * A home address of a person.
     */
    public final URI homeAdr = createProperty("homeAdr");

    /**
     * A home phone number of a person.
     */
    public final URI homeTel = createProperty("homeTel");

    /**
     * An honorific prefix part of a person's name.
     */
    public final URI honorific_prefix = createProperty("honorific-prefix");

    /**
     * An honorific suffix part of a person's name.
     */
    public final URI honorific_suffix = createProperty("honorific-suffix");

    /**
     * A key (e.g, PKI key) of a person.
     */
    public final URI key = createProperty("key");

    /**
     * The formatted version of a postal address (a string with embedded line breaks,
     * punctuation, etc.).
     */
    public final URI label = createProperty("label");

    /**
     * The latitude of a geographic location.
     */
    public final URI latitude = createProperty("latitude");

    /**
     * The locality (e.g., city) of a postal address.
     */
    public final URI locality = createProperty("locality");

    /**
     * A logo associated with a person or their organization.
     */
    public final URI logo = createProperty("logo");

    /**
     * The longitude of a geographic location.
     */
    public final URI longitude = createProperty("longitude");

    /**
     * A mailer associated with a vCard.
     */
    public final URI mailer = createProperty("mailer");

    /**
     * A mobile email address of a person.
     */
    public final URI mobileEmail = createProperty("mobileEmail");

    /**
     * A mobile phone number of a person.
     */
    public final URI mobileTel = createProperty("mobileTel");

    /**
     * The components of the name of a person.
     */
    public final URI n = createProperty("n");

    /**
     * The nickname of a person.
     */
    public final URI nickname = createProperty("nickname");

    /**
     * Notes about a person on a vCard.
     */
    public final URI note = createProperty("note");

    /**
     * An organization associated with a person.
     */
    public final URI org = createProperty("org");

    /**
     * The name of an organization.
     */
    public final URI organization_name = createProperty("organization-name");

    /**
     * The name of a unit within an organization.
     */
    public final URI organization_unit = createProperty("organization-unit");

    /**
     * An email address unaffiliated with any particular organization or employer;
     * a personal email address.
     */
    public final URI personalEmail = createProperty("personalEmail");

    /**
     * A photograph of a person.
     */
    public final URI photo = createProperty("photo");

    /**
     * The post office box of a postal address.
     */
    public final URI post_office_box = createProperty("post-office-box");

    /**
     * The postal code (e.g., U.S. ZIP code) of a postal address.
     */
    public final URI postal_code = createProperty("postal-code");

    /**
     * The region (e.g., state or province) of a postal address.
     */
    public final URI region = createProperty("region");

    /**
     * The timestamp of a revision of a vCard.
     */
    public final URI rev = createProperty("rev");

    /**
     * A role a person plays within an organization.
     */
    public final URI role = createProperty("role");

    /**
     * A version of a person's name suitable for collation.
     */
    public final URI sort_string = createProperty("sort-string");

    /**
     * A sound (e.g., a greeting or pronounciation) of a person.
     */
    public final URI sound = createProperty("sound");

    /**
     * The street address of a postal address.
     */
    public final URI street_address = createProperty("street-address");

    /**
     * A telephone number of a person.
     */
    public final URI tel = createProperty("tel");

    /**
     * A person's title.
     */
    public final URI title = createProperty("title");

    /**
     * A timezone associated with a person.
     */
    public final URI tz = createProperty("tz");

    /**
     * A UID of a person's vCard.
     */
    public final URI uid = createProperty("uid");

    /**
     * An (explicitly) unlabeled address of a person.
     */
    public final URI unlabeledAdr = createProperty("unlabeledAdr");

    /**
     * An (explicitly) unlabeled email address of a person.
     */
    public final URI unlabeledEmail = createProperty("unlabeledEmail");

    /**
     * An (explicitly) unlabeled phone number of a person.
     */
    public final URI unlabeledTel = createProperty("unlabeledTel");

    /**
     * A URL associated with a person.
     */
    public final URI url = createProperty("url");

    /**
     * A work address of a person.
     */
    public final URI workAdr = createProperty("workAdr");

    /**
     * A work email address of a person.
     */
    public final URI workEmail = createProperty("workEmail");

    /**
     * A work phone number of a person.
     */
    public final URI workTel = createProperty("workTel");

    /**
     * Resources that are vCard (postal) addresses.
     */
    public final URI Address = createURI("http://www.w3.org/2006/vcard/ns#Address");

    public final URI addressType = createProperty("addressType");

    /**
     * Resources that are vCard Telephones.
     */
    public final URI Telephone = createURI("http://www.w3.org/2006/vcard/ns#Address");

    /**
     * Resources that are vCard geographic locations.
     */
    public final URI Location = createURI("http://www.w3.org/2006/vcard/ns#Location");

    /**
     * Resources that are vCard personal names.
     */
    public final URI Name = createURI("http://www.w3.org/2006/vcard/ns#Name");

    /**
     * Resources that are vCard organizations.
     */
    public final URI Organization = createURI("http://www.w3.org/2006/vcard/ns#Organization");

    /**
     * Resources that are vCards
     */
    public final URI VCard = createURI("http://www.w3.org/2006/vcard/ns#VCard");


    private URI createProperty(String localName) {
        return createProperty(NS, localName);
    }

    public VCARD(){
        super(NS);
    }
}
