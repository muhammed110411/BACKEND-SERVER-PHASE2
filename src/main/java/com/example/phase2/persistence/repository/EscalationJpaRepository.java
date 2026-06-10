package com.example.phase2.persistence.repository;

import com.example.phase2.domain.enums.EscalationType;
import com.example.phase2.persistence.entity.EscalationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EscalationJpaRepository extends JpaRepository<EscalationEntity, String> {

    List<EscalationEntity> findBySessionIdOrderByTriggeredAtAsc(String sessionId);

    List<EscalationEntity> findByEscalationType(EscalationType escalationType);

    long countByEscalationType(EscalationType escalationType);
}
