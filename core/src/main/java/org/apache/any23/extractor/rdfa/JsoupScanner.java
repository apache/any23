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

package org.apache.any23.extractor.rdfa;

import org.jsoup.nodes.CDataNode;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;
import org.semarglproject.sink.XmlSink;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.NamespaceSupport;

import java.util.ArrayList;

/**
 * @author Hans Brende (hansbrende@apache.org)
 */
class JsoupScanner implements NodeVisitor {

    private final NamespaceSupport ns = new NamespaceSupport();
    private final AttributesImpl attrs = new AttributesImpl();
    private final String[] nameParts = new String[3];

    private final XmlSink handler;

    JsoupScanner(XmlSink handler) {
        this.handler = handler;
    }

    private static String orEmpty(String str) {
        return str == null ? "" : str;
    }

    private void startElement(Element e) throws SAXException {
        ns.pushContext();

        attrs.clear();
        final ArrayList<String> remainingAttrs = new ArrayList<>();
        for (org.jsoup.nodes.Attribute attr : e.attributes()) {
            String name = attr.getKey();
            String value = attr.getValue();
            if (name.startsWith("xmlns")) {
                if (name.length() == 5) {
                    ns.declarePrefix("", value);
                    handler.startPrefixMapping("", value);
                    continue;
                } else if (name.charAt(5) == ':') {
                    String localName = name.substring(6);
                    ns.declarePrefix(localName, value);
                    handler.startPrefixMapping(localName, value);
                    continue;
                }
            }

            remainingAttrs.add(name);
            remainingAttrs.add(value);
        }

        for (int i = 0, len = remainingAttrs.size(); i < len; i += 2) {
            String name = remainingAttrs.get(i);
            String value = remainingAttrs.get(i + 1);
            String[] parts = ns.processName(name, nameParts, true);
            if (parts != null) {
                attrs.addAttribute(orEmpty(parts[0]), orEmpty(parts[1]), parts[2], "CDATA", value);
            }
        }

        String qName = e.tagName();

        String[] parts = ns.processName(qName, nameParts, false);
        if (parts == null) {
            handler.startElement("", "", qName, attrs);
        } else {
            handler.startElement(orEmpty(parts[0]), orEmpty(parts[1]), parts[2], attrs);
        }

    }

    private void endElement(Element e) throws SAXException {

        String qName = e.tagName();
        String[] parts = ns.processName(qName, nameParts, false);
        if (parts == null) {
            handler.endElement("", "", qName);
        } else {
            handler.endElement(orEmpty(parts[0]), orEmpty(parts[1]), parts[2]);
        }

        for (org.jsoup.nodes.Attribute attr : e.attributes()) {
            String name = attr.getKey();
            if (name.startsWith("xmlns")) {
                if (name.length() == 5) {
                    handler.endPrefixMapping("");
                } else if (name.charAt(5) == ':') {
                    String localName = name.substring(6);
                    handler.endPrefixMapping(localName);
                }
            }
        }

        ns.popContext();
    }

    private void handleText(String str) throws SAXException {
        handler.characters(str.toCharArray(), 0, str.length());
    }

    private void handleComment(String str) throws SAXException {
        handler.comment(str.toCharArray(), 0, str.length());
    }

    @Override
    public void head(Node node, int depth) {
        try {
            if (node instanceof Element) {
                startElement((Element) node);
            } else if (node instanceof CDataNode) {
                handler.startCDATA();
                handleText(((CDataNode) node).text());
            } else if (node instanceof TextNode) {
                handleText(((TextNode) node).text());
                // TODO support document types
//            } else if (node instanceof DocumentType) {
//                DocumentType dt = (DocumentType)node;
//                handler.startDTD(dt.attr("name"), orNull(dt.attr("publicId")), orNull(dt.attr("systemId")));
            } else if (node instanceof Comment) {
                handleComment(((Comment) node).getData());
            }
        } catch (SAXException e) {
            sneakyThrow(e);
        }
    }

    @Override
    public void tail(Node node, int depth) {
        try {
            if (node instanceof Element) {
                endElement((Element) node);
            } else if (node instanceof CDataNode) {
                handler.endCDATA();
                // TODO support document types
//            } else if (node instanceof DocumentType) {
//                handler.endDTD();
            }
        } catch (SAXException e) {
            sneakyThrow(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
        throw (E)e;
    }
}
