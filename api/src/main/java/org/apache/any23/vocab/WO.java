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
 * The <a href="http://purl.org/ontology/wo/">Wildlife Ontology</a> vocabulary.</p>
 * A simple vocabulary for describing biological species and related taxa. 
 * The vocabulary defines terms for describing the names and ranking of taxa, 
 * as well as providing support for describing their habitats, conservation status, 
 * and behavioural characteristics, etc 
 * 
 * @author lewismc
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

    ////////////////////////////////////////////////////////////////////
    //CLASSES
    ////////////////////////////////////////////////////////////////////
    
    /** An adaptation is any feature of an animal or plant which makes 
     * it better suited for a particular habitat or to do a particular 
     * task. For instance, being streamlined is an adaptation to swimming 
     * fast and being able to survive on very little water is an 
     * adaptation to life in the desert.*/
    public final URI Adaption = createClass("Adaption");

    /** Animal Intelligence or animal cognition is the title 
     * given to a modern approach to the mental capacities of 
     * non-human animals. It has developed out of comparative 
     * psychology, but has also been strongly influenced by 
     * the approach of ethology, behavioral ecology, and 
     * evolutionary psychology.*/
    public final URI Animal_Intelligence = createClass("AnimalIntelligence");
    
    /** Behavioural pattern describes an animal's dominant way of 
     * life. Arboreal animals, for example, live in trees and 
     * nocturnal animals are active at night.*/
    public final URI BehaviouralPattern = createClass("BehaviouralPattern");
    
    /** A class is a scientific way to group related organisms 
     * together, some examples of classes being jellyfish, 
     * reptiles and sea urchins. Classes are big groups and 
     * contain within them smaller groupings called orders, 
     * families, genera and species.*/
    public final URI Class = createClass("Class");
    
    /** A collection of resources, including documents, 
     * multimedia files, programme clips and their associated .
     * taxa, which aims to showcase a particular aspect of 
     * natural history film-making, or illustrate aspects of 
     * the natural world. A collection provides an alternate 
     * way to organize content over and above the basic 
     * taxonomic hierarchy.*/
    public final URI Collection = createClass("Collection");
    
    /** Communication and senses are how an organism perceives 
     * the world - for instance through scent or sight - and 
     * how it sends messages or warnings to others.*/
    public final URI CommunicationAdaption = createClass("CommunicationAdaption");
    
    /** Conservation status as described by the IUCN Red List. 
     * Will typically have a number of properties including an 
     * official IUCN status, population trend, and a year of assessment.*/
    public final URI ConservationStatus = createClass("ConservationStatus");
    
    /** Ecosystem roles are about the part an animal or plant 
     * plays in sustaining or maintaining the habitat around them. 
     * Bees, for example, pollinate flowers, without which those 
     * plants would not produce fruits or seeds. Other species, 
     * such as dung beetles, play a vital role in keeping grasslands 
     * clear of animal waste and recycling valuable resources.*/
    public final URI EcosystemRole = createClass("EcosystemRole");
    
    /** Ecozones are a method of dividing up the Earth's surface. 
     * Each ecozone is a large area that contains a number of 
     * habitats, which are linked by the evolutionary history 
     * of the animals and plants within them. For instance one 
     * ecozone is Australasia, because its marsupials evolved 
     * in isolation to mammals in the rest of the world.*/
    public final URI Ecozone = createClass("Ecozone");
    
    /** Organisms that are adapted to extremes (known as Extremophiles) 
     * are organisms that thrives in and even may require physically 
     * or geochemically extreme conditions that are detrimental 
     * to the majority of life on Earth.*/
    public final URI ExtremesAdaptiation = createClass("ExtremesAdaptiation");
    
    /** A family is a scientific grouping of closely related organisms. 
     * It has smaller groups, called genera and species, within it. A 
     * family can have a lot of members or only a few. Examples of 
     * families include the cats (Felidae), the gulls (Laridae) and 
     * the grasses (Poaceae). Further Reading: 
     * http://en.wikipedia.org/wiki/Family_%28biology%29
     * http://www.bbc.co.uk/nature/family*/
    public final URI Family = createClass("Family");
    
    /** Feeding habits describe the dominant diet of a particular 
     * species or group of species, and how they go about obtaining it.*/
    public final URI FeedingHabit = createClass("FeedingHabit");
    
    /** Freshwater habitats include bogs, ponds, lakes, rivers and streams. 
     * About 3% of Earth's water is freshwater, but this includes the water 
     * locked up in the ice caps and trapped in rocks and soil as groundwater. 
     * Only a tiny fraction (0.014%) is surface water in the form of rivers, 
     * lakes and swamps.*/
    public final URI FreshwaterHabitat = createClass("FreshwaterHabitat");
    
    /** A genus is a scientific way of showing that species are very closed 
     * related to each other. In fact the first word of the species' 
     * scientific name is its genus. So for lions (Panthera leo), Panthera 
     * is the genus and tells us that they are closely related to tigers 
     * (Panthera tigris), because they share the name. Further Reading:
     * http://en.wikipedia.org/wiki/Genus
     * http://www.bbc.co.uk/nature/genus
     * */
    public final URI Genus = createClass("Genus");
    
    /** A habitat, or biome, is the type of environment in which plant 
     * and animals live. Habitat is dictated by what kinds of plants 
     * grow there, the climate and the geography. Rainforest, coral 
     * reefs and the tundra are all habitats where particular kinds 
     * of plants and animals might be found. Further Reading:
     * http://en.wikipedia.org/wiki/Habitat
     * http://www.bbc.co.uk/nature/habitats */
    public final URI Habitat = createClass("Habitat");
    
    /** Infraorders are a subdivision of suborders - thus infraorders 
     * are an intermediate rank of classification, that group together 
     * related superfamilies and families. The tarsiers are a infraorder 
     * of primates, containing a single extant family, whilst 
     * shrimps (Caridea) are an example of an infraorder which 
     * encompases many related superfamiles. Ceratopsia is the 
     * infraorder that contains all the horned dinosaurs. Further Reading:
     * http://en.wikipedia.org/wiki/Infraorder
     * http://www.bbc.co.uk/nature/infraorder*/
    public final URI Infraorder = createClass("Infraorder");
    
    /** Kingdoms are the major categories into which scientists 
     * divide up all living things. The main kingdoms are animals, 
     * plants, fungi and bacteria, although there are others. 
     * Each kingdom has its own suite of defining characteristics - 
     * for instance plants have rigid cell walls, whilst animals 
     * do not. Further Reading: 
     * http://en.wikipedia.org/wiki/Kingdom_%28biology%29
     * http://www.bbc.co.uk/nature/kingdom*/
    public final URI Kingdom = createClass("Kingdom");
    
    /** An organism's Life Cycle describes the stages in an 
     * organisms development including metamorphosis, courtship 
     * displays and parental care.*/
    public final URI Lifecycle = createClass("Lifecycle");
    
    /** Locomotion is how an animal gets around - for instance by swimming, 
     * flying or climbing.*/
    public final URI LocomotionAdaption = createClass("LocomotionAdaption");
    
    /** Approximately 71% of the Earth's surface is covered by the oceans, 
     * an area of some 223698816km/sq. Although marine life evolved around 
     * three billion years before life on land, marine habitats are relatively 
     * poorly studied and much of the ocean's depths remains unexplored.*/
    public final URI MarineHabitat = createClass("MarineHabitat");
    
    /** Morphology is anything to do with what a plant or animal looks like - 
     * its size, shape, colour or structure.*/
    public final URI Morphology = createClass("Morphology");
    
    /** An order is a scientific way to categorise related organisms. 
     * An order is a smaller grouping than a class, but bigger than 
     * a family or genus. Examples of orders are willows, cockroaches 
     * and primates. Further Reading: 
     * http://en.wikipedia.org/wiki/Order_%28biology%29
     * http://www.bbc.co.uk/nature/order*/
    public final URI Order = createClass("Order");
    
    /** A phylum - also known as a division when referring to plants - 
     * is a scientfic way of grouping together related organisms. 
     * All the members of a phylum have a common ancestor and 
     * anatomical similarities. For instance, all the arthropods 
     * have external skeletons. Phlya are large groups and are 
     * further subdivided into classes, orders, families and so on. 
     * Further Reading: http://en.wikipedia.org/wiki/Phylum
     * http://www.bbc.co.uk/nature/phylum*/
    public final URI Phylum = createClass("Phylum");

    /** Predation is catching and killing an animal in order to eat it. 
     * The prey can be chased, ambushed or caught in a trap such as a 
     * spider's web.*/
    public final URI PredationStrategy = createClass("PredationStrategy");
    
    /** A category in the IUCN red list, 2001. Further Reading:
     * http://www.iucnredlist.org/technical-documents/categories-and-criteria/2001-categories-criteria
     */
    public final URI RedListStatus = createClass("RedListStatus");
    
    /** Reproduction covers all the tactics and behaviours involved in obtaining 
     * a mate, conceiving the next generation and successfully raising them. 
     * It includes everything from plants being pollinated, to stags 
     * fighting over hinds, to lionesses babysitting their sisters' cubs.*/
    public final URI ReproductionStrategy = createClass("ReproductionStrategy");
    
    /** Social behaviour is all about how an animal interacts with members 
     * of its own species. For instance, does it live in a colony or on 
     * its own, does it fight to be top of the pecking order, or does 
     * it try to keep strangers away from its home?*/
    public final URI SocialBehaviour = createClass("SocialBehaviour");
    
    /** Generic class defining a biological species. Further Reading:
     * http://en.wikipedia.org/wiki/Species
     * http://www.bbc.co.uk/nature/species*/
    public final URI Species = createClass("Species");
    
    /** Suborders are an intermediate classification rank - 
     * an order can be split into many closely related suborders. 
     * Suborders are therefore of lower rank than a order, but 
     * higher than a infraorder or a family. All modern 
     * snakes are placed within a suborder (Serpentes). 
     * Theropoda is another suborder to which many 
     * famous carnivorous dinosaurs belong. Further Reading:
     * http://en.wikipedia.org/wiki/Suborder
     * http://www.bbc.co.uk/nature/suborder*/
    public final URI Suborder = createClass("Suborder");
    
    /** Subspecies are a rank of classification that is lower 
     * than a species. The differences between subspecies are 
     * less distinct than between species. Generally speaking 
     * two subspecies can successfully interbreed but two species 
     * cannot. If a subspecies can be shown to be different 
     * enough, then it can be elevated to the status of species. 
     * There are several subspecies of tiger (Panthera tigris) 
     * including the Bengal tiger (Panthera tigris tigris), 
     * Sumatran tiger (Panthera tigris sumatrae) and Siberian 
     * tiger (Panthera tigris altaica). Further Reading:
     * http://en.wikipedia.org/wiki/Subspecies
     * http://www.bbc.co.uk/nature/subspecies*/
    public final URI Subspecies = createClass("Subspecies");
    
    /** Superclass is an intermediate classification rank, or 
     * grouping, that sits directly above a class, ranking 
     * below a phylum or subphylum and containing one or more 
     * classes. The tetrapods are a superclass encompasing the 
     * amphibian, reptile, bird, mammal and dinosaur classes. 
     * Further Reading: http://en.wikipedia.org/wiki/Superclass_(biology)
     * http://www.bbc.co.uk/nature/superclass*/
    public final URI Superclass = createClass("Superclass");
    
    /** Superfamilies are an intermediate classification rank, 
     * or grouping, that is directly above a family. A 
     * superfamily may contain one or more related families. 
     * Dung beetles are a superfamily containing the stag, 
     * bess and scarab beetle families. There is also a 
     * superfamily of rodents (muroidea) containing six families 
     * of rats, mice, hamsters and gerbils. Further Reading:
     * http://en.wikipedia.org/wiki/Taxonomic_rank
     * http://www.bbc.co.uk/nature/superfamily*/
    public final URI Superfamily = createClass("Superfamily");
    
    /** Superorders are an intermediate classification rank or 
     * grouping that sit directly above an order. A superorder 
     * may contain several orders. Sharks are a good example of 
     * a superorder, grouping together eight living orders of shark, 
     * as well as, five extinct orders. Perhaps the most famous 
     * superorder of them all is Dinosauria - the dinosaurs!
     * Further Reading: http://en.wikipedia.org/wiki/Superorder
     * http://www.bbc.co.uk/nature/superorder*/
    public final URI Superorder = createClass("Superorder");
    
    /** Survival strategies include adaptations to changes in .
     * the organisms environment, including: hibernation, abscission and migration.*/
    public final URI SurvivalStrategy = createClass("SurvivalStrategy");
    
    /** A taxonomic name, describing the structure and provenance of a taxonomic name.*/
    public final URI TaxonName = createClass("TaxonName");
    
    /** Generic concept for a taxonomic rank such as a Genus or Species.
     * Further Reading: http://en.wikipedia.org/wiki/Taxonomic_rank
*/
    public final URI TaxonRank= createClass("TaxonRank");
    
    /** Terrestrial habitats include forests, grasslands, deserts 
     * and rainforests. They are typically defined by factors such 
     * as plant structure (trees and grasses), leaf types (eg broadleaf 
     * and needleleaf), plant spacing (forest, woodland, savanna) and climate.*/
    public final URI TerrestrialHabitat = createClass("TerrestrialHabitat");
    
    /** Tribes are a taxonomic rank that fall between family and genus. 
     * Tribes can also be split in to smaller related groups called subtribes. 
     * Tribes are mainly, but not always, used in botany to classify plants. 
     * The true grass family is divided up into many subfamiles and then into 
     * tribes, one of which is bamboo. The insect world also uses tribes 
     * as a classification rank, for example, bombini is the tribe of bumblebees.
     * Further Reading: http://en.wikipedia.org/wiki/Tribe_(biology)
     * http://www.bbc.co.uk/nature/tribe*/
    public final URI Tribe = createClass("Tribe");

    //////////////////////////////////////////////////////////
    //PROPERTIES
    //////////////////////////////////////////////////////////
    /**
     * associates a taxon rank with an adaptation which it displays 
     */
    public final URI adaptation = createProperty("adaptation");

    /**
     * associates a taxon rank with a class
     */
    public final URI clazz = createProperty("class");
    
    /**
     * associates a taxon rank, habitat, species, clip with a collection of which it is a member
     */
    public final URI collection = createProperty("collection");

    /**
     * associates a taxon rank with a description of a recent assessment of its conservation status
     */
    public final URI conservationStatus = createProperty("conservationStatus");

    /**
     * associates a habitat, ecozone, or taxon rank with a map depicting its distribution or location
     */
    public final URI distributionMap = createProperty("distributionMap");

    /** indicates that a habitat or a taxon rank can be found within an ecozone */
    public final URI ecozone = createProperty("ecozone");

    /** associates a taxon rank with a family */
    public final URI family = createProperty("family");

    /** associates a taxon rank with a genus */
    public final URI genus = createProperty("genus");
    
    /** associates a taxon rank with a habitat in which it grows. 
     * Sub-property of wo:habitat to be used for plants, fungi, etc */
    public final URI growsIn = createProperty("growsIn");

    /** associates a taxon rank with a habitat in which it may typically be found */
    public final URI habitat = createProperty("habitat");

    /** associates a taxon rank with a infraorder */
    public final URI infraorder = createProperty("infraorder");

    /** associates a taxon rank with a kingdom*/
    public final URI kingdom = createProperty("kingdom");

    /** associates a taxon rank with a habitat in which it lives. 
     * Sub-property of wo:habitat to be used for members of the animal kingdom */
    public final URI livesIn = createProperty("livesIn");

    /** associates a taxon rank with a taxon name */
    public final URI name = createProperty("name");

    /** associates a taxon rank with an order */
    public final URI order = createProperty("order");

    /** associates a taxon rank with a phylum */
    public final URI phylum = createProperty("phylum");
    
    /** associates a Conservation Status with a category in the IUCN Red List*/
    public final URI redListStatus = createProperty("redListStatus");
    
    /** associates a taxon rank with a species*/
    public final URI species = createProperty("species");
    
    /** associates a taxon rank with a subspecies*/
    public final URI subspecies = createProperty("subspecies");
    
    /** associates a taxon rank with a suborder*/
    public final URI suborder = createProperty("suborder");
    
    /** associates a taxon rank with a superclass*/
    public final URI superclass = createProperty("superclass");
    
    /** associates a taxon rank with a superfamily*/
    public final URI superfamily = createProperty("superfamily");
    
    /** associates a taxon rank with a superorder*/
    public final URI superorder = createProperty("superorder");
    
    /** associates a taxon rank with a tribe*/
    public final URI tribe = createProperty("tribe");
    
    //////////////////////////////////////////////////////////////
    //DATATYPE PROPERTIES
    //////////////////////////////////////////////////////////////
    
    /** Used to specify the name of a class as part of a Taxon Name*/
    public final URI clazzName = createProperty("className");
    
    /** associates a formal taxon name with a common version. E.g. 
     * Panthera leo might be associated with a common name of 'Lion'. 
     * A given taxon name may have several common names*/
    public final URI commonName = createProperty("commonName");
    
    /** Used to specify the name of a family as part of a Taxon Name*/
    public final URI familyName = createProperty("familyName");
    
    /** specifies the genus part of a binomial name, allowing this 
     * portion of the name to be explicitly described. Therefore this 
     * property will typically only be used in TaxonNames associated 
     * with species. The property is largely provided as a convenience 
     * to avoid applications having to parse the binomial name.*/
    public final URI genusName = createProperty("genusName");
    
    /** Used to specify the name of a infraorder as part of a Taxon Name*/
    public final URI infraorderName = createProperty("infraorderName");
    
    /** Used to specify the name of a kingdom as part of a Taxon Name */
    public final URI kingdomName = createProperty("kingdomName");
    
    /** Used to specify the name of an order as part of a Taxon Name */
    public final URI orderName = createProperty("orderName");
    
    /** Used to specify the name of a phylum as part of a Taxon Name */
    public final URI phylumName = createProperty("phylumName");
    
    /** provides some indication of the population trend associated with an 
     * assessment of a taxon's conversation status. The value of this 
     * property is a simple literal, and is recommended to be one of: 
     * Decreasing, Increasing, Stable, Unknown.*/
    public final URI populationTrend = createProperty("populationTrend");
    
    /** associates a taxon name with its formal scientific name. This 
     * may be a binomial name (e.g. Panthera leo) in the case of a 
     * species name, or a uninomial (e.g. Panthera) name in the case 
     * of a name associated with another taxon rank. In formal 
     * taxonomic naming conventions, the scientific name is often 
     * qualified with the source of the name, e.g. Panthera leo (Linnaeus, 1758).*/
    public final URI scientificName = createProperty("scientificName");
    
    /** associates a short description with a Collection.*/
    public final URI shortDescription = createProperty("shortDescription");
    
    /** specifies the species part of a binomial name, allowing this 
     * portion of the name to be explicitly described. Therefore 
     * this property will typically only be used in TaxonNames 
     * associated with species. The property is largely provided 
     * as a convenience to avoid applications having to parse the 
     * binomial name.*/
    public final URI speciesName = createProperty("speciesName");
    
    /** Used to specify the name of a suborder as part of a Taxon Name*/
    public final URI suborderName = createProperty("suborderName");
    
    /** Used to specify the name of a subspecies as part of a Taxon Name*/
    public final URI subspeciesName = createProperty("subspeciesName");
    
    /** Used to specify the name of a superspecies as part of a Taxon Name*/
    public final URI superspeciesName = createProperty("superspeciesName");
    
    /** Used to specify the name of a superclass as part of a Taxon Name*/
    public final URI superclassName = createProperty("superclassName");
    
    /** Used to specify the name of a superfamily as part of a Taxon Name*/
    public final URI superfamilyName = createProperty("superfamilyName");
    
    /** Used to specify the name of a superorder as part of a Taxon Name*/
    public final URI superorderName = createProperty("superorderName");
    
    /** a naming property, associating a formal taxonomic name with a Taxon 
     * Name instance. This property is a parent of a number of sub-properties 
     * that provide more specific terms for denoting names of families, phyla, 
     * species, etc.*/
    public final URI taxonomicName = createProperty("taxonomicName");
    
    /** description of the threat(s) that have been identified as part of 
     * the assessment of the Conservation Status of a taxon*/
    public final URI threatDescription = createProperty("threatDescription");
    
    /** Used to specify the name of a tribe as part of a Taxon Name*/
    public final URI tribeName = createProperty("tribeName");
    
    /** the year in which the conservation status was assessed.*/
    public final URI yearAssessed = createProperty("yearAssessed");
    
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