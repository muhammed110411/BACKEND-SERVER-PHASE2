package com.example.phase2.api.controller;

import com.example.phase2.domain.enums.AdminAccountStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.domain.model.AdminUser;
import com.example.phase2.persistence.entity.AdminUserEntity;
import com.example.phase2.persistence.mapper.AdminUserPersistenceMapper;
import com.example.phase2.persistence.repository.AdminUserJpaRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminSessionAuthApiControllerTest {

    private AuthenticationManager authenticationManager;
    private AdminUserJpaRepository adminUserJpaRepository;
    private AdminUserPersistenceMapper adminUserPersistenceMapper;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        adminUserJpaRepository = mock(AdminUserJpaRepository.class);
        adminUserPersistenceMapper = mock(AdminUserPersistenceMapper.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AdminSessionAuthApiController(
                authenticationManager,
                adminUserJpaRepository,
                adminUserPersistenceMapper
        )).build();
    }

    @Test
    void loginCreatesSessionAndReturnsAdminShape() throws Exception {
        Authentication authenticated = UsernamePasswordAuthenticationToken.authenticated(
                "ops-admin",
                "ignored",
                java.util.List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        AdminUserEntity entity = mock(AdminUserEntity.class);
        AdminUser adminUser = new AdminUser(
                "admin-1",
                "ops-admin",
                "hash",
                AdminAccountStatus.ACTIVE,
                UserRole.ADMIN,
                1L,
                2L,
                null,
                null
        );

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authenticated);
        when(adminUserJpaRepository.findByUsername(eq("ops-admin"))).thenReturn(Optional.of(entity));
        when(adminUserPersistenceMapper.toDomain(entity)).thenReturn(adminUser);

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
        assertNotNull(result.getRequest().getSession(false)
                .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
        verify(authenticationManager).authenticate(any(Authentication.class));
    }
}
