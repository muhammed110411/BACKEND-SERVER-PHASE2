package com.example.phase2.api.driver.response;

import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.UserRole;

public class DriverSessionResponse {

    private String driverId;
    private String name;
    private String email;
    private UserRole role;
    private DriverAccountStatus accountStatus;

    public DriverSessionResponse() {
    }

    public DriverSessionResponse(
            String driverId,
            String name,
            String email,
            UserRole role,
            DriverAccountStatus accountStatus
    ) {
        this.driverId = driverId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.accountStatus = accountStatus;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

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

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public DriverAccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(DriverAccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }
}
