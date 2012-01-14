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
 * This class models the <a href="http://purl.org/ontology/wo/">BBC Wildlife Ontology</a>.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 */
public class WO extends Vocabulary {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://purl.org/ontology/wo/";

    private static WO instance;

    public static WO getInstance() {
        if(instance == null) {
            instance = new WO();
        }
        return instance;
    }

    /**
     * The namespace of the vocabulary as a URI.
     */
    public final URI NAMESPACE = createURI(NS);

    /**
     * Generic class defining a biological species
     */
    public final URI species = createProperty("species");

    public final URI kingdomClass = createClass("Kingdom");

    public final URI divisionClass = createClass("Division");

    public final URI phylumClass = createClass("Phylum");

    public final URI orderClass = createClass("Order");

    public final URI genusClass = createClass("Genus");

    public final URI classClass = createClass("Class");

    /**
     * A family is a scientific grouping of closely related organisms.
     * It has smaller groups, called genera and species, within it.
     * A family can have a lot of members or only a few.
     * Examples of families include the cats (Felidae), the gulls (Laridae) and the grasses (Poaceae).
     */
    public final URI family = createClass("Family");

    /**
     * associates a taxon rank with a family 
     */
    public final URI familyProperty = createProperty("family");

    /**
     * Used to specify the name of a family as part of a Taxon Name
     */
    public final URI familyName = createProperty("familyName");

    /**
     * specifies the species part of a binomial name, allowing
     * this portion of the name to be explicitly described.
     * Therefore this property will typically only be used in TaxonNames
     * associated with species. The property is largely provided as a 
     * convenience to avoid applications having to parse the binomial name.
     */
    public final URI speciesName = createProperty("speciesName");

    /**
     * specifies the scientific name of a species, allowing
     * this portion of the name to be explicitly described.
     * Therefore this property will typically only be used in TaxonNames
     * associated with species. The property is largely provided as a
     * convenience to avoid applications having to parse the binomial name.
     */
    public final URI scientificName = createProperty("scientificName");

    public final URI kingdom = createProperty("kingdom");

    public final URI phylum = createProperty("phylum");

    public final URI order = createProperty("order");

    public final URI genus = createProperty("genus");

    public final URI division = createProperty("division");

    public final URI clazz = createProperty("class");

    public final URI kingdomName = createProperty("kingdomName");

    public final URI phylumName = createProperty("phylumName");

    public final URI orderName = createProperty("orderName");

    public final URI genusName = createProperty("genusName");

    public final URI divisionName = createProperty("divisionName");

    public final URI clazzName = createProperty("className");

    private URI createClass(String name) {
        return createClass(NS, name);
    }

    private URI createProperty(String name) {
        return createProperty(NS, name);
    }

    private WO(){
        super(NS);
    }

}