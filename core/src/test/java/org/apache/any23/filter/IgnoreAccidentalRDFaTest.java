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

package org.apache.any23.filter;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TripleHandlerException;
import org.junit.Test;
import org.mockito.verification.VerificationMode;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test case for {@link IgnoreAccidentalRDFa}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class IgnoreAccidentalRDFaTest {


    @Test
    public void testBlockCSSTriple() throws TripleHandlerException {
        checkTriple("http://www.w3.org/1999/xhtml/vocab#stylesheet", never());
    }

    @Test
    public void testAcceptGenericTriple() throws TripleHandlerException {
        checkTriple("http://www.w3.org/1999/xhtml/vocab#license", times(1));
    }

    private void checkTriple(String predicate, VerificationMode verificationMode)
    throws TripleHandlerException {
        final String DOCUMENT_IRI = "http://an.html.page";
        final TripleHandler mockTripleHandler = mock(TripleHandler.class);
        final ValueFactory valueFactory = SimpleValueFactory.getInstance();
        ExtractionContext extractionContext = new ExtractionContext(
                "test-extractor",
                valueFactory.createIRI(DOCUMENT_IRI)
        );
        final IgnoreAccidentalRDFa ignoreAccidentalRDFa = new IgnoreAccidentalRDFa(mockTripleHandler, true);
        ignoreAccidentalRDFa.openContext(extractionContext);
        ignoreAccidentalRDFa.receiveTriple(
                valueFactory.createIRI(DOCUMENT_IRI),
                valueFactory.createIRI(predicate),
                valueFactory.createIRI("http://www.myedu.com/modules/20110519065453/profile.css"),
                valueFactory.createIRI(DOCUMENT_IRI),
                extractionContext
        );
        ignoreAccidentalRDFa.close();

        verify(
                mockTripleHandler,
                verificationMode
        ).receiveTriple(
                (Resource) any(),
                (IRI) any(),
                (Value) any(),
                (IRI) any(),
                (ExtractionContext) any()
        );
    }

}
