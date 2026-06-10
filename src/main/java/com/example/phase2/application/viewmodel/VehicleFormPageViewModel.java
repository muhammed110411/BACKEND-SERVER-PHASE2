package com.example.phase2.application.viewmodel;

import com.example.phase2.api.admin.response.VehicleResponse;

import java.util.List;
import java.util.Map;

public class VehicleFormPageViewModel {

    private VehicleResponse vehicle;
    private Boolean editMode;
    private List<String> allowedStatuses;
    private Map<String, String> fieldErrors;

    public VehicleFormPageViewModel() {
    }

    public VehicleFormPageViewModel(
            VehicleResponse vehicle,
            Boolean editMode,
            List<String> allowedStatuses,
            Map<String, String> fieldErrors
    ) {
        this.vehicle = vehicle;
        this.editMode = editMode;
        this.allowedStatuses = allowedStatuses;
        this.fieldErrors = fieldErrors;
    }

    public VehicleResponse getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleResponse vehicle) {
        this.vehicle = vehicle;
    }

    public Boolean getEditMode() {
        return editMode;
    }

    public void setEditMode(Boolean editMode) {
        this.editMode = editMode;
    }

    public List<String> getAllowedStatuses() {
        return allowedStatuses;
    }

    public void setAllowedStatuses(List<String> allowedStatuses) {
        this.allowedStatuses = allowedStatuses;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
