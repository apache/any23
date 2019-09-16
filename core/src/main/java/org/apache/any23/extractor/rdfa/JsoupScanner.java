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

//    private static String orNull(String str) {
//        return "".equals(str) ? null : str;
//    }

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
