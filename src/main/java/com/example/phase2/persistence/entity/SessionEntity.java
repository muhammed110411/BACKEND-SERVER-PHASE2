package com.example.phase2.persistence.entity;

import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "sessions")
public class SessionEntity {

    @Id
    @Column(nullable = false, updatable = false, length = 128)
    private String id;

    @Column(nullable = false, length = 128)
    private String driverId;

    @Column(nullable = false, length = 128)
    private String vehicleId;

    @Column(nullable = false)
    private long startTimestamp;

    @Column(nullable = false)
    private long endTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private SessionEndStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private SessionValidity validity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private UploadProcessingStatus uploadProcessingStatus;

    @Column(nullable = false)
    private double finalScore;

    @Column(nullable = false)
    private double totalContinuousPenalty;

    @Column(nullable = false)
    private double totalEventPenalty;

    @Column(nullable = false)
    private double totalEscalationPenalty;

    @Column(nullable = false)
    private long totalDurationSeconds;

    @Column(nullable = false)
    private double totalDistanceKm;

    @Column(nullable = false)
    private int totalEventCount;

    @Column(nullable = false)
    private int totalEscalationCount;

    @Column(nullable = false)
    private long uploadedAt;

    @Column
    private Long processedAt;

    @Column(length = 128)
    private String sourceClientId;

    protected SessionEntity() {
    }

    public SessionEntity(
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
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.driverId = Objects.requireNonNull(driverId, "driverId must not be null");
        this.vehicleId = Objects.requireNonNull(vehicleId, "vehicleId must not be null");
        this.startTimestamp = startTimestamp;
        setEndTimestamp(endTimestamp);
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.validity = Objects.requireNonNull(validity, "validity must not be null");
        this.uploadProcessingStatus = Objects.requireNonNull(
                uploadProcessingStatus,
                "uploadProcessingStatus must not be null"
        );
        this.finalScore = finalScore;
        this.totalContinuousPenalty = totalContinuousPenalty;
        this.totalEventPenalty = totalEventPenalty;
        this.totalEscalationPenalty = totalEscalationPenalty;
        setTotalDurationSeconds(totalDurationSeconds);
        setTotalDistanceKm(totalDistanceKm);
        setTotalEventCount(totalEventCount);
        setTotalEscalationCount(totalEscalationCount);
        this.uploadedAt = uploadedAt;
        this.processedAt = processedAt;
        this.sourceClientId = sourceClientId;
    }

    public String getId() {
        return id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = Objects.requireNonNull(driverId, "driverId must not be null");
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = Objects.requireNonNull(vehicleId, "vehicleId must not be null");
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        if (endTimestamp != 0L && startTimestamp > endTimestamp) {
            throw new IllegalArgumentException("startTimestamp must be less than or equal to endTimestamp");
        }
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        if (endTimestamp < startTimestamp) {
            throw new IllegalArgumentException("endTimestamp must be greater than or equal to startTimestamp");
        }
        this.endTimestamp = endTimestamp;
    }

    public SessionEndStatus getStatus() {
        return status;
    }

    public void setStatus(SessionEndStatus status) {
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    public SessionValidity getValidity() {
        return validity;
    }

    public void setValidity(SessionValidity validity) {
        this.validity = Objects.requireNonNull(validity, "validity must not be null");
    }

    public UploadProcessingStatus getUploadProcessingStatus() {
        return uploadProcessingStatus;
    }

    public void setUploadProcessingStatus(UploadProcessingStatus uploadProcessingStatus) {
        this.uploadProcessingStatus = Objects.requireNonNull(
                uploadProcessingStatus,
                "uploadProcessingStatus must not be null"
        );
    }

    public double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }

    public double getTotalContinuousPenalty() {
        return totalContinuousPenalty;
    }

    public void setTotalContinuousPenalty(double totalContinuousPenalty) {
        this.totalContinuousPenalty = totalContinuousPenalty;
    }

    public double getTotalEventPenalty() {
        return totalEventPenalty;
    }

    public void setTotalEventPenalty(double totalEventPenalty) {
        this.totalEventPenalty = totalEventPenalty;
    }

    public double getTotalEscalationPenalty() {
        return totalEscalationPenalty;
    }

    public void setTotalEscalationPenalty(double totalEscalationPenalty) {
        this.totalEscalationPenalty = totalEscalationPenalty;
    }

    public long getTotalDurationSeconds() {
        return totalDurationSeconds;
    }

    public void setTotalDurationSeconds(long totalDurationSeconds) {
        if (totalDurationSeconds < 0L) {
            throw new IllegalArgumentException("totalDurationSeconds must be non-negative");
        }
        this.totalDurationSeconds = totalDurationSeconds;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(double totalDistanceKm) {
        if (totalDistanceKm < 0.0d) {
            throw new IllegalArgumentException("totalDistanceKm must be non-negative");
        }
        this.totalDistanceKm = totalDistanceKm;
    }

    public int getTotalEventCount() {
        return totalEventCount;
    }

    public void setTotalEventCount(int totalEventCount) {
        if (totalEventCount < 0) {
            throw new IllegalArgumentException("totalEventCount must be non-negative");
        }
        this.totalEventCount = totalEventCount;
    }

    public int getTotalEscalationCount() {
        return totalEscalationCount;
    }

    public void setTotalEscalationCount(int totalEscalationCount) {
        if (totalEscalationCount < 0) {
            throw new IllegalArgumentException("totalEscalationCount must be non-negative");
        }
        this.totalEscalationCount = totalEscalationCount;
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

    public String getSourceClientId() {
        return sourceClientId;
    }

    public void setSourceClientId(String sourceClientId) {
        this.sourceClientId = sourceClientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SessionEntity that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
