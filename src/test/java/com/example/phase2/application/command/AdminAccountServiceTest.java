package com.example.phase2.application.command;

import com.example.phase2.config.TimeProvider;
import com.example.phase2.application.validation.AdminPasswordPolicyValidator;
import com.example.phase2.domain.enums.AdminAccountStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.domain.model.AdminUser;
import com.example.phase2.exception.ResourceNotFoundException;
import com.example.phase2.persistence.entity.AdminUserEntity;
import com.example.phase2.persistence.mapper.AdminUserPersistenceMapper;
import com.example.phase2.persistence.repository.AdminUserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminAccountServiceTest {

    private AdminUserJpaRepository adminUserJpaRepository;
    private PasswordEncoder passwordEncoder;
    private AdminAccountService adminAccountService;

    @BeforeEach
    void setUp() {
        adminUserJpaRepository = mock(AdminUserJpaRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        AdminUserPersistenceMapper adminUserPersistenceMapper = new AdminUserPersistenceMapper();
        AdminPasswordPolicyValidator adminPasswordPolicyValidator = new AdminPasswordPolicyValidator();
        TimeProvider timeProvider = () -> 1700001234567L;

        adminAccountService = new AdminAccountService(
                adminUserJpaRepository,
                adminUserPersistenceMapper,
                adminPasswordPolicyValidator,
                passwordEncoder,
                timeProvider
        );
    }

    @Test
    void changePasswordPersistsEncodedHashWithoutChangingIdentityFields() {
        AdminUserEntity adminUserEntity = buildAdminUserEntity(AdminAccountStatus.ACTIVE, null);

        when(adminUserJpaRepository.findById("admin-1"))
                .thenReturn(Optional.of(adminUserEntity))
                .thenReturn(Optional.of(adminUserEntity));
        when(passwordEncoder.matches("CurrentPass1", "stored-hash")).thenReturn(true);
        when(passwordEncoder.encode("NewPass123")).thenReturn("encoded-new-hash");
        when(adminUserJpaRepository.save(adminUserEntity)).thenReturn(adminUserEntity);

        AdminUser updatedAdminUser = adminAccountService.changePassword(
                "admin-1",
                "CurrentPass1",
                "NewPass123",
                "NewPass123"
        );

        assertEquals("admin-1", updatedAdminUser.getId());
        assertEquals("ops-admin", updatedAdminUser.getUsername());
        assertEquals("encoded-new-hash", updatedAdminUser.getPasswordHash());
        assertEquals(UserRole.ADMIN, updatedAdminUser.getRole());
        assertEquals(AdminAccountStatus.ACTIVE, updatedAdminUser.getAccountStatus());
        assertEquals(1700001234567L, updatedAdminUser.getUpdatedAt());

        verify(passwordEncoder).matches("CurrentPass1", "stored-hash");
        verify(passwordEncoder).encode("NewPass123");
        verify(adminUserJpaRepository, times(2)).findById("admin-1");
        verify(adminUserJpaRepository).save(adminUserEntity);
    }

    @Test
    void changePasswordFailsWhenCurrentPasswordIsIncorrect() {
        AdminUserEntity adminUserEntity = buildAdminUserEntity(AdminAccountStatus.ACTIVE, null);

        when(adminUserJpaRepository.findById("admin-1")).thenReturn(Optional.of(adminUserEntity));
        when(passwordEncoder.matches("WrongPass1", "stored-hash")).thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> adminAccountService.changePassword("admin-1", "WrongPass1", "NewPass123", "NewPass123")
        );

        verify(passwordEncoder).matches("WrongPass1", "stored-hash");
        verify(passwordEncoder, never()).encode("NewPass123");
        verify(adminUserJpaRepository, never()).save(adminUserEntity);
    }

    @Test
    void changePasswordFailsWhenNewPasswordMatchesCurrentPassword() {
        AdminUserEntity adminUserEntity = buildAdminUserEntity(AdminAccountStatus.ACTIVE, null);

        when(adminUserJpaRepository.findById("admin-1")).thenReturn(Optional.of(adminUserEntity));
        when(passwordEncoder.matches("CurrentPass1", "stored-hash")).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> adminAccountService.changePassword("admin-1", "CurrentPass1", "CurrentPass1", "CurrentPass1")
        );

        verify(passwordEncoder).matches("CurrentPass1", "stored-hash");
        verify(passwordEncoder, never()).encode("CurrentPass1");
        verify(adminUserJpaRepository, never()).save(adminUserEntity);
    }

    @Test
    void changePasswordForUsernameUsesAuthenticatedAdminIdentity() {
        AdminUserEntity adminUserEntity = buildAdminUserEntity(AdminAccountStatus.ACTIVE, null);

        when(adminUserJpaRepository.findByUsername("ops-admin")).thenReturn(Optional.of(adminUserEntity));
        when(adminUserJpaRepository.findById("admin-1")).thenReturn(Optional.of(adminUserEntity));
        when(passwordEncoder.matches("CurrentPass1", "stored-hash")).thenReturn(true);
        when(passwordEncoder.encode("NewPass123")).thenReturn("encoded-new-hash");
        when(adminUserJpaRepository.save(adminUserEntity)).thenReturn(adminUserEntity);

        AdminUser updatedAdminUser = adminAccountService.changePasswordForUsername(
                "ops-admin",
                "CurrentPass1",
                "NewPass123",
                "NewPass123"
        );

        assertEquals("admin-1", updatedAdminUser.getId());
        assertEquals("ops-admin", updatedAdminUser.getUsername());
        assertEquals("encoded-new-hash", updatedAdminUser.getPasswordHash());

        verify(adminUserJpaRepository).findByUsername("ops-admin");
        verify(adminUserJpaRepository).findById("admin-1");
        verify(passwordEncoder).matches("CurrentPass1", "stored-hash");
        verify(passwordEncoder).encode("NewPass123");
        verify(adminUserJpaRepository).save(adminUserEntity);
    }

    @Test
    void updateAccountStatusUpdatesOnlyApprovedAdminStateFields() {
        AdminUserEntity adminUserEntity = buildAdminUserEntity(AdminAccountStatus.SUSPENDED, null);

        when(adminUserJpaRepository.findById("admin-1"))
                .thenReturn(Optional.of(adminUserEntity))
                .thenReturn(Optional.of(adminUserEntity));
        when(adminUserJpaRepository.save(adminUserEntity)).thenReturn(adminUserEntity);

        AdminUser updatedAdminUser = adminAccountService.updateAccountStatus("admin-1", AdminAccountStatus.DEACTIVATED);

        assertEquals("admin-1", updatedAdminUser.getId());
        assertEquals("ops-admin", updatedAdminUser.getUsername());
        assertEquals(UserRole.ADMIN, updatedAdminUser.getRole());
        assertEquals(AdminAccountStatus.DEACTIVATED, updatedAdminUser.getAccountStatus());
        assertEquals(1700001234567L, updatedAdminUser.getUpdatedAt());
        assertEquals(1700001234567L, updatedAdminUser.getDeactivatedAt());
    }

    @Test
    void updateAccountStatusFailsWhenAdminUserDoesNotExist() {
        when(adminUserJpaRepository.findById("missing-admin")).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminAccountService.updateAccountStatus("missing-admin", AdminAccountStatus.INACTIVE)
        );
    }

    private AdminUserEntity buildAdminUserEntity(AdminAccountStatus accountStatus, Long deactivatedAt) {
        return new AdminUserEntity(
                "admin-1",
                "ops-admin",
                "stored-hash",
                accountStatus,
                UserRole.ADMIN,
                1699990000000L,
                1699995000000L,
                1699997000000L,
                deactivatedAt
        );
    }
}
