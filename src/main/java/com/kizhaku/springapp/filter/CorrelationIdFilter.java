package com.kizhaku.springapp.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.UUID;

public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_MDC = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Check if correlation ID is already present in header. If so, will propagate that, else create one
        String correlationId = req.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty())
            correlationId = UUID.randomUUID().toString();

        MDC.put(CORRELATION_ID_MDC, correlationId);
        res.setHeader(CORRELATION_ID_HEADER, correlationId);

        filterChain.doFilter(req, res);
    }
}
