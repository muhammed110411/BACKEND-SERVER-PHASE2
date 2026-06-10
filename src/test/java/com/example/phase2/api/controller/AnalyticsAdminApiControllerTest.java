package com.example.phase2.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.phase2.api.admin.response.DriverAnalyticsResponse;
import com.example.phase2.api.admin.response.EscalationCountResponse;
import com.example.phase2.api.admin.response.EventCountResponse;
import com.example.phase2.api.admin.response.GlobalAnalyticsResponse;
import com.example.phase2.api.admin.response.ScoreTrendPointResponse;
import com.example.phase2.application.query.AnalyticsQueryService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class AnalyticsAdminApiControllerTest {

    private AnalyticsQueryService analyticsQueryService;
    private AnalyticsAdminApiController analyticsAdminApiController;

    @BeforeEach
    void setUp() {
        analyticsQueryService = mock(AnalyticsQueryService.class);
        analyticsAdminApiController = new AnalyticsAdminApiController(analyticsQueryService);
    }

    @Test
    void getDriverAnalyticsReturnsOkAndDelegatesToQueryService() {
        DriverAnalyticsResponse response = new DriverAnalyticsResponse();
        when(analyticsQueryService.getDriverAnalytics("driver-1", 100L, 200L)).thenReturn(response);

        ResponseEntity<DriverAnalyticsResponse> result =
                analyticsAdminApiController.getDriverAnalytics("driver-1", 100L, 200L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(analyticsQueryService).getDriverAnalytics("driver-1", 100L, 200L);
    }

    @Test
    void getGlobalAnalyticsReturnsOkAndDelegatesToQueryService() {
        GlobalAnalyticsResponse response = new GlobalAnalyticsResponse();
        when(analyticsQueryService.getGlobalAnalytics(100L, 200L)).thenReturn(response);

        ResponseEntity<GlobalAnalyticsResponse> result =
                analyticsAdminApiController.getGlobalAnalytics(100L, 200L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(analyticsQueryService).getGlobalAnalytics(100L, 200L);
    }

    @Test
    void getDriverScoreTrendReturnsOkAndDelegatesToQueryService() {
        List<ScoreTrendPointResponse> response = List.of(new ScoreTrendPointResponse());
        when(analyticsQueryService.getDriverScoreTrend("driver-2", null, 300L)).thenReturn(response);

        ResponseEntity<List<ScoreTrendPointResponse>> result =
                analyticsAdminApiController.getDriverScoreTrend("driver-2", null, 300L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(analyticsQueryService).getDriverScoreTrend("driver-2", null, 300L);
    }

    @Test
    void getGlobalScoreTrendReturnsOkAndDelegatesToQueryService() {
        List<ScoreTrendPointResponse> response = List.of(new ScoreTrendPointResponse());
        when(analyticsQueryService.getGlobalScoreTrend(400L, null)).thenReturn(response);

        ResponseEntity<List<ScoreTrendPointResponse>> result =
                analyticsAdminApiController.getGlobalScoreTrend(400L, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(analyticsQueryService).getGlobalScoreTrend(400L, null);
    }

    @Test
    void getDriverEventCountsReturnsOkAndDelegatesToQueryService() {
        List<EventCountResponse> response = List.of(new EventCountResponse());
        when(analyticsQueryService.getDriverEventCounts("driver-3", 500L, 600L)).thenReturn(response);

        ResponseEntity<List<EventCountResponse>> result =
                analyticsAdminApiController.getDriverEventCounts("driver-3", 500L, 600L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(analyticsQueryService).getDriverEventCounts("driver-3", 500L, 600L);
    }

    @Test
    void getGlobalEventCountsReturnsOkAndDelegatesToQueryService() {
        List<EventCountResponse> response = List.of(new EventCountResponse());
        when(analyticsQueryService.getGlobalEventCounts(null, 700L)).thenReturn(response);

        ResponseEntity<List<EventCountResponse>> result =
                analyticsAdminApiController.getGlobalEventCounts(null, 700L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(analyticsQueryService).getGlobalEventCounts(null, 700L);
    }

    @Test
    void getDriverEscalationCountsReturnsOkAndDelegatesToQueryService() {
        List<EscalationCountResponse> response = List.of(new EscalationCountResponse());
        when(analyticsQueryService.getDriverEscalationCounts("driver-4", 800L, 900L)).thenReturn(response);

        ResponseEntity<List<EscalationCountResponse>> result =
                analyticsAdminApiController.getDriverEscalationCounts("driver-4", 800L, 900L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(analyticsQueryService).getDriverEscalationCounts("driver-4", 800L, 900L);
    }

    @Test
    void getGlobalEscalationCountsReturnsOkAndDelegatesToQueryService() {
        List<EscalationCountResponse> response = List.of(new EscalationCountResponse());
        when(analyticsQueryService.getGlobalEscalationCounts(1000L, null)).thenReturn(response);

        ResponseEntity<List<EscalationCountResponse>> result =
                analyticsAdminApiController.getGlobalEscalationCounts(1000L, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(analyticsQueryService).getGlobalEscalationCounts(1000L, null);
    }
}
