package com.example.phase2.application.query;

import com.example.phase2.api.admin.response.DriverCardResponse;
import com.example.phase2.api.admin.response.DriverDetailResponse;
import com.example.phase2.api.admin.response.GlobalAnalyticsResponse;
import com.example.phase2.api.admin.response.SessionDetailResponse;
import com.example.phase2.api.admin.response.SessionSummaryResponse;
import com.example.phase2.api.admin.response.VehicleResponse;
import com.example.phase2.api.admin.response.mobile.MobileAdminDashboardResponse;
import com.example.phase2.api.admin.response.mobile.MobileAdminDriverDetailResponse;
import com.example.phase2.api.admin.response.mobile.MobileAdminDriverSummaryResponse;
import com.example.phase2.api.admin.response.mobile.MobileAdminSessionDetailResponse;
import com.example.phase2.api.admin.response.mobile.MobileAdminSessionPenaltiesResponse;
import com.example.phase2.api.admin.response.mobile.MobileAdminSessionSummaryResponse;
import com.example.phase2.api.admin.response.mobile.MobileAdminVehicleSummaryResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;
import com.example.phase2.domain.enums.VehicleStatus;
import com.example.phase2.persistence.repository.VehicleJpaRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MobileAdminQueryService {

    private static final int DEFAULT_DRIVER_RECENT_SESSION_SIZE = 10;
    private static final int DEFAULT_DASHBOARD_RECENT_SESSION_SIZE = 5;

    private final DriverQueryService driverQueryService;
    private final SessionQueryService sessionQueryService;
    private final VehicleQueryService vehicleQueryService;
    private final AnalyticsQueryService analyticsQueryService;
    private final VehicleJpaRepository vehicleJpaRepository;

    public MobileAdminQueryService(
            DriverQueryService driverQueryService,
            SessionQueryService sessionQueryService,
            VehicleQueryService vehicleQueryService,
            AnalyticsQueryService analyticsQueryService,
            VehicleJpaRepository vehicleJpaRepository
    ) {
        this.driverQueryService = driverQueryService;
        this.sessionQueryService = sessionQueryService;
        this.vehicleQueryService = vehicleQueryService;
        this.analyticsQueryService = analyticsQueryService;
        this.vehicleJpaRepository = vehicleJpaRepository;
    }

    public PagedResponse<MobileAdminDriverSummaryResponse> getDrivers(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            DriverAccountStatus accountStatus,
            String searchTerm
    ) {
        PagedResponse<DriverCardResponse> source = driverQueryService.getDrivers(
                page,
                size,
                sortBy,
                sortDirection,
                accountStatus,
                searchTerm
        );
        return new PagedResponse<>(
                source.getItems().stream().map(this::toMobileDriverSummary).toList(),
                source.getPage(),
                source.getSize(),
                source.getTotalItems(),
                source.getTotalPages(),
                source.hasNext(),
                source.hasPrevious()
        );
    }

    public MobileAdminDriverDetailResponse getDriverDetail(String driverId) {
        DriverDetailResponse driver = driverQueryService.getDriverDetail(driverId);
        PagedResponse<SessionSummaryResponse> recentSessions = sessionQueryService.getSessionsByDriver(
                driverId,
                0,
                DEFAULT_DRIVER_RECENT_SESSION_SIZE,
                "startTimestamp",
                "desc",
                null,
                null,
                null
        );
        return new MobileAdminDriverDetailResponse(
                driver.getDriverId(),
                driver.getDriverName(),
                driver.getEmail(),
                driver.getRole(),
                driver.getAccountStatus(),
                driver.getTotalSessions(),
                driver.getTotalDistanceKm(),
                driver.getLongTermReliabilityScore(),
                toMobileSessionSummaries(recentSessions.getItems())
        );
    }

    public PagedResponse<MobileAdminVehicleSummaryResponse> getVehicles(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            VehicleStatus status,
            String searchTerm
    ) {
        PagedResponse<VehicleResponse> source = vehicleQueryService.getVehicles(
                page,
                size,
                sortBy,
                sortDirection,
                status,
                searchTerm
        );
        return new PagedResponse<>(
                source.getItems().stream().map(this::toMobileVehicleSummary).toList(),
                source.getPage(),
                source.getSize(),
                source.getTotalItems(),
                source.getTotalPages(),
                source.hasNext(),
                source.hasPrevious()
        );
    }

    public PagedResponse<MobileAdminSessionSummaryResponse> getSessions(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            String driverId,
            String vehicleId,
            SessionEndStatus status,
            SessionValidity validity,
            UploadProcessingStatus uploadProcessingStatus
    ) {
        PagedResponse<SessionSummaryResponse> source = sessionQueryService.getSessions(
                page,
                size,
                sortBy,
                sortDirection,
                driverId,
                vehicleId,
                validity,
                status,
                uploadProcessingStatus
        );
        return new PagedResponse<>(
                toMobileSessionSummaries(source.getItems()),
                source.getPage(),
                source.getSize(),
                source.getTotalItems(),
                source.getTotalPages(),
                source.hasNext(),
                source.hasPrevious()
        );
    }

    public MobileAdminSessionDetailResponse getSessionDetail(String sessionId) {
        SessionDetailResponse detail = sessionQueryService.getSessionDetail(sessionId);
        return new MobileAdminSessionDetailResponse(
                detail.getSessionId(),
                detail.getDriverId(),
                detail.getDriverName(),
                detail.getVehicleId(),
                findVehicleName(detail.getVehicleId()),
                detail.getFinalScore(),
                detail.getValidity(),
                detail.getStatus(),
                detail.getStartTimestamp(),
                detail.getEndTimestamp(),
                detail.getUploadedAt(),
                detail.getTotalEventCount(),
                detail.getTotalEscalationCount(),
                new MobileAdminSessionPenaltiesResponse(
                        detail.getTotalContinuousPenalty(),
                        detail.getTotalEventPenalty(),
                        detail.getTotalEscalationPenalty()
                ),
                detail.getEvents(),
                detail.getEscalations(),
                detail.getScoreHistory(),
                detail.getAnomalyFlags()
        );
    }

    public MobileAdminDashboardResponse getDashboard(Long fromTimestamp, Long toTimestamp) {
        GlobalAnalyticsResponse analytics = analyticsQueryService.getGlobalAnalytics(fromTimestamp, toTimestamp);
        PagedResponse<SessionSummaryResponse> recentSessions = sessionQueryService.getSessions(
                0,
                DEFAULT_DASHBOARD_RECENT_SESSION_SIZE,
                "uploadedAt",
                "desc",
                null,
                null,
                null,
                null,
                null
        );
        return new MobileAdminDashboardResponse(
                analytics.getTotalDrivers(),
                analytics.getActiveDriverCount(),
                analytics.getTotalVehicles(),
                analytics.getTotalSessions(),
                analytics.getAverageFinalScore(),
                toMobileSessionSummaries(recentSessions.getItems())
        );
    }

    private MobileAdminDriverSummaryResponse toMobileDriverSummary(DriverCardResponse driver) {
        return new MobileAdminDriverSummaryResponse(
                driver.getDriverId(),
                driver.getDriverName(),
                driver.getEmail(),
                driver.getRole(),
                driver.getAccountStatus(),
                driver.getTotalSessions(),
                driver.getTotalDistanceKm(),
                driver.getLongTermReliabilityScore()
        );
    }

    private MobileAdminVehicleSummaryResponse toMobileVehicleSummary(VehicleResponse vehicle) {
        return new MobileAdminVehicleSummaryResponse(
                vehicle.getVehicleId(),
                vehicle.getDisplayName(),
                vehicle.getStatus()
        );
    }

    private List<MobileAdminSessionSummaryResponse> toMobileSessionSummaries(List<SessionSummaryResponse> sessions) {
        Map<String, String> vehicleNames = findVehicleNames(sessions);
        return sessions.stream()
                .map(session -> toMobileSessionSummary(session, vehicleNames.get(session.getVehicleId())))
                .toList();
    }

    private MobileAdminSessionSummaryResponse toMobileSessionSummary(
            SessionSummaryResponse session,
            String vehicleName
    ) {
        return new MobileAdminSessionSummaryResponse(
                session.getSessionId(),
                session.getDriverId(),
                session.getDriverName(),
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

    private Map<String, String> findVehicleNames(List<SessionSummaryResponse> sessions) {
        List<String> vehicleIds = sessions.stream()
                .map(SessionSummaryResponse::getVehicleId)
                .filter(vehicleId -> vehicleId != null && !vehicleId.isBlank())
                .distinct()
                .toList();
        if (vehicleIds.isEmpty()) {
            return Map.of();
        }
        return vehicleJpaRepository.findAllById(vehicleIds).stream()
                .collect(Collectors.toMap(
                        vehicle -> vehicle.getId(),
                        vehicle -> vehicle.getDisplayName(),
                        (left, ignored) -> left,
                        LinkedHashMap::new
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
}
