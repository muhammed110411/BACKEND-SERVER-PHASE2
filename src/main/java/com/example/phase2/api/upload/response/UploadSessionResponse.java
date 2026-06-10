package com.example.phase2.api.upload.response;

import java.util.List;

import com.example.phase2.domain.enums.UploadProcessingStatus;
import com.example.phase2.domain.model.IngestionAnomalyFlag;

public class UploadSessionResponse {

    private String sessionId;
    private UploadProcessingStatus uploadProcessingStatus;
    private long uploadedAt;
    private Long processedAt;
    private List<IngestionAnomalyFlag> anomalyFlags;

    public UploadSessionResponse() {
    }

    public UploadSessionResponse(
            String sessionId,
            UploadProcessingStatus uploadProcessingStatus,
            long uploadedAt,
            Long processedAt,
            List<IngestionAnomalyFlag> anomalyFlags
    ) {
        this.sessionId = sessionId;
        this.uploadProcessingStatus = uploadProcessingStatus;
        this.uploadedAt = uploadedAt;
        this.processedAt = processedAt;
        this.anomalyFlags = anomalyFlags;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public UploadProcessingStatus getUploadProcessingStatus() {
        return uploadProcessingStatus;
    }

    public void setUploadProcessingStatus(UploadProcessingStatus uploadProcessingStatus) {
        this.uploadProcessingStatus = uploadProcessingStatus;
    }

    public long getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(long uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Long getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Long processedAt) {
        this.processedAt = processedAt;
    }

    public List<IngestionAnomalyFlag> getAnomalyFlags() {
        return anomalyFlags;
    }

    public void setAnomalyFlags(List<IngestionAnomalyFlag> anomalyFlags) {
        this.anomalyFlags = anomalyFlags;
    }
}
