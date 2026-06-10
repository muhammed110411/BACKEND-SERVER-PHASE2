package com.example.phase2.application.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.phase2.api.upload.request.UploadEscalationRequest;
import com.example.phase2.api.upload.request.UploadEventRequest;
import com.example.phase2.api.upload.request.UploadScorePointRequest;
import com.example.phase2.api.upload.request.UploadSessionRequest;
import com.example.phase2.domain.enums.UploadProcessingStatus;
import com.example.phase2.domain.model.DrivingSessionRecord;
import com.example.phase2.domain.model.EscalationRecord;
import com.example.phase2.domain.model.IngestionAnomalyFlag;
import com.example.phase2.domain.model.SafetyEventRecord;
import com.example.phase2.domain.model.ScorePointRecord;
import com.example.phase2.domain.model.SessionDetailRecord;

@Component
public class UploadRequestToDomainMapper {

    public DrivingSessionRecord toDrivingSessionRecord(
            UploadSessionRequest request,
            UploadProcessingStatus uploadProcessingStatus,
            long uploadedAt,
            Long processedAt,
            String sourceClientId
    ) {
        if (request == null) {
            return null;
        }

        return new DrivingSessionRecord(
                request.getSessionId(),
                request.getDriverId(),
                request.getVehicleId(),
                request.getStartTimestamp(),
                request.getEndTimestamp(),
                request.getSessionEndStatus(),
                request.getSessionValidity(),
                uploadProcessingStatus,
                request.getFinalScore(),
                request.getTotalContinuousPenalty(),
                request.getTotalEventPenalty(),
                request.getTotalEscalationPenalty(),
                request.getTotalDurationSeconds(),
                request.getTotalDistanceKm(),
                request.getTotalEventCount(),
                request.getTotalEscalationCount(),
                uploadedAt,
                processedAt,
                sourceClientId
        );
    }

    public List<SafetyEventRecord> toSafetyEventRecords(UploadSessionRequest request) {
        if (request == null || request.getEvents() == null) {
            return List.of();
        }

        String sessionId = request.getSessionId();
        return request.getEvents()
                .stream()
                .map(event -> toSafetyEventRecord(sessionId, event))
                .toList();
    }

    public List<EscalationRecord> toEscalationRecords(UploadSessionRequest request) {
        if (request == null || request.getEscalations() == null) {
            return List.of();
        }

        String sessionId = request.getSessionId();
        return request.getEscalations()
                .stream()
                .map(escalation -> toEscalationRecord(sessionId, escalation))
                .toList();
    }

    public List<ScorePointRecord> toScorePointRecords(UploadSessionRequest request) {
        if (request == null || request.getScorePoints() == null) {
            return List.of();
        }

        String sessionId = request.getSessionId();
        return request.getScorePoints()
                .stream()
                .map(scorePoint -> toScorePointRecord(sessionId, scorePoint))
                .toList();
    }

    public SessionDetailRecord toSessionDetailRecord(
            DrivingSessionRecord session,
            List<SafetyEventRecord> events,
            List<EscalationRecord> escalations,
            List<ScorePointRecord> scoreHistory,
            List<IngestionAnomalyFlag> ingestionAnomalyFlags
    ) {
        if (session == null) {
            return null;
        }

        return new SessionDetailRecord(
                session,
                events,
                escalations,
                scoreHistory,
                ingestionAnomalyFlags
        );
    }

    private SafetyEventRecord toSafetyEventRecord(String sessionId, UploadEventRequest event) {
        return new SafetyEventRecord(
                event.getEventId(),
                sessionId,
                event.getEventType(),
                event.getTimestamp(),
                event.getSeverity()
        );
    }

    private EscalationRecord toEscalationRecord(String sessionId, UploadEscalationRequest escalation) {
        return new EscalationRecord(
                escalation.getEscalationId(),
                sessionId,
                escalation.getEscalationType(),
                escalation.getTriggeredAt(),
                escalation.getRelatedEventTypes(),
                escalation.getSeverityMultiplier()
        );
    }

    private ScorePointRecord toScorePointRecord(String sessionId, UploadScorePointRequest scorePoint) {
        return new ScorePointRecord(
                sessionId,
                scorePoint.getTimestamp(),
                scorePoint.getScoreValue()
        );
    }
}
