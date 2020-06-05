package main.java;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class LoginFilter
 */
// @WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("LoginFilter: " + httpRequest.getRequestURI());

        String thisURL = httpRequest.getRequestURI();

        // Check if this URL is allowed to access without logging in
        if (this.isUrlAllowedWithoutLogin(thisURL)) {
            // Keep default action: pass along the filter chain
            chain.doFilter(request, response);
            return;
        }

        // Redirect to login page if the "user" attribute doesn't exist in session and thisURL isn't _dashboard_main
        if (httpRequest.getSession().getAttribute("user") == null
                && !thisURL.endsWith("_dashboard_main.html") && !thisURL.endsWith("_dashboard_main.js") && !thisURL.endsWith("api/_dashboard_main")
                && !thisURL.endsWith("_dashboard_star.html") && !thisURL.endsWith("_dashboard_star.js") && !thisURL.endsWith("api/_dashboard_star")
                && !thisURL.endsWith("_dashboard_menu.html") && !thisURL.endsWith("_dashboard_menu.js") && !thisURL.endsWith("api/_dashboard_menu")) {
            httpResponse.sendRedirect("login.html");
        } else if (httpRequest.getSession().getAttribute("employee") == null
                && (thisURL.endsWith("_dashboard_main.html") || thisURL.endsWith("_dashboard_main.js") || thisURL.endsWith("api/_dashboard_main") ||
                thisURL.endsWith("_dashboard_star.html") || thisURL.endsWith("_dashboard_star.js") || thisURL.endsWith("api/_dashboard_star") ||
                thisURL.endsWith("_dashboard_menu.html") || thisURL.endsWith("_dashboard_menu.js") || thisURL.endsWith("api/_dashboard_menu"))) {
            httpResponse.sendRedirect("_dashboard.html");
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        /*
         Setup your own rules here to allow accessing some resources without logging in
         Always allow your own login related requests(html, js, servlet, etc..)
         You might also want to allow some CSS files, etc..
         */
        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    public void init(FilterConfig fConfig) {
        // Go to login before dashboard
        allowedURIs.add("login.html");
        allowedURIs.add("login.js");
        allowedURIs.add("api/login");
        allowedURIs.add("_dashboard.html");
        allowedURIs.add("_dashboard.js");
        allowedURIs.add("/api/_dashboard");
    }

    public void destroy() {
        // ignored.
    }

}
