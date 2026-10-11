package com.kabera.rw_reviews.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;


@Component
public class RequestLoggingFilter extends OncePerRequestFilter
{
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);
        response.setHeader("X-Request-Id", requestId); // lets a client quote it when reporting a problem
        long start = System.nanoTime();

        try
        {
            filterChain.doFilter(request, response);
        }
        finally
        {
            long ms = (System.nanoTime() - start) / 1_000_000;
            String query = request.getQueryString() == null ? "" : "?" + request.getQueryString();
            logger.info("{} {}{} -> {} ({} ms)", request.getMethod(), request.getRequestURI(),
                    query, response.getStatus(), ms);
            MDC.remove("requestId"); // threads are reused, so always clean up
        }
    }

    /** Skip noisy infrastructure endpoints (health checks poll often). */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)
    {
        String path = request.getRequestURI();
        return path.startsWith("/actuator") || path.startsWith("/h2-console");
    }
}
