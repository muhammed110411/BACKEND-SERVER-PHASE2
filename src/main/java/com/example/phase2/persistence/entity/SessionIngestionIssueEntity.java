package com.example.phase2.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "session_ingestion_issues")
public class SessionIngestionIssueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, length = 128)
    private String sessionId;

    @Column(nullable = false, length = 128)
    private String code;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false, length = 32)
    private String severity;

    @Column
    private String fieldName;

    @Column(nullable = false)
    private long detectedAt;

    @Column(nullable = false)
    private boolean blocksProcessing;

    protected SessionIngestionIssueEntity() {
    }

    public SessionIngestionIssueEntity(
            Long id,
            String sessionId,
            String code,
            String message,
            String severity,
            String fieldName,
            long detectedAt,
            boolean blocksProcessing
    ) {
        this.id = id;
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId must not be null");
        this.code = Objects.requireNonNull(code, "code must not be null");
        this.message = Objects.requireNonNull(message, "message must not be null");
        this.severity = Objects.requireNonNull(severity, "severity must not be null");
        this.fieldName = fieldName;
        this.detectedAt = detectedAt;
        this.blocksProcessing = blocksProcessing;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId must not be null");
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = Objects.requireNonNull(code, "code must not be null");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = Objects.requireNonNull(message, "message must not be null");
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = Objects.requireNonNull(severity, "severity must not be null");
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public long getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(long detectedAt) {
        this.detectedAt = detectedAt;
    }

    public boolean isBlocksProcessing() {
        return blocksProcessing;
    }

    public void setBlocksProcessing(boolean blocksProcessing) {
        this.blocksProcessing = blocksProcessing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SessionIngestionIssueEntity that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
