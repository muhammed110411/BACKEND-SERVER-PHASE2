package com.example.phase2.application.command;

import com.example.phase2.config.TimeProvider;
import com.example.phase2.config.IdGenerator;
import com.example.phase2.application.validation.DriverValidationService;
import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.domain.model.Driver;
import com.example.phase2.exception.ResourceNotFoundException;
import com.example.phase2.persistence.entity.DriverEntity;
import com.example.phase2.persistence.mapper.DriverPersistenceMapper;
import com.example.phase2.persistence.repository.DriverJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DriverLifecycleService {

    private static final String DRIVER_RESOURCE_TYPE = "driver";
    private static final String ID_LOOKUP_FIELD = "id";

    private final DriverJpaRepository driverJpaRepository;
    private final DriverPersistenceMapper driverPersistenceMapper;
    private final DriverValidationService driverValidationService;
    private final TimeProvider timeProvider;
    private final IdGenerator idGenerator;
    private final PasswordEncoder passwordEncoder;

    public DriverLifecycleService(
            DriverJpaRepository driverJpaRepository,
            DriverPersistenceMapper driverPersistenceMapper,
            DriverValidationService driverValidationService,
            TimeProvider timeProvider,
            IdGenerator idGenerator,
            PasswordEncoder passwordEncoder
    ) {
        this.driverJpaRepository = driverJpaRepository;
        this.driverPersistenceMapper = driverPersistenceMapper;
        this.driverValidationService = driverValidationService;
        this.timeProvider = timeProvider;
        this.idGenerator = idGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Driver createDriver(String name, String email, String rawPassword, UserRole role) {
        driverValidationService.validateDriverCreation(name, email, rawPassword, role);

        String driverId = requireText(idGenerator.newId(), "generatedDriverId");
        String normalizedEmail = driverValidationService.normalizeEmail(email);
        long createdAt = timeProvider.nowEpochMillis();
        DriverEntity driverEntity = new DriverEntity(
                driverId,
                requireText(name, "name").trim(),
                normalizedEmail,
                passwordEncoder.encode(rawPassword),
                UserRole.DRIVER,
                DriverAccountStatus.ACTIVE,
                0.0d,
                0,
                0.0d,
                createdAt,
                createdAt,
                null
        );

        DriverEntity savedEntity;
        try {
            savedEntity = driverJpaRepository.save(driverEntity);
        } catch (DataIntegrityViolationException exception) {
            if (driverJpaRepository.existsByEmailIgnoreCase(normalizedEmail)) {
                throw new IllegalArgumentException("Driver email is already in use: " + normalizedEmail);
            }
            throw exception;
        }
        return driverPersistenceMapper.toDomain(savedEntity);
    }

    @Transactional
    public Driver deactivateDriver(String driverId, String reason) {
        requireText(reason, "reason");
        Driver driver = resolveExistingDriver(driverId);
        validateStatusTransition(driver, DriverAccountStatus.DEACTIVATED);
        return persistDriverStatusChange(driver, DriverAccountStatus.DEACTIVATED, timeProvider.nowEpochMillis());
    }

    @Transactional
    public Driver updateDriverAccountStatus(String driverId, DriverAccountStatus targetStatus) {
        Driver driver = resolveExistingDriver(driverId);
        validateStatusTransition(driver, targetStatus);
        return persistDriverStatusChange(driver, targetStatus, timeProvider.nowEpochMillis());
    }

    private Driver resolveExistingDriver(String driverId) {
        String requiredDriverId = requireText(driverId, "driverId");
        return driverJpaRepository.findById(requiredDriverId)
                .map(driverPersistenceMapper::toDomain)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver not found: " + requiredDriverId,
                        DRIVER_RESOURCE_TYPE,
                        requiredDriverId,
                        ID_LOOKUP_FIELD
                ));
    }

    private void validateStatusTransition(Driver driver, DriverAccountStatus targetStatus) {
        driverValidationService.validateDriverStatusTransition(
                driver.getId(),
                driver.getAccountStatus(),
                targetStatus
        );
    }

    private Driver persistDriverStatusChange(Driver driver, DriverAccountStatus targetStatus, Long changedAt) {
        DriverEntity driverEntity = driverJpaRepository.findById(driver.getId()).orElseThrow();
        driverEntity.setAccountStatus(targetStatus);
        driverEntity.setUpdatedAt(changedAt);
        driverEntity.setDeactivatedAt(targetStatus == DriverAccountStatus.DEACTIVATED ? changedAt : null);

        DriverEntity savedEntity = driverJpaRepository.save(driverEntity);
        return driverPersistenceMapper.toDomain(savedEntity);
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
