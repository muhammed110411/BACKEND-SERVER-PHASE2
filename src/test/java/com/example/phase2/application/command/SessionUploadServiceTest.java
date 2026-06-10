package com.example.phase2.application.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.phase2.api.upload.request.UploadEscalationRequest;
import com.example.phase2.api.upload.request.UploadEventRequest;
import com.example.phase2.api.upload.request.UploadScorePointRequest;
import com.example.phase2.api.upload.request.UploadSessionRequest;
import com.example.phase2.application.mapper.UploadRequestToDomainMapper;
import com.example.phase2.application.validation.UploadSessionRequestValidator;
import com.example.phase2.application.validation.UploadSoftValidationAnalyzer;
import com.example.phase2.application.validation.UploadStructuralValidator;
import com.example.phase2.config.TimeProvider;
import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.EscalationType;
import com.example.phase2.domain.enums.EventType;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.domain.model.Driver;
import com.example.phase2.domain.model.DrivingSessionRecord;
import com.example.phase2.domain.model.Vehicle;
import com.example.phase2.exception.UploadConflictException;
import com.example.phase2.persistence.entity.DriverEntity;
import com.example.phase2.persistence.entity.SessionEntity;
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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

class SessionUploadServiceTest {

    private SessionJpaRepository sessionJpaRepository;
    private EventJpaRepository eventJpaRepository;
    private EscalationJpaRepository escalationJpaRepository;
    private ScorePointJpaRepository scorePointJpaRepository;
    private SessionIngestionIssueJpaRepository sessionIngestionIssueJpaRepository;
    private DriverJpaRepository driverJpaRepository;
    private VehicleJpaRepository vehicleJpaRepository;
    private UploadSessionRequestValidator uploadSessionRequestValidator;
    private UploadStructuralValidator uploadStructuralValidator;
    private UploadSoftValidationAnalyzer uploadSoftValidationAnalyzer;
    private UploadRequestToDomainMapper uploadRequestToDomainMapper;
    private DriverPersistenceMapper driverPersistenceMapper;
    private SessionPersistenceMapper sessionPersistenceMapper;
    private EventPersistenceMapper eventPersistenceMapper;
    private EscalationPersistenceMapper escalationPersistenceMapper;
    private ScorePointPersistenceMapper scorePointPersistenceMapper;
    private SessionIngestionIssuePersistenceMapper sessionIngestionIssuePersistenceMapper;
    private IngestionIssueCodeFactory ingestionIssueCodeFactory;
    private TimeProvider timeProvider;
    private SessionUploadService sessionUploadService;

    @BeforeEach
    void setUp() {
        sessionJpaRepository = mock(SessionJpaRepository.class);
        eventJpaRepository = mock(EventJpaRepository.class);
        escalationJpaRepository = mock(EscalationJpaRepository.class);
        scorePointJpaRepository = mock(ScorePointJpaRepository.class);
        sessionIngestionIssueJpaRepository = mock(SessionIngestionIssueJpaRepository.class);
        driverJpaRepository = mock(DriverJpaRepository.class);
        vehicleJpaRepository = mock(VehicleJpaRepository.class);
        uploadSessionRequestValidator = mock(UploadSessionRequestValidator.class);
        uploadStructuralValidator = mock(UploadStructuralValidator.class);
        uploadSoftValidationAnalyzer = mock(UploadSoftValidationAnalyzer.class);
        uploadRequestToDomainMapper = mock(UploadRequestToDomainMapper.class);
        driverPersistenceMapper = mock(DriverPersistenceMapper.class);
        sessionPersistenceMapper = mock(SessionPersistenceMapper.class);
        eventPersistenceMapper = mock(EventPersistenceMapper.class);
        escalationPersistenceMapper = mock(EscalationPersistenceMapper.class);
        scorePointPersistenceMapper = mock(ScorePointPersistenceMapper.class);
        sessionIngestionIssuePersistenceMapper = mock(SessionIngestionIssuePersistenceMapper.class);
        ingestionIssueCodeFactory = mock(IngestionIssueCodeFactory.class);
        timeProvider = mock(TimeProvider.class);

        sessionUploadService = new SessionUploadService(
                sessionJpaRepository,
                eventJpaRepository,
                escalationJpaRepository,
                scorePointJpaRepository,
                sessionIngestionIssueJpaRepository,
                driverJpaRepository,
                vehicleJpaRepository,
                uploadSessionRequestValidator,
                uploadStructuralValidator,
                uploadSoftValidationAnalyzer,
                uploadRequestToDomainMapper,
                driverPersistenceMapper,
                sessionPersistenceMapper,
                eventPersistenceMapper,
                escalationPersistenceMapper,
                scorePointPersistenceMapper,
                sessionIngestionIssuePersistenceMapper,
                ingestionIssueCodeFactory,
                timeProvider
        );
    }

    @Test
    void uploadSessionRejectsPreexistingSessionId() {
        UploadSessionRequest request = buildValidRequest();
        when(sessionJpaRepository.existsById("session-1")).thenReturn(true);

        UploadConflictException exception = assertThrows(
                UploadConflictException.class,
                () -> sessionUploadService.uploadSession(request, "upload-client")
        );

        assertEquals("UPLOAD_DUPLICATE_SESSION", exception.getConflictCode());
        verify(driverJpaRepository, never()).findById(any());
    }

    @Test
    void uploadSessionMapsDatabaseDuplicateRaceToConflictAndSkipsAggregateUpdate() {
        UploadSessionRequest request = buildValidRequest();
        DriverEntity driverEntity = new DriverEntity(
                "driver-1",
                "Driver One",
                "driver@example.com",
                null,
                UserRole.DRIVER,
                DriverAccountStatus.ACTIVE,
                10.0d,
                2,
                20.0d,
                1L,
                2L,
                null
        );

        VehicleEntity vehicleEntity = new VehicleEntity(
                "vehicle-1",
                "Vehicle One",
                com.example.phase2.domain.enums.VehicleStatus.AVAILABLE,
                1L,
                2L,
                null
        );

        Driver driver = new Driver(
                "driver-1",
                "Driver One",
                "driver@example.com",
                UserRole.DRIVER,
                DriverAccountStatus.ACTIVE,
                10.0d,
                2,
                20.0d,
                1L,
                2L,
                null
        );
        DrivingSessionRecord sessionRecord = new DrivingSessionRecord(
                "session-1",
                "driver-1",
                "vehicle-1",
                1700000000000L,
                1700000500000L,
                SessionEndStatus.COMPLETED,
                SessionValidity.VALID,
                UploadProcessingStatus.PROCESSED,
                91.5d,
                3.0d,
                4.0d,
                2.0d,
                500L,
                12.75d,
                2,
                1,
                1712345678901L,
                1712345678901L,
                "upload-client"
        );

        when(sessionJpaRepository.existsById("session-1")).thenReturn(false);
        when(driverJpaRepository.findById("driver-1")).thenReturn(Optional.of(driverEntity));
        when(vehicleJpaRepository.findById("vehicle-1")).thenReturn(Optional.of(vehicleEntity));
        when(driverPersistenceMapper.toDomain(driverEntity)).thenReturn(driver);
        when(uploadSoftValidationAnalyzer.analyze(request)).thenReturn(List.of());
        when(timeProvider.nowEpochMillis()).thenReturn(1712345678901L);
        when(uploadRequestToDomainMapper.toDrivingSessionRecord(
                request,
                UploadProcessingStatus.PROCESSED,
                1712345678901L,
                1712345678901L,
                "upload-client"
        )).thenReturn(sessionRecord);
        when(uploadRequestToDomainMapper.toSafetyEventRecords(any())).thenReturn(List.of());
        when(uploadRequestToDomainMapper.toEscalationRecords(any())).thenReturn(List.of());
        when(uploadRequestToDomainMapper.toScorePointRecords(any())).thenReturn(List.of());
        when(sessionPersistenceMapper.toEntity(sessionRecord)).thenReturn(new SessionEntity(
                "session-1",
                "driver-1",
                "vehicle-1",
                1700000000000L,
                1700000500000L,
                SessionEndStatus.COMPLETED,
                SessionValidity.VALID,
                UploadProcessingStatus.PROCESSED,
                91.5d,
                3.0d,
                4.0d,
                2.0d,
                500L,
                12.75d,
                2,
                1,
                1712345678901L,
                1712345678901L,
                "upload-client"
        ));
        when(sessionJpaRepository.saveAndFlush(any(SessionEntity.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key value violates unique constraint"));

        UploadConflictException exception = assertThrows(
                UploadConflictException.class,
                () -> sessionUploadService.uploadSession(request, "upload-client")
        );

        assertEquals("UPLOAD_DUPLICATE_SESSION", exception.getConflictCode());
        verify(driverJpaRepository, never()).save(any());
    }

    private static UploadSessionRequest buildValidRequest() {
        return new UploadSessionRequest(
                "session-1",
                "driver-1",
                "vehicle-1",
                1700000000000L,
                1700000500000L,
                SessionEndStatus.COMPLETED,
                SessionValidity.VALID,
                91.5d,
                3.0d,
                4.0d,
                2.0d,
                500L,
                12.75d,
                2,
                1,
                List.of(
                        new UploadEventRequest("event-1", EventType.HARSH_BRAKING, 1700000100000L, 1.5d),
                        new UploadEventRequest("event-2", EventType.LANE_DEPARTURE, 1700000200000L, 2.5d)
                ),
                List.of(
                        new UploadEscalationRequest(
                                "escalation-1",
                                EscalationType.REPEATED_HARSH_BRAKING,
                                1700000300000L,
                                List.of(EventType.HARSH_BRAKING, EventType.LANE_DEPARTURE),
                                2.5d
                        )
                ),
                List.of(
                        new UploadScorePointRequest(1700000000000L, 100.0d),
                        new UploadScorePointRequest(1700000400000L, 91.5d)
                )
        );
    }
}
