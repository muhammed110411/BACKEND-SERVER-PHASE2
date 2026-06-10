package com.example.phase2.persistence.mapper;

import com.example.phase2.domain.model.DrivingSessionRecord;
import com.example.phase2.persistence.entity.SessionEntity;
import org.springframework.stereotype.Component;

@Component
public class SessionPersistenceMapper {

    public DrivingSessionRecord toDomain(SessionEntity entity) {
        if (entity == null) {
            return null;
        }

        return new DrivingSessionRecord(
                entity.getId(),
                entity.getDriverId(),
                entity.getVehicleId(),
                entity.getStartTimestamp(),
                entity.getEndTimestamp(),
                entity.getStatus(),
                entity.getValidity(),
                entity.getUploadProcessingStatus(),
                entity.getFinalScore(),
                entity.getTotalContinuousPenalty(),
                entity.getTotalEventPenalty(),
                entity.getTotalEscalationPenalty(),
                entity.getTotalDurationSeconds(),
                entity.getTotalDistanceKm(),
                entity.getTotalEventCount(),
                entity.getTotalEscalationCount(),
                entity.getUploadedAt(),
                entity.getProcessedAt(),
                entity.getSourceClientId()
        );
    }

    public SessionEntity toEntity(DrivingSessionRecord session) {
        if (session == null) {
            return null;
        }

        return new SessionEntity(
                session.getId(),
                session.getDriverId(),
                session.getVehicleId(),
                session.getStartTimestamp(),
                session.getEndTimestamp(),
                session.getStatus(),
                session.getValidity(),
                session.getUploadProcessingStatus(),
                session.getFinalScore(),
                session.getTotalContinuousPenalty(),
                session.getTotalEventPenalty(),
                session.getTotalEscalationPenalty(),
                session.getTotalDurationSeconds(),
                session.getTotalDistanceKm(),
                session.getTotalEventCount(),
                session.getTotalEscalationCount(),
                session.getUploadedAt(),
                session.getProcessedAt(),
                session.getSourceClientId()
        );
    }
}
