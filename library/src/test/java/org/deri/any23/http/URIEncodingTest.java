package org.deri.any23.http;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.httpclient.util.URIUtil;

import junit.framework.TestCase;


public class URIEncodingTest extends TestCase{

	private static Set<String> uris = new HashSet<String>();
	static{
		uris.add("http://www.left4dead411.com/news/2009/01/boomers-day-off/");
		uris.add("http://fredda.2good.nu/blog/?p=52");
			 uris.add("http://digg.com/submit?phase=2&url=http://www.auction-sources-exposed.com/2009/03/how-to-start-making-money-with-photography-using-my-ebaycafepress-guide/&title=How To Start Making Money With Photography Using My eBay/CafePress Guide");
			 uris.add("http://www.deai-saikou.net/web/%8Fo%89%EF%82%A2%20%83%60%83%88%83_%83E%81%5B%83e");
			 
			 
	}
	
	
	public void testEndoding() throws Exception {
		int errors=0;
		for(String uri: uris){
			try{
				System.out.println(new URI(URIUtil.encodeQuery(uri)));
			}catch(Exception e){errors++;}
		}
		
		assertEquals(errors,0);
	}
}
