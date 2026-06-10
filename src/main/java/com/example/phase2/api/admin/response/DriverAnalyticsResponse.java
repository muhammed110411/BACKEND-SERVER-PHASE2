package com.example.phase2.api.admin.response;

import java.util.List;

public class DriverAnalyticsResponse {

    private String driverId;
    private double longTermReliabilityScore;
    private int totalSessions;
    private double totalDistanceKm;
    private double averageFinalScore;
    private int validSessionCount;
    private int invalidSessionCount;
    private int completedSessionCount;
    private int abortedSessionCount;
    private int totalEventCount;
    private int totalEscalationCount;
    private List<ScoreTrendPointResponse> scoreTrend;
    private List<EventCountResponse> eventCounts;
    private List<EscalationCountResponse> escalationCounts;

    public DriverAnalyticsResponse() {
    }

    public DriverAnalyticsResponse(
            String driverId,
            double longTermReliabilityScore,
            int totalSessions,
            double totalDistanceKm,
            double averageFinalScore,
            int validSessionCount,
            int invalidSessionCount,
            int completedSessionCount,
            int abortedSessionCount,
            int totalEventCount,
            int totalEscalationCount,
            List<ScoreTrendPointResponse> scoreTrend,
            List<EventCountResponse> eventCounts,
            List<EscalationCountResponse> escalationCounts
    ) {
        this.driverId = driverId;
        this.longTermReliabilityScore = longTermReliabilityScore;
        this.totalSessions = totalSessions;
        this.totalDistanceKm = totalDistanceKm;
        this.averageFinalScore = averageFinalScore;
        this.validSessionCount = validSessionCount;
        this.invalidSessionCount = invalidSessionCount;
        this.completedSessionCount = completedSessionCount;
        this.abortedSessionCount = abortedSessionCount;
        this.totalEventCount = totalEventCount;
        this.totalEscalationCount = totalEscalationCount;
        this.scoreTrend = scoreTrend;
        this.eventCounts = eventCounts;
        this.escalationCounts = escalationCounts;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public double getLongTermReliabilityScore() {
        return longTermReliabilityScore;
    }

    public void setLongTermReliabilityScore(double longTermReliabilityScore) {
        this.longTermReliabilityScore = longTermReliabilityScore;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    public double getAverageFinalScore() {
        return averageFinalScore;
    }

    public void setAverageFinalScore(double averageFinalScore) {
        this.averageFinalScore = averageFinalScore;
    }

    public int getValidSessionCount() {
        return validSessionCount;
    }

    public void setValidSessionCount(int validSessionCount) {
        this.validSessionCount = validSessionCount;
    }

    public int getInvalidSessionCount() {
        return invalidSessionCount;
    }

    public void setInvalidSessionCount(int invalidSessionCount) {
        this.invalidSessionCount = invalidSessionCount;
    }

    public int getCompletedSessionCount() {
        return completedSessionCount;
    }

    public void setCompletedSessionCount(int completedSessionCount) {
        this.completedSessionCount = completedSessionCount;
    }

    public int getAbortedSessionCount() {
        return abortedSessionCount;
    }

    public void setAbortedSessionCount(int abortedSessionCount) {
        this.abortedSessionCount = abortedSessionCount;
    }

    public int getTotalEventCount() {
        return totalEventCount;
    }

    public void setTotalEventCount(int totalEventCount) {
        this.totalEventCount = totalEventCount;
    }

    public int getTotalEscalationCount() {
        return totalEscalationCount;
    }

    public void setTotalEscalationCount(int totalEscalationCount) {
        this.totalEscalationCount = totalEscalationCount;
    }

    public List<ScoreTrendPointResponse> getScoreTrend() {
        return scoreTrend;
    }

    public void setScoreTrend(List<ScoreTrendPointResponse> scoreTrend) {
        this.scoreTrend = scoreTrend;
    }

    public List<EventCountResponse> getEventCounts() {
        return eventCounts;
    }

    public void setEventCounts(List<EventCountResponse> eventCounts) {
        this.eventCounts = eventCounts;
    }

    public List<EscalationCountResponse> getEscalationCounts() {
        return escalationCounts;
    }

    public void setEscalationCounts(List<EscalationCountResponse> escalationCounts) {
        this.escalationCounts = escalationCounts;
    }
}
