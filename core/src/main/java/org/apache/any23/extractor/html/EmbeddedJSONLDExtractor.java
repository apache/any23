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

package org.apache.any23.extractor.html;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.rdf.JSONLDExtractor;
import org.apache.any23.extractor.rdf.JSONLDExtractorFactory;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.vocab.SINDICE;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This extractor represents the HTML script tags used to embed blocks of data in documents. 
 * This way, JSON-LD content can be easily embedded in HTML by placing it in a script element 
 * with the type attribute set to application/ld+json 
 * according the <a href="http://www.w3.org/TR/json-ld/#embedding-json-ld-in-html-documents">JSON-LD specification</a>.
 *
 */
public class EmbeddedJSONLDExtractor implements Extractor.TagSoupDOMExtractor {

    private static final SINDICE vSINDICE = SINDICE.getInstance();

    private URI profile;

    private Map<String, URI> prefixes = new HashMap<String, URI>();

    private String documentLang;
    
    private JSONLDExtractor extractor;

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(
            ExtractionParameters extractionParameters,
            ExtractionContext extractionContext,
            Document in,
            ExtractionResult out
    ) throws IOException, ExtractionException {
        profile = extractProfile(in);
        documentLang = getDocumentLanguage(in);
        extractLinkDefinedPrefixes(in);

        String baseProfile = vSINDICE.NS;
        if(profile != null) {
            baseProfile = profile.toString();
        }

        final URI documentURI = extractionContext.getDocumentURI();
        Set<JSONLDScript> jsonldScripts = extractJSONLDScript(in, baseProfile, extractionParameters, extractionContext, out);
        for(JSONLDScript jsonldScript : jsonldScripts) {
            String lang = documentLang;
            if(jsonldScript.getLang() != null) {
                lang = jsonldScript.getLang();
            }
            out.writeTriple(
                    documentURI,
                    jsonldScript.getName(),
                    new LiteralImpl(jsonldScript.getContent(), lang)
            );
        }
    }

    /**
     * Returns the {@link Document} language if declared, <code>null</code> otherwise.
     *
     * @param in a instance of {@link Document}.
     * @return the language declared, could be <code>null</code>.
     */
    private String getDocumentLanguage(Document in) {
        String lang = DomUtils.find(in, "string(/HTML/@lang)");
        if (lang.equals("")) {
            return null;
        }
        return lang;
    }

    private URI extractProfile(Document in) {
        String profile = DomUtils.find(in, "string(/HTML/@profile)");
        if (profile.equals("")) {
            return null;
        }
        return new URIImpl(profile);
    }

    /**
     * It extracts prefixes defined in the <i>LINK</i> meta tags.
     *
     * @param in
     */
    private void extractLinkDefinedPrefixes(Document in) {
        List<Node> linkNodes = DomUtils.findAll(in, "/HTML/HEAD/LINK");
        for(Node linkNode : linkNodes) {
            NamedNodeMap attributes = linkNode.getAttributes();
            String rel = attributes.getNamedItem("rel").getTextContent();
            String href = attributes.getNamedItem("href").getTextContent();
            if(rel != null && href !=null && RDFUtils.isAbsoluteURI(href)) {
                prefixes.put(rel, new URIImpl(href));
            }
        }
    }

    private Set<JSONLDScript> extractJSONLDScript(Document in, String baseProfile, ExtractionParameters extractionParameters, 
            ExtractionContext extractionContext, ExtractionResult out) throws IOException, ExtractionException {
        List<Node> scriptNodes = DomUtils.findAll(in, "/HTML/HEAD/SCRIPT");
        Set<JSONLDScript> result = new HashSet<JSONLDScript>();
        extractor = new JSONLDExtractorFactory().createExtractor();
        for (Node jsonldNode : scriptNodes) {
            NamedNodeMap attributes = jsonldNode.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
              if (attributes.item(i).getTextContent().equalsIgnoreCase("application/ld+json")) {
              extractor.run(extractionParameters, extractionContext, DomUtils.nodeToInputStream(jsonldNode), out);
              }
            }
            Node nameAttribute = attributes.getNamedItem("name");
            Node contentAttribute = attributes.getNamedItem("content");
            if (nameAttribute == null || contentAttribute == null) {
                continue;
            }
            String name = nameAttribute.getTextContent();
            String content = contentAttribute.getTextContent();
            String xpath = DomUtils.getXPathForNode(jsonldNode);
            URI nameAsURI = getPrefixIfExists(name);
            if (nameAsURI == null) {
                nameAsURI = new URIImpl(baseProfile + name);
             }
            JSONLDScript jsonldScript = new JSONLDScript(xpath, nameAsURI, content);
            result.add(jsonldScript);
        }
        return result;
    }

    private URI getPrefixIfExists(String name) {
        String[] split = name.split("\\.");
        if(split.length == 2 && prefixes.containsKey(split[0])) {
            return new URIImpl(prefixes.get(split[0]) + split[1]);
        }
        return null;
    }

    @Override
    public ExtractorDescription getDescription() {
        return HTMLMetaExtractorFactory.getDescriptionInstance();
    }

    private class JSONLDScript {

        private String xpath;

        private URI name;

        private String lang;

        private String content;

        public JSONLDScript(String xpath, URI name, String content) {
            this.xpath = xpath;
            this.name = name;
            this.content = content;
        }

        public JSONLDScript(String xpath, URI name, String content, String lang) {
            this(xpath, name, content);
            this.lang = lang;
        }

        public URI getName() {
            return name;
        }

        public void setName(URI name) {
            this.name = name;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            JSONLDScript meta = (JSONLDScript) o;

            if (xpath != null ? !xpath.equals(meta.xpath) : meta.xpath != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return xpath != null ? xpath.hashCode() : 0;
        }
    }

}
