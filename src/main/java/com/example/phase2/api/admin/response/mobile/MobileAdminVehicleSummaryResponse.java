package com.example.phase2.api.admin.response.mobile;

import com.example.phase2.domain.enums.VehicleStatus;

public record MobileAdminVehicleSummaryResponse(
        String vehicleId,
        String displayName,
        VehicleStatus status
) {
}
