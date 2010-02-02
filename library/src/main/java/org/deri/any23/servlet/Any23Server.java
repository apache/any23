package org.deri.any23.servlet;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import java.io.File;

/**
 * A simple server that uses Jetty to launch the Any23 servlet.
 * Starts up on port 8080.
 *
 * @author Richard Cyganiak
 */
public class Any23Server {

    public static void main(String[] args) throws Exception {
        if (!new File("./webapp").isDirectory()) {
            System.err.println("webapp directory not found. Server must be launched from the any23 directory.");
            System.exit(1);
        }
        Server server = new Server(8080);
        WebAppContext app = new WebAppContext(server, "webapp", "/");
        server.setHandler(app);
        server.start();
    }
}
