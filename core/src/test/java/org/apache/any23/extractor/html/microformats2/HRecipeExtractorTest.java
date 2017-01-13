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

package org.apache.any23.extractor.html.microformats2;

import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.html.AbstractExtractorTestCase;
import org.junit.Test;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFHandlerException;

public class HRecipeExtractorTest extends AbstractExtractorTestCase {

    protected ExtractorFactory<?> getExtractorFactory() {
        return new HRecipeExtractorFactory();
    }

    @Test
    public void testModelNotEmpty() throws RepositoryException, RDFHandlerException {
        assertExtract("/microformats2/h-recipe/h-recipe-test.html");
        assertModelNotEmpty();
        assertStatementsSize(null, null, null, 15);
    }

}
