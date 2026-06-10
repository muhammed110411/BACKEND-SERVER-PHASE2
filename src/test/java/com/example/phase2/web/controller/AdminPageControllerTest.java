package com.example.phase2.web.controller;

import com.example.phase2.application.mapper.PageViewModelMapper;
import com.example.phase2.application.query.AdminDashboardQueryService;
import com.example.phase2.application.security.ApiClientAuthenticationService;
import com.example.phase2.security.AuthenticationEntryPointHandler;
import com.example.phase2.web.viewmodel.AdminDashboardPageViewModel;
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

@WebMvcTest(AdminPageController.class)
class AdminPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminDashboardQueryService adminDashboardQueryService;

    @MockitoBean
    private PageViewModelMapper pageViewModelMapper;

    @MockitoBean
    private ApiClientAuthenticationService apiClientAuthenticationService;

    @MockitoBean
    private AuthenticationEntryPointHandler authenticationEntryPointHandler;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAdminDashboardRendersDashboardViewWithCanonicalPageModel() throws Exception {
        AdminDashboardPageViewModel dashboard = new AdminDashboardPageViewModel(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                0,
                0,
                0L,
                "All time"
        );
        when(adminDashboardQueryService.getDashboard()).thenReturn(dashboard);

        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attribute("dashboard", dashboard))
                .andExpect(model().attribute("driverSearch", (Object) null))
                .andExpect(model().attribute("sortBy", (Object) null))
                .andExpect(model().attribute("direction", (Object) null))
                .andExpect(model().attribute("fromTimestamp", (Object) null))
                .andExpect(model().attribute("toTimestamp", (Object) null));

        verify(adminDashboardQueryService).getDashboard();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAdminDashboardWithOptionalInputsPreservesUiEchoWithoutChangingQueryContract() throws Exception {
        AdminDashboardPageViewModel dashboard = new AdminDashboardPageViewModel(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                0,
                0,
                0L,
                "All time"
        );
        when(adminDashboardQueryService.getDashboard()).thenReturn(dashboard);

        mockMvc.perform(get("/admin")
                        .param("driverSearch", "aylin")
                        .param("sortBy", "updatedAt")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attribute("dashboard", dashboard))
                .andExpect(model().attribute("driverSearch", "aylin"))
                .andExpect(model().attribute("sortBy", "updatedAt"))
                .andExpect(model().attribute("direction", "desc"))
                .andExpect(model().attribute("fromTimestamp", (Object) null))
                .andExpect(model().attribute("toTimestamp", (Object) null));

        verify(adminDashboardQueryService).getDashboard();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAdminDashboardWithTimeWindowDelegatesToDashboardRangeQuery() throws Exception {
        AdminDashboardPageViewModel dashboard = new AdminDashboardPageViewModel(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                0,
                0,
                200L,
                "Range: 100 to 200"
        );
        when(adminDashboardQueryService.getDashboard(100L, 200L)).thenReturn(dashboard);

        mockMvc.perform(get("/admin")
                        .param("fromTimestamp", "100")
                        .param("toTimestamp", "200"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attribute("dashboard", dashboard))
                .andExpect(model().attribute("fromTimestamp", 100L))
                .andExpect(model().attribute("toTimestamp", 200L));

        verify(adminDashboardQueryService).getDashboard(100L, 200L);
    }
}
