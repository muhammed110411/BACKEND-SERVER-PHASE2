package com.example.phase2.web.viewmodel;

import com.example.phase2.domain.enums.DriverAccountStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DriverCreateForm {

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email address.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters.")
    private String password;

    @NotBlank(message = "Confirm password is required.")
    private String confirmPassword;

    @NotNull(message = "Account status is required.")
    private DriverAccountStatus accountStatus = DriverAccountStatus.ACTIVE;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public DriverAccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(DriverAccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public void clearPasswordFields() {
        this.password = null;
        this.confirmPassword = null;
    }
}
