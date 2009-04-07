package com.google.code.any23.extractors;


import org.deri.any23.TestHelper;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.extractor.html.AbstractMicroformatTestCase;
import org.deri.any23.extractor.html.HCalendarExtractor;
import org.deri.any23.extractor.html.HTMLFixture;
import org.deri.any23.vocab.ICAL;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;

//TODO COPY & PASTING FTW! sorry, I'll asbtract later
public class HCalendarExtractorTest extends AbstractMicroformatTestCase {

	protected final static URI vcal = ICAL.Vcalendar;

	protected final static URI vevent = ICAL.Vevent;
	protected final static URI vjournal = ICAL.Vjournal;
	protected final static URI vtodo = ICAL.Vtodo;
	protected final static URI vfreebusy = ICAL.Vfreebusy;

	protected ExtractorFactory<?> getExtractorFactory() {
		return HCalendarExtractor.factory;
	}
	
	// old examples from the uF pages
	public void testOneVEvent() throws RepositoryException {
		assertExtracts("hcalendar/example1.html");
		assertModelNotEmpty();
		assertContains(baseURI, RDF.TYPE, vcal );
		assertContains(null, RDF.TYPE, vevent );
		ResIterator iter = model.listSubjectsWithProperty(RDF.TYPE, vevent);
		while(iter.hasNext()) {
			Resource ev = iter.nextResource();
			assertContains(null, ICAL.component, ev);
			assertContains(ev, RDF.TYPE, vevent);
			assertContains(ev, ICAL.dtstart, "19970903T163000Z");
			assertContains(ev, ICAL.dtstamp, "19970901T1300Z");
			assertContains(ev, ICAL.dtend, "19970903T190000Z");
			assertContains(ev, ICAL.uid, "19970901T130000Z-123401@host.com");
			assertContains(ev, ICAL.summary, "Annual Employee Review");
			assertContains(ev, ICAL.class_, "private");
			assertContains(ev, ICAL.categories, "Business");
			assertContains(ev, ICAL.categories, "Human Resources");

		}
	}

	public void testTransparentEvent() throws RepositoryException {
		assertExtracts("hcalendar/example2.html");
		assertModelNotEmpty();
		assertContains(baseURI, RDF.TYPE, vcal );
		assertContains(null, RDF.TYPE, vevent );
		ResIterator iter = model.listSubjectsWithProperty(RDF.TYPE, vevent);
		while(iter.hasNext()) {
			Resource ev = iter.nextResource();
			assertContains(null, ICAL.component, ev);
			assertContains(ev, RDF.TYPE, vevent);
			assertContains(ev, ICAL.dtstart, "19970401T163000Z");
			assertContains(ev, ICAL.dtstamp, "19970901T1300Z");
			assertContains(ev, ICAL.dtend, "19970402T010000Z");
			assertContains(ev, ICAL.uid, "19970901T130000Z-123402@host.com");
			assertContains(ev, ICAL.summary, "Laurel is in sensitivity awareness class.");
			assertContains(ev, ICAL.class_, "public");
			assertContains(ev, ICAL.transp, "transparent");
			assertContains(ev, ICAL.categories, "Business");
			assertContains(ev, ICAL.categories, "Human Resources");
		}
	}
	
	public void testRepetitiveEvent() throws RepositoryException {
		assertExtracts("hcalendar/example3.html");
		assertModelNotEmpty();
		assertContains(baseURI, RDF.TYPE, vcal );
		assertContains(null, RDF.TYPE, vevent );
		ResIterator iter = model.listSubjectsWithProperty(RDF.TYPE, vevent);
		while(iter.hasNext()) {
			Resource ev = iter.nextResource();
			assertContains(null, ICAL.component, ev);
			assertContains(ev, RDF.TYPE, vevent);
			assertContains(ev, ICAL.dtstart, "19971102");
			assertContains(ev, ICAL.dtstamp, "19970901T1300Z");
			assertContains(ev, ICAL.uid, "19970901T130000Z-123403@host.com");
			assertContains(ev, ICAL.summary, "Our Blissful Anniversary");
			assertContains(ev, ICAL.class_, "confidential");
			assertContains(ev, ICAL.categories, "Anniversary");
			assertContains(ev, ICAL.categories, "Personal");
			assertContains(ev, ICAL.categories, "Special Occassion");
			
			assertContains(ev, ICAL.rrule, (Value) null);
			Resource rrule = ev.getProperty(ICAL.rrule).getResource();
			assertTrue(rrule.hasProperty(RDF.TYPE, ICAL.DomainOf_rrule));
			assertTrue(rrule.hasProperty(ICAL.freq, "yearly"));
		}
	}
	
	public void testThreeDayEvent() throws RepositoryException {
		assertExtracts("hcalendar/example5.html");
		assertModelNotEmpty();
		assertContains(baseURI, RDF.TYPE, vcal );
		assertContains(null, RDF.TYPE, vevent );
		ResIterator iter = model.listSubjectsWithProperty(RDF.TYPE, vevent);
		while(iter.hasNext()) {
			Resource ev = iter.nextResource();
			assertContains(null, ICAL.component, ev);
			assertContains(ev, RDF.TYPE, vevent);
			assertContains(ev, ICAL.dtstart, "19960918T143000Z");
			assertContains(ev, ICAL.dtstamp, "19960704T120000Z");
			assertContains(ev, ICAL.dtend, "19960920T220000Z");
			assertContains(ev, ICAL.uid, "uid1@host.com");
			assertContains(ev, ICAL.summary, "Networld+Interop Conference");
			assertContains(ev, ICAL.description, "Networld+Interop Conference and Exhibit Atlanta World Congress\n  Center Atlanta, Georgia");
			assertContains(ev, ICAL.categories, "Conference");
			assertContains(ev, ICAL.status, "CONFIRMED");
			
			assertContains(ev, ICAL.organizer, (Value) null);
			Resource organizer = ev.getProperty(ICAL.organizer).getResource();
//			dumpModel();
			assertContains(organizer, ICAL.calAddress,"mailto:jsmith@host.com");
		}

	}

	public void testHCalendarWithBudyInfo() throws RepositoryException {
		assertExtracts("hcalendar/example5.5.html");
		assertModelNotEmpty();		
		assertContains(baseURI, RDF.TYPE, vcal );
		assertContains(null, RDF.TYPE, vjournal );
	}

	// uF test suite
	public void test01() throws RepositoryException {
		assertDefault("hcalendar/01-component-vevent-dtstart-date.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, ICAL.dtstart, "19970903");
	}

	public void test02() throws RepositoryException {
		assertDefault("hcalendar/02-component-vevent-dtstart-datetime.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, ICAL.dtstart, "19970903T163000Z");
	}

	public void test03() throws RepositoryException {
		assertDefault("hcalendar/03-component-vevent-dtend-date.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, ICAL.dtstart, "19970903");
		assertContains(event, ICAL.dtend, "19970904");
	}	

	public void test04() throws RepositoryException {
		assertDefault("hcalendar/04-component-vevent-dtend-datetime.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, ICAL.dtstart, "19970903T160000Z");
		assertContains(event, ICAL.dtend, "19970903T180000Z");
	}		

	public void test05() throws RepositoryException {
		assertDefault("hcalendar/05-calendar-simple.html");
		Resource event = getExactlyOneComponent(vevent);
		// todo normalize dates
		assertContains(event, ICAL.dtstart, "2005-10-05");
		assertContains(event, ICAL.dtend, "2005-10-08");
		assertContains(event, ICAL.summary, "Web 2.0 Conference");
		assertContains(event, ICAL.url, TestHelper.uri("http://www.web2con.com/"));
		assertContains(event, ICAL.location, "Argent Hotel, San Francisco, CA");
	}		
	
	public void test06() throws RepositoryException {
		assertDefault("hcalendar/06-component-vevent-uri-relative.html");
		Resource event = getExactlyOneComponent(vevent);
		// todo normalize dates
		assertContains(event, ICAL.dtstart, "20060115T000000");
		assertContains(event, ICAL.summary, "Bad Movie Night - Gigli (blame mike spiegelman)");
		assertContains(event, ICAL.url, TestHelper.uri(baseURI+"squidlist/calendar/12279/2006/1/15"));
	}

	public void test07() throws RepositoryException {
		assertDefault("hcalendar/07-component-vevent-description-simple.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, ICAL.description, "Project xyz Review Meeting Minutes");
		assertNotContains(event, ICAL.url, (Resource)null);
	}

	public void test08() throws RepositoryException {
		assertDefault("hcalendar/08-component-vevent-multiple-classes.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, ICAL.dtstart, "2005-10-05");
		assertContains(event, ICAL.dtend, "2005-10-08");
		assertContains(event, ICAL.summary, "Web 2.0 Conference");
		assertContains(event, ICAL.url, TestHelper.uri("http://www.web2con.com/"));
		assertContains(event, ICAL.location, "Argent Hotel, San Francisco, CA");
	}

	public void test09() throws RepositoryException {
		assertDefault("hcalendar/09-component-vevent-summary-in-img-alt.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, ICAL.dtend, "20060310");
		assertContains(event, ICAL.dtstart, "20060306");
		assertContains(event, ICAL.summary, "O'Reilly Emerging Technology Conference");
		assertContains(event, ICAL.url, TestHelper.uri("http://conferences.oreillynet.com/et2006/"));
		assertContains(event, ICAL.location, "Manchester Grand Hyatt in San Diego, CA");
	}

	public void test10() throws RepositoryException {
		assertDefault("hcalendar/10-component-vevent-entity.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, ICAL.summary, "Cricket & Tennis Centre");
		assertContains(event, ICAL.description, "Melbourne's Cricket & Tennis Centres are in the heart of the city");
	}
	
	public void test11() throws RepositoryException {
		assertDefault("hcalendar/11-component-vevent-summary-in-subelements.html");
		Resource event = getExactlyOneComponent(vevent);
		
		assertContains(event, ICAL.dtstart, "20051005T1630-0700");
		assertContains(event, ICAL.dtend, "20051005T1645-0700");
		//cleanup spaces TODO
		assertContains(event, ICAL.summary, "Welcome!\n      John Battelle,\n      Tim O'Reilly");
	}

	public void test12() throws RepositoryException {
		assertDefault("hcalendar/12-component-vevent-summary-url-in-same-class.html");
		Resource event = getExactlyOneComponent(vevent);	
		assertContains(event, ICAL.dtstart, "20060125T000000");
		assertContains(event, ICAL.url, TestHelper.uri("http://www.laughingsquid.com/squidlist/calendar/12377/2006/1/25"));
		assertContains(event, ICAL.summary, "Art Reception for Tom Schultz and Felix Macnee");
	}

	public void test13() throws RepositoryException {
		assertDefault("hcalendar/13-component-vevent-summary-url-property.html");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, ICAL.url, TestHelper.uri("http://dps1.travelocity.com/dparcobrand.ctl?smls=Y&Service=YHOE&.intl=us&aln_name=AA&flt_num=1655&dep_arp_name=&arr_arp_name=&dep_dt_dy_1=23&dep_dt_mn_1=Jan&dep_dt_yr_1=2006&dep_tm_1=9:00am"));
		assertContains(event, ICAL.summary, "ORD-SFO/AA 1655");
	}
	
	public void test15() throws RepositoryException {
		assertDefault("hcalendar/15-calendar-xml-lang.html");
		Resource event = getExactlyOneComponent(vevent);
		// todo normalize dates
		assertContains(event, ICAL.dtstart, "2005-10-05");
		assertContains(event, ICAL.dtend, "2005-10-08");
		assertContains(event, ICAL.summary, "Web 2.0 Conference");
		assertContains(event, ICAL.url, TestHelper.uri("http://www.web2con.com/"));
		assertContains(event, ICAL.location, "Argent Hotel, San Francisco, CA");
	}

	public void test16() throws RepositoryException {
		assertDefault("hcalendar/16-calendar-force-outlook.html");
		Resource event = getExactlyOneComponent(vevent);
		// todo normalize dates
		assertContains(event, ICAL.dtstart, "2005-10-05");
		assertContains(event, ICAL.dtend, "2005-10-08");
		assertContains(event, ICAL.location, "Argent Hotel, San Francisco, CA");
	}

	public void test17() throws RepositoryException {
		assertDefault("hcalendar/17-component-vevent-description-value-in-subelements.html");
		Resource event = getExactlyOneComponent(vevent);
		// todo normalize dates
		assertContains(event, ICAL.dtstart, "2006-01-18");
		assertContains(event, ICAL.dtend, "2006-01-20");
		assertContains(event, ICAL.location, "Maryland");
		assertContains(event, ICAL.summary, "3rd PAW ftf meeting");
		assertContains(event, ICAL.description,
				"RESOLUTION: to have a\n      3rd PAW ftf meeting \n      18-19 Jan in \n      Maryland; location contingent on confirmation from timbl");
	}

	public void test18() throws RepositoryException {
		assertDefault("hcalendar/18-component-vevent-uid.html");
		assertStatementsSize(RDF.TYPE, vevent, 5);
		assertStatementsSize(ICAL.uid, TestHelper.uri("http://example.com/foo.html"), 5);
	}
	//TODO attachments
	
	private Resource getExactlyOneComponent(Resource r) {
		ResIterator iter = model.listSubjectsWithProperty(RDF.TYPE, r);
		assertTrue(iter.hasNext());
		Resource next = iter.nextResource();
		assertTrue(!iter.hasNext());
		return next;
	}

	private void assertDefault(String name) throws RepositoryException {
		assertExtracts(name);
		assertModelNotEmpty();		
		assertContains(baseURI, RDF.TYPE, vcal );
		assertStatementsSize(RDF.TYPE, vcal, 1);
	}

	public void testNoMicroformats() throws RepositoryException {
		assertNotExtracts("html-without-uf.html");
		assertModelEmpty();
	}

	public void testNoMicroformatsInStatCvsPage() throws RepositoryException {
		assertNotExtracts("hcalendar/empty-statcvs.html");
		assertModelEmpty();
	}

	public void testFullHCalendarClass() throws RepositoryException {
		assertExtracts("hcalendar/example5.3.html");
		assertModelNotEmpty();
		assertContains(baseURI, RDF.TYPE, vcal );
		assertContains(null, RDF.TYPE, vevent );
	}

	public void testHCalendarClassWithTodo() throws RepositoryException {
		assertExtracts("hcalendar/example5.4.html");
		assertModelNotEmpty();
		assertContains(baseURI, RDF.TYPE, vcal );
		assertContains(null, RDF.TYPE, vtodo );		
	}

	public void testHCalendarClassWithJournal() throws RepositoryException {
		assertExtracts("hcalendar/example5.5.html");
		assertModelNotEmpty();
		assertContains(baseURI, RDF.TYPE, vcal );
		assertContains(null, RDF.TYPE, vjournal );
	}
}