package com.example.phase2.web.viewmodel;

import com.example.phase2.api.admin.response.DriverCardResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.domain.enums.DriverAccountStatus;

public class DriverListPageViewModel {

    private final PagedResponse<DriverCardResponse> drivers;
    private final String sortBy;
    private final String sortDirection;
    private final String searchQuery;
    private final DriverAccountStatus statusFilter;
    private final int totalDriverCount;
    private final String viewMode;
    private final String pageTitle;
    private final String emptyStateMessage;

    public DriverListPageViewModel(
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
        this.drivers = drivers;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
        this.searchQuery = searchQuery;
        this.statusFilter = statusFilter;
        this.totalDriverCount = Math.max(0, totalDriverCount);
        this.viewMode = viewMode;
        this.pageTitle = pageTitle;
        this.emptyStateMessage = emptyStateMessage;
    }

    public PagedResponse<DriverCardResponse> getDrivers() {
        return drivers;
    }

    public String getSortBy() {
        return sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public DriverAccountStatus getStatusFilter() {
        return statusFilter;
    }

    public int getTotalDriverCount() {
        return totalDriverCount;
    }

    public String getViewMode() {
        return viewMode;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getEmptyStateMessage() {
        return emptyStateMessage;
    }
}
