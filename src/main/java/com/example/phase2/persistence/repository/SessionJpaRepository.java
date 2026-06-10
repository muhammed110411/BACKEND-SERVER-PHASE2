package com.example.phase2.persistence.repository;

import com.example.phase2.domain.enums.UploadProcessingStatus;
import com.example.phase2.persistence.entity.SessionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SessionJpaRepository
        extends JpaRepository<SessionEntity, String>, JpaSpecificationExecutor<SessionEntity> {

    List<SessionEntity> findByDriverIdOrderByStartTimestampDesc(String driverId);

    boolean existsById(String id);

    List<SessionEntity> findByUploadProcessingStatusOrderByUploadedAtDesc(
            UploadProcessingStatus uploadProcessingStatus
    );
}
