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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jacek Grzebyta (jgrzebyta [at] apache [dot] org)
 */
public class ElementsProcessorTest {
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final ElementsProcessor ep = ElementsProcessor.getInstance();
    
    @Test
    public void processMap() throws Exception {
        Map<String, Object> simpleMap = new HashMap<String, Object>() {
            {
                put("key1", "value1");
                put("key2", "value2");
                put("key3", 3);
            }
        };
        
        ElementsProcessor.ModelHolder toTest = ep.processMap(ep.vf.createIRI("http://example.org/"),
                simpleMap,
                ep.vf.createIRI("http://example.org/node1"));
        
        Assert.assertEquals("http://example.org/node1", toTest.getRoot().stringValue()); // if parent node is not blank than returns it as key
        Assert.assertTrue(toTest.getModel().size() > 0);
        log.debug("Model: \n{}\n", dumpModel(toTest.getModel(), RDFFormat.TURTLE));
    }
    
    @Test
    public void processList() throws Exception {
        List<Object> simpleList = new ArrayList<Object>() {
            {
                add("Ala");
                add(6);
                add("ma");
                add("k".getBytes()[0]);
            }
        };
        
        ElementsProcessor.ModelHolder toTest = ep.processList(ep.vf.createIRI("http://example.org/data"), simpleList);
        Assert.assertNotNull(toTest);
        Assert.assertTrue(toTest.getModel().contains(null, RDF.FIRST, ep.vf.createLiteral("Ala"), (Resource[]) null));
        Assert.assertTrue(toTest.getModel().contains(null, RDF.FIRST, ep.vf.createLiteral(6), (Resource[]) null));
        Assert.assertTrue(toTest.getModel().contains(null, RDF.FIRST, ep.vf.createLiteral("ma"), (Resource[]) null));
        Assert.assertTrue(toTest.getModel().contains(null, RDF.FIRST, ep.vf.createLiteral("k".getBytes()[0]), (Resource[]) null));
        log.debug("Model: \n{}\n", dumpModel(toTest.getModel(), RDFFormat.TURTLE));
    }
    
    @Test
    public void processSimple() throws Exception {
        List<Object> simpleList = new ArrayList<Object>() {
            {
                add("Ala");
                add(6);
                add("ma");
                add("k".getBytes()[0]);
            }
        };
        
        simpleList.forEach((i) -> {
            ElementsProcessor.ModelHolder out = ep.asModel(ep.vf.createIRI("urn:test/"), i, null);
            Assert.assertTrue(out.getRoot() instanceof Literal);
            Assert.assertTrue(out.getModel().isEmpty());
        });
    }
    
    private String dumpModel(Model m, RDFFormat format) {
        StringWriter writer = new StringWriter();
        Rio.write(m, writer, format);
        return writer.toString();
    }
}
