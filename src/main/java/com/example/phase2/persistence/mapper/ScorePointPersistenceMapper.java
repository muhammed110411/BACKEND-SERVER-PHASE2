package com.example.phase2.persistence.mapper;

import com.example.phase2.domain.model.ScorePointRecord;
import com.example.phase2.persistence.entity.ScorePointEntity;
import org.springframework.stereotype.Component;

@Component
public class ScorePointPersistenceMapper {

    public ScorePointRecord toDomain(ScorePointEntity entity) {
        if (entity == null) {
            return null;
        }

        return new ScorePointRecord(
                entity.getSessionId(),
                entity.getTimestamp(),
                entity.getScoreValue()
        );
    }

    public ScorePointEntity toEntity(ScorePointRecord scorePoint) {
        if (scorePoint == null) {
            return null;
        }

        return new ScorePointEntity(
                null,
                scorePoint.getSessionId(),
                scorePoint.getTimestamp(),
                scorePoint.getScoreValue()
        );
    }
}
