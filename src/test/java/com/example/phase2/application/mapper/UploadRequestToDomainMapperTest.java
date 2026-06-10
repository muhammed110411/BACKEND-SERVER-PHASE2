package com.example.phase2.application.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.phase2.api.upload.request.UploadEscalationRequest;
import com.example.phase2.api.upload.request.UploadEventRequest;
import com.example.phase2.api.upload.request.UploadScorePointRequest;
import com.example.phase2.api.upload.request.UploadSessionRequest;
import com.example.phase2.domain.enums.EscalationType;
import com.example.phase2.domain.enums.EventType;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;
import com.example.phase2.domain.model.DrivingSessionRecord;
import com.example.phase2.domain.model.EscalationRecord;
import com.example.phase2.domain.model.IngestionAnomalyFlag;
import com.example.phase2.domain.model.SafetyEventRecord;
import com.example.phase2.domain.model.ScorePointRecord;
import com.example.phase2.domain.model.SessionDetailRecord;

class UploadRequestToDomainMapperTest {

    private UploadRequestToDomainMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UploadRequestToDomainMapper();
    }

    @Test
    void toDrivingSessionRecordMapsValidatedRequestFieldsAndExplicitUploadMetadata() {
        UploadSessionRequest request = buildRequest();

        DrivingSessionRecord session = mapper.toDrivingSessionRecord(
                request,
                UploadProcessingStatus.PROCESSED,
                1700000600L,
                1700000700L,
                "client-7"
        );

        assertNotNull(session);
        assertEquals("session-1", session.getId());
        assertEquals("driver-1", session.getDriverId());
        assertEquals("vehicle-1", session.getVehicleId());
        assertEquals(1700000000L, session.getStartTimestamp());
        assertEquals(1700000500L, session.getEndTimestamp());
        assertEquals(SessionEndStatus.COMPLETED, session.getStatus());
        assertEquals(SessionValidity.VALID, session.getValidity());
        assertEquals(UploadProcessingStatus.PROCESSED, session.getUploadProcessingStatus());
        assertEquals(91.5d, session.getFinalScore());
        assertEquals(3.0d, session.getTotalContinuousPenalty());
        assertEquals(4.0d, session.getTotalEventPenalty());
        assertEquals(2.0d, session.getTotalEscalationPenalty());
        assertEquals(500L, session.getTotalDurationSeconds());
        assertEquals(12.75d, session.getTotalDistanceKm());
        assertEquals(2, session.getTotalEventCount());
        assertEquals(1, session.getTotalEscalationCount());
        assertEquals(1700000600L, session.getUploadedAt());
        assertEquals(1700000700L, session.getProcessedAt());
        assertEquals("client-7", session.getSourceClientId());
    }

    @Test
    void toSafetyEventRecordsMapsOnlyRequestEventsAndPreservesOrder() {
        List<SafetyEventRecord> events = mapper.toSafetyEventRecords(buildRequest());

        assertEquals(2, events.size());
        assertEquals("event-1", events.get(0).getId());
        assertEquals("session-1", events.get(0).getSessionId());
        assertEquals(EventType.HARSH_BRAKING, events.get(0).getEventType());
        assertEquals(1700000100L, events.get(0).getTimestamp());
        assertEquals(1.5d, events.get(0).getSeverity());
        assertEquals("event-2", events.get(1).getId());
        assertEquals(EventType.LANE_DEPARTURE, events.get(1).getEventType());
    }

    @Test
    void toEscalationRecordsMapsOnlyRequestEscalationsAndPreservesRelatedEventOrdering() {
        List<EscalationRecord> escalations = mapper.toEscalationRecords(buildRequest());

        assertEquals(1, escalations.size());
        assertEquals("escalation-1", escalations.get(0).getId());
        assertEquals("session-1", escalations.get(0).getSessionId());
        assertEquals(EscalationType.REPEATED_HARSH_BRAKING, escalations.get(0).getEscalationType());
        assertEquals(1700000300L, escalations.get(0).getTriggeredAt());
        assertEquals(List.of(EventType.HARSH_BRAKING, EventType.LANE_DEPARTURE), escalations.get(0).getRelatedEventTypes());
        assertEquals(2.5d, escalations.get(0).getSeverityMultiplier());
    }

    @Test
    void toScorePointRecordsMapsOnlyRequestScorePointsWithoutInventedIds() {
        List<ScorePointRecord> scorePoints = mapper.toScorePointRecords(buildRequest());

        assertEquals(2, scorePoints.size());
        assertEquals("session-1", scorePoints.get(0).getSessionId());
        assertEquals(1700000000L, scorePoints.get(0).getTimestamp());
        assertEquals(100.0d, scorePoints.get(0).getScoreValue());
        assertEquals(1700000400L, scorePoints.get(1).getTimestamp());
        assertEquals(91.5d, scorePoints.get(1).getScoreValue());
    }

    @Test
    void toSessionDetailRecordAssemblesSuppliedDomainObjectsWithoutDerivingAnomalies() {
        DrivingSessionRecord session = mapper.toDrivingSessionRecord(
                buildRequest(),
                UploadProcessingStatus.PROCESSED,
                1700000600L,
                1700000700L,
                "client-7"
        );
        List<SafetyEventRecord> events = mapper.toSafetyEventRecords(buildRequest());
        List<EscalationRecord> escalations = mapper.toEscalationRecords(buildRequest());
        List<ScorePointRecord> scoreHistory = mapper.toScorePointRecords(buildRequest());
        List<IngestionAnomalyFlag> anomalyFlags = List.of(
                new IngestionAnomalyFlag("WARN-1", "warning", "WARNING", "scorePoints", 1700000700L, false)
        );

        SessionDetailRecord detail = mapper.toSessionDetailRecord(
                session,
                events,
                escalations,
                scoreHistory,
                anomalyFlags
        );

        assertSame(session, detail.getSession());
        assertEquals(events, detail.getEvents());
        assertEquals(escalations, detail.getEscalations());
        assertEquals(scoreHistory, detail.getScoreHistory());
        assertEquals(anomalyFlags, detail.getIngestionAnomalyFlags());
        assertTrue(detail.hasAnomalies());
    }

    @Test
    void mappingMethodsReturnNullForNullTopLevelSessionObjectsAndEmptyListsForMissingChildren() {
        assertNull(mapper.toDrivingSessionRecord(null, UploadProcessingStatus.PROCESSED, 1L, 2L, "client"));
        assertNull(mapper.toSessionDetailRecord(null, List.of(), List.of(), List.of(), List.of()));
        assertEquals(List.of(), mapper.toSafetyEventRecords(null));
        assertEquals(List.of(), mapper.toEscalationRecords(null));
        assertEquals(List.of(), mapper.toScorePointRecords(null));
    }

    private UploadSessionRequest buildRequest() {
        return new UploadSessionRequest(
                "session-1",
                "driver-1",
                "vehicle-1",
                1700000000L,
                1700000500L,
                SessionEndStatus.COMPLETED,
                SessionValidity.VALID,
                91.5d,
                3.0d,
                4.0d,
                2.0d,
                500L,
                12.75d,
                2,
                1,
                List.of(
                        new UploadEventRequest("event-1", EventType.HARSH_BRAKING, 1700000100L, 1.5d),
                        new UploadEventRequest("event-2", EventType.LANE_DEPARTURE, 1700000200L, 2.5d)
                ),
                List.of(
                        new UploadEscalationRequest(
                                "escalation-1",
                                EscalationType.REPEATED_HARSH_BRAKING,
                                1700000300L,
                                List.of(EventType.HARSH_BRAKING, EventType.LANE_DEPARTURE),
                                2.5d
                        )
                ),
                List.of(
                        new UploadScorePointRequest(1700000000L, 100.0d),
                        new UploadScorePointRequest(1700000400L, 91.5d)
                )
        );
    }
}
