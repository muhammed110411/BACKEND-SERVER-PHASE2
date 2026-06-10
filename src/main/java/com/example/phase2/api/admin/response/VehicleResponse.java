package com.example.phase2.api.admin.response;

import com.example.phase2.domain.enums.VehicleStatus;

public class VehicleResponse {

    private String vehicleId;
    private String displayName;
    private VehicleStatus status;
    private long createdAt;
    private long updatedAt;
    private Long deactivatedAt;

    public VehicleResponse() {
    }

    public VehicleResponse(
            String vehicleId,
            String displayName,
            VehicleStatus status,
            long createdAt,
            long updatedAt,
            Long deactivatedAt
    ) {
        this.vehicleId = vehicleId;
        this.displayName = displayName;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deactivatedAt = deactivatedAt;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getDeactivatedAt() {
        return deactivatedAt;
    }

    public void setDeactivatedAt(Long deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
    }
}
