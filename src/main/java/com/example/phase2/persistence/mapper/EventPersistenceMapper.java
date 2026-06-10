package com.example.phase2.persistence.mapper;

import com.example.phase2.domain.model.SafetyEventRecord;
import com.example.phase2.persistence.entity.EventEntity;
import org.springframework.stereotype.Component;

@Component
public class EventPersistenceMapper {

    public SafetyEventRecord toDomain(EventEntity entity) {
        if (entity == null) {
            return null;
        }

        return new SafetyEventRecord(
                entity.getId(),
                entity.getSessionId(),
                entity.getEventType(),
                entity.getTimestamp(),
                entity.getSeverity()
        );
    }

    public EventEntity toEntity(SafetyEventRecord event) {
        if (event == null) {
            return null;
        }

        return new EventEntity(
                event.getId(),
                event.getSessionId(),
                event.getEventType(),
                event.getTimestamp(),
                event.getSeverity()
        );
    }
}
