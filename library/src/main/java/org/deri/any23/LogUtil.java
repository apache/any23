package org.deri.any23;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {

    public static void setDefaultLogging() {
        Logger.getLogger("").setLevel(Level.WARNING);
        // Suppress silly cookie warnings
        Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.SEVERE);
        Logger.getLogger("").getHandlers()[0].setLevel(Level.ALL);
    }

    public static void setVerboseLogging() {
        Logger.getLogger("").setLevel(Level.INFO);
        Logger.getLogger("org.deri.any23").setLevel(Level.ALL);
        Logger.getLogger("").getHandlers()[0].setLevel(Level.ALL);
    }
}
