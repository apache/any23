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
import org.apache.any23.vocab.DOAC;
import org.apache.any23.vocab.FOAF;
import org.apache.any23.vocab.SINDICE;
import org.apache.any23.vocab.VCard;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Reference Test class for the {@link HResumeExtractor} extractor.
 *
 * @author Davide Palmisano (dpalmisano@gmail.com)
 */
public class HResumeExtractorTest extends AbstractExtractorTestCase {

	private static final SINDICE vSINDICE = SINDICE.getInstance();
	private static final FOAF vFOAF = FOAF.getInstance();
	private static final DOAC vDOAC = DOAC.getInstance();
	private static final VCard vVCARD = VCard.getInstance();

	private static final Logger logger = LoggerFactory
			.getLogger(HReviewExtractorTest.class);

	protected ExtractorFactory<?> getExtractorFactory() {
		return new HResumeExtractorFactory();
	}

	@Test
	public void testNoMicroformats() throws Exception {
		assertExtract("/html/html-without-uf.html");
		assertModelEmpty();
	}

	@Test
	public void testLinkedIn() throws Exception {
		assertExtract("/microformats/hresume/steveganz.html");
		assertModelNotEmpty();
		assertStatementsSize(RDF.TYPE, vFOAF.Person, 1);

		Resource person = findExactlyOneBlankSubject(RDF.TYPE, vFOAF.Person);

		assertContains(person, vDOAC.summary, (Resource) null);

		assertContains(
				person,
				vDOAC.summary,
				"Steve Ganz is passionate about connecting people,\n"
						+ "semantic markup, sushi, and disc golf - not necessarily in that order.\n"
						+ "Currently obsessed with developing the user experience at LinkedIn,\n"
						+ "Steve is a second generation Silicon Valley geek and a veteran web\n"
						+ "professional who has been building human-computer interfaces since 1994.");

		assertContains(person, vFOAF.isPrimaryTopicOf, (Resource) null);

		assertStatementsSize(RDF.TYPE, vVCARD.VCard, 0);

		assertStatementsSize(vDOAC.experience, (Value) null, 7);
		assertStatementsSize(vDOAC.education, (Value) null, 2);
		assertStatementsSize(vDOAC.affiliation, (Value) null, 8);
	}

	@Test
	public void testLinkedInComplete() throws Exception {

		assertExtract("/microformats/hresume/steveganz.html");
		assertModelNotEmpty();

		assertStatementsSize(RDF.TYPE, vFOAF.Person, 1);

		assertStatementsSize(vDOAC.experience, (Value) null, 7);
		assertStatementsSize(vDOAC.education, (Value) null, 2);
		assertStatementsSize(vDOAC.affiliation, (Value) null, 8);
		assertStatementsSize(vDOAC.skill, (Value) null, 17);

		RepositoryResult<Statement> statements = getStatements(null,
				vDOAC.organization, null);

		Set<String> checkSet = new HashSet<String>();

		try {
			while (statements.hasNext()) {
				Statement statement = statements.next();
				checkSet.add(statement.getObject().stringValue());
				logger.debug(statement.getObject().stringValue());
			}

		} finally {
			statements.close();
		}

		String[] names = new String[] { "LinkedIn Corporation",
				"PayPal, an eBay Company", "McAfee, Inc.",
				"Printable Technologies", "Collabria, Inc.", "Self-employed",
				"3G Productions",
				"Lee Strasberg Theatre and Film\n" + "\tInstitute",
				"Leland High School" };

		for (String name : names)
			Assert.assertTrue(checkSet.contains(name));

		Resource person = findExactlyOneBlankSubject(RDF.TYPE, vFOAF.Person);
		assertContains(person, vFOAF.isPrimaryTopicOf, (Value) null);
		findExactlyOneObject(person, vFOAF.isPrimaryTopicOf);
	}

	@Test
	public void testAnt() throws Exception {
		assertExtract("/microformats/hresume/ant.html");
		assertModelNotEmpty();

		assertStatementsSize(RDF.TYPE, vFOAF.Person, 1);

		Resource person = findExactlyOneBlankSubject(RDF.TYPE, vFOAF.Person);
		assertContains(person, vDOAC.summary, (Resource) null);

		assertContains(
				person,
				vDOAC.summary,
				"Senior Systems\n              Analyst/Developer.\n              "
						+ "Experienced in the analysis, design and\n              "
						+ "implementation of distributed, multi-tier\n              "
						+ "applications using Microsoft\n              technologies.\n"
						+ "              Specialising in data capture applications on the\n"
						+ "              Web.");

		assertContains(person, vFOAF.isPrimaryTopicOf, (Resource) null);

		assertStatementsSize(RDF.TYPE, vVCARD.VCard, 0);

		assertStatementsSize(vDOAC.experience, (Value) null, 16);
		assertStatementsSize(vDOAC.education, (Value) null, 2);
		assertStatementsSize(vDOAC.affiliation, (Value) null, 0);
		assertStatementsSize(vDOAC.skill, (Value) null, 4);
	}

}
