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
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Show /resources/form.html for GET requests to the app's root
        if (("/".equals(request.getPathInfo()) && request.getQueryString() == null)) {
            getServletContext().getRequestDispatcher("/resources/form.html").forward(request, response);
            return;
        }
        // forward requests to /resources/* to the default servlet, this is
        // where we can put static files
        if (request.getPathInfo().startsWith("/resources/")) {
            getServletContext().getNamedDispatcher("default").forward(request, response);
            return;
        }
        getServletContext().getRequestDispatcher("/any23").forward(request, response);        
    }
}
