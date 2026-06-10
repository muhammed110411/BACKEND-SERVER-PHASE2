package com.example.phase2.web.viewmodel;

import com.example.phase2.api.common.response.FieldValidationErrorResponse;
import com.example.phase2.domain.enums.VehicleStatus;

import java.util.ArrayList;
import java.util.List;

public class VehicleFormPageViewModel {

    private String vehicleId;
    private String displayName;
    private VehicleStatus status;
    private List<VehicleStatus> availableStatuses;
    private String formMode;
    private String pageTitle;
    private String submitAction;
    private String submitLabel;
    private String cancelUrl;
    private boolean editMode;
    private boolean createMode;
    private String formErrorMessage;
    private List<FieldValidationErrorResponse> fieldErrors;

    public VehicleFormPageViewModel() {
        this.availableStatuses = new ArrayList<>();
        this.fieldErrors = new ArrayList<>();
    }

    public VehicleFormPageViewModel(
            String vehicleId,
            String displayName,
            VehicleStatus status,
            List<VehicleStatus> availableStatuses,
            String formMode,
            String pageTitle,
            String submitAction,
            String submitLabel,
            String cancelUrl,
            boolean editMode,
            boolean createMode,
            String formErrorMessage,
            List<FieldValidationErrorResponse> fieldErrors
    ) {
        this.vehicleId = vehicleId;
        this.displayName = displayName;
        this.status = status;
        this.availableStatuses = availableStatuses == null ? new ArrayList<>() : new ArrayList<>(availableStatuses);
        this.formMode = formMode;
        this.pageTitle = pageTitle;
        this.submitAction = submitAction;
        this.submitLabel = submitLabel;
        this.cancelUrl = cancelUrl;
        this.editMode = editMode;
        this.createMode = createMode;
        this.formErrorMessage = formErrorMessage;
        this.fieldErrors = fieldErrors == null ? new ArrayList<>() : new ArrayList<>(fieldErrors);
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public List<VehicleStatus> getAvailableStatuses() {
        return availableStatuses;
    }

    public void setAvailableStatuses(List<VehicleStatus> availableStatuses) {
        this.availableStatuses = availableStatuses == null ? new ArrayList<>() : new ArrayList<>(availableStatuses);
    }

    public String getFormMode() {
        return formMode;
    }

    public void setFormMode(String formMode) {
        this.formMode = formMode;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getSubmitAction() {
        return submitAction;
    }

    public void setSubmitAction(String submitAction) {
        this.submitAction = submitAction;
    }

    public String getSubmitLabel() {
        return submitLabel;
    }

    public void setSubmitLabel(String submitLabel) {
        this.submitLabel = submitLabel;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isCreateMode() {
        return createMode;
    }

    public void setCreateMode(boolean createMode) {
        this.createMode = createMode;
    }

    public String getFormErrorMessage() {
        return formErrorMessage;
    }

    public void setFormErrorMessage(String formErrorMessage) {
        this.formErrorMessage = formErrorMessage;
    }

    public List<FieldValidationErrorResponse> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<FieldValidationErrorResponse> fieldErrors) {
        this.fieldErrors = fieldErrors == null ? new ArrayList<>() : new ArrayList<>(fieldErrors);
    }
}
