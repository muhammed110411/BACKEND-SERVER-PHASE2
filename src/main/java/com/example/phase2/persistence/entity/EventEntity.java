package com.example.phase2.persistence.entity;

import com.example.phase2.domain.enums.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "events")
public class EventEntity {

    @Id
    @Column(nullable = false, updatable = false, length = 128)
    private String id;

    @Column(nullable = false, length = 128)
    private String sessionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 64)
    private EventType eventType;

    @Column(nullable = false)
    private long timestamp;

    @Column(nullable = false)
    private double severity;

    protected EventEntity() {
    }

    public EventEntity(
            String id,
            String sessionId,
            EventType eventType,
            long timestamp,
            double severity
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId must not be null");
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

    public void setSessionId(String sessionId) {
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId must not be null");
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = Objects.requireNonNull(eventType, "eventType must not be null");
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getSeverity() {
        return severity;
    }

    public void setSeverity(double severity) {
        this.severity = severity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventEntity that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
