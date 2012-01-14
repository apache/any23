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
 * Class modeling the <a href="http://microformats.org/wiki/hlisting-proposal">hListing</a> vocabulary.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 * 
 */
public class HLISTING extends Vocabulary {

    public static final String NS = "http://sindice.com/hlisting/0.1/";

    private static HLISTING instance;

    public static HLISTING getInstance() {
        if(instance == null) {
            instance = new HLISTING();
        }
        return instance;
    }

    // Resources.
    public final URI Listing = createClass("Listing");
    public final URI Lister  = createClass("Lister" ); // isa FOAF.Person
    public final URI Item    = createClass("Item"   ); // isa ?

    // Properties.
    public final URI action = createProperty("action");
    public final URI lister = createProperty("lister"); // ranges over Lister
    public final URI item   = createProperty("item"  );

    public final URI sell     = createClass("sell"    );
    public final URI rent     = createClass("rent"    );
    public final URI trade    = createClass("trade"   );
    public final URI meet     = createClass("meet"    );
    public final URI announce = createClass("announce");
    public final URI offer    = createClass("offer"   );
    public final URI wanted   = createClass("wanted"  );
    public final URI event    = createClass("event"   );
    public final URI service  = createClass("service" );

    public final URI tel       = VCARD.getInstance().tel;
    public final URI dtlisted  = createProperty("dtlisted" );
    public final URI dtexpired = createProperty("dtexpired");
    public final URI price     = createProperty("price"    );

    public final URI description = createProperty("description");
    public final URI summary     = createProperty("summary"    );
    public final URI permalink   = createProperty("permalink"  );

    public final URI region          = VCARD.getInstance().region;
    public final URI postOfficeBox   = VCARD.getInstance().post_office_box;
    public final URI locality        = VCARD.getInstance().locality;
    public final URI extendedAddress = VCARD.getInstance().extended_address;
    public final URI streetAddress   = VCARD.getInstance().street_address;
    public final URI postalCode      = VCARD.getInstance().postal_code;
    public final URI countryName     = VCARD.getInstance().country_name;

    public final URI listerUrl  = createProperty("listerUrl" );
    public final URI listerName = createProperty("listerName");
    public final URI itemName   = createProperty("itemName"  );
    public final URI itemUrl    = createProperty("itemUrl"   );
    public final URI itemPhoto  = createProperty("itemPhoto" );
    public final URI listerOrg  = createProperty("listerOrg" );
    public final URI listerLogo = createProperty("listerLogo");

    private URI createProperty(String localName) {
        return createProperty(NS, localName);
    }

    private URI createClass(String localName) {
        return createClass(NS, localName);
    }

    private HLISTING(){
        super(NS);
    }

}
