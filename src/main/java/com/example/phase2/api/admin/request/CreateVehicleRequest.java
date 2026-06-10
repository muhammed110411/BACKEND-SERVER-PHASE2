package com.example.phase2.api.admin.request;

import com.example.phase2.domain.enums.VehicleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateVehicleRequest {

    @NotBlank
    private String displayName;

    @NotNull
    private VehicleStatus status;

    public CreateVehicleRequest() {
    }

    public CreateVehicleRequest(String displayName, VehicleStatus status) {
        this.displayName = displayName;
        this.status = status;
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
}
