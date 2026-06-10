package com.example.phase2.api.admin.response;

import java.util.List;

public class GlobalAnalyticsResponse {

    private int totalDrivers;
    private int activeDriverCount;
    private int inactiveDriverCount;
    private int suspendedDriverCount;
    private int deactivatedDriverCount;
    private int totalVehicles;
    private int availableVehicleCount;
    private int busyVehicleCount;
    private int offlineVehicleCount;
    private int totalSessions;
    private int validSessionCount;
    private int invalidSessionCount;
    private int completedSessionCount;
    private int abortedSessionCount;
    private int processedSessionCount;
    private int processedWithWarningsSessionCount;
    private int rejectedSessionCount;
    private double averageFinalScore;
    private double totalDistanceKm;
    private int totalEventCount;
    private int totalEscalationCount;
    private List<ScoreTrendPointResponse> scoreTrend;
    private List<EventCountResponse> eventCounts;
    private List<EscalationCountResponse> escalationCounts;

    public GlobalAnalyticsResponse() {
    }

    public GlobalAnalyticsResponse(
            int totalDrivers,
            int activeDriverCount,
            int inactiveDriverCount,
            int suspendedDriverCount,
            int deactivatedDriverCount,
            int totalVehicles,
            int availableVehicleCount,
            int busyVehicleCount,
            int offlineVehicleCount,
            int totalSessions,
            int validSessionCount,
            int invalidSessionCount,
            int completedSessionCount,
            int abortedSessionCount,
            int processedSessionCount,
            int processedWithWarningsSessionCount,
            int rejectedSessionCount,
            double averageFinalScore,
            double totalDistanceKm,
            int totalEventCount,
            int totalEscalationCount,
            List<ScoreTrendPointResponse> scoreTrend,
            List<EventCountResponse> eventCounts,
            List<EscalationCountResponse> escalationCounts
    ) {
        this.totalDrivers = totalDrivers;
        this.activeDriverCount = activeDriverCount;
        this.inactiveDriverCount = inactiveDriverCount;
        this.suspendedDriverCount = suspendedDriverCount;
        this.deactivatedDriverCount = deactivatedDriverCount;
        this.totalVehicles = totalVehicles;
        this.availableVehicleCount = availableVehicleCount;
        this.busyVehicleCount = busyVehicleCount;
        this.offlineVehicleCount = offlineVehicleCount;
        this.totalSessions = totalSessions;
        this.validSessionCount = validSessionCount;
        this.invalidSessionCount = invalidSessionCount;
        this.completedSessionCount = completedSessionCount;
        this.abortedSessionCount = abortedSessionCount;
        this.processedSessionCount = processedSessionCount;
        this.processedWithWarningsSessionCount = processedWithWarningsSessionCount;
        this.rejectedSessionCount = rejectedSessionCount;
        this.averageFinalScore = averageFinalScore;
        this.totalDistanceKm = totalDistanceKm;
        this.totalEventCount = totalEventCount;
        this.totalEscalationCount = totalEscalationCount;
        this.scoreTrend = scoreTrend;
        this.eventCounts = eventCounts;
        this.escalationCounts = escalationCounts;
    }

    public int getTotalDrivers() {
        return totalDrivers;
    }

    public void setTotalDrivers(int totalDrivers) {
        this.totalDrivers = totalDrivers;
    }

    public int getActiveDriverCount() {
        return activeDriverCount;
    }

    public void setActiveDriverCount(int activeDriverCount) {
        this.activeDriverCount = activeDriverCount;
    }

    public int getInactiveDriverCount() {
        return inactiveDriverCount;
    }

    public void setInactiveDriverCount(int inactiveDriverCount) {
        this.inactiveDriverCount = inactiveDriverCount;
    }

    public int getSuspendedDriverCount() {
        return suspendedDriverCount;
    }

    public void setSuspendedDriverCount(int suspendedDriverCount) {
        this.suspendedDriverCount = suspendedDriverCount;
    }

    public int getDeactivatedDriverCount() {
        return deactivatedDriverCount;
    }

    public void setDeactivatedDriverCount(int deactivatedDriverCount) {
        this.deactivatedDriverCount = deactivatedDriverCount;
    }

    public int getTotalVehicles() {
        return totalVehicles;
    }

    public void setTotalVehicles(int totalVehicles) {
        this.totalVehicles = totalVehicles;
    }

    public int getAvailableVehicleCount() {
        return availableVehicleCount;
    }

    public void setAvailableVehicleCount(int availableVehicleCount) {
        this.availableVehicleCount = availableVehicleCount;
    }

    public int getBusyVehicleCount() {
        return busyVehicleCount;
    }

    public void setBusyVehicleCount(int busyVehicleCount) {
        this.busyVehicleCount = busyVehicleCount;
    }

    public int getOfflineVehicleCount() {
        return offlineVehicleCount;
    }

    public void setOfflineVehicleCount(int offlineVehicleCount) {
        this.offlineVehicleCount = offlineVehicleCount;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
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

    public int getProcessedSessionCount() {
        return processedSessionCount;
    }

    public void setProcessedSessionCount(int processedSessionCount) {
        this.processedSessionCount = processedSessionCount;
    }

    public int getProcessedWithWarningsSessionCount() {
        return processedWithWarningsSessionCount;
    }

    public void setProcessedWithWarningsSessionCount(int processedWithWarningsSessionCount) {
        this.processedWithWarningsSessionCount = processedWithWarningsSessionCount;
    }

    public int getRejectedSessionCount() {
        return rejectedSessionCount;
    }

    public void setRejectedSessionCount(int rejectedSessionCount) {
        this.rejectedSessionCount = rejectedSessionCount;
    }

    public double getAverageFinalScore() {
        return averageFinalScore;
    }

    public void setAverageFinalScore(double averageFinalScore) {
        this.averageFinalScore = averageFinalScore;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
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
