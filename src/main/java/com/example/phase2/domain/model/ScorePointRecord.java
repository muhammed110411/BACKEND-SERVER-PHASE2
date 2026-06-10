package com.example.phase2.domain.model;

import java.util.Objects;

public class ScorePointRecord {

    private final String sessionId;
    private final long timestamp;
    private final double scoreValue;

    public ScorePointRecord(String sessionId, long timestamp, double scoreValue) {
        this.sessionId = requireText(sessionId, "sessionId");
        this.timestamp = timestamp;
        this.scoreValue = scoreValue;
    }

    public String getSessionId() {
        return sessionId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getScoreValue() {
        return scoreValue;
    }

    public boolean belongsToSession(String sessionId) {
        return this.sessionId.equals(requireText(sessionId, "sessionId"));
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
