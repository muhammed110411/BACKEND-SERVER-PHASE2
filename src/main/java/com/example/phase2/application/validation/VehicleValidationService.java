package com.example.phase2.application.validation;

import com.example.phase2.domain.enums.VehicleStatus;
import com.example.phase2.exception.ResourceNotFoundException;
import com.example.phase2.persistence.entity.VehicleEntity;
import com.example.phase2.persistence.repository.VehicleJpaRepository;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class VehicleValidationService {

    private static final String VEHICLE_RESOURCE_TYPE = "vehicle";
    private static final String VEHICLE_ID_LOOKUP_FIELD = "id";

    private final VehicleJpaRepository vehicleJpaRepository;

    public VehicleValidationService(VehicleJpaRepository vehicleJpaRepository) {
        this.vehicleJpaRepository = vehicleJpaRepository;
    }

    public void validateVehicleExists(String vehicleId) {
        getRequiredVehicle(vehicleId);
    }

    public void validateVehicleCreation(String displayName, VehicleStatus initialStatus) {
        validateVehicleDisplayName(displayName);
        Objects.requireNonNull(initialStatus, "initialStatus must not be null");
    }

    public void validateVehicleUpdate(String vehicleId, String displayName, VehicleStatus targetStatus) {
        requireText(vehicleId, "vehicleId");
        getRequiredVehicle(vehicleId);
        validateVehicleDisplayName(displayName);
        Objects.requireNonNull(targetStatus, "targetStatus must not be null");
    }

    public void validateVehicleDisplayName(String displayName) {
        requireText(displayName, "displayName");
    }

    public void validateVehicleDisplayNameUniqueness(String displayName, String excludingVehicleId) {
        String normalizedDisplayName = requireText(displayName, "displayName");
        if (excludingVehicleId == null || excludingVehicleId.isBlank()) {
            if (vehicleJpaRepository.existsByDisplayName(normalizedDisplayName)) {
                throw new IllegalArgumentException("Vehicle displayName is already in use: " + normalizedDisplayName);
            }
            return;
        }

        if (vehicleJpaRepository.existsByDisplayNameAndIdNot(normalizedDisplayName, excludingVehicleId)) {
            throw new IllegalArgumentException("Vehicle displayName is already in use: " + normalizedDisplayName);
        }
    }

    public void validateVehicleAvailabilityForAdministrativeUse(String vehicleId) {
        VehicleEntity vehicle = getRequiredVehicle(vehicleId);
        if (vehicle.getDeactivatedAt() != null) {
            throw new IllegalArgumentException(
                    "Vehicle is not available for administrative use because it is deactivated: " + vehicle.getId()
            );
        }
        if (!vehicle.getStatus().isAvailable()) {
            throw new IllegalArgumentException(
                    "Vehicle is not available for administrative use because status is "
                            + vehicle.getStatus().name()
                            + ": "
                            + vehicle.getId()
            );
        }
    }

    private VehicleEntity getRequiredVehicle(String vehicleId) {
        String requiredVehicleId = requireText(vehicleId, "vehicleId");
        return vehicleJpaRepository.findById(requiredVehicleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle not found: " + requiredVehicleId,
                        VEHICLE_RESOURCE_TYPE,
                        requiredVehicleId,
                        VEHICLE_ID_LOOKUP_FIELD
                ));
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
