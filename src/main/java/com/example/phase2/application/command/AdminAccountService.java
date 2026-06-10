package com.example.phase2.application.command;

import com.example.phase2.config.TimeProvider;
import com.example.phase2.application.validation.AdminPasswordPolicyValidator;
import com.example.phase2.domain.enums.AdminAccountStatus;
import com.example.phase2.domain.model.AdminUser;
import com.example.phase2.exception.ResourceNotFoundException;
import com.example.phase2.persistence.entity.AdminUserEntity;
import com.example.phase2.persistence.mapper.AdminUserPersistenceMapper;
import com.example.phase2.persistence.repository.AdminUserJpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminAccountService {

    private static final String ADMIN_USER_RESOURCE_TYPE = "adminUser";
    private static final String ID_LOOKUP_FIELD = "id";

    private final AdminUserJpaRepository adminUserJpaRepository;
    private final AdminUserPersistenceMapper adminUserPersistenceMapper;
    private final AdminPasswordPolicyValidator adminPasswordPolicyValidator;
    private final PasswordEncoder passwordEncoder;
    private final TimeProvider timeProvider;

    public AdminAccountService(
            AdminUserJpaRepository adminUserJpaRepository,
            AdminUserPersistenceMapper adminUserPersistenceMapper,
            AdminPasswordPolicyValidator adminPasswordPolicyValidator,
            PasswordEncoder passwordEncoder,
            TimeProvider timeProvider
    ) {
        this.adminUserJpaRepository = adminUserJpaRepository;
        this.adminUserPersistenceMapper = adminUserPersistenceMapper;
        this.adminPasswordPolicyValidator = adminPasswordPolicyValidator;
        this.passwordEncoder = passwordEncoder;
        this.timeProvider = timeProvider;
    }

    @Transactional
    public AdminUser changePassword(
            String adminUserId,
            String currentPassword,
            String newPassword,
            String confirmNewPassword
    ) {
        AdminUser adminUser = resolveExistingAdminUser(adminUserId);
        return changePassword(adminUser, currentPassword, newPassword, confirmNewPassword);
    }

    @Transactional
    public AdminUser changePasswordForUsername(
            String username,
            String currentPassword,
            String newPassword,
            String confirmNewPassword
    ) {
        AdminUser adminUser = resolveExistingAdminUserByUsername(username);
        return changePassword(adminUser, currentPassword, newPassword, confirmNewPassword);
    }

    private AdminUser changePassword(
            AdminUser adminUser,
            String currentPassword,
            String newPassword,
            String confirmNewPassword
    ) {
        verifyCurrentPassword(adminUser, currentPassword);
        validateNewPassword(currentPassword, newPassword, confirmNewPassword);

        String encodedPassword = passwordEncoder.encode(newPassword);
        return persistPasswordChange(adminUser, encodedPassword, timeProvider.nowEpochMillis());
    }

    @Transactional
    public AdminUser updateAccountStatus(String adminUserId, AdminAccountStatus targetStatus) {
        AdminUser adminUser = resolveExistingAdminUser(adminUserId);
        return persistAccountStatusChange(adminUser, targetStatus, timeProvider.nowEpochMillis());
    }

    private AdminUser resolveExistingAdminUser(String adminUserId) {
        String requiredAdminUserId = requireText(adminUserId, "adminUserId");
        return adminUserJpaRepository.findById(requiredAdminUserId)
                .map(adminUserPersistenceMapper::toDomain)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Admin user not found: " + requiredAdminUserId,
                        ADMIN_USER_RESOURCE_TYPE,
                        requiredAdminUserId,
                        ID_LOOKUP_FIELD
                ));
    }

    private AdminUser resolveExistingAdminUserByUsername(String username) {
        String requiredUsername = requireText(username, "username");
        return adminUserJpaRepository.findByUsername(requiredUsername)
                .map(adminUserPersistenceMapper::toDomain)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Admin user not found: " + requiredUsername,
                        ADMIN_USER_RESOURCE_TYPE,
                        requiredUsername,
                        "username"
                ));
    }

    private void verifyCurrentPassword(AdminUser adminUser, String currentPassword) {
        String requiredCurrentPassword = requireText(currentPassword, "currentPassword");
        if (!passwordEncoder.matches(requiredCurrentPassword, adminUser.getPasswordHash())) {
            throw new IllegalArgumentException("currentPassword is incorrect");
        }
    }

    private void validateNewPassword(String currentPassword, String newPassword, String confirmNewPassword) {
        String requiredCurrentPassword = requireText(currentPassword, "currentPassword");
        String requiredNewPassword = requireText(newPassword, "newPassword");
        requireText(confirmNewPassword, "confirmNewPassword");

        if (requiredCurrentPassword.equals(requiredNewPassword)) {
            throw new IllegalArgumentException("newPassword must differ from currentPassword");
        }

        adminPasswordPolicyValidator.validatePasswordChange(requiredNewPassword, confirmNewPassword);
    }

    private AdminUser persistPasswordChange(AdminUser adminUser, String encodedPassword, Long changedAt) {
        AdminUserEntity adminUserEntity = adminUserJpaRepository.findById(adminUser.getId()).orElseThrow();
        adminUserEntity.setPasswordHash(encodedPassword);
        adminUserEntity.setUpdatedAt(requireChangedAt(changedAt));

        AdminUserEntity savedEntity = adminUserJpaRepository.save(adminUserEntity);
        return adminUserPersistenceMapper.toDomain(savedEntity);
    }

    private AdminUser persistAccountStatusChange(AdminUser adminUser, AdminAccountStatus targetStatus, Long changedAt) {
        AdminAccountStatus requiredTargetStatus = requireStatus(targetStatus);
        AdminUserEntity adminUserEntity = adminUserJpaRepository.findById(adminUser.getId()).orElseThrow();
        adminUserEntity.setAccountStatus(requiredTargetStatus);
        adminUserEntity.setUpdatedAt(requireChangedAt(changedAt));
        adminUserEntity.setDeactivatedAt(requiredTargetStatus == AdminAccountStatus.DEACTIVATED ? changedAt : null);

        AdminUserEntity savedEntity = adminUserJpaRepository.save(adminUserEntity);
        return adminUserPersistenceMapper.toDomain(savedEntity);
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

    private static Long requireChangedAt(Long changedAt) {
        if (changedAt == null) {
            throw new IllegalArgumentException("changedAt must not be null");
        }
        return changedAt;
    }

    private static AdminAccountStatus requireStatus(AdminAccountStatus targetStatus) {
        if (targetStatus == null) {
            throw new IllegalArgumentException("targetStatus must not be null");
        }
        return targetStatus;
    }
}
