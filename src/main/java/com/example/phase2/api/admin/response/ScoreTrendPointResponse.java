package com.example.phase2.api.admin.response;

public class ScoreTrendPointResponse {

    private long timestamp;
    private double averageScore;
    private int sessionCount;
    private String sessionId;
    private String driverId;
    private String driverName;

    public ScoreTrendPointResponse() {
    }

    public ScoreTrendPointResponse(long timestamp, double averageScore, int sessionCount) {
        this.timestamp = timestamp;
        this.averageScore = averageScore;
        this.sessionCount = sessionCount;
    }

    public ScoreTrendPointResponse(
            long timestamp,
            double averageScore,
            int sessionCount,
            String sessionId,
            String driverId,
            String driverName
    ) {
        this(timestamp, averageScore, sessionCount);
        this.sessionId = sessionId;
        this.driverId = driverId;
        this.driverName = driverName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public int getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(int sessionCount) {
        this.sessionCount = sessionCount;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}
