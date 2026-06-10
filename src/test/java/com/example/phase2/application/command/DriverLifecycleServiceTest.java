package com.example.phase2.application.command;

import com.example.phase2.config.IdGenerator;
import com.example.phase2.config.TimeProvider;
import com.example.phase2.application.validation.DriverValidationService;
import com.example.phase2.application.security.DriverAuthenticationService;
import com.example.phase2.application.security.DriverAuthenticationService.DriverLoginResult;
import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.domain.model.Driver;
import com.example.phase2.exception.ResourceNotFoundException;
import com.example.phase2.persistence.entity.DriverEntity;
import com.example.phase2.persistence.mapper.DriverPersistenceMapper;
import com.example.phase2.persistence.repository.DriverJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DriverLifecycleServiceTest {

    private DriverJpaRepository driverJpaRepository;
    private DriverLifecycleService driverLifecycleService;
    private DriverAuthenticationService driverAuthenticationService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        driverJpaRepository = mock(DriverJpaRepository.class);
        DriverPersistenceMapper driverPersistenceMapper = new DriverPersistenceMapper();
        DriverValidationService driverValidationService = new DriverValidationService(driverJpaRepository);
        TimeProvider timeProvider = () -> 1700001234567L;
        IdGenerator idGenerator = () -> "driver-test-001";
        passwordEncoder = new BCryptPasswordEncoder();

        driverLifecycleService = new DriverLifecycleService(
                driverJpaRepository,
                driverPersistenceMapper,
                driverValidationService,
                timeProvider,
                idGenerator,
                passwordEncoder
        );
        driverAuthenticationService = new DriverAuthenticationService(
                driverJpaRepository,
                driverPersistenceMapper,
                driverValidationService,
                passwordEncoder
        );
    }

    @Test
    void createDriverHashesPasswordAndAssignsDeterministicDriverId() {
        when(driverJpaRepository.existsByEmailIgnoreCase("aylin@example.com")).thenReturn(false);
        when(driverJpaRepository.save(any(DriverEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Driver createdDriver = driverLifecycleService.createDriver(
                "Aylin Driver",
                "aylin@example.com",
                "PlaintextPass123!",
                UserRole.DRIVER
        );

        assertEquals("driver-test-001", createdDriver.getId());
        assertEquals("Aylin Driver", createdDriver.getName());
        assertEquals("aylin@example.com", createdDriver.getEmail());
        assertEquals(DriverAccountStatus.ACTIVE, createdDriver.getAccountStatus());
        assertEquals(UserRole.DRIVER, createdDriver.getRole());

        verify(driverJpaRepository).save(any(DriverEntity.class));
        verify(driverJpaRepository, never()).findById("driver-test-001");
        verify(driverJpaRepository).save(org.mockito.ArgumentMatchers.argThat(saved ->
                "driver-test-001".equals(saved.getId())
                        && assertPasswordWasHashed(saved.getPasswordHash(), "PlaintextPass123!")
        ));
    }

    @Test
    void createDriverRejectsDuplicateEmailBeforeSaving() {
        when(driverJpaRepository.existsByEmailIgnoreCase("aylin@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> driverLifecycleService.createDriver(
                        "Aylin Driver",
                        "Aylin@Example.com",
                        "PlaintextPass123!",
                        UserRole.DRIVER
                )
        );

        assertTrue(exception.getMessage().contains("Driver email is already in use"));
        verify(driverJpaRepository, never()).save(any(DriverEntity.class));
    }

    @Test
    void createDriverConvertsDuplicateEmailRaceToValidationFailure() {
        when(driverJpaRepository.existsByEmailIgnoreCase("aylin@example.com")).thenReturn(false, true);
        when(driverJpaRepository.save(any(DriverEntity.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate email"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> driverLifecycleService.createDriver(
                        "Aylin Driver",
                        "aylin@example.com",
                        "PlaintextPass123!",
                        UserRole.DRIVER
                )
        );

        assertTrue(exception.getMessage().contains("Driver email is already in use"));
        verify(driverJpaRepository).save(any(DriverEntity.class));
    }

    @Test
    void createdDriverCanAuthenticateThroughDriverLoginService() {
        AtomicReference<DriverEntity> savedDriver = new AtomicReference<>();
        when(driverJpaRepository.existsByEmailIgnoreCase("aylin@example.com")).thenReturn(false);
        when(driverJpaRepository.save(any(DriverEntity.class))).thenAnswer(invocation -> {
            DriverEntity saved = invocation.getArgument(0);
            savedDriver.set(saved);
            return saved;
        });

        Driver createdDriver = driverLifecycleService.createDriver(
                "Aylin Driver",
                "aylin@example.com",
                "PlaintextPass123!",
                UserRole.DRIVER
        );
        when(driverJpaRepository.findByEmailIgnoreCase("aylin@example.com"))
                .thenReturn(Optional.of(savedDriver.get()));

        DriverLoginResult loginResult = driverAuthenticationService.authenticate(
                "aylin@example.com",
                "PlaintextPass123!"
        );

        assertEquals(createdDriver.getId(), loginResult.driver().getId());
        assertEquals(UserRole.DRIVER, loginResult.driver().getRole());
        assertEquals(DriverAccountStatus.ACTIVE, loginResult.driver().getAccountStatus());
        assertTrue(passwordEncoder.matches("PlaintextPass123!", savedDriver.get().getPasswordHash()));
    }

    @Test
    void deactivateDriverMarksDriverAsDeactivatedWithoutChangingHistoricalAggregates() {
        DriverEntity driverEntity = buildDriverEntity(DriverAccountStatus.ACTIVE, null);

        when(driverJpaRepository.findById("driver-1"))
                .thenReturn(Optional.of(driverEntity))
                .thenReturn(Optional.of(driverEntity));
        when(driverJpaRepository.save(driverEntity)).thenReturn(driverEntity);

        Driver updatedDriver = driverLifecycleService.deactivateDriver("driver-1", "fraud-review");

        assertEquals(DriverAccountStatus.DEACTIVATED, updatedDriver.getAccountStatus());
        assertEquals(1700001234567L, updatedDriver.getUpdatedAt());
        assertEquals(1700001234567L, updatedDriver.getDeactivatedAt());
        assertEquals(97.5d, updatedDriver.getLongTermReliabilityScore());
        assertEquals(48, updatedDriver.getTotalSessions());
        assertEquals(1250.25d, updatedDriver.getTotalDistanceKm());
        assertEquals(UserRole.DRIVER, updatedDriver.getRole());

        verify(driverJpaRepository, times(2)).findById("driver-1");
        verify(driverJpaRepository).save(driverEntity);
    }

    @Test
    void updateDriverAccountStatusCanReactivateWithoutChangingIdentityFields() {
        DriverEntity driverEntity = buildDriverEntity(DriverAccountStatus.DEACTIVATED, 1699999999999L);

        when(driverJpaRepository.findById("driver-1"))
                .thenReturn(Optional.of(driverEntity))
                .thenReturn(Optional.of(driverEntity));
        when(driverJpaRepository.save(driverEntity)).thenReturn(driverEntity);

        Driver updatedDriver = driverLifecycleService.updateDriverAccountStatus("driver-1", DriverAccountStatus.ACTIVE);

        assertEquals("driver-1", updatedDriver.getId());
        assertEquals("Aylin Driver", updatedDriver.getName());
        assertEquals("aylin@example.com", updatedDriver.getEmail());
        assertEquals(DriverAccountStatus.ACTIVE, updatedDriver.getAccountStatus());
        assertEquals(1700001234567L, updatedDriver.getUpdatedAt());
        assertEquals(null, updatedDriver.getDeactivatedAt());
    }

    @Test
    void deactivateDriverFailsWhenDriverDoesNotExist() {
        when(driverJpaRepository.findById("missing-driver")).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> driverLifecycleService.deactivateDriver("missing-driver", "duplicate-account")
        );
    }

    private DriverEntity buildDriverEntity(DriverAccountStatus status, Long deactivatedAt) {
        return new DriverEntity(
                "driver-1",
                "Aylin Driver",
                "aylin@example.com",
                "$2a$10$7EqJtq98hPqEX7fNZaFWoOHi6M9G6N7B2Lr7z7k0GQzQ0jrISFRCW",
                UserRole.DRIVER,
                status,
                97.5d,
                48,
                1250.25d,
                1699990000000L,
                1699995000000L,
                deactivatedAt
        );
    }

    private boolean assertPasswordWasHashed(String storedHash, String rawPassword) {
        PasswordEncoder verifier = new BCryptPasswordEncoder();
        assertNotEquals(rawPassword, storedHash);
        assertTrue(verifier.matches(rawPassword, storedHash));
        return true;
    }
}
