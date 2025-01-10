package com.wearltnow.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wearltnow.dto.ApiResponse;
import com.wearltnow.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // We cannot catch this exception in GlobalExceptionHandler, because it'll throw in Filter Service
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode errorCode;

        if (authException.getCause() instanceof BadJwtException) {
            errorCode = ErrorCode.INVALID_TOKEN;
            log.info("Bad JWT exception occurred: {}", authException.getMessage());
        } else if (authException.getCause() instanceof JwtException) {
            errorCode = ErrorCode.INVALID_TOKEN;
            log.info("JWT exception occurred: {}", authException.getMessage());
        } else if (authException instanceof AuthenticationServiceException) {
            errorCode = ErrorCode.INVALID_TOKEN;
            log.info("Authentication service exception occurred: {}", authException.getMessage());
        } else {
            errorCode = ErrorCode.UNAUTHENTICATED;
            log.info("Unauthenticated request: {}", authException.getMessage());
        }

        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.getWriter().flush();
    }


}
