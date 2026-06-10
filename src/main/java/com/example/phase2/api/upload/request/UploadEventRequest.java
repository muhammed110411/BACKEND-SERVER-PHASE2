package com.example.phase2.api.upload.request;

import com.example.phase2.domain.enums.EventType;

public class UploadEventRequest {

    private String eventId;
    private EventType eventType;
    private Long timestamp;
    private Double severity;

    public UploadEventRequest() {
    }

    public UploadEventRequest(
            String eventId,
            EventType eventType,
            Long timestamp,
            Double severity
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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getSeverity() {
        return severity;
    }

    public void setSeverity(Double severity) {
        this.severity = severity;
    }
}
