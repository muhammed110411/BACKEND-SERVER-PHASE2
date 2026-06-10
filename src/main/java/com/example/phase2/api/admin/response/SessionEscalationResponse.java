package com.example.phase2.api.admin.response;

import com.example.phase2.domain.enums.EscalationType;
import com.example.phase2.domain.enums.EventType;

import java.util.List;

public class SessionEscalationResponse {

    private String escalationId;
    private EscalationType escalationType;
    private long triggeredAt;
    private List<EventType> relatedEventTypes;
    private double severityMultiplier;

    public SessionEscalationResponse() {
    }

    public SessionEscalationResponse(
            String escalationId,
            EscalationType escalationType,
            long triggeredAt,
            List<EventType> relatedEventTypes,
            double severityMultiplier
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

    public long getTriggeredAt() {
        return triggeredAt;
    }

    public void setTriggeredAt(long triggeredAt) {
        this.triggeredAt = triggeredAt;
    }

    public List<EventType> getRelatedEventTypes() {
        return relatedEventTypes;
    }

    public void setRelatedEventTypes(List<EventType> relatedEventTypes) {
        this.relatedEventTypes = relatedEventTypes;
    }

    public double getSeverityMultiplier() {
        return severityMultiplier;
    }

    public void setSeverityMultiplier(double severityMultiplier) {
        this.severityMultiplier = severityMultiplier;
    }
}
