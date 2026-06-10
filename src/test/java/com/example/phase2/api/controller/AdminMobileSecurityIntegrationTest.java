package com.example.phase2.api.controller;

import com.example.phase2.api.admin.response.mobile.MobileAdminDashboardResponse;
import com.example.phase2.application.query.MobileAdminQueryService;
import com.example.phase2.application.security.ApiClientAuthenticationService;
import com.example.phase2.config.TimeProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.phase2.domain.enums.AdminAccountStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.domain.model.AdminUser;
import com.example.phase2.persistence.entity.AdminUserEntity;
import com.example.phase2.persistence.mapper.AdminUserPersistenceMapper;
import com.example.phase2.persistence.repository.AdminUserJpaRepository;
import com.example.phase2.security.AdminUserDetailsService;
import com.example.phase2.security.ApiClientAuthFilter;
import com.example.phase2.security.AuthenticationEntryPointHandler;
import com.example.phase2.security.SecurityConfig;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        AdminSessionAuthApiController.class,
        MobileAdminApiController.class
})
@Import({
        SecurityConfig.class,
        AuthenticationEntryPointHandler.class,
        AdminSessionAuthApiExceptionHandler.class,
        AdminMobileSecurityIntegrationTest.TestSecurityBeans.class,
        ApiClientAuthFilter.class
})
class AdminMobileSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AdminUserDetailsService adminUserDetailsService;

    @MockitoBean
    private AdminUserJpaRepository adminUserJpaRepository;

    @MockitoBean
    private AdminUserPersistenceMapper adminUserPersistenceMapper;

    @MockitoBean
    private MobileAdminQueryService mobileAdminQueryService;

    @MockitoBean
    private ApiClientAuthenticationService apiClientAuthenticationService;

    @MockitoBean
    private TimeProvider timeProvider;

    @BeforeEach
    void setUp() {
        when(timeProvider.nowEpochMillis()).thenReturn(1712345678901L);
    }

    @Test
    void unauthenticatedPostLoginReachesAuthenticationPathAndCreatesSessionForValidCredentials() throws Exception {
        stubValidAdminAuthentication();

        MvcResult result = mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"ops-admin","password":"Password1"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adminId").value("admin-1"))
                .andExpect(jsonPath("$.username").value("ops-admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn();

        assertNotNull(result.getRequest().getSession(false));
        verify(adminUserDetailsService).loadUserByUsername("ops-admin");
    }

    @Test
    void invalidAdminCredentialsReturnUnauthorized() throws Exception {
        UserDetails userDetails = User.withUsername("ops-admin")
                .password(passwordEncoder.encode("Password1"))
                .roles("ADMIN")
                .build();
        when(adminUserDetailsService.loadUserByUsername("ops-admin")).thenReturn(userDetails);

        mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"ops-admin","password":"WrongPassword1"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Invalid username or password."))
                .andExpect(jsonPath("$.path").value("/api/admin/auth/login"));
    }

    @Test
    void unauthenticatedAdminSessionEndpointReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/auth/session"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.path").value("/api/admin/auth/session"));
    }

    @Test
    void unauthenticatedMobileDashboardReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/mobile/dashboard"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.path").value("/api/admin/mobile/dashboard"));
    }

    @Test
    void authenticatedMobileDashboardReturnsOkWithAdminSession() throws Exception {
        stubValidAdminAuthentication();
        when(mobileAdminQueryService.getDashboard(eq(null), eq(null))).thenReturn(
                new MobileAdminDashboardResponse(3, 2, 4, 8, 87.5d, List.of())
        );

        MvcResult loginResult = mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"ops-admin","password":"Password1"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertNotNull(session);

        mockMvc.perform(get("/api/admin/mobile/dashboard").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDrivers").value(3))
                .andExpect(jsonPath("$.activeDrivers").value(2));
    }

    private void stubValidAdminAuthentication() {
        UserDetails userDetails = User.withUsername("ops-admin")
                .password(passwordEncoder.encode("Password1"))
                .roles("ADMIN")
                .build();
        AdminUserEntity entity = mock(AdminUserEntity.class);
        AdminUser adminUser = new AdminUser(
                "admin-1",
                "ops-admin",
                userDetails.getPassword(),
                AdminAccountStatus.ACTIVE,
                UserRole.ADMIN,
                1L,
                2L,
                null,
                null
        );

        when(adminUserDetailsService.loadUserByUsername("ops-admin")).thenReturn(userDetails);
        when(adminUserJpaRepository.findByUsername(eq("ops-admin"))).thenReturn(Optional.of(entity));
        when(adminUserPersistenceMapper.toDomain(entity)).thenReturn(adminUser);
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
