package com.example.phase2.persistence.repository;

import com.example.phase2.persistence.entity.AdminUserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserJpaRepository extends JpaRepository<AdminUserEntity, String> {

    Optional<AdminUserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsById(String id);
}
