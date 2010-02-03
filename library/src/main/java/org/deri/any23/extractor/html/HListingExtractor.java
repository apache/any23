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
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.FOAF;
import org.deri.any23.vocab.HLISTING;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hlisting">hListing</a>
 * microformat.
 *
 * @author Gabriele Renzi
 */
public class HListingExtractor extends EntityBasedMicroformatExtractor {

    private static final Set<String> ActionClasses = new HashSet<String>() {
        {
            add("sell"    );
            add("rent"    );
            add("trade"   );
            add("meet"    );
            add("announce");
            add("offer"   );
            add("wanted"  );
            add("event"   );
            add("service" );
        }
    };

    private HTMLDocument fragment;

    public final static ExtractorFactory<HListingExtractor> factory =
            SimpleExtractorFactory.create(
                    "html-mf-hlisting",
                    PopularPrefixes.createSubset("rdf", "hlisting"),
                    Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                    null,
                    HListingExtractor.class
            );

    public ExtractorDescription getDescription() {
        return factory;
    }

    protected String getBaseClassName() {
        return "hlisting";
    }

    @Override
    protected boolean extractEntity(Node node, ExtractionResult out) throws ExtractionException {
        this.fragment = new HTMLDocument(node);
        BNode listing = getBlankNodeFor(node);
        out.writeTriple(listing, RDF.TYPE, HLISTING.Listing);

        for (String action : findActions(fragment)) {
            out.writeTriple(listing, HLISTING.action, HLISTING.getResource(action));
        }
        out.writeTriple(listing, HLISTING.lister, addLister() );
        addItem(listing);
        addDateTimes(listing);
        addPrice(listing);
        addDescription(listing);
        addSummary(listing);
        addPermalink(listing);
        return true;
    }

    private void addItem(Resource listing) throws ExtractionException {
        Node node = fragment.findMicroformattedObjectNode("*", "item");
        if (null == node) return;
        Resource blankItem = valueFactory.createBNode();
        out.writeTriple(listing, HLISTING.item, blankItem);
        out.writeTriple(blankItem, RDF.TYPE, HLISTING.Item);

        HTMLDocument item = new HTMLDocument(node);

        addItemName(item, blankItem);
        addItemUrl(item, blankItem);
        // the format is specified with photo into item, but kelkoo has it into the top level
        addItemPhoto(fragment, blankItem);
        addItemAddresses(fragment, blankItem);
    }

    private static final List<String> validClassesForAddress = Arrays.asList(
            "post-office-box",
            "extended-address",
            "street-address",
            "locality",
            "region",
            "postal-code",
            "country-name"
    );

    private void addItemAddresses(HTMLDocument doc, Resource blankItem) {
        for (Node node : doc.findAll(".//*[contains(@class,'adr')]//*[@class]")) {
            String[] klasses = node.getAttributes().getNamedItem("class").getNodeValue().split("\\s+");
            for (String klass : klasses)
                if (validClassesForAddress.contains(klass)) {
                    String value = node.getNodeValue();
                    // do not use conditionallyAdd, it won't work cause of evaluation rules
                    if (!(null == value || "".equals(value))) {
                        URI property = HLISTING.getPropertyCamelized(klass);
                        out.writeTriple(blankItem, property, valueFactory.createLiteral(value));
                    }
                }
        }
    }

    private void addPermalink(Resource listing) {
        String link = fragment.find(".//A[contains(@rel,'self') and contains(@rel,'bookmark')]/@href");
        conditionallyAddStringProperty(listing, HLISTING.permalink, link);
    }

    private void addPrice(Resource listing) {
        String price = fragment.getSingularTextField("price");
        conditionallyAddStringProperty(listing, HLISTING.price, price);
    }

    private void addDescription(Resource listing) {
        String description = fragment.getSingularTextField("description");
        conditionallyAddStringProperty(listing, HLISTING.description, description);
    }

    private void addSummary(Resource listing) {
        String summary = fragment.getSingularTextField("summary");
        conditionallyAddStringProperty(listing, HLISTING.summary, summary);
    }

    private void addDateTimes(Resource listing) {
        String listed = fragment.getSingularTextField("dtlisted");
        conditionallyAddStringProperty(listing, HLISTING.dtlisted, listed);
        String expired = fragment.getSingularTextField("dtexpired");
        conditionallyAddStringProperty(listing, HLISTING.dtexpired, expired);
    }

    private Resource addLister() throws ExtractionException {
        Resource blankLister = valueFactory.createBNode();
        out.writeTriple(blankLister, RDF.TYPE, HLISTING.Lister);
        Node node = fragment.findMicroformattedObjectNode("*", "lister");
        if (null == node)
            return blankLister;
        HTMLDocument listerNode = new HTMLDocument(node);
        addListerFn(listerNode, blankLister);
        addListerOrg(listerNode, blankLister);
        addListerEmail(listerNode, blankLister);
        addListerUrl(listerNode, blankLister);
        addListerTel(listerNode, blankLister);
        addListerLogo(listerNode, blankLister);
        return blankLister;
    }

    private void addListerTel(HTMLDocument doc, Resource blankLister) {
        String tel = doc.getSingularTextField("tel");
        conditionallyAddStringProperty(blankLister, HLISTING.tel, tel);
    }

    private void addListerUrl(HTMLDocument doc, Resource blankLister) throws ExtractionException {
        String url = doc.getSingularUrlField("url");
        conditionallyAddResourceProperty(blankLister, HLISTING.listerUrl, document.resolveURI(url));
    }

    private void addListerEmail(HTMLDocument doc, Resource blankLister) {
        String email = doc.getSingularUrlField("email");
        conditionallyAddResourceProperty(blankLister, FOAF.mbox, fixLink(email, "mailto"));
    }

    private void addListerFn(HTMLDocument doc, Resource blankLister) {
        String fn = doc.getSingularTextField("fn");
        conditionallyAddStringProperty(blankLister, HLISTING.listerName, fn);
    }

    private void addListerLogo(HTMLDocument doc, Resource blankLister) throws ExtractionException {
        String logo = doc.getSingularUrlField("logo");
        conditionallyAddResourceProperty(blankLister, HLISTING.listerLogo, document.resolveURI(logo));
    }

    private void addListerOrg(HTMLDocument doc, Resource blankLister) {
        String org = doc.getSingularTextField("org");
        conditionallyAddStringProperty(blankLister, HLISTING.listerOrg, org);
    }

    private void addItemName(HTMLDocument item, Resource blankItem) {
        String fn = item.getSingularTextField("fn");
        conditionallyAddStringProperty(blankItem, HLISTING.itemName, fn);
    }

    private void addItemUrl(HTMLDocument item, Resource blankItem) throws ExtractionException {
        String url = item.getSingularUrlField("url");
        conditionallyAddResourceProperty(blankItem, HLISTING.itemUrl, document.resolveURI(url));
    }

    private void addItemPhoto(HTMLDocument doc, Resource blankLister) throws ExtractionException {
        // as per spec
        String url = doc.findMicroformattedValue("*", "item", "A", "photo", "@href");
        conditionallyAddResourceProperty(blankLister, HLISTING.itemPhoto, document.resolveURI(url));
        url = doc.findMicroformattedValue("*", "item", "IMG", "photo", "@src");
        conditionallyAddResourceProperty(blankLister, HLISTING.itemPhoto, document.resolveURI(url));
        // as per kelkoo. Remember that contains(foo,'') is true in xpath
        url = doc.findMicroformattedValue("*", "photo", "IMG", "", "@src");
        conditionallyAddResourceProperty(blankLister, HLISTING.itemPhoto, document.resolveURI(url));
    }

    private List<String> findActions(HTMLDocument doc) {
        List<String> actions = new ArrayList<String>(0);
        // first check if values are inlined
        String[] classes = doc.readAttribute("class").split("\\s+");
        for (String klass : classes) {
            if (ActionClasses.contains(klass))
                actions.add(klass);
        }

        for (Node action : doc.findAll("./*[@class]/@class")) {
            for (String substring : action.getNodeValue().split("\\s+")) {
                if (ActionClasses.contains(substring))
                    actions.add(substring);
            }
        }
        return actions;
    }

}