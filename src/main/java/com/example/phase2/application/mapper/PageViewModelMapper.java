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
import com.example.phase2.web.viewmodel.AdminDashboardPageViewModel;
import com.example.phase2.web.viewmodel.DriverAnalyticsPageViewModel;
import com.example.phase2.web.viewmodel.DriverDetailPageViewModel;
import com.example.phase2.web.viewmodel.DriverListPageViewModel;
import com.example.phase2.web.viewmodel.FleetAnalyticsPageViewModel;
import com.example.phase2.web.viewmodel.SessionDetailPageViewModel;
import com.example.phase2.web.viewmodel.VehicleListPageViewModel;
import com.example.phase2.domain.enums.VehicleStatus;
import com.example.phase2.domain.enums.DriverAccountStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class PageViewModelMapper {

    public AdminDashboardPageViewModel toAdminDashboardPageViewModel(
            GlobalAnalyticsResponse globalAnalytics,
            List<DriverCardResponse> recentDrivers,
            List<SessionSummaryResponse> recentSessions,
            List<VehicleResponse> recentVehicles,
            PagedResponse<DriverCardResponse> driverPage,
            PagedResponse<SessionSummaryResponse> sessionPage,
            PagedResponse<VehicleResponse> vehiclePage,
            int totalDriverCount,
            int totalVehicleCount,
            int totalSessionCount,
            long generatedAt,
            String activeFiltersSummary
    ) {
        return new AdminDashboardPageViewModel(
                globalAnalytics,
                copyList(recentDrivers),
                copyList(recentSessions),
                copyList(recentVehicles),
                driverPage,
                sessionPage,
                vehiclePage,
                totalDriverCount,
                totalVehicleCount,
                totalSessionCount,
                generatedAt,
                activeFiltersSummary
        );
    }

    public DriverListPageViewModel toDriverListPageViewModel(
            PagedResponse<DriverCardResponse> drivers,
            String sortBy,
            String sortDirection,
            String searchQuery,
            DriverAccountStatus statusFilter,
            int totalDriverCount,
            String viewMode,
            String pageTitle,
            String emptyStateMessage
    ) {
        return new DriverListPageViewModel(
                drivers,
                sortBy,
                sortDirection,
                searchQuery,
                statusFilter,
                totalDriverCount,
                viewMode,
                pageTitle,
                emptyStateMessage
        );
    }

    public DriverDetailPageViewModel toDriverDetailPageViewModel(
            DriverDetailResponse driver,
            String pageTitle,
            boolean canViewAnalytics,
            boolean canViewSessions,
            String emptyStateMessage,
            String errorMessage
    ) {
        return new DriverDetailPageViewModel(
                driver,
                pageTitle,
                canViewAnalytics,
                canViewSessions,
                emptyStateMessage,
                errorMessage
        );
    }

    public DriverAnalyticsPageViewModel toDriverAnalyticsPageViewModel(
            String driverId,
            DriverAnalyticsResponse analytics,
            DriverDetailResponse driverSummary,
            Long fromDate,
            Long toDate,
            String pageTitle,
            String activeRangeLabel,
            boolean hasAnalyticsData,
            String emptyStateMessage
    ) {
        return new DriverAnalyticsPageViewModel(
                driverId,
                analytics,
                driverSummary,
                fromDate,
                toDate,
                pageTitle,
                activeRangeLabel,
                hasAnalyticsData,
                emptyStateMessage
        );
    }

    public FleetAnalyticsPageViewModel toFleetAnalyticsPageViewModel(
            GlobalAnalyticsResponse globalAnalytics,
            Long fromTimestamp,
            Long toTimestamp,
            String pageTitle,
            String activeRangeLabel,
            boolean hasAnalyticsData,
            String emptyStateMessage
    ) {
        return new FleetAnalyticsPageViewModel(
                globalAnalytics,
                fromTimestamp,
                toTimestamp,
                pageTitle,
                activeRangeLabel,
                hasAnalyticsData,
                emptyStateMessage
        );
    }

    public SessionDetailPageViewModel toSessionDetailPageViewModel(SessionDetailResponse sessionDetail) {
        SessionDetailPageViewModel viewModel = new SessionDetailPageViewModel();
        viewModel.setSession(sessionDetail);
        viewModel.setPageTitle("Session Details");
        viewModel.setDriverName(sessionDetail != null ? sessionDetail.getDriverName() : null);
        viewModel.setVehicleDisplayName(sessionDetail != null ? sessionDetail.getVehicleId() : null);
        viewModel.setCanViewDriver(sessionDetail != null && sessionDetail.getDriverId() != null);
        viewModel.setCanViewVehicle(sessionDetail != null && sessionDetail.getVehicleId() != null);
        int anomalyCount = sessionDetail != null && sessionDetail.getAnomalyFlags() != null
                ? sessionDetail.getAnomalyFlags().size()
                : 0;
        viewModel.setHasAnomalies(anomalyCount > 0);
        viewModel.setAnomalyCount(anomalyCount);
        viewModel.setScoreChartLabel("Score History");
        viewModel.setEmptyScoreHistoryMessage("No score history is available for this historical session.");
        viewModel.setEmptyStateMessage("Historical session detail is not available for this record.");
        return viewModel;
    }

    public VehicleListPageViewModel toVehicleListPageViewModel(
            PagedResponse<VehicleResponse> vehicles,
            String sortBy,
            String sortDirection,
            String searchQuery,
            VehicleStatus statusFilter,
            int totalVehicleCount,
            String pageTitle,
            String emptyStateMessage
    ) {
        return new VehicleListPageViewModel(
                vehicles,
                sortBy,
                sortDirection,
                searchQuery,
                statusFilter,
                totalVehicleCount,
                pageTitle,
                emptyStateMessage
        );
    }

    public VehicleFormPageViewModel toVehicleFormPageViewModel(
            VehicleResponse vehicle,
            Boolean editMode,
            List<String> allowedStatuses,
            Map<String, String> fieldErrors
    ) {
        return new VehicleFormPageViewModel(
                vehicle,
                editMode,
                copyList(allowedStatuses),
                copyMap(fieldErrors)
        );
    }

    public LoginPageViewModel toLoginPageViewModel(
            String username,
            String errorMessage,
            boolean authenticated,
            String redirectUrl,
            List<FieldValidationErrorResponse> fieldErrors
    ) {
        return new LoginPageViewModel(
                username,
                errorMessage,
                authenticated,
                redirectUrl,
                copyList(fieldErrors)
        );
    }

    private static <T> List<T> copyList(List<T> values) {
        if (values == null) {
            return List.of();
        }
        return new ArrayList<>(values);
    }

    private static <K, V> Map<K, V> copyMap(Map<K, V> values) {
        if (values == null) {
            return Map.of();
        }
        return new LinkedHashMap<>(values);
    }
}
