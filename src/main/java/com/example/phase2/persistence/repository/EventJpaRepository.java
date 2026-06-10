package com.example.phase2.persistence.repository;

import com.example.phase2.domain.enums.EventType;
import com.example.phase2.persistence.entity.EventEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventJpaRepository extends JpaRepository<EventEntity, String> {

    List<EventEntity> findBySessionIdOrderByTimestampAsc(String sessionId);

    List<EventEntity> findByEventType(EventType eventType);

    long countByEventType(EventType eventType);
}
