package org.deri.any23.mime.purifier;

import java.io.IOException;
import java.io.InputStream;

/**
 * This interface defines a minimum set of methods that
 * a {@link org.deri.any23.mime.TikaMIMETypeDetector} could
 * call in order to clean the input before performing the <i>MIME type</i>
 * detection.
 * 
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public interface Purifier {

    /**
     * Performs the purification of the provided resettable {@link java.io.InputStream}.
     * 
     * @param inputStream a resettable {@link java.io.InputStream} to be cleaned.
     */
    void purify(InputStream inputStream) throws IOException;

}
