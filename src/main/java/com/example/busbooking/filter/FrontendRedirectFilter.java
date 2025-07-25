package com.example.busbooking.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@WebFilter("/*")
public class FrontendRedirectFilter implements Filter {

    private static final String DIST_DIR = "C:\\Users\\Intern\\Desktop\\bus-booking-client\\dist";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String requestPath = request.getRequestURI();

        // Allow API paths
        if (requestPath.startsWith("/busBookingServer/api")) {
            chain.doFilter(req, res);
            return;
        }

        // Remove context path if any
        String relativePath = requestPath;
        if (request.getContextPath() != null && !request.getContextPath().isEmpty()) {
            System.out.println(request.getContextPath() );
            relativePath = requestPath.substring(request.getContextPath().length());
        }

        // Handle root (/) to index.html
        if (relativePath.equals("/") || relativePath.isEmpty()) {
            relativePath = "/index.html";
        }

        java.nio.file.Path filePath = Paths.get(DIST_DIR, relativePath);

        if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
            // File exists, serve it`
            String mimeType = req.getServletContext().getMimeType(filePath.toString());
            if (mimeType == null) mimeType = "application/octet-stream";

            res.setContentType(mimeType);
            Files.copy(filePath, res.getOutputStream());
            return;
        }


        // For unknown routes (like /dashboard), fallback to index.html
        java.nio.file.Path indexPath = Paths.get(DIST_DIR, "index.html");

        if (Files.exists(indexPath)) {
            String html = Files.readString(indexPath, StandardCharsets.UTF_8);
            res.setContentType("text/html;charset=UTF-8");
            res.getWriter().write(html);
        } else {
            ((HttpServletResponse) res).sendError(404, "Not Found");
        }
    }
}
