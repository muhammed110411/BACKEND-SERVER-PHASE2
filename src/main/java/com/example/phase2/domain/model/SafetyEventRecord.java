package com.example.phase2.domain.model;

import com.example.phase2.domain.enums.EventType;

import java.util.Objects;

public class SafetyEventRecord {

    private final String id;
    private final String sessionId;
    private final EventType eventType;
    private final long timestamp;
    private final double severity;

    public SafetyEventRecord(
            String id,
            String sessionId,
            EventType eventType,
            long timestamp,
            double severity
    ) {
        this.id = requireText(id, "id");
        this.sessionId = requireText(sessionId, "sessionId");
        this.eventType = Objects.requireNonNull(eventType, "eventType must not be null");
        this.timestamp = timestamp;
        this.severity = severity;
    }

    public String getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getSeverity() {
        return severity;
    }

    public boolean isSevere(double threshold) {
        return severity >= threshold;
    }

    public boolean belongsToSession(String sessionId) {
        return this.sessionId.equals(sessionId);
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
