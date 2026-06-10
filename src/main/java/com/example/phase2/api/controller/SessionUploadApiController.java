package com.example.phase2.api.controller;

import com.example.phase2.api.upload.request.UploadSessionRequest;
import com.example.phase2.api.upload.response.UploadSessionResponse;
import com.example.phase2.application.command.SessionUploadService;
import com.example.phase2.application.security.AuthenticatedApiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/upload")
public class SessionUploadApiController {

    private final SessionUploadService sessionUploadService;

    public SessionUploadApiController(SessionUploadService sessionUploadService) {
        this.sessionUploadService = sessionUploadService;
    }

    @PostMapping("/sessions")
    public ResponseEntity<UploadSessionResponse> uploadSession(
            @RequestBody UploadSessionRequest request,
            Authentication authentication
    ) {
        UploadSessionResponse response = sessionUploadService.uploadSession(
                request,
                resolveSourceClientId(authentication)
        );
        return ResponseEntity.ok(response);
    }

    private static String resolveSourceClientId(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedApiClient authenticatedApiClient) {
            return authenticatedApiClient.getClientId();
        }

        Object details = authentication.getDetails();
        if (details instanceof AuthenticatedApiClient authenticatedApiClient) {
            return authenticatedApiClient.getClientId();
        }

        return null;
    }
}
