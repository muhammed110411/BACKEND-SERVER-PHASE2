package com.example.phase2.api.controller;

import com.example.phase2.api.admin.response.DriverAnalyticsResponse;
import com.example.phase2.api.admin.response.EscalationCountResponse;
import com.example.phase2.api.admin.response.EventCountResponse;
import com.example.phase2.api.admin.response.GlobalAnalyticsResponse;
import com.example.phase2.api.admin.response.ScoreTrendPointResponse;
import com.example.phase2.application.query.AnalyticsQueryService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AnalyticsAdminApiController {

    private final AnalyticsQueryService analyticsQueryService;

    public AnalyticsAdminApiController(AnalyticsQueryService analyticsQueryService) {
        this.analyticsQueryService = analyticsQueryService;
    }

    @GetMapping("/drivers/{driverId}/analytics")
    public ResponseEntity<DriverAnalyticsResponse> getDriverAnalytics(
            @PathVariable String driverId,
            @RequestParam(required = false) Long fromTimestamp,
            @RequestParam(required = false) Long toTimestamp
    ) {
        return ResponseEntity.ok(analyticsQueryService.getDriverAnalytics(
                driverId,
                fromTimestamp,
                toTimestamp
        ));
    }

    @GetMapping("/analytics")
    public ResponseEntity<GlobalAnalyticsResponse> getGlobalAnalytics(
            @RequestParam(required = false) Long fromTimestamp,
            @RequestParam(required = false) Long toTimestamp
    ) {
        return ResponseEntity.ok(analyticsQueryService.getGlobalAnalytics(
                fromTimestamp,
                toTimestamp
        ));
    }

    @GetMapping("/drivers/{driverId}/analytics/score-trend")
    public ResponseEntity<List<ScoreTrendPointResponse>> getDriverScoreTrend(
            @PathVariable String driverId,
            @RequestParam(required = false) Long fromTimestamp,
            @RequestParam(required = false) Long toTimestamp
    ) {
        return ResponseEntity.ok(analyticsQueryService.getDriverScoreTrend(
                driverId,
                fromTimestamp,
                toTimestamp
        ));
    }

    @GetMapping("/analytics/score-trend")
    public ResponseEntity<List<ScoreTrendPointResponse>> getGlobalScoreTrend(
            @RequestParam(required = false) Long fromTimestamp,
            @RequestParam(required = false) Long toTimestamp
    ) {
        return ResponseEntity.ok(analyticsQueryService.getGlobalScoreTrend(
                fromTimestamp,
                toTimestamp
        ));
    }

    @GetMapping("/drivers/{driverId}/analytics/event-counts")
    public ResponseEntity<List<EventCountResponse>> getDriverEventCounts(
            @PathVariable String driverId,
            @RequestParam(required = false) Long fromTimestamp,
            @RequestParam(required = false) Long toTimestamp
    ) {
        return ResponseEntity.ok(analyticsQueryService.getDriverEventCounts(
                driverId,
                fromTimestamp,
                toTimestamp
        ));
    }

    @GetMapping("/analytics/event-counts")
    public ResponseEntity<List<EventCountResponse>> getGlobalEventCounts(
            @RequestParam(required = false) Long fromTimestamp,
            @RequestParam(required = false) Long toTimestamp
    ) {
        return ResponseEntity.ok(analyticsQueryService.getGlobalEventCounts(
                fromTimestamp,
                toTimestamp
        ));
    }

    @GetMapping("/drivers/{driverId}/analytics/escalation-counts")
    public ResponseEntity<List<EscalationCountResponse>> getDriverEscalationCounts(
            @PathVariable String driverId,
            @RequestParam(required = false) Long fromTimestamp,
            @RequestParam(required = false) Long toTimestamp
    ) {
        return ResponseEntity.ok(analyticsQueryService.getDriverEscalationCounts(
                driverId,
                fromTimestamp,
                toTimestamp
        ));
    }

    @GetMapping("/analytics/escalation-counts")
    public ResponseEntity<List<EscalationCountResponse>> getGlobalEscalationCounts(
            @RequestParam(required = false) Long fromTimestamp,
            @RequestParam(required = false) Long toTimestamp
    ) {
        return ResponseEntity.ok(analyticsQueryService.getGlobalEscalationCounts(
                fromTimestamp,
                toTimestamp
        ));
    }
}
