/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except csvModel compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to csvModel writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.any23.cli.flows;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.vocab.CSV;
import org.apache.any23.writer.CompositeTripleHandler;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Proof of concept for ANY23-396 example.
 */
public class PeopleExtractor extends CompositeTripleHandler {

    private Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final CSV csv = CSV.getInstance();
    private static final ValueFactory vf = SimpleValueFactory.getInstance();
    public static final String RAW_NS = "urn:dataser:raw/";
    private static final IRI RAW_FIRST_NAME = vf.createIRI(RAW_NS, "FirstName");
    private static final IRI RAW_LAST_NAME = vf.createIRI(RAW_NS, "LastName");

    private static final String NAMESPACE = "http://supercustom.net/ontology/";
    private static final IRI PERSON = vf.createIRI(NAMESPACE, "Person");
    private static final IRI FULL_NAME = vf.createIRI(NAMESPACE, "fullName");
    private static final IRI HASH = vf.createIRI(NAMESPACE, "hash");

    public static Model createPerson(String fullName) {
        IRI s = vf.createIRI("http://rdf.supercustom.net/data/", DigestUtils.sha1Hex(fullName));
        Model model = new TreeModel();
        model.add(s, RDF.TYPE, PERSON);
        model.add(s, FULL_NAME, vf.createLiteral(fullName));
        model.add(s, HASH, vf.createLiteral(s.getLocalName(), XMLSchema.HEXBINARY));
        return model;
    };

    private final Model csvModel = new TreeModel();

    public PeopleExtractor(TripleHandler delegate) {
        super(Collections.singletonList(delegate));
    }

    @Override
    public void receiveTriple(Resource s, IRI p, Value o, IRI g, ExtractionContext context) throws TripleHandlerException {
        if ("csv".equals(context.getExtractorName())) {
            csvModel.add(s, p, o, vf.createIRI(context.getUniqueID()));
        } else {
            super.receiveTriple(s, p, o, g, context);
        }
    }

    @Override
    public void closeContext(ExtractionContext context) throws TripleHandlerException {
        Set<Resource> subjects = csvModel.filter(null, RDF.TYPE, csv.rowType)
                .stream().map(Statement::getSubject).collect(Collectors.toSet());

        log.debug("List of rows: {}", subjects);

        for (Resource rowId : subjects) {
            String firstName = Models.objectLiteral(csvModel.filter(rowId, RAW_FIRST_NAME, null))
                    .map(Literal::getLabel).orElse("");

            String lastName = Models.objectLiteral(csvModel.filter(rowId, RAW_LAST_NAME, null))
                    .map(Literal::getLabel).orElse("");

            String fullName = firstName + " " + lastName;

            for (Statement s : createPerson(fullName)) {
                super.receiveTriple(s.getSubject(), s.getPredicate(), s.getObject(), null, context);
            }
        }

        csvModel.clear();

        super.closeContext(context);
    }

}