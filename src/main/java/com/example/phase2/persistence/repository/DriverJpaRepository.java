package com.example.phase2.persistence.repository;

import com.example.phase2.persistence.entity.DriverEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DriverJpaRepository extends JpaRepository<DriverEntity, String>, JpaSpecificationExecutor<DriverEntity> {

    Optional<DriverEntity> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, String id);

    boolean existsById(String id);
}
