package com.example.phase2.application.query;

import com.example.phase2.api.admin.response.GlobalAnalyticsResponse;
import com.example.phase2.application.mapper.PageViewModelMapper;
import com.example.phase2.web.viewmodel.FleetAnalyticsPageViewModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FleetAnalyticsPageQueryService {

    private final AnalyticsQueryService analyticsQueryService;
    private final PageViewModelMapper pageViewModelMapper;

    public FleetAnalyticsPageQueryService(
            AnalyticsQueryService analyticsQueryService,
            PageViewModelMapper pageViewModelMapper
    ) {
        this.analyticsQueryService = analyticsQueryService;
        this.pageViewModelMapper = pageViewModelMapper;
    }

    public FleetAnalyticsPageViewModel getFleetAnalyticsPage(Long fromTimestamp, Long toTimestamp) {
        GlobalAnalyticsResponse analytics = analyticsQueryService.getGlobalAnalytics(fromTimestamp, toTimestamp);
        return pageViewModelMapper.toFleetAnalyticsPageViewModel(
                analytics,
                fromTimestamp,
                toTimestamp,
                "Fleet Analytics",
                summarizeFilters(fromTimestamp, toTimestamp),
                hasAnalyticsData(analytics),
                "Fleet analytics appear after historical uploads create drivers, vehicles, and sessions in scope."
        );
    }

    private boolean hasAnalyticsData(GlobalAnalyticsResponse analytics) {
        return analytics != null && (
                analytics.getTotalDrivers() > 0
                        || analytics.getTotalVehicles() > 0
                        || analytics.getTotalSessions() > 0
                        || analytics.getTotalEventCount() > 0
                        || analytics.getTotalEscalationCount() > 0
        );
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
}
