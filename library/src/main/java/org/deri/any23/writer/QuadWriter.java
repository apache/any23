package org.deri.any23.writer;

import java.io.IOException;
import java.io.OutputStream;

import org.deri.any23.extractor.ExtractionContext;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.ntriples.NTriplesUtil;

/**
 * A triple handler that converts triples to quads by using the
 * document URI of each triple's context as the graph name.
 * Optionally, a metadata graph can be specified; for each
 * document URI, it will record which extractors were used on
 * it, and the document title if any.
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class QuadWriter implements TripleHandler {
	

private OutputStream _out;
	public QuadWriter(OutputStream out) {
		_out= out;
	}
	
	

	public void startDocument(URI documentURI) {
		// ignore
	}
	
	public void openContext(ExtractionContext context) {
		;
	}
	
	public void closeContext(ExtractionContext context) {
		// do nothing
	}

	public void receiveTriple(Resource s, URI p, Value o, ExtractionContext context) {
		try{
			StringBuffer sb = new StringBuffer();
			sb.append(NTriplesUtil.toNTriplesString(s)).append(" ");
			sb.append(NTriplesUtil.toNTriplesString(p)).append(" ");
			sb.append(NTriplesUtil.toNTriplesString(o)).append(" ");
			sb.append(NTriplesUtil.toNTriplesString(context.getDocumentURI())).append(" .\n");
			_out.write(sb.toString().getBytes());
			_out.flush();
		}catch(IOException ioe){
			;
		}
	}
	
	public void receiveNamespace(String prefix, String uri, ExtractionContext context) {
		// ignore prefix mappings
	}
	
	public void close() {
		try {
			_out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void endDocument(URI documentURI) {
		;
	}
	
	@Override
	public void setContentLength(long contentLength) {
//		_contentLength = contentLength;
		//ignore
		;
	}
}
