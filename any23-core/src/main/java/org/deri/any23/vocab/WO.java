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
 * This class models the <a href="http://purl.org/ontology/wo/">BBC Wildlife Ontology</a>.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 */
public class WO extends Vocabulary {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://purl.org/ontology/wo/";

    /**
     * The namespace of the vocabulary as a URI.
     */
    public static final URI NAMESPACE = createResource(NS);

    /**
     * Generic class defining a biological species
     */
    public static final URI species = createResource("species");

    public static final URI kingdomClass = createResource("Kingdom");

    public static final URI divisionClass = createResource("Division");

    public static final URI phylumClass = createResource("Phylum");

    public static final URI orderClass = createResource("Order");

    public static final URI genusClass = createResource("Genus");

    public static final URI classClass = createResource("Class");

    /**
     * A family is a scientific grouping of closely related organisms.
     * It has smaller groups, called genera and species, within it.
     * A family can have a lot of members or only a few.
     * Examples of families include the cats (Felidae), the gulls (Laridae) and the grasses (Poaceae).
     */
    public static final URI family = createResource("Family");

    /**
     * associates a taxon rank with a family 
     */
    public static final URI familyProperty = createProperty("family");

    /**
     * Used to specify the name of a family as part of a Taxon Name
     */
    public static final URI familyName = createProperty("familyName");

    /**
     * specifies the species part of a binomial name, allowing
     * this portion of the name to be explicitly described.
     * Therefore this property will typically only be used in TaxonNames
     * associated with species. The property is largely provided as a 
     * convenience to avoid applications having to parse the binomial name.
     */
    public static final URI speciesName = createProperty("speciesName");

    /**
     * specifies the scientific name of a species, allowing
     * this portion of the name to be explicitly described.
     * Therefore this property will typically only be used in TaxonNames
     * associated with species. The property is largely provided as a
     * convenience to avoid applications having to parse the binomial name.
     */
    public static final URI scientificName = createProperty("scientificName");

    public static final URI kingdom = createProperty("kingdom");

    public static final URI phylum = createProperty("phylum");

    public static final URI order = createProperty("order");

    public static final URI genus = createProperty("genus");

    public static final URI division = createProperty("division");

    public static final URI clazz = createProperty("class");

    public static final URI kingdomName = createProperty("kingdomName");

    public static final URI phylumName = createProperty("phylumName");

    public static final URI orderName = createProperty("orderName");

    public static final URI genusName = createProperty("genusName");

    public static final URI divisionName = createProperty("divisionName");

    public static final URI clazzName = createProperty("className");

    private static Map<String, URI> localNamesMap;

    public static URI getResource(String name) {
        URI res = localNamesMap.get(name);
        if (null == res)
            throw new RuntimeException("heck, you are using a non existing URI:" + name);
        return res;
    }

    public static URI getProperty(String name) {
        return getResource(name);
    }

    private static URI createResource(String string) {
        URI res = createURI(NS + string);
        if(localNamesMap == null) {
            localNamesMap = new HashMap<String, URI>(10);
        }
        localNamesMap.put(string, res);
        return res;
    }

    private static URI createProperty(String string) {
        URI res = createURI(NS + string);
        if(localNamesMap == null) {
            localNamesMap = new HashMap<String, URI>(10);
        }
        localNamesMap.put(string, res);
        return res;
    }

    private WO(){}

}