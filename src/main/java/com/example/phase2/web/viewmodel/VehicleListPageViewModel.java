package com.example.phase2.web.viewmodel;

import com.example.phase2.api.admin.response.VehicleResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.domain.enums.VehicleStatus;
import java.util.List;

public class VehicleListPageViewModel {

    private PagedResponse<VehicleResponse> vehicles;
    private String sortBy;
    private String sortDirection;
    private String searchQuery;
    private VehicleStatus statusFilter;
    private int totalVehicleCount;
    private String pageTitle;
    private String emptyStateMessage;
    private String errorMessage;

    public VehicleListPageViewModel() {
    }

    public VehicleListPageViewModel(
            PagedResponse<VehicleResponse> vehicles,
            String sortBy,
            String sortDirection,
            String searchQuery,
            VehicleStatus statusFilter,
            int totalVehicleCount,
            String pageTitle,
            String emptyStateMessage
    ) {
        this.vehicles = vehicles;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
        this.searchQuery = searchQuery;
        this.statusFilter = statusFilter;
        this.totalVehicleCount = Math.max(0, totalVehicleCount);
        this.pageTitle = pageTitle;
        this.emptyStateMessage = emptyStateMessage;
    }

    public PagedResponse<VehicleResponse> getVehicles() {
        return vehicles;
    }

    public void setVehicles(PagedResponse<VehicleResponse> vehicles) {
        this.vehicles = vehicles;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public VehicleStatus getStatusFilter() {
        return statusFilter;
    }

    public void setStatusFilter(VehicleStatus statusFilter) {
        this.statusFilter = statusFilter;
    }

    public int getTotalVehicleCount() {
        return totalVehicleCount;
    }

    public void setTotalVehicleCount(int totalVehicleCount) {
        this.totalVehicleCount = Math.max(0, totalVehicleCount);
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getEmptyStateMessage() {
        return emptyStateMessage;
    }

    public void setEmptyStateMessage(String emptyStateMessage) {
        this.emptyStateMessage = emptyStateMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getVisibleVehicleCount() {
        return getVehicleItems().size();
    }

    public int getAvailableVehicleCount() {
        return countByStatus(VehicleStatus.AVAILABLE);
    }

    public int getBusyVehicleCount() {
        return countByStatus(VehicleStatus.BUSY);
    }

    public int getOfflineVehicleCount() {
        return countByStatus(VehicleStatus.OFFLINE);
    }

    public int getUnknownVehicleCount() {
        return countByStatus(VehicleStatus.UNKNOWN);
    }

    public boolean hasVehicles() {
        return !getVehicleItems().isEmpty();
    }

    public boolean hasError() {
        return errorMessage != null && !errorMessage.isBlank();
    }

    public boolean hasActiveFilters() {
        return (searchQuery != null && !searchQuery.isBlank()) || statusFilter != null;
    }

    private List<VehicleResponse> getVehicleItems() {
        if (vehicles == null || vehicles.getItems() == null) {
            return List.of();
        }
        return vehicles.getItems();
    }

    private int countByStatus(VehicleStatus expectedStatus) {
        int count = 0;
        for (VehicleResponse vehicle : getVehicleItems()) {
            if (vehicle != null && vehicle.getStatus() == expectedStatus) {
                count += 1;
            }
        }
        return count;
    }
}
