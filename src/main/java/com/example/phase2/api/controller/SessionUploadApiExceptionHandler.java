package com.example.phase2.api.controller;

import com.example.phase2.api.common.response.ApiErrorResponse;
import com.example.phase2.api.common.response.FieldValidationErrorResponse;
import com.example.phase2.config.TimeProvider;
import com.example.phase2.exception.HardValidationException;
import com.example.phase2.exception.InactiveDriverUploadException;
import com.example.phase2.exception.ResourceNotFoundException;
import com.example.phase2.exception.UnauthorizedApiClientException;
import com.example.phase2.exception.UploadConflictException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = SessionUploadApiController.class)
public class SessionUploadApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionUploadApiExceptionHandler.class);

    private static final String INVALID_REQUEST_CODE = "INVALID_REQUEST";
    private static final String MALFORMED_REQUEST_CODE = "MALFORMED_REQUEST_BODY";
    private static final String NOT_FOUND_CODE = "RESOURCE_NOT_FOUND";
    private static final String INTERNAL_ERROR_CODE = "INTERNAL_SERVER_ERROR";
    private static final String INTERNAL_ERROR_MESSAGE = "An unexpected error occurred while processing the upload.";

    private final TimeProvider timeProvider;

    public SessionUploadApiExceptionHandler(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @ExceptionHandler(HardValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleHardValidation(
            HardValidationException exception,
            HttpServletRequest request
    ) {
        List<FieldValidationErrorResponse> fieldErrors = exception.getFieldName() == null
                ? List.of()
                : List.of(new FieldValidationErrorResponse(
                        exception.getFieldName(),
                        exception.getErrorCode() == null ? INVALID_REQUEST_CODE : exception.getErrorCode(),
                        exception.getMessage(),
                        stringifyRejectedValue(exception.getRejectedValue())
                ));

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getErrorCode() == null ? INVALID_REQUEST_CODE : exception.getErrorCode(),
                exception.getMessage(),
                request,
                fieldErrors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedRequest(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                MALFORMED_REQUEST_CODE,
                "Malformed upload request body.",
                request,
                List.of()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                NOT_FOUND_CODE,
                exception.getMessage(),
                request,
                List.of()
        );
    }

    @ExceptionHandler(InactiveDriverUploadException.class)
    public ResponseEntity<ApiErrorResponse> handleInactiveDriver(
            InactiveDriverUploadException exception,
            HttpServletRequest request
    ) {
        String code = exception.getErrorCode() == null ? INVALID_REQUEST_CODE : exception.getErrorCode();
        return buildResponse(HttpStatus.CONFLICT, code, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(UploadConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleUploadConflict(
            UploadConflictException exception,
            HttpServletRequest request
    ) {
        String code = exception.getConflictCode() == null ? INVALID_REQUEST_CODE : exception.getConflictCode();
        return buildResponse(HttpStatus.CONFLICT, code, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(UnauthorizedApiClientException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorizedUploadClient(
            UnauthorizedApiClientException exception,
            HttpServletRequest request
    ) {
        String code = exception.getErrorCode() == null ? "UNAUTHORIZED" : exception.getErrorCode();
        return buildResponse(HttpStatus.UNAUTHORIZED, code, "Authentication required", request, List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        LOGGER.warn("Unexpected upload data integrity violation at {}", request.getRequestURI());
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                INTERNAL_ERROR_CODE,
                INTERNAL_ERROR_MESSAGE,
                request,
                List.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        LOGGER.error("Unhandled upload API failure at {}", request.getRequestURI(), exception);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                INTERNAL_ERROR_CODE,
                INTERNAL_ERROR_MESSAGE,
                request,
                List.of()
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request,
            List<FieldValidationErrorResponse> fieldErrors
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                code,
                message,
                status.value(),
                timeProvider.nowEpochMillis(),
                request.getRequestURI(),
                List.copyOf(fieldErrors)
        );
        return ResponseEntity.status(status).body(response);
    }

    private static String stringifyRejectedValue(Object rejectedValue) {
        return rejectedValue == null ? null : String.valueOf(rejectedValue);
    }
}
