package com.example.phase2.api.controller;

import com.example.phase2.api.driver.request.DriverLoginRequest;
import com.example.phase2.api.driver.response.DriverLoginResponse;
import com.example.phase2.api.driver.response.DriverSessionResponse;
import com.example.phase2.application.security.DriverAuthenticationService;
import com.example.phase2.application.security.DriverAuthenticationService.DriverLoginResult;
import com.example.phase2.domain.model.Driver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
@RequestMapping("/api/driver/auth")
public class DriverAuthApiController {

    private final DriverAuthenticationService driverAuthenticationService;
    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    public DriverAuthApiController(DriverAuthenticationService driverAuthenticationService) {
        this.driverAuthenticationService = driverAuthenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<DriverLoginResponse> login(
            @Valid @RequestBody DriverLoginRequest request,
            HttpServletRequest httpServletRequest
    ) {
        DriverLoginResult loginResult = driverAuthenticationService.authenticate(
                request.getEmail(),
                request.getPassword()
        );
        Driver driver = loginResult.driver();
        createDriverSession(httpServletRequest, driver);

        return ResponseEntity.ok(new DriverLoginResponse(
                driver.getId(),
                driver.getName(),
                driver.getEmail(),
                driver.getRole(),
                driver.getAccountStatus(),
                loginResult.message(),
                loginResult.token()
        ));
    }

    @GetMapping("/session")
    public ResponseEntity<DriverSessionResponse> getSession(Authentication authentication) {
        Driver driver = driverAuthenticationService.getAuthenticatedDriver(authentication.getName());
        return ResponseEntity.ok(toSessionResponse(driver));
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

    private static void createDriverSession(HttpServletRequest httpServletRequest, Driver driver) {
        HttpSession existingSession = httpServletRequest.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }

        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                driver.getId(),
                "N/A",
                List.of(new SimpleGrantedAuthority("ROLE_DRIVER"))
        );
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
    }

    private static DriverSessionResponse toSessionResponse(Driver driver) {
        return new DriverSessionResponse(
                driver.getId(),
                driver.getName(),
                driver.getEmail(),
                driver.getRole(),
                driver.getAccountStatus()
        );
    }
}
