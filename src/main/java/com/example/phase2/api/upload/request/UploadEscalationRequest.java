package com.example.phase2.api.upload.request;

import java.util.List;

import com.example.phase2.domain.enums.EscalationType;
import com.example.phase2.domain.enums.EventType;

public class UploadEscalationRequest {

    private String escalationId;
    private EscalationType escalationType;
    private Long triggeredAt;
    private List<EventType> relatedEventTypes;
    private Double severityMultiplier;

    public UploadEscalationRequest() {
    }

    public UploadEscalationRequest(
            String escalationId,
            EscalationType escalationType,
            Long triggeredAt,
            List<EventType> relatedEventTypes,
            Double severityMultiplier
    ) {
        this.escalationId = escalationId;
        this.escalationType = escalationType;
        this.triggeredAt = triggeredAt;
        this.relatedEventTypes = relatedEventTypes;
        this.severityMultiplier = severityMultiplier;
    }

    public String getEscalationId() {
        return escalationId;
    }

    public void setEscalationId(String escalationId) {
        this.escalationId = escalationId;
    }

    public EscalationType getEscalationType() {
        return escalationType;
    }

    public void setEscalationType(EscalationType escalationType) {
        this.escalationType = escalationType;
    }

    public Long getTriggeredAt() {
        return triggeredAt;
    }

    public void setTriggeredAt(Long triggeredAt) {
        this.triggeredAt = triggeredAt;
    }

    public List<EventType> getRelatedEventTypes() {
        return relatedEventTypes;
    }

    public void setRelatedEventTypes(List<EventType> relatedEventTypes) {
        this.relatedEventTypes = relatedEventTypes;
    }

    public Double getSeverityMultiplier() {
        return severityMultiplier;
    }

    public void setSeverityMultiplier(Double severityMultiplier) {
        this.severityMultiplier = severityMultiplier;
    }
}
