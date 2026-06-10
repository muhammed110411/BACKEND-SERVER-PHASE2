package com.example.phase2.api.controller;

import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.api.driver.response.DriverSessionDetailResponse;
import com.example.phase2.api.driver.response.DriverSessionSummaryResponse;
import com.example.phase2.application.query.SessionQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/driver/sessions")
public class DriverSessionApiController {

    private final SessionQueryService sessionQueryService;

    public DriverSessionApiController(SessionQueryService sessionQueryService) {
        this.sessionQueryService = sessionQueryService;
    }

    @GetMapping
    public ResponseEntity<PagedResponse<DriverSessionSummaryResponse>> getSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startTimestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            Authentication authentication
    ) {
        return ResponseEntity.ok(sessionQueryService.getDriverSessions(
                authentication.getName(),
                page,
                size,
                sortBy,
                sortDirection
        ));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<DriverSessionDetailResponse> getSessionDetail(
            @PathVariable String sessionId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(sessionQueryService.getDriverSessionDetail(
                authentication.getName(),
                sessionId
        ));
    }
}
