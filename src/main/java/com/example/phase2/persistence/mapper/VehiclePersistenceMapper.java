package com.example.phase2.persistence.mapper;

import com.example.phase2.domain.model.Vehicle;
import com.example.phase2.persistence.entity.VehicleEntity;
import org.springframework.stereotype.Component;

@Component
public class VehiclePersistenceMapper {

    public Vehicle toDomain(VehicleEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Vehicle(
                entity.getId(),
                entity.getDisplayName(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeactivatedAt()
        );
    }

    public VehicleEntity toEntity(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }

        return new VehicleEntity(
                vehicle.getId(),
                vehicle.getDisplayName(),
                vehicle.getStatus(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt(),
                vehicle.getDeactivatedAt()
        );
    }
}
