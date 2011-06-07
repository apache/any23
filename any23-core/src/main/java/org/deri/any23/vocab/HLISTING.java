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
    public final URI Listing = createResource("Listing");
    public final URI Lister  = createResource("Lister" ); // isa FOAF.Person
    public final URI Item    = createResource("Item"   ); // isa ?

    // Properties.
    public final URI action = createProperty("action");
    public final URI lister = createProperty("lister"); // ranges over Lister
    public final URI item   = createProperty("item"  );

    public final URI sell     = createResource("sell"    );
    public final URI rent     = createResource("rent"    );
    public final URI trade    = createResource("trade"   );
    public final URI meet     = createResource("meet"    );
    public final URI announce = createResource("announce");
    public final URI offer    = createResource("offer"   );
    public final URI wanted   = createResource("wanted"  );
    public final URI event    = createResource("event"   );
    public final URI service  = createResource("service" );

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

    private URI createResource(String localName) {
        return createResource(NS, localName);
    }

    private HLISTING(){}

}
