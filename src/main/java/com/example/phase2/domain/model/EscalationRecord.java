package com.example.phase2.domain.model;

import com.example.phase2.domain.enums.EscalationType;
import com.example.phase2.domain.enums.EventType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EscalationRecord {

    private final String id;
    private final String sessionId;
    private final EscalationType escalationType;
    private final long triggeredAt;
    private final List<EventType> relatedEventTypes;
    private final double severityMultiplier;

    public EscalationRecord(
            String id,
            String sessionId,
            EscalationType escalationType,
            long triggeredAt,
            List<EventType> relatedEventTypes,
            double severityMultiplier
    ) {
        this.id = requireText(id, "id");
        this.sessionId = requireText(sessionId, "sessionId");
        this.escalationType = Objects.requireNonNull(escalationType, "escalationType must not be null");
        this.triggeredAt = triggeredAt;
        this.relatedEventTypes = Collections.unmodifiableList(requireEventTypes(relatedEventTypes));
        this.severityMultiplier = severityMultiplier;
    }

    public String getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public EscalationType getEscalationType() {
        return escalationType;
    }

    public long getTriggeredAt() {
        return triggeredAt;
    }

    public List<EventType> getRelatedEventTypes() {
        return relatedEventTypes;
    }

    public double getSeverityMultiplier() {
        return severityMultiplier;
    }

    public boolean involves(EventType eventType) {
        return relatedEventTypes.contains(Objects.requireNonNull(eventType, "eventType must not be null"));
    }

    public boolean belongsToSession(String sessionId) {
        return this.sessionId.equals(requireText(sessionId, "sessionId"));
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

    private static List<EventType> requireEventTypes(List<EventType> relatedEventTypes) {
        Objects.requireNonNull(relatedEventTypes, "relatedEventTypes must not be null");
        return new ArrayList<>(relatedEventTypes);
    }
}
