/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.extractor.TagSoupExtractionResult;
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.DOAC;
import org.deri.any23.vocab.FOAF;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.RDF;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.List;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hresume">hResume</a>
 * microformat.
 *
 * @author Gabriele Renzi
 */
public class HResumeExtractor extends EntityBasedMicroformatExtractor {

    public final static ExtractorFactory<HResumeExtractor> factory =
            SimpleExtractorFactory.create(
                    "html-mf-hresume",
                    PopularPrefixes.createSubset("rdf", "doac", "foaf"),
                    Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                    null,
                    HResumeExtractor.class
            );

    public ExtractorDescription getDescription() {
        return factory;
    }

    public String getBaseClassName() {
        return "hresume";
    }

    @Override
    protected void resetExtractor() {
        // Empty.
    }

    @Override
    protected boolean extractEntity(Node node, ExtractionResult out) {
        if (null == node) return false;
        BNode person = getBlankNodeFor(node);
        // we have a person, at least
        out.writeTriple(person, RDF.TYPE, FOAF.Person);
        final HTMLDocument fragment = new HTMLDocument(node);
        addSummary(fragment, person);
        addContact(fragment, person);
        addExperiences(fragment, person);
        addEducations(fragment, person);
        addAffiliations(fragment, person);
        addSkills(fragment, person);

        final TagSoupExtractionResult tser = (TagSoupExtractionResult) out;
        tser.addResourceRoot(
                DomUtils.getXPathListForNode(node),
                person,
                getDescription().getExtractorName()
        );

        return true;
    }

    private void addSummary(HTMLDocument doc, Resource person) {
        HTMLDocument.TextField summary = doc.getSingularTextField("summary");
        conditionallyAddStringProperty(
                getDescription().getExtractorName(),
                summary.source(),
                person,
                DOAC.summary,
                summary.value()
        );
    }

    private void addContact(HTMLDocument doc, Resource person) {
        List<Node> nodes = doc.findAllByClassName("contact");
        if (nodes.size() > 0)
            addBNodeProperty(
                    getDescription().getExtractorName(),
                    nodes.get(0),
                    person, FOAF.isPrimaryTopicOf, getBlankNodeFor(nodes.get(0))
            );
    }

    private void addExperiences(HTMLDocument doc, Resource person) {
        List<Node> nodes = doc.findAllByClassName("experience");
        for (Node node : nodes) {
            BNode exp = valueFactory.createBNode();
            if (addExperience(exp, new HTMLDocument(node)))
            addBNodeProperty(
                    getDescription().getExtractorName(),
                    node,
                    person, DOAC.experience, exp
            );
        }
    }

    private boolean addExperience(Resource exp, HTMLDocument document) {
        final String extractorName = getDescription().getExtractorName();
        final Node documentNode    = document.getDocument();
        String check = "";

        HTMLDocument.TextField value = document.getSingularTextField("title");
        check += value;
        conditionallyAddStringProperty(extractorName, value.source(), exp, DOAC.title, value.value().trim());

        value = document.getSingularTextField("dtstart");
        check += value;
        conditionallyAddStringProperty(extractorName, documentNode, exp, DOAC.start_date, value.value().trim());

        value = document.getSingularTextField("dtend");
        check += value;
        conditionallyAddStringProperty(extractorName, documentNode, exp, DOAC.end_date, value.value().trim());

        value = document.getSingularTextField("summary");
        check += value;
        conditionallyAddStringProperty(extractorName, documentNode, exp, DOAC.organization, value.value().trim());

        return !"".equals(check);
    }

    private void addEducations(HTMLDocument doc, Resource person) {
        List<Node> nodes = doc.findAllByClassName("education");
        for (Node node : nodes) {
            BNode exp = valueFactory.createBNode();
            if (addExperience(exp, new HTMLDocument(node)))
            addBNodeProperty(
                    getDescription().getExtractorName(),
                    node,
                    person, DOAC.education, exp
            );
        }
    }

    private void addAffiliations(HTMLDocument doc, Resource person) {
        List<Node> nodes = doc.findAllByClassName("affiliation");
        for (Node node : nodes) {
            addBNodeProperty(
                    getDescription().getExtractorName(),
                    node,
                    person, DOAC.affiliation, getBlankNodeFor(node)
            );
        }
    }

    private void addSkills(HTMLDocument doc, Resource person) {
        List<Node> nodes;
        final String extractorName = getDescription().getExtractorName();

        // Extracting data from single node.
        nodes = doc.findAllByClassName("skill");
        for (Node node : nodes) {
            conditionallyAddStringProperty(
                    extractorName,
                    node,
                    person, DOAC.skill, extractSkillValue(node)
            );
        }
        // Extracting from enlisting node.
        nodes = doc.findAllByClassName("skills");
        for(Node node : nodes) {
            String nodeText = node.getTextContent();
            String[] skills = nodeText.split(",");
            for(String skill : skills) {
                conditionallyAddStringProperty(
                        extractorName,
                        node,
                        person, DOAC.skill, skill.trim()
                );
            }
        }
    }

    private String extractSkillValue(Node n) {
        String name = n.getNodeName();
        String skill = null;
        if ("A".equals(name) && DomUtils.hasAttribute(n, "rel", "tag")) {
            skill = n.getAttributes().getNamedItem("href").getTextContent();
        } else {
            skill = n.getTextContent();
        }
        return skill;
    }

}
