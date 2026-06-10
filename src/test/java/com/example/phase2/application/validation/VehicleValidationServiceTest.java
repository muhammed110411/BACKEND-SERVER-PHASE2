package com.example.phase2.application.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.phase2.domain.enums.VehicleStatus;
import com.example.phase2.exception.ResourceNotFoundException;
import com.example.phase2.persistence.entity.VehicleEntity;
import com.example.phase2.persistence.repository.VehicleJpaRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VehicleValidationServiceTest {

    @Mock
    private VehicleJpaRepository vehicleJpaRepository;

    private VehicleValidationService vehicleValidationService;

    @BeforeEach
    void setUp() {
        vehicleValidationService = new VehicleValidationService(vehicleJpaRepository);
    }

    @Test
    void validateVehicleExistsThrowsWhenVehicleIsMissing() {
        when(vehicleJpaRepository.findById("vehicle-1")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> vehicleValidationService.validateVehicleExists("vehicle-1")
        );

        assertEquals("vehicle", exception.getResourceType());
        assertEquals("vehicle-1", exception.getResourceId());
        assertEquals("id", exception.getLookupField());
    }

    @Test
    void validateVehicleCreationRejectsDuplicateDisplayName() {
        when(vehicleJpaRepository.existsByDisplayName("Admin Car")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vehicleValidationService.validateVehicleDisplayNameUniqueness("Admin Car", null)
        );

        assertEquals("Vehicle displayName is already in use: Admin Car", exception.getMessage());
    }

    @Test
    void validateVehicleCreationAllowsUniquePersistedMetadata() {
        assertDoesNotThrow(() -> vehicleValidationService.validateVehicleCreation("Admin Car", VehicleStatus.OFFLINE));
    }

    @Test
    void validateVehicleUpdateRequiresExistingVehicleAndUniqueDisplayName() {
        when(vehicleJpaRepository.existsByDisplayNameAndIdNot("Renamed Car", "vehicle-1")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vehicleValidationService.validateVehicleDisplayNameUniqueness("Renamed Car", "vehicle-1")
        );

        assertEquals("Vehicle displayName is already in use: Renamed Car", exception.getMessage());
    }

    @Test
    void validateVehicleUpdateRequiresExistingVehicle() {
        when(vehicleJpaRepository.findById("vehicle-1")).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> vehicleValidationService.validateVehicleUpdate("vehicle-1", "Renamed Car", VehicleStatus.BUSY)
        );
    }

    @Test
    void validateVehicleAvailabilityForAdministrativeUseRejectsDeactivatedVehicle() {
        when(vehicleJpaRepository.findById("vehicle-1"))
                .thenReturn(Optional.of(new VehicleEntity(
                        "vehicle-1",
                        "Admin Car",
                        VehicleStatus.AVAILABLE,
                        1L,
                        2L,
                        3L
                )));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vehicleValidationService.validateVehicleAvailabilityForAdministrativeUse("vehicle-1")
        );

        assertEquals(
                "Vehicle is not available for administrative use because it is deactivated: vehicle-1",
                exception.getMessage()
        );
    }

    @Test
    void validateVehicleAvailabilityForAdministrativeUseRejectsNonAvailableStatus() {
        when(vehicleJpaRepository.findById("vehicle-1")).thenReturn(Optional.of(activeVehicle("vehicle-1", VehicleStatus.BUSY)));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vehicleValidationService.validateVehicleAvailabilityForAdministrativeUse("vehicle-1")
        );

        assertEquals(
                "Vehicle is not available for administrative use because status is BUSY: vehicle-1",
                exception.getMessage()
        );
    }

    @Test
    void validateVehicleDisplayNameStopsBlankInputBeforeRepositoryChecks() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vehicleValidationService.validateVehicleDisplayName(" ")
        );

        assertEquals("displayName must not be blank", exception.getMessage());
        verify(vehicleJpaRepository, never()).existsByDisplayName(" ");
    }

    private static VehicleEntity activeVehicle(String id, VehicleStatus status) {
        return new VehicleEntity(id, "Admin Car", status, 1L, 2L, null);
    }
}
