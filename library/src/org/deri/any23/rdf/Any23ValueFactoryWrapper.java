package org.deri.any23.rdf;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.XMLGregorianCalendar;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;

public class Any23ValueFactoryWrapper implements ValueFactory{

	private static final Logger logger = Logger.getLogger(Any23ValueFactoryWrapper.class.getName());
	private final ValueFactory _vFactory;

	public Any23ValueFactoryWrapper(final ValueFactory vFactory) {
		_vFactory = vFactory;

	}

	@Override
	public BNode createBNode() {
		return _vFactory.createBNode();
	}

	@Override
	public BNode createBNode(String arg0) {
		return _vFactory.createBNode(arg0);
	}

	@Override
	public Literal createLiteral(String arg0) {
		return _vFactory.createLiteral(arg0);
	}

	@Override
	public Literal createLiteral(boolean arg0) {
		return _vFactory.createLiteral(arg0);
	}

	@Override
	public Literal createLiteral(byte arg0) {
		return _vFactory.createLiteral(arg0);
	}

	@Override
	public Literal createLiteral(short arg0) {
		return _vFactory.createLiteral(arg0);
	}

	@Override
	public Literal createLiteral(int arg0) {
		return _vFactory.createLiteral(arg0);
	}

	@Override
	public Literal createLiteral(long arg0) {
		return _vFactory.createLiteral(arg0);
	}

	@Override
	public Literal createLiteral(float arg0) {
		return _vFactory.createLiteral(arg0);
	}

	@Override
	public Literal createLiteral(double arg0) {
		return _vFactory.createLiteral(arg0);
	}

	@Override
	public Literal createLiteral(XMLGregorianCalendar arg0) {
		return _vFactory.createLiteral(arg0);
	}

	@Override
	public Literal createLiteral(String arg0, String arg1) {
		return _vFactory.createLiteral(arg0,arg1);
	}

	@Override
	public Literal createLiteral(String arg0, URI arg1) {
		return _vFactory.createLiteral(arg0,arg1);
	}

	@Override
	public Statement createStatement(Resource arg0, URI arg1, Value arg2) {
		return _vFactory.createStatement(arg0,arg1,arg2);
	}

	@Override
	public Statement createStatement(Resource arg0, URI arg1, Value arg2,
			Resource arg3) {
		return _vFactory.createStatement(arg0,arg1,arg2,arg3);
	}

	
	

	@Override
	/**
	 * @param arg0
	 * @return a valid sesame URI or null if any exception occured
	 */
	public URI createURI(String arg0) {
		try{
			return _vFactory.createURI(escapeURI(arg0));
		}catch(Exception e){
			logger.log(Level.WARNING,e.getMessage());
			return null;
		}
	}
	
	/**
	 * 
	 * @return a valid sesame URI or null if any exception occured
	 */
	public URI createURI(String arg0, String arg1) {
		try{
			return _vFactory.createURI(escapeURI(arg0),arg1);
		}catch(Exception e){
			logger.log(Level.WARNING,e.getMessage());
			return null;
		}
	}

	/**
	 *These appear to be good rules:
	 *
	 *		Remove whitespace or '\' or '"' in beginning and end
	 *		Replace space with %20
	 *		Drop the triple if it matches this regex (only protocol): ^[a-zA-Z0-9]+:(//)?$
	 *		Drop the triple if it matches this regex: ^javascript:
	 *		Truncate ">.*$ from end of lines (Neko didn't quite manage to fix broken markup)
	 *		Drop the triple if any of these appear in the URL: <>[]|*{}"<>\
	 */
	private String escapeURI(String unescapedURI) {
		//	Remove starting and ending whitespace  
		String escapedURI = unescapedURI.trim();
		
		//Replace space with %20
		escapedURI = escapedURI.replaceAll(" ", "%20");
		
		//strip linebreaks 
		escapedURI = escapedURI.replaceAll("\n", "");
		
		//'Remove starting  "\" or '"'  
		if(escapedURI.startsWith("\\") || escapedURI.startsWith("\"")) escapedURI = escapedURI.substring(1);
		//Remove  ending   "\" or '"'
		if(escapedURI.endsWith("\\") || escapedURI.endsWith("\"")) escapedURI = escapedURI.substring(0,escapedURI.length()-1);
		
		//Drop the triple if it matches this regex (only protocol): ^[a-zA-Z0-9]+:(//)?$
		if(escapedURI.matches("^[a-zA-Z0-9]+:(//)?$")) throw new IllegalArgumentException("no authority in URI");
		
		//Drop the triple if it matches this regex: ^javascript:
		if(escapedURI.matches("^javascript:")) throw new IllegalArgumentException("URI starts with javascript");
		
		
		if(escapedURI.matches("^[a-zA-Z0-9]+:(//)")) throw new IllegalArgumentException("no scheme in URI");
				
//		//stripHTML
//		escapedURI = escapedURI.replaceAll("\\<.*?\\>", "");
		
		//>.*$ from end of lines (Neko didn't quite manage to fix broken markup)
		escapedURI = escapedURI.replaceAll(">.*$", "");
		
		//Drop the triple if any of these appear in the URL: <>[]|*{}"<>\
		if(escapedURI.matches("<>\\[\\]|\\*\\{\\}\"\\\\")) throw new IllegalArgumentException("Invalid character in URI");
		
		return escapedURI;
	}
}
