package com.example.phase2.application.mapper;

import com.example.phase2.api.admin.response.DriverAnalyticsResponse;
import com.example.phase2.api.admin.response.DriverCardResponse;
import com.example.phase2.api.admin.response.DriverDetailResponse;
import com.example.phase2.api.admin.response.GlobalAnalyticsResponse;
import com.example.phase2.api.admin.response.SessionDetailResponse;
import com.example.phase2.api.admin.response.SessionSummaryResponse;
import com.example.phase2.api.admin.response.VehicleResponse;
import com.example.phase2.api.common.response.FieldValidationErrorResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.application.viewmodel.LoginPageViewModel;
import com.example.phase2.application.viewmodel.VehicleFormPageViewModel;
import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.domain.enums.VehicleStatus;
import com.example.phase2.web.viewmodel.AdminDashboardPageViewModel;
import com.example.phase2.web.viewmodel.DriverAnalyticsPageViewModel;
import com.example.phase2.web.viewmodel.DriverDetailPageViewModel;
import com.example.phase2.web.viewmodel.DriverListPageViewModel;
import com.example.phase2.web.viewmodel.SessionDetailPageViewModel;
import com.example.phase2.web.viewmodel.VehicleListPageViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PageViewModelMapperTest {

    private PageViewModelMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PageViewModelMapper();
    }

    @Test
    void toAdminDashboardPageViewModelPreservesPreparedSectionsAndCopiesPreviewLists() {
        GlobalAnalyticsResponse analytics = buildGlobalAnalytics();
        List<DriverCardResponse> recentDrivers = new ArrayList<>(List.of(buildDriverCard()));
        List<SessionSummaryResponse> recentSessions = new ArrayList<>(List.of(buildSessionSummary()));
        List<VehicleResponse> recentVehicles = new ArrayList<>(List.of(buildVehicle()));
        PagedResponse<DriverCardResponse> driverPage = new PagedResponse<>(List.of(buildDriverCard()), 0, 10, 1, 1, false, false);
        PagedResponse<SessionSummaryResponse> sessionPage = new PagedResponse<>(List.of(buildSessionSummary()), 0, 10, 1, 1, false, false);
        PagedResponse<VehicleResponse> vehiclePage = new PagedResponse<>(List.of(buildVehicle()), 0, 10, 1, 1, false, false);

        AdminDashboardPageViewModel viewModel = mapper.toAdminDashboardPageViewModel(
                analytics,
                recentDrivers,
                recentSessions,
                recentVehicles,
                driverPage,
                sessionPage,
                vehiclePage,
                10,
                5,
                20,
                1700000000L,
                "status=ACTIVE"
        );

        assertSame(analytics, viewModel.getGlobalAnalytics());
        assertEquals(recentDrivers, viewModel.getRecentDrivers());
        assertEquals(recentSessions, viewModel.getRecentSessions());
        assertEquals(recentVehicles, viewModel.getRecentVehicles());
        assertNotSame(recentDrivers, viewModel.getRecentDrivers());
        assertNotSame(recentSessions, viewModel.getRecentSessions());
        assertNotSame(recentVehicles, viewModel.getRecentVehicles());
        assertSame(driverPage, viewModel.getDriverPage());
        assertSame(sessionPage, viewModel.getSessionPage());
        assertSame(vehiclePage, viewModel.getVehiclePage());
        assertEquals(10, viewModel.getTotalDriverCount());
        assertEquals(5, viewModel.getTotalVehicleCount());
        assertEquals(20, viewModel.getTotalSessionCount());
        assertEquals(1700000000L, viewModel.getGeneratedAt());
        assertEquals("status=ACTIVE", viewModel.getActiveFiltersSummary());
    }

    @Test
    void toDriverAndVehicleListPageViewModelsPreservePreparedPagingAndUiState() {
        PagedResponse<DriverCardResponse> drivers = new PagedResponse<>(List.of(buildDriverCard()), 1, 20, 31, 2, false, true);
        PagedResponse<VehicleResponse> vehicles = new PagedResponse<>(List.of(buildVehicle()), 2, 10, 25, 3, true, true);

        DriverListPageViewModel driverList = mapper.toDriverListPageViewModel(
                drivers,
                "driverName",
                "asc",
                "ali",
                DriverAccountStatus.ACTIVE,
                31,
                "table",
                "Drivers",
                "No drivers found"
        );
        VehicleListPageViewModel vehicleList = mapper.toVehicleListPageViewModel(
                vehicles,
                "displayName",
                "desc",
                "van",
                VehicleStatus.AVAILABLE,
                25,
                "Vehicles",
                "No vehicles found"
        );

        assertSame(drivers, driverList.getDrivers());
        assertEquals("driverName", driverList.getSortBy());
        assertEquals("asc", driverList.getSortDirection());
        assertEquals("ali", driverList.getSearchQuery());
        assertEquals(DriverAccountStatus.ACTIVE, driverList.getStatusFilter());
        assertEquals(31, driverList.getTotalDriverCount());
        assertEquals("table", driverList.getViewMode());
        assertEquals("Drivers", driverList.getPageTitle());
        assertEquals("No drivers found", driverList.getEmptyStateMessage());

        assertSame(vehicles, vehicleList.getVehicles());
        assertEquals("displayName", vehicleList.getSortBy());
        assertEquals("desc", vehicleList.getSortDirection());
        assertEquals("van", vehicleList.getSearchQuery());
        assertEquals(VehicleStatus.AVAILABLE, vehicleList.getStatusFilter());
        assertEquals(25, vehicleList.getTotalVehicleCount());
        assertEquals("Vehicles", vehicleList.getPageTitle());
        assertEquals("No vehicles found", vehicleList.getEmptyStateMessage());
    }

    @Test
    void detailAndAnalyticsMappingsWrapProvidedResponsesWithoutRecomputation() {
        DriverDetailResponse driver = buildDriverDetail();
        DriverAnalyticsResponse analytics = buildDriverAnalytics();
        SessionDetailResponse sessionDetail = buildSessionDetail();

        DriverDetailPageViewModel driverDetail = mapper.toDriverDetailPageViewModel(
                driver,
                "Driver detail",
                true,
                true,
                "No driver",
                null
        );
        DriverAnalyticsPageViewModel driverAnalytics = mapper.toDriverAnalyticsPageViewModel(
                "driver-1",
                analytics,
                driver,
                1700000000L,
                1700000500L,
                "Driver analytics",
                "Last 30 days",
                true,
                null
        );
        SessionDetailPageViewModel sessionPage = mapper.toSessionDetailPageViewModel(sessionDetail);

        assertSame(driver, driverDetail.getDriver());
        assertEquals("Driver detail", driverDetail.getPageTitle());
        assertTrue(driverDetail.isCanViewAnalytics());
        assertTrue(driverDetail.isCanViewSessions());
        assertEquals("No driver", driverDetail.getEmptyStateMessage());
        assertEquals("driver-1", driverAnalytics.getDriverId());
        assertSame(analytics, driverAnalytics.getAnalytics());
        assertSame(driver, driverAnalytics.getDriverSummary());
        assertEquals(1700000000L, driverAnalytics.getFromDate());
        assertEquals(1700000500L, driverAnalytics.getToDate());
        assertEquals("Driver analytics", driverAnalytics.getPageTitle());
        assertEquals("Last 30 days", driverAnalytics.getActiveRangeLabel());
        assertTrue(driverAnalytics.isHasAnalyticsData());
        assertEquals(null, driverAnalytics.getEmptyStateMessage());
        assertSame(sessionDetail, sessionPage.getSessionDetail());
        assertEquals("Session Details", sessionPage.getPageTitle());
        assertEquals("driver-1", sessionPage.getDriverName());
        assertEquals("vehicle-1", sessionPage.getVehicleDisplayName());
        assertTrue(sessionPage.isCanViewDriver());
        assertTrue(sessionPage.isCanViewVehicle());
        assertFalse(sessionPage.isHasAnomalies());
        assertEquals(0, sessionPage.getAnomalyCount());
        assertEquals("Score History", sessionPage.getScoreChartLabel());
        assertEquals("No score history is available for this historical session.", sessionPage.getEmptyScoreHistoryMessage());
    }

    @Test
    void formAndLoginMappingsCopyUiCollectionsAndDefaultMissingCollectionsToEmpty() {
        VehicleResponse vehicle = buildVehicle();
        List<String> allowedStatuses = new ArrayList<>(List.of("AVAILABLE", "BUSY"));
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        fieldErrors.put("displayName", "Required");
        List<FieldValidationErrorResponse> loginErrors = new ArrayList<>(
                List.of(new FieldValidationErrorResponse("username", "NotBlank", "Username is required", ""))
        );

        VehicleFormPageViewModel form = mapper.toVehicleFormPageViewModel(vehicle, false, allowedStatuses, fieldErrors);
        LoginPageViewModel login = mapper.toLoginPageViewModel("admin", "Invalid credentials", false, "/admin", loginErrors);
        VehicleFormPageViewModel emptyForm = mapper.toVehicleFormPageViewModel(null, false, null, null);
        LoginPageViewModel emptyLogin = mapper.toLoginPageViewModel("admin", null, true, null, null);

        assertSame(vehicle, form.getVehicle());
        assertFalse(form.getEditMode());
        assertEquals(allowedStatuses, form.getAllowedStatuses());
        assertEquals(fieldErrors, form.getFieldErrors());
        assertNotSame(allowedStatuses, form.getAllowedStatuses());
        assertNotSame(fieldErrors, form.getFieldErrors());

        assertEquals("admin", login.getUsername());
        assertEquals("Invalid credentials", login.getErrorMessage());
        assertFalse(login.isAuthenticated());
        assertEquals("/admin", login.getRedirectUrl());
        assertEquals(loginErrors, login.getFieldErrors());
        assertNotSame(loginErrors, login.getFieldErrors());

        assertTrue(emptyForm.getAllowedStatuses().isEmpty());
        assertTrue(emptyForm.getFieldErrors().isEmpty());
        assertTrue(emptyLogin.getFieldErrors().isEmpty());
    }

    private GlobalAnalyticsResponse buildGlobalAnalytics() {
        return new GlobalAnalyticsResponse(
                10, 7, 1, 1, 1,
                5, 2, 2, 1,
                20, 18, 2, 16, 4, 19, 1, 0,
                88.5d, 1250.0d, 40, 6,
                List.of(), List.of(), List.of()
        );
    }

    private DriverCardResponse buildDriverCard() {
        return new DriverCardResponse("driver-1", "Ali Driver", "ali@example.com", UserRole.DRIVER, DriverAccountStatus.ACTIVE, 91.0d, 12, 340.5d);
    }

    private DriverDetailResponse buildDriverDetail() {
        return new DriverDetailResponse(
                "driver-1",
                "Ali Driver",
                "ali@example.com",
                DriverAccountStatus.ACTIVE,
                UserRole.DRIVER,
                91.0d,
                12,
                340.5d,
                1700000000L,
                1700000500L,
                null
        );
    }

    private DriverAnalyticsResponse buildDriverAnalytics() {
        return new DriverAnalyticsResponse(
                "driver-1",
                91.0d,
                12,
                340.5d,
                88.5d,
                10,
                2,
                9,
                3,
                20,
                4,
                List.of(),
                List.of(),
                List.of()
        );
    }

    private SessionSummaryResponse buildSessionSummary() {
        return new SessionSummaryResponse(
                "session-1",
                "driver-1",
                "vehicle-1",
                1700000000L,
                1700000500L,
                SessionEndStatus.COMPLETED,
                SessionValidity.VALID,
                UploadProcessingStatus.PROCESSED,
                91.5d,
                500L,
                12.75d,
                2,
                1,
                1700000600L,
                1700000700L
        );
    }

    private SessionDetailResponse buildSessionDetail() {
        return new SessionDetailResponse(
                "session-1",
                "driver-1",
                "vehicle-1",
                1700000000L,
                1700000500L,
                SessionEndStatus.COMPLETED,
                SessionValidity.VALID,
                UploadProcessingStatus.PROCESSED,
                91.5d,
                1.0d,
                2.0d,
                3.0d,
                500L,
                12.75d,
                2,
                1,
                1700000600L,
                1700000700L,
                "client-1",
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    private VehicleResponse buildVehicle() {
        return new VehicleResponse("vehicle-1", "Van 01", VehicleStatus.AVAILABLE, 1700000000L, 1700000500L, null);
    }
}
