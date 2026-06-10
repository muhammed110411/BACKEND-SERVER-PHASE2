package com.example.phase2.application.query;

import com.example.phase2.api.admin.response.DriverCardResponse;
import com.example.phase2.api.admin.response.GlobalAnalyticsResponse;
import com.example.phase2.api.admin.response.SessionSummaryResponse;
import com.example.phase2.api.admin.response.VehicleResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.application.mapper.PageViewModelMapper;
import com.example.phase2.web.viewmodel.AdminDashboardPageViewModel;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminDashboardQueryService {

    private static final int DASHBOARD_PREVIEW_SIZE = 5;
    private static final int DASHBOARD_PAGE_SIZE = 10;
    private static final String DEFAULT_SORT_DIRECTION = "desc";
    private static final String RISK_SORT_DIRECTION = "asc";

    private final DriverQueryService driverQueryService;
    private final VehicleQueryService vehicleQueryService;
    private final SessionQueryService sessionQueryService;
    private final AnalyticsQueryService analyticsQueryService;
    private final PageViewModelMapper pageViewModelMapper;

    public AdminDashboardQueryService(
            DriverQueryService driverQueryService,
            VehicleQueryService vehicleQueryService,
            SessionQueryService sessionQueryService,
            AnalyticsQueryService analyticsQueryService,
            PageViewModelMapper pageViewModelMapper
    ) {
        this.driverQueryService = driverQueryService;
        this.vehicleQueryService = vehicleQueryService;
        this.sessionQueryService = sessionQueryService;
        this.analyticsQueryService = analyticsQueryService;
        this.pageViewModelMapper = pageViewModelMapper;
    }

    public AdminDashboardPageViewModel getDashboard() {
        return buildDashboard(null, null);
    }

    public AdminDashboardPageViewModel getDashboard(Long fromTimestamp, Long toTimestamp) {
        validateRange(fromTimestamp, toTimestamp);
        return buildDashboard(fromTimestamp, toTimestamp);
    }

    private AdminDashboardPageViewModel buildDashboard(Long fromTimestamp, Long toTimestamp) {
        GlobalAnalyticsResponse globalAnalytics = analyticsQueryService.getGlobalAnalytics(fromTimestamp, toTimestamp);
        PagedResponse<DriverCardResponse> driverPage = driverQueryService.getDrivers(
                0,
                DASHBOARD_PAGE_SIZE,
                "longTermReliabilityScore",
                RISK_SORT_DIRECTION,
                null,
                null
        );
        PagedResponse<VehicleResponse> vehiclePage = vehicleQueryService.getVehicles(
                0,
                DASHBOARD_PAGE_SIZE,
                "updatedAt",
                DEFAULT_SORT_DIRECTION,
                null,
                null
        );
        PagedResponse<SessionSummaryResponse> sessionPage = sessionQueryService.getSessions(
                0,
                DASHBOARD_PAGE_SIZE,
                "startTimestamp",
                DEFAULT_SORT_DIRECTION,
                null,
                null,
                null,
                null,
                null
        );

        return pageViewModelMapper.toAdminDashboardPageViewModel(
                globalAnalytics,
                limitItems(driverPage.getItems(), DASHBOARD_PREVIEW_SIZE),
                limitItems(sessionPage.getItems(), DASHBOARD_PREVIEW_SIZE),
                limitItems(vehiclePage.getItems(), DASHBOARD_PREVIEW_SIZE),
                driverPage,
                sessionPage,
                vehiclePage,
                Math.toIntExact(driverPage.getTotalItems()),
                Math.toIntExact(vehiclePage.getTotalItems()),
                Math.toIntExact(sessionPage.getTotalItems()),
                resolveGeneratedAt(fromTimestamp, toTimestamp),
                summarizeFilters(fromTimestamp, toTimestamp)
        );
    }

    private <T> List<T> limitItems(List<T> items, int maxSize) {
        return items.stream().limit(maxSize).toList();
    }

    private long resolveGeneratedAt(Long fromTimestamp, Long toTimestamp) {
        if (toTimestamp != null) {
            return toTimestamp;
        }
        if (fromTimestamp != null) {
            return fromTimestamp;
        }
        return 0L;
    }

    private String summarizeFilters(Long fromTimestamp, Long toTimestamp) {
        if (fromTimestamp == null && toTimestamp == null) {
            return "All time";
        }
        if (fromTimestamp != null && toTimestamp != null) {
            return "Range: " + fromTimestamp + " to " + toTimestamp;
        }
        if (fromTimestamp != null) {
            return "From: " + fromTimestamp;
        }
        return "Until: " + toTimestamp;
    }

    private void validateRange(Long fromTimestamp, Long toTimestamp) {
        if (fromTimestamp != null && toTimestamp != null && fromTimestamp > toTimestamp) {
            throw new IllegalArgumentException("fromTimestamp must be less than or equal to toTimestamp");
        }
    }
}
