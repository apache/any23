package org.deri.any23.vocab;

import java.util.HashMap;
import java.util.Map;


import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class HLISTING {
	private static final String NS = "http://sindice.com/hlisting/0.1/";
	private static final Map<String, Resource> resourceMap = new HashMap<String, Resource>();
	private static final Map<String, Property> propertyMap = new HashMap<String, Property>();

    public static final Property action = createProperty("action");
    public static final Property lister = createProperty("lister"); // ranges over Lister
    public static final Property item = createProperty("item"); 

    // my classes
    public static final Resource Listing = createResource("Listing");
    public static final Resource Lister  = createResource("Lister"); // isa FOAF.Person
    public static final Resource Item  = createResource("Item"); // isa ?
    
    
    public static final Resource sell = createResource("sell");
    public static final Resource rent = createResource("rent" );
    public static final Resource trade = createResource("trade" );
    public static final Resource meet = createResource("meet" );
    public static final Resource announce = createResource("announce" );
    public static final Resource offer = createResource("offer" );
    public static final Resource wanted = createResource("wanted" );
    public static final Resource event = createResource("event" );
    public static final Resource service = createResource("service" );
  
    // TODO: use vcard NS
    public static final Property tel = createProperty("tel");
	public static final Property dtlisted = createProperty("dtlisted");
	public static final Property dtexpired = createProperty("dtexpired");
	public static final Property price = createProperty("price");
	
	
	// TODO: use DC 
	public static final Property description = createProperty("description");
	public static final Property summary = createProperty("summary");
	public static final Property permalink = createProperty("permalink");
	  
	
	// TODO: use adr
	public static final Property region = createProperty("region");
	public static final Property postOfficeBox = createProperty("postOfficeBox");
	public static final Property locality = createProperty("locality");
	public static final Property extendedAddress = createProperty("extendedAddress");
	public static final Property streetAddress = createProperty("streetAddress");
	public static final Property postalCode = createProperty("postalCode");
	public static final Property countryName = createProperty("countryName");

	
    // TODO: subPropertyOf foaf.homepage, domain Lister
    // should handle mbox homepage, name etc 
    public static final	Property listerUrl = createProperty("listerUrl");
    public static final	Property listerName = createProperty("listerName");

    public static final Property itemName = createProperty("itemName"); // over Item
	public static final Property itemUrl = createProperty("itemUrl");
	public static final Property itemPhoto = createProperty("itemPhoto");
	public static final Property listerOrg= createProperty("listerOrg");
	public static final Property listerLogo = createProperty("listerLogo");
    

	
	
    private static Property createProperty(String localName) {
		Property result = ResourceFactory.createProperty(NS, localName);
		propertyMap.put(localName, result);
		return result;
	}

    private static Resource createResource(String localName) {
		Resource result = ResourceFactory.createResource(NS+localName);
		resourceMap.put(localName, result);
		return result;
	}	
	public static Resource getResource(String localName) {
		return resourceMap.get(localName);
	}

	public static Property getPropertyCamelized(String klass) {
		String[] names = klass.split("\\W");
		String result = names[0];
		for(int i=1; i < names.length; i++) {
			String tmp = names[i];
			result+=tmp.replaceFirst("(.)",tmp.substring(0,1).toUpperCase());
		}
		if (!propertyMap.containsKey(result))
			throw new RuntimeException("fool, using a non existign property!");
		return propertyMap.get(result);
	}
	
}
