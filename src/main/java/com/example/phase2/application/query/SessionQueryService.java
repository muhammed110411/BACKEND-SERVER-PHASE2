package com.example.phase2.application.query;

import com.example.phase2.api.admin.response.SessionDetailResponse;
import com.example.phase2.api.admin.response.SessionSummaryResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.api.driver.response.DriverSessionDetailResponse;
import com.example.phase2.api.driver.response.DriverSessionPenaltiesResponse;
import com.example.phase2.api.driver.response.DriverSessionSummaryResponse;
import com.example.phase2.application.mapper.AdminResponseMapper;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;
import com.example.phase2.domain.model.DrivingSessionRecord;
import com.example.phase2.domain.model.EscalationRecord;
import com.example.phase2.domain.model.IngestionAnomalyFlag;
import com.example.phase2.domain.model.SafetyEventRecord;
import com.example.phase2.domain.model.ScorePointRecord;
import com.example.phase2.domain.model.SessionDetailRecord;
import com.example.phase2.exception.ResourceNotFoundException;
import com.example.phase2.persistence.entity.EscalationEntity;
import com.example.phase2.persistence.entity.EventEntity;
import com.example.phase2.persistence.entity.ScorePointEntity;
import com.example.phase2.persistence.entity.SessionEntity;
import com.example.phase2.persistence.entity.SessionIngestionIssueEntity;
import com.example.phase2.persistence.mapper.EscalationPersistenceMapper;
import com.example.phase2.persistence.mapper.EventPersistenceMapper;
import com.example.phase2.persistence.mapper.ScorePointPersistenceMapper;
import com.example.phase2.persistence.mapper.SessionIngestionIssuePersistenceMapper;
import com.example.phase2.persistence.mapper.SessionPersistenceMapper;
import com.example.phase2.persistence.repository.DriverJpaRepository;
import com.example.phase2.persistence.repository.EscalationJpaRepository;
import com.example.phase2.persistence.repository.EventJpaRepository;
import com.example.phase2.persistence.repository.ScorePointJpaRepository;
import com.example.phase2.persistence.repository.SessionIngestionIssueJpaRepository;
import com.example.phase2.persistence.repository.SessionJpaRepository;
import com.example.phase2.persistence.repository.VehicleJpaRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SessionQueryService {

    private static final String SESSION_RESOURCE_TYPE = "session";
    private static final String ID_LOOKUP_FIELD = "id";
    private static final String DEFAULT_SORT_BY = "startTimestamp";

    private final SessionJpaRepository sessionJpaRepository;
    private final DriverJpaRepository driverJpaRepository;
    private final EventJpaRepository eventJpaRepository;
    private final EscalationJpaRepository escalationJpaRepository;
    private final ScorePointJpaRepository scorePointJpaRepository;
    private final SessionIngestionIssueJpaRepository sessionIngestionIssueJpaRepository;
    private final VehicleJpaRepository vehicleJpaRepository;
    private final AdminResponseMapper adminResponseMapper;
    private final PaginationUtils paginationUtils;
    private final SessionPersistenceMapper sessionPersistenceMapper;
    private final EventPersistenceMapper eventPersistenceMapper;
    private final EscalationPersistenceMapper escalationPersistenceMapper;
    private final ScorePointPersistenceMapper scorePointPersistenceMapper;
    private final SessionIngestionIssuePersistenceMapper sessionIngestionIssuePersistenceMapper;

    public SessionQueryService(
            SessionJpaRepository sessionJpaRepository,
            DriverJpaRepository driverJpaRepository,
            EventJpaRepository eventJpaRepository,
            EscalationJpaRepository escalationJpaRepository,
            ScorePointJpaRepository scorePointJpaRepository,
            SessionIngestionIssueJpaRepository sessionIngestionIssueJpaRepository,
            VehicleJpaRepository vehicleJpaRepository,
            AdminResponseMapper adminResponseMapper,
            PaginationUtils paginationUtils,
            SessionPersistenceMapper sessionPersistenceMapper,
            EventPersistenceMapper eventPersistenceMapper,
            EscalationPersistenceMapper escalationPersistenceMapper,
            ScorePointPersistenceMapper scorePointPersistenceMapper,
            SessionIngestionIssuePersistenceMapper sessionIngestionIssuePersistenceMapper
    ) {
        this.sessionJpaRepository = sessionJpaRepository;
        this.driverJpaRepository = driverJpaRepository;
        this.eventJpaRepository = eventJpaRepository;
        this.escalationJpaRepository = escalationJpaRepository;
        this.scorePointJpaRepository = scorePointJpaRepository;
        this.sessionIngestionIssueJpaRepository = sessionIngestionIssueJpaRepository;
        this.vehicleJpaRepository = vehicleJpaRepository;
        this.adminResponseMapper = adminResponseMapper;
        this.paginationUtils = paginationUtils;
        this.sessionPersistenceMapper = sessionPersistenceMapper;
        this.eventPersistenceMapper = eventPersistenceMapper;
        this.escalationPersistenceMapper = escalationPersistenceMapper;
        this.scorePointPersistenceMapper = scorePointPersistenceMapper;
        this.sessionIngestionIssuePersistenceMapper = sessionIngestionIssuePersistenceMapper;
    }

    public PagedResponse<SessionSummaryResponse> getSessions(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            String driverId,
            String vehicleId,
            SessionValidity validity,
            SessionEndStatus status,
            UploadProcessingStatus uploadProcessingStatus
    ) {
        Pageable pageable = paginationUtils.buildPageable(page, size, buildSort(sortBy, sortDirection));
        Page<DrivingSessionRecord> resultPage = sessionJpaRepository.findAll(
                        buildSessionSpecification(
                                normalizeOptionalText(driverId),
                                normalizeOptionalText(vehicleId),
                                validity,
                                status,
                                uploadProcessingStatus
                        ),
                        pageable
                )
                .map(sessionPersistenceMapper::toDomain);
        return toEnrichedSessionPage(resultPage);
    }

    public PagedResponse<SessionSummaryResponse> getSessionsByDriver(
            String driverId,
            int page,
            int size,
            String sortBy,
            String sortDirection,
            SessionValidity validity,
            SessionEndStatus status,
            UploadProcessingStatus uploadProcessingStatus
    ) {
        String requiredDriverId = requireText(driverId, "driverId");
        Pageable pageable = paginationUtils.buildPageable(page, size, buildSort(sortBy, sortDirection));
        Page<DrivingSessionRecord> resultPage = sessionJpaRepository.findAll(
                        buildSessionSpecification(
                                requiredDriverId,
                                null,
                                validity,
                                status,
                                uploadProcessingStatus
                        ),
                        pageable
                )
                .map(sessionPersistenceMapper::toDomain);
        return toEnrichedSessionPage(resultPage);
    }

    public SessionDetailResponse getSessionDetail(String sessionId) {
        String requiredSessionId = requireText(sessionId, "sessionId");
        SessionEntity sessionEntity = sessionJpaRepository.findById(requiredSessionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session not found: " + requiredSessionId,
                        SESSION_RESOURCE_TYPE,
                        requiredSessionId,
                        ID_LOOKUP_FIELD
                ));

        SessionDetailRecord sessionDetail = new SessionDetailRecord(
                sessionPersistenceMapper.toDomain(sessionEntity),
                mapEvents(eventJpaRepository.findBySessionIdOrderByTimestampAsc(requiredSessionId)),
                mapEscalations(escalationJpaRepository.findBySessionIdOrderByTriggeredAtAsc(requiredSessionId)),
                mapScoreHistory(scorePointJpaRepository.findBySessionIdOrderByTimestampAsc(requiredSessionId)),
                mapIngestionIssues(sessionIngestionIssueJpaRepository.findBySessionIdOrderByDetectedAtAsc(requiredSessionId))
        );

        return adminResponseMapper.toSessionDetailResponse(sessionDetail, findDriverName(sessionEntity.getDriverId()));
    }

    public PagedResponse<DriverSessionSummaryResponse> getDriverSessions(
            String driverId,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        String requiredDriverId = requireText(driverId, "driverId");
        Pageable pageable = paginationUtils.buildPageable(page, size, buildSort(sortBy, sortDirection));
        Page<DrivingSessionRecord> resultPage = sessionJpaRepository.findAll(
                        buildSessionSpecification(requiredDriverId, null, null, null, null),
                        pageable
                )
                .map(sessionPersistenceMapper::toDomain);
        String driverName = findDriverName(requiredDriverId);
        Map<String, String> vehicleNames = findVehicleNames(resultPage.getContent());
        Page<DriverSessionSummaryResponse> responsePage = resultPage.map(session ->
                toDriverSummaryResponse(session, driverName, vehicleNames.get(session.getVehicleId()))
        );
        return paginationUtils.toPagedResponse(responsePage);
    }

    public DriverSessionDetailResponse getDriverSessionDetail(String driverId, String sessionId) {
        String requiredDriverId = requireText(driverId, "driverId");
        String requiredSessionId = requireText(sessionId, "sessionId");
        SessionEntity sessionEntity = sessionJpaRepository.findById(requiredSessionId)
                .filter(session -> Objects.equals(requiredDriverId, session.getDriverId()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session not found: " + requiredSessionId,
                        SESSION_RESOURCE_TYPE,
                        requiredSessionId,
                        ID_LOOKUP_FIELD
                ));

        SessionDetailRecord sessionDetail = new SessionDetailRecord(
                sessionPersistenceMapper.toDomain(sessionEntity),
                mapEvents(eventJpaRepository.findBySessionIdOrderByTimestampAsc(requiredSessionId)),
                mapEscalations(escalationJpaRepository.findBySessionIdOrderByTriggeredAtAsc(requiredSessionId)),
                mapScoreHistory(scorePointJpaRepository.findBySessionIdOrderByTimestampAsc(requiredSessionId)),
                mapIngestionIssues(sessionIngestionIssueJpaRepository.findBySessionIdOrderByDetectedAtAsc(requiredSessionId))
        );

        return toDriverDetailResponse(
                sessionDetail,
                findDriverName(requiredDriverId),
                findVehicleName(sessionEntity.getVehicleId())
        );
    }

    private PagedResponse<SessionSummaryResponse> toEnrichedSessionPage(Page<DrivingSessionRecord> resultPage) {
        Map<String, String> driverNames = findDriverNames(resultPage.getContent());
        Page<SessionSummaryResponse> responsePage = resultPage.map(session ->
                adminResponseMapper.toSessionSummaryResponse(session, driverNames.get(session.getDriverId()))
        );
        return paginationUtils.toPagedResponse(responsePage);
    }

    private Map<String, String> findDriverNames(List<DrivingSessionRecord> sessions) {
        List<String> driverIds = sessions.stream()
                .map(DrivingSessionRecord::getDriverId)
                .distinct()
                .toList();
        if (driverIds.isEmpty()) {
            return Map.of();
        }
        return driverJpaRepository.findAllById(driverIds).stream()
                .collect(Collectors.toMap(
                        driver -> driver.getId(),
                        driver -> driver.getName(),
                        (left, ignored) -> left
                ));
    }

    private Map<String, String> findVehicleNames(List<DrivingSessionRecord> sessions) {
        List<String> vehicleIds = sessions.stream()
                .map(DrivingSessionRecord::getVehicleId)
                .distinct()
                .toList();
        if (vehicleIds.isEmpty()) {
            return Map.of();
        }
        return vehicleJpaRepository.findAllById(vehicleIds).stream()
                .collect(Collectors.toMap(
                        vehicle -> vehicle.getId(),
                        vehicle -> vehicle.getDisplayName(),
                        (left, ignored) -> left
                ));
    }

    private String findVehicleName(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return null;
        }
        return vehicleJpaRepository.findById(vehicleId)
                .map(vehicle -> vehicle.getDisplayName())
                .orElse(null);
    }

    private String findDriverName(String driverId) {
        if (driverId == null || driverId.isBlank()) {
            return null;
        }
        return driverJpaRepository.findById(driverId)
                .map(driver -> driver.getName())
                .orElse(null);
    }

    public boolean existsBySessionId(String sessionId) {
        return sessionJpaRepository.existsById(requireText(sessionId, "sessionId"));
    }

    private DriverSessionSummaryResponse toDriverSummaryResponse(
            DrivingSessionRecord session,
            String driverName,
            String vehicleName
    ) {
        return new DriverSessionSummaryResponse(
                session.getId(),
                session.getDriverId(),
                driverName,
                session.getVehicleId(),
                vehicleName,
                session.getFinalScore(),
                session.getValidity(),
                session.getStatus(),
                session.getStartTimestamp(),
                session.getEndTimestamp(),
                session.getUploadedAt(),
                session.getTotalEventCount(),
                session.getTotalEscalationCount()
        );
    }

    private DriverSessionDetailResponse toDriverDetailResponse(
            SessionDetailRecord sessionDetail,
            String driverName,
            String vehicleName
    ) {
        DrivingSessionRecord session = sessionDetail.getSession();
        return new DriverSessionDetailResponse(
                session.getId(),
                session.getDriverId(),
                driverName,
                session.getVehicleId(),
                vehicleName,
                session.getFinalScore(),
                session.getValidity(),
                session.getStatus(),
                session.getStartTimestamp(),
                session.getEndTimestamp(),
                session.getUploadedAt(),
                session.getTotalEventCount(),
                session.getTotalEscalationCount(),
                new DriverSessionPenaltiesResponse(
                        session.getTotalContinuousPenalty(),
                        session.getTotalEventPenalty(),
                        session.getTotalEscalationPenalty()
                ),
                sessionDetail.getEvents().stream()
                        .map(adminResponseMapper::toSessionEventResponse)
                        .toList(),
                sessionDetail.getEscalations().stream()
                        .map(adminResponseMapper::toSessionEscalationResponse)
                        .toList(),
                sessionDetail.getScoreHistory().stream()
                        .map(adminResponseMapper::toSessionScorePointResponse)
                        .toList(),
                sessionDetail.getIngestionAnomalyFlags().stream()
                        .map(adminResponseMapper::toIngestionAnomalyFlagResponse)
                        .toList()
        );
    }

    private Specification<SessionEntity> buildSessionSpecification(
            String driverId,
            String vehicleId,
            SessionValidity validity,
            SessionEndStatus status,
            UploadProcessingStatus uploadProcessingStatus
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (driverId != null) {
                predicates.add(criteriaBuilder.equal(root.get("driverId"), driverId));
            }
            if (vehicleId != null) {
                predicates.add(criteriaBuilder.equal(root.get("vehicleId"), vehicleId));
            }
            if (validity != null) {
                predicates.add(criteriaBuilder.equal(root.get("validity"), validity));
            }
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (uploadProcessingStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("uploadProcessingStatus"), uploadProcessingStatus));
            }

            return predicates.isEmpty()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private Sort buildSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, resolveSortProperty(sortBy));
    }

    private String resolveSortProperty(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return DEFAULT_SORT_BY;
        }

        return switch (sortBy.trim()) {
            case "id", "driverId", "vehicleId", "startTimestamp", "endTimestamp", "status",
                    "validity", "uploadProcessingStatus", "finalScore", "totalDurationSeconds",
                    "totalDistanceKm", "totalEventCount", "totalEscalationCount",
                    "uploadedAt", "processedAt", "sourceClientId" -> sortBy.trim();
            case "sessionId" -> "id";
            default -> DEFAULT_SORT_BY;
        };
    }

    private List<SafetyEventRecord> mapEvents(List<EventEntity> eventEntities) {
        return eventEntities.stream()
                .map(eventPersistenceMapper::toDomain)
                .toList();
    }

    private List<EscalationRecord> mapEscalations(List<EscalationEntity> escalationEntities) {
        return escalationEntities.stream()
                .map(escalationPersistenceMapper::toDomain)
                .toList();
    }

    private List<ScorePointRecord> mapScoreHistory(List<ScorePointEntity> scorePointEntities) {
        return scorePointEntities.stream()
                .map(scorePointPersistenceMapper::toDomain)
                .toList();
    }

    private List<IngestionAnomalyFlag> mapIngestionIssues(List<SessionIngestionIssueEntity> issueEntities) {
        return issueEntities.stream()
                .map(sessionIngestionIssuePersistenceMapper::toDomain)
                .toList();
    }

    private static String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
