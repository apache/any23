package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.WO;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.List;

/**
 * Extractor able to extract the <a href="http://microformats.org/wiki/species">Species Microformat</a>.
 * The data are represented using the
 * <a href="http://www.bbc.co.uk/ontologies/wildlife/2010-02-22.shtml">BBC Wildlife Ontology</a>.
 *
 * @see {@link org.deri.any23.vocab.WO}
 * @author Davide Palmisano (dpalmisano@gmail.com)
 */
public class SpeciesExtractor extends MicroformatExtractor {

    private static final String[] binomials = {
            "bird",
            "mammal",
            "fish",
            "insect",
            "arachnid",
            "plant",
            "fungi"
    };

    public final static ExtractorFactory<SpeciesExtractor> factory =
            SimpleExtractorFactory.create(
                    "html-mf-species",
                    PopularPrefixes.createSubset("rdf", "wo"),
                    Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                    null,
                    SpeciesExtractor.class
            );

    /**
     * Returns the description of this extractor.
     *
     * @return a human readable description.
     */
    @Override
    public ExtractorDescription getDescription() {
        return factory;
    }

    /**
     * Performs the extraction of the data and writes them to the model.
     * The nodes generated in the model can have any name or implicit label
     * but if possible they </i>SHOULD</i> have names (either URIs or AnonId) that
     * are uniquely derivable from their position in the DOM tree, so that
     * multiple extractors can merge information.
     */
    @Override
    protected boolean extract() throws ExtractionException {
        final HTMLDocument document = getHTMLDocument();
        final URI documentURI = getDocumentURI();
        boolean foundAny = false;
        List<Node> biotas = document.findAllByClassName("biota");
        if(biotas.size() > 0) {
            for (Node biota : biotas)
                foundAny |= extractBiotas(biota);
        }
        if(foundAny)
            addURIProperty(documentURI, RDF.TYPE, WO.species);
        return foundAny;
    }

    private boolean extractBiotas(Node node) throws ExtractionException {
        boolean foundAny = false;
        Resource biota = valueFactory.createBNode();
        addURIProperty(biota, RDF.TYPE, WO.species);
        foundAny |= extractNames(node, biota);
        foundAny |= extractFamily(node, biota);
        return foundAny;
    }

    private boolean extractNames(Node node, Resource resource) throws ExtractionException {
        boolean foundAny = false;
        List<Node> vernacularNames = DomUtils.findAllByClassName(node, "vernacular");
        List<Node> binomialNames = DomUtils.findAllByClassName(node, "binominal");

        final String extractor = getDescription().getExtractorName();
        if (binomialNames.size() > 0) {
            for (Node binomialName : binomialNames) {
                conditionallyAddStringProperty(
                        extractor,
                        binomialName,
                        resource, WO.scientificName, binomialName.getTextContent()
                );
            }
            foundAny = true;
        }

        if (vernacularNames.size() > 0) {
            for (Node vernacularName : vernacularNames) {
                conditionallyAddStringProperty(
                        extractor,
                        vernacularName,
                        resource, WO.speciesName, vernacularName.getTextContent()
                );
            }
            foundAny = true;
        }
        return foundAny;
    }

    private boolean extractFamily(Node node, Resource resource) throws ExtractionException {
        boolean foundAny = false;
        final String extractor = getDescription().getExtractorName();
        for (String binomial : binomials) {
            List<Node> biotas = DomUtils.findAllByClassName(node, binomial);
            if (biotas.size() == 0)
                return foundAny;
            for (Node biotaNode : biotas) {
                BNode familyNode = valueFactory.createBNode();
                addURIProperty(familyNode, RDF.TYPE, WO.family);
                conditionallyAddStringProperty(
                        extractor,
                        biotaNode,
                        familyNode, WO.familyName, binomial
                );
                addBNodeProperty(resource, WO.familyProperty, familyNode);
                foundAny |= extractNames(biotaNode, resource);
            }
            foundAny = true;
        }
        return foundAny;
    }

}
