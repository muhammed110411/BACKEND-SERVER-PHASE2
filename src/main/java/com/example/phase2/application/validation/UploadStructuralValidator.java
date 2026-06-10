package com.example.phase2.application.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.example.phase2.api.upload.request.UploadEscalationRequest;
import com.example.phase2.api.upload.request.UploadEventRequest;
import com.example.phase2.api.upload.request.UploadScorePointRequest;
import com.example.phase2.api.upload.request.UploadSessionRequest;
import com.example.phase2.domain.enums.EventType;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.exception.HardValidationException;

@Component
public class UploadStructuralValidator {

    private static final String ERROR_CODE = "UPLOAD_HARD_VALIDATION";
    private static final double MIN_FINAL_SCORE = 0.0d;
    private static final double MAX_FINAL_SCORE = 100.0d;

    public void validateSessionIdentity(String sessionId, String driverId, String vehicleId) {
        requireNotBlank(sessionId, "sessionId");
        requireNotBlank(driverId, "driverId");
        requireNotBlank(vehicleId, "vehicleId");
    }

    public void validateSessionTimestamps(long startTimestamp, long endTimestamp) {
        if (startTimestamp < 0L) {
            throw invalid("startTimestamp", "must be non-negative", startTimestamp);
        }

        if (endTimestamp < 0L) {
            throw invalid("endTimestamp", "must be non-negative", endTimestamp);
        }

        if (startTimestamp > endTimestamp) {
            throw invalid("endTimestamp", "must be greater than or equal to startTimestamp", endTimestamp);
        }
    }

    public void validateSessionEnums(SessionEndStatus status, SessionValidity validity) {
        requireNotNull(status, "sessionEndStatus");
        requireNotNull(validity, "sessionValidity");
    }

    public void validateSessionTotals(
            double finalScore,
            double totalContinuousPenalty,
            double totalEventPenalty,
            double totalEscalationPenalty,
            long totalDurationSeconds,
            double totalDistanceKm,
            int totalEventCount,
            int totalEscalationCount
    ) {
        requireFinite(finalScore, "finalScore");
        requireFinite(totalContinuousPenalty, "totalContinuousPenalty");
        requireFinite(totalEventPenalty, "totalEventPenalty");
        requireFinite(totalEscalationPenalty, "totalEscalationPenalty");
        requireFinite(totalDistanceKm, "totalDistanceKm");

        if (finalScore < MIN_FINAL_SCORE || finalScore > MAX_FINAL_SCORE) {
            throw invalid(
                    "finalScore",
                    "must be within the approved range [" + MIN_FINAL_SCORE + ", " + MAX_FINAL_SCORE + "]",
                    finalScore
            );
        }

        if (totalContinuousPenalty < 0.0d) {
            throw invalid("totalContinuousPenalty", "must be non-negative", totalContinuousPenalty);
        }

        if (totalEventPenalty < 0.0d) {
            throw invalid("totalEventPenalty", "must be non-negative", totalEventPenalty);
        }

        if (totalEscalationPenalty < 0.0d) {
            throw invalid("totalEscalationPenalty", "must be non-negative", totalEscalationPenalty);
        }

        if (totalDurationSeconds < 0L) {
            throw invalid("totalDurationSeconds", "must be non-negative", totalDurationSeconds);
        }

        if (totalDistanceKm < 0.0d) {
            throw invalid("totalDistanceKm", "must be non-negative", totalDistanceKm);
        }

        if (totalEventCount < 0) {
            throw invalid("totalEventCount", "must be non-negative", totalEventCount);
        }

        if (totalEscalationCount < 0) {
            throw invalid("totalEscalationCount", "must be non-negative", totalEscalationCount);
        }
    }

    public void validateEventRequest(UploadEventRequest event) {
        requireNotNull(event, "event");
        requireNotBlank(event.getEventId(), "event.eventId");
        requireNotNull(event.getEventType(), "event.eventType");
        requireNotNull(event.getTimestamp(), "event.timestamp");
        requireNotNull(event.getSeverity(), "event.severity");

        if (event.getTimestamp() < 0L) {
            throw invalid("event.timestamp", "must be non-negative", event.getTimestamp());
        }

        requireFinite(event.getSeverity(), "event.severity");
        if (event.getSeverity() < 0.0d) {
            throw invalid("event.severity", "must be non-negative", event.getSeverity());
        }
    }

    public void validateEscalationRequest(UploadEscalationRequest escalation) {
        requireNotNull(escalation, "escalation");
        requireNotBlank(escalation.getEscalationId(), "escalation.escalationId");
        requireNotNull(escalation.getEscalationType(), "escalation.escalationType");
        requireNotNull(escalation.getTriggeredAt(), "escalation.triggeredAt");
        requireNotNull(escalation.getSeverityMultiplier(), "escalation.severityMultiplier");
        requireNotNull(escalation.getRelatedEventTypes(), "escalation.relatedEventTypes");

        if (escalation.getTriggeredAt() < 0L) {
            throw invalid("escalation.triggeredAt", "must be non-negative", escalation.getTriggeredAt());
        }

        requireFinite(escalation.getSeverityMultiplier(), "escalation.severityMultiplier");
        if (escalation.getSeverityMultiplier() < 0.0d) {
            throw invalid(
                    "escalation.severityMultiplier",
                    "must be non-negative",
                    escalation.getSeverityMultiplier()
            );
        }

        validateRelatedEventTypes(escalation.getRelatedEventTypes(), "escalation.relatedEventTypes");
    }

    public void validateScorePointRequest(UploadScorePointRequest scorePoint) {
        requireNotNull(scorePoint, "scorePoint");
        requireNotNull(scorePoint.getTimestamp(), "scorePoint.timestamp");
        requireNotNull(scorePoint.getScoreValue(), "scorePoint.scoreValue");

        if (scorePoint.getTimestamp() < 0L) {
            throw invalid("scorePoint.timestamp", "must be non-negative", scorePoint.getTimestamp());
        }

        requireFinite(scorePoint.getScoreValue(), "scorePoint.scoreValue");
    }

    public void validateEventRequests(List<UploadEventRequest> events) {
        requireNotNull(events, "events");
        for (int index = 0; index < events.size(); index++) {
            UploadEventRequest event = events.get(index);
            requireNotNull(event, "events[" + index + "]");
            validateIndexedEventRequest(event, index);
        }
    }

    public void validateEscalationRequests(List<UploadEscalationRequest> escalations) {
        requireNotNull(escalations, "escalations");
        for (int index = 0; index < escalations.size(); index++) {
            UploadEscalationRequest escalation = escalations.get(index);
            requireNotNull(escalation, "escalations[" + index + "]");
            validateIndexedEscalationRequest(escalation, index);
        }
    }

    public void validateScorePointRequests(List<UploadScorePointRequest> scorePoints) {
        requireNotNull(scorePoints, "scorePoints");
        for (int index = 0; index < scorePoints.size(); index++) {
            UploadScorePointRequest scorePoint = scorePoints.get(index);
            requireNotNull(scorePoint, "scorePoints[" + index + "]");
            validateIndexedScorePointRequest(scorePoint, index);
        }
    }

    public void validateNonEmptySessionPayload(UploadSessionRequest request) {
        requireNotNull(request, "request");
        requireNotNull(request.getStartTimestamp(), "startTimestamp");
        requireNotNull(request.getEndTimestamp(), "endTimestamp");
        requireNotNull(request.getFinalScore(), "finalScore");
        requireNotNull(request.getTotalContinuousPenalty(), "totalContinuousPenalty");
        requireNotNull(request.getTotalEventPenalty(), "totalEventPenalty");
        requireNotNull(request.getTotalEscalationPenalty(), "totalEscalationPenalty");
        requireNotNull(request.getTotalDurationSeconds(), "totalDurationSeconds");
        requireNotNull(request.getTotalDistanceKm(), "totalDistanceKm");
        requireNotNull(request.getTotalEventCount(), "totalEventCount");
        requireNotNull(request.getTotalEscalationCount(), "totalEscalationCount");
        requireNotNull(request.getEvents(), "events");
        requireNotNull(request.getEscalations(), "escalations");
        requireNotNull(request.getScorePoints(), "scorePoints");
    }

    public void validateDeclaredCountsMatchPayload(UploadSessionRequest request) {
        requireNotNull(request, "request");
        requireNotNull(request.getTotalEventCount(), "totalEventCount");
        requireNotNull(request.getEvents(), "events");
        requireNotNull(request.getTotalEscalationCount(), "totalEscalationCount");
        requireNotNull(request.getEscalations(), "escalations");

        if (request.getTotalEventCount() != request.getEvents().size()) {
            throw invalid(
                    "totalEventCount",
                    "must match events.size",
                    request.getTotalEventCount()
            );
        }

        if (request.getTotalEscalationCount() != request.getEscalations().size()) {
            throw invalid(
                    "totalEscalationCount",
                    "must match escalations.size",
                    request.getTotalEscalationCount()
            );
        }
    }

    public void validateUniqueChildIdentifiers(UploadSessionRequest request) {
        requireNotNull(request, "request");
        requireUniqueEventIds(request.getEvents());
        requireUniqueEscalationIds(request.getEscalations());
    }

    private void validateIndexedEventRequest(UploadEventRequest event, int index) {
        String prefix = "events[" + index + "]";

        requireNotBlank(event.getEventId(), prefix + ".eventId");
        requireNotNull(event.getEventType(), prefix + ".eventType");
        requireNotNull(event.getTimestamp(), prefix + ".timestamp");
        requireNotNull(event.getSeverity(), prefix + ".severity");

        if (event.getTimestamp() < 0L) {
            throw invalid(prefix + ".timestamp", "must be non-negative", event.getTimestamp());
        }

        requireFinite(event.getSeverity(), prefix + ".severity");
        if (event.getSeverity() < 0.0d) {
            throw invalid(prefix + ".severity", "must be non-negative", event.getSeverity());
        }
    }

    private void validateIndexedEscalationRequest(UploadEscalationRequest escalation, int index) {
        String prefix = "escalations[" + index + "]";

        requireNotBlank(escalation.getEscalationId(), prefix + ".escalationId");
        requireNotNull(escalation.getEscalationType(), prefix + ".escalationType");
        requireNotNull(escalation.getTriggeredAt(), prefix + ".triggeredAt");
        requireNotNull(escalation.getSeverityMultiplier(), prefix + ".severityMultiplier");
        requireNotNull(escalation.getRelatedEventTypes(), prefix + ".relatedEventTypes");

        if (escalation.getTriggeredAt() < 0L) {
            throw invalid(prefix + ".triggeredAt", "must be non-negative", escalation.getTriggeredAt());
        }

        requireFinite(escalation.getSeverityMultiplier(), prefix + ".severityMultiplier");
        if (escalation.getSeverityMultiplier() < 0.0d) {
            throw invalid(
                    prefix + ".severityMultiplier",
                    "must be non-negative",
                    escalation.getSeverityMultiplier()
            );
        }

        validateRelatedEventTypes(escalation.getRelatedEventTypes(), prefix + ".relatedEventTypes");
    }

    private void validateIndexedScorePointRequest(UploadScorePointRequest scorePoint, int index) {
        String prefix = "scorePoints[" + index + "]";

        requireNotNull(scorePoint.getTimestamp(), prefix + ".timestamp");
        requireNotNull(scorePoint.getScoreValue(), prefix + ".scoreValue");

        if (scorePoint.getTimestamp() < 0L) {
            throw invalid(prefix + ".timestamp", "must be non-negative", scorePoint.getTimestamp());
        }

        requireFinite(scorePoint.getScoreValue(), prefix + ".scoreValue");
    }

    private void validateRelatedEventTypes(List<EventType> relatedEventTypes, String fieldName) {
        for (int index = 0; index < relatedEventTypes.size(); index++) {
            if (relatedEventTypes.get(index) == null) {
                throw invalid(fieldName + "[" + index + "]", "must not be null", null);
            }
        }
    }

    private void requireUniqueEventIds(List<UploadEventRequest> events) {
        requireNotNull(events, "events");
        Set<String> seenEventIds = new HashSet<>();
        for (int index = 0; index < events.size(); index++) {
            UploadEventRequest event = events.get(index);
            requireNotNull(event, "events[" + index + "]");
            String eventId = event.getEventId();
            requireNotBlank(eventId, "events[" + index + "].eventId");
            if (!seenEventIds.add(eventId)) {
                throw invalid("events[" + index + "].eventId", "must be unique within the payload", eventId);
            }
        }
    }

    private void requireUniqueEscalationIds(List<UploadEscalationRequest> escalations) {
        requireNotNull(escalations, "escalations");
        Set<String> seenEscalationIds = new HashSet<>();
        for (int index = 0; index < escalations.size(); index++) {
            UploadEscalationRequest escalation = escalations.get(index);
            requireNotNull(escalation, "escalations[" + index + "]");
            String escalationId = escalation.getEscalationId();
            requireNotBlank(escalationId, "escalations[" + index + "].escalationId");
            if (!seenEscalationIds.add(escalationId)) {
                throw invalid(
                        "escalations[" + index + "].escalationId",
                        "must be unique within the payload",
                        escalationId
                );
            }
        }
    }

    private void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw invalid(fieldName, "must be provided", null);
        }
    }

    private void requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw invalid(fieldName, "must not be blank", value);
        }
    }

    private void requireFinite(double value, String fieldName) {
        if (!Double.isFinite(value)) {
            throw invalid(fieldName, "must be a finite number", value);
        }
    }

    HardValidationException invalid(String fieldName, String message, Object rejectedValue) {
        return new HardValidationException(
                fieldName + " " + message,
                ERROR_CODE,
                fieldName,
                rejectedValue
        );
    }
}
