package com.example.phase2.web.controller;

import com.example.phase2.application.query.FleetAnalyticsPageQueryService;
import com.example.phase2.application.security.ApiClientAuthenticationService;
import com.example.phase2.security.AuthenticationEntryPointHandler;
import com.example.phase2.web.viewmodel.FleetAnalyticsPageViewModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(FleetAnalyticsPageController.class)
class FleetAnalyticsPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FleetAnalyticsPageQueryService fleetAnalyticsPageQueryService;

    @MockitoBean
    private ApiClientAuthenticationService apiClientAuthenticationService;

    @MockitoBean
    private AuthenticationEntryPointHandler authenticationEntryPointHandler;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getFleetAnalyticsPageRendersAnalyticsView() throws Exception {
        FleetAnalyticsPageViewModel viewModel = new FleetAnalyticsPageViewModel(
                null,
                null,
                null,
                "Fleet Analytics",
                "All time",
                false,
                "Fleet analytics appear after historical uploads create drivers, vehicles, and sessions in scope."
        );

        when(fleetAnalyticsPageQueryService.getFleetAnalyticsPage(null, null)).thenReturn(viewModel);

        mockMvc.perform(get("/admin/fleet-analytics"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/fleet-analytics"))
                .andExpect(model().attribute("fleetAnalytics", viewModel));

        verify(fleetAnalyticsPageQueryService).getFleetAnalyticsPage(null, null);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getFleetAnalyticsPageWithWindowDelegatesTimeRange() throws Exception {
        FleetAnalyticsPageViewModel viewModel = new FleetAnalyticsPageViewModel(
                null,
                100L,
                200L,
                "Fleet Analytics",
                "Range: 100 to 200",
                true,
                "Fleet analytics appear after historical uploads create drivers, vehicles, and sessions in scope."
        );

        when(fleetAnalyticsPageQueryService.getFleetAnalyticsPage(100L, 200L)).thenReturn(viewModel);

        mockMvc.perform(get("/admin/fleet-analytics")
                        .param("fromTimestamp", "100")
                        .param("toTimestamp", "200"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/fleet-analytics"))
                .andExpect(model().attribute("fleetAnalytics", viewModel));

        verify(fleetAnalyticsPageQueryService).getFleetAnalyticsPage(100L, 200L);
    }
}
