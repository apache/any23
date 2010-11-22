/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This servlet contains the logic to perform the correct redirects
 * when <i>Any23</i> is used as a all-in-one web application.
 * 
 * @author Davide Palmisano ( palmisano@fbk.eu )
 */
public class RedirectServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // Show /resources/form.html for GET requests to the app's root
        final String pathInfo = request.getPathInfo();
        final String queryString = request.getQueryString();

        if (("/".equals(pathInfo) && queryString == null)) {
            getServletContext().getRequestDispatcher("/resources/form.html").forward(request, response);
            return;
        }
        // forward requests to /resources/* to the default servlet, this is
        // where we can put static files
        if (pathInfo.startsWith("/resources/")) {
            getServletContext().getNamedDispatcher("default").forward(request, response);
            return;
        }

        response.sendRedirect(
                request.getContextPath() + "/any23" +
                        request.getPathInfo() +
                        (queryString == null ? "" : "?" + queryString)
        );
    }
}
