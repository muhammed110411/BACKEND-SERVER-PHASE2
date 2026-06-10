package com.example.phase2.application.viewmodel;

import com.example.phase2.api.admin.response.VehicleResponse;
import com.example.phase2.api.common.response.PagedResponse;

public class VehicleListPageViewModel {

    private PagedResponse<VehicleResponse> vehicles;
    private String sortBy;
    private String sortDirection;
    private String searchQuery;

    public VehicleListPageViewModel() {
    }

    public VehicleListPageViewModel(
            PagedResponse<VehicleResponse> vehicles,
            String sortBy,
            String sortDirection,
            String searchQuery
    ) {
        this.vehicles = vehicles;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
        this.searchQuery = searchQuery;
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
}
