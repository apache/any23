/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.servlet;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import java.io.File;

/**
 * A simple server that uses <i>Jetty</i> to launch the Any23 Servlet.
 * Starts up on port 8080.
 *
 * @author Richard Cyganiak
 */
public class Any23Server {

    /**
     * Runs the {@link org.deri.any23.servlet.Servlet} instance on the local <code>8080</code> port.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        final String webapp = args.length > 0 ? args[0] : "./webapp";
        if (!new File(webapp).isDirectory()) {
            System.err.println("webapp directory not found. Server must be launched from any23's /bin directory.");
            System.exit(1);
        }
        Server server = new Server(8080);
        WebAppContext app = new WebAppContext(server, webapp, "/");
        server.setHandler(app);
        server.start();
    }
    
}
