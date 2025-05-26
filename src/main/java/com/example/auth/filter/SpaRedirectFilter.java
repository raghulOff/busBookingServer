//package com.example.auth.filter;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//public class SpaRedirectFilter implements Filter {
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest req = (HttpServletRequest) request;
//        String path = req.getRequestURI();
//
//        // Allow API calls and static asset requests through
//        if (path.startsWith("/tryingAuth/api/") || path.startsWith("/tryingAuth/assets/") || path.endsWith(".js") || path.endsWith(".css") || path.endsWith(".ico")) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // Otherwise, redirect to index.html
//        request.getRequestDispatcher("/index.html").forward(request, response);
//    }
//}
