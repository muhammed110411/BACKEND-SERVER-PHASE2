package com.example.phase2.api.controller;

import com.example.phase2.api.common.response.ApiErrorResponse;
import com.example.phase2.config.TimeProvider;
import com.example.phase2.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {
        DriverAuthApiController.class,
        DriverSessionApiController.class
})
public class DriverApiExceptionHandler {

    private static final String NOT_FOUND_CODE = "RESOURCE_NOT_FOUND";

    private final TimeProvider timeProvider;

    public DriverApiExceptionHandler(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(
                NOT_FOUND_CODE,
                exception.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                timeProvider.nowEpochMillis(),
                request.getRequestURI(),
                List.of()
        ));
    }
}
