package com.example.phase2.api.upload.request;

public class UploadScorePointRequest {

    private Long timestamp;
    private Double scoreValue;

    public UploadScorePointRequest() {
    }

    public UploadScorePointRequest(Long timestamp, Double scoreValue) {
        this.timestamp = timestamp;
        this.scoreValue = scoreValue;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(Double scoreValue) {
        this.scoreValue = scoreValue;
    }
}
