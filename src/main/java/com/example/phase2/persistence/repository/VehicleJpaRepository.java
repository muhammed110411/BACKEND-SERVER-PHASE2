package com.example.phase2.persistence.repository;

import com.example.phase2.persistence.entity.VehicleEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VehicleJpaRepository extends JpaRepository<VehicleEntity, String>, JpaSpecificationExecutor<VehicleEntity> {

    Optional<VehicleEntity> findByDisplayName(String displayName);

    boolean existsByDisplayName(String displayName);

    boolean existsByDisplayNameAndIdNot(String displayName, String id);

    boolean existsById(String id);

    List<VehicleEntity> findAllByOrderByDisplayNameAsc();
}
