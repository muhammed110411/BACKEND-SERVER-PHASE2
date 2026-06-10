package com.example.phase2.api.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.phase2.application.command.SessionUploadService;
import com.example.phase2.config.TimeProvider;
import com.example.phase2.exception.HardValidationException;
import com.example.phase2.exception.InactiveDriverUploadException;
import com.example.phase2.exception.ResourceNotFoundException;
import com.example.phase2.exception.UploadConflictException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;

class SessionUploadApiExceptionHandlerTest {

    private SessionUploadService sessionUploadService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        sessionUploadService = mock(SessionUploadService.class);
        TimeProvider timeProvider = mock(TimeProvider.class);
        when(timeProvider.nowEpochMillis()).thenReturn(1712345678901L);

        mockMvc = MockMvcBuilders.standaloneSetup(new SessionUploadApiController(sessionUploadService))
                .setControllerAdvice(new SessionUploadApiExceptionHandler(timeProvider))
                .build();
    }

    @Test
    void hardValidationFailureReturnsBadRequestJson() throws Exception {
        when(sessionUploadService.uploadSession(any(), eq(null)))
                .thenThrow(new HardValidationException(
                        "totalEventCount must match events.size",
                        "UPLOAD_HARD_VALIDATION",
                        "totalEventCount",
                        3
                ));

        mockMvc.perform(post("/api/upload/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new UploadRequestBody())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("UPLOAD_HARD_VALIDATION"))
                .andExpect(jsonPath("$.message").value("totalEventCount must match events.size"))
                .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("totalEventCount"));
    }

    @Test
    void missingDriverReturnsNotFoundJson() throws Exception {
        when(sessionUploadService.uploadSession(any(), eq(null)))
                .thenThrow(new ResourceNotFoundException(
                        "Driver not found: driver-404",
                        "driver",
                        "driver-404",
                        "id"
                ));

        mockMvc.perform(post("/api/upload/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new UploadRequestBody())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Driver not found: driver-404"));
    }

    @Test
    void inactiveDriverReturnsConflictJson() throws Exception {
        when(sessionUploadService.uploadSession(any(), eq(null)))
                .thenThrow(new InactiveDriverUploadException(
                        "Driver is not allowed to upload sessions: driver-2",
                        "driver-2",
                        "DRIVER_UPLOAD_NOT_ALLOWED",
                        "SUSPENDED"
                ));

        mockMvc.perform(post("/api/upload/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new UploadRequestBody())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DRIVER_UPLOAD_NOT_ALLOWED"));
    }

    @Test
    void duplicateSessionReturnsConflictJson() throws Exception {
        when(sessionUploadService.uploadSession(any(), eq(null)))
                .thenThrow(new UploadConflictException(
                        "Session upload conflict: session already exists: session-1",
                        "UPLOAD_DUPLICATE_SESSION",
                        "session-1",
                        "session",
                        "sessionId"
                ));

        mockMvc.perform(post("/api/upload/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new UploadRequestBody())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("UPLOAD_DUPLICATE_SESSION"));
    }

    @Test
    void unexpectedFailureReturnsSanitizedInternalServerError() throws Exception {
        when(sessionUploadService.uploadSession(any(), eq(null)))
                .thenThrow(new IllegalStateException("raw stack detail /secret/path"));

        mockMvc.perform(post("/api/upload/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new UploadRequestBody())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred while processing the upload."))
                .andExpect(content().string(not(containsString("secret"))));
    }

    private static final class UploadRequestBody {
        public String sessionId = "session-1";
        public String driverId = "driver-1";
        public String vehicleId = "vehicle-1";
    }
}
