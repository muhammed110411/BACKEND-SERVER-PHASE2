package com.example.phase2.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.phase2.domain.enums.AdminAccountStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.domain.model.AdminUser;
import com.example.phase2.exception.UnauthorizedAdminAuthenticationException;
import com.example.phase2.persistence.entity.AdminUserEntity;
import com.example.phase2.persistence.repository.AdminUserJpaRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class AdminAuthenticationServiceTest {

    private AdminUserJpaRepository adminUserJpaRepository;
    private PasswordEncoder passwordEncoder;
    private AdminAuthenticationService adminAuthenticationService;

    @BeforeEach
    void setUp() {
        adminUserJpaRepository = mock(AdminUserJpaRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        adminAuthenticationService = new AdminAuthenticationService(adminUserJpaRepository, passwordEncoder);
    }

    @Test
    void authenticateReturnsValidatedAdminUserWhenCredentialsAndEligibilityAreValid() {
        AdminUserEntity adminUserEntity = adminUserEntity("admin-user", "stored-hash", AdminAccountStatus.ACTIVE, UserRole.ADMIN);
        when(adminUserJpaRepository.findByUsername("admin-user")).thenReturn(Optional.of(adminUserEntity));
        when(passwordEncoder.matches("Password123", "stored-hash")).thenReturn(true);

        AdminUser adminUser = adminAuthenticationService.authenticate("admin-user", "Password123");

        assertEquals("admin-1", adminUser.getId());
        assertEquals("admin-user", adminUser.getUsername());
        assertEquals(AdminAccountStatus.ACTIVE, adminUser.getAccountStatus());
        assertEquals(UserRole.ADMIN, adminUser.getRole());
        verify(adminUserJpaRepository).findByUsername("admin-user");
        verify(passwordEncoder).matches("Password123", "stored-hash");
    }

    @Test
    void authenticateRejectsBlankUsername() {
        UnauthorizedAdminAuthenticationException exception = assertThrows(
                UnauthorizedAdminAuthenticationException.class,
                () -> adminAuthenticationService.authenticate(" ", "Password123")
        );

        assertEquals("UNAUTHORIZED_ADMIN_AUTHENTICATION", exception.getErrorCode());
        verify(adminUserJpaRepository, never()).findByUsername(org.mockito.ArgumentMatchers.anyString());
        verify(passwordEncoder, never()).matches(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void authenticateRejectsBlankPassword() {
        UnauthorizedAdminAuthenticationException exception = assertThrows(
                UnauthorizedAdminAuthenticationException.class,
                () -> adminAuthenticationService.authenticate("admin-user", " ")
        );

        assertEquals("UNAUTHORIZED_ADMIN_AUTHENTICATION", exception.getErrorCode());
        verify(adminUserJpaRepository, never()).findByUsername(org.mockito.ArgumentMatchers.anyString());
        verify(passwordEncoder, never()).matches(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void authenticateRejectsUnknownUsername() {
        when(adminUserJpaRepository.findByUsername("missing-admin")).thenReturn(Optional.empty());

        UnauthorizedAdminAuthenticationException exception = assertThrows(
                UnauthorizedAdminAuthenticationException.class,
                () -> adminAuthenticationService.authenticate("missing-admin", "Password123")
        );

        assertEquals("UNAUTHORIZED_ADMIN_AUTHENTICATION", exception.getErrorCode());
        assertEquals("missing-admin", exception.getUsername());
        verify(passwordEncoder, never()).matches(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void authenticateRejectsMissingPasswordHash() {
        AdminUserEntity adminUserEntity = adminUserEntity("admin-user", "stored-hash", AdminAccountStatus.ACTIVE, UserRole.ADMIN);
        adminUserEntity.setPasswordHash(" ");
        when(adminUserJpaRepository.findByUsername("admin-user")).thenReturn(Optional.of(adminUserEntity));

        UnauthorizedAdminAuthenticationException exception = assertThrows(
                UnauthorizedAdminAuthenticationException.class,
                () -> adminAuthenticationService.authenticate("admin-user", "Password123")
        );

        assertEquals("UNAUTHORIZED_ADMIN_AUTHENTICATION", exception.getErrorCode());
        verify(passwordEncoder, never()).matches(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void authenticateRejectsInvalidPassword() {
        AdminUserEntity adminUserEntity = adminUserEntity("admin-user", "stored-hash", AdminAccountStatus.ACTIVE, UserRole.ADMIN);
        when(adminUserJpaRepository.findByUsername("admin-user")).thenReturn(Optional.of(adminUserEntity));
        when(passwordEncoder.matches("WrongPassword123", "stored-hash")).thenReturn(false);

        UnauthorizedAdminAuthenticationException exception = assertThrows(
                UnauthorizedAdminAuthenticationException.class,
                () -> adminAuthenticationService.authenticate("admin-user", "WrongPassword123")
        );

        assertEquals("UNAUTHORIZED_ADMIN_AUTHENTICATION", exception.getErrorCode());
        verify(passwordEncoder).matches("WrongPassword123", "stored-hash");
    }

    @Test
    void authenticateRejectsIneligibleAccountStatus() {
        AdminUserEntity adminUserEntity = adminUserEntity("admin-user", "stored-hash", AdminAccountStatus.SUSPENDED, UserRole.ADMIN);
        when(adminUserJpaRepository.findByUsername("admin-user")).thenReturn(Optional.of(adminUserEntity));
        when(passwordEncoder.matches("Password123", "stored-hash")).thenReturn(true);

        UnauthorizedAdminAuthenticationException exception = assertThrows(
                UnauthorizedAdminAuthenticationException.class,
                () -> adminAuthenticationService.authenticate("admin-user", "Password123")
        );

        assertEquals("UNAUTHORIZED_ADMIN_AUTHENTICATION", exception.getErrorCode());
        assertEquals("admin-user", exception.getUsername());
    }

    @Test
    void validateAdminEligibilityRejectsNullAdminUser() {
        assertThrows(
                UnauthorizedAdminAuthenticationException.class,
                () -> adminAuthenticationService.validateAdminEligibility(null)
        );
    }

    @Test
    void supportsAdminAccessReturnsFalseForNullOrIneligibleAdminUser() {
        assertFalse(adminAuthenticationService.supportsAdminAccess(null));
        assertFalse(adminAuthenticationService.supportsAdminAccess(
                adminUser("inactive-admin", AdminAccountStatus.INACTIVE)
        ));
        assertFalse(adminAuthenticationService.supportsAdminAccess(
                adminUser("suspended-admin", AdminAccountStatus.SUSPENDED)
        ));
        assertFalse(adminAuthenticationService.supportsAdminAccess(
                adminUser("deactivated-admin", AdminAccountStatus.DEACTIVATED)
        ));
        assertTrue(adminAuthenticationService.supportsAdminAccess(
                adminUser("active-admin", AdminAccountStatus.ACTIVE)
        ));
    }

    private static AdminUserEntity adminUserEntity(
            String username,
            String passwordHash,
            AdminAccountStatus accountStatus,
            UserRole role
    ) {
        Long deactivatedAt = accountStatus == AdminAccountStatus.DEACTIVATED ? 200L : null;
        return new AdminUserEntity(
                "admin-1",
                username,
                passwordHash,
                accountStatus,
                role,
                100L,
                100L,
                null,
                deactivatedAt
        );
    }

    private static AdminUser adminUser(String username, AdminAccountStatus accountStatus) {
        Long deactivatedAt = accountStatus == AdminAccountStatus.DEACTIVATED ? 200L : null;
        return new AdminUser(
                "admin-1",
                username,
                "stored-hash",
                accountStatus,
                UserRole.ADMIN,
                100L,
                100L,
                null,
                deactivatedAt
        );
    }
}
