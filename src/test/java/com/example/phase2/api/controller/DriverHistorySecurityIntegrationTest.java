package com.example.phase2.api.controller;

import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.api.driver.response.DriverSessionDetailResponse;
import com.example.phase2.api.driver.response.DriverSessionPenaltiesResponse;
import com.example.phase2.api.driver.response.DriverSessionSummaryResponse;
import com.example.phase2.api.upload.response.UploadSessionResponse;
import com.example.phase2.application.command.SessionUploadService;
import com.example.phase2.application.query.MobileAdminQueryService;
import com.example.phase2.application.query.SessionQueryService;
import com.example.phase2.application.security.ApiClientAuthenticationService;
import com.example.phase2.application.security.AuthenticatedApiClient;
import com.example.phase2.application.security.DriverAuthenticationService;
import com.example.phase2.config.TimeProvider;
import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.domain.model.Driver;
import com.example.phase2.exception.ResourceNotFoundException;
import com.example.phase2.persistence.repository.AdminUserJpaRepository;
import com.example.phase2.security.AdminUserDetailsService;
import com.example.phase2.security.ApiClientAuthFilter;
import com.example.phase2.security.AuthenticationEntryPointHandler;
import com.example.phase2.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        DriverAuthApiController.class,
        DriverSessionApiController.class,
        MobileAdminApiController.class,
        SessionUploadApiController.class
})
@org.springframework.context.annotation.Import({
        SecurityConfig.class,
        AuthenticationEntryPointHandler.class,
        DriverApiExceptionHandler.class,
        ApiClientAuthFilter.class,
        DriverHistorySecurityIntegrationTest.TestSecurityBeans.class
})
class DriverHistorySecurityIntegrationTest {

    private static final String DRIVER_ID = "driver-1";
    private static final String OTHER_SESSION_ID = "session-other";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DriverAuthenticationService driverAuthenticationService;

    @MockitoBean
    private SessionQueryService sessionQueryService;

    @MockitoBean
    private AdminUserDetailsService adminUserDetailsService;

    @MockitoBean
    private AdminUserJpaRepository adminUserJpaRepository;

    @MockitoBean
    private MobileAdminQueryService mobileAdminQueryService;

    @MockitoBean
    private ApiClientAuthenticationService apiClientAuthenticationService;

    @MockitoBean
    private SessionUploadService sessionUploadService;

    @MockitoBean
    private TimeProvider timeProvider;

    @BeforeEach
    void setUp() {
        when(timeProvider.nowEpochMillis()).thenReturn(1712345678901L);
    }

    @Test
    void loginCreatesDriverSessionCookieAndKeepsResponseShape() throws Exception {
        Driver driver = activeDriver();
        when(driverAuthenticationService.authenticate("driver@example.com", "DriverPass1"))
                .thenReturn(new DriverAuthenticationService.DriverLoginResult(
                        driver,
                        "Driver authenticated successfully.",
                        null
                ));
        when(driverAuthenticationService.getAuthenticatedDriver(DRIVER_ID)).thenReturn(driver);

        MvcResult result = login();

        assertNotNull(result.getRequest().getSession(false));
        mockMvc.perform(get("/api/driver/auth/session")
                        .session((MockHttpSession) result.getRequest().getSession(false)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverId").value(DRIVER_ID));
    }

    @Test
    void unauthenticatedDriverSessionReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/driver/auth/session"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.path").value("/api/driver/auth/session"));
    }

    @Test
    void authenticatedDriverSessionReturnsDriverIdentity() throws Exception {
        MockHttpSession session = authenticatedDriverSession();

        mockMvc.perform(get("/api/driver/auth/session").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverId").value(DRIVER_ID))
                .andExpect(jsonPath("$.name").value("Demo Driver"))
                .andExpect(jsonPath("$.email").value("driver@example.com"))
                .andExpect(jsonPath("$.role").value("DRIVER"))
                .andExpect(jsonPath("$.accountStatus").value("ACTIVE"));
    }

    @Test
    void unauthenticatedDriverSessionsReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/driver/sessions"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.path").value("/api/driver/sessions"));
    }

    @Test
    void authenticatedDriverSessionsUsesAuthenticatedDriverScopeOnly() throws Exception {
        MockHttpSession session = authenticatedDriverSession();
        when(sessionQueryService.getDriverSessions(DRIVER_ID, 0, 20, "startTimestamp", "desc"))
                .thenReturn(new PagedResponse<>(
                        List.of(summary("session-own")),
                        0,
                        20,
                        1,
                        1,
                        false,
                        false
                ));

        mockMvc.perform(get("/api/driver/sessions").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].sessionId").value("session-own"))
                .andExpect(jsonPath("$.items[0].driverId").value(DRIVER_ID));

        verify(sessionQueryService).getDriverSessions(DRIVER_ID, 0, 20, "startTimestamp", "desc");
    }

    @Test
    void authenticatedDriverSessionDetailReturnsOwnSession() throws Exception {
        MockHttpSession session = authenticatedDriverSession();
        when(sessionQueryService.getDriverSessionDetail(DRIVER_ID, "session-own"))
                .thenReturn(detail("session-own"));

        mockMvc.perform(get("/api/driver/sessions/session-own").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session-own"))
                .andExpect(jsonPath("$.driverId").value(DRIVER_ID))
                .andExpect(jsonPath("$.penalties.event").value(1.5));

        verify(sessionQueryService).getDriverSessionDetail(DRIVER_ID, "session-own");
    }

    @Test
    void authenticatedDriverCannotAccessAnotherDriversSessionDetail() throws Exception {
        MockHttpSession session = authenticatedDriverSession();
        when(sessionQueryService.getDriverSessionDetail(DRIVER_ID, OTHER_SESSION_ID))
                .thenThrow(new ResourceNotFoundException(
                        "Session not found: " + OTHER_SESSION_ID,
                        "session",
                        OTHER_SESSION_ID,
                        "id"
                ));

        mockMvc.perform(get("/api/driver/sessions/{sessionId}", OTHER_SESSION_ID).session(session))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));

        verify(sessionQueryService).getDriverSessionDetail(DRIVER_ID, OTHER_SESSION_ID);
    }

    @Test
    void driverSessionDoesNotGrantAdminApiAccess() throws Exception {
        MockHttpSession session = authenticatedDriverSession();

        mockMvc.perform(get("/api/admin/mobile/dashboard").session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    void uploadBasicAuthStillReachesUploadController() throws Exception {
        when(apiClientAuthenticationService.authenticate("upload-client", "secret"))
                .thenReturn(new AuthenticatedApiClient("upload-client", true, true, "Upload Client"));
        when(sessionUploadService.uploadSession(any(), eq("upload-client")))
                .thenReturn(new UploadSessionResponse(
                        "upload-session",
                        UploadProcessingStatus.PROCESSED,
                        100L,
                        100L,
                        List.of()
                ));

        mockMvc.perform(post("/api/upload/sessions")
                        .with(httpBasic("upload-client", "secret"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("upload-session"));
    }

    private MockHttpSession authenticatedDriverSession() throws Exception {
        Driver driver = activeDriver();
        when(driverAuthenticationService.authenticate("driver@example.com", "DriverPass1"))
                .thenReturn(new DriverAuthenticationService.DriverLoginResult(
                        driver,
                        "Driver authenticated successfully.",
                        null
                ));
        when(driverAuthenticationService.getAuthenticatedDriver(DRIVER_ID)).thenReturn(driver);

        return (MockHttpSession) login().getRequest().getSession(false);
    }

    private MvcResult login() throws Exception {
        return mockMvc.perform(post("/api/driver/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"driver@example.com","password":"DriverPass1"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverId").value(DRIVER_ID))
                .andExpect(jsonPath("$.name").value("Demo Driver"))
                .andExpect(jsonPath("$.email").value("driver@example.com"))
                .andExpect(jsonPath("$.role").value("DRIVER"))
                .andExpect(jsonPath("$.accountStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.message").value("Driver authenticated successfully."))
                .andExpect(jsonPath("$.token").doesNotExist())
                .andReturn();
    }

    private static Driver activeDriver() {
        return new Driver(
                DRIVER_ID,
                "Demo Driver",
                "driver@example.com",
                UserRole.DRIVER,
                DriverAccountStatus.ACTIVE,
                98.0,
                3,
                42.5,
                1L,
                2L,
                null
        );
    }

    private static DriverSessionSummaryResponse summary(String sessionId) {
        return new DriverSessionSummaryResponse(
                sessionId,
                DRIVER_ID,
                "Demo Driver",
                "vehicle-1",
                "Truck 1",
                92.5,
                SessionValidity.VALID,
                SessionEndStatus.COMPLETED,
                10L,
                20L,
                30L,
                1,
                1
        );
    }

    private static DriverSessionDetailResponse detail(String sessionId) {
        return new DriverSessionDetailResponse(
                sessionId,
                DRIVER_ID,
                "Demo Driver",
                "vehicle-1",
                "Truck 1",
                92.5,
                SessionValidity.VALID,
                SessionEndStatus.COMPLETED,
                10L,
                20L,
                30L,
                1,
                1,
                new DriverSessionPenaltiesResponse(0.5, 1.5, 2.5),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }

    @TestConfiguration
    static class TestSecurityBeans {

        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
