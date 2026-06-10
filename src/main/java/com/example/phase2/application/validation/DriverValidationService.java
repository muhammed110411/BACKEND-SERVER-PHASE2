package com.example.phase2.application.validation;

import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.exception.InactiveDriverUploadException;
import com.example.phase2.exception.ResourceNotFoundException;
import com.example.phase2.persistence.entity.DriverEntity;
import com.example.phase2.persistence.repository.DriverJpaRepository;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class DriverValidationService {

    private static final String DRIVER_RESOURCE_TYPE = "driver";
    private static final String DRIVER_ID_LOOKUP_FIELD = "id";
    private static final String INACTIVE_DRIVER_UPLOAD_ERROR_CODE = "DRIVER_UPLOAD_NOT_ALLOWED";
    private static final int MIN_DRIVER_PASSWORD_LENGTH = 8;

    private final DriverJpaRepository driverJpaRepository;

    public DriverValidationService(DriverJpaRepository driverJpaRepository) {
        this.driverJpaRepository = driverJpaRepository;
    }

    public void validateDriverExists(String driverId) {
        getRequiredDriver(driverId);
    }

    public void validateDriverActiveForUpload(String driverId) {
        DriverEntity driver = getRequiredDriver(driverId);
        if (!driver.getAccountStatus().canUploadSessions()) {
            throw new InactiveDriverUploadException(
                    "Driver is not allowed to upload sessions: " + driverId,
                    driver.getId(),
                    INACTIVE_DRIVER_UPLOAD_ERROR_CODE,
                    driver.getAccountStatus().name()
            );
        }
    }

    public void validateDriverDeactivation(String driverId) {
        DriverEntity driver = getRequiredDriver(driverId);
        if (driver.getAccountStatus() == DriverAccountStatus.DEACTIVATED) {
            throw new IllegalArgumentException("Driver is already deactivated: " + driverId);
        }
    }

    public void validateDriverStatusTransition(
            String driverId,
            DriverAccountStatus currentStatus,
            DriverAccountStatus targetStatus
    ) {
        String requiredDriverId = requireText(driverId, "driverId");
        DriverAccountStatus requiredCurrentStatus = Objects.requireNonNull(currentStatus, "currentStatus must not be null");
        DriverAccountStatus requiredTargetStatus = Objects.requireNonNull(targetStatus, "targetStatus must not be null");

        if (requiredCurrentStatus == requiredTargetStatus) {
            throw new IllegalArgumentException("Driver is already in status: " + requiredTargetStatus);
        }
        if (requiredTargetStatus == DriverAccountStatus.DEACTIVATED) {
            if (requiredCurrentStatus == DriverAccountStatus.DEACTIVATED) {
                throw new IllegalArgumentException("Driver is already deactivated: " + requiredDriverId);
            }
        }
    }

    public void validateDriverEmailUniqueness(String email, String excludingDriverId) {
        String normalizedEmail = normalizeEmail(email);
        if (excludingDriverId == null || excludingDriverId.isBlank()) {
            if (driverJpaRepository.existsByEmailIgnoreCase(normalizedEmail)) {
                throw new IllegalArgumentException("Driver email is already in use: " + normalizedEmail);
            }
            return;
        }

        if (driverJpaRepository.existsByEmailIgnoreCaseAndIdNot(normalizedEmail, excludingDriverId)) {
            throw new IllegalArgumentException("Driver email is already in use: " + normalizedEmail);
        }
    }

    public void validateDriverCreation(String name, String email, String rawPassword, UserRole role) {
        requireText(name, "name");
        normalizeEmail(email);
        validateDriverEmailUniqueness(email, null);
        validateDriverRoleForCreation(role);
        validateDriverPassword(rawPassword);
    }

    public void validateDriverProfileUpdate(String driverId, String name, String email) {
        validateDriverExists(driverId);
        requireText(name, "name");
        validateDriverEmailUniqueness(email, driverId);
    }

    public String normalizeEmail(String email) {
        return requireText(email, "email").trim().toLowerCase();
    }

    public void validateDriverRoleForCreation(UserRole role) {
        UserRole requiredRole = Objects.requireNonNull(role, "role must not be null");
        if (requiredRole != UserRole.DRIVER) {
            throw new IllegalArgumentException("role must be DRIVER for driver account creation");
        }
    }

    public void validateDriverPassword(String rawPassword) {
        String requiredPassword = requireText(rawPassword, "password");
        if (requiredPassword.length() < MIN_DRIVER_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "password must be at least " + MIN_DRIVER_PASSWORD_LENGTH + " characters long"
            );
        }
    }

    private DriverEntity getRequiredDriver(String driverId) {
        String requiredDriverId = requireText(driverId, "driverId");
        return driverJpaRepository.findById(requiredDriverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver not found: " + requiredDriverId,
                        DRIVER_RESOURCE_TYPE,
                        requiredDriverId,
                        DRIVER_ID_LOOKUP_FIELD
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
