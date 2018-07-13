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

import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.vocab.ICAL;
import org.apache.any23.vocab.SINDICE;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryResult;

import java.io.IOException;

/**
 * Test case for {@link HCalendarExtractor}class.
 *
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id$
 */
public class HCalendarExtractorTest extends AbstractExtractorTestCase {

	private static final ICAL vICAL = ICAL.getInstance();
	private static final SINDICE vSINDICE = SINDICE.getInstance();

	private final static IRI vcal = vICAL.Vcalendar;
	private final static IRI vevent = vICAL.Vevent;
	private final static IRI vjournal = vICAL.Vjournal;
	private final static IRI vtodo = vICAL.Vtodo;

	protected ExtractorFactory<?> getExtractorFactory() {
		return new HCalendarExtractorFactory();
	}

	@Test
	public void testOneVEvent() throws Exception {
		assertExtract("/microformats/hcalendar/example1.html");
		assertModelNotEmpty();
		assertContains(baseIRI, RDF.TYPE, vcal);
		assertContains(null, RDF.TYPE, vevent);
		RepositoryResult<Statement> result = getStatements(null, RDF.TYPE,
				vevent);
		try {
			while (result.hasNext()) {
				Statement statement = result.next();
				final Resource subject = statement.getSubject();
				assertContains(null, vICAL.component, subject);
				assertContains(subject, RDF.TYPE, vevent);
				assertContains(subject, vICAL.dtstart,
						"1997-09-05T18:00:00.000Z");
				assertContains(subject, vICAL.dtstamp,
						"1997-09-01T13:00:00.000Z");
				assertContains(subject, vICAL.dtend, "1997-09-03T19:00:00.000Z");
				assertContains(subject, vICAL.uid,
						"19970901T130000Z-123401@host.com");
				assertContains(subject, vICAL.summary, "Annual Employee Review");
				assertContains(subject, vICAL.class_, "private");
				assertContains(subject, vICAL.categories, "Business");
				assertContains(subject, vICAL.categories, "Human Resources");
			}
		} finally {
			result.close();
		}
	}

	@Test
	public void testTransparentEvent() throws Exception {
		assertExtract("/microformats/hcalendar/example2.html");
		assertModelNotEmpty();
		assertContains(baseIRI, RDF.TYPE, vcal);
		assertContains(null, RDF.TYPE, vevent);
		RepositoryResult<Statement> result = getStatements(null, RDF.TYPE,
				vevent);
		try {
			while (result.hasNext()) {
				Statement statement = result.next();
				final Resource subject = statement.getSubject();
				assertContains(null, vICAL.component, subject);
				assertContains(subject, RDF.TYPE, vevent);
				assertContains(subject, vICAL.dtstart,
						"1997-04-03T18:00:00.000Z");
				assertContains(subject, vICAL.dtstamp,
						"1997-09-01T13:00:00.000Z");
				assertContains(subject, vICAL.dtend, "1997-04-02T01:00:00.000Z");
				assertContains(subject, vICAL.uid,
						"19970901T130000Z-123402@host.com");
				assertContains(subject, vICAL.summary,
						"Laurel is in sensitivity awareness class.");
				assertContains(subject, vICAL.class_, "public");
				assertContains(subject, vICAL.transp, "transparent");
				assertContains(subject, vICAL.categories, "Business");
				assertContains(subject, vICAL.categories, "Human Resources");
			}
		} finally {
			result.close();
		}
	}

	@Test
	public void testRepetitiveEvent() throws Exception {
		assertExtract("/microformats/hcalendar/example3.html");
		assertModelNotEmpty();
		assertContains(baseIRI, RDF.TYPE, vcal);
		assertContains(null, RDF.TYPE, vevent);
		RepositoryResult<Statement> result = getStatements(null, RDF.TYPE,
				vevent);
		try {
			while (result.hasNext()) {
				Statement statement = result.next();
				final Resource subject = statement.getSubject();
				assertContains(null, vICAL.component, subject);
				assertContains(subject, RDF.TYPE, vevent);
				assertContains(subject, vICAL.dtstart, "19971102");
				assertContains(subject, vICAL.dtstamp,
						"1997-09-01T13:00:00.000Z");
				assertContains(subject, vICAL.uid,
						"19970901T130000Z-123403@host.com");
				assertContains(subject, vICAL.summary,
						"Our Blissful Anniversary");
				assertContains(subject, vICAL.class_, "confidential");
				assertContains(subject, vICAL.categories, "Anniversary");
				assertContains(subject, vICAL.categories, "Personal");
				assertContains(subject, vICAL.categories, "Special Occassion");
				assertContains(subject, vICAL.rrule, (Value) null);
			}
		} finally {
			result.close();
		}
	}

	@Test
	public void testThreeDayEvent() throws Exception {
		assertExtract("/microformats/hcalendar/example5.html");
		assertModelNotEmpty();
		assertContains(baseIRI, RDF.TYPE, vcal);
		assertContains(null, RDF.TYPE, vevent);
		RepositoryResult<Statement> result = getStatements(null, RDF.TYPE,
				vevent);
		try {
			while (result.hasNext()) {
				Statement statement = result.next();
				final Resource subject = statement.getSubject();
				assertContains(null, vICAL.component, subject);
				assertContains(subject, RDF.TYPE, vevent);
				assertContains(subject, vICAL.dtstart,
						"1996-09-20T16:00:00.000Z");
				assertContains(subject, vICAL.dtstamp,
						"1996-07-04T12:00:00.000Z");
				assertContains(subject, vICAL.dtend, "1996-09-20T22:00:00.000Z");
				assertContains(subject, vICAL.uid, "uid1@host.com");
				assertContains(subject, vICAL.summary,
						"Networld+Interop Conference");
				assertContains(subject, vICAL.description,
						"Networld+Interop Conference and Exhibit Atlanta World Congress\n"
								+ "  Center Atlanta, Georgia");
				assertContains(subject, vICAL.categories, "Conference");
				assertContains(subject, vICAL.status, "CONFIRMED");
				assertContains(subject, vICAL.organizer, (Value) null);
			}
		} finally {
			result.close();
		}
	}

	@Test
	public void testHCalendarWithBudyInfo() throws Exception {
		assertExtract("/microformats/hcalendar/example5.5.html");
		assertModelNotEmpty();
		assertContains(baseIRI, RDF.TYPE, vcal);
		assertContains(null, RDF.TYPE, vjournal);
	}

	@Test
	public void test01() throws Exception {
		assertDefault("/microformats/hcalendar/01-component-vevent-dtstart-date.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, vICAL.dtstart, "19970903");
	}

	@Test
	public void test02() throws Exception {
		assertDefault("/microformats/hcalendar/02-component-vevent-dtstart-datetime.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, vICAL.dtstart, "1997-09-05T18:00:00.000Z");
	}

	@Test
	public void test03() throws Exception {
		assertDefault("/microformats/hcalendar/03-component-vevent-dtend-date.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, vICAL.dtstart, "19970903");
		assertContains(event, vICAL.dtend, "19970904");
	}

	@Test
	public void test04() throws Exception {
		assertDefault("/microformats/hcalendar/04-component-vevent-dtend-datetime.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, vICAL.dtstart, "1997-09-03T16:00:00.000Z");
		assertContains(event, vICAL.dtend, "1997-09-03T18:00:00.000Z");
	}

	@Test
	public void test05() throws Exception {
		assertDefault("/microformats/hcalendar/05-calendar-simple.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, vICAL.dtstart, "2005-10-05");
		assertContains(event, vICAL.dtend, "2005-10-08");
		assertContains(event, vICAL.summary, "Web 2.0 Conference");
		assertContains(event, vICAL.url,
				RDFUtils.iri("http://www.web2con.com/"));
		assertContains(event, vICAL.location, "Argent Hotel, San Francisco, CA");
	}

	@Test
	public void test06() throws Exception {
		assertDefault("/microformats/hcalendar/06-component-vevent-uri-relative.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, vICAL.dtstart, "20060115T000000");
		assertContains(event, vICAL.summary,
				"Bad Movie Night - Gigli (blame mike spiegelman)");
		assertContains(event, vICAL.url,
				RDFUtils.iri(baseIRI + "squidlist/calendar/12279/2006/1/15"));
	}

	@Test
	public void test07() throws Exception {
		assertDefault("/microformats/hcalendar/07-component-vevent-description-simple.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, vICAL.description,
				"Project xyz Review Meeting Minutes");
		assertNotContains(event, vICAL.url, (Resource) null);
	}

	@Test
	public void test08() throws Exception {
		assertDefault("/microformats/hcalendar/08-component-vevent-multiple-classes.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, vICAL.dtstart, "2005-10-05");
		assertContains(event, vICAL.dtend, "2005-10-08");
		assertContains(event, vICAL.summary, "Web 2.0 Conference");
		assertContains(event, vICAL.url,
				RDFUtils.iri("http://www.web2con.com/"));
		assertContains(event, vICAL.location, "Argent Hotel, San Francisco, CA");
	}

	@Test
	public void test09() throws Exception {
		assertDefault("/microformats/hcalendar/09-component-vevent-summary-in-img-alt.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, vICAL.dtend, "20060310");
		assertContains(event, vICAL.dtstart, "20060306");
		assertContains(event, vICAL.summary,
				"O'Reilly Emerging Technology Conference");
		assertContains(event, vICAL.url,
				RDFUtils.iri("http://conferences.oreillynet.com/et2006/"));
		assertContains(event, vICAL.location,
				"Manchester Grand Hyatt in San Diego, CA");
	}

	@Test
	public void test10() throws Exception {
		assertDefault("/microformats/hcalendar/10-component-vevent-entity.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, vICAL.summary, "Cricket & Tennis Centre");
		assertContains(event, vICAL.description,
				"Melbourne's Cricket & Tennis Centres are in the heart of the city");
	}

	@Test
	public void test11() throws Exception {
		assertDefault("/microformats/hcalendar/11-component-vevent-summary-in-subelements.html");
		Resource event = getExactlyOneComponent(vevent);

		assertContains(event, vICAL.dtstart, "20051005T1630-0700");
		assertContains(event, vICAL.dtend, "20051005T1645-0700");
		assertContains(event, vICAL.summary,
				"Welcome!\n      John Battelle,\n      Tim O'Reilly");
	}

	@Test
	public void test12() throws Exception {
		assertDefault("/microformats/hcalendar/12-component-vevent-summary-url-in-same-class.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, vICAL.dtstart, "20060125T000000");
		assertContains(
				event,
				vICAL.url,
				RDFUtils.iri("http://www.laughingsquid.com/squidlist/calendar/12377/2006/1/25"));
		assertContains(event, vICAL.summary,
				"Art Reception for Tom Schultz and Felix Macnee");
	}

	@Test
	public void test13() throws Exception {
		assertDefault("/microformats/hcalendar/13-component-vevent-summary-url-property.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(
				event,
				vICAL.url,
				RDFUtils.iri("http://dps1.travelocity.com/dparcobrand.ctl?smls=Y&Service=YHOE&.intl=us&aln_name=AA&flt_num="
				+ "1655&dep_arp_name=&arr_arp_name=&dep_dt_dy_1=23&dep_dt_mn_1=Jan&dep_dt_yr_1=2006&dep_tm_1=9:00am"));
		assertContains(event, vICAL.summary, "ORD-SFO/AA 1655");
	}

	@Test
	public void test15() throws Exception {
		assertDefault("/microformats/hcalendar/15-calendar-xml-lang.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, vICAL.dtstart, "2005-10-05");
		assertContains(event, vICAL.dtend, "2005-10-08");
		assertContains(event, vICAL.summary, "Web 2.0 Conference");
		assertContains(event, vICAL.url,
				RDFUtils.iri("http://www.web2con.com/"));
		assertContains(event, vICAL.location, "Argent Hotel, San Francisco, CA");
	}

	@Test
	public void test16() throws Exception {
		assertDefault("/microformats/hcalendar/16-calendar-force-outlook.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, vICAL.dtstart, "2005-10-05");
		assertContains(event, vICAL.dtend, "2005-10-08");
		assertContains(event, vICAL.location, "Argent Hotel, San Francisco, CA");
	}

	@Test
	public void test17() throws Exception {
		assertDefault("/microformats/hcalendar/17-component-vevent-description-value-in-subelements.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, vICAL.dtstart, "2006-01-18");
		assertContains(event, vICAL.dtend, "2006-01-20");
		assertContains(event, vICAL.location, "Maryland");
		assertContains(event, vICAL.summary, "3rd PAW ftf meeting");
		assertContains(
				event,
				vICAL.description,
				"RESOLUTION: to have a\n      3rd PAW ftf meeting \n"
						+ "      18-19 Jan in \n      Maryland; location contingent"
						+ " on confirmation from timbl");
	}

	@Test
	public void test18() throws Exception {
		assertDefault("/microformats/hcalendar/18-component-vevent-uid.html");
		assertStatementsSize(RDF.TYPE, vevent, 5);
		assertStatementsSize(vICAL.uid,
				RDFUtils.literal("http://example.com/foo.html"), 5);
	}

	@Test
	public void testNoMicroformats() throws Exception, IOException,
			ExtractionException {
		extract("/html/html-without-uf.html");
		assertModelEmpty();
	}

	@Test
	public void testNoMicroformatsInStatCvsPage() throws Exception,
			IOException, ExtractionException {
		extract("/microformats/hcalendar/empty-statcvs.html");
		assertModelEmpty();
	}

	@Test
	public void testFullHCalendarClass() throws Exception {
		assertExtract("/microformats/hcalendar/example5.3.html");
		assertModelNotEmpty();
		assertContains(baseIRI, RDF.TYPE, vcal);
		assertContains(null, RDF.TYPE, vevent);
	}

	@Test
	public void testHCalendarClassWithTodo() throws Exception {
		assertExtract("/microformats/hcalendar/example5.4.html");
		assertModelNotEmpty();
		assertContains(baseIRI, RDF.TYPE, vcal);
		assertContains(null, RDF.TYPE, vtodo);
	}

	@Test
	public void testHCalendarClassWithJournal() throws Exception {
		assertExtract("/microformats/hcalendar/example5.5.html");
		assertModelNotEmpty();
		assertContains(baseIRI, RDF.TYPE, vcal);
		assertContains(null, RDF.TYPE, vjournal);
	}

	private Resource getExactlyOneComponent(Resource r) throws Exception {
		RepositoryResult<Statement> result = getStatements(null, RDF.TYPE, r);
		try {
			Assert.assertTrue(result.hasNext());
			Resource sub = result.next().getSubject();
			Assert.assertFalse(result.hasNext());
			return sub;
		} finally {
			result.close();
		}
	}

	private void assertDefault(String name) throws Exception {
		assertExtract(name);
		assertModelNotEmpty();
		assertContains(baseIRI, RDF.TYPE, vcal);
		assertStatementsSize(RDF.TYPE, vcal, 1);
	}

}
