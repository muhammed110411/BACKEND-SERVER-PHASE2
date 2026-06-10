package com.example.phase2.persistence.mapper;

import com.example.phase2.domain.enums.EventType;
import com.example.phase2.domain.model.EscalationRecord;
import com.example.phase2.persistence.entity.EscalationEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class EscalationPersistenceMapper {

    public EscalationRecord toDomain(EscalationEntity entity) {
        if (entity == null) {
            return null;
        }

        return new EscalationRecord(
                entity.getId(),
                entity.getSessionId(),
                entity.getEscalationType(),
                entity.getTriggeredAt(),
                deserializeRelatedEventTypes(entity.getRelatedEventTypes()),
                entity.getSeverityMultiplier()
        );
    }

    public EscalationEntity toEntity(EscalationRecord escalation) {
        if (escalation == null) {
            return null;
        }

        return new EscalationEntity(
                escalation.getId(),
                escalation.getSessionId(),
                escalation.getEscalationType(),
                escalation.getTriggeredAt(),
                serializeRelatedEventTypes(escalation.getRelatedEventTypes()),
                escalation.getSeverityMultiplier()
        );
    }

    private List<EventType> deserializeRelatedEventTypes(String serializedRelatedEventTypes) {
        if (serializedRelatedEventTypes.isEmpty()) {
            return List.of();
        }

        String[] tokens = serializedRelatedEventTypes.split(",", -1);
        List<EventType> relatedEventTypes = new ArrayList<>(tokens.length);
        for (String token : tokens) {
            try {
                relatedEventTypes.add(EventType.valueOf(token));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException(
                        "Invalid relatedEventTypes token: " + token,
                        ex
                );
            }
        }
        return relatedEventTypes;
    }

    private String serializeRelatedEventTypes(List<EventType> relatedEventTypes) {
        return relatedEventTypes.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }
}
