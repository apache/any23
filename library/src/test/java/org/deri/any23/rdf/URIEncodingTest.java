package org.deri.any23.rdf;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.openrdf.model.impl.ValueFactoryImpl;


public class URIEncodingTest extends TestCase{

	private Any23ValueFactoryWrapper factory;


	@Override
	protected void setUp() throws Exception {
		
		super.setUp();
		factory = new Any23ValueFactoryWrapper(ValueFactoryImpl.getInstance());
	}
	
	private static Set<String> uris = new HashSet<String>();
	static{
		uris.add("http://www.left4dead411.com/news/2009/01/boomers-day-off/");
		uris.add("http://fredda.2good.nu/blog/?p=52");
		uris.add("http://digg.com/submit?phase=2&url=http://www.auction-sources-exposed.com/2009/03/how-to-start-making-money-with-photography-using-my-ebaycafepress-guide/&title=How To Start Making Money With Photography Using My eBay/CafePress Guide");
		uris.add("http://www.deai-saikou.net/web/%8Fo%89%EF%82%A2%20%83%60%83%88%83_%83E%81%5B%83e");
			 
			 
	}
		
	public void testEndoding() throws Exception {
		for(String uri: uris){
			try{
				System.out.println(factory.createURI(uri));
			}catch(Exception e){e.printStackTrace();}
		}
	}
	
	public void testNoProtocol() throws Exception {
//		System.out.println(factory.createURI("/index.php/Special:ExportRDF/Main Page"));
//		assertNull(factory.createURI("/index.php/Special:ExportRDF/Main Page"));
		
		System.out.println(new String("/index.php/Special:ExportRDF/Main Page").matches("^[a-zA-Z0-9]+:(//)"));
	}
	
	public void testNoAuthority() throws Exception {
		try {
			factory.createURI("http://");
			fail();
		} catch (IllegalArgumentException ex) {
			// expected
		}
	}
	
}
