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
 * Vocabulary class for <a href="http://gmpg.org/xfn/11">XFN</a>, as per
 * <a href="http://vocab.sindice.com/xfn/guide.html">Expressing XFN in RDF</a>.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class XFN extends Vocabulary {

    public static final String NS = "http://vocab.sindice.com/xfn#";

    public static final URI contact      = createProperty("contact");
    public static final URI acquaintance = createProperty("acquaintance");
    public static final URI friend       = createProperty("friend");
    public static final URI met          = createProperty("met");
    public static final URI coWorker     = createProperty("co-worker");
    public static final URI colleague    = createProperty("colleague");
    public static final URI coResident   = createProperty("co-resident");
    public static final URI neighbor     = createProperty("neighbor");
    public static final URI child        = createProperty("child");
    public static final URI parent       = createProperty("parent");
    public static final URI spouse       = createProperty("spouse");
    public static final URI kin          = createProperty("kin");
    public static final URI muse         = createProperty("muse");
    public static final URI crush        = createProperty("crush");
    public static final URI date         = createProperty("date");
    public static final URI sweetheart   = createProperty("sweetheart");
    public static final URI me           = createProperty("me");

    public static final URI mePage = createURI(NS, "mePage");

    private static  Map<String, URI> PeopleXFNProperties;

    private static Map<String, URI> HyperlinkXFNProperties;

    public static URI getPropertyByLocalName(String localName) {
        return PeopleXFNProperties.get(localName);
    }

    public static URI getExtendedProperty(String localName) {
        return HyperlinkXFNProperties.get(localName);
    }

    public static boolean isXFNLocalName(String localName) {
        return PeopleXFNProperties.containsKey(localName);
    }

    public static boolean isExtendedXFNLocalName(String localName) {
        return PeopleXFNProperties.containsKey(localName);
    }

    private static URI createProperty(String localName) {
        if(HyperlinkXFNProperties == null) {
            HyperlinkXFNProperties = new HashMap<String, URI>();
        }
        if(PeopleXFNProperties == null) {
            PeopleXFNProperties =  new HashMap<String, URI>();
        }

        URI result = createURI(NS + localName + "-hyperlink");
        HyperlinkXFNProperties.put(localName, result);

        result = createURI(NS, localName);
        PeopleXFNProperties.put(localName, result);
        return result;
    }

    private XFN(){}

}
