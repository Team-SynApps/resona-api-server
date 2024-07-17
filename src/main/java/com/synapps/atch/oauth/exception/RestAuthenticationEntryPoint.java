package com.synapps.atch.oauth.exception;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import java.util.Map;

@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        log.error("Authentication error: ", authException);
        log.error("Request URI: {}", request.getRequestURI());
        log.error("Request method: {}", request.getMethod());
        log.error("Request headers: {}", Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(Function.identity(), request::getHeader)));

        Map<String, Object> errorDetails = Map.of(
                "error", "Unauthorized",
                "message", authException.getMessage(),
                "path", request.getServletPath()
        );

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        objectMapper.writeValue(response.getWriter(), errorDetails);
    }
}