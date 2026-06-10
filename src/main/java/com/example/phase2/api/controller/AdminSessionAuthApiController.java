package com.example.phase2.api.controller;

import com.example.phase2.api.admin.request.AdminApiLoginRequest;
import com.example.phase2.api.admin.response.AuthResponse;
import com.example.phase2.domain.model.AdminUser;
import com.example.phase2.persistence.mapper.AdminUserPersistenceMapper;
import com.example.phase2.persistence.repository.AdminUserJpaRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminSessionAuthApiController {

    private final AuthenticationManager authenticationManager;
    private final AdminUserJpaRepository adminUserJpaRepository;
    private final AdminUserPersistenceMapper adminUserPersistenceMapper;
    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    public AdminSessionAuthApiController(
            AuthenticationManager authenticationManager,
            AdminUserJpaRepository adminUserJpaRepository,
            AdminUserPersistenceMapper adminUserPersistenceMapper
    ) {
        this.authenticationManager = authenticationManager;
        this.adminUserJpaRepository = adminUserJpaRepository;
        this.adminUserPersistenceMapper = adminUserPersistenceMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AdminApiLoginRequest request,
            HttpServletRequest httpServletRequest
    ) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        HttpSession existingSession = httpServletRequest.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        return ResponseEntity.ok(toAuthResponse(authentication.getName()));
    }

    @GetMapping("/session")
    public ResponseEntity<AuthResponse> getSession(Authentication authentication) {
        return ResponseEntity.ok(toAuthResponse(authentication.getName()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        logoutHandler.logout(request, response, authentication);
        SecurityContextHolder.clearContext();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private AuthResponse toAuthResponse(String username) {
        AdminUser adminUser = adminUserJpaRepository.findByUsername(username)
                .map(adminUserPersistenceMapper::toDomain)
                .orElseThrow();
        return new AuthResponse(
                adminUser.getId(),
                adminUser.getUsername(),
                adminUser.getRole(),
                adminUser.getAccountStatus(),
                null
        );
    }
}
