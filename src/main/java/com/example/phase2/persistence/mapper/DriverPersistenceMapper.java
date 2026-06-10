package com.example.phase2.persistence.mapper;

import com.example.phase2.domain.model.Driver;
import com.example.phase2.persistence.entity.DriverEntity;
import org.springframework.stereotype.Component;

@Component
public class DriverPersistenceMapper {

    public Driver toDomain(DriverEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Driver(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getRole(),
                entity.getAccountStatus(),
                entity.getLongTermReliabilityScore(),
                entity.getTotalSessions(),
                entity.getTotalDistanceKm(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeactivatedAt()
        );
    }

    public DriverEntity toEntity(Driver driver) {
        if (driver == null) {
            return null;
        }

        return new DriverEntity(
                driver.getId(),
                driver.getName(),
                driver.getEmail(),
                null,
                driver.getRole(),
                driver.getAccountStatus(),
                driver.getLongTermReliabilityScore(),
                driver.getTotalSessions(),
                driver.getTotalDistanceKm(),
                driver.getCreatedAt(),
                driver.getUpdatedAt(),
                driver.getDeactivatedAt()
        );
    }
}
