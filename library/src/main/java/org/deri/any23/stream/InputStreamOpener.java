package org.deri.any23.stream;

import java.io.IOException;
import java.io.InputStream;

/**
 * A source of input streams. Mostly intended for
 * situations where opening of an input stream is
 * to be delayed.
 * 
 * TODO Should have a method canOpenMultipleTimes?
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public interface InputStreamOpener {
	InputStream openInputStream() throws IOException;
	public long getContentLength();
	
	// TODO This method is needed to deal with HTTP redirects, but really shouldn't be here. Redesign! 
	public String getDocumentURI();
}
