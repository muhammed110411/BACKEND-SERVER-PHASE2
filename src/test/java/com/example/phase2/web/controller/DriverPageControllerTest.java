package com.example.phase2.web.controller;

import com.example.phase2.api.admin.response.DriverAnalyticsResponse;
import com.example.phase2.api.admin.response.DriverCardResponse;
import com.example.phase2.api.admin.response.DriverDetailResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.application.command.DriverLifecycleService;
import com.example.phase2.application.mapper.PageViewModelMapper;
import com.example.phase2.application.query.DriverQueryService;
import com.example.phase2.application.security.ApiClientAuthenticationService;
import com.example.phase2.config.TimeProvider;
import com.example.phase2.security.AuthenticationEntryPointHandler;
import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.domain.model.Driver;
import com.example.phase2.web.viewmodel.DriverAnalyticsPageViewModel;
import com.example.phase2.web.viewmodel.DriverDetailPageViewModel;
import com.example.phase2.web.viewmodel.DriverListPageViewModel;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(DriverPageController.class)
class DriverPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DriverQueryService driverQueryService;

    @MockitoBean
    private DriverLifecycleService driverLifecycleService;

    @MockitoBean
    private PageViewModelMapper pageViewModelMapper;

    @MockitoBean
    private ApiClientAuthenticationService apiClientAuthenticationService;

    @MockitoBean
    private AuthenticationEntryPointHandler authenticationEntryPointHandler;

    @MockitoBean
    private TimeProvider timeProvider;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDriverListPageRendersListViewWithDefaultFilters() throws Exception {
        PagedResponse<DriverCardResponse> drivers = new PagedResponse<>(
                List.of(),
                0,
                200,
                0,
                0,
                false,
                false
        );
        DriverListPageViewModel viewModel = new DriverListPageViewModel(
                drivers,
                "name",
                "asc",
                null,
                null,
                0,
                "admin",
                "Drivers",
                "No drivers matched the selected filters."
        );

        when(driverQueryService.getDrivers(0, 200, "name", "asc", null, null)).thenReturn(drivers);
        when(pageViewModelMapper.toDriverListPageViewModel(
                drivers,
                "name",
                "asc",
                null,
                null,
                0,
                "admin",
                "Drivers",
                "No drivers matched the selected filters."
        )).thenReturn(viewModel);

        mockMvc.perform(get("/admin/drivers"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/drivers/list"))
                .andExpect(model().attribute("driverList", viewModel));

        verify(driverQueryService).getDrivers(0, 200, "name", "asc", null, null);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDriverListPageRendersListViewWithOptionalFilters() throws Exception {
        PagedResponse<DriverCardResponse> drivers = new PagedResponse<>(
                List.of(),
                0,
                200,
                1,
                1,
                false,
                false
        );
        DriverListPageViewModel viewModel = new DriverListPageViewModel(
                drivers,
                "name",
                "asc",
                "aylin",
                DriverAccountStatus.ACTIVE,
                1,
                "admin",
                "Drivers",
                "No drivers matched the selected filters."
        );

        when(driverQueryService.getDrivers(
                0,
                200,
                "name",
                "asc",
                DriverAccountStatus.ACTIVE,
                "aylin"
        )).thenReturn(drivers);
        when(pageViewModelMapper.toDriverListPageViewModel(
                drivers,
                "name",
                "asc",
                "aylin",
                DriverAccountStatus.ACTIVE,
                1,
                "admin",
                "Drivers",
                "No drivers matched the selected filters."
        )).thenReturn(viewModel);

        mockMvc.perform(get("/admin/drivers")
                        .param("search", "aylin")
                        .param("status", "active")
                        .param("sortBy", "name")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/drivers/list"))
                .andExpect(model().attribute("driverList", viewModel));

        verify(driverQueryService).getDrivers(
                0,
                200,
                "name",
                "asc",
                DriverAccountStatus.ACTIVE,
                "aylin"
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCreateDriverPageRendersAdminCreateForm() throws Exception {
        mockMvc.perform(get("/admin/drivers/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/drivers/create"))
                .andExpect(model().attributeExists("driverForm"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDriverPostsToLifecycleServiceWithFixedDriverRole() throws Exception {
        Driver createdDriver = new Driver(
                "driver-new",
                "Aylin Kaya",
                "aylin@example.com",
                UserRole.DRIVER,
                DriverAccountStatus.ACTIVE,
                0.0d,
                0,
                0.0d,
                100L,
                100L,
                null
        );
        when(driverLifecycleService.createDriver(
                "Aylin Kaya",
                "aylin@example.com",
                "DriverPass1",
                UserRole.DRIVER
        )).thenReturn(createdDriver);

        mockMvc.perform(post("/admin/drivers/new")
                        .with(csrf())
                        .param("name", "Aylin Kaya")
                        .param("email", "aylin@example.com")
                        .param("password", "DriverPass1")
                        .param("confirmPassword", "DriverPass1")
                        .param("accountStatus", "ACTIVE")
                        .param("role", "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/drivers/driver-new?created=true"));

        verify(driverLifecycleService).createDriver(
                "Aylin Kaya",
                "aylin@example.com",
                "DriverPass1",
                UserRole.DRIVER
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDriverRendersFieldErrorForDuplicateEmailWithoutKeepingPasswordsInModel() throws Exception {
        when(driverLifecycleService.createDriver(
                "Aylin Kaya",
                "aylin@example.com",
                "DriverPass1",
                UserRole.DRIVER
        )).thenThrow(new IllegalArgumentException("Driver email is already in use: aylin@example.com"));

        mockMvc.perform(post("/admin/drivers/new")
                        .with(csrf())
                        .param("name", "Aylin Kaya")
                        .param("email", "aylin@example.com")
                        .param("password", "DriverPass1")
                        .param("confirmPassword", "DriverPass1")
                        .param("accountStatus", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/drivers/create"))
                .andExpect(model().attributeHasFieldErrors("driverForm", "email"))
                .andExpect(model().attribute("driverForm", hasProperty("password", nullValue())))
                .andExpect(model().attribute("driverForm", hasProperty("confirmPassword", nullValue())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDriverRejectsMismatchedPasswordConfirmation() throws Exception {
        mockMvc.perform(post("/admin/drivers/new")
                        .with(csrf())
                        .param("name", "Aylin Kaya")
                        .param("email", "aylin@example.com")
                        .param("password", "DriverPass1")
                        .param("confirmPassword", "DriverPass2")
                        .param("accountStatus", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/drivers/create"))
                .andExpect(model().attributeHasFieldErrors("driverForm", "confirmPassword"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDriverDetailPageRendersReadOnlyAdministrativeRecordView() throws Exception {
        DriverDetailResponse driver = sampleDriverDetail();
        DriverDetailPageViewModel viewModel = new DriverDetailPageViewModel(
                driver,
                "Driver Detail",
                true,
                true,
                "Driver record could not be found.",
                null
        );

        when(driverQueryService.getDriverDetail("driver-1")).thenReturn(driver);
        when(pageViewModelMapper.toDriverDetailPageViewModel(
                driver,
                "Driver Detail",
                true,
                true,
                "Driver record could not be found.",
                null
        )).thenReturn(viewModel);

        mockMvc.perform(get("/admin/drivers/driver-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/drivers/detail"))
                .andExpect(model().attribute("driverDetail", viewModel));

        verify(driverQueryService).getDriverDetail("driver-1");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDriverAnalyticsPageRendersHistoricalAnalyticsView() throws Exception {
        DriverDetailResponse driver = sampleDriverDetail();
        DriverAnalyticsResponse analytics = new DriverAnalyticsResponse();
        analytics.setTotalSessions(1);
        DriverAnalyticsPageViewModel viewModel = new DriverAnalyticsPageViewModel(
                "driver-1",
                analytics,
                driver,
                null,
                null,
                "Driver Analytics",
                "All time",
                true,
                "No analytical data yet. Analytics will appear after session uploads."
        );

        when(driverQueryService.getDriverDetail("driver-1")).thenReturn(driver);
        when(driverQueryService.getDriverAnalytics("driver-1", null, null)).thenReturn(analytics);
        when(pageViewModelMapper.toDriverAnalyticsPageViewModel(
                "driver-1",
                analytics,
                driver,
                null,
                null,
                "Driver Analytics",
                "All time",
                true,
                "No analytical data yet. Analytics will appear after session uploads."
        )).thenReturn(viewModel);

        mockMvc.perform(get("/admin/drivers/driver-1/analytics"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/drivers/analytics"))
                .andExpect(model().attribute("driverAnalytics", viewModel));

        verify(driverQueryService).getDriverDetail("driver-1");
        verify(driverQueryService).getDriverAnalytics("driver-1", null, null);
    }

    private DriverDetailResponse sampleDriverDetail() {
        return new DriverDetailResponse(
                "driver-1",
                "Aylin Kaya",
                "aylin@example.com",
                DriverAccountStatus.ACTIVE,
                UserRole.DRIVER,
                91.5d,
                12,
                300.0d,
                100L,
                200L,
                null
        );
    }
}
