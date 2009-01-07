package com.google.code.any23.vocab;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * Vocabulary class for <a href="http://gmpg.org/xfn/11">XFN</a>, as per
 * <a href="http://esw.w3.org/topic/CustomRdfDialects">ESW page</a>.
 * 
 * @author Richard Cyganiak (richard at cyganiak dot de)
 */
public class XFN {
	public static final String NS = "http://gmpg.org/xfn/11#";
	
	private static final Map<String, Property> XFNProperties = new HashMap<String, Property>();

	private static final Map<String, Property> ExtendedXFNProperties = new HashMap<String, Property>();

	public static final Property test = createProperty("test");
	public static final Property contact = createProperty("contact");
	public static final Property acquaintance = createProperty("acquaintance");
	public static final Property friend = createProperty("friend");
	public static final Property met = createProperty("met");
	public static final Property coWorker = createProperty("co-worker");
	public static final Property colleague = createProperty("colleague");
	public static final Property coResident = createProperty("co-resident");
	public static final Property neighbor = createProperty("neighbor");
	public static final Property child = createProperty("child");
	public static final Property parent = createProperty("parent");
	public static final Property spouse = createProperty("spouse");
	public static final Property kin = createProperty("kin");
	public static final Property muse = createProperty("muse");
	public static final Property crush = createProperty("crush");
	public static final Property date = createProperty("date");
	public static final Property sweetheart = createProperty("sweetheart");
	public static final Property me = createProperty("me");

	public static final String ExtendedNS = "http://sindice.com/exfn/0.1/";

	private static Property createProperty(String localName) {
		Property result = ResourceFactory.createProperty(ExtendedNS, localName);
		ExtendedXFNProperties.put(localName, result);

		result = ResourceFactory.createProperty(NS, localName);
		XFNProperties.put(localName, result);
		return result;
	}
	
	public static Property getPropertyByLocalName(String localName) {
		return XFNProperties.get(localName);
	}
	
	public static Property getExtendedProperty(String localName) {
		return ExtendedXFNProperties.get(localName);
	}

	
	public static boolean isXFNLocalName(String localName) {
		return XFNProperties.containsKey(localName);
	}

	public static boolean isExtendedXFNLocalName(String localName) {
		return XFNProperties.containsKey(localName);
	}

	
}
