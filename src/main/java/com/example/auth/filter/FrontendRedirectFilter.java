package com.example.auth.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class FrontendRedirectFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI();


        if (path.startsWith("/tryingAuth/api") ||
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


//        if (path.startsWith("/api") ||
//                path.startsWith("/assets") ||
//                path.startsWith("/favicon") ||
//                path.endsWith(".js") ||
//                path.endsWith(".css") ||
//                path.endsWith(".map") ||
//                path.endsWith(".ico") ||
//                path.endsWith(".png") ||
//                path.endsWith(".jpg") ||
//                path.endsWith(".jpeg")) {
//            chain.doFilter(req, res);
//            return;
//        }
//        System.out.println("here it came");
//        x++;
        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.html");
        dispatcher.forward(req, res);
    }
}
