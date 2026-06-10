package com.example.phase2.application.query;

import com.example.phase2.api.admin.response.DriverAnalyticsResponse;
import com.example.phase2.api.admin.response.EscalationCountResponse;
import com.example.phase2.api.admin.response.EventCountResponse;
import com.example.phase2.api.admin.response.GlobalAnalyticsResponse;
import com.example.phase2.api.admin.response.ScoreTrendPointResponse;
import com.example.phase2.application.mapper.AdminResponseMapper;
import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.EscalationType;
import com.example.phase2.domain.enums.EventType;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.domain.enums.VehicleStatus;
import com.example.phase2.exception.ResourceNotFoundException;
import com.example.phase2.persistence.entity.DriverEntity;
import com.example.phase2.persistence.entity.EscalationEntity;
import com.example.phase2.persistence.entity.EventEntity;
import com.example.phase2.persistence.entity.SessionEntity;
import com.example.phase2.persistence.entity.VehicleEntity;
import com.example.phase2.persistence.repository.DriverJpaRepository;
import com.example.phase2.persistence.repository.EscalationJpaRepository;
import com.example.phase2.persistence.repository.EventJpaRepository;
import com.example.phase2.persistence.repository.SessionJpaRepository;
import com.example.phase2.persistence.repository.VehicleJpaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnalyticsQueryServiceTest {

    private SessionJpaRepository sessionJpaRepository;
    private EventJpaRepository eventJpaRepository;
    private EscalationJpaRepository escalationJpaRepository;
    private DriverJpaRepository driverJpaRepository;
    private VehicleJpaRepository vehicleJpaRepository;
    private AnalyticsQueryService analyticsQueryService;

    @BeforeEach
    void setUp() {
        sessionJpaRepository = mock(SessionJpaRepository.class);
        eventJpaRepository = mock(EventJpaRepository.class);
        escalationJpaRepository = mock(EscalationJpaRepository.class);
        driverJpaRepository = mock(DriverJpaRepository.class);
        vehicleJpaRepository = mock(VehicleJpaRepository.class);
        analyticsQueryService = new AnalyticsQueryService(
                sessionJpaRepository,
                eventJpaRepository,
                escalationJpaRepository,
                driverJpaRepository,
                vehicleJpaRepository,
                new AdminResponseMapper()
        );
    }

    @Test
    void getDriverAnalyticsAggregatesOnlyFilteredDriverHistory() {
        when(driverJpaRepository.findById("driver-1")).thenReturn(Optional.of(buildDriver("driver-1")));
        when(sessionJpaRepository.findAll(any(Specification.class))).thenReturn(List.of(
                buildSession("session-1", "driver-1", 86_400_001L, 86_400_100L, 95.0d, 4.0d, 1, 1,
                        SessionValidity.VALID, SessionEndStatus.COMPLETED, UploadProcessingStatus.PROCESSED),
                buildSession("session-2", "driver-1", 86_400_200L, 86_400_300L, 85.0d, 6.0d, 1, 0,
                        SessionValidity.INVALID, SessionEndStatus.ABORTED, UploadProcessingStatus.PROCESSED_WITH_WARNINGS),
                buildSession("session-3", "driver-1", 172_800_100L, 172_800_200L, 75.0d, 2.0d, 0, 1,
                        SessionValidity.VALID, SessionEndStatus.COMPLETED, UploadProcessingStatus.REJECTED)
        ));
        when(eventJpaRepository.findAll()).thenReturn(List.of(
                buildEvent("event-1", "session-1", EventType.HARSH_BRAKING),
                buildEvent("event-2", "session-2", EventType.HARSH_BRAKING),
                buildEvent("event-3", "session-2", EventType.LANE_DEPARTURE),
                buildEvent("event-4", "session-x", EventType.OBSTACLE_NEAR_MISS)
        ));
        when(escalationJpaRepository.findAll()).thenReturn(List.of(
                buildEscalation("esc-1", "session-1", EscalationType.REPEATED_HARSH_BRAKING),
                buildEscalation("esc-2", "session-3", EscalationType.PROLONGED_LANE_DRIFT),
                buildEscalation("esc-3", "session-x", EscalationType.REPEATED_HARSH_BRAKING)
        ));

        DriverAnalyticsResponse response = analyticsQueryService.getDriverAnalytics("driver-1", 100L, 200L);

        assertEquals("driver-1", response.getDriverId());
        assertEquals(85.0d, response.getAverageFinalScore());
        assertEquals(2, response.getValidSessionCount());
        assertEquals(1, response.getInvalidSessionCount());
        assertEquals(2, response.getCompletedSessionCount());
        assertEquals(1, response.getAbortedSessionCount());
        assertEquals(3, response.getTotalEventCount());
        assertEquals(2, response.getTotalEscalationCount());
        assertEquals(2, response.getScoreTrend().size());
        assertEquals(2, response.getScoreTrend().get(0).getSessionCount());
        assertEquals(90.0d, response.getScoreTrend().get(0).getAverageScore());
        assertEquals(1, countForEvent(response.getEventCounts(), EventType.LANE_DEPARTURE));
        assertEquals(2, countForEvent(response.getEventCounts(), EventType.HARSH_BRAKING));
        assertEquals(1, countForEscalation(response.getEscalationCounts(), EscalationType.REPEATED_HARSH_BRAKING));
        assertEquals(1, countForEscalation(response.getEscalationCounts(), EscalationType.PROLONGED_LANE_DRIFT));
    }

    @Test
    void getGlobalAnalyticsUsesFilteredSessionsForCountsAndTotals() {
        when(driverJpaRepository.findAll()).thenReturn(List.of(
                buildDriver("driver-1"),
                buildDriver("driver-2", DriverAccountStatus.SUSPENDED)
        ));
        when(vehicleJpaRepository.findAll()).thenReturn(List.of(
                buildVehicle("vehicle-1", VehicleStatus.AVAILABLE),
                buildVehicle("vehicle-2", VehicleStatus.BUSY)
        ));
        when(sessionJpaRepository.findAll(any(Specification.class))).thenReturn(List.of(
                buildSession("session-1", "driver-1", 100L, 200L, 90.0d, 10.0d, 1, 1,
                        SessionValidity.VALID, SessionEndStatus.COMPLETED, UploadProcessingStatus.PROCESSED),
                buildSession("session-2", "driver-2", 300L, 400L, 70.0d, 5.5d, 2, 0,
                        SessionValidity.INVALID, SessionEndStatus.ABORTED, UploadProcessingStatus.REJECTED)
        ));
        when(eventJpaRepository.findAll()).thenReturn(List.of(
                buildEvent("event-1", "session-1", EventType.HARSH_BRAKING),
                buildEvent("event-2", "session-2", EventType.HARSH_BRAKING),
                buildEvent("event-3", "session-2", EventType.LANE_DEPARTURE),
                buildEvent("event-4", "other-session", EventType.LANE_DEPARTURE)
        ));
        when(escalationJpaRepository.findAll()).thenReturn(List.of(
                buildEscalation("esc-1", "session-1", EscalationType.REPEATED_HARSH_BRAKING),
                buildEscalation("esc-2", "other-session", EscalationType.PROLONGED_LANE_DRIFT)
        ));

        GlobalAnalyticsResponse response = analyticsQueryService.getGlobalAnalytics(100L, 500L);

        assertEquals(2, response.getTotalDrivers());
        assertEquals(1, response.getActiveDriverCount());
        assertEquals(1, response.getSuspendedDriverCount());
        assertEquals(2, response.getTotalVehicles());
        assertEquals(1, response.getAvailableVehicleCount());
        assertEquals(1, response.getBusyVehicleCount());
        assertEquals(2, response.getTotalSessions());
        assertEquals(1, response.getValidSessionCount());
        assertEquals(1, response.getInvalidSessionCount());
        assertEquals(1, response.getCompletedSessionCount());
        assertEquals(1, response.getAbortedSessionCount());
        assertEquals(1, response.getProcessedSessionCount());
        assertEquals(1, response.getRejectedSessionCount());
        assertEquals(80.0d, response.getAverageFinalScore());
        assertEquals(15.5d, response.getTotalDistanceKm());
        assertEquals(3, response.getTotalEventCount());
        assertEquals(1, response.getTotalEscalationCount());
    }

    @Test
    void driverScopedAnalyticsFailsThroughNotFoundPathWhenDriverDoesNotExist() {
        when(driverJpaRepository.findById("missing-driver")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> analyticsQueryService.getDriverEventCounts("missing-driver", null, null));
    }

    @Test
    void analyticsRejectsInvertedTimestampRange() {
        assertThrows(IllegalArgumentException.class,
                () -> analyticsQueryService.getGlobalScoreTrend(200L, 100L));
    }

    private int countForEvent(List<EventCountResponse> counts, EventType eventType) {
        return counts.stream()
                .filter(item -> item.getEventType() == eventType)
                .findFirst()
                .map(EventCountResponse::getCount)
                .orElseThrow();
    }

    private int countForEscalation(List<EscalationCountResponse> counts, EscalationType escalationType) {
        return counts.stream()
                .filter(item -> item.getEscalationType() == escalationType)
                .findFirst()
                .map(EscalationCountResponse::getCount)
                .orElseThrow();
    }

    private DriverEntity buildDriver(String id) {
        return buildDriver(id, DriverAccountStatus.ACTIVE);
    }

    private DriverEntity buildDriver(String id, DriverAccountStatus status) {
        return new DriverEntity(
                id,
                "Driver " + id,
                id + "@example.com",
                "$2a$10$7EqJtq98hPqEX7fNZaFWoOHi6M9G6N7B2Lr7z7k0GQzQ0jrISFRCW",
                UserRole.DRIVER,
                status,
                98.0d,
                12,
                320.5d,
                10L,
                20L,
                null
        );
    }

    private VehicleEntity buildVehicle(String id, VehicleStatus status) {
        return new VehicleEntity(id, "Vehicle " + id, status, 10L, 20L, null);
    }

    private SessionEntity buildSession(
            String id,
            String driverId,
            long startTimestamp,
            long endTimestamp,
            double finalScore,
            double totalDistanceKm,
            int totalEventCount,
            int totalEscalationCount,
            SessionValidity validity,
            SessionEndStatus status,
            UploadProcessingStatus processingStatus
    ) {
        return new SessionEntity(
                id,
                driverId,
                "vehicle-1",
                startTimestamp,
                endTimestamp,
                status,
                validity,
                processingStatus,
                finalScore,
                1.0d,
                1.0d,
                1.0d,
                100L,
                totalDistanceKm,
                totalEventCount,
                totalEscalationCount,
                endTimestamp,
                600L,
                "client-1"
        );
    }

    private EventEntity buildEvent(String id, String sessionId, EventType eventType) {
        return new EventEntity(id, sessionId, eventType, 100L, 1.0d);
    }

    private EscalationEntity buildEscalation(String id, String sessionId, EscalationType escalationType) {
        return new EscalationEntity(id, sessionId, escalationType, 100L, "[]", 1.0d);
    }
}
