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

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.vocab.YAML;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ModelFactory;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModelFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Literals;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;

/**
 * Converts Object into RDF graph encoded to {@link ModelHolder}. Where key is a
 * graph root node and value is a graph itself inside a {@link Model}.
 *
 * This parser performs conversion for three main types:
 * <ul>
 * <li> List - Creates RDF:List with bnode as root
 * <li> Map - Creates simple graph where {key: value} is converted to
 * predicate:object pair
 * <li> Simple type - Crates RDF Literal
 * </ul>
 *
 * @author Jacek Grzebyta (grzebyta.dev [at] gmail.com)
 */
public class ElementsProcessor {

    private final ModelFactory modelFactory = new LinkedHashModelFactory();
    private final YAML vocab = YAML.getInstance();
    protected ValueFactory vf = SimpleValueFactory.getInstance();

    private static final ElementsProcessor _ep = new ElementsProcessor();

    // hide constructor
    private ElementsProcessor() {
    }

    /**
     * A model holder describes the two required parameters which makes a model useful
     * in further processing: a root node and model itself.
     */
    public class ModelHolder {
        private final Value root;
        private final Model model;

        public ModelHolder(Value root, Model model) {
            this.root = root;
            this.model = model;
        }

        public Value getRoot() {
            return root;
        }

        public Model getModel() {
            return model;
        }
    }
    
    
    private ModelHolder asModelHolder(Value v, Model m) {
        return new ModelHolder(v,m);
    }

    /**
     * Converts a data structure to {@link ModelHolder}. where value
     * is a root node of the data structure and model is a content of the RDF
     * graph.
     *
     * If requested object is simple object (i.e. is neither List or Map) than
     * method returns map entry of relevant instance of {@link Literal} as key
     * and empty model as value.
     *
     * @param namespace Namespace for predicates
     * @param t Object (or data structure) converting to RDF graph
     * @param rootNode root node of the graph. If not given then blank node is
     * created.
     * @return instance of {@link ModelHolder},
     */
    @SuppressWarnings("unchecked")
    public ModelHolder asModel(IRI namespace, final Object t, Value rootNode) {

        if (t instanceof List) {
            return processList(namespace, (List<Object>) t);
        } else if (t instanceof Map) {
            return processMap(namespace, (Map<String, Object>) t, rootNode);
        } else if (t instanceof String) {
            return asModelHolder(RDFUtils.makeIRI(t.toString()), modelFactory.createEmptyModel());
        } else if (t == null) {
            return asModelHolder(vocab.nullValue, modelFactory.createEmptyModel());
        } else {
            return asModelHolder(Literals.createLiteral(vf, t), modelFactory.createEmptyModel());
        }
    }

    /**
     * This method processes a map with non bnode root.
     * 
     * If a map has instantiated root (not a blank node) it is simpler to create SPARQL query.
     * 
     * @param ns the namespace to associated with statements
     * @param object a populated {@link java.util.Map} 
     * @param parentNode a {@link org.eclipse.rdf4j.model.Value} subject node to use in the new statement
     * @return instance of {@link ModelHolder}.
     */
    protected ModelHolder processMap(IRI ns, Map<String, Object> object, Value parentNode) {
        // check if map is empty
        if (object.isEmpty()) {
            return null;
        }
        HashSet<Object> vals = Sets.newHashSet(object.values());
        boolean isEmpty = false;
        if (vals.size() == 1 && vals.contains(null)) {
            isEmpty = true;
        }
        assert ns != null : "Namespace value is null";

        Model model = modelFactory.createEmptyModel();
        Value nodeURI = parentNode instanceof BNode ? RDFUtils.makeIRI("node", ns, true) : parentNode;

        if (!isEmpty) {
            model.add(vf.createStatement((Resource) nodeURI, RDF.TYPE, vocab.mapping));
        }
        object.keySet().forEach((k) -> {
            /* False prevents adding _<int> to the predicate.
            Thus the predicate pattern is:
            "some string" ---> ns:someString
             */
            Resource predicate = RDFUtils.makeIRI(k, ns, false);
            /* add map's key as statements:
            predicate rdf:type rdf:predicate .
            predicate rdfs:label predicate name
             */
            model.add(vf.createStatement(predicate, RDF.TYPE, RDF.PREDICATE));
            model.add(vf.createStatement(predicate, RDFS.LABEL, RDFUtils.literal(k)));
            Value subGraphRoot = RDFUtils.makeIRI();
            ModelHolder valInst = asModel(ns, object.get(k), subGraphRoot);
            // if asModel returns null than 
            if (valInst != null) {
                /*
            Subgraph root node is added always. If subgraph is null that root node is Literal.
            Otherwise submodel in added to the current model.
                 */
                model.add(vf.createStatement((Resource) nodeURI, (IRI) predicate, valInst.root));
                if (valInst.model != null) {
                    model.addAll(valInst.model);
                }
            }

        });
        return asModelHolder(nodeURI, model);
    }

    protected ModelHolder processList(IRI ns, List<Object> object) {

        if (object.isEmpty() || object.stream().noneMatch((i) -> {
            return i != null;
        })) {
            return null;
        }
        assert ns != null : "Namespace value is null";

        int objectSize = object.size();
        Value listRoot = null;
        Resource prevNode = null;
        Model finalModel = modelFactory.createEmptyModel();
        for (int i=0; i < objectSize; i++) {
            ModelHolder node = asModel(ns, object.get(i), RDFUtils.bnode());
            BNode currentNode = RDFUtils.bnode();

            if (i == 0) {
                listRoot = currentNode;
            }

            finalModel.add(currentNode, RDF.FIRST, node.root, (Resource[]) null);

            if (prevNode != null) {
                finalModel.add(prevNode, RDF.REST, currentNode, (Resource[]) null);
            }

            if (i == objectSize-1) {
                finalModel.add(currentNode, RDF.REST, RDF.NIL, (Resource[]) null);
            }

            if(node.model != null) {
                finalModel.addAll(node.model);
            }

            prevNode = currentNode;
        }

        return asModelHolder(listRoot, finalModel);
    }

    public static final ElementsProcessor getInstance() {
        return _ep;
    }
}
