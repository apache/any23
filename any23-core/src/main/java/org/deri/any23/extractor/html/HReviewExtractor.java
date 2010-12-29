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

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.extractor.TagSoupExtractionResult;
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.DCTERMS;
import org.deri.any23.vocab.REVIEW;
import org.deri.any23.vocab.VCARD;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.RDF;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.List;

import static org.deri.any23.extractor.html.HTMLDocument.TextField;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hreview">hReview</a>
 * microformat.
 *
 * @author Gabriele Renzi
 */
public class HReviewExtractor extends EntityBasedMicroformatExtractor {

    public final static ExtractorFactory<HReviewExtractor> factory =
            SimpleExtractorFactory.create(
                    "html-mf-hreview",
                    PopularPrefixes.createSubset("rdf", "vcard", "rev"),
                    Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                    null,
                    HReviewExtractor.class
            );

    public ExtractorDescription getDescription() {
        return factory;
    }

    protected String getBaseClassName() {
        return "hreview";
    }

    @Override
    protected void resetExtractor() {
        // Empty.
    }

    protected boolean extractEntity(Node node, ExtractionResult out) throws ExtractionException {
        BNode rev = getBlankNodeFor(node);
        out.writeTriple(rev, RDF.TYPE, REVIEW.Review);
        final HTMLDocument fragment = new HTMLDocument(node);
        addRating(fragment, rev);
        addSummary(fragment, rev);
        addTime(fragment, rev);
        addType(fragment, rev);
        addDescription(fragment, rev);
        addItem(fragment, rev);
        addReviewer(fragment, rev);

        final TagSoupExtractionResult tser = (TagSoupExtractionResult) out;
        tser.addResourceRoot(
                DomUtils.getXPathListForNode(node),
                rev,
                getDescription().getExtractorName()
        );

        return true;
    }

    private void addType(HTMLDocument doc, Resource rev) {
        TextField value = doc.getSingularTextField("type");
        conditionallyAddStringProperty(
                getDescription().getExtractorName(),
                value.source(),
                rev, REVIEW.type, value.value()
        );
    }

    private void addReviewer(HTMLDocument doc, Resource rev) {
        List<Node> nodes = doc.findAllByClassName("reviewer");
        if (nodes.size() > 0) {
            Node node0 = nodes.get(0);
            addBNodeProperty(
                    getDescription().getExtractorName(),
                    node0,
                    rev, REVIEW.reviewer, getBlankNodeFor(node0)
            );
        }
    }

    private void addItem(HTMLDocument root, BNode rev) throws ExtractionException {
        List<Node> nodes = root.findAllByClassName("item");
        for (Node node : nodes) {
            Resource item = findDummy(new HTMLDocument(node));
            addBNodeProperty(
                    getDescription().getExtractorName(),
                    node,
                    item, REVIEW.hasReview, rev
            );
        }
    }

    private Resource findDummy(HTMLDocument item) throws ExtractionException {
        Resource blank = getBlankNodeFor(item.getDocument());
        TextField val = item.getSingularTextField("fn");
        conditionallyAddStringProperty(
                getDescription().getExtractorName(),
                val.source(),
                blank, VCARD.fn, val.value()
        );
        final TextField url = item.getSingularUrlField("url");
        conditionallyAddResourceProperty(blank, VCARD.url, getHTMLDocument().resolveURI(url.value()));
        TextField pics[] = item.getPluralUrlField("photo");
        for (TextField pic : pics) {
            addURIProperty(blank, VCARD.photo, getHTMLDocument().resolveURI(pic.value()));
        }
        return blank;
    }

    private void addRating(HTMLDocument doc, Resource rev) {
        HTMLDocument.TextField value = doc.getSingularTextField("rating");
        conditionallyAddStringProperty(
                getDescription().getExtractorName(),
                value.source(), rev, REVIEW.rating, value.value()
        );
    }

    private void addSummary(HTMLDocument doc, Resource rev) {
        TextField value = doc.getSingularTextField("summary");
        conditionallyAddStringProperty(
                getDescription().getExtractorName(),
                value.source(),
                rev, REVIEW.title, value.value()
        );
    }

    private void addTime(HTMLDocument doc, Resource rev) {
        TextField value = doc.getSingularTextField("dtreviewed");
        conditionallyAddStringProperty(
                getDescription().getExtractorName(),
                value.source(),
                rev, DCTERMS.date, value.value()
        );
    }

    private void addDescription(HTMLDocument doc, Resource rev) {
        TextField value = doc.getSingularTextField("description");
        conditionallyAddStringProperty(
                getDescription().getExtractorName(),
                value.source(),
                rev, REVIEW.text, value.value()
        );
    }

}