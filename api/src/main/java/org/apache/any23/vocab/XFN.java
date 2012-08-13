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

    private static XFN instance;

    public static XFN getInstance() {
        if(instance == null) {
            instance = new XFN();
        }
        return instance;
    }

    public final URI contact      = createProperty("contact");
    public final URI acquaintance = createProperty("acquaintance");
    public final URI friend       = createProperty("friend");
    public final URI met          = createProperty("met");
    public final URI coWorker     = createProperty("co-worker");
    public final URI colleague    = createProperty("colleague");
    public final URI coResident   = createProperty("co-resident");
    public final URI neighbor     = createProperty("neighbor");
    public final URI child        = createProperty("child");
    public final URI parent       = createProperty("parent");
    public final URI spouse       = createProperty("spouse");
    public final URI kin          = createProperty("kin");
    public final URI muse         = createProperty("muse");
    public final URI crush        = createProperty("crush");
    public final URI date         = createProperty("date");
    public final URI sweetheart   = createProperty("sweetheart");
    public final URI me           = createProperty("me");

    public final URI mePage = createProperty(NS, "mePage");

    private  Map<String, URI> PeopleXFNProperties;

    private Map<String, URI> HyperlinkXFNProperties;

    public URI getPropertyByLocalName(String localName) {
        return PeopleXFNProperties.get(localName);
    }

    public URI getExtendedProperty(String localName) {
        return HyperlinkXFNProperties.get(localName);
    }

    public boolean isXFNLocalName(String localName) {
        return PeopleXFNProperties.containsKey(localName);
    }

    public boolean isExtendedXFNLocalName(String localName) {
        return PeopleXFNProperties.containsKey(localName);
    }

    private URI createProperty(String localName) {
        if(HyperlinkXFNProperties == null) {
            HyperlinkXFNProperties = new HashMap<String, URI>();
        }
        if(PeopleXFNProperties == null) {
            PeopleXFNProperties =  new HashMap<String, URI>();
        }

        URI result = createProperty(NS, localName + "-hyperlink");
        HyperlinkXFNProperties.put(localName, result);

        result = createProperty(NS, localName);
        PeopleXFNProperties.put(localName, result);
        return result;
    }

    private XFN(){
        super(NS);
    }

}
