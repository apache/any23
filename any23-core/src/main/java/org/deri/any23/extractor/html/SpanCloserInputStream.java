package org.deri.any23.extractor.html;

import java.io.IOException;
import java.io.InputStream;

/**
 * Extension of {@link java.io.InputStream} meant to
 * detect and replace any occurrence of inline <i>span</i>:
 * <pre>
 * &lt;span/&gt;
 * </pre>
 * with an open close tag sequence:
 * <pre>
 * &lt;span&gt;&lt;/span&gt;
 * </pre>
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class SpanCloserInputStream extends InputStream {

    private static final String TRAILING_SEQUENCE_OPEN  = "<span";
    private static final char   TRAILING_SEQUENCE_CLOSE = '>';
    private static final String CLOSE_SEQUENCE          = "</span>";

    private final InputStream wrapped;

    private int trailingSequenceOpenMatch  = 0;
    private int closeSequenceIndex = 0;
    private boolean trailingSequenceOpenDetected  = false;
    private boolean trailingSequenceCloseDetected = false;
    private boolean inlineDetected = false;
    private boolean betweenQuotes = false;

    public SpanCloserInputStream(InputStream is) {
        wrapped = is;
    }

    @Override
    public int read() throws IOException {
        if(trailingSequenceOpenDetected && inlineDetected && trailingSequenceCloseDetected) {
            final int ret = CLOSE_SEQUENCE.charAt(closeSequenceIndex);
            closeSequenceIndex++;
            if(closeSequenceIndex >= CLOSE_SEQUENCE.length()) {
                resetDetector();
            }
            return ret;
        } else if(trailingSequenceOpenDetected && trailingSequenceCloseDetected) {
            resetDetector();
        }

        int c = wrapped.read();
        if(c == '"') {
            betweenQuotes = !betweenQuotes;
        } else if(c == '/' && !betweenQuotes && trailingSequenceOpenDetected && !trailingSequenceCloseDetected) {
            inlineDetected = true;
            c = wrapped.read();
        }

        if( !trailingSequenceOpenDetected && checkOpenTrailingSequence(c) ) {
           trailingSequenceOpenDetected = true;
            trailingSequenceCloseDetected = false;
        } else if(c == TRAILING_SEQUENCE_CLOSE && trailingSequenceOpenDetected) {
            trailingSequenceCloseDetected = true;
        }
        return c;
    }

    private boolean checkOpenTrailingSequence(int c) {
        if( TRAILING_SEQUENCE_OPEN.charAt(trailingSequenceOpenMatch) == Character.toLowerCase(c) ) {
            trailingSequenceOpenMatch++;
            if(trailingSequenceOpenMatch == TRAILING_SEQUENCE_OPEN.length()) {
                trailingSequenceOpenMatch = 0;
                return true;
            }
        } else {
            trailingSequenceOpenMatch = 0;
        }
        return false;
    }

    private void resetDetector() {
        trailingSequenceOpenMatch = 0;
        closeSequenceIndex = 0;
        trailingSequenceOpenDetected = false;
        trailingSequenceCloseDetected = false;
        inlineDetected = false;
        betweenQuotes = false;
    }

}
