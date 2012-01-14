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
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import static org.mockito.Matchers.any;
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
        final String DOCUMENT_URI = "http://an.html.page";
        final TripleHandler mockTripleHandler = mock(TripleHandler.class);
        final ValueFactory valueFactory = new ValueFactoryImpl();
        ExtractionContext extractionContext = new ExtractionContext(
                "test-extractor",
                valueFactory.createURI(DOCUMENT_URI)
        );
        final IgnoreAccidentalRDFa ignoreAccidentalRDFa = new IgnoreAccidentalRDFa(mockTripleHandler, true);
        ignoreAccidentalRDFa.openContext(extractionContext);
        ignoreAccidentalRDFa.receiveTriple(
                valueFactory.createURI(DOCUMENT_URI),
                valueFactory.createURI(predicate),
                valueFactory.createURI("http://www.myedu.com/modules/20110519065453/profile.css"),
                valueFactory.createURI(DOCUMENT_URI),
                extractionContext
        );
        ignoreAccidentalRDFa.close();

        verify(
                mockTripleHandler,
                verificationMode
        ).receiveTriple(
                (Resource) any(),
                (URI) any(),
                (Value) any(),
                (URI) any(),
                (ExtractionContext) any()
        );
    }

}
