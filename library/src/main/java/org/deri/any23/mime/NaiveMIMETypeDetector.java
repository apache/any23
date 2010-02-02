package org.deri.any23.mime;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic implementation of {@link org.deri.any23.mime.MIMETypeDetector} based
 * on file extensions.
 */
public class NaiveMIMETypeDetector implements MIMETypeDetector {

    private final static Map<String, String> extensions = new HashMap<String, String>() {
        {
            // extension -> mime type
            put("html" , "text/html"            );
            put("htm"  , "text/html"            );
            put("xhtml", "application/xhtml+xml");
            put("xht"  , "application/xhtml+xml");
            put("rdf"  , "application/rdf+xml"  );
            put("xrdf" , "application/rdf+xml"  );
            put("rdfx" , "application/rdf+xml"  );
            put("owl"  , "application/rdf+xml"  );
            put("nt"   , "text/plain"           );
            put("txt"  , "text/plain"           );
            put("ttl"  , "application/x-turtle" );
            put("n3"   , "text/rdf+n3"          );
        }
    };

    private final static Pattern extensionRegex = Pattern.compile(".*\\.([a-z0-9]+)");

    public MIMEType guessMIMEType(
            String fileName,
            InputStream input,

            MIMEType mimeTypeFromMetadata
    ) {
        if (mimeTypeFromMetadata != null) {
            return mimeTypeFromMetadata;
        }
        String extension = getExtension(fileName);
        if (extension == null) {
            // Assume index file on web server.
            extension = "html";
        }
        if (extensions.containsKey(extension)) {
            return MIMEType.parse(extensions.get(extension));
        }
        return null;
    }

    public int requiredBufferSize() {
        return 0;
    }

    private String getExtension(String filename) {
        Matcher m = extensionRegex.matcher(filename);
        if (!m.matches()) return null;
        return m.group(1);
    }
    
}
