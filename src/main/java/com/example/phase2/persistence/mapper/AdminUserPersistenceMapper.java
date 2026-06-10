package com.example.phase2.persistence.mapper;

import com.example.phase2.domain.model.AdminUser;
import com.example.phase2.persistence.entity.AdminUserEntity;
import org.springframework.stereotype.Component;

@Component
public class AdminUserPersistenceMapper {

    public AdminUser toDomain(AdminUserEntity entity) {
        if (entity == null) {
            return null;
        }

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

    public AdminUserEntity toEntity(AdminUser adminUser) {
        if (adminUser == null) {
            return null;
        }

        return new AdminUserEntity(
                adminUser.getId(),
                adminUser.getUsername(),
                adminUser.getPasswordHash(),
                adminUser.getAccountStatus(),
                adminUser.getRole(),
                adminUser.getCreatedAt(),
                adminUser.getUpdatedAt(),
                adminUser.getLastLoginAt(),
                adminUser.getDeactivatedAt()
        );
    }
}
