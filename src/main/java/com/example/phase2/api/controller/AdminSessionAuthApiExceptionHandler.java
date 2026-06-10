package com.example.phase2.api.controller;

import com.example.phase2.api.common.response.ApiErrorResponse;
import com.example.phase2.config.TimeProvider;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = AdminSessionAuthApiController.class)
public class AdminSessionAuthApiExceptionHandler {

    private static final String ERROR_CODE = "UNAUTHORIZED";
    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid username or password.";

    private final TimeProvider timeProvider;

    public AdminSessionAuthApiExceptionHandler(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationFailure(
            AuthenticationException exception,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                ERROR_CODE,
                INVALID_CREDENTIALS_MESSAGE,
                HttpStatus.UNAUTHORIZED.value(),
                timeProvider.nowEpochMillis(),
                request.getRequestURI(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
