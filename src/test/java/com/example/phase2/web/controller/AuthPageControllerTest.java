package com.example.phase2.web.controller;

import com.example.phase2.application.mapper.PageViewModelMapper;
import com.example.phase2.application.security.ApiClientAuthenticationService;
import com.example.phase2.security.AuthenticationEntryPointHandler;
import com.example.phase2.application.viewmodel.LoginPageViewModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AuthPageController.class)
class AuthPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PageViewModelMapper pageViewModelMapper;

    @MockitoBean
    private ApiClientAuthenticationService apiClientAuthenticationService;

    @MockitoBean
    private AuthenticationEntryPointHandler authenticationEntryPointHandler;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getLoginPageRendersDefaultLoginViewWithoutSecurityStateLeakage() throws Exception {
        LoginPageViewModel viewModel = new LoginPageViewModel(
                null,
                null,
                false,
                null,
                null
        );
        when(pageViewModelMapper.toLoginPageViewModel(null, null, false, null, null)).thenReturn(viewModel);

        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"))
                .andExpect(model().attribute("login", viewModel))
                .andExpect(model().attribute("logoutSuccess", false))
                .andExpect(model().attribute("loginProcessingPath", "/admin/login"));

        verify(pageViewModelMapper).toLoginPageViewModel(null, null, false, null, null);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getLoginPageRendersGenericFailureStateFromApprovedErrorParameter() throws Exception {
        LoginPageViewModel viewModel = new LoginPageViewModel(
                null,
                "Invalid username or password.",
                false,
                null,
                null
        );
        when(pageViewModelMapper.toLoginPageViewModel(
                null,
                "Invalid username or password.",
                false,
                null,
                null
        )).thenReturn(viewModel);

        mockMvc.perform(get("/admin/login").param("error", "anything"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"))
                .andExpect(model().attribute("login", viewModel))
                .andExpect(model().attribute("logoutSuccess", false))
                .andExpect(model().attribute("loginProcessingPath", "/admin/login"));

        verify(pageViewModelMapper).toLoginPageViewModel(
                null,
                "Invalid username or password.",
                false,
                null,
                null
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getLoginPageRendersLogoutSuccessStateFromApprovedLogoutParameter() throws Exception {
        LoginPageViewModel viewModel = new LoginPageViewModel(
                null,
                null,
                false,
                null,
                null
        );
        when(pageViewModelMapper.toLoginPageViewModel(null, null, false, null, null)).thenReturn(viewModel);

        mockMvc.perform(get("/admin/login").param("logout", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"))
                .andExpect(model().attribute("login", viewModel))
                .andExpect(model().attribute("logoutSuccess", true))
                .andExpect(model().attribute("loginProcessingPath", "/admin/login"));

        verify(pageViewModelMapper).toLoginPageViewModel(null, null, false, null, null);
    }
}
