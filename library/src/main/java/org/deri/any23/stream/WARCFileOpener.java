package org.deri.any23.stream;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpParser;
import org.archive.io.ArchiveRecord;

public class WARCFileOpener implements InputStreamOpener {
	private ArchiveRecord	_archiveStream;
	
	/**
	 * @param rec
	 */
	public WARCFileOpener(ArchiveRecord rec) {
		_archiveStream = rec;
	}

	public InputStream openInputStream() throws IOException {
		skipHeader(_archiveStream);
		return _archiveStream;
	}
	
	/**  this code is need to read the http header in warc entries (e.g. heritrix or multicrawler) **/
	private int getEolCharsCount(byte [] bytes) {
        int count = 0;
        if (bytes != null && bytes.length >=1 &&
                bytes[bytes.length - 1] == '\n') {
            count++;
            if (bytes.length >=2 && bytes[bytes.length -2] == '\r') {
                count++;
            }
        }
        return count;
    }
    
    private void skipHeader (InputStream in) throws IOException{
        // Now read the header lines looking for the separation
        // between header and body.
//        StringBuffer buf = new StringBuffer();
        int eolCharCount=0;
        for (byte [] lineBytes = null; true;) {
            lineBytes = HttpParser.readRawLine(in);

            eolCharCount = getEolCharsCount(lineBytes);
            if (eolCharCount <= 0) {
                return;
            }
            if ((lineBytes.length - eolCharCount) <= 0) {
                // We've finished reading the http header.
                break;
            }
        }
        return;
    }
}