package com.example.phase2.application.query;

import com.example.phase2.api.admin.response.DriverCardResponse;
import com.example.phase2.api.admin.response.GlobalAnalyticsResponse;
import com.example.phase2.api.admin.response.SessionSummaryResponse;
import com.example.phase2.api.admin.response.VehicleResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.application.mapper.PageViewModelMapper;
import com.example.phase2.web.viewmodel.AdminDashboardPageViewModel;
import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.domain.enums.VehicleStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminDashboardQueryServiceTest {

    private DriverQueryService driverQueryService;
    private VehicleQueryService vehicleQueryService;
    private SessionQueryService sessionQueryService;
    private AnalyticsQueryService analyticsQueryService;
    private AdminDashboardQueryService adminDashboardQueryService;

    @BeforeEach
    void setUp() {
        driverQueryService = mock(DriverQueryService.class);
        vehicleQueryService = mock(VehicleQueryService.class);
        sessionQueryService = mock(SessionQueryService.class);
        analyticsQueryService = mock(AnalyticsQueryService.class);
        adminDashboardQueryService = new AdminDashboardQueryService(
                driverQueryService,
                vehicleQueryService,
                sessionQueryService,
                analyticsQueryService,
                new PageViewModelMapper()
        );
    }

    @Test
    void getDashboardBuildsComposedDashboardViewModel() {
        when(analyticsQueryService.getGlobalAnalytics(null, null)).thenReturn(buildAnalytics());
        when(driverQueryService.getDrivers(0, 10, "longTermReliabilityScore", "asc", null, null))
                .thenReturn(new PagedResponse<>(List.of(buildDriver()), 0, 10, 1, 1, false, false));
        when(vehicleQueryService.getVehicles(0, 10, "updatedAt", "desc", null, null))
                .thenReturn(new PagedResponse<>(List.of(buildVehicle()), 0, 10, 1, 1, false, false));
        when(sessionQueryService.getSessions(0, 10, "startTimestamp", "desc", null, null, null, null, null))
                .thenReturn(new PagedResponse<>(List.of(buildSession()), 0, 10, 1, 1, false, false));

        AdminDashboardPageViewModel dashboard = adminDashboardQueryService.getDashboard();

        assertEquals(1, dashboard.getRecentDrivers().size());
        assertEquals(1, dashboard.getRecentVehicles().size());
        assertEquals(1, dashboard.getRecentSessions().size());
        assertEquals(1, dashboard.getTotalDriverCount());
        assertEquals(1, dashboard.getTotalVehicleCount());
        assertEquals(1, dashboard.getTotalSessionCount());
        assertEquals("All time", dashboard.getActiveFiltersSummary());
        assertEquals(0L, dashboard.getGeneratedAt());
    }

    @Test
    void getDashboardWithRangeDelegatesTimeWindowToAnalyticsService() {
        when(analyticsQueryService.getGlobalAnalytics(100L, 200L)).thenReturn(buildAnalytics());
        when(driverQueryService.getDrivers(0, 10, "longTermReliabilityScore", "asc", null, null))
                .thenReturn(new PagedResponse<>(List.of(), 0, 10, 0, 0, false, false));
        when(vehicleQueryService.getVehicles(0, 10, "updatedAt", "desc", null, null))
                .thenReturn(new PagedResponse<>(List.of(), 0, 10, 0, 0, false, false));
        when(sessionQueryService.getSessions(0, 10, "startTimestamp", "desc", null, null, null, null, null))
                .thenReturn(new PagedResponse<>(List.of(), 0, 10, 0, 0, false, false));

        AdminDashboardPageViewModel dashboard = adminDashboardQueryService.getDashboard(100L, 200L);

        verify(analyticsQueryService).getGlobalAnalytics(100L, 200L);
        assertEquals("Range: 100 to 200", dashboard.getActiveFiltersSummary());
        assertEquals(200L, dashboard.getGeneratedAt());
    }

    @Test
    void getDashboardRejectsInvertedTimestampRange() {
        assertThrows(IllegalArgumentException.class, () -> adminDashboardQueryService.getDashboard(200L, 100L));
    }

    private GlobalAnalyticsResponse buildAnalytics() {
        return new GlobalAnalyticsResponse(
                1, 1, 0, 0, 0,
                1, 1, 0, 0,
                1, 1, 0, 1, 0,
                1, 0, 0,
                98.5d, 120.0d, 3, 1,
                List.of(), List.of(), List.of()
        );
    }

    private DriverCardResponse buildDriver() {
        return new DriverCardResponse("driver-1", "Aylin Driver", "aylin@example.com", UserRole.DRIVER, DriverAccountStatus.ACTIVE, 98.0d, 4, 150.0d);
    }

    private VehicleResponse buildVehicle() {
        return new VehicleResponse("vehicle-1", "Van 01", VehicleStatus.AVAILABLE, 10L, 20L, null);
    }

    private SessionSummaryResponse buildSession() {
        return new SessionSummaryResponse(
                "session-1",
                "driver-1",
                "vehicle-1",
                100L,
                200L,
                SessionEndStatus.COMPLETED,
                SessionValidity.VALID,
                UploadProcessingStatus.PROCESSED,
                99.0d,
                500L,
                12.5d,
                3,
                1,
                300L,
                400L
        );
    }
}
