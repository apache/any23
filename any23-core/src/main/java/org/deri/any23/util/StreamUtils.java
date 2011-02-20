package org.deri.any23.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Contains general utility functions for handling streams.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class StreamUtils {

    private StreamUtils(){}

    /**
     * Returns the string content of a stream.
     *
     * @param is input stream.
     * @return the string content.
     * @throws IOException if an error occurs while consuming the <code>is</code> stream.
     */
    public static String asString(InputStream is) throws IOException {
        if (is == null) {
            throw new NullPointerException("input stream is null.");
        }
        final BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            final StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
            return content.toString();
        } finally {
            br.close();
        }
    }

}
