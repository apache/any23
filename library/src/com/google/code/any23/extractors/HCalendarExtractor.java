package com.google.code.any23.extractors;

import java.net.URI;
import java.util.List;

import org.w3c.dom.Node;

import com.google.code.any23.DomUtils;
import com.google.code.any23.HTMLDocument;
import com.google.code.any23.vocab.ICAL;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class HCalendarExtractor extends MicroformatExtractor {

	public HCalendarExtractor(URI baseURI, HTMLDocument document) {
		super(baseURI, document);
	}

	public static void main(String args[]) {
		doExtraction(new HCalendarExtractor(URI.create("http://bbc.con"),getDocumentFromArgs(args)));
	}
	
	@Override
	public boolean extractTo(Model model) {
		List<Node> calendars = document.findAllByClassName("vcalendar");
		if (calendars.size() == 0)
			// vcal allows to avoid top name, in which case whole document is
			// the calendar, let's try
			if (document.findAllByClassName("vevent").size() > 0)
				calendars.add(document.getDocument());

		boolean foundAny = false;
		for (Node node : calendars)
			foundAny |= extract(node, model);

		return foundAny;
	}

	private boolean extract(Node node, Model model) {
		Resource cal = model.createResource(baseURI.toString(), ICAL.Vcalendar);
		return addComponents(node, cal);
	}

	private static final String[] Components = { "Vevent", "Vtodo", "Vjournal",
			"Vfreebusy" };

	private boolean addComponents(Node node, Resource cal) {

		boolean foundAny = false;

		for (String component : Components) {
			List<Node> events = DomUtils.findAllByClassName(node, component);
			if (events.size() == 0)
				continue;
			for (Node evtNode : events)
				foundAny |= extractComponent(evtNode, cal, component);
		}
		return foundAny;

	}

	private boolean extractComponent(Node node, Resource cal, String component) {
		HTMLDocument compoNode = new HTMLDocument(node);
		Resource evt = cal.getModel().createResource(
				ICAL.getResource(component));
		addTextProps(compoNode, evt);
		addUrl(compoNode, evt);
		addRRule(compoNode, evt);
		addOrganizer(compoNode, evt);
		addUid(compoNode,evt);
		cal.addProperty(ICAL.component, evt);
		return true;
	}

	private void addUid(HTMLDocument compoNode, Resource evt) {
		String url = compoNode.getSingularUrlField("uid");
		conditionallyAddStringProperty(evt, ICAL.uid, url);
	}

	private void addUrl(HTMLDocument compoNode, Resource evt) {
		String url = compoNode.getSingularUrlField("url");
		if(!url.equals(""))
			evt.addProperty(ICAL.url, evt.getModel().createResource(absolutizeURI(url)));
	}

	private void addRRule(HTMLDocument compoNode, Resource evt) {
		// TODO Auto-generated method stub
		for (Node rule : compoNode.findAllByClassName("rrule")) {
			Resource rrule = evt.getModel().createResource(ICAL.DomainOf_rrule);
			String freq = new HTMLDocument(rule).getSingularTextField("freq");
			conditionallyAddStringProperty(rrule, ICAL.freq, freq);
			evt.addProperty(ICAL.rrule, rrule);
		}
	}

	
	private void addOrganizer(HTMLDocument compoNode, Resource evt) {
		// TODO Auto-generated method stub
		for (Node organizer : compoNode.findAllByClassName("organizer")) {
			//untyped
			Resource blank = evt.getModel().createResource();
			String mail = new HTMLDocument(organizer).getSingularUrlField("organizer");
			conditionallyAddStringProperty(blank, ICAL.calAddress, mail);
			evt.addProperty(ICAL.organizer, blank);
		}
	}
	
	private String[] textSingularProps = { "dtstart", "dtstamp", "dtend", "summary", "class", "transp", "description", "status","location" };

	private void addTextProps(HTMLDocument node, Resource evt) {

		for (String date : textSingularProps) {
			String val = node.getSingularTextField(date);
			conditionallyAddStringProperty(evt, ICAL.getProperty(date), val);
		}
		String[] values = node.getPluralTextField("category");
		for (String val : values) {
			conditionallyAddStringProperty(evt, ICAL.categories, val);
		}
	}


	@Override
	public String getFormatName() {
		return "HCALENDAR";
	}

}
