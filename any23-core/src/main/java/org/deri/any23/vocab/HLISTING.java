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

import java.util.HashMap;
import java.util.Map;

/**
 * Class modeling the <a href="http://microformats.org/wiki/hlisting-proposal">hListing</a> vocabulary.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 * 
 */
public class HLISTING extends Vocabulary {

    public static final String NS = "http://sindice.com/hlisting/0.1/";

    public static final URI action = createProperty("action");
    public static final URI lister = createProperty("lister"); // ranges over Lister
    public static final URI item   = createProperty("item"  );

    // my classes
    public static final URI Listing = createResource("Listing");
    public static final URI Lister  = createResource("Lister" ); // isa FOAF.Person
    public static final URI Item    = createResource("Item"   ); // isa ?

    public static final URI sell     = createResource("sell"    );
    public static final URI rent     = createResource("rent"    );
    public static final URI trade    = createResource("trade"   );
    public static final URI meet     = createResource("meet"    );
    public static final URI announce = createResource("announce");
    public static final URI offer    = createResource("offer"   );
    public static final URI wanted   = createResource("wanted"  );
    public static final URI event    = createResource("event"   );
    public static final URI service  = createResource("service" );

    public static final URI tel       = VCARD.tel;
    public static final URI dtlisted  = createProperty("dtlisted" );
    public static final URI dtexpired = createProperty("dtexpired");
    public static final URI price     = createProperty("price"    );

    public static final URI description = createProperty("description");
    public static final URI summary     = createProperty("summary"    );
    public static final URI permalink   = createProperty("permalink"  );

    public static final URI region = VCARD.region;
    public static final URI postOfficeBox   = VCARD.post_office_box;
    public static final URI locality        = VCARD.locality;
    public static final URI extendedAddress = VCARD.extended_address;
    public static final URI streetAddress   = VCARD.street_address;
    public static final URI postalCode      = VCARD.postal_code;
    public static final URI countryName     = VCARD.country_name;

    public static final URI listerUrl  = createProperty("listerUrl" );
    public static final URI listerName = createProperty("listerName");
    public static final URI itemName   = createProperty("itemName"  );
    public static final URI itemUrl    = createProperty("itemUrl"   );
    public static final URI itemPhoto  = createProperty("itemPhoto" );
    public static final URI listerOrg  = createProperty("listerOrg" );
    public static final URI listerLogo = createProperty("listerLogo");

    private static Map<String, URI> resourceMap;
    private static Map<String, URI> propertyMap;
   
    public static URI getResource(String localName) {
        return resourceMap.get(localName);
    }

    public static URI getPropertyCamelized(String klass) {
        String[] names = klass.split("\\W");
        String result = names[0];
        for (int i = 1; i < names.length; i++) {
            String tmp = names[i];
            result += tmp.replaceFirst("(.)", tmp.substring(0, 1).toUpperCase());
        }
        if (!propertyMap.containsKey(result))
            throw new RuntimeException("Fool, using a non existing URI!");
        return propertyMap.get(result);
    }

    private static URI createProperty(String localName) {
        URI result = createURI(NS, localName);
        if(propertyMap == null) {
            propertyMap = new HashMap<String, URI>();
        }
        propertyMap.put(localName, result);
        return result;
    }

    private static URI createResource(String localName) {
        URI result = createURI(NS, localName);
        if(resourceMap == null) {
            resourceMap = new HashMap<String, URI>();
        }
        resourceMap.put(localName, result);
        return result;
    }

    private HLISTING(){}

}
