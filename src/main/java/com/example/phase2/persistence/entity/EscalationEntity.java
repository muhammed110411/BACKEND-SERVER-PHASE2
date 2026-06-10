package com.example.phase2.persistence.entity;

import com.example.phase2.domain.enums.EscalationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "escalations")
public class EscalationEntity {

    @Id
    @Column(nullable = false, updatable = false, length = 128)
    private String id;

    @Column(nullable = false, length = 128)
    private String sessionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 64)
    private EscalationType escalationType;

    @Column(nullable = false)
    private long triggeredAt;

    @Column(nullable = false, length = 512)
    private String relatedEventTypes;

    @Column(nullable = false)
    private double severityMultiplier;

    protected EscalationEntity() {
    }

    public EscalationEntity(
            String id,
            String sessionId,
            EscalationType escalationType,
            long triggeredAt,
            String relatedEventTypes,
            double severityMultiplier
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId must not be null");
        this.escalationType = Objects.requireNonNull(escalationType, "escalationType must not be null");
        this.triggeredAt = triggeredAt;
        this.relatedEventTypes = Objects.requireNonNull(relatedEventTypes, "relatedEventTypes must not be null");
        this.severityMultiplier = severityMultiplier;
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

    public EscalationType getEscalationType() {
        return escalationType;
    }

    public void setEscalationType(EscalationType escalationType) {
        this.escalationType = Objects.requireNonNull(escalationType, "escalationType must not be null");
    }

    public long getTriggeredAt() {
        return triggeredAt;
    }

    public void setTriggeredAt(long triggeredAt) {
        this.triggeredAt = triggeredAt;
    }

    public String getRelatedEventTypes() {
        return relatedEventTypes;
    }

    public void setRelatedEventTypes(String relatedEventTypes) {
        this.relatedEventTypes = Objects.requireNonNull(relatedEventTypes, "relatedEventTypes must not be null");
    }

    public double getSeverityMultiplier() {
        return severityMultiplier;
    }

    public void setSeverityMultiplier(double severityMultiplier) {
        this.severityMultiplier = severityMultiplier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EscalationEntity that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
