//package com.example.auth.filter;
//
//import jakarta.servlet.*;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//
//@WebFilter("/api/*")
//public class AuthFilter implements Filter {
//
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest request = (HttpServletRequest) req;
//        HttpServletResponse response = (HttpServletResponse) res;
//
//        String path = request.getRequestURI();
//
//        // Check token in cookies or headers here
//        String token = ...; // get token from cookie or header
//
//        if (token == null || !isValid(token)) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return;
//        }
//
//        chain.doFilter(req, res);
//    }
//
//    private boolean isValid(String token) {
//        // verify JWT token
//        return true; // or false
//    }
//}