package org.deri.any23.cli;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {

	public static void changeToVerboseLogging() {
		Logger.getLogger("").setLevel(Level.INFO);
		Logger.getLogger("org.deri.any23").setLevel(Level.ALL);
		Logger.getLogger("").getHandlers()[0].setLevel(Level.ALL);
	}
}
