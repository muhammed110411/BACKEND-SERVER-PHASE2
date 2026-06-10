package com.example.phase2.api.controller;

import com.example.phase2.api.admin.response.SessionDetailResponse;
import com.example.phase2.api.admin.response.SessionSummaryResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.application.query.SessionQueryService;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class SessionAdminApiController {

    private final SessionQueryService sessionQueryService;

    public SessionAdminApiController(SessionQueryService sessionQueryService) {
        this.sessionQueryService = sessionQueryService;
    }

    @GetMapping("/sessions")
    public ResponseEntity<PagedResponse<SessionSummaryResponse>> getSessions(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam String sortDirection,
            @RequestParam(required = false) String driverId,
            @RequestParam(required = false) String vehicleId,
            @RequestParam(required = false) SessionEndStatus status,
            @RequestParam(required = false) SessionValidity validity,
            @RequestParam(required = false) UploadProcessingStatus uploadProcessingStatus
    ) {
        return ResponseEntity.ok(sessionQueryService.getSessions(
                page,
                size,
                sortBy,
                sortDirection,
                driverId,
                vehicleId,
                validity,
                status,
                uploadProcessingStatus
        ));
    }

    @GetMapping("/drivers/{driverId}/sessions")
    public ResponseEntity<PagedResponse<SessionSummaryResponse>> getSessionsByDriver(
            @PathVariable String driverId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam String sortDirection,
            @RequestParam(required = false) SessionEndStatus status,
            @RequestParam(required = false) SessionValidity validity,
            @RequestParam(required = false) UploadProcessingStatus uploadProcessingStatus
    ) {
        return ResponseEntity.ok(sessionQueryService.getSessionsByDriver(
                driverId,
                page,
                size,
                sortBy,
                sortDirection,
                validity,
                status,
                uploadProcessingStatus
        ));
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<SessionDetailResponse> getSessionDetail(@PathVariable String sessionId) {
        return ResponseEntity.ok(sessionQueryService.getSessionDetail(sessionId));
    }
}
