package org.deri.any23.vocab;

import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;

public class HLISTING {
	private static final String NS = "http://sindice.com/hlisting/0.1/";
	
	private static final Map<String, URI> resourceMap = new HashMap<String, URI>();
	private static final Map<String, URI> propertyMap = new HashMap<String, URI>();

    public static final URI action = createProperty("action");
    public static final URI lister = createProperty("lister"); // ranges over Lister
    public static final URI item = createProperty("item"); 

    // my classes
    public static final URI Listing = createResource("Listing");
    public static final URI Lister  = createResource("Lister"); // isa FOAF.Person
    public static final URI Item  = createResource("Item"); // isa ?
    
    public static final URI sell = createResource("sell");
    public static final URI rent = createResource("rent" );
    public static final URI trade = createResource("trade" );
    public static final URI meet = createResource("meet" );
    public static final URI announce = createResource("announce" );
    public static final URI offer = createResource("offer" );
    public static final URI wanted = createResource("wanted" );
    public static final URI event = createResource("event" );
    public static final URI service = createResource("service" );
  
    // TODO: use vcard NS
    public static final URI tel = createProperty("tel");
	public static final URI dtlisted = createProperty("dtlisted");
	public static final URI dtexpired = createProperty("dtexpired");
	public static final URI price = createProperty("price");
	
	// TODO: use DC 
	public static final URI description = createProperty("description");
	public static final URI summary = createProperty("summary");
	public static final URI permalink = createProperty("permalink");

	// TODO: use adr
	public static final URI region = createProperty("region");
	public static final URI postOfficeBox = createProperty("postOfficeBox");
	public static final URI locality = createProperty("locality");
	public static final URI extendedAddress = createProperty("extendedAddress");
	public static final URI streetAddress = createProperty("streetAddress");
	public static final URI postalCode = createProperty("postalCode");
	public static final URI countryName = createProperty("countryName");

    // TODO: subPropertyOf foaf.homepage, domain Lister
    // should handle mbox homepage, name etc 
    public static final	URI listerUrl = createProperty("listerUrl");
    public static final	URI listerName = createProperty("listerName");

    public static final URI itemName = createProperty("itemName"); // over Item
	public static final URI itemUrl = createProperty("itemUrl");
	public static final URI itemPhoto = createProperty("itemPhoto");
	public static final URI listerOrg= createProperty("listerOrg");
	public static final URI listerLogo = createProperty("listerLogo");
	
    private static URI createProperty(String localName) {
		URI result = ValueFactoryImpl.getInstance().createURI(NS, localName);
		propertyMap.put(localName, result);
		return result;
	}

    private static URI createResource(String localName) {
		URI result = ValueFactoryImpl.getInstance().createURI(NS, localName);
		resourceMap.put(localName, result);
		return result;
	}
    
	public static URI getResource(String localName) {
		return resourceMap.get(localName);
	}

	public static URI getPropertyCamelized(String klass) {
		String[] names = klass.split("\\W");
		String result = names[0];
		for(int i=1; i < names.length; i++) {
			String tmp = names[i];
			result+=tmp.replaceFirst("(.)",tmp.substring(0,1).toUpperCase());
		}
		if (!propertyMap.containsKey(result))
			throw new RuntimeException("fool, using a non existign URI!");
		return propertyMap.get(result);
	}
}
