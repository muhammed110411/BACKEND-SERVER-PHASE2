package com.example.phase2.application.command;

import java.util.List;
import java.util.Objects;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.phase2.api.upload.request.UploadEscalationRequest;
import com.example.phase2.api.upload.request.UploadEventRequest;
import com.example.phase2.api.upload.request.UploadScorePointRequest;
import com.example.phase2.api.upload.request.UploadSessionRequest;
import com.example.phase2.api.upload.response.UploadSessionResponse;
import com.example.phase2.application.mapper.UploadRequestToDomainMapper;
import com.example.phase2.config.TimeProvider;
import com.example.phase2.application.validation.UploadSessionRequestValidator;
import com.example.phase2.application.validation.UploadSoftValidationAnalyzer;
import com.example.phase2.application.validation.UploadStructuralValidator;
import com.example.phase2.domain.enums.UploadProcessingStatus;
import com.example.phase2.domain.model.Driver;
import com.example.phase2.domain.model.DrivingSessionRecord;
import com.example.phase2.domain.model.EscalationRecord;
import com.example.phase2.domain.model.IngestionAnomalyFlag;
import com.example.phase2.domain.model.SafetyEventRecord;
import com.example.phase2.domain.model.ScorePointRecord;
import com.example.phase2.domain.model.Vehicle;
import com.example.phase2.exception.InactiveDriverUploadException;
import com.example.phase2.exception.ResourceNotFoundException;
import com.example.phase2.exception.UnauthorizedApiClientException;
import com.example.phase2.exception.UploadConflictException;
import com.example.phase2.persistence.entity.DriverEntity;
import com.example.phase2.persistence.entity.SessionIngestionIssueEntity;
import com.example.phase2.persistence.entity.VehicleEntity;
import com.example.phase2.persistence.mapper.DriverPersistenceMapper;
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
import com.example.phase2.util.IngestionIssueCodeFactory;

@Service
public class SessionUploadService {

    private static final String DRIVER_RESOURCE_TYPE = "driver";
    private static final String VEHICLE_RESOURCE_TYPE = "vehicle";
    private static final String ID_LOOKUP_FIELD = "id";
    private static final String UNAUTHORIZED_CLIENT_ERROR_CODE = "UNAUTHORIZED_API_CLIENT";
    private static final String DUPLICATE_SESSION_ERROR_CODE = "UPLOAD_DUPLICATE_SESSION";
    private static final String DRIVER_UPLOAD_NOT_ALLOWED_ERROR_CODE = "DRIVER_UPLOAD_NOT_ALLOWED";
    private static final String SESSION_RESOURCE_TYPE = "session";

    private final SessionJpaRepository sessionJpaRepository;
    private final EventJpaRepository eventJpaRepository;
    private final EscalationJpaRepository escalationJpaRepository;
    private final ScorePointJpaRepository scorePointJpaRepository;
    private final SessionIngestionIssueJpaRepository sessionIngestionIssueJpaRepository;
    private final DriverJpaRepository driverJpaRepository;
    private final VehicleJpaRepository vehicleJpaRepository;
    private final UploadSessionRequestValidator uploadSessionRequestValidator;
    private final UploadStructuralValidator uploadStructuralValidator;
    private final UploadSoftValidationAnalyzer uploadSoftValidationAnalyzer;
    private final UploadRequestToDomainMapper uploadRequestToDomainMapper;
    private final DriverPersistenceMapper driverPersistenceMapper;
    private final SessionPersistenceMapper sessionPersistenceMapper;
    private final EventPersistenceMapper eventPersistenceMapper;
    private final EscalationPersistenceMapper escalationPersistenceMapper;
    private final ScorePointPersistenceMapper scorePointPersistenceMapper;
    private final SessionIngestionIssuePersistenceMapper sessionIngestionIssuePersistenceMapper;
    private final IngestionIssueCodeFactory ingestionIssueCodeFactory;
    private final TimeProvider timeProvider;

    public SessionUploadService(
            SessionJpaRepository sessionJpaRepository,
            EventJpaRepository eventJpaRepository,
            EscalationJpaRepository escalationJpaRepository,
            ScorePointJpaRepository scorePointJpaRepository,
            SessionIngestionIssueJpaRepository sessionIngestionIssueJpaRepository,
            DriverJpaRepository driverJpaRepository,
            VehicleJpaRepository vehicleJpaRepository,
            UploadSessionRequestValidator uploadSessionRequestValidator,
            UploadStructuralValidator uploadStructuralValidator,
            UploadSoftValidationAnalyzer uploadSoftValidationAnalyzer,
            UploadRequestToDomainMapper uploadRequestToDomainMapper,
            DriverPersistenceMapper driverPersistenceMapper,
            SessionPersistenceMapper sessionPersistenceMapper,
            EventPersistenceMapper eventPersistenceMapper,
            EscalationPersistenceMapper escalationPersistenceMapper,
            ScorePointPersistenceMapper scorePointPersistenceMapper,
            SessionIngestionIssuePersistenceMapper sessionIngestionIssuePersistenceMapper,
            IngestionIssueCodeFactory ingestionIssueCodeFactory,
            TimeProvider timeProvider
    ) {
        this.sessionJpaRepository = sessionJpaRepository;
        this.eventJpaRepository = eventJpaRepository;
        this.escalationJpaRepository = escalationJpaRepository;
        this.scorePointJpaRepository = scorePointJpaRepository;
        this.sessionIngestionIssueJpaRepository = sessionIngestionIssueJpaRepository;
        this.driverJpaRepository = driverJpaRepository;
        this.vehicleJpaRepository = vehicleJpaRepository;
        this.uploadSessionRequestValidator = uploadSessionRequestValidator;
        this.uploadStructuralValidator = uploadStructuralValidator;
        this.uploadSoftValidationAnalyzer = uploadSoftValidationAnalyzer;
        this.uploadRequestToDomainMapper = uploadRequestToDomainMapper;
        this.driverPersistenceMapper = driverPersistenceMapper;
        this.sessionPersistenceMapper = sessionPersistenceMapper;
        this.eventPersistenceMapper = eventPersistenceMapper;
        this.escalationPersistenceMapper = escalationPersistenceMapper;
        this.scorePointPersistenceMapper = scorePointPersistenceMapper;
        this.sessionIngestionIssuePersistenceMapper = sessionIngestionIssuePersistenceMapper;
        this.ingestionIssueCodeFactory = ingestionIssueCodeFactory;
        this.timeProvider = timeProvider;
    }

    @Transactional
    public UploadSessionResponse uploadSession(UploadSessionRequest request, String sourceClientId) {
        if (sourceClientId == null || sourceClientId.isBlank()) {
            throw new UnauthorizedApiClientException(
                    "Missing authenticated source client id for session upload.",
                    sourceClientId,
                    UNAUTHORIZED_CLIENT_ERROR_CODE
            );
        }

        uploadSessionRequestValidator.validate(request);
        uploadStructuralValidator.validateNonEmptySessionPayload(request);
        ensureSessionDoesNotAlreadyExist(request.getSessionId());

        Driver driver = resolveActiveUploadDriver(request.getDriverId());
        resolveReferencedVehicle(request.getVehicleId());

        List<IngestionAnomalyFlag> anomalyFlags = analyzeSoftIssues(request);
        UploadProcessingStatus uploadProcessingStatus = resolveAcceptedProcessingStatus(anomalyFlags);
        long uploadedAt = nowEpochMillis();
        Long processedAt = uploadedAt;

        DrivingSessionRecord sessionRecord = buildSessionRecord(
                request,
                sourceClientId,
                uploadProcessingStatus,
                uploadedAt,
                processedAt
        );
        List<SafetyEventRecord> eventRecords = buildEventRecords(request.getSessionId(), request.getEvents());
        List<EscalationRecord> escalationRecords = buildEscalationRecords(request.getSessionId(), request.getEscalations());
        List<ScorePointRecord> scorePointRecords = buildScorePointRecords(request.getSessionId(), request.getScorePoints());

        persistSessionBundle(sessionRecord, eventRecords, escalationRecords, scorePointRecords, anomalyFlags);
        updateDriverAggregatesAfterAcceptedUpload(driver, sessionRecord);

        return buildAcceptedResponse(sessionRecord, anomalyFlags);
    }

    private void ensureSessionDoesNotAlreadyExist(String sessionId) {
        if (sessionJpaRepository.existsById(sessionId)) {
            throw new UploadConflictException(
                    "Session upload conflict: session already exists: " + sessionId,
                    DUPLICATE_SESSION_ERROR_CODE,
                    sessionId,
                    "session",
                    "sessionId"
            );
        }
    }

    private Driver resolveActiveUploadDriver(String driverId) {
        DriverEntity driverEntity = driverJpaRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver not found: " + driverId,
                        DRIVER_RESOURCE_TYPE,
                        driverId,
                        ID_LOOKUP_FIELD
                ));

        Driver driver = driverPersistenceMapper.toDomain(driverEntity);
        if (!driver.canAcceptSessionUpload()) {
            throw new InactiveDriverUploadException(
                    "Driver is not allowed to upload sessions: " + driverId,
                    driverId,
                    DRIVER_UPLOAD_NOT_ALLOWED_ERROR_CODE,
                    driver.getAccountStatus().name()
            );
        }

        return driver;
    }

    private Vehicle resolveReferencedVehicle(String vehicleId) {
        VehicleEntity vehicleEntity = vehicleJpaRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle not found: " + vehicleId,
                        VEHICLE_RESOURCE_TYPE,
                        vehicleId,
                        ID_LOOKUP_FIELD
                ));

        return new Vehicle(
                vehicleEntity.getId(),
                vehicleEntity.getDisplayName(),
                vehicleEntity.getStatus(),
                vehicleEntity.getCreatedAt(),
                vehicleEntity.getUpdatedAt(),
                vehicleEntity.getDeactivatedAt()
        );
    }

    private List<IngestionAnomalyFlag> analyzeSoftIssues(UploadSessionRequest request) {
        return List.copyOf(uploadSoftValidationAnalyzer.analyze(request));
    }

    private DrivingSessionRecord buildSessionRecord(
            UploadSessionRequest request,
            String sourceClientId,
            UploadProcessingStatus uploadProcessingStatus,
            long uploadedAt,
            Long processedAt
    ) {
        return uploadRequestToDomainMapper.toDrivingSessionRecord(
                request,
                uploadProcessingStatus,
                uploadedAt,
                processedAt,
                sourceClientId
        );
    }

    private List<SafetyEventRecord> buildEventRecords(String sessionId, List<UploadEventRequest> events) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        UploadSessionRequest mappingRequest = new UploadSessionRequest();
        mappingRequest.setSessionId(sessionId);
        mappingRequest.setEvents(events);
        return uploadRequestToDomainMapper.toSafetyEventRecords(mappingRequest);
    }

    private List<EscalationRecord> buildEscalationRecords(String sessionId, List<UploadEscalationRequest> escalations) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        UploadSessionRequest mappingRequest = new UploadSessionRequest();
        mappingRequest.setSessionId(sessionId);
        mappingRequest.setEscalations(escalations);
        return uploadRequestToDomainMapper.toEscalationRecords(mappingRequest);
    }

    private List<ScorePointRecord> buildScorePointRecords(String sessionId, List<UploadScorePointRequest> scorePoints) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        UploadSessionRequest mappingRequest = new UploadSessionRequest();
        mappingRequest.setSessionId(sessionId);
        mappingRequest.setScorePoints(scorePoints);
        return uploadRequestToDomainMapper.toScorePointRecords(mappingRequest);
    }

    private void persistSessionBundle(
            DrivingSessionRecord sessionRecord,
            List<SafetyEventRecord> eventRecords,
            List<EscalationRecord> escalationRecords,
            List<ScorePointRecord> scorePointRecords,
            List<IngestionAnomalyFlag> anomalyFlags
    ) {
        persistSessionRecord(sessionRecord);
        eventJpaRepository.saveAll(eventRecords.stream().map(eventPersistenceMapper::toEntity).toList());
        escalationJpaRepository.saveAll(escalationRecords.stream().map(escalationPersistenceMapper::toEntity).toList());
        scorePointJpaRepository.saveAll(scorePointRecords.stream().map(scorePointPersistenceMapper::toEntity).toList());

        List<SessionIngestionIssueEntity> issueEntities = anomalyFlags.stream()
                .map(flag -> {
                    IngestionAnomalyFlag persistedFlag = flag;
                    if (flag.getCode() == null || flag.getCode().isBlank()) {
                        persistedFlag = new IngestionAnomalyFlag(
                                ingestionIssueCodeFactory.createProcessingWarningCode("session"),
                                flag.getMessage(),
                                flag.getSeverity(),
                                flag.getFieldName(),
                                flag.getDetectedAt(),
                                flag.isBlocking()
                        );
                    }
                    return sessionIngestionIssuePersistenceMapper.toEntity(sessionRecord.getId(), persistedFlag);
                })
                .toList();

        if (!issueEntities.isEmpty()) {
            sessionIngestionIssueJpaRepository.saveAll(issueEntities);
        }
    }

    private void persistSessionRecord(DrivingSessionRecord sessionRecord) {
        try {
            sessionJpaRepository.saveAndFlush(sessionPersistenceMapper.toEntity(sessionRecord));
        } catch (DataIntegrityViolationException exception) {
            throw duplicateSessionConflict(sessionRecord.getId(), exception);
        }
    }

    private UploadConflictException duplicateSessionConflict(String sessionId, Exception cause) {
        return new UploadConflictException(
                "Session upload conflict: session already exists: " + sessionId,
                DUPLICATE_SESSION_ERROR_CODE,
                sessionId,
                SESSION_RESOURCE_TYPE,
                "sessionId"
        );
    }

    private void updateDriverAggregatesAfterAcceptedUpload(Driver driver, DrivingSessionRecord sessionRecord) {
        DriverEntity driverEntity = driverJpaRepository.findById(driver.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver not found: " + driver.getId(),
                        DRIVER_RESOURCE_TYPE,
                        driver.getId(),
                        ID_LOOKUP_FIELD
                ));
        driverEntity.setLongTermReliabilityScore(sessionRecord.getFinalScore());
        driverEntity.setTotalSessions(driver.getTotalSessions() + 1);
        driverEntity.setTotalDistanceKm(driver.getTotalDistanceKm() + sessionRecord.getTotalDistanceKm());
        driverEntity.setUpdatedAt(nowEpochMillis());

        driverJpaRepository.save(driverEntity);
    }

    private UploadSessionResponse buildAcceptedResponse(
            DrivingSessionRecord sessionRecord,
            List<IngestionAnomalyFlag> anomalyFlags
    ) {
        return new UploadSessionResponse(
                sessionRecord.getId(),
                sessionRecord.getUploadProcessingStatus(),
                sessionRecord.getUploadedAt(),
                sessionRecord.getProcessedAt(),
                List.copyOf(anomalyFlags)
        );
    }

    private UploadProcessingStatus resolveAcceptedProcessingStatus(List<IngestionAnomalyFlag> anomalyFlags) {
        return anomalyFlags.isEmpty()
                ? UploadProcessingStatus.PROCESSED
                : UploadProcessingStatus.PROCESSED_WITH_WARNINGS;
    }

    private long nowEpochMillis() {
        return timeProvider.nowEpochMillis();
    }
}
