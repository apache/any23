package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.extractor.TagSoupExtractionResult;
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.WO;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.w3c.dom.Node;

import java.util.Arrays;

/**
 * Extractor able to extract the <a href="http://microformats.org/wiki/species">Species Microformat</a>.
 * The data are represented using the
 * <a href="http://www.bbc.co.uk/ontologies/wildlife/2010-02-22.shtml">BBC Wildlife Ontology</a>.
 *
 * @see org.deri.any23.vocab.WO
 * @author Davide Palmisano (dpalmisano@gmail.com)
 */
public class SpeciesExtractor extends EntityBasedMicroformatExtractor {

    private static final String[] classes = {
            "kingdom",
            "division",
            "phylum",
            "order",
            "family",
            "genus",
            "species",
            "class",
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
     * Returns the base class name for the extractor.
     *
     * @return a string containing the base of the extractor.
     */
    @Override
    protected String getBaseClassName() {
        return "biota";
    }

    /**
     * Resets the internal status of the extractor to prepare it to a new extraction section.
     */
    @Override
    protected void resetExtractor() {
        // empty
    }

    /**
     * Extracts an entity from a <i>DOM</i> node.
     *
     * @param node the DOM node.
     * @param out  the extraction result collector.
     * @return <code>true</code> if the extraction has produces something, <code>false</code> otherwise.
     * @throws org.deri.any23.extractor.ExtractionException
     *
     */
    @Override
    protected boolean extractEntity(Node node, ExtractionResult out) throws ExtractionException {
        BNode biota = getBlankNodeFor(node);
        conditionallyAddResourceProperty(biota, RDF.TYPE, WO.species);

        final HTMLDocument fragment = new HTMLDocument(node);
        addNames(fragment, biota);
        addClasses(fragment, biota);

        final TagSoupExtractionResult tser = (TagSoupExtractionResult) out;
        tser.addResourceRoot(
                DomUtils.getXPathListForNode(node),
                biota,
                getDescription().getExtractorName()
        );

        return true;
    }

    private void addNames(HTMLDocument doc, Resource biota) throws ExtractionException {
        HTMLDocument.TextField binomial = doc.getSingularTextField("binomial");
        conditionallyAddStringProperty(
                getDescription().getExtractorName(),
                binomial.source(), biota, WO.scientificName, binomial.value()
        );
        HTMLDocument.TextField vernacular = doc.getSingularTextField("vernacular");
        conditionallyAddStringProperty(
                getDescription().getExtractorName(),
                vernacular.source(), biota, WO.speciesName, vernacular.value()
        );
    }

    private void addClassesName(HTMLDocument doc, Resource biota) throws ExtractionException {
        for (String clazz : classes) {
            HTMLDocument.TextField classTextField = doc.getSingularTextField(clazz);
            conditionallyAddStringProperty(getDescription().getExtractorName(),
                    classTextField.source(), biota, resolvePropertyName(clazz), classTextField.value());
        }
    }

    private void addClasses(HTMLDocument doc, Resource biota) throws ExtractionException {
        for(String clazz : classes) {
            HTMLDocument.TextField classTextField = doc.getSingularUrlField(clazz);
            if(classTextField.source() != null) {
                BNode classBNode = getBlankNodeFor(classTextField.source());
                addBNodeProperty(biota, WO.getProperty(clazz), classBNode);
                conditionallyAddResourceProperty(classBNode, RDF.TYPE, resolveClassName(clazz));
                HTMLDocument fragment = new HTMLDocument(classTextField.source());
                addClassesName(fragment, classBNode);
            }
        }
    }

    private URI resolvePropertyName(String clazz) {
        return WO.getProperty(
                String.format(
                        "%sName",
                        clazz
                )
        );
    }

    private URI resolveClassName(String clazz) {
        String upperCaseClass = clazz.substring(0, 1);
        return WO.getResource(
                String.format("%s%s",
                        upperCaseClass.toUpperCase(),
                        clazz.substring(1)
                )
        );
    }
}
