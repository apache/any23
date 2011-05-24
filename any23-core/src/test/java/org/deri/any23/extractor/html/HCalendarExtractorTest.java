/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.extractor.html;

import junit.framework.Assert;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.util.RDFHelper;
import org.deri.any23.vocab.ICAL;
import org.deri.any23.vocab.SINDICE;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import java.io.IOException;

/**
 * Test case for {@link org.deri.any23.extractor.html.HCalendarExtractor}class.
 *
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @version $Id$
 */
public class HCalendarExtractorTest extends AbstractExtractorTestCase {

    private final static URI vcal      = ICAL.Vcalendar;
    private final static URI vevent    = ICAL.Vevent;
    private final static URI vjournal  = ICAL.Vjournal;
    private final static URI vtodo     = ICAL.Vtodo;

    protected ExtractorFactory<?> getExtractorFactory() {
        return HCalendarExtractor.factory;
    }

    @Test
    public void testOneVEvent() throws RepositoryException {
        assertExtracts("microformats/hcalendar/example1.html");
        assertModelNotEmpty();
        assertContains(baseURI, RDF.TYPE, vcal);
        assertContains(null, RDF.TYPE, vevent);
        RepositoryResult<Statement> result = conn.getStatements(null, RDF.TYPE, vevent, false);
        try {
            while (result.hasNext()) {
                Statement statement = result.next();
                final Resource subject = statement.getSubject();
                assertContains(null, ICAL.component, subject);
                assertContains(subject, RDF.TYPE, vevent);
                assertContains(subject, ICAL.dtstart, "1997-09-05T18:00:00.000Z");
                assertContains(subject, ICAL.dtstamp, "1997-09-01T13:00:00.000Z");
                assertContains(subject, ICAL.dtend, "1997-09-03T19:00:00.000Z");
                assertContains(subject, ICAL.uid, "19970901T130000Z-123401@host.com");
                assertContains(subject, ICAL.summary, "Annual Employee Review");
                assertContains(subject, ICAL.class_, "private");
                assertContains(subject, ICAL.categories, "Business");
                assertContains(subject, ICAL.categories, "Human Resources");
            }
        } finally {
            result.close();
        }
    }

    @Test
    public void testTransparentEvent() throws RepositoryException {
        assertExtracts("microformats/hcalendar/example2.html");
        assertModelNotEmpty();
        assertContains(baseURI, RDF.TYPE, vcal);
        assertContains(null, RDF.TYPE, vevent);
        RepositoryResult<Statement> result = conn.getStatements(null, RDF.TYPE, vevent, false);
        try {
            while (result.hasNext()) {
                Statement statement = result.next();
                final Resource subject = statement.getSubject();
                assertContains(null, ICAL.component, subject);
                assertContains(subject, RDF.TYPE, vevent);
                assertContains(subject, ICAL.dtstart, "1997-04-03T18:00:00.000Z");
                assertContains(subject, ICAL.dtstamp, "1997-09-01T13:00:00.000Z");
                assertContains(subject, ICAL.dtend, "1997-04-02T01:00:00.000Z");
                assertContains(subject, ICAL.uid, "19970901T130000Z-123402@host.com");
                assertContains(subject, ICAL.summary, "Laurel is in sensitivity awareness class.");
                assertContains(subject, ICAL.class_, "public");
                assertContains(subject, ICAL.transp, "transparent");
                assertContains(subject, ICAL.categories, "Business");
                assertContains(subject, ICAL.categories, "Human Resources");
            }
        } finally {
            result.close();
        }
    }

    @Test
    public void testRepetitiveEvent() throws RepositoryException {
        assertExtracts("microformats/hcalendar/example3.html");
        assertModelNotEmpty();
        assertContains(baseURI, RDF.TYPE, vcal);
        assertContains(null, RDF.TYPE, vevent);
        RepositoryResult<Statement> result = conn.getStatements(null, RDF.TYPE, vevent, false);
        try {
            while (result.hasNext()) {
                Statement statement = result.next();
                final Resource subject = statement.getSubject();
                assertContains(null, ICAL.component, subject);
                assertContains(subject, RDF.TYPE, vevent);
                assertContains(subject, ICAL.dtstart, "19971102");
                assertContains(subject, ICAL.dtstamp, "1997-09-01T13:00:00.000Z");
                assertContains(subject, ICAL.uid, "19970901T130000Z-123403@host.com");
                assertContains(subject, ICAL.summary, "Our Blissful Anniversary");
                assertContains(subject, ICAL.class_, "confidential");
                assertContains(subject, ICAL.categories, "Anniversary");
                assertContains(subject, ICAL.categories, "Personal");
                assertContains(subject, ICAL.categories, "Special Occassion");
                assertContains(subject, ICAL.rrule, (Value) null);
            }
        } finally {
            result.close();
        }
    }

    @Test
    public void testThreeDayEvent() throws RepositoryException {
        assertExtracts("microformats/hcalendar/example5.html");
        assertModelNotEmpty();
        assertContains(baseURI, RDF.TYPE, vcal);
        assertContains(null, RDF.TYPE, vevent);
        RepositoryResult<Statement> result = conn.getStatements(null, RDF.TYPE, vevent, false);
        try {
            while (result.hasNext()) {
                Statement statement = result.next();
                final Resource subject = statement.getSubject();
                assertContains(null, ICAL.component, subject);
                assertContains(subject, RDF.TYPE, vevent);
                assertContains(subject, ICAL.dtstart, "1996-09-20T16:00:00.000Z");
                assertContains(subject, ICAL.dtstamp, "1996-07-04T12:00:00.000Z");
                assertContains(subject, ICAL.dtend, "1996-09-20T22:00:00.000Z");
                assertContains(subject, ICAL.uid, "uid1@host.com");
                assertContains(subject, ICAL.summary, "Networld+Interop Conference");
                assertContains(subject, ICAL.description, "Networld+Interop Conference and Exhibit Atlanta World Congress\n" +
                        "  Center Atlanta, Georgia");
                assertContains(subject, ICAL.categories, "Conference");
                assertContains(subject, ICAL.status, "CONFIRMED");
                assertContains(subject, ICAL.organizer, (Value) null);
            }
        } finally {
            result.close();
        }
    }

    @Test
    public void testHCalendarWithBudyInfo() throws RepositoryException {
        assertExtracts("microformats/hcalendar/example5.5.html");
        assertModelNotEmpty();
        assertContains(baseURI, RDF.TYPE, vcal);
        assertContains(null, RDF.TYPE, vjournal);
    }

    @Test
    public void test01() throws RepositoryException {
        assertDefault("microformats/hcalendar/01-component-vevent-dtstart-date.html");
        Resource event = getExactlyOneComponent(vevent);
        assertContains(event, ICAL.dtstart, "19970903");
    }

    @Test
    public void test02() throws RepositoryException {
        assertDefault("microformats/hcalendar/02-component-vevent-dtstart-datetime.html");
        Resource event = getExactlyOneComponent(vevent);
        assertContains(event, ICAL.dtstart, "1997-09-05T18:00:00.000Z");
    }

    @Test
    public void test03() throws RepositoryException {
        assertDefault("microformats/hcalendar/03-component-vevent-dtend-date.html");
        Resource event = getExactlyOneComponent(vevent);
        assertContains(event, ICAL.dtstart, "19970903");
        assertContains(event, ICAL.dtend, "19970904");
    }

    @Test
    public void test04() throws RepositoryException {
        assertDefault("microformats/hcalendar/04-component-vevent-dtend-datetime.html");
        Resource event = getExactlyOneComponent(vevent);
        assertContains(event, ICAL.dtstart, "1997-09-03T16:00:00.000Z");
        assertContains(event, ICAL.dtend, "1997-09-03T18:00:00.000Z");
    }

    @Test
    public void test05() throws RepositoryException {
        assertDefault("microformats/hcalendar/05-calendar-simple.html");
        Resource event = getExactlyOneComponent(vevent);
        assertContains(event, ICAL.dtstart, "2005-10-05");
        assertContains(event, ICAL.dtend, "2005-10-08");
        assertContains(event, ICAL.summary, "Web 2.0 Conference");
        assertContains(event, ICAL.url, RDFHelper.uri("http://www.web2con.com/"));
        assertContains(event, ICAL.location, "Argent Hotel, San Francisco, CA");
    }

    @Test
    public void test06() throws RepositoryException {
        assertDefault("microformats/hcalendar/06-component-vevent-uri-relative.html");
        Resource event = getExactlyOneComponent(vevent);
        assertContains(event, ICAL.dtstart, "20060115T000000");
        assertContains(event, ICAL.summary, "Bad Movie Night - Gigli (blame mike spiegelman)");
        assertContains(event, ICAL.url, RDFHelper.uri(baseURI + "squidlist/calendar/12279/2006/1/15"));
    }

    @Test
    public void test07() throws RepositoryException {
        assertDefault("microformats/hcalendar/07-component-vevent-description-simple.html");
        Resource event = getExactlyOneComponent(vevent);
        assertContains(event, ICAL.description, "Project xyz Review Meeting Minutes");
        assertNotContains(event, ICAL.url, (Resource) null);
    }

    @Test
    public void test08() throws RepositoryException {
        assertDefault("microformats/hcalendar/08-component-vevent-multiple-classes.html");
        Resource event = getExactlyOneComponent(vevent);
        assertContains(event, ICAL.dtstart, "2005-10-05");
        assertContains(event, ICAL.dtend, "2005-10-08");
        assertContains(event, ICAL.summary, "Web 2.0 Conference");
        assertContains(event, ICAL.url, RDFHelper.uri("http://www.web2con.com/"));
        assertContains(event, ICAL.location, "Argent Hotel, San Francisco, CA");
    }

    @Test
    public void test09() throws RepositoryException {
        assertDefault("microformats/hcalendar/09-component-vevent-summary-in-img-alt.html");
        Resource event = getExactlyOneComponent(vevent);
        assertContains(event, ICAL.dtend, "20060310");
        assertContains(event, ICAL.dtstart, "20060306");
        assertContains(event, ICAL.summary, "O'Reilly Emerging Technology Conference");
        assertContains(event, ICAL.url, RDFHelper.uri("http://conferences.oreillynet.com/et2006/"));
        assertContains(event, ICAL.location, "Manchester Grand Hyatt in San Diego, CA");
    }

    @Test
    public void test10() throws RepositoryException {
        assertDefault("microformats/hcalendar/10-component-vevent-entity.html");
        Resource event = getExactlyOneComponent(vevent);
        assertContains(event, ICAL.summary, "Cricket & Tennis Centre");
        assertContains(event, ICAL.description, "Melbourne's Cricket & Tennis Centres are in the heart of the city");
    }

    @Test
    public void test11() throws RepositoryException {
        assertDefault("microformats/hcalendar/11-component-vevent-summary-in-subelements.html");
        Resource event = getExactlyOneComponent(vevent);

        assertContains(event, ICAL.dtstart, "20051005T1630-0700");
        assertContains(event, ICAL.dtend, "20051005T1645-0700");
        assertContains(event, ICAL.summary, "Welcome!\n      John Battelle,\n      Tim O'Reilly");
    }

    @Test
    public void test12() throws RepositoryException {
        assertDefault("microformats/hcalendar/12-component-vevent-summary-url-in-same-class.html");
        Resource event = getExactlyOneComponent(vevent);
        assertContains(event, ICAL.dtstart, "20060125T000000");
        assertContains(event, ICAL.url, RDFHelper.uri("http://www.laughingsquid.com/squidlist/calendar/12377/2006/1/25"));
        assertContains(event, ICAL.summary, "Art Reception for Tom Schultz and Felix Macnee");
    }

    @Test
    public void test13() throws RepositoryException {
        assertDefault("microformats/hcalendar/13-component-vevent-summary-url-property.html");
        Resource event = getExactlyOneComponent(vevent);
        assertContains(event, ICAL.url, RDFHelper.uri(
                "http://dps1.travelocity.com/dparcobrand.ctl?smls=Y&Service=YHOE&.intl=us&aln_name=AA&flt_num=" +
                "1655&dep_arp_name=&arr_arp_name=&dep_dt_dy_1=23&dep_dt_mn_1=Jan&dep_dt_yr_1=2006&dep_tm_1=9:00am")
        );
        assertContains(event, ICAL.summary, "ORD-SFO/AA 1655");
    }

    @Test
    public void test15() throws RepositoryException {
        assertDefault("microformats/hcalendar/15-calendar-xml-lang.html");
        Resource event = getExactlyOneComponent(vevent);
        assertContains(event, ICAL.dtstart, "2005-10-05");
        assertContains(event, ICAL.dtend, "2005-10-08");
        assertContains(event, ICAL.summary, "Web 2.0 Conference");
        assertContains(event, ICAL.url, RDFHelper.uri("http://www.web2con.com/"));
        assertContains(event, ICAL.location, "Argent Hotel, San Francisco, CA");
    }

    @Test
    public void test16() throws RepositoryException {
        assertDefault("microformats/hcalendar/16-calendar-force-outlook.html");
        Resource event = getExactlyOneComponent(vevent);
        assertContains(event, ICAL.dtstart, "2005-10-05");
        assertContains(event, ICAL.dtend, "2005-10-08");
        assertContains(event, ICAL.location, "Argent Hotel, San Francisco, CA");
    }

    @Test
    public void test17() throws RepositoryException {
        assertDefault("microformats/hcalendar/17-component-vevent-description-value-in-subelements.html");
        Resource event = getExactlyOneComponent(vevent);
        assertContains(event, ICAL.dtstart, "2006-01-18");
        assertContains(event, ICAL.dtend, "2006-01-20");
        assertContains(event, ICAL.location, "Maryland");
        assertContains(event, ICAL.summary, "3rd PAW ftf meeting");
        assertContains(event, ICAL.description,
                "RESOLUTION: to have a\n      3rd PAW ftf meeting \n" +
                "      18-19 Jan in \n      Maryland; location contingent" +
                " on confirmation from timbl"
        );
    }

    @Test
    public void test18() throws RepositoryException {
		assertDefault("microformats/hcalendar/18-component-vevent-uid.html");
		assertStatementsSize(RDF.TYPE, vevent, 5);
		assertStatementsSize(ICAL.uid, RDFHelper.literal("http://example.com/foo.html"), 5);
	}

    @Test
    public void testNoMicroformats() throws RepositoryException, IOException, ExtractionException {
        extract("html/html-without-uf.html");
        assertModelNotEmpty();
        assertStatementsSize(null, null, null, 2);
        assertStatementsSize(SINDICE.getProperty(SINDICE.DATE), (Value) null, 1);
        assertStatementsSize(SINDICE.getProperty(SINDICE.SIZE), (Value) null, 1);
    }

    @Test
    public void testNoMicroformatsInStatCvsPage() throws RepositoryException, IOException, ExtractionException {
        extract("microformats/hcalendar/empty-statcvs.html");
        assertModelNotEmpty();
        assertModelNotEmpty();
        assertStatementsSize(null, null, null, 2);
        assertStatementsSize(SINDICE.getProperty(SINDICE.DATE), (Value) null, 1);
        assertStatementsSize(SINDICE.getProperty(SINDICE.SIZE), (Value) null, 1);
    }

    @Test
    public void testFullHCalendarClass() throws RepositoryException {
        assertExtracts("microformats/hcalendar/example5.3.html");
        assertModelNotEmpty();
        assertContains(baseURI, RDF.TYPE, vcal);
        assertContains(null, RDF.TYPE, vevent);
    }

    @Test
    public void testHCalendarClassWithTodo() throws RepositoryException {
        assertExtracts("microformats/hcalendar/example5.4.html");
        assertModelNotEmpty();
        assertContains(baseURI, RDF.TYPE, vcal);
        assertContains(null, RDF.TYPE, vtodo);
    }

    @Test
    public void testHCalendarClassWithJournal() throws RepositoryException {
        assertExtracts("microformats/hcalendar/example5.5.html");
        assertModelNotEmpty();
        assertContains(baseURI, RDF.TYPE, vcal);
        assertContains(null, RDF.TYPE, vjournal);
    }

    private Resource getExactlyOneComponent(Resource r) throws RepositoryException {
        RepositoryResult<Statement> result = conn.getStatements(null, RDF.TYPE, r, false);
        try {
            Assert.assertTrue(result.hasNext());
            Resource sub = result.next().getSubject();
            Assert.assertFalse(result.hasNext());
            return sub;
        } finally {
            result.close();
        }
    }

    private void assertDefault(String name) throws RepositoryException {
        assertExtracts(name);
        assertModelNotEmpty();
        assertContains(baseURI, RDF.TYPE, vcal);
        assertStatementsSize(RDF.TYPE, vcal, 1);
    }

}
