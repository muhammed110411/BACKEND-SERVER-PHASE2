package com.example.phase2.web.viewmodel;

import com.example.phase2.api.common.response.FieldValidationErrorResponse;

import java.util.ArrayList;
import java.util.List;

public class LoginPageViewModel {

    private String username;
    private String password;
    private String pageTitle;
    private String loginAction;
    private String submitLabel;
    private String errorMessage;
    private boolean authenticated;
    private String redirectUrl;
    private List<FieldValidationErrorResponse> fieldErrors;

    public LoginPageViewModel() {
        this.fieldErrors = new ArrayList<>();
    }

    public LoginPageViewModel(
            String username,
            String password,
            String pageTitle,
            String loginAction,
            String submitLabel,
            String errorMessage,
            boolean authenticated,
            String redirectUrl,
            List<FieldValidationErrorResponse> fieldErrors
    ) {
        this.username = username;
        this.password = password;
        this.pageTitle = pageTitle;
        this.loginAction = loginAction;
        this.submitLabel = submitLabel;
        this.errorMessage = errorMessage;
        this.authenticated = authenticated;
        this.redirectUrl = redirectUrl;
        this.fieldErrors = fieldErrors == null ? new ArrayList<>() : new ArrayList<>(fieldErrors);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getLoginAction() {
        return loginAction;
    }

    public void setLoginAction(String loginAction) {
        this.loginAction = loginAction;
    }

    public String getSubmitLabel() {
        return submitLabel;
    }

    public void setSubmitLabel(String submitLabel) {
        this.submitLabel = submitLabel;
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
        this.fieldErrors = fieldErrors == null ? new ArrayList<>() : new ArrayList<>(fieldErrors);
    }
}
