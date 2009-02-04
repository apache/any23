package com.google.code.any23.extractors;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



import org.deri.any23.extractor.html.HTMLDocument;
import org.deri.any23.vocab.FOAF;
import org.deri.any23.vocab.HLISTING;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Extractor for the <a href="http://microformats.org/wiki/hlisting">hListing</a>
 * microformat.
 * 
 * @author Gabriele Renzi
 */
public class HListingExtractor extends EntityBasedMicroformatExtractor {
	public HListingExtractor(URI baseURI, HTMLDocument doc) {
		super(baseURI, doc, "hlisting");
	}
	

	@Override
	protected boolean extractEntity(Node node, Model model) {
		Resource listing = getBlankNodeFor(model, node);
		HTMLDocument root = new HTMLDocument(node);
		listing.addProperty(RDF.type, HLISTING.Listing);
		
		List<String> actions = findActions(root);
		for (String action: actions) {
			listing.addProperty(HLISTING.action, HLISTING.getResource(action));
		}
		Resource blankLister = addLister(root, listing);
		listing.addProperty(HLISTING.lister, blankLister);
		
		Resource blankItem = addItem(root, listing);
		listing.addProperty(HLISTING.item, blankItem);
			
		addDateTimes(root, listing);
		
		addPrice(root, listing);
		addDescription(root, listing);
		addSummary(root, listing);
		addPermalink(root, listing);
		return true;
	}
	
	private Resource addItem(HTMLDocument root, Resource listing) {	
		Resource blankItem = listing.getModel().createResource();
		Node node = root.findMicroformattedObjectNode("*", "item");
		blankItem.addProperty(RDF.type, HLISTING.Item);

		if (null==node)
			return blankItem;
		HTMLDocument item = new HTMLDocument(node);

		addItemName(item, blankItem);
		addItemUrl(item, blankItem);
		// the format is specified with photo into item, but kelkoo has it into the top level
		addItemPhoto(root, blankItem);
		addItemAddresses(root,blankItem);
		return blankItem;
	}

	private static final List<String> validClassesForAddress = Arrays.asList( new String[] {
		"post-office-box",
		"extended-address",
		"street-address",
		"locality",
		"region",
		"postal-code",
		"country-name"
	});
	
	private void addItemAddresses(HTMLDocument doc, Resource blankItem) {
		NodeList list = doc.findAll(".//*[contains(@class,'adr')]//*[@class]");
		for(int i=0; i < list.getLength();i++) {
			Node node = list.item(i);
			
			String[] klasses = node.getAttributes().getNamedItem("class").getNodeValue().split("\\s+");
			for(String klass: klasses)
				if (validClassesForAddress.contains(klass)) {
					String value = node.getNodeValue();
					// do not use conditionallyAdd, it won't work cause of evaluation rules
					if (!(null==value || "".equals(value))) {
						Property property = HLISTING.getPropertyCamelized(klass);
						blankItem.addProperty(property, value);
					}
				}
		}
	}

	private void addPermalink(HTMLDocument doc, Resource listing) {
		String link = doc.find(".//A[contains(@rel,'self') and contains(@rel,'bookmark')]/@href");
		conditionallyAddStringProperty(listing, HLISTING.permalink, link);
	}
	private void addPrice(HTMLDocument doc, Resource listing) {
		String price = doc.getSingularTextField("price");
		conditionallyAddStringProperty(listing, HLISTING.price, price);
	}

	private void addDescription(HTMLDocument doc, Resource listing) {
		String description = doc.getSingularTextField("description");
		conditionallyAddStringProperty(listing, HLISTING.description, description);
	}


	private void addSummary(HTMLDocument doc, Resource listing) {
		String summary = doc.getSingularTextField("summary");
		conditionallyAddStringProperty(listing, HLISTING.summary, summary);
	}

	
	private void addDateTimes(HTMLDocument doc, Resource listing) {
		String listed = doc.getSingularTextField("dtlisted");
		conditionallyAddStringProperty(listing, HLISTING.dtlisted, listed);
		String expired = doc.getSingularTextField("dtexpired");
		conditionallyAddStringProperty(listing, HLISTING.dtexpired, expired );
	}

// TODO cleanup
	private Resource addLister(HTMLDocument doc, Resource listing) {
		Resource blankLister = listing.getModel().createResource();
		blankLister.addProperty(RDF.type, HLISTING.Lister);
		Node node = doc.findMicroformattedObjectNode("*", "lister");
		if (null==node)
			return blankLister;
		HTMLDocument listerNode = new HTMLDocument(node);
//	    addVCard(doc,blankLister);	
	    addListerFn(listerNode, blankLister);
	    addListerOrg(listerNode, blankLister);
	    addListerEmail(listerNode, blankLister);
	    addListerUrl(listerNode, blankLister);
	    addListerTel(listerNode, blankLister);
	    addListerLogo(listerNode, blankLister);
	    return blankLister;
	}

	
	private void addListerTel(HTMLDocument doc, Resource blankLister) {
		String tel = doc.getSingularTextField("tel");
		conditionallyAddStringProperty(blankLister, HLISTING.tel, tel);
	}
	

	private void addListerUrl(HTMLDocument doc, Resource blankLister) {
		String url = doc.getSingularUrlField("url");
		conditionallyAddStringProperty(blankLister, HLISTING.listerUrl, absolutizeURI(url));
	}

	private void addListerEmail(HTMLDocument doc, Resource blankLister) {
		String email = doc.getSingularUrlField("email");
		conditionallyAddStringProperty(blankLister, FOAF.mbox, fixSchema("mailto",email));
	}
	

	// TODO: actually has complex and stupid rules, microformats suck
	private void addListerFn(HTMLDocument doc, Resource blankLister) {
		String fn = doc.getSingularTextField("fn");
		conditionallyAddStringProperty(blankLister, HLISTING.listerName, fn);
	}

	private void addListerLogo(HTMLDocument doc, Resource blankLister) {
		String logo = doc.getSingularUrlField("logo");
		conditionallyAddStringProperty(blankLister, HLISTING.listerLogo, absolutizeURI(logo));
	}
	
	
	private void addListerOrg(HTMLDocument doc, Resource blankLister) {
		String org = doc.getSingularTextField("org");
		conditionallyAddStringProperty(blankLister, HLISTING.listerOrg, org);
	}
	
	
	// TODO: actually has complex and stupid rules, microformats suck
	private void addItemName(HTMLDocument item, Resource blankItem) {
		String fn = item.getSingularTextField("fn");
		conditionallyAddStringProperty(blankItem, HLISTING.itemName, fn);
	}

	private void addItemUrl(HTMLDocument item, Resource blankItem) {
		String url = item.getSingularUrlField("url");
		conditionallyAddStringProperty(blankItem, HLISTING.itemUrl, absolutizeURI(url));
	}
	private void addItemPhoto(HTMLDocument doc, Resource blankLister) {
		// as per spec
		String url = doc.findMicroformattedValue("*","item", "A", "photo","@href");
		conditionallyAddStringProperty(blankLister, HLISTING.itemPhoto, absolutizeURI(url));
		url = doc.findMicroformattedValue("*", "item", "IMG", "photo", "@src");
		conditionallyAddStringProperty(blankLister, HLISTING.itemPhoto, absolutizeURI(url));
		// as per kelkoo. Remember that contains(foo,'') is true in xpath
		url = doc.findMicroformattedValue("*", "photo", "IMG", "", "@src");
		conditionallyAddStringProperty(blankLister, HLISTING.itemPhoto, absolutizeURI(url));
	}

	
	// TODO: use sorted set
	private static final List<String> ActionClasses = Arrays.asList("sell", "rent" , "trade", "meet", "announce", "offer", "wanted", "event", "service");
	
	private List<String> findActions(HTMLDocument doc) {
		
		List<String> actions = new ArrayList<String>(0);
		// first check if values are inlined
		String[] classes = doc.readAttribute("class").split("\\s+");
		for (String klass: classes) {
			if (ActionClasses.contains(klass)) 
				actions.add(klass);
		}

		NodeList nodes = doc.findAll("./*[@class]/@class");
		for(int i=0; i < nodes.getLength(); i++){	
			String action = nodes.item(i).getNodeValue();
			for (String substring: action.split("\\s+")) {
				if (ActionClasses.contains(substring)) 
					actions.add(substring);
			}
		}
		return actions;
	}


	@Override
	public String getFormatName() {
		return "HLISTING";
	}
	
}
