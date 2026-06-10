package com.example.phase2.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.phase2.api.admin.response.SessionDetailResponse;
import com.example.phase2.api.admin.response.SessionSummaryResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.application.query.SessionQueryService;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class SessionAdminApiControllerTest {

    private SessionQueryService sessionQueryService;
    private SessionAdminApiController sessionAdminApiController;

    @BeforeEach
    void setUp() {
        sessionQueryService = mock(SessionQueryService.class);
        sessionAdminApiController = new SessionAdminApiController(sessionQueryService);
    }

    @Test
    void getSessionsReturnsOkAndDelegatesAllIndependentFilters() {
        PagedResponse<SessionSummaryResponse> response = new PagedResponse<>(
                List.of(new SessionSummaryResponse()),
                0,
                20,
                1,
                1,
                false,
                false
        );
        when(sessionQueryService.getSessions(
                0,
                20,
                "startTimestamp",
                "desc",
                "driver-1",
                "vehicle-1",
                SessionValidity.VALID,
                SessionEndStatus.COMPLETED,
                UploadProcessingStatus.PROCESSED
        )).thenReturn(response);

        ResponseEntity<PagedResponse<SessionSummaryResponse>> result = sessionAdminApiController.getSessions(
                0,
                20,
                "startTimestamp",
                "desc",
                "driver-1",
                "vehicle-1",
                SessionEndStatus.COMPLETED,
                SessionValidity.VALID,
                UploadProcessingStatus.PROCESSED
        );

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(sessionQueryService).getSessions(
                0,
                20,
                "startTimestamp",
                "desc",
                "driver-1",
                "vehicle-1",
                SessionValidity.VALID,
                SessionEndStatus.COMPLETED,
                UploadProcessingStatus.PROCESSED
        );
    }

    @Test
    void getSessionsByDriverReturnsOkAndUsesDriverPathScope() {
        PagedResponse<SessionSummaryResponse> response = new PagedResponse<>(
                List.of(new SessionSummaryResponse()),
                1,
                10,
                5,
                1,
                false,
                true
        );
        when(sessionQueryService.getSessionsByDriver(
                "driver-42",
                1,
                10,
                "uploadedAt",
                "asc",
                SessionValidity.INVALID,
                SessionEndStatus.ABORTED,
                UploadProcessingStatus.PROCESSED_WITH_WARNINGS
        )).thenReturn(response);

        ResponseEntity<PagedResponse<SessionSummaryResponse>> result =
                sessionAdminApiController.getSessionsByDriver(
                        "driver-42",
                        1,
                        10,
                        "uploadedAt",
                        "asc",
                        SessionEndStatus.ABORTED,
                        SessionValidity.INVALID,
                        UploadProcessingStatus.PROCESSED_WITH_WARNINGS
                );

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(sessionQueryService).getSessionsByDriver(
                "driver-42",
                1,
                10,
                "uploadedAt",
                "asc",
                SessionValidity.INVALID,
                SessionEndStatus.ABORTED,
                UploadProcessingStatus.PROCESSED_WITH_WARNINGS
        );
    }

    @Test
    void getSessionDetailReturnsOkAndDelegatesToQueryService() {
        SessionDetailResponse response = new SessionDetailResponse();
        when(sessionQueryService.getSessionDetail("session-9")).thenReturn(response);

        ResponseEntity<SessionDetailResponse> result =
                sessionAdminApiController.getSessionDetail("session-9");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(sessionQueryService).getSessionDetail("session-9");
    }
}
