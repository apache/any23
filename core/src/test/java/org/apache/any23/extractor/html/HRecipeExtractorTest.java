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
import org.apache.any23.vocab.HRecipe;
import org.apache.any23.vocab.SINDICE;
import org.junit.Test;
import org.eclipse.rdf4j.model.vocabulary.RDF;

/**
 * Test case for {@link HRecipeExtractor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class HRecipeExtractorTest extends AbstractExtractorTestCase {

	private static final SINDICE vSINDICE = SINDICE.getInstance();
	private static final HRecipe vHRECIPE = HRecipe.getInstance();

	@Override
	protected ExtractorFactory<?> getExtractorFactory() {
		return new HRecipeExtractorFactory();
	}

	@Test
	public void testNoMicroformats() throws Exception {
		assertExtract("/html/html-without-uf.html");
		assertModelEmpty();
	}

	@Test
	public void testExtraction() throws Exception {
		assertExtract("/microformats/hrecipe/01-spec.html");
		assertModelNotEmpty();
		assertStatementsSize(RDF.TYPE, vHRECIPE.Recipe, 1);
		assertStatementsSize(RDF.TYPE, vHRECIPE.Ingredient, 3);
		assertStatementsSize(RDF.TYPE, vHRECIPE.Duration, 2);
		assertStatementsSize(RDF.TYPE, vHRECIPE.Nutrition, 2);
		assertStatementsSize(vHRECIPE.fn, (String) null, 1);
		assertStatementsSize(vHRECIPE.yield, (String) null, 1);
		assertStatementsSize(vHRECIPE.instructions, (String) null, 1);
		assertStatementsSize(vHRECIPE.photo, (String) null, 1);
		assertStatementsSize(vHRECIPE.summary, (String) null, 1);
		assertStatementsSize(vHRECIPE.author, (String) null, 2);
		assertStatementsSize(vHRECIPE.published, (String) null, 1);
		assertStatementsSize(vHRECIPE.tag, (String) null, 2);
	}

}
