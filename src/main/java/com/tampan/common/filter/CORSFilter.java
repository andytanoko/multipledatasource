package com.tampan.common.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CORSFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CORSFilter.class);
    private static final String ALLOW_HEADERS = "origin, content-type, accept, authorization, user-id, time," +
            "Content-Type, Authorization, OUTLET_ID, OUTLET_IDS";
    private static final String ALLOW_CREDENTIALS = "true";
    private static final String ALLOW_METHODS = "GET, POST, PUT, DELETE, PATCH, OPTIONS";
    private static final String BYPASSED_METHOD = "OPTIONS";

    public static final String X_AMAZON_REQUEST_ID = "x-amzn-requestid";

    private final CorsConfigurationSource configSource;



    public CORSFilter(CorsConfigurationSource configSource){
        this.configSource = configSource;
    }

    private static boolean isCorsRequest(HttpServletRequest request) {
        return (request.getHeader(HttpHeaders.ORIGIN) != null && !request.getHeader(HttpHeaders.ORIGIN).isEmpty());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestOrigin = request.getHeader("origin");
        response.addHeader("Access-Control-Allow-Origin", requestOrigin);
        response.addHeader("Access-Control-Allow-Headers", CORSFilter.ALLOW_HEADERS);
        response.addHeader("Access-Control-Allow-Credentials", CORSFilter.ALLOW_CREDENTIALS);
        response.addHeader("Access-Control-Allow-Methods", CORSFilter.ALLOW_METHODS);

        LOGGER.info("Access-Control-Allow-* headers added to the response for request with id: {}", request.getHeader(X_AMAZON_REQUEST_ID));

        if (!BYPASSED_METHOD.equals(request.getMethod())) {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        //Does not require to filter if there's no ORIGIN Header
        if (!isCorsRequest(request)) {
            return true;
        }

        return false;
    }

   
}
