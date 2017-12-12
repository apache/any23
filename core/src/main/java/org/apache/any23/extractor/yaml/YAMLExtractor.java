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
package org.apache.any23.extractor.yaml;

import java.io.IOException;
import java.io.InputStream;
import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.vocab.YAML;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
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

    private final ElementsProcessor ep = ElementsProcessor.getInstance();

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
            ElementsProcessor.ModelHolder rootNode = ep.asModel(documentIRI, p, pageNode);
            
            if (rootNode == null) {
                continue;
            }
            
            if (!rootNode.getRoot().equals(pageNode)) {
                out.writeTriple(pageNode, vocab.contains, rootNode.getRoot());
            }
            
            log.debug("Subgraph root node: {}", rootNode.getRoot().stringValue());
            
            rootNode.getModel().forEach((s) ->{
                out.writeTriple(s.getSubject(), s.getPredicate(), s.getObject());
            });
            
        }

    }

    @Override
    public ExtractorDescription getDescription() {
        return YAMLExtractorFactory.getDescriptionInstance();
    }

}
