package com.example.phase2.application.command;

import com.example.phase2.application.validation.VehicleValidationService;
import com.example.phase2.config.IdGenerator;
import com.example.phase2.domain.enums.VehicleStatus;
import com.example.phase2.domain.model.Vehicle;
import com.example.phase2.exception.ResourceNotFoundException;
import com.example.phase2.persistence.entity.VehicleEntity;
import com.example.phase2.persistence.mapper.VehiclePersistenceMapper;
import com.example.phase2.persistence.repository.VehicleJpaRepository;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehicleManagementService {

    private static final String VEHICLE_RESOURCE_TYPE = "vehicle";
    private static final String ID_LOOKUP_FIELD = "id";
    private static final boolean DISPLAY_NAME_UNIQUENESS_REQUIRED = true;

    private final VehicleJpaRepository vehicleJpaRepository;
    private final VehiclePersistenceMapper vehiclePersistenceMapper;
    private final VehicleValidationService vehicleValidationService;
    private final Clock clock;
    private final IdGenerator idGenerator;

    public VehicleManagementService(
            VehicleJpaRepository vehicleJpaRepository,
            VehiclePersistenceMapper vehiclePersistenceMapper,
            VehicleValidationService vehicleValidationService,
            Clock clock,
            IdGenerator idGenerator
    ) {
        this.vehicleJpaRepository = vehicleJpaRepository;
        this.vehiclePersistenceMapper = vehiclePersistenceMapper;
        this.vehicleValidationService = vehicleValidationService;
        this.clock = clock;
        this.idGenerator = idGenerator;
    }

    @Transactional
    public Vehicle createVehicle(String displayName, VehicleStatus initialStatus) {
        vehicleValidationService.validateVehicleCreation(displayName, initialStatus);
        ensureDisplayNameUniqueness(displayName, null);

        String vehicleId = requireText(idGenerator.newId(), "generatedVehicleId");
        long createdAt = clock.millis();
        return persistNewVehicle(vehicleId, displayName, initialStatus, createdAt);
    }

    @Transactional
    public Vehicle updateVehicle(String vehicleId, String displayName, VehicleStatus targetStatus) {
        Vehicle existingVehicle = resolveExistingVehicle(vehicleId);
        vehicleValidationService.validateVehicleUpdate(existingVehicle.getId(), displayName, targetStatus);
        ensureDisplayNameUniqueness(displayName, existingVehicle.getId());

        long updatedAt = clock.millis();
        return persistVehicleUpdate(existingVehicle, displayName, targetStatus, updatedAt);
    }

    private Vehicle resolveExistingVehicle(String vehicleId) {
        String requiredVehicleId = requireText(vehicleId, "vehicleId");
        return vehicleJpaRepository.findById(requiredVehicleId)
                .map(vehiclePersistenceMapper::toDomain)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle not found: " + requiredVehicleId,
                        VEHICLE_RESOURCE_TYPE,
                        requiredVehicleId,
                        ID_LOOKUP_FIELD
                ));
    }

    private void ensureDisplayNameUniqueness(String displayName, String excludedVehicleId) {
        if (!DISPLAY_NAME_UNIQUENESS_REQUIRED) {
            return;
        }
        vehicleValidationService.validateVehicleDisplayNameUniqueness(displayName, excludedVehicleId);
    }

    private Vehicle persistNewVehicle(String vehicleId, String displayName, VehicleStatus initialStatus, Long createdAt) {
        VehicleEntity vehicleEntity = new VehicleEntity(
                vehicleId,
                displayName,
                initialStatus,
                createdAt,
                createdAt,
                null
        );
        VehicleEntity savedEntity = vehicleJpaRepository.save(vehicleEntity);
        return vehiclePersistenceMapper.toDomain(savedEntity);
    }

    private Vehicle persistVehicleUpdate(
            Vehicle existingVehicle,
            String displayName,
            VehicleStatus targetStatus,
            Long updatedAt
    ) {
        VehicleEntity vehicleEntity = vehicleJpaRepository.findById(existingVehicle.getId()).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Vehicle not found: " + existingVehicle.getId(),
                        VEHICLE_RESOURCE_TYPE,
                        existingVehicle.getId(),
                        ID_LOOKUP_FIELD
                ));
        vehicleEntity.setDisplayName(displayName);
        vehicleEntity.setStatus(targetStatus);
        vehicleEntity.setUpdatedAt(updatedAt);

        VehicleEntity savedEntity = vehicleJpaRepository.save(vehicleEntity);
        return vehiclePersistenceMapper.toDomain(savedEntity);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
