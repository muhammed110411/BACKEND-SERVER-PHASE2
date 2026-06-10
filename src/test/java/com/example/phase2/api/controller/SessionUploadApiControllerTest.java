package com.example.phase2.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.phase2.api.upload.request.UploadSessionRequest;
import com.example.phase2.api.upload.response.UploadSessionResponse;
import com.example.phase2.application.command.SessionUploadService;
import com.example.phase2.application.security.AuthenticatedApiClient;
import com.example.phase2.domain.enums.UploadProcessingStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class SessionUploadApiControllerTest {

    private SessionUploadService sessionUploadService;
    private SessionUploadApiController sessionUploadApiController;

    @BeforeEach
    void setUp() {
        sessionUploadService = mock(SessionUploadService.class);
        sessionUploadApiController = new SessionUploadApiController(sessionUploadService);
    }

    @Test
    void uploadSessionReturnsOkForAcceptedUploadAndPassesAuthenticatedClientId() {
        UploadSessionRequest request = new UploadSessionRequest();
        UploadSessionResponse response = new UploadSessionResponse(
                "session-1",
                UploadProcessingStatus.PROCESSED,
                100L,
                100L,
                List.of()
        );
        Authentication authentication = authenticationWithClient("upload-client");
        when(sessionUploadService.uploadSession(request, "upload-client")).thenReturn(response);

        ResponseEntity<UploadSessionResponse> result =
                sessionUploadApiController.uploadSession(request, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(sessionUploadService).uploadSession(request, "upload-client");
    }

    @Test
    void uploadSessionFallsBackToNullClientIdWhenAuthenticationDoesNotExposeApprovedClient() {
        UploadSessionRequest request = new UploadSessionRequest();
        Authentication authentication = mock(Authentication.class);
        when(sessionUploadService.uploadSession(request, null))
                .thenReturn(new UploadSessionResponse(
                        "session-2",
                        UploadProcessingStatus.PROCESSED_WITH_WARNINGS,
                        200L,
                        200L,
                        List.of()
                ));

        sessionUploadApiController.uploadSession(request, authentication);

        verify(sessionUploadService).uploadSession(request, null);
    }

    private static Authentication authenticationWithClient(String clientId) {
        AuthenticatedApiClient authenticatedApiClient =
                new AuthenticatedApiClient(clientId, true, true, "Upload Client");
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        authenticatedApiClient,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_API_CLIENT"))
                );
        authentication.setDetails(authenticatedApiClient);
        return authentication;
    }
}
