package com.example.phase2.security;

import com.example.phase2.api.common.response.ApiErrorResponse;
import com.example.phase2.config.TimeProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    private static final String ERROR_CODE = "UNAUTHORIZED";
    private static final String UNAUTHORIZED_MESSAGE = "Authentication required";

    private final ObjectMapper objectMapper;
    private final TimeProvider timeProvider;

    public AuthenticationEntryPointHandler(ObjectMapper objectMapper, TimeProvider timeProvider) {
        this.objectMapper = objectMapper;
        this.timeProvider = timeProvider;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ApiErrorResponse apiErrorResponse = buildUnauthorizedResponse(request);
        objectMapper.writeValue(response.getWriter(), apiErrorResponse);
        response.getWriter().flush();
    }

    private ApiErrorResponse buildUnauthorizedResponse(HttpServletRequest request) {
        return new ApiErrorResponse(
                ERROR_CODE,
                UNAUTHORIZED_MESSAGE,
                HttpServletResponse.SC_UNAUTHORIZED,
                timeProvider.nowEpochMillis(),
                request.getRequestURI(),
                List.of()
        );
    }
}
