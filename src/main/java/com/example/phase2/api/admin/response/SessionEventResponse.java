package com.example.phase2.api.admin.response;

import com.example.phase2.domain.enums.EventType;

public class SessionEventResponse {

    private String eventId;
    private EventType eventType;
    private long timestamp;
    private double severity;

    public SessionEventResponse() {
    }

    public SessionEventResponse(
            String eventId,
            EventType eventType,
            long timestamp,
            double severity
    ) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.severity = severity;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
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
}
