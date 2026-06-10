package com.example.phase2.application.query;

import com.example.phase2.api.admin.response.DriverAnalyticsResponse;
import com.example.phase2.api.admin.response.EscalationCountResponse;
import com.example.phase2.api.admin.response.EventCountResponse;
import com.example.phase2.api.admin.response.GlobalAnalyticsResponse;
import com.example.phase2.api.admin.response.ScoreTrendPointResponse;
import com.example.phase2.application.mapper.AdminResponseMapper;
import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.EscalationType;
import com.example.phase2.domain.enums.EventType;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.domain.enums.VehicleStatus;
import com.example.phase2.domain.model.Driver;
import com.example.phase2.exception.ResourceNotFoundException;
import com.example.phase2.persistence.entity.DriverEntity;
import com.example.phase2.persistence.entity.EscalationEntity;
import com.example.phase2.persistence.entity.EventEntity;
import com.example.phase2.persistence.entity.SessionEntity;
import com.example.phase2.persistence.entity.VehicleEntity;
import com.example.phase2.persistence.repository.DriverJpaRepository;
import com.example.phase2.persistence.repository.EscalationJpaRepository;
import com.example.phase2.persistence.repository.EventJpaRepository;
import com.example.phase2.persistence.repository.SessionJpaRepository;
import com.example.phase2.persistence.repository.VehicleJpaRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AnalyticsQueryService {

    private static final String DRIVER_RESOURCE_TYPE = "driver";
    private static final String ID_LOOKUP_FIELD = "id";

    private final SessionJpaRepository sessionJpaRepository;
    private final EventJpaRepository eventJpaRepository;
    private final EscalationJpaRepository escalationJpaRepository;
    private final DriverJpaRepository driverJpaRepository;
    private final VehicleJpaRepository vehicleJpaRepository;
    private final AdminResponseMapper adminResponseMapper;

    public AnalyticsQueryService(
            SessionJpaRepository sessionJpaRepository,
            EventJpaRepository eventJpaRepository,
            EscalationJpaRepository escalationJpaRepository,
            DriverJpaRepository driverJpaRepository,
            VehicleJpaRepository vehicleJpaRepository,
            AdminResponseMapper adminResponseMapper
    ) {
        this.sessionJpaRepository = sessionJpaRepository;
        this.eventJpaRepository = eventJpaRepository;
        this.escalationJpaRepository = escalationJpaRepository;
        this.driverJpaRepository = driverJpaRepository;
        this.vehicleJpaRepository = vehicleJpaRepository;
        this.adminResponseMapper = adminResponseMapper;
    }

    public DriverAnalyticsResponse getDriverAnalytics(
            String driverId,
            Long fromTimestamp,
            Long toTimestamp
    ) {
        validateRange(fromTimestamp, toTimestamp);
        Driver driver = toDriver(loadDriver(driverId));
        List<SessionEntity> sessions = sessionJpaRepository.findAll(
                buildSessionSpecification(driver.getId(), fromTimestamp, toTimestamp)
        );

        List<EventEntity> events = filterEventsForSessions(sessions);
        List<EscalationEntity> escalations = filterEscalationsForSessions(sessions);

        return adminResponseMapper.toDriverAnalyticsResponse(
                driver,
                sessions.stream().mapToDouble(SessionEntity::getFinalScore).average().orElse(0.0d),
                countSessionsByValidity(sessions, SessionValidity.VALID),
                countSessionsByValidity(sessions, SessionValidity.INVALID),
                countSessionsByStatus(sessions, SessionEndStatus.COMPLETED),
                countSessionsByStatus(sessions, SessionEndStatus.ABORTED),
                Math.toIntExact(events.size()),
                Math.toIntExact(escalations.size()),
                buildScoreTrend(sessions),
                buildEventCounts(events),
                buildEscalationCounts(escalations)
        );
    }

    public GlobalAnalyticsResponse getGlobalAnalytics(
            Long fromTimestamp,
            Long toTimestamp
    ) {
        validateRange(fromTimestamp, toTimestamp);

        List<DriverEntity> drivers = driverJpaRepository.findAll();
        List<VehicleEntity> vehicles = vehicleJpaRepository.findAll();
        List<SessionEntity> sessions = sessionJpaRepository.findAll(
                buildSessionSpecification(null, fromTimestamp, toTimestamp)
        );
        List<EventEntity> events = filterEventsForSessions(sessions);
        List<EscalationEntity> escalations = filterEscalationsForSessions(sessions);

        return adminResponseMapper.toGlobalAnalyticsResponse(
                drivers.size(),
                countDriversByStatus(drivers, DriverAccountStatus.ACTIVE),
                countDriversByStatus(drivers, DriverAccountStatus.INACTIVE),
                countDriversByStatus(drivers, DriverAccountStatus.SUSPENDED),
                countDriversByStatus(drivers, DriverAccountStatus.DEACTIVATED),
                vehicles.size(),
                countVehiclesByStatus(vehicles, VehicleStatus.AVAILABLE),
                countVehiclesByStatus(vehicles, VehicleStatus.BUSY),
                countVehiclesByStatus(vehicles, VehicleStatus.OFFLINE),
                sessions.size(),
                countSessionsByValidity(sessions, SessionValidity.VALID),
                countSessionsByValidity(sessions, SessionValidity.INVALID),
                countSessionsByStatus(sessions, SessionEndStatus.COMPLETED),
                countSessionsByStatus(sessions, SessionEndStatus.ABORTED),
                countSessionsByProcessingStatus(sessions, UploadProcessingStatus.PROCESSED),
                countSessionsByProcessingStatus(sessions, UploadProcessingStatus.PROCESSED_WITH_WARNINGS),
                countSessionsByProcessingStatus(sessions, UploadProcessingStatus.REJECTED),
                sessions.stream().mapToDouble(SessionEntity::getFinalScore).average().orElse(0.0d),
                sessions.stream().mapToDouble(SessionEntity::getTotalDistanceKm).sum(),
                Math.toIntExact(events.size()),
                Math.toIntExact(escalations.size()),
                buildSessionScoreTrend(sessions, drivers),
                buildEventCounts(events),
                buildEscalationCounts(escalations)
        );
    }

    public List<ScoreTrendPointResponse> getDriverScoreTrend(
            String driverId,
            Long fromTimestamp,
            Long toTimestamp
    ) {
        validateRange(fromTimestamp, toTimestamp);
        DriverEntity driver = loadDriver(driverId);
        return buildScoreTrend(sessionJpaRepository.findAll(
                buildSessionSpecification(driver.getId(), fromTimestamp, toTimestamp)
        ));
    }

    public List<ScoreTrendPointResponse> getGlobalScoreTrend(
            Long fromTimestamp,
            Long toTimestamp
    ) {
        validateRange(fromTimestamp, toTimestamp);
        return buildScoreTrend(sessionJpaRepository.findAll(
                buildSessionSpecification(null, fromTimestamp, toTimestamp)
        ));
    }

    public List<EventCountResponse> getDriverEventCounts(
            String driverId,
            Long fromTimestamp,
            Long toTimestamp
    ) {
        validateRange(fromTimestamp, toTimestamp);
        DriverEntity driver = loadDriver(driverId);
        return buildEventCounts(filterEventsForSessions(sessionJpaRepository.findAll(
                buildSessionSpecification(driver.getId(), fromTimestamp, toTimestamp)
        )));
    }

    public List<EventCountResponse> getGlobalEventCounts(
            Long fromTimestamp,
            Long toTimestamp
    ) {
        validateRange(fromTimestamp, toTimestamp);
        return buildEventCounts(filterEventsForSessions(sessionJpaRepository.findAll(
                buildSessionSpecification(null, fromTimestamp, toTimestamp)
        )));
    }

    public List<EscalationCountResponse> getDriverEscalationCounts(
            String driverId,
            Long fromTimestamp,
            Long toTimestamp
    ) {
        validateRange(fromTimestamp, toTimestamp);
        DriverEntity driver = loadDriver(driverId);
        return buildEscalationCounts(filterEscalationsForSessions(sessionJpaRepository.findAll(
                buildSessionSpecification(driver.getId(), fromTimestamp, toTimestamp)
        )));
    }

    public List<EscalationCountResponse> getGlobalEscalationCounts(
            Long fromTimestamp,
            Long toTimestamp
    ) {
        validateRange(fromTimestamp, toTimestamp);
        return buildEscalationCounts(filterEscalationsForSessions(sessionJpaRepository.findAll(
                buildSessionSpecification(null, fromTimestamp, toTimestamp)
        )));
    }

    private DriverEntity loadDriver(String driverId) {
        String resolvedDriverId = requireText(driverId, "driverId");
        return driverJpaRepository.findById(resolvedDriverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver not found: " + resolvedDriverId,
                        DRIVER_RESOURCE_TYPE,
                        resolvedDriverId,
                        ID_LOOKUP_FIELD
                ));
    }

    private Specification<SessionEntity> buildSessionSpecification(
            String driverId,
            Long fromTimestamp,
            Long toTimestamp
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (driverId != null) {
                predicates.add(criteriaBuilder.equal(root.get("driverId"), driverId));
            }
            if (fromTimestamp != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startTimestamp"), fromTimestamp));
            }
            if (toTimestamp != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endTimestamp"), toTimestamp));
            }

            return predicates.isEmpty()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private List<ScoreTrendPointResponse> buildScoreTrend(List<SessionEntity> sessions) {
        Map<Long, List<SessionEntity>> sessionsByDay = sessions.stream()
                .sorted(Comparator.comparingLong(SessionEntity::getUploadedAt))
                .collect(Collectors.groupingBy(
                        this::toDayBucket,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return sessionsByDay.entrySet().stream()
                .map(entry -> adminResponseMapper.toScoreTrendPointResponse(
                        entry.getKey(),
                        entry.getValue().stream().mapToDouble(SessionEntity::getFinalScore).average().orElse(0.0d),
                        entry.getValue().size()
                ))
                .toList();
    }

    private List<ScoreTrendPointResponse> buildSessionScoreTrend(
            List<SessionEntity> sessions,
            List<DriverEntity> drivers
    ) {
        Map<String, String> driverNamesById = new LinkedHashMap<>();
        drivers.forEach(driver -> driverNamesById.put(driver.getId(), driver.getName()));

        return sessions.stream()
                .sorted(Comparator.comparingLong(SessionEntity::getUploadedAt))
                .map(session -> adminResponseMapper.toScoreTrendPointResponse(
                        session.getUploadedAt(),
                        session.getFinalScore(),
                        1,
                        session.getId(),
                        session.getDriverId(),
                        driverNamesById.get(session.getDriverId())
                ))
                .toList();
    }

    private List<EventCountResponse> buildEventCounts(List<EventEntity> events) {
        return buildCountsByEnum(
                events,
                EventEntity::getEventType,
                EventType.values(),
                adminResponseMapper::toEventCountResponse
        );
    }

    private List<EscalationCountResponse> buildEscalationCounts(List<EscalationEntity> escalations) {
        return buildCountsByEnum(
                escalations,
                EscalationEntity::getEscalationType,
                EscalationType.values(),
                adminResponseMapper::toEscalationCountResponse
        );
    }

    private <T, E extends Enum<E>, R> List<R> buildCountsByEnum(
            List<T> records,
            Function<T, E> typeExtractor,
            E[] values,
            java.util.function.BiFunction<E, Integer, R> mapper
    ) {
        Map<E, Long> counts = records.stream()
                .collect(Collectors.groupingBy(typeExtractor, Collectors.counting()));

        return Arrays.stream(values)
                .map(value -> mapper.apply(value, Math.toIntExact(counts.getOrDefault(value, 0L))))
                .toList();
    }

    private List<EventEntity> filterEventsForSessions(List<SessionEntity> sessions) {
        return filterChildRecordsForSessions(
                sessions,
                eventJpaRepository.findAll(),
                EventEntity::getSessionId
        );
    }

    private List<EscalationEntity> filterEscalationsForSessions(List<SessionEntity> sessions) {
        return filterChildRecordsForSessions(
                sessions,
                escalationJpaRepository.findAll(),
                EscalationEntity::getSessionId
        );
    }

    private <T> List<T> filterChildRecordsForSessions(
            List<SessionEntity> sessions,
            List<T> records,
            Function<T, String> sessionIdExtractor
    ) {
        if (sessions.isEmpty() || records.isEmpty()) {
            return List.of();
        }

        Set<String> sessionIds = sessions.stream()
                .map(SessionEntity::getId)
                .collect(Collectors.toSet());

        return records.stream()
                .filter(record -> sessionIds.contains(sessionIdExtractor.apply(record)))
                .toList();
    }

    private int countDriversByStatus(List<DriverEntity> drivers, DriverAccountStatus status) {
        return Math.toIntExact(drivers.stream().filter(driver -> driver.getAccountStatus() == status).count());
    }

    private int countVehiclesByStatus(List<VehicleEntity> vehicles, VehicleStatus status) {
        return Math.toIntExact(vehicles.stream().filter(vehicle -> vehicle.getStatus() == status).count());
    }

    private int countSessionsByValidity(List<SessionEntity> sessions, SessionValidity validity) {
        return Math.toIntExact(sessions.stream().filter(session -> session.getValidity() == validity).count());
    }

    private int countSessionsByStatus(List<SessionEntity> sessions, SessionEndStatus status) {
        return Math.toIntExact(sessions.stream().filter(session -> session.getStatus() == status).count());
    }

    private int countSessionsByProcessingStatus(List<SessionEntity> sessions, UploadProcessingStatus processingStatus) {
        return Math.toIntExact(sessions.stream()
                .filter(session -> session.getUploadProcessingStatus() == processingStatus)
                .count());
    }

    private long toDayBucket(SessionEntity session) {
        return (session.getUploadedAt() / 86_400_000L) * 86_400_000L;
    }

    private Driver toDriver(DriverEntity entity) {
        return new Driver(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getRole() == null ? UserRole.DRIVER : entity.getRole(),
                entity.getAccountStatus(),
                entity.getLongTermReliabilityScore(),
                entity.getTotalSessions(),
                entity.getTotalDistanceKm(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeactivatedAt()
        );
    }

    private void validateRange(Long fromTimestamp, Long toTimestamp) {
        if (fromTimestamp != null && toTimestamp != null && fromTimestamp > toTimestamp) {
            throw new IllegalArgumentException("fromTimestamp must be less than or equal to toTimestamp");
        }
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
