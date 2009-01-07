package com.google.code.any23.extractors;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.code.any23.HTMLDocument;
import com.google.code.any23.vocab.FOAF;
import com.google.code.any23.vocab.VCARD;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hcard">hCard</a>
 * microformat.
 * 
 * @author Gabriele Renzi
 */
public class HCardExtractor extends EntityBasedMicroformatExtractor {
	public HCardExtractor(URI uri, HTMLDocument doc) {
		super(uri, doc, "vcard");
	}

	private void fixIncludes(HTMLDocument document, Node node) {
		NamedNodeMap attributes = node.getAttributes();
		// header case test 32
		if ("TD".equals(node.getNodeName()) && (null!=attributes.getNamedItem("headers"))) {
			String id =attributes.getNamedItem("headers").getNodeValue();
			Node header = document.findNodeById(id);
			if(null!=header) {
				node.appendChild(header.cloneNode(true)); 
				attributes.removeNamedItem("headers");
			}
		}
		// include pattern, test 31
		
		NodeList includers = document.findAll("//*[contains(@class,'include')]"); 
		for(int i=0;i<includers.getLength();i++) {
			Node current = includers.item(i);
			// we have to remove the field soon to avoid infinite loops
			// no null check, we know it's there or we won't be in the loop
			current.getAttributes().removeNamedItem("class");
			ArrayList<String> res = new ArrayList<String>(1);
			HTMLDocument.readUrlField(res, current);
			String id = res.get(0);
			if (null==id)
				continue;
			id = StringUtils.substringAfter(id, "#");
			Node included = document.findNodeById(id);
			if (null==included) 
				continue;

			
			current.appendChild(included.cloneNode(true)); 
		}
	}

	protected boolean extractEntity(Node node, Model model) {
		fixIncludes(document, node);
		Resource card = getBlankNodeFor(model, node);
		long previous = 	model.size();

		HTMLDocument doc = new HTMLDocument(node);
		addOrganizationName(doc, card);
		addFn(doc,card);
		addSortString(doc,card);
		addNames(doc,card);
		addUrl(doc,card);
		addEmail(doc,card);
		addPhoto(doc,card);
		addLogo(doc,card);
		addUid(doc,card);
		addTimes(doc,card);
		addCategory(doc,card);
		addClass(doc,card);
		addAddresses(doc,card);
		addTelephones(doc,card);
		addTitle(doc,card);
		addRole(doc,card);
		addNote(doc,card);
		addGeo(doc,card);
		
		if (previous == model.size())
			return false;
		card.addProperty(RDF.type, VCARD.VCard);

		fixNames(card);
		addPerson(card);
		//TODO remove single card
		return true;
	}

	private void addGeo(HTMLDocument doc, Resource card) {
		List<Node> nodes = doc.findAllByClassName("geo");
		if (nodes.size()>0)
			card.addProperty(VCARD.geo, getBlankNodeFor(card.getModel(), nodes.get(0)));
	}

	private void addRole(HTMLDocument doc, Resource card) {
		String c = doc.getSingularTextField("role");
		conditionallyAddStringProperty(card, VCARD.role, c);		
	}

	private void addNote(HTMLDocument doc, Resource card) {
		String c = doc.getSingularTextField("note");
		conditionallyAddStringProperty(card, VCARD.note, c);		
	}

	
	private void addTitle(HTMLDocument doc, Resource card) {
		String c = doc.getSingularTextField("title");
		conditionallyAddStringProperty(card, VCARD.title, c);		
	}

	private void addTelephones(HTMLDocument main, Resource card) {
		NodeList nodes = main.findAll(".//*[contains(@class,'tel')]");			
		for(int i=0; i<nodes.getLength();i++) {
			HTMLDocument tel = new HTMLDocument(nodes.item(i));
			String[] values = tel.getPluralUrlField("value");
			if (values.length<1) {
				//no sub values
				String[] typeAndValue = tel.getSingularUrlField("tel").split(":");
				//modem:goo fax:foo tel:bar
				if (typeAndValue.length>1) {
					addTel(card, "tel", typeAndValue[1]);
				}
				else 
					addTel(card, "tel", typeAndValue[0]);
			}
			else { 
				String[] types = tel.getPluralTextField("type");
				for(String typ: types) {
					addTel(card, typ, StringUtils.join(values));
				}
				if (types.length==0)
					addTel(card, "tel", StringUtils.join(values));
			}

		}
	}


	private void addTel(Resource card, String type, String value) {

		value = fixSchema("tel",value);
		Property composed= VCARD.getProperty(type+"Tel");

		if (null!=composed)
			conditionallyAddResourceProperty(card, composed, value);
		else {

			Property simple= VCARD.getProperty(type);
			if (null!=simple)
				conditionallyAddResourceProperty(card, simple, value);
			else
				conditionallyAddResourceProperty(card, VCARD.tel, value);		    
		}
	}


	private void addAddresses(HTMLDocument main, Resource card) {
		List<Node> nodes = main.findAllByClassName("adr");
		for(Node node: nodes)
			card.addProperty(VCARD.adr, getBlankNodeFor(card.getModel(), node));
	}


	private void addClass(HTMLDocument doc, Resource card) {
		String c = doc.getSingularTextField("class");
		conditionallyAddStringProperty(card, VCARD.class_, c);		
	}


	private void addSortString(HTMLDocument doc, Resource card) {
		String ss = doc.getSingularTextField("sort-string");
		conditionallyAddStringProperty(card, VCARD.sort_string, ss);		
	}


	private void addCategory(HTMLDocument doc, Resource card) {
		String[] categories = doc.getPluralTextField("category");
		for(String category: categories) 
			conditionallyAddStringProperty(card, VCARD.category, category);
	}


	private void addTimes(HTMLDocument doc, Resource card) {
		String time = doc.getSingularTextField("bday");
		conditionallyAddStringProperty(card, VCARD.bday, time);
		time = doc.getSingularTextField("rev");
		conditionallyAddStringProperty(card, VCARD.rev, time);
		time = doc.getSingularTextField("tz");
		conditionallyAddStringProperty(card, VCARD.tz, time);
	}


	private void addUid(HTMLDocument doc, Resource card) {
		String uid = doc.getSingularUrlField("uid");
		conditionallyAddStringProperty(card, VCARD.uid, uid);
	}


	//TODO check if tests are checking plurality
	private void addLogo(HTMLDocument doc, Resource card) {
		String[] links = doc.getPluralUrlField("logo");
		for(String link:links)
			conditionallyAddResourceProperty(card, VCARD.logo, absolutizeURI(link));
	}

	private void addPhoto(HTMLDocument doc, Resource card) {
		String[] links = doc.getPluralUrlField("photo");
		for(String link:links)
			conditionallyAddResourceProperty(card, VCARD.photo, absolutizeURI(link));
	}


	private void addEmail(HTMLDocument doc, Resource card) {
		String link = doc.getSingularUrlField("email");
		conditionallyAddResourceProperty(card, VCARD.email, dropSubject(fixSchema("mailto",link)) );
	}

	private String dropSubject(String mail) {
		return mail.split("\\?")[0];
	}
	private static final String[] nameFields  = {"given-name",
												 "family-name",
												 "additional-name",
												 "nickname",
												 "honorific-prefix",
												 "honorific-suffix"};
	private void addNames(HTMLDocument doc, Resource card) {
		Resource name = null;
		String values[];
		
		for(String n: nameFields) {
			values = doc.getPluralTextField(n);
			for(String text: values) {
				if ("".equals(text))
					continue;
				if (null==name) {
					name = card.getModel().createResource();
					name.addProperty(RDF.type, VCARD.Name);
					card.addProperty(VCARD.n, name);
				}
				name.addProperty(VCARD.getProperty(n), text);
			}
		}	
	}


	private void addFn(HTMLDocument doc, Resource card) {
		String name = doc.getSingularTextField("fn");
		conditionallyAddStringProperty(card, VCARD.fn, fixWhiteSpace(name));
	}

	private void addOrganizationName(HTMLDocument doc, Resource card) {
		Node _node = doc.findMicroformattedObjectNode("*", "org");
		if (null==_node)
			return;
		HTMLDocument node = new HTMLDocument(_node);
		String name = node.getSingularTextField("organization-name");
		if ("".equals(name))
			name = doc.getSingularTextField("org");
		if ("".equals(name))
			return;
		Resource org = card.getModel().createResource();
		org.addProperty(RDF.type, VCARD.Organization);
		card.addProperty(VCARD.org, org);
		org.addProperty(VCARD.organization_name, fixWhiteSpace(name));

		String unit = node.getSingularTextField("organization-unit");
		conditionallyAddStringProperty(org, VCARD.organization_unit, unit);
	}
	


	private void addUrl(HTMLDocument doc, Resource card) {
		String[] links = doc.getPluralUrlField("url");
		for(String link:links)
			conditionallyAddResourceProperty(card, VCARD.url, absolutizeURI(link));
	}


	private void addPerson(Resource card) {
		if(card.hasProperty(FOAF.topic))
			return;
		Resource person = card.getModel().createResource();
		addFOAFPropertFromVCard(person, FOAF.name, card, VCARD.fn);
		card.addProperty(FOAF.topic, person);
	}
	
	private void addFOAFPropertFromVCard(Resource person, Property foaf, Resource card, Property prop ) {
		if(card.hasProperty(prop))
			person.addProperty(foaf, card.getProperty(prop).getObject());
	}


	private void fixNames(Resource card) {
		fixFnFromOthers(card);
		fixOthersFromFn(card);
	}

	private String fixWhiteSpace(String name) {
		return name.replaceAll("\\s+", " ").trim();
	}

	private void fixOthersFromFn(Resource card) {
		if (card.hasProperty(VCARD.fn)) {
			String fn = card.getProperty(VCARD.fn).getString();

			// special case: if org and fn are the same the hCard is for an organization, then no n:Name property
			// nested ifs just to avoid catching exceptions, can flatten
			Statement org = card.getProperty(VCARD.org);
			if (null!=org) {
				Statement orgName = org.getResource().getProperty(VCARD.organization_name);
				if (null!=orgName) {
					if(fn.equals(orgName.getString())) {
						return;
					}
				}
			}
			Statement nameProperty = card.getProperty(VCARD.n);
			Resource name;
			if (null==nameProperty) {
				name = card.getModel().createResource();
				name.addProperty(RDF.type, VCARD.Name);
				card.addProperty(VCARD.n, name);
			}
			else
				name = nameProperty.getResource();
			String[] splitFullName= fn.split("\\s+");
			if (!name.hasProperty(VCARD.given_name) && splitFullName.length>0) 
				name.addProperty(VCARD.given_name, splitFullName[0]);
			if (!name.hasProperty(VCARD.family_name) && splitFullName.length>1) 
				name.addProperty(VCARD.family_name, splitFullName[1]);

		}

	}

	private void fixFnFromOthers(Resource vcard) {

		if (!vcard.hasProperty(VCARD.n))
			return;
		Resource name = vcard.getProperty(VCARD.n).getResource();

		if (vcard.hasProperty(VCARD.fn))
			return;
		else {
			// remember order is significant
			Statement[] names = {
					name.getProperty(VCARD.honorific_prefix),
					name.getProperty(VCARD.given_name),
					name.getProperty(VCARD.additional_name),
					name.getProperty(VCARD.family_name),
					name.getProperty(VCARD.honorific_suffix)
			};

			String fn="";
			for(Statement s:names)
				if (null==s)
					continue;
				else
					fn=fn+" "+s.getString().trim();
			vcard.addProperty(VCARD.fn, fixWhiteSpace(fn));
		}
	}



	@Override
	public String getFormatName() {
		return "HCARD";
	}

}
