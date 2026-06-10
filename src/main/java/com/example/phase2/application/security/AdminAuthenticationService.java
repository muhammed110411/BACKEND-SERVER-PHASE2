package com.example.phase2.application.security;

import com.example.phase2.domain.enums.AdminAccountStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.domain.model.AdminUser;
import com.example.phase2.exception.UnauthorizedAdminAuthenticationException;
import com.example.phase2.persistence.entity.AdminUserEntity;
import com.example.phase2.persistence.repository.AdminUserJpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminAuthenticationService {

    private static final String UNAUTHORIZED_ADMIN_ERROR_CODE = "UNAUTHORIZED_ADMIN_AUTHENTICATION";
    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid admin username or password.";
    private static final String INELIGIBLE_ACCOUNT_MESSAGE = "Admin account is not allowed to sign in.";

    private final AdminUserJpaRepository adminUserJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminAuthenticationService(
            AdminUserJpaRepository adminUserJpaRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.adminUserJpaRepository = adminUserJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public AdminUser authenticate(String username, String rawPassword) {
        String requiredUsername = requireCredential(username, "username");
        String requiredPassword = requireCredential(rawPassword, "rawPassword");

        AdminUserEntity adminUserEntity = adminUserJpaRepository.findByUsername(requiredUsername)
                .orElseThrow(() -> unauthorized(requiredUsername, INVALID_CREDENTIALS_MESSAGE));

        String passwordHash = adminUserEntity.getPasswordHash();
        if (passwordHash == null || passwordHash.isBlank()) {
            throw unauthorized(requiredUsername, INVALID_CREDENTIALS_MESSAGE);
        }

        if (!passwordEncoder.matches(requiredPassword, passwordHash)) {
            throw unauthorized(requiredUsername, INVALID_CREDENTIALS_MESSAGE);
        }

        if (adminUserEntity.getRole() != UserRole.ADMIN) {
            throw unauthorized(requiredUsername, INELIGIBLE_ACCOUNT_MESSAGE);
        }

        AdminUser adminUser = toDomain(adminUserEntity);
        validateAdminEligibility(adminUser);
        return adminUser;
    }

    public void validateAdminEligibility(AdminUser adminUser) {
        if (!supportsAdminAccess(adminUser)) {
            String username = adminUser == null ? null : adminUser.getUsername();
            throw unauthorized(username, INELIGIBLE_ACCOUNT_MESSAGE);
        }
    }

    public boolean supportsAdminAccess(AdminUser adminUser) {
        return adminUser != null
                && adminUser.getRole() == UserRole.ADMIN
                && adminUser.getAccountStatus() == AdminAccountStatus.ACTIVE;
    }

    private AdminUser toDomain(AdminUserEntity entity) {
        return new AdminUser(
                entity.getId(),
                entity.getUsername(),
                entity.getPasswordHash(),
                entity.getAccountStatus(),
                entity.getRole(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getLastLoginAt(),
                entity.getDeactivatedAt()
        );
    }

    private static String requireCredential(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw unauthorized(null, fieldName + " must not be blank");
        }
        return value;
    }

    private static UnauthorizedAdminAuthenticationException unauthorized(String username, String message) {
        return new UnauthorizedAdminAuthenticationException(message, username, UNAUTHORIZED_ADMIN_ERROR_CODE);
    }
}
