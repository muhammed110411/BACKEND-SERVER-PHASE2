package com.example.phase2.application.security;

import com.example.phase2.application.validation.DriverValidationService;
import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.domain.model.Driver;
import com.example.phase2.persistence.entity.DriverEntity;
import com.example.phase2.persistence.mapper.DriverPersistenceMapper;
import com.example.phase2.persistence.repository.DriverJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DriverAuthenticationService {

    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid driver email or password.";
    private static final String INELIGIBLE_ACCOUNT_MESSAGE = "Driver account is not allowed to sign in.";
    private static final String LOGIN_SUCCESS_MESSAGE = "Driver authenticated successfully.";

    private final DriverJpaRepository driverJpaRepository;
    private final DriverPersistenceMapper driverPersistenceMapper;
    private final DriverValidationService driverValidationService;
    private final PasswordEncoder passwordEncoder;

    public DriverAuthenticationService(
            DriverJpaRepository driverJpaRepository,
            DriverPersistenceMapper driverPersistenceMapper,
            DriverValidationService driverValidationService,
            PasswordEncoder passwordEncoder
    ) {
        this.driverJpaRepository = driverJpaRepository;
        this.driverPersistenceMapper = driverPersistenceMapper;
        this.driverValidationService = driverValidationService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public DriverLoginResult authenticate(String email, String rawPassword) {
        String normalizedEmail = driverValidationService.normalizeEmail(email);
        String requiredPassword = requireCredential(rawPassword, "password");

        DriverEntity driverEntity = driverJpaRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> unauthorized(INVALID_CREDENTIALS_MESSAGE));

        String passwordHash = driverEntity.getPasswordHash();
        if (passwordHash == null || passwordHash.isBlank()) {
            throw unauthorized(INVALID_CREDENTIALS_MESSAGE);
        }

        if (!passwordEncoder.matches(requiredPassword, passwordHash)) {
            throw unauthorized(INVALID_CREDENTIALS_MESSAGE);
        }

        if (driverEntity.getRole() != UserRole.DRIVER) {
            throw forbidden(INELIGIBLE_ACCOUNT_MESSAGE);
        }

        Driver driver = driverPersistenceMapper.toDomain(driverEntity);
        if (driver.getAccountStatus() != DriverAccountStatus.ACTIVE) {
            throw forbidden(INELIGIBLE_ACCOUNT_MESSAGE);
        }

        return new DriverLoginResult(driver, LOGIN_SUCCESS_MESSAGE, null);
    }

    @Transactional(readOnly = true)
    public Driver getAuthenticatedDriver(String driverId) {
        DriverEntity driverEntity = driverJpaRepository.findById(requireCredential(driverId, "driverId"))
                .orElseThrow(() -> unauthorized(INVALID_CREDENTIALS_MESSAGE));
        Driver driver = driverPersistenceMapper.toDomain(driverEntity);
        if (driver.getRole() != UserRole.DRIVER || driver.getAccountStatus() != DriverAccountStatus.ACTIVE) {
            throw forbidden(INELIGIBLE_ACCOUNT_MESSAGE);
        }
        return driver;
    }

    private static String requireCredential(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must not be blank");
        }
        return value;
    }

    private static ResponseStatusException unauthorized(String message) {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
    }

    private static ResponseStatusException forbidden(String message) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    }

    public record DriverLoginResult(Driver driver, String message, String token) {
    }
}
