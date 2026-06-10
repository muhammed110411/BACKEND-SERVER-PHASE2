package com.example.phase2.persistence.repository;

import com.example.phase2.persistence.entity.ApiClientEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiClientJpaRepository extends JpaRepository<ApiClientEntity, String> {

    Optional<ApiClientEntity> findByClientId(String clientId);

    boolean existsByClientId(String clientId);

    List<ApiClientEntity> findByActiveTrue();
}
