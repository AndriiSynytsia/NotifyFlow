package com.notifyflow.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;


public class ApiKeyAuthenticationFilter implements Filter {

    private static final String API_KEY_HEADER = "X-API-Key";

    @Value("${notifyflow.api.key}")
    private String expectedApiKey;

    @Value("${notifyflow.api.enabled:true}")
    private boolean apiKeyEnabled;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (!apiKeyEnabled) {
            chain.doFilter(request, response);
            return;
        }

        if (!httpRequest.getRequestURI().startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        String apiKey = httpRequest.getHeader(API_KEY_HEADER);

        if (apiKey == null || !expectedApiKey.equals(apiKey)) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("""
                    {
                      "code": "UNAUTHORIZED",
                      "message": "Missing or invalid API key",
                      "status": 401
                    }
                    """);
            return;
        }

        chain.doFilter(request, response);
    }
}
