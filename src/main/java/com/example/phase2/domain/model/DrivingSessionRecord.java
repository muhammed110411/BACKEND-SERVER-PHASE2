package com.example.phase2.domain.model;

import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;

import java.util.Objects;

public class DrivingSessionRecord {

    private final String id;
    private final String driverId;
    private final String vehicleId;
    private final long startTimestamp;
    private final long endTimestamp;
    private final SessionEndStatus status;
    private final SessionValidity validity;
    private final UploadProcessingStatus uploadProcessingStatus;
    private final double finalScore;
    private final double totalContinuousPenalty;
    private final double totalEventPenalty;
    private final double totalEscalationPenalty;
    private final long totalDurationSeconds;
    private final double totalDistanceKm;
    private final int totalEventCount;
    private final int totalEscalationCount;
    private final long uploadedAt;
    private final Long processedAt;
    private final String sourceClientId;

    public DrivingSessionRecord(
            String id,
            String driverId,
            String vehicleId,
            long startTimestamp,
            long endTimestamp,
            SessionEndStatus status,
            SessionValidity validity,
            UploadProcessingStatus uploadProcessingStatus,
            double finalScore,
            double totalContinuousPenalty,
            double totalEventPenalty,
            double totalEscalationPenalty,
            long totalDurationSeconds,
            double totalDistanceKm,
            int totalEventCount,
            int totalEscalationCount,
            long uploadedAt,
            Long processedAt,
            String sourceClientId
    ) {
        this.id = requireText(id, "id");
        this.driverId = requireText(driverId, "driverId");
        this.vehicleId = requireText(vehicleId, "vehicleId");
        this.startTimestamp = startTimestamp;
        this.endTimestamp = requireEndTimestamp(startTimestamp, endTimestamp);
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.validity = Objects.requireNonNull(validity, "validity must not be null");
        this.uploadProcessingStatus = Objects.requireNonNull(uploadProcessingStatus, "uploadProcessingStatus must not be null");
        this.finalScore = finalScore;
        this.totalContinuousPenalty = totalContinuousPenalty;
        this.totalEventPenalty = totalEventPenalty;
        this.totalEscalationPenalty = totalEscalationPenalty;
        this.totalDurationSeconds = requireNonNegative(totalDurationSeconds, "totalDurationSeconds");
        this.totalDistanceKm = requireNonNegative(totalDistanceKm, "totalDistanceKm");
        this.totalEventCount = requireNonNegative(totalEventCount, "totalEventCount");
        this.totalEscalationCount = requireNonNegative(totalEscalationCount, "totalEscalationCount");
        this.uploadedAt = uploadedAt;
        this.processedAt = processedAt;
        this.sourceClientId = requireOptionalText(sourceClientId, "sourceClientId");
    }

    public String getId() {
        return id;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public SessionEndStatus getStatus() {
        return status;
    }

    public SessionValidity getValidity() {
        return validity;
    }

    public UploadProcessingStatus getUploadProcessingStatus() {
        return uploadProcessingStatus;
    }

    public double getFinalScore() {
        return finalScore;
    }

    public double getTotalContinuousPenalty() {
        return totalContinuousPenalty;
    }

    public double getTotalEventPenalty() {
        return totalEventPenalty;
    }

    public double getTotalEscalationPenalty() {
        return totalEscalationPenalty;
    }

    public long getTotalDurationSeconds() {
        return totalDurationSeconds;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public int getTotalEventCount() {
        return totalEventCount;
    }

    public int getTotalEscalationCount() {
        return totalEscalationCount;
    }

    public long getUploadedAt() {
        return uploadedAt;
    }

    public Long getProcessedAt() {
        return processedAt;
    }

    public String getSourceClientId() {
        return sourceClientId;
    }

    public boolean isCompleted() {
        return status == SessionEndStatus.COMPLETED;
    }

    public boolean isValid() {
        return validity.isValid();
    }

    public boolean isProcessed() {
        return uploadProcessingStatus.isTerminal();
    }

    public boolean isImmutableHistoricalRecord() {
        return true;
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

    private static String requireOptionalText(String value, String fieldName) {
        if (value == null) {
            return null;
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank when provided");
        }
        return value;
    }

    private static long requireEndTimestamp(long startTimestamp, long endTimestamp) {
        if (endTimestamp < startTimestamp) {
            throw new IllegalArgumentException("endTimestamp must be greater than or equal to startTimestamp");
        }
        return endTimestamp;
    }

    private static long requireNonNegative(long value, String fieldName) {
        if (value < 0L) {
            throw new IllegalArgumentException(fieldName + " must be greater than or equal to 0");
        }
        return value;
    }

    private static double requireNonNegative(double value, String fieldName) {
        if (value < 0.0d) {
            throw new IllegalArgumentException(fieldName + " must be greater than or equal to 0.0");
        }
        return value;
    }

    private static int requireNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than or equal to 0");
        }
        return value;
    }
}
