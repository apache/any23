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

import org.apache.commons.lang.StringUtils;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractorDescription;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.SimpleExtractorFactory;
import org.deri.any23.rdf.PopularPrefixes;
import org.deri.any23.vocab.VCARD;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hcard">hCard</a>
 * microformat.
 *
 * @author Gabriele Renzi
 */
public class HCardExtractor extends EntityBasedMicroformatExtractor {

    private HCardName name = new HCardName();
    
    private HTMLDocument fragment;

    public final static ExtractorFactory<HCardExtractor> factory =
            SimpleExtractorFactory.create(
                    "html-mf-hcard",
                    PopularPrefixes.createSubset("rdf", "vcard"),
                    Arrays.asList("text/html;q=0.1", "application/xhtml+xml;q=0.1"),
                    null,
                    HCardExtractor.class
            );

    public ExtractorDescription getDescription() {
        return factory;
    }

    @Override
    protected String getBaseClassName() {
        return "vcard";
    }

    private void fixIncludes(HTMLDocument document, Node node) {
        NamedNodeMap attributes = node.getAttributes();
        // header case test 32
        if ("TD".equals(node.getNodeName()) && (null != attributes.getNamedItem("headers"))) {
            String id = attributes.getNamedItem("headers").getNodeValue();
            Node header = document.findNodeById(id);
            if (null != header) {
                node.appendChild(header.cloneNode(true));
                attributes.removeNamedItem("headers");
            }
        }
        // include pattern, test 31

        for (Node current : document.findAll("//*[@class]")) {
            if (!DomUtils.hasClassName(current, "include")) continue;
            // we have to remove the field soon to avoid infinite loops
            // no null check, we know it's there or we won't be in the loop
            current.getAttributes().removeNamedItem("class");
            ArrayList<String> res = new ArrayList<String>(1);
            HTMLDocument.readUrlField(res, current);
            String id = res.get(0);
            if (null == id)
                continue;
            id = StringUtils.substringAfter(id, "#");
            Node included = document.findNodeById(id);
            if (null == included)
                continue;
            current.appendChild(included.cloneNode(true));
        }
    }

    @Override
    protected boolean extractEntity(Node node, ExtractionResult out) throws ExtractionException {
        this.fragment = new HTMLDocument(node);
        fixIncludes(document, node);
        BNode card = getBlankNodeFor(node);
        boolean foundSomething = false;

        readFn();
        readNames();
        readOrganization();
        foundSomething |= addFn(card);
        foundSomething |= addNames(card);
        foundSomething |= addOrganizationName(card);
        foundSomething |= addStringProperty("sort-string", card, VCARD.sort_string);
        foundSomething |= addUrl(card);
        foundSomething |= addEmail(card);
        foundSomething |= addPhoto(card);
        foundSomething |= addLogo(card);
        foundSomething |= addUid(card);
        foundSomething |= addStringProperty("bday", card, VCARD.bday);
        foundSomething |= addStringProperty("rev", card, VCARD.rev);
        foundSomething |= addStringProperty("tz", card, VCARD.tz);
        foundSomething |= addCategory(card);
        foundSomething |= addStringProperty("card", card, VCARD.class_);
        foundSomething |= addSubMicroformat("adr", card, VCARD.adr);
        foundSomething |= addTelephones(card);
        foundSomething |= addStringProperty("title", card, VCARD.title);
        foundSomething |= addStringProperty("role", card, VCARD.role);
        foundSomething |= addStringProperty("note", card, VCARD.note);
        foundSomething |= addSubMicroformat("geo", card, VCARD.geo);

        if (!foundSomething) return false;
        out.writeTriple(card, RDF.TYPE, VCARD.VCard);

        return true;

    }

    private boolean addTelephones(Resource card) {
        boolean found = false;
        for (Node node : fragment.findAll(".//*[contains(@class,'tel')]")) {
            HTMLDocument telFragment = new HTMLDocument(node);
            String[] values = telFragment.getPluralUrlField("value");
            if (values.length == 0) {
                //no sub values
                String[] typeAndValue = telFragment.getSingularUrlField("tel").split(":");
                //modem:goo fax:foo tel:bar
                if (typeAndValue.length > 1) {
                    found |= addTel(card, "tel", typeAndValue[1]);
                } else {
                    found |= addTel(card, "tel", typeAndValue[0]);
                }
            } else {
                String[] types = telFragment.getPluralTextField("type");
                if (types.length == 0) {
                    found |= addTel(card, "tel", StringUtils.join(values));
                }
                for (String type : types) {
                    found |= addTel(card, type, StringUtils.join(values));
                }
            }
        }
        return found;
    }

    private boolean addTel(Resource card, String type, String value) {
        URI tel = fixLink(value, "tel");
        URI composed = VCARD.getProperty(type + "Tel");
        if (composed == null) {
            URI simple = VCARD.getProperty(type);
            if (simple == null) {
                return conditionallyAddResourceProperty(card, VCARD.tel, tel);
            }
            return conditionallyAddResourceProperty(card, simple, tel);
        }
        return conditionallyAddResourceProperty(card, composed, tel);
    }

    private boolean addSubMicroformat(String className, Resource resource, URI property) {
        List<Node> nodes = fragment.findAllByClassName(className);
        if (nodes.isEmpty()) return false;
        for (Node node : nodes) {
            out.writeTriple(resource, property, getBlankNodeFor(node));
        }
        return true;
    }

    private boolean addStringProperty(String className, Resource resource, URI property) {
        return conditionallyAddStringProperty(resource, property, fragment.getSingularTextField(className));
    }

    private boolean addCategory(Resource card) {
        String[] categories = fragment.getPluralTextField("category");
        boolean found = false;
        for (String category : categories) {
            found |= conditionallyAddStringProperty(card, VCARD.category, category);
        }
        return found;
    }

    private boolean addUid(Resource card) {
        String uid = fragment.getSingularUrlField("uid");
        return conditionallyAddStringProperty(card, VCARD.uid, uid);
    }

    //TODO: #7 - Check if tests are checking plurality.
    private boolean addLogo(Resource card) throws ExtractionException {
        String[] links = fragment.getPluralUrlField("logo");
        boolean found = false;
        for (String link : links) {
            found |= conditionallyAddResourceProperty(card, VCARD.logo, document.resolveURI(link));
        }
        return found;
    }

    private boolean addPhoto(Resource card) throws ExtractionException {
        String[] links = fragment.getPluralUrlField("photo");
        boolean found = false;
        for (String link : links) {
            found |= conditionallyAddResourceProperty(card, VCARD.photo, document.resolveURI(link));
        }
        return found;
    }

    private boolean addEmail(Resource card) {
        String email = dropSubject(fragment.getSingularUrlField("email"));
        return conditionallyAddResourceProperty(card, VCARD.email,
                fixLink(email, "mailto"));
    }

    private String dropSubject(String mail) {
        if (mail == null) return null;
        return mail.split("\\?")[0];
    }

    private void readNames() {
        for (String field : HCardName.FIELDS) {
            String[] values = fragment.getPluralTextField(field);
            for (String text : values) {
                if ("".equals(text)) continue;
                name.setField(field, text);
            }
        }
    }

    private boolean addNames(Resource card) {
        BNode n = null;
        for (String fieldName : HCardName.FIELDS) {
            String value = name.getField(fieldName);
            if (value == null) continue;
            if (n == null) {
                n = valueFactory.createBNode();
                out.writeTriple(card, VCARD.n, n);
                out.writeTriple(n, RDF.TYPE, VCARD.Name);
            }
            out.writeTriple(n, VCARD.getProperty(fieldName), valueFactory.createLiteral(value));
        }
        return n != null;
    }

    private void readFn() {
        name.setFullName(fragment.getSingularTextField("fn"));
    }

    private boolean addFn(Resource card) {
        return conditionallyAddStringProperty(card, VCARD.fn, name.getFullName());
    }

    private void readOrganization() {
        Node node = fragment.findMicroformattedObjectNode("*", "org");
        if (node == null) return;
        HTMLDocument doc = new HTMLDocument(node);
        name.setOrganization(doc.getSingularTextField("organization-name"));
        name.setOrganization(doc.getSingularTextField("org"));
        name.setOrganizationUnit(doc.getSingularTextField("organization-unit"));
    }

    private boolean addOrganizationName(Resource card) {
        if (name.getOrganization() == null) return false;
        BNode org = valueFactory.createBNode();
        out.writeTriple(card, VCARD.org, org);
        out.writeTriple(org, RDF.TYPE, VCARD.Organization);
        out.writeTriple(org, VCARD.organization_name, valueFactory.createLiteral(name.getOrganization()));
        conditionallyAddStringProperty(org, VCARD.organization_unit, name.getOrganizationUnit());
        return true;
    }

    private boolean addUrl(Resource card) throws ExtractionException {
        String[] links = fragment.getPluralUrlField("url");
        boolean found = false;
        for (String link : links) {
            found |= conditionallyAddResourceProperty(card, VCARD.url, document.resolveURI(link));
        }
        return found;
    }

}
