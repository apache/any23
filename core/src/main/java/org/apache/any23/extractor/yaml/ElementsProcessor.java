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

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.vocab.YAML;
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
 *
 * @author Jacek Grzebyta (grzebyta.dev [at] gmail.com)
 */
public class ElementsProcessor {

    private final ModelFactory modelFactory = new LinkedHashModelFactory();
    private final YAML vocab = YAML.getInstance();
    protected ValueFactory vf = SimpleValueFactory.getInstance();
    
    private Map.Entry<Value,Model> asMapEntry(Value v, Model m) {
        return new AbstractMap.SimpleEntry(v, m);
    }

    /**
     * Converts a data structure to {@link Map.Entry<Value,Model>}. where value is a 
     * root node of the data structure and model is a content of the RDF graph.
     * 
     * If requested object is simple object (i.e. is neither List or Map) than 
     * method returns map entry of relevant instance of {@link Literal} as key and
     * null as value.
     * 
     * @param namespace Namespace for predicates
     * @param t Object (or data structure) converting to RDF graph
     * @param rootNode root node of the graph. If not given then blank node is created.
     * @return 
     */
    public Map.Entry<Value,Model> asModel(IRI namespace, final Object t, Value rootNode) {
        if (t == null) {
            return null;
        }

        if (t instanceof List) {
            //return processList(namespace, (List) t);
        } else if (t instanceof Map) {
            return processMap(namespace, (Map) t, rootNode);
        } else {
            return asMapEntry(Literals.createLiteral(vf, t), null);
        }
         return null;
    }
    
    protected Map.Entry<Value,Model> processMap(IRI ns, Map<String, Object> object, Value rootNode) {
        // check if map is empty of contains only null values
        if (object.isEmpty() || (object.values().size() == 1 && object.values().contains(null))) {
            return null;
        }
        assert ns != null : "Namespace value is null";
        
        Model model = modelFactory.createEmptyModel();
        Value nodeURI = rootNode == null ? RDFUtils.makeIRI() : rootNode;
        model.add(vf.createStatement((Resource) nodeURI, RDF.TYPE, vocab.mapping));
        object.keySet().forEach( (k) -> {
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
            Map.Entry<Value, Model> valInst = asModel(ns, object.get(k), subGraphRoot);
            // if asModel returns null than 
            if (valInst != null) {
                /*
            Subgraph root node is added always. If subgraph is null that root node is Literal.
            Otherwise submodel in added to the current model.
                 */
                model.add(vf.createStatement((Resource) nodeURI, (IRI) predicate, valInst.getKey()));
                if (valInst.getValue() != null) {
                    model.addAll(valInst.getValue());
                }
            }
            
        });
        return asMapEntry(nodeURI, model);
    }
}
