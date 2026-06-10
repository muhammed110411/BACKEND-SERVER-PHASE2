package com.example.phase2.persistence.mapper;

import com.example.phase2.domain.model.IngestionAnomalyFlag;
import com.example.phase2.persistence.entity.SessionIngestionIssueEntity;
import org.springframework.stereotype.Component;

@Component
public class SessionIngestionIssuePersistenceMapper {

    public IngestionAnomalyFlag toDomain(SessionIngestionIssueEntity entity) {
        if (entity == null) {
            return null;
        }

        return new IngestionAnomalyFlag(
                entity.getCode(),
                entity.getMessage(),
                entity.getSeverity(),
                entity.getFieldName(),
                entity.getDetectedAt(),
                entity.isBlocksProcessing()
        );
    }

    public SessionIngestionIssueEntity toEntity(String sessionId, IngestionAnomalyFlag anomalyFlag) {
        if (anomalyFlag == null) {
            return null;
        }

        return new SessionIngestionIssueEntity(
                null,
                sessionId,
                anomalyFlag.getCode(),
                anomalyFlag.getMessage(),
                anomalyFlag.getSeverity(),
                anomalyFlag.getFieldName(),
                anomalyFlag.getDetectedAt(),
                anomalyFlag.isBlocking()
        );
    }
}
