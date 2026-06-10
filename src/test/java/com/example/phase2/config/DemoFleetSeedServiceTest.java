package com.example.phase2.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.phase2.domain.enums.VehicleStatus;
import com.example.phase2.persistence.entity.DriverEntity;
import com.example.phase2.persistence.entity.EscalationEntity;
import com.example.phase2.persistence.entity.EventEntity;
import com.example.phase2.persistence.entity.ScorePointEntity;
import com.example.phase2.persistence.entity.SessionEntity;
import com.example.phase2.persistence.entity.SessionIngestionIssueEntity;
import com.example.phase2.persistence.entity.VehicleEntity;
import com.example.phase2.persistence.repository.DriverJpaRepository;
import com.example.phase2.persistence.repository.EscalationJpaRepository;
import com.example.phase2.persistence.repository.EventJpaRepository;
import com.example.phase2.persistence.repository.ScorePointJpaRepository;
import com.example.phase2.persistence.repository.SessionIngestionIssueJpaRepository;
import com.example.phase2.persistence.repository.SessionJpaRepository;
import com.example.phase2.persistence.repository.VehicleJpaRepository;
import com.example.phase2.util.IngestionIssueCodeFactory;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DemoFleetSeedServiceTest {

    private DriverJpaRepository driverJpaRepository;
    private VehicleJpaRepository vehicleJpaRepository;
    private SessionJpaRepository sessionJpaRepository;
    private EventJpaRepository eventJpaRepository;
    private EscalationJpaRepository escalationJpaRepository;
    private ScorePointJpaRepository scorePointJpaRepository;
    private SessionIngestionIssueJpaRepository sessionIngestionIssueJpaRepository;
    private TimeProvider timeProvider;
    private DemoFleetSeedService demoFleetSeedService;

    @BeforeEach
    void setUp() {
        driverJpaRepository = org.mockito.Mockito.mock(DriverJpaRepository.class);
        vehicleJpaRepository = org.mockito.Mockito.mock(VehicleJpaRepository.class);
        sessionJpaRepository = org.mockito.Mockito.mock(SessionJpaRepository.class);
        eventJpaRepository = org.mockito.Mockito.mock(EventJpaRepository.class);
        escalationJpaRepository = org.mockito.Mockito.mock(EscalationJpaRepository.class);
        scorePointJpaRepository = org.mockito.Mockito.mock(ScorePointJpaRepository.class);
        sessionIngestionIssueJpaRepository = org.mockito.Mockito.mock(SessionIngestionIssueJpaRepository.class);
        timeProvider = org.mockito.Mockito.mock(TimeProvider.class);
        demoFleetSeedService = new DemoFleetSeedService(
                driverJpaRepository,
                vehicleJpaRepository,
                sessionJpaRepository,
                eventJpaRepository,
                escalationJpaRepository,
                scorePointJpaRepository,
                sessionIngestionIssueJpaRepository,
                new IngestionIssueCodeFactory(),
                timeProvider
        );
    }

    @Test
    void seedDemoFleetWhenEnabledCreatesDeterministicDatasetWhenOnlyBootstrapVehicleExists() {
        VehicleEntity baselineVehicle = new VehicleEntity(
                "baseline-vehicle-id",
                "Admin Car",
                VehicleStatus.AVAILABLE,
                1_700_000_000_000L,
                1_700_000_000_000L,
                null
        );
        List<VehicleEntity> savedVehicles = new ArrayList<>();
        List<DriverEntity> savedDrivers = new ArrayList<>();
        List<SessionEntity> savedSessions = new ArrayList<>();
        List<EventEntity> savedEvents = new ArrayList<>();
        List<EscalationEntity> savedEscalations = new ArrayList<>();
        List<ScorePointEntity> savedScorePoints = new ArrayList<>();
        List<SessionIngestionIssueEntity> savedIssues = new ArrayList<>();

        when(driverJpaRepository.count()).thenReturn(0L);
        when(sessionJpaRepository.count()).thenReturn(0L);
        when(eventJpaRepository.count()).thenReturn(0L);
        when(escalationJpaRepository.count()).thenReturn(0L);
        when(scorePointJpaRepository.count()).thenReturn(0L);
        when(sessionIngestionIssueJpaRepository.count()).thenReturn(0L);
        when(vehicleJpaRepository.count()).thenReturn(1L);
        when(vehicleJpaRepository.existsByDisplayName("Admin Car")).thenReturn(true);
        when(vehicleJpaRepository.findAll()).thenAnswer(invocation -> {
            List<VehicleEntity> allVehicles = new ArrayList<>();
            allVehicles.add(baselineVehicle);
            allVehicles.addAll(savedVehicles);
            return allVehicles;
        });
        when(timeProvider.nowEpochMillis()).thenReturn(1_714_521_600_000L);

        when(vehicleJpaRepository.saveAll(any())).thenAnswer(invocation -> {
            savedVehicles.addAll(toList(invocation.getArgument(0)));
            return savedVehicles;
        });
        when(driverJpaRepository.saveAll(any())).thenAnswer(invocation -> {
            savedDrivers.addAll(toList(invocation.getArgument(0)));
            return savedDrivers;
        });
        when(sessionJpaRepository.saveAll(any())).thenAnswer(invocation -> {
            savedSessions.addAll(toList(invocation.getArgument(0)));
            return savedSessions;
        });
        when(eventJpaRepository.saveAll(any())).thenAnswer(invocation -> {
            savedEvents.addAll(toList(invocation.getArgument(0)));
            return savedEvents;
        });
        when(escalationJpaRepository.saveAll(any())).thenAnswer(invocation -> {
            savedEscalations.addAll(toList(invocation.getArgument(0)));
            return savedEscalations;
        });
        when(scorePointJpaRepository.saveAll(any())).thenAnswer(invocation -> {
            savedScorePoints.addAll(toList(invocation.getArgument(0)));
            return savedScorePoints;
        });
        when(sessionIngestionIssueJpaRepository.saveAll(any())).thenAnswer(invocation -> {
            savedIssues.addAll(toList(invocation.getArgument(0)));
            return savedIssues;
        });

        DemoFleetSeedService.DemoSeedSummary summary = demoFleetSeedService.seedDemoFleetWhenEnabled(true);

        assertTrue(summary.seeded());
        assertEquals(50, summary.drivers());
        assertEquals(24, summary.vehicles());
        assertEquals(900, summary.sessions());
        assertEquals(1454, summary.events());
        assertEquals(285, summary.escalations());
        assertEquals(8358, summary.scorePoints());
        assertEquals(280, summary.ingestionIssues());
        assertEquals(savedDrivers.size(), summary.drivers());
        assertEquals(savedVehicles.size() + 1, summary.vehicles());
        assertEquals(savedSessions.size(), summary.sessions());
        assertEquals(savedEvents.size(), summary.events());
        assertEquals(savedEscalations.size(), summary.escalations());
        assertEquals(savedScorePoints.size(), summary.scorePoints());
        assertEquals(savedIssues.size(), summary.ingestionIssues());
    }

    @Test
    void seedDemoFleetWhenEnabledSkipsWhenOperationalDataAlreadyExists() {
        when(driverJpaRepository.count()).thenReturn(1L);

        DemoFleetSeedService.DemoSeedSummary summary = demoFleetSeedService.seedDemoFleetWhenEnabled(true);

        assertFalse(summary.seeded());
        assertEquals("existing operational data detected", summary.reason());
        verify(vehicleJpaRepository, never()).saveAll(any());
        verify(driverJpaRepository, never()).saveAll(any());
        verify(sessionJpaRepository, never()).saveAll(any());
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> toList(Object value) {
        List<T> result = new ArrayList<>();
        ((Iterable<T>) value).forEach(result::add);
        return result;
    }
}
