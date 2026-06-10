package com.example.phase2.api.admin.response;

public class SessionScorePointResponse {

    private long timestamp;
    private double scoreValue;

    public SessionScorePointResponse() {
    }

    public SessionScorePointResponse(long timestamp, double scoreValue) {
        this.timestamp = timestamp;
        this.scoreValue = scoreValue;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(double scoreValue) {
        this.scoreValue = scoreValue;
    }
}
