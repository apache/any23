package com.google.code.any23.extractors;


import com.google.code.any23.extractors.HCalendarExtractor;
import com.google.code.any23.vocab.ICAL;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;

//TODO COPY & PASTING FTW! sorry, I'll asbtract later
public class HCalendarExtractorTest extends AbstractMicroformatTestCase {

	protected final static Resource thePage = ResourceFactory.createResource(baseURI.toString());
	protected final static Resource vcal = ICAL.Vcalendar;

	protected final static Resource vevent = ICAL.Vevent;
	protected final static Resource vjournal = ICAL.Vjournal;
	protected final static Resource vtodo = ICAL.Vtodo;
	protected final static Resource vfreebusy = ICAL.Vfreebusy;
	// old examples from the uF pages
	
	public void testOneVEvent() {
		assertExtracts("example1");
		assertModelNotEmpty();
		assertContains(thePage, RDF.type, vcal );
		assertContains(null, RDF.type, vevent );
		ResIterator iter = model.listSubjectsWithProperty(RDF.type, vevent);
		while(iter.hasNext()) {
			Resource ev = iter.nextResource();
			assertContains(null, ICAL.component, ev);
			assertTrue(ev.hasProperty(RDF.type, vevent));
			assertTrue(ev.hasProperty(ICAL.dtstart, "19970903T163000Z"));
			assertTrue(ev.hasProperty(ICAL.dtstamp, "19970901T1300Z"));
			assertTrue(ev.hasProperty(ICAL.dtend, "19970903T190000Z"));
			assertTrue(ev.hasProperty(ICAL.uid, "19970901T130000Z-123401@host.com"));
			assertTrue(ev.hasProperty(ICAL.summary, "Annual Employee Review"));
			assertTrue(ev.hasProperty(ICAL.class_, "private"));
			assertTrue(ev.hasProperty(ICAL.categories, "Business"));
			assertTrue(ev.hasProperty(ICAL.categories, "Human Resources"));

		}
	}

	
	public void testTransparentEvent() {
		assertExtracts("example2");
		assertModelNotEmpty();
		assertContains(thePage, RDF.type, vcal );
		assertContains(null, RDF.type, vevent );
		ResIterator iter = model.listSubjectsWithProperty(RDF.type, vevent);
		while(iter.hasNext()) {
			Resource ev = iter.nextResource();
			assertContains(null, ICAL.component, ev);
			assertTrue(ev.hasProperty(RDF.type, vevent));
			assertTrue(ev.hasProperty(ICAL.dtstart, "19970401T163000Z"));
			assertTrue(ev.hasProperty(ICAL.dtstamp, "19970901T1300Z"));
			assertTrue(ev.hasProperty(ICAL.dtend, "19970402T010000Z"));
			assertTrue(ev.hasProperty(ICAL.uid, "19970901T130000Z-123402@host.com"));
			assertTrue(ev.hasProperty(ICAL.summary, "Laurel is in sensitivity awareness class."));
			assertTrue(ev.hasProperty(ICAL.class_, "public"));
			assertTrue(ev.hasProperty(ICAL.transp, "transparent"));
			assertTrue(ev.hasProperty(ICAL.categories, "Business"));
			assertTrue(ev.hasProperty(ICAL.categories, "Human Resources"));
		}
	}
	
	
	public void testRepetitiveEvent() {
		assertExtracts("example3");
		assertModelNotEmpty();
		assertContains(thePage, RDF.type, vcal );
		assertContains(null, RDF.type, vevent );
		ResIterator iter = model.listSubjectsWithProperty(RDF.type, vevent);
		while(iter.hasNext()) {
			Resource ev = iter.nextResource();
			assertContains(null, ICAL.component, ev);
			assertTrue(ev.hasProperty(RDF.type, vevent));
			assertTrue(ev.hasProperty(ICAL.dtstart, "19971102"));
			assertTrue(ev.hasProperty(ICAL.dtstamp, "19970901T1300Z"));
			assertTrue(ev.hasProperty(ICAL.uid, "19970901T130000Z-123403@host.com"));
			assertTrue(ev.hasProperty(ICAL.summary, "Our Blissful Anniversary"));
			assertTrue(ev.hasProperty(ICAL.class_, "confidential"));
			assertTrue(ev.hasProperty(ICAL.categories, "Anniversary"));
			assertTrue(ev.hasProperty(ICAL.categories, "Personal"));
			assertTrue(ev.hasProperty(ICAL.categories, "Special Occassion"));
			
			assertTrue(ev.hasProperty(ICAL.rrule));
			Resource rrule = ev.getProperty(ICAL.rrule).getResource();
			assertTrue(rrule.hasProperty(RDF.type, ICAL.DomainOf_rrule));
			assertTrue(rrule.hasProperty(ICAL.freq, "yearly"));
		}
	}
	
	
	public void testThreeDayEvent() {
		assertExtracts("example5");
		assertModelNotEmpty();
		assertContains(thePage, RDF.type, vcal );
		assertContains(null, RDF.type, vevent );
		ResIterator iter = model.listSubjectsWithProperty(RDF.type, vevent);
		while(iter.hasNext()) {
			Resource ev = iter.nextResource();
			assertContains(null, ICAL.component, ev);
			assertTrue(ev.hasProperty(RDF.type, vevent));
			assertTrue(ev.hasProperty(ICAL.dtstart, "19960918T143000Z"));
			assertTrue(ev.hasProperty(ICAL.dtstamp, "19960704T120000Z"));
			assertTrue(ev.hasProperty(ICAL.dtend, "19960920T220000Z"));
			assertTrue(ev.hasProperty(ICAL.uid, "uid1@host.com"));
			assertTrue(ev.hasProperty(ICAL.summary, "Networld+Interop Conference"));
			assertTrue(ev.hasProperty(ICAL.description, "Networld+Interop Conference and Exhibit Atlanta World Congress\n  Center Atlanta, Georgia"));
			assertTrue(ev.hasProperty(ICAL.categories, "Conference"));
			assertTrue(ev.hasProperty(ICAL.status, "CONFIRMED"));
			
			assertTrue(ev.hasProperty(ICAL.organizer));
			Resource organizer = ev.getProperty(ICAL.organizer).getResource();
//			dumpModel();
			assertTrue(organizer.hasProperty(ICAL.calAddress,"mailto:jsmith@host.com"));
		}

	}

	
	
	public void testHCalendarWithBudyInfo() {
		assertExtracts("example5.5");
		assertModelNotEmpty();		
		assertContains(thePage, RDF.type, vcal );
		assertContains(null, RDF.type, vjournal );
	}

	
	// uF test suite

	public void test01() {
		assertDefault("01-component-vevent-dtstart-date");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, ICAL.dtstart, "19970903");
	}

	public void test02() {
		assertDefault("02-component-vevent-dtstart-datetime");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, ICAL.dtstart, "19970903T163000Z");
	}

	public void test03() {
		assertDefault("03-component-vevent-dtend-date");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, ICAL.dtstart, "19970903");
		assertContains(event, ICAL.dtend, "19970904");
	}	

	public void test04() {
		assertDefault("04-component-vevent-dtend-datetime");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, ICAL.dtstart, "19970903T160000Z");
		assertContains(event, ICAL.dtend, "19970903T180000Z");
	}		

	public void test05() {
		assertDefault("05-calendar-simple");
		Resource event = getExactlyOneComponent(vevent);
		// todo normalize dates
		assertContains(event, ICAL.dtstart, "2005-10-05");
		assertContains(event, ICAL.dtend, "2005-10-08");
		assertContains(event, ICAL.summary, "Web 2.0 Conference");
		assertContains(event, ICAL.url, model.createResource("http://www.web2con.com/"));
		assertContains(event, ICAL.location, "Argent Hotel, San Francisco, CA");
	}		
	
	public void test06() {
		assertDefault("06-component-vevent-uri-relative");
		Resource event = getExactlyOneComponent(vevent);
		// todo normalize dates
		assertContains(event, ICAL.dtstart, "20060115T000000");
		assertContains(event, ICAL.summary, "Bad Movie Night - Gigli (blame mike spiegelman)");
		assertContains(event, ICAL.url, model.createResource(thePage+"squidlist/calendar/12279/2006/1/15"));
	}

	public void test07() {
		assertDefault("07-component-vevent-description-simple");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, ICAL.description, "Project xyz Review Meeting Minutes");
		assertNotContains(event, ICAL.url, (Resource)null);
	}

	public void test08() {
		assertDefault("08-component-vevent-multiple-classes");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, ICAL.dtstart, "2005-10-05");
		assertContains(event, ICAL.dtend, "2005-10-08");
		assertContains(event, ICAL.summary, "Web 2.0 Conference");
		assertContains(event, ICAL.url, model.createResource("http://www.web2con.com/"));
		assertContains(event, ICAL.location, "Argent Hotel, San Francisco, CA");
	}

	public void test09() {
		assertDefault("09-component-vevent-summary-in-img-alt");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, ICAL.dtend, "20060310");
		assertContains(event, ICAL.dtstart, "20060306");
		assertContains(event, ICAL.summary, "O'Reilly Emerging Technology Conference");
		assertContains(event, ICAL.url, model.createResource("http://conferences.oreillynet.com/et2006/"));
		assertContains(event, ICAL.location, "Manchester Grand Hyatt in San Diego, CA");
	}

	public void test10() {
		assertDefault("10-component-vevent-entity");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, ICAL.summary, "Cricket & Tennis Centre");
		assertContains(event, ICAL.description, "Melbourne's Cricket & Tennis Centres are in the heart of the city");
	}
	
	public void test11() {
		assertDefault("11-component-vevent-summary-in-subelements");
		Resource event = getExactlyOneComponent(vevent);
		
		assertContains(event, ICAL.dtstart, "20051005T1630-0700");
		assertContains(event, ICAL.dtend, "20051005T1645-0700");
		//cleanup spaces TODO
		assertContains(event, ICAL.summary, "Welcome!\n      John Battelle,\n      Tim O'Reilly");
	}

	public void test12() {
		assertDefault("12-component-vevent-summary-url-in-same-class");
		Resource event = getExactlyOneComponent(vevent);	
		assertContains(event, ICAL.dtstart, "20060125T000000");
		assertContains(event, ICAL.url, model.createResource("http://www.laughingsquid.com/squidlist/calendar/12377/2006/1/25"));
		assertContains(event, ICAL.summary, "Art Reception for Tom Schultz and Felix Macnee");
	}

	public void test13() {
		assertDefault("13-component-vevent-summary-url-property");
		Resource event = getExactlyOneComponent(vevent);
		assertContains(event, ICAL.url, model.createResource("http://dps1.travelocity.com/dparcobrand.ctl?smls=Y&Service=YHOE&.intl=us&aln_name=AA&flt_num=1655&dep_arp_name=&arr_arp_name=&dep_dt_dy_1=23&dep_dt_mn_1=Jan&dep_dt_yr_1=2006&dep_tm_1=9:00am"));
		assertContains(event, ICAL.summary, "ORD-SFO/AA 1655");
	}
	
	public void test15() {
		assertDefault("15-calendar-xml-lang");
		Resource event = getExactlyOneComponent(vevent);
		// todo normalize dates
		assertContains(event, ICAL.dtstart, "2005-10-05");
		assertContains(event, ICAL.dtend, "2005-10-08");
		assertContains(event, ICAL.summary, "Web 2.0 Conference");
		assertContains(event, ICAL.url, model.createResource("http://www.web2con.com/"));
		assertContains(event, ICAL.location, "Argent Hotel, San Francisco, CA");
	}
	

	public void test16() {
		assertDefault("16-calendar-force-outlook");
		Resource event = getExactlyOneComponent(vevent);
		// todo normalize dates
		assertContains(event, ICAL.dtstart, "2005-10-05");
		assertContains(event, ICAL.dtend, "2005-10-08");
		assertContains(event, ICAL.location, "Argent Hotel, San Francisco, CA");
	}

	public void test17() {
		assertDefault("17-component-vevent-description-value-in-subelements");
		Resource event = getExactlyOneComponent(vevent);
		// todo normalize dates
		assertContains(event, ICAL.dtstart, "2006-01-18");
		assertContains(event, ICAL.dtend, "2006-01-20");
		assertContains(event, ICAL.location, "Maryland");
		assertContains(event, ICAL.summary, "3rd PAW ftf meeting");
		assertContains(event, ICAL.description,
				"RESOLUTION: to have a\n      3rd PAW ftf meeting \n      18-19 Jan in \n      Maryland; location contingent on confirmation from timbl");
	}

	public void test18() {
		assertDefault("18-component-vevent-uid");
		assertStatementsSize(RDF.type, vevent, 5);
		assertEquals(5, model.listStatements(null, ICAL.uid, "http://example.com/foo.html").toList().size());
	}
	//TODO attachments
	
	private Resource getExactlyOneComponent(Resource r) {
		ResIterator iter = model.listSubjectsWithProperty(RDF.type, r);
		assertTrue(iter.hasNext());
		Resource next = iter.nextResource();
		assertTrue(!iter.hasNext());
		return next;
	}

	
	private void assertDefault(String name) {
		assertExtracts(name);
		assertModelNotEmpty();		
		assertContains(thePage, RDF.type, vcal );
		assertStatementsSize(RDF.type, vcal, 1);
	}

	
	
	
	protected boolean extract(String filename) {
		return new HCalendarExtractor(baseURI, 
				new HTMLFixture("hcalendar/"+filename+".html", true).getHTMLDocument()).extractTo(model);
	}

	public void testNoMicroformats() {
		assertNotExtracts("../html-without-uf");
		assertModelEmpty();
	}

	public void testNoMicroformatsInStatCvsPage() {
		assertNotExtracts("empty-statcvs");
		assertModelEmpty();
	}

	public void testFullHCalendarClass() {
		assertExtracts("example5.3");
		assertModelNotEmpty();
		assertContains(thePage, RDF.type, vcal );
		assertContains(null, RDF.type, vevent );
	}

	public void testHCalendarClassWithTodo() {
		assertExtracts("example5.4");
		assertModelNotEmpty();
		assertContains(thePage, RDF.type, vcal );
		assertContains(null, RDF.type, vtodo );		
	}

	public void testHCalendarClassWithJournal() {
		assertExtracts("example5.5");
		assertModelNotEmpty();
		assertContains(thePage, RDF.type, vcal );
		assertContains(null, RDF.type, vjournal );
	}


	
	
	
}
