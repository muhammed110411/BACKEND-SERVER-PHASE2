package com.example.phase2.application.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.phase2.application.validation.VehicleValidationService;
import com.example.phase2.config.IdGenerator;
import com.example.phase2.domain.enums.VehicleStatus;
import com.example.phase2.domain.model.Vehicle;
import com.example.phase2.exception.ResourceNotFoundException;
import com.example.phase2.persistence.entity.VehicleEntity;
import com.example.phase2.persistence.mapper.VehiclePersistenceMapper;
import com.example.phase2.persistence.repository.VehicleJpaRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VehicleManagementServiceTest {

    private VehicleJpaRepository vehicleJpaRepository;
    private VehicleManagementService vehicleManagementService;

    @BeforeEach
    void setUp() {
        vehicleJpaRepository = org.mockito.Mockito.mock(VehicleJpaRepository.class);
        VehiclePersistenceMapper vehiclePersistenceMapper = new VehiclePersistenceMapper();
        VehicleValidationService vehicleValidationService = new VehicleValidationService(vehicleJpaRepository);
        Clock clock = Clock.fixed(Instant.ofEpochMilli(1700001234567L), ZoneOffset.UTC);
        IdGenerator idGenerator = () -> "vehicle-123";

        vehicleManagementService = new VehicleManagementService(
                vehicleJpaRepository,
                vehiclePersistenceMapper,
                vehicleValidationService,
                clock,
                idGenerator
        );
    }

    @Test
    void createVehiclePersistsManagedRecordWithConsistentTimestamps() {
        VehicleEntity savedEntity = new VehicleEntity(
                "vehicle-123",
                "Admin Car",
                VehicleStatus.AVAILABLE,
                1700001234567L,
                1700001234567L,
                null
        );
        when(vehicleJpaRepository.existsByDisplayName("Admin Car")).thenReturn(false);
        when(vehicleJpaRepository.save(org.mockito.ArgumentMatchers.any(VehicleEntity.class))).thenReturn(savedEntity);

        Vehicle vehicle = vehicleManagementService.createVehicle("Admin Car", VehicleStatus.AVAILABLE);

        assertEquals("vehicle-123", vehicle.getId());
        assertEquals("Admin Car", vehicle.getDisplayName());
        assertEquals(VehicleStatus.AVAILABLE, vehicle.getStatus());
        assertEquals(1700001234567L, vehicle.getCreatedAt());
        assertEquals(1700001234567L, vehicle.getUpdatedAt());
        assertNull(vehicle.getDeactivatedAt());
    }

    @Test
    void updateVehicleChangesOnlyMutableManagedFields() {
        VehicleEntity existingEntity = new VehicleEntity(
                "vehicle-1",
                "Old Name",
                VehicleStatus.OFFLINE,
                1699990000000L,
                1699995000000L,
                null
        );

        when(vehicleJpaRepository.findById("vehicle-1"))
                .thenReturn(Optional.of(existingEntity))
                .thenReturn(Optional.of(existingEntity));
        when(vehicleJpaRepository.existsByDisplayNameAndIdNot("Renamed Car", "vehicle-1")).thenReturn(false);
        when(vehicleJpaRepository.save(existingEntity)).thenReturn(existingEntity);

        Vehicle updatedVehicle = vehicleManagementService.updateVehicle(
                "vehicle-1",
                "Renamed Car",
                VehicleStatus.BUSY
        );

        assertEquals("vehicle-1", updatedVehicle.getId());
        assertEquals("Renamed Car", updatedVehicle.getDisplayName());
        assertEquals(VehicleStatus.BUSY, updatedVehicle.getStatus());
        assertEquals(1699990000000L, updatedVehicle.getCreatedAt());
        assertEquals(1700001234567L, updatedVehicle.getUpdatedAt());
        assertNull(updatedVehicle.getDeactivatedAt());
        verify(vehicleJpaRepository, times(3)).findById("vehicle-1");
    }

    @Test
    void updateVehicleFailsWhenTargetDoesNotExist() {
        when(vehicleJpaRepository.findById("missing-vehicle")).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> vehicleManagementService.updateVehicle("missing-vehicle", "Renamed Car", VehicleStatus.BUSY)
        );
    }
}
