package com.example.phase2.persistence.repository;

import com.example.phase2.persistence.entity.ScorePointEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScorePointJpaRepository extends JpaRepository<ScorePointEntity, Long> {

    List<ScorePointEntity> findBySessionIdOrderByTimestampAsc(String sessionId);
}
