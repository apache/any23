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

import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.vocab.VCard;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryException;

import java.util.List;

/**
 * Test case for {@link AdrExtractor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class AdrExtractorTest extends AbstractExtractorTestCase {

    private static final VCard vVCARD = VCard.getInstance();

    protected ExtractorFactory<?> getExtractorFactory() {
        return new AdrExtractorFactory();
    }

    @Test
    public void testVCardMultiAddress() throws RepositoryException {
        assertExtract("/microformats/hcard/lastfm-adr-multi-address.html");
        assertModelNotEmpty();
        List<Resource> addresses = findSubjects(RDF.TYPE, vVCARD.Address);
        int[] expectedStatementsPerAddress = new int[]{5, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
        int index = 0;
        for (Resource address : addresses) {
            int size = getStatementsSize(address, null, null);
            Assert.assertTrue(
                    String.format("Unexpected statements count %d for address index %d", size, index),
                    size == expectedStatementsPerAddress[index++]
            );
        }
    }

}
