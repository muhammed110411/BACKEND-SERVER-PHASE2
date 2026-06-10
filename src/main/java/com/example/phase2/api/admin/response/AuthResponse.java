package com.example.phase2.api.admin.response;

import com.example.phase2.domain.enums.AdminAccountStatus;
import com.example.phase2.domain.enums.UserRole;

public class AuthResponse {

    private String adminId;
    private String username;
    private UserRole role;
    private AdminAccountStatus accountStatus;
    private String token;

    public AuthResponse() {
    }

    public AuthResponse(
            String adminId,
            String username,
            UserRole role,
            AdminAccountStatus accountStatus,
            String token
    ) {
        this.adminId = adminId;
        this.username = username;
        this.role = role;
        this.accountStatus = accountStatus;
        this.token = token;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public AdminAccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AdminAccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
