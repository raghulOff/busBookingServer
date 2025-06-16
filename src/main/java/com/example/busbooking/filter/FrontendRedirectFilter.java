package com.example.busbooking.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

// This file is useful when a build output is placed in the webapp folder (/busBookingServer/src/webapp)
// Any api call to this server is redirected to the index.html file
// Since the product is still in development mode. The build output is not placed.

@WebFilter("/*")
public class FrontendRedirectFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String path = request.getRequestURI();

        if (path.startsWith("/busBookingServer/api") ||
                path.startsWith("/assets") ||
                path.startsWith("/favicon") ||
                path.endsWith(".js") ||
                path.endsWith(".css") ||
                path.endsWith(".map") ||
                path.endsWith(".ico") ||
                path.endsWith(".png") ||
                path.endsWith(".jpg") ||
                path.endsWith(".jpeg")) {
            chain.doFilter(req, res);
            return;
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.html");
        dispatcher.forward(req, res);
    }
}
