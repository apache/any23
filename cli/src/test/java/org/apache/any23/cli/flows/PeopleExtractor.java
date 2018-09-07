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
package org.apache.any23.cli.flows;


import org.apache.any23.cli.ExtractorsFlowTest;
import org.apache.any23.extractor.*;
import org.apache.any23.vocab.CSV;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Proof of concept for ANY23-396 example.
 */
public class PeopleExtractor implements Extractor.ModelExtractor {

    private Logger log = LoggerFactory.getLogger(PeopleExtractor.class);

    private static final String RAW_NS = "urn:dataser:raw/";
    private CSV csv = CSV.getInstance();
    private ValueFactory vf = SimpleValueFactory.getInstance();

    @Override
    public void run(ExtractionParameters extractionParameters, ExtractionContext context, Model in, ExtractionResult out) throws IOException, ExtractionException {
        if (in.isEmpty()) {
            throw new ExtractionException("model is empty ");
        }


        //for reach row
        Set<Resource> subjects = in.filter(null, RDF.TYPE, csv.rowType)
                .stream()
                .map( s -> {return s.getSubject(); }) // get subjects from each triple
                .collect(Collectors.toSet());

        log.debug("List of rows: {}", subjects);

        subjects.stream()
                .forEach( rowId -> {
                    String firstName = "";
                    Optional<Literal> firstNameO = Models.objectLiteral(in.filter(rowId, vf.createIRI(RAW_NS, "FirstName"), null));
                    if (firstNameO.isPresent()) {
                        firstName = firstNameO.get().stringValue();
                    }

                    String lastName = "";
                    Optional<Literal> lastNameO = Models.objectLiteral(in.filter(rowId, vf.createIRI(RAW_NS, "LastName"), null));
                    if (lastNameO.isPresent()) {
                        lastName = lastNameO.get().stringValue();
                    }

                    String fullName = firstName + " " + lastName;

                    IRI personID = ExtractorsFlowTest.personIRIFactory.apply(fullName);
                    in.add(personID, RDF.TYPE, ExtractorsFlowTest.PERSON);
                    in.add(personID, ExtractorsFlowTest.FULL_NAME, vf.createLiteral(fullName));
                    in.add(personID, ExtractorsFlowTest.HASH, vf.createLiteral(DigestUtils.sha1Hex(fullName), XMLSchema.HEXBINARY));

                    // clean model
                    in.remove(rowId, null, null);
                });

        // remove description
        Set<Resource> collumns = in.filter(null, csv.columnPosition, null).stream().map(s -> {
            return s.getSubject();
        }).collect(Collectors.toSet());

        collumns.stream()
                .forEach(s -> {
                    in.remove(s, null, null);
                });

        // remove metadata
        Resource datasetId = in.filter(null, csv.numberOfColumns, null).iterator().next().getSubject();
        in.remove(datasetId, null, null);

        log.info("Display model: \n\n{}", in);
    }

    @Override
    public ExtractorDescription getDescription() {
        return PeopleExtractorFactory.getDescriptionInstance();
    }
}
