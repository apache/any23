package org.deri.any23.vocab;

import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * Vocabulary class for <a href="http://gmpg.org/xfn/11">XFN</a>, as per
 * <a href="http://vocab.sindice.com/xfn/guide.html">Expressing XFN in RDF</a>.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class XFN {
	public static final String NS = "http://vocab.sindice.com/xfn#";
	
	private static final Map<String, URI> PeopleXFNProperties = new HashMap<String, URI>();

	private static final Map<String, URI> HyperlinkXFNProperties = new HashMap<String, URI>();

	public static final URI contact = createProperty("contact");
	public static final URI acquaintance = createProperty("acquaintance");
	public static final URI friend = createProperty("friend");
	public static final URI met = createProperty("met");
	public static final URI coWorker = createProperty("co-worker");
	public static final URI colleague = createProperty("colleague");
	public static final URI coResident = createProperty("co-resident");
	public static final URI neighbor = createProperty("neighbor");
	public static final URI child = createProperty("child");
	public static final URI parent = createProperty("parent");
	public static final URI spouse = createProperty("spouse");
	public static final URI kin = createProperty("kin");
	public static final URI muse = createProperty("muse");
	public static final URI crush = createProperty("crush");
	public static final URI date = createProperty("date");
	public static final URI sweetheart = createProperty("sweetheart");
	public static final URI me = createProperty("me");

	public static final URI mePage = ValueFactoryImpl.getInstance().createURI(NS, "mePage");

	private static URI createProperty(String localName) {
		URI result = ValueFactoryImpl.getInstance().createURI(NS + localName + "-hyperlink");
		HyperlinkXFNProperties.put(localName, result);

		result = ValueFactoryImpl.getInstance().createURI(NS, localName);
		PeopleXFNProperties.put(localName, result);
		return result;
	}
	
	public static URI getPropertyByLocalName(String localName) {
		return PeopleXFNProperties.get(localName);
	}
	
	public static URI getExtendedProperty(String localName) {
		return HyperlinkXFNProperties.get(localName);
	}

	public static boolean isXFNLocalName(String localName) {
		return PeopleXFNProperties.containsKey(localName);
	}

	public static boolean isExtendedXFNLocalName(String localName) {
		return PeopleXFNProperties.containsKey(localName);
	}
}
