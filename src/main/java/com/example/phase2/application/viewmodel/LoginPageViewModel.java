package com.example.phase2.application.viewmodel;

import com.example.phase2.api.common.response.FieldValidationErrorResponse;

import java.util.List;

public class LoginPageViewModel {

    private String username;
    private String errorMessage;
    private boolean authenticated;
    private String redirectUrl;
    private List<FieldValidationErrorResponse> fieldErrors;

    public LoginPageViewModel() {
    }

    public LoginPageViewModel(
            String username,
            String errorMessage,
            boolean authenticated,
            String redirectUrl,
            List<FieldValidationErrorResponse> fieldErrors
    ) {
        this.username = username;
        this.errorMessage = errorMessage;
        this.authenticated = authenticated;
        this.redirectUrl = redirectUrl;
        this.fieldErrors = fieldErrors;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public List<FieldValidationErrorResponse> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<FieldValidationErrorResponse> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
