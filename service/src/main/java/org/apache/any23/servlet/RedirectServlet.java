/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.any23.servlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This servlet contains the logic to perform the correct redirects
 * when <i>Any23</i> is used as a all-in-one web application.
 * 
 * @author Davide Palmisano ( palmisano@fbk.eu )
 */
public class RedirectServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(RedirectServlet.class);

    /**
   * 
   */
  private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
            doGet(request, response);
        } catch (ServletException | IOException e) {
            LOG.error("Error executing GET request.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // Show /resources/form.html for GET requests to the app's root
        final String pathInfo = request.getPathInfo();
        final String queryString = request.getQueryString();

        if ("/".equals(pathInfo) && queryString == null) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/resources/form.html");
            try {
                dispatcher.forward(request, response);
            } catch (ServletException | IOException e) {
                LOG.error("Error in request dispatcher forwarding.", e);
            }
            return;
        }
        // forward requests to /resources/* to the default servlet, this is
        // where we can put static files
        if (pathInfo.startsWith("/resources/")) {
            RequestDispatcher dispatcher = getServletContext().getNamedDispatcher("default");
            try {
              dispatcher.forward(request, response);
            } catch (ServletException | IOException e) {
                LOG.error("Error in named request dispatcher forwarding.", e);
            }
            return;
        }

        try {
            response.sendRedirect(
                  request.getContextPath() + "/any23" +
                          request.getPathInfo() +
                          (queryString == null ? "" : "?" + queryString)
            );
        } catch (IOException e) {
            LOG.error("Error in sending HttpServletResponse Redirect.", e);
        }
        
    }
}
