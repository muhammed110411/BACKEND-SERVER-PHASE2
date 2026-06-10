package com.example.phase2.api.controller;

import com.example.phase2.api.admin.request.ChangePasswordRequest;
import com.example.phase2.api.admin.response.AuthResponse;
import com.example.phase2.application.command.AdminAccountService;
import com.example.phase2.domain.model.AdminUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AuthApiController {

    private final AdminAccountService adminAccountService;

    public AuthApiController(AdminAccountService adminAccountService) {
        this.adminAccountService = adminAccountService;
    }

    @PostMapping("/account/password")
    public ResponseEntity<AuthResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ) {
        AdminUser adminUser = adminAccountService.changePasswordForUsername(
                authentication.getName(),
                request.getCurrentPassword(),
                request.getNewPassword(),
                request.getConfirmNewPassword()
        );

        return ResponseEntity.ok(new AuthResponse(
                adminUser.getId(),
                adminUser.getUsername(),
                adminUser.getRole(),
                adminUser.getAccountStatus(),
                null
        ));
    }
}
