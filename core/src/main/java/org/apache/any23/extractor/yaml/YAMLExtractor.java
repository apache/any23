/*
 * Copyright 2017 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.any23.extractor.yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.util.StringUtils;
import org.apache.any23.vocab.YAML;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

/**
 * @author Jacek Grzebyta (grzebyta.dev [at] gmail.com)
 */
public class YAMLExtractor implements Extractor.ContentExtractor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final Yaml yml = new Yaml();

    private static final YAML vocab = YAML.getInstance();

    private int nodeId = 0;

    private Resource documentRoot;

    @Override
    public void setStopAtFirstError(boolean f) {
    }

    @Override
    public void run(ExtractionParameters extractionParameters, ExtractionContext context, InputStream in,
            ExtractionResult out)
            throws IOException, ExtractionException {

        IRI documentIRI = context.getDocumentIRI();
        documentRoot = RDFUtils.iri(documentIRI.toString() + "root");

        log.debug("Processing: {}", documentIRI.toString());
        out.writeNamespace(vocab.PREFIX, vocab.NS);
        out.writeNamespace(RDF.PREFIX, RDF.NAMESPACE);
        out.writeNamespace(RDFS.PREFIX, RDFS.NAMESPACE);

        out.writeTriple(documentRoot, RDF.TYPE, vocab.root);
        Iterable<Object> docIterate = yml.loadAll(in);

        // Iterate over page(s)
        for (Object p : docIterate) {
            Resource pageNode = RDFUtils.makeIRI("document", documentIRI, true);
            out.writeTriple(documentRoot, vocab.contains, pageNode);
            out.writeTriple(pageNode, RDF.TYPE, vocab.document);
            buildNode(documentIRI, p, out, pageNode);
        }

    }

    @Override
    public ExtractorDescription getDescription() {
        return YAMLExtractorFactory.getDescriptionInstance();
    }

    private Optional<Value> buildNode(IRI fileURI, Object treeData, ExtractionResult out, Resource... parent) {

        if (treeData != null) {
            log.debug("object type: {}", treeData.getClass());
        }

        if (treeData == null) {
            return Optional.empty();
        } else if (treeData instanceof Map) {
            return Optional.ofNullable(processMap(fileURI, (Map) treeData, out, parent));
        } else if (treeData instanceof List) {
            return Optional.ofNullable(processList(fileURI, (List) treeData, out, parent));
        } else if (treeData instanceof Long) {
            return Optional.of(RDFUtils.literal(((Long) treeData)));
        } else if (treeData instanceof Integer) {
            return Optional.of(RDFUtils.literal(((Integer) treeData)));
        } else if (treeData instanceof Float) {
            return Optional.of(RDFUtils.literal((Float) treeData));
        } else if (treeData instanceof Double) {
            return Optional.of(RDFUtils.literal((Double) treeData));
        } else if (treeData instanceof Byte) {
            return Optional.of(RDFUtils.literal((Byte) treeData));
        } else if (treeData instanceof Boolean) {
            return Optional.of(RDFUtils.literal((Boolean) treeData));
        } else {
            return Optional.of(processString((String) treeData));
        }
    }

    private Value processMap(IRI file, Map<String, Object> node, ExtractionResult out, Resource... parent) {
        Resource nodeURI = Arrays.asList(parent).isEmpty() ? YAMLExtractor.this.makeUri(file) : parent[0];
        

        node.keySet().forEach((k) -> {
            /* False prevents adding _<int> to the predicate.
            Thus the predicate pattern is:
            "some string" ---> ns:someString
            */
            Resource predicate = RDFUtils.makeIRI(k, file, false);
            Optional<Value> isValue = buildNode(file, node.get(k), out);
            out.writeTriple(nodeURI, RDF.TYPE, vocab.mapping);
            if (isValue.isPresent()) {
                out.writeTriple(nodeURI, (IRI) predicate, isValue.get());
            }
            out.writeTriple(predicate, RDF.TYPE, RDF.PREDICATE);
            out.writeTriple(predicate, RDFS.LABEL, RDFUtils.literal(k));
        });
        return nodeURI;
    }

    private Value processList(IRI fileURI, Iterable iter, ExtractionResult out, Resource... parent) {
        Resource node = YAMLExtractor.this.makeUri();
        out.writeTriple(node, RDF.TYPE, RDF.LIST);
        
        if (!Arrays.asList(parent).isEmpty()) {
            out.writeTriple(parent[0], vocab.contains, node);
        }

        Resource pList = null; // previous RDF iter node
        Resource cList = node; // cutternt RDF iter node
        Iterator<?> listIter = iter.iterator();
        while (listIter.hasNext()) {
            // If previous RDF iter node is given lint with current one
            if (pList != null) {
                out.writeTriple(pList, RDF.REST, cList);
            }
            // adds value to the current iter
            Optional<Value> isValue = buildNode(fileURI, listIter.next(), out);
            out.writeTriple(cList, RDF.FIRST, isValue.orElse(RDF.NIL));
            // makes current node the previuos one and generate new current node
            pList = cList;
            cList = YAMLExtractor.this.makeUri();
        }
        out.writeTriple(pList, RDF.REST, RDF.NIL);

        return node;
    }
    
    private Value processString(String str) {
        if (RDFUtils.isAbsoluteIRI(str)) {
            return RDFUtils.iri(str);
        } else {
            return RDFUtils.literal(str);
        }
    }

    private Resource makeUri() {
        Resource bnode = RDFUtils.bnode(Integer.toString(nodeId));
        nodeId++;
        return bnode;
    }

    private Resource makeUri(IRI docUri) {
        return makeUri("node", docUri);
}

    private Resource makeUri(String type, IRI docUri) {
        return makeUri(type, docUri, true);
    }

    private Resource makeUri(String type, IRI docUri, boolean addId) {

        // preprocess string: converts - -> _
        //                    converts <space>: word1 word2 -> word1Word2
        String newType = StringUtils.implementJavaNaming(type);

        String uriString;
        if (docUri.toString().endsWith("/")) {
            uriString = docUri.toString() + newType;
        } else {
            uriString = docUri.toString() + "#" + newType;
        }

        if (addId) {
            uriString = uriString + "_" + Integer.toString(nodeId);
        }

        Resource node = RDFUtils.iri(uriString);
        if (addId) {
            nodeId++;
        }
        return node;
    }
}
