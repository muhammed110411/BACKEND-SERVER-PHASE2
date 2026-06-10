package com.example.phase2.persistence.repository;

import com.example.phase2.persistence.entity.SessionIngestionIssueEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionIngestionIssueJpaRepository
        extends JpaRepository<SessionIngestionIssueEntity, Long> {

    List<SessionIngestionIssueEntity> findBySessionIdOrderByDetectedAtAsc(String sessionId);

    boolean existsBySessionId(String sessionId);

    List<SessionIngestionIssueEntity> findByBlocksProcessingTrue();

    List<SessionIngestionIssueEntity> findByCode(String code);
}
