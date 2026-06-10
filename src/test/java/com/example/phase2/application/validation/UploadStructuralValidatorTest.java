package com.example.phase2.application.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.phase2.api.upload.request.UploadEscalationRequest;
import com.example.phase2.api.upload.request.UploadEventRequest;
import com.example.phase2.api.upload.request.UploadScorePointRequest;
import com.example.phase2.api.upload.request.UploadSessionRequest;
import com.example.phase2.domain.enums.EscalationType;
import com.example.phase2.domain.enums.EventType;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.exception.HardValidationException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UploadStructuralValidatorTest {

    private UploadStructuralValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UploadStructuralValidator();
    }

    @Test
    void validateDeclaredCountsMatchPayloadRejectsEventCountMismatch() {
        UploadSessionRequest request = buildValidRequest();
        request.setTotalEventCount(3);

        HardValidationException exception = assertThrows(
                HardValidationException.class,
                () -> validator.validateDeclaredCountsMatchPayload(request)
        );

        assertEquals("totalEventCount", exception.getFieldName());
    }

    @Test
    void validateDeclaredCountsMatchPayloadRejectsEscalationCountMismatch() {
        UploadSessionRequest request = buildValidRequest();
        request.setTotalEscalationCount(2);

        HardValidationException exception = assertThrows(
                HardValidationException.class,
                () -> validator.validateDeclaredCountsMatchPayload(request)
        );

        assertEquals("totalEscalationCount", exception.getFieldName());
    }

    @Test
    void validateUniqueChildIdentifiersRejectsDuplicateEventId() {
        UploadSessionRequest request = buildValidRequest();
        request.setEvents(List.of(
                new UploadEventRequest("event-1", EventType.HARSH_BRAKING, 1700000100000L, 1.5d),
                new UploadEventRequest("event-1", EventType.LANE_DEPARTURE, 1700000200000L, 2.5d)
        ));

        HardValidationException exception = assertThrows(
                HardValidationException.class,
                () -> validator.validateUniqueChildIdentifiers(request)
        );

        assertEquals("events[1].eventId", exception.getFieldName());
    }

    @Test
    void validateUniqueChildIdentifiersRejectsDuplicateEscalationId() {
        UploadSessionRequest request = buildValidRequest();
        request.setEscalations(List.of(
                new UploadEscalationRequest(
                        "escalation-1",
                        EscalationType.REPEATED_HARSH_BRAKING,
                        1700000300000L,
                        List.of(EventType.HARSH_BRAKING),
                        2.5d
                ),
                new UploadEscalationRequest(
                        "escalation-1",
                        EscalationType.REPEATED_HARSH_BRAKING,
                        1700000400000L,
                        List.of(EventType.LANE_DEPARTURE),
                        3.0d
                )
        ));

        HardValidationException exception = assertThrows(
                HardValidationException.class,
                () -> validator.validateUniqueChildIdentifiers(request)
        );

        assertEquals("escalations[1].escalationId", exception.getFieldName());
    }

    private static UploadSessionRequest buildValidRequest() {
        return new UploadSessionRequest(
                "session-1",
                "driver-1",
                "vehicle-1",
                1700000000000L,
                1700000500000L,
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
                        new UploadEventRequest("event-1", EventType.HARSH_BRAKING, 1700000100000L, 1.5d),
                        new UploadEventRequest("event-2", EventType.LANE_DEPARTURE, 1700000200000L, 2.5d)
                ),
                List.of(
                        new UploadEscalationRequest(
                                "escalation-1",
                                EscalationType.REPEATED_HARSH_BRAKING,
                                1700000300000L,
                                List.of(EventType.HARSH_BRAKING, EventType.LANE_DEPARTURE),
                                2.5d
                        )
                ),
                List.of(
                        new UploadScorePointRequest(1700000000000L, 100.0d),
                        new UploadScorePointRequest(1700000400000L, 91.5d)
                )
        );
    }
}
