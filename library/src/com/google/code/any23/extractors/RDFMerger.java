package com.google.code.any23.extractors;

import java.net.URI;
import java.util.List;


import com.google.code.any23.HTMLDocument;
import com.google.code.any23.vocab.FOAF;
import com.google.code.any23.vocab.VCARD;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;


public class RDFMerger extends MicroformatExtractor {

	public RDFMerger(URI baseURI, HTMLDocument document) {
		super(baseURI, document);
	}

	@Override
	public boolean extractTo(Model model) {
		mixVCardAndXFN(model);
		return true;
	}

	@SuppressWarnings("unchecked")
	private List<Resource> getListFromModel(Model model, Property p, RDFNode r) {
		ResIterator iter = model.listSubjectsWithProperty(p, r);
		List<?> list = iter.toList();
		iter.close();
		return (List<Resource>) list;
	}
	private void mixVCardAndXFN(Model model) {
		List<Resource>	list = getListFromModel(model, RDF.type, VCARD.VCard);
		if (list.size()!=1) 
			// no representative hCard
			return;
		Resource card = (Resource) list.get(0);
		list = getListFromModel(model, FOAF.isPrimaryTopicOf, model.createResource(baseURI.toString()));
		if (list.size()!=1) 
			// no representative XFN
			return;		
		Resource person = list.get(0);
		person.addProperty(OWL.sameAs, card.getRequiredProperty(FOAF.topic).getResource());
		
	}


	@Override
	public String getFormatName() {
		return "";
	}

}
