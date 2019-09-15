package org.apache.any23.extractor.rdfa;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.html.JsoupUtils;
import org.apache.any23.extractor.rdf.BaseRDFExtractor;
import org.eclipse.rdf4j.common.net.ParsedIRI;
import org.jsoup.nodes.*;
import org.jsoup.select.NodeFilter;
import org.jsoup.select.NodeTraversor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;

abstract class BaseRDFaExtractor extends BaseRDFExtractor {


    private static final Pattern invalidXMLCharacters = Pattern.compile(
            "[^\u0009\r\n\u0020-\uD7FF\uE000-\uFFFD\ud800\udc00-\udbff\udfff]");

    private static final Charset charset = StandardCharsets.UTF_8;

    BaseRDFaExtractor(boolean verifyDataType, boolean stopAtFirstError) {
        super(verifyDataType, stopAtFirstError);
    }

    @Override
    public void run(ExtractionParameters extractionParameters, ExtractionContext extractionContext, InputStream in, ExtractionResult extractionResult) throws IOException, ExtractionException {

        String iri = extractionContext.getDocumentIRI().stringValue();

        Document doc = JsoupUtils.parse(in, iri, null);
        doc.outputSettings()
                .prettyPrint(false)
                .syntax(Document.OutputSettings.Syntax.xml)
                .escapeMode(Entities.EscapeMode.xhtml)
                .charset(charset);
        // Delete scripts, comments, and doctypes
        // See https://issues.apache.org/jira/browse/ANY23-317
        // and https://issues.apache.org/jira/browse/ANY23-340
        NodeTraversor.filter(new NodeFilter() {
            final HashSet<String> tmpAttributeKeys = new HashSet<>();

            @Override
            public FilterResult head(Node node, int depth) {
                if (node instanceof Element) {
                    HashSet<String> attributeKeys = tmpAttributeKeys;
                    for (Iterator<Attribute> it = node.attributes().iterator(); it.hasNext(); ) {
                        // fix for ANY23-350: valid xml attribute names are ^[a-zA-Z_:][-a-zA-Z0-9_:.]
                        Attribute attr = it.next();
                        String oldKey = attr.getKey();
                        String newKey = oldKey.replaceAll("[^-a-zA-Z0-9_:.]", "");

                        // fix for ANY23-347: strip non-reserved xml namespaces
                        // See https://www.w3.org/TR/xml-names/#sec-namespaces
                        // "All other prefixes beginning with the three-letter sequence x, m, l,
                        // in any case combination, are reserved. This means that:
                        //   * users SHOULD NOT use them except as defined by later specifications
                        //   * processors MUST NOT treat them as fatal errors."
                        int prefixlen = newKey.lastIndexOf(':') + 1;
                        String prefix = newKey.substring(0, prefixlen).toLowerCase();
                        newKey = (prefix.startsWith("xml") ? prefix : "") + newKey.substring(prefixlen);

                        if (newKey.matches("[a-zA-Z_:][-a-zA-Z0-9_:.]*")
                                //the namespace name for "xmlns" MUST NOT be declared
                                //the namespace name for "xml" need not be declared
                                && !newKey.startsWith("xmlns:xml")
                                // fix for ANY23-380: disallow duplicate attribute keys
                                && attributeKeys.add(newKey)) {
                            //avoid indexOf() operation if possible
                            if (!newKey.equals(oldKey)) {
                                attr.setKey(newKey);
                            }
                        } else {
                            it.remove();
                        }
                    }
                    attributeKeys.clear();

                    String tagName = ((Element)node).tagName().replaceAll("[^-a-zA-Z0-9_:.]", "");
                    tagName = tagName.substring(tagName.lastIndexOf(':') + 1);
                    ((Element)node).tagName(tagName.matches("[a-zA-Z_:][-a-zA-Z0-9_:.]*") ? tagName : "div");

                    // fix for ANY23-389
                    resolve_base:
                    if ("base".equalsIgnoreCase(tagName) && node.hasAttr("href")) {
                        String href = node.attr("href");
                        String absHref;
                        try {
                            ParsedIRI parsedHref = ParsedIRI.create(href.trim());
                            if (parsedHref.isAbsolute()) {
                                absHref = parsedHref.toString();
                            } else {
                                parsedHref = ParsedIRI.create(iri.trim()).resolve(parsedHref);
                                if (parsedHref.isAbsolute()) {
                                    absHref = parsedHref.toString();
                                } else {
                                    // shouldn't happen unless document IRI wasn't absolute
                                    // ignore and let underlying RDFa parser report the issue
                                    break resolve_base;
                                }
                            }
                        } catch (RuntimeException e) {
                            // can't parse href as a relative or absolute IRI:
                            // ignore and let underlying RDFa parser report the issue
                            break resolve_base;
                        }
                        if (!absHref.equals(href)) {
                            node.attr("href", absHref);
                        }
                    }

                    return FilterResult.CONTINUE;
                }
                return node instanceof DataNode || node instanceof Comment || node instanceof DocumentType
                        ? FilterResult.REMOVE : FilterResult.CONTINUE;
            }
            @Override
            public FilterResult tail(Node node, int depth) {
                return FilterResult.CONTINUE;
            }
        }, doc);

        // fix for ANY23-379: remove invalid xml characters from document
        String finalOutput = invalidXMLCharacters.matcher(doc.toString()).replaceAll("");

        in = new ByteArrayInputStream(finalOutput.getBytes(charset));

        super.run(extractionParameters, extractionContext, in, extractionResult);
    }
}
