package com.example.phase2.config;

import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.EscalationType;
import com.example.phase2.domain.enums.EventType;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;
import com.example.phase2.domain.enums.UserRole;
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
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SplittableRandom;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DemoFleetSeedService {

    private static final ZoneOffset SEED_ZONE = ZoneOffset.UTC;
    private static final String BASELINE_VEHICLE_DISPLAY_NAME = "Admin Car";
    private static final int TARGET_DRIVER_COUNT = 50;
    private static final int TARGET_TOTAL_VEHICLES = 24;
    private static final int TARGET_TOTAL_SESSIONS = 900;

    private static final List<String> DRIVER_NAMES = List.of(
            "Aylin Demir",
            "Mert Kaya",
            "Selin Arslan",
            "Burak Yildiz",
            "Zeynep Sahin",
            "Kerem Acar",
            "Elif Koc",
            "Can Eren",
            "Derya Polat",
            "Onur Gunes",
            "Seda Karaca",
            "Hakan Tas",
            "Buse Aydin",
            "Emre Kurt",
            "Nazli Cakir",
            "Tolga Ergin",
            "Gamze Cetin",
            "Murat Kalkan",
            "Ece Duran",
            "Alper Aksoy",
            "Ceren Tekin",
            "Ozan Peker",
            "Ipek Yaman",
            "Deniz Ozkan",
            "Pelin Dogan",
            "Serkan Boz",
            "Asli Kiran",
            "Umut Yildirim",
            "Yasemin Avci",
            "Kaan Ates",
            "Nehir Kaplan",
            "Baris Sari",
            "Cansu Yilmaz",
            "Volkan Celik",
            "Melis Kose",
            "Tuna Inan",
            "Esra Ucar",
            "Firat Unal",
            "Gizem Ersoy",
            "Levent Karaman",
            "Pinar Erol",
            "Samet Korkmaz",
            "Ayca Tunc",
            "Eren Bulut",
            "Dilara Sen",
            "Sinem Ozturk",
            "Berk Toprak",
            "Ebru Soylu",
            "Yigit Candan",
            "Hande Vural"
    );

    private static final List<String> VEHICLE_DISPLAY_NAMES = List.of(
            "TRK-201 Ford Transit Cargo",
            "TRK-202 Mercedes Sprinter 316",
            "TRK-203 Renault Master L3H2",
            "TRK-204 Iveco Daily 35S",
            "TRK-205 Fiat Ducato Maxi",
            "TRK-206 MAN TGE Fleet",
            "TRK-207 Volkswagen Crafter",
            "TRK-208 Peugeot Boxer Long",
            "TRK-209 Citroen Jumper XL",
            "TRK-210 Isuzu NPR Urban",
            "TRK-211 Ford Transit Refrigerated",
            "TRK-212 Mercedes Vito Support",
            "TRK-213 Renault Trafic Service",
            "TRK-214 Fiat Doblo City",
            "TRK-215 Volkswagen Transporter",
            "TRK-216 Toyota Hilux Field",
            "TRK-217 Ford Ranger Response",
            "TRK-218 BMC Tugra Regional",
            "TRK-219 Isuzu D-Max Yard",
            "TRK-220 MAN TGL Short Haul",
            "TRK-221 Mercedes Atego Box",
            "TRK-222 Iveco Eurocargo Liftgate",
            "TRK-223 Otokar Atlas Depot"
    );

    private final DriverJpaRepository driverJpaRepository;
    private final VehicleJpaRepository vehicleJpaRepository;
    private final SessionJpaRepository sessionJpaRepository;
    private final EventJpaRepository eventJpaRepository;
    private final EscalationJpaRepository escalationJpaRepository;
    private final ScorePointJpaRepository scorePointJpaRepository;
    private final SessionIngestionIssueJpaRepository sessionIngestionIssueJpaRepository;
    private final IngestionIssueCodeFactory ingestionIssueCodeFactory;
    private final TimeProvider timeProvider;

    public DemoFleetSeedService(
            DriverJpaRepository driverJpaRepository,
            VehicleJpaRepository vehicleJpaRepository,
            SessionJpaRepository sessionJpaRepository,
            EventJpaRepository eventJpaRepository,
            EscalationJpaRepository escalationJpaRepository,
            ScorePointJpaRepository scorePointJpaRepository,
            SessionIngestionIssueJpaRepository sessionIngestionIssueJpaRepository,
            IngestionIssueCodeFactory ingestionIssueCodeFactory,
            TimeProvider timeProvider
    ) {
        this.driverJpaRepository = driverJpaRepository;
        this.vehicleJpaRepository = vehicleJpaRepository;
        this.sessionJpaRepository = sessionJpaRepository;
        this.eventJpaRepository = eventJpaRepository;
        this.escalationJpaRepository = escalationJpaRepository;
        this.scorePointJpaRepository = scorePointJpaRepository;
        this.sessionIngestionIssueJpaRepository = sessionIngestionIssueJpaRepository;
        this.ingestionIssueCodeFactory = ingestionIssueCodeFactory;
        this.timeProvider = timeProvider;
    }

    @Transactional
    public DemoSeedSummary seedDemoFleetWhenEnabled(boolean demoSeedEnabled) {
        if (!demoSeedEnabled) {
            return DemoSeedSummary.skipped("disabled");
        }

        if (!isOperationalDatasetEmpty()) {
            return DemoSeedSummary.skipped("existing operational data detected");
        }

        long nowEpochMillis = timeProvider.nowEpochMillis();
        LocalDate anchorDate = Instant.ofEpochMilli(nowEpochMillis)
                .atZone(SEED_ZONE)
                .toLocalDate()
                .minusDays(364L);

        List<VehicleEntity> vehicles = buildVehicles(nowEpochMillis);
        if (!vehicles.isEmpty()) {
            vehicleJpaRepository.saveAll(vehicles);
        }

        List<String> allVehicleIds = loadAllVehicleIds();
        List<DriverSeedPlan> driverPlans = buildDriverPlans(nowEpochMillis, anchorDate);
        List<DriverEntity> drivers = driverPlans.stream().map(DriverSeedPlan::driverEntity).toList();
        List<SessionBundle> sessionBundles = buildSessionBundles(driverPlans, allVehicleIds, anchorDate);

        driverJpaRepository.saveAll(drivers);
        sessionJpaRepository.saveAll(sessionBundles.stream().map(SessionBundle::sessionEntity).toList());

        List<EventEntity> events = new ArrayList<>();
        List<EscalationEntity> escalations = new ArrayList<>();
        List<ScorePointEntity> scorePoints = new ArrayList<>();
        List<SessionIngestionIssueEntity> ingestionIssues = new ArrayList<>();
        for (SessionBundle sessionBundle : sessionBundles) {
            events.addAll(sessionBundle.events());
            escalations.addAll(sessionBundle.escalations());
            scorePoints.addAll(sessionBundle.scorePoints());
            ingestionIssues.addAll(sessionBundle.ingestionIssues());
        }

        if (!events.isEmpty()) {
            eventJpaRepository.saveAll(events);
        }
        if (!escalations.isEmpty()) {
            escalationJpaRepository.saveAll(escalations);
        }
        if (!scorePoints.isEmpty()) {
            scorePointJpaRepository.saveAll(scorePoints);
        }
        if (!ingestionIssues.isEmpty()) {
            sessionIngestionIssueJpaRepository.saveAll(ingestionIssues);
        }

        return new DemoSeedSummary(
                true,
                "seeded",
                drivers.size(),
                allVehicleIds.size(),
                sessionBundles.size(),
                events.size(),
                escalations.size(),
                scorePoints.size(),
                ingestionIssues.size()
        );
    }

    private boolean isOperationalDatasetEmpty() {
        long driverCount = driverJpaRepository.count();
        long sessionCount = sessionJpaRepository.count();
        long eventCount = eventJpaRepository.count();
        long escalationCount = escalationJpaRepository.count();
        long scorePointCount = scorePointJpaRepository.count();
        long issueCount = sessionIngestionIssueJpaRepository.count();
        long vehicleCount = vehicleJpaRepository.count();

        if (driverCount > 0L || sessionCount > 0L || eventCount > 0L
                || escalationCount > 0L || scorePointCount > 0L || issueCount > 0L) {
            return false;
        }

        if (vehicleCount == 0L) {
            return true;
        }

        return vehicleCount == 1L && vehicleJpaRepository.existsByDisplayName(BASELINE_VEHICLE_DISPLAY_NAME);
    }

    private List<VehicleEntity> buildVehicles(long nowEpochMillis) {
        List<VehicleEntity> vehicles = new ArrayList<>();
        long existingVehicleCount = vehicleJpaRepository.count();
        int vehiclesToCreate = (int) Math.max(0L, TARGET_TOTAL_VEHICLES - existingVehicleCount);

        for (int index = 0; index < vehiclesToCreate; index++) {
            String displayName = VEHICLE_DISPLAY_NAMES.get(index);
            VehicleStatus status = resolveVehicleStatus(index);
            long createdAt = nowEpochMillis - daysToMillis(420L - index * 5L);
            Long deactivatedAt = status == VehicleStatus.OFFLINE
                    ? createdAt + daysToMillis(330L - index * 3L)
                    : null;

            vehicles.add(new VehicleEntity(
                    deterministicId("vehicle", index + 1),
                    displayName,
                    status,
                    createdAt,
                    nowEpochMillis - daysToMillis((index % 6) * 3L),
                    deactivatedAt
            ));
        }

        return vehicles;
    }

    private List<String> loadAllVehicleIds() {
        return vehicleJpaRepository.findAll().stream()
                .sorted(Comparator.comparing(VehicleEntity::getDisplayName))
                .map(VehicleEntity::getId)
                .toList();
    }

    private List<DriverSeedPlan> buildDriverPlans(long nowEpochMillis, LocalDate anchorDate) {
        List<DriverSeedPlan> driverPlans = new ArrayList<>();

        for (int index = 0; index < TARGET_DRIVER_COUNT; index++) {
            DriverPerformanceTier tier = resolveTier(index);
            int targetSessions = resolveSessionCount(index);
            long createdAt = anchorDate.minusDays(45L + index * 6L)
                    .atTime(8 + (index % 4), 15 + (index % 3) * 10)
                    .toInstant(SEED_ZONE)
                    .toEpochMilli();

            driverPlans.add(new DriverSeedPlan(
                    index,
                    tier,
                    targetSessions,
                    new DriverEntity(
                            deterministicId("driver", index + 1),
                            DRIVER_NAMES.get(index),
                            toDriverEmail(DRIVER_NAMES.get(index), index),
                            null,
                            UserRole.DRIVER,
                            resolveDriverAccountStatus(index),
                            0.0d,
                            0,
                            0.0d,
                            createdAt,
                            nowEpochMillis,
                            null
                    )
            ));
        }

        return driverPlans;
    }

    private List<SessionBundle> buildSessionBundles(
            List<DriverSeedPlan> driverPlans,
            List<String> vehicleIds,
            LocalDate anchorDate
    ) {
        List<SessionBundle> bundles = new ArrayList<>();
        int globalSessionOrdinal = 0;

        for (DriverSeedPlan driverPlan : driverPlans) {
            DriverEntity driver = driverPlan.driverEntity();
            double sessionScoreSum = 0.0d;
            double totalDistanceKm = 0.0d;
            long latestActivity = driver.getCreatedAt();
            Long deactivatedAt = null;

            for (int sessionIndex = 0; sessionIndex < driverPlan.targetSessions(); sessionIndex++) {
                SessionBundle bundle = buildSessionBundle(
                        driverPlan,
                        sessionIndex,
                        globalSessionOrdinal,
                        driver.getId(),
                        vehicleIds,
                        anchorDate
                );
                bundles.add(bundle);

                sessionScoreSum += bundle.sessionEntity().getFinalScore();
                totalDistanceKm += bundle.sessionEntity().getTotalDistanceKm();
                latestActivity = Math.max(latestActivity, bundle.sessionEntity().getEndTimestamp());
                globalSessionOrdinal++;
            }

            driver.setTotalSessions(driverPlan.targetSessions());
            driver.setTotalDistanceKm(roundTwoDecimals(totalDistanceKm));
            driver.setLongTermReliabilityScore(roundOneDecimal(sessionScoreSum / driverPlan.targetSessions()));
            driver.setUpdatedAt(latestActivity);

            if (driver.getAccountStatus() == DriverAccountStatus.DEACTIVATED) {
                deactivatedAt = latestActivity + daysToMillis(10L + driverPlan.driverIndex());
            }
            driver.setDeactivatedAt(deactivatedAt);
        }

        if (globalSessionOrdinal != TARGET_TOTAL_SESSIONS) {
            throw new IllegalStateException("Expected " + TARGET_TOTAL_SESSIONS + " sessions but built " + globalSessionOrdinal);
        }

        return bundles;
    }

    private SessionBundle buildSessionBundle(
            DriverSeedPlan driverPlan,
            int sessionIndex,
            int globalSessionOrdinal,
            String driverId,
            List<String> vehicleIds,
            LocalDate anchorDate
    ) {
        SplittableRandom random = new SplittableRandom(stableSeed("session-" + driverPlan.driverIndex() + "-" + sessionIndex));
        int targetSessions = driverPlan.targetSessions();
        int baseDay = (sessionIndex * 360) / targetSessions;
        int dayOffset = Math.min(364, baseDay + (driverPlan.driverIndex() * 7 + sessionIndex * 11) % 18);
        LocalDate sessionDate = anchorDate.plusDays(dayOffset);

        int startHour = resolveStartHour(sessionIndex, random);
        int startMinute = (driverPlan.driverIndex() * 7 + sessionIndex * 13) % 60;
        long startTimestamp = sessionDate
                .atTime(startHour, startMinute)
                .toInstant(SEED_ZONE)
                .toEpochMilli();

        SessionEndStatus endStatus = resolveSessionEndStatus(driverPlan, sessionIndex, globalSessionOrdinal);
        SessionValidity validity = resolveSessionValidity(driverPlan, sessionIndex, globalSessionOrdinal, endStatus);
        UploadProcessingStatus processingStatus = resolveProcessingStatus(
                driverPlan,
                sessionIndex,
                globalSessionOrdinal,
                validity
        );

        long durationSeconds = resolveDurationSeconds(driverPlan, sessionIndex, endStatus, random);
        double averageSpeedKmh = resolveAverageSpeed(driverPlan, sessionIndex, random);
        double distanceKm = roundTwoDecimals((durationSeconds / 3600.0d) * averageSpeedKmh);

        double continuousPenalty = roundTwoDecimals(resolveContinuousPenalty(driverPlan, durationSeconds, random));
        int eventCount = resolveEventCount(driverPlan, sessionIndex, endStatus, validity, processingStatus);
        List<EventEntity> events = buildEvents(
                driverPlan,
                sessionIndex,
                globalSessionOrdinal,
                startTimestamp,
                durationSeconds,
                eventCount,
                random
        );
        int escalationCount = resolveEscalationCount(driverPlan, sessionIndex, events.size(), processingStatus);
        List<EscalationEntity> escalations = buildEscalations(
                driverPlan,
                sessionIndex,
                globalSessionOrdinal,
                startTimestamp,
                durationSeconds,
                events,
                escalationCount
        );

        double eventPenalty = roundTwoDecimals(events.stream().mapToDouble(event -> event.getSeverity() * 3.15d).sum());
        double escalationPenalty = roundTwoDecimals(escalations.stream()
                .mapToDouble(escalation -> escalation.getSeverityMultiplier() * 4.75d)
                .sum());
        double finalScore = roundOneDecimal(resolveFinalScore(
                driverPlan,
                continuousPenalty,
                eventPenalty,
                escalationPenalty,
                endStatus,
                validity,
                processingStatus,
                random
        ));

        long endTimestamp = startTimestamp + durationSeconds * 1000L;
        long uploadedAt = endTimestamp + minutesToMillis(20L + (globalSessionOrdinal % 180));
        Long processedAt = uploadedAt + minutesToMillis(
                processingStatus == UploadProcessingStatus.REJECTED ? 9L : 22L + (sessionIndex % 35)
        );

        String sessionId = deterministicId("session", globalSessionOrdinal + 1);
        List<ScorePointEntity> scorePoints = buildScorePoints(
                sessionId,
                startTimestamp,
                endTimestamp,
                finalScore,
                driverPlan,
                sessionIndex
        );
        List<SessionIngestionIssueEntity> ingestionIssues = buildIngestionIssues(
                sessionId,
                uploadedAt,
                validity,
                processingStatus,
                durationSeconds,
                distanceKm,
                globalSessionOrdinal
        );

        SessionEntity sessionEntity = new SessionEntity(
                sessionId,
                driverId,
                vehicleIds.get((driverPlan.driverIndex() * 5 + sessionIndex * 3) % vehicleIds.size()),
                startTimestamp,
                endTimestamp,
                endStatus,
                validity,
                processingStatus,
                finalScore,
                continuousPenalty,
                eventPenalty,
                escalationPenalty,
                durationSeconds,
                distanceKm,
                events.size(),
                escalations.size(),
                uploadedAt,
                processedAt,
                resolveSourceClientId(globalSessionOrdinal)
        );

        return new SessionBundle(sessionEntity, events, escalations, scorePoints, ingestionIssues);
    }

    private List<EventEntity> buildEvents(
            DriverSeedPlan driverPlan,
            int sessionIndex,
            int globalSessionOrdinal,
            long startTimestamp,
            long durationSeconds,
            int eventCount,
            SplittableRandom random
    ) {
        List<EventEntity> events = new ArrayList<>();
        for (int eventIndex = 0; eventIndex < eventCount; eventIndex++) {
            long eventOffsetSeconds = Math.max(60L, ((eventIndex + 1L) * durationSeconds) / (eventCount + 1L));
            EventType eventType = EventType.values()[
                    Math.floorMod(driverPlan.driverIndex() + sessionIndex + eventIndex, EventType.values().length)
            ];
            double severity = roundTwoDecimals(
                    0.45d
                            + driverPlan.tier().eventBias()
                            + eventIndex * 0.18d
                            + random.nextDouble(0.12d, 0.82d)
            );

            events.add(new EventEntity(
                    deterministicId("event", globalSessionOrdinal + 1, eventIndex + 1),
                    deterministicId("session", globalSessionOrdinal + 1),
                    eventType,
                    startTimestamp + eventOffsetSeconds * 1000L,
                    severity
            ));
        }
        return events;
    }

    private List<EscalationEntity> buildEscalations(
            DriverSeedPlan driverPlan,
            int sessionIndex,
            int globalSessionOrdinal,
            long startTimestamp,
            long durationSeconds,
            List<EventEntity> events,
            int escalationCount
    ) {
        if (escalationCount == 0 || events.isEmpty()) {
            return List.of();
        }

        List<EscalationEntity> escalations = new ArrayList<>();
        for (int escalationIndex = 0; escalationIndex < escalationCount; escalationIndex++) {
            EscalationType escalationType = EscalationType.values()[
                    Math.floorMod(driverPlan.driverIndex() + sessionIndex + escalationIndex, EscalationType.values().length)
            ];
            long offsetSeconds = Math.max(120L, ((escalationIndex + 2L) * durationSeconds) / (escalationCount + 3L));
            String relatedEventTypes = events.stream()
                    .skip(Math.max(0, events.size() - 2L - escalationIndex))
                    .limit(2)
                    .map(event -> event.getEventType().name())
                    .distinct()
                    .reduce((left, right) -> left + "," + right)
                    .orElse(events.get(events.size() - 1).getEventType().name());

            escalations.add(new EscalationEntity(
                    deterministicId("escalation", globalSessionOrdinal + 1, escalationIndex + 1),
                    deterministicId("session", globalSessionOrdinal + 1),
                    escalationType,
                    startTimestamp + offsetSeconds * 1000L,
                    relatedEventTypes,
                    roundTwoDecimals(1.18d + driverPlan.tier().eventBias() + escalationIndex * 0.25d)
            ));
        }

        return escalations;
    }

    private List<ScorePointEntity> buildScorePoints(
            String sessionId,
            long startTimestamp,
            long endTimestamp,
            double finalScore,
            DriverSeedPlan driverPlan,
            int sessionIndex
    ) {
        int pointCount = 6 + (driverPlan.driverIndex() % 4) + (sessionIndex % 5);
        List<ScorePointEntity> points = new ArrayList<>();
        double openingScore = Math.min(99.0d, finalScore + 4.5d + driverPlan.tier().scoreBuffer());

        for (int pointIndex = 0; pointIndex < pointCount; pointIndex++) {
            double progress = pointCount == 1 ? 1.0d : pointIndex / (double) (pointCount - 1);
            long timestamp = startTimestamp + Math.round((endTimestamp - startTimestamp) * progress);
            double drift = (driverPlan.driverIndex() % 3) * 0.18d - (sessionIndex % 2) * 0.11d;
            double scoreValue = finalScore + (openingScore - finalScore) * (1.0d - progress) + drift;
            if (pointIndex == pointCount - 1) {
                scoreValue = finalScore;
            }
            points.add(new ScorePointEntity(null, sessionId, timestamp, roundOneDecimal(scoreValue)));
        }

        return points;
    }

    private List<SessionIngestionIssueEntity> buildIngestionIssues(
            String sessionId,
            long uploadedAt,
            SessionValidity validity,
            UploadProcessingStatus processingStatus,
            long durationSeconds,
            double distanceKm,
            int globalSessionOrdinal
    ) {
        if (processingStatus == UploadProcessingStatus.PROCESSED && globalSessionOrdinal % 17 != 0) {
            return List.of();
        }

        List<SessionIngestionIssueEntity> issues = new ArrayList<>();
        if (processingStatus == UploadProcessingStatus.PROCESSED_WITH_WARNINGS || globalSessionOrdinal % 17 == 0) {
            issues.add(new SessionIngestionIssueEntity(
                    null,
                    sessionId,
                    ingestionIssueCodeFactory.createProcessingWarningCode("late_upload_window"),
                    "Upload arrived outside the preferred processing window but was accepted.",
                    "WARNING",
                    "uploadedAt",
                    uploadedAt + minutesToMillis(2L),
                    false
            ));
        }

        if (processingStatus == UploadProcessingStatus.REJECTED) {
            issues.add(new SessionIngestionIssueEntity(
                    null,
                    sessionId,
                    ingestionIssueCodeFactory.createProcessingBlockCode("telemetry_gap"),
                    "Telemetry continuity gap exceeded the safe processing tolerance for demo review.",
                    "ERROR",
                    "scoreHistory",
                    uploadedAt + minutesToMillis(5L),
                    true
            ));

            String fieldName = validity == SessionValidity.INVALID ? "endTimestamp" : "totalDistanceKm";
            String code = validity == SessionValidity.INVALID
                    ? ingestionIssueCodeFactory.createChronologyIssueCode(fieldName)
                    : ingestionIssueCodeFactory.createInvalidFieldCode(fieldName);
            String message = validity == SessionValidity.INVALID
                    ? "Session chronology was inconsistent after ingestion validation."
                    : "Distance telemetry could not be reconciled with the uploaded duration.";

            issues.add(new SessionIngestionIssueEntity(
                    null,
                    sessionId,
                    code,
                    message,
                    "ERROR",
                    fieldName,
                    uploadedAt + minutesToMillis(7L),
                    true
            ));
        } else if (validity == SessionValidity.INVALID && durationSeconds > 6_000L && distanceKm < 40.0d) {
            issues.add(new SessionIngestionIssueEntity(
                    null,
                    sessionId,
                    ingestionIssueCodeFactory.createInconsistentFieldCode("totalDistanceKm"),
                    "Low distance vs. duration mismatch was retained for supervisor review.",
                    "WARNING",
                    "totalDistanceKm",
                    uploadedAt + minutesToMillis(6L),
                    false
            ));
        }

        return issues;
    }

    private DriverPerformanceTier resolveTier(int driverIndex) {
        if (driverIndex < 6) {
            return DriverPerformanceTier.ELITE;
        }
        if (driverIndex < 16) {
            return DriverPerformanceTier.STEADY;
        }
        if (driverIndex < 30) {
            return DriverPerformanceTier.BALANCED;
        }
        if (driverIndex < 42) {
            return DriverPerformanceTier.AT_RISK;
        }
        return DriverPerformanceTier.PROBATIONARY;
    }

    private int resolveSessionCount(int driverIndex) {
        if (driverIndex < 6) {
            return 33;
        }
        if (driverIndex < 16) {
            return 24;
        }
        if (driverIndex < 30) {
            return 18;
        }
        if (driverIndex < 40) {
            return 13;
        }
        if (driverIndex < 42) {
            return 12;
        }
        return 7;
    }

    private DriverAccountStatus resolveDriverAccountStatus(int driverIndex) {
        if (driverIndex < 36) {
            return DriverAccountStatus.ACTIVE;
        }
        if (driverIndex < 41) {
            return DriverAccountStatus.INACTIVE;
        }
        if (driverIndex < 46) {
            return DriverAccountStatus.SUSPENDED;
        }
        return DriverAccountStatus.DEACTIVATED;
    }

    private VehicleStatus resolveVehicleStatus(int index) {
        if (index < 14) {
            return VehicleStatus.AVAILABLE;
        }
        if (index < 18) {
            return VehicleStatus.BUSY;
        }
        if (index < 21) {
            return VehicleStatus.OFFLINE;
        }
        return VehicleStatus.UNKNOWN;
    }

    private int resolveStartHour(int sessionIndex, SplittableRandom random) {
        int bucket = sessionIndex % 5;
        return switch (bucket) {
            case 0 -> 5 + random.nextInt(2);
            case 1 -> 7 + random.nextInt(3);
            case 2 -> 10 + random.nextInt(2);
            case 3 -> 13 + random.nextInt(3);
            default -> 17 + random.nextInt(3);
        };
    }

    private SessionEndStatus resolveSessionEndStatus(
            DriverSeedPlan driverPlan,
            int sessionIndex,
            int globalSessionOrdinal
    ) {
        int marker = Math.floorMod(
                driverPlan.driverIndex() * 5 + sessionIndex * 3 + globalSessionOrdinal,
                17
        );
        if (driverPlan.tier().ordinal() >= DriverPerformanceTier.AT_RISK.ordinal() && marker >= 12) {
            return SessionEndStatus.ABORTED;
        }
        return marker == 0 ? SessionEndStatus.ABORTED : SessionEndStatus.COMPLETED;
    }

    private SessionValidity resolveSessionValidity(
            DriverSeedPlan driverPlan,
            int sessionIndex,
            int globalSessionOrdinal,
            SessionEndStatus endStatus
    ) {
        int marker = Math.floorMod(
                driverPlan.driverIndex() * 11 + sessionIndex * 7 + globalSessionOrdinal,
                23
        );
        if (endStatus == SessionEndStatus.ABORTED && marker >= 16) {
            return SessionValidity.INVALID;
        }
        if (driverPlan.tier() == DriverPerformanceTier.PROBATIONARY && marker >= 15) {
            return SessionValidity.INVALID;
        }
        return marker == 0 || marker == 19 ? SessionValidity.INVALID : SessionValidity.VALID;
    }

    private UploadProcessingStatus resolveProcessingStatus(
            DriverSeedPlan driverPlan,
            int sessionIndex,
            int globalSessionOrdinal,
            SessionValidity validity
    ) {
        int marker = Math.floorMod(
                driverPlan.driverIndex() * 13 + sessionIndex * 5 + globalSessionOrdinal,
                31
        );
        if (validity == SessionValidity.INVALID && (marker >= 24 || driverPlan.tier() == DriverPerformanceTier.PROBATIONARY)) {
            return marker % 2 == 0 ? UploadProcessingStatus.REJECTED : UploadProcessingStatus.PROCESSED_WITH_WARNINGS;
        }
        if (marker == 0 || marker == 7 || marker == 18 || marker == 25) {
            return UploadProcessingStatus.PROCESSED_WITH_WARNINGS;
        }
        if (marker == 29 && driverPlan.tier().ordinal() >= DriverPerformanceTier.AT_RISK.ordinal()) {
            return UploadProcessingStatus.REJECTED;
        }
        return UploadProcessingStatus.PROCESSED;
    }

    private long resolveDurationSeconds(
            DriverSeedPlan driverPlan,
            int sessionIndex,
            SessionEndStatus endStatus,
            SplittableRandom random
    ) {
        long baseMinutes = switch (driverPlan.tier()) {
            case ELITE -> 85L + sessionIndex % 70;
            case STEADY -> 70L + sessionIndex % 65;
            case BALANCED -> 60L + sessionIndex % 60;
            case AT_RISK -> 50L + sessionIndex % 55;
            case PROBATIONARY -> 45L + sessionIndex % 45;
        };
        long jitterMinutes = random.nextLong(12L, 48L);
        long totalMinutes = baseMinutes + jitterMinutes;
        if (endStatus == SessionEndStatus.ABORTED) {
            totalMinutes = Math.max(24L, totalMinutes - random.nextLong(18L, 42L));
        }
        return totalMinutes * 60L;
    }

    private double resolveAverageSpeed(
            DriverSeedPlan driverPlan,
            int sessionIndex,
            SplittableRandom random
    ) {
        double baseSpeed = switch (driverPlan.tier()) {
            case ELITE -> 61.0d;
            case STEADY -> 58.0d;
            case BALANCED -> 54.0d;
            case AT_RISK -> 50.0d;
            case PROBATIONARY -> 46.0d;
        };
        return baseSpeed + (sessionIndex % 4) * 3.2d + random.nextDouble(1.5d, 8.5d);
    }

    private double resolveContinuousPenalty(
            DriverSeedPlan driverPlan,
            long durationSeconds,
            SplittableRandom random
    ) {
        double durationHours = durationSeconds / 3600.0d;
        return durationHours * (0.9d + driverPlan.tier().eventBias() * 0.95d) + random.nextDouble(0.1d, 1.1d);
    }

    private int resolveEventCount(
            DriverSeedPlan driverPlan,
            int sessionIndex,
            SessionEndStatus endStatus,
            SessionValidity validity,
            UploadProcessingStatus processingStatus
    ) {
        int base = switch (driverPlan.tier()) {
            case ELITE -> sessionIndex % 5 == 0 ? 1 : 0;
            case STEADY -> sessionIndex % 3 == 0 ? 1 : 0;
            case BALANCED -> 1 + sessionIndex % 3;
            case AT_RISK -> 1 + sessionIndex % 4;
            case PROBATIONARY -> 2 + sessionIndex % 4;
        };

        if (endStatus == SessionEndStatus.ABORTED) {
            base++;
        }
        if (validity == SessionValidity.INVALID) {
            base++;
        }
        if (processingStatus == UploadProcessingStatus.REJECTED) {
            base++;
        }

        return Math.min(base, 7);
    }

    private int resolveEscalationCount(
            DriverSeedPlan driverPlan,
            int sessionIndex,
            int eventCount,
            UploadProcessingStatus processingStatus
    ) {
        if (eventCount < 2) {
            return 0;
        }

        int base = switch (driverPlan.tier()) {
            case ELITE -> 0;
            case STEADY -> sessionIndex % 6 == 0 ? 1 : 0;
            case BALANCED -> sessionIndex % 4 == 0 ? 1 : 0;
            case AT_RISK -> 1 + (sessionIndex % 5 == 0 ? 1 : 0);
            case PROBATIONARY -> 1 + (sessionIndex % 3 == 0 ? 1 : 0);
        };

        if (processingStatus == UploadProcessingStatus.REJECTED && base < 2) {
            base++;
        }

        return Math.min(base, Math.max(0, eventCount / 2));
    }

    private double resolveFinalScore(
            DriverSeedPlan driverPlan,
            double continuousPenalty,
            double eventPenalty,
            double escalationPenalty,
            SessionEndStatus endStatus,
            SessionValidity validity,
            UploadProcessingStatus processingStatus,
            SplittableRandom random
    ) {
        double score = driverPlan.tier().baseScore()
                - continuousPenalty
                - eventPenalty
                - escalationPenalty
                + random.nextDouble(0.25d, 1.85d);

        if (endStatus == SessionEndStatus.ABORTED) {
            score -= 4.0d;
        }
        if (validity == SessionValidity.INVALID) {
            score -= 5.5d;
        }
        if (processingStatus == UploadProcessingStatus.REJECTED) {
            score -= 4.5d;
        }

        return clamp(score, 24.0d, 99.0d);
    }

    private String resolveSourceClientId(int globalSessionOrdinal) {
        return switch (globalSessionOrdinal % 5) {
            case 0 -> "demo-telematics-gateway-01";
            case 1 -> "demo-mobile-uploader-02";
            case 2 -> "demo-depot-sync-03";
            case 3 -> "demo-regional-hub-04";
            default -> "demo-yard-terminal-05";
        };
    }

    private String toDriverEmail(String driverName, int index) {
        return driverName.toLowerCase()
                .replace(' ', '.')
                .concat(String.format("%02d@demofleet.local", index + 1));
    }

    private String deterministicId(String type, int ordinal) {
        return deterministicId(type + "-" + ordinal);
    }

    private String deterministicId(String type, int firstOrdinal, int secondOrdinal) {
        return deterministicId(type + "-" + firstOrdinal + "-" + secondOrdinal);
    }

    private String deterministicId(String rawKey) {
        return UUID.nameUUIDFromBytes(rawKey.getBytes(StandardCharsets.UTF_8)).toString();
    }

    private long stableSeed(String key) {
        return UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8)).getMostSignificantBits();
    }

    private static long daysToMillis(long days) {
        return days * 86_400_000L;
    }

    private static long minutesToMillis(long minutes) {
        return minutes * 60_000L;
    }

    private static double roundOneDecimal(double value) {
        return Math.round(value * 10.0d) / 10.0d;
    }

    private static double roundTwoDecimals(double value) {
        return Math.round(value * 100.0d) / 100.0d;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public record DemoSeedSummary(
            boolean seeded,
            String reason,
            int drivers,
            int vehicles,
            int sessions,
            int events,
            int escalations,
            int scorePoints,
            int ingestionIssues
    ) {
        public static DemoSeedSummary skipped(String reason) {
            return new DemoSeedSummary(false, reason, 0, 0, 0, 0, 0, 0, 0);
        }
    }

    private enum DriverPerformanceTier {
        ELITE(96.0d, 0.10d, 2.0d),
        STEADY(90.0d, 0.24d, 2.8d),
        BALANCED(84.5d, 0.42d, 3.4d),
        AT_RISK(76.0d, 0.72d, 4.2d),
        PROBATIONARY(68.5d, 0.98d, 4.8d);

        private final double baseScore;
        private final double eventBias;
        private final double scoreBuffer;

        DriverPerformanceTier(double baseScore, double eventBias, double scoreBuffer) {
            this.baseScore = baseScore;
            this.eventBias = eventBias;
            this.scoreBuffer = scoreBuffer;
        }

        public double baseScore() {
            return baseScore;
        }

        public double eventBias() {
            return eventBias;
        }

        public double scoreBuffer() {
            return scoreBuffer;
        }
    }

    private record DriverSeedPlan(
            int driverIndex,
            DriverPerformanceTier tier,
            int targetSessions,
            DriverEntity driverEntity
    ) {
    }

    private record SessionBundle(
            SessionEntity sessionEntity,
            List<EventEntity> events,
            List<EscalationEntity> escalations,
            List<ScorePointEntity> scorePoints,
            List<SessionIngestionIssueEntity> ingestionIssues
    ) {
    }
}
