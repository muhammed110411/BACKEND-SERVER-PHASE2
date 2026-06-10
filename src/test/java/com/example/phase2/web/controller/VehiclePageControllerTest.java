package com.example.phase2.web.controller;

import com.example.phase2.api.admin.response.VehicleResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.application.command.VehicleManagementService;
import com.example.phase2.application.mapper.PageViewModelMapper;
import com.example.phase2.application.query.VehicleQueryService;
import com.example.phase2.application.security.ApiClientAuthenticationService;
import com.example.phase2.security.AuthenticationEntryPointHandler;
import com.example.phase2.application.viewmodel.VehicleFormPageViewModel;
import com.example.phase2.domain.enums.VehicleStatus;
import com.example.phase2.web.viewmodel.VehicleListPageViewModel;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(VehiclePageController.class)
class VehiclePageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleQueryService vehicleQueryService;

    @MockitoBean
    private VehicleManagementService vehicleManagementService;

    @MockitoBean
    private PageViewModelMapper pageViewModelMapper;

    @MockitoBean
    private ApiClientAuthenticationService apiClientAuthenticationService;

    @MockitoBean
    private AuthenticationEntryPointHandler authenticationEntryPointHandler;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getVehicleListPageRendersListViewWithDefaultFilters() throws Exception {
        PagedResponse<VehicleResponse> vehicles = new PagedResponse<>(
                List.of(),
                0,
                1,
                0,
                0,
                false,
                false
        );
        VehicleListPageViewModel viewModel = new VehicleListPageViewModel(
                vehicles,
                "displayName",
                "asc",
                null,
                null,
                0,
                "Vehicles",
                "No managed vehicles are available yet."
        );

        when(vehicleQueryService.getVehicles(0, 1, "displayName", "asc", null, null)).thenReturn(vehicles);
        when(pageViewModelMapper.toVehicleListPageViewModel(
                any(),
                eq("displayName"),
                eq("asc"),
                eq(null),
                eq(null),
                eq(0),
                eq("Vehicles"),
                eq("No managed vehicles are available yet.")
        )).thenReturn(viewModel);

        mockMvc.perform(get("/admin/vehicles"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/vehicles/list"))
                .andExpect(model().attribute("vehicleList", viewModel));

        verify(vehicleQueryService).getVehicles(0, 1, "displayName", "asc", null, null);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getVehicleListPageRendersListViewWithOptionalFilters() throws Exception {
        PagedResponse<VehicleResponse> probeVehicles = new PagedResponse<>(
                List.of(sampleVehicle()),
                0,
                1,
                2,
                2,
                true,
                false
        );
        PagedResponse<VehicleResponse> vehicles = new PagedResponse<>(
                List.of(
                        sampleVehicle(),
                        new VehicleResponse("vehicle-2", "Van 02", VehicleStatus.AVAILABLE, 1700000001L, 1700000600L, null)
                ),
                0,
                2,
                2,
                1,
                false,
                false
        );
        VehicleListPageViewModel viewModel = new VehicleListPageViewModel(
                vehicles,
                "displayName",
                "asc",
                "van",
                VehicleStatus.AVAILABLE,
                2,
                "Vehicles",
                "No vehicles matched the selected filters."
        );

        when(vehicleQueryService.getVehicles(
                0,
                1,
                "displayName",
                "asc",
                VehicleStatus.AVAILABLE,
                "van"
        )).thenReturn(probeVehicles);
        when(vehicleQueryService.getVehicles(
                0,
                2,
                "displayName",
                "asc",
                VehicleStatus.AVAILABLE,
                "van"
        )).thenReturn(vehicles);
        when(pageViewModelMapper.toVehicleListPageViewModel(
                any(),
                eq("displayName"),
                eq("asc"),
                eq("van"),
                eq(VehicleStatus.AVAILABLE),
                eq(2),
                eq("Vehicles"),
                eq("No vehicles matched the selected filters.")
        )).thenReturn(viewModel);

        mockMvc.perform(get("/admin/vehicles")
                        .param("search", "van")
                        .param("status", "available")
                        .param("sortBy", "displayName")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/vehicles/list"))
                .andExpect(model().attribute("vehicleList", viewModel));

        verify(vehicleQueryService).getVehicles(
                0,
                1,
                "displayName",
                "asc",
                VehicleStatus.AVAILABLE,
                "van"
        );
        verify(vehicleQueryService).getVehicles(
                0,
                2,
                "displayName",
                "asc",
                VehicleStatus.AVAILABLE,
                "van"
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCreateVehiclePageRendersCreateForm() throws Exception {
        VehicleFormPageViewModel viewModel = new VehicleFormPageViewModel(
                null,
                false,
                List.of("AVAILABLE", "BUSY", "OFFLINE", "UNKNOWN"),
                Map.of()
        );

        when(pageViewModelMapper.toVehicleFormPageViewModel(
                null,
                false,
                List.of("AVAILABLE", "BUSY", "OFFLINE", "UNKNOWN"),
                null
        )).thenReturn(viewModel);

        mockMvc.perform(get("/admin/vehicles/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/vehicles/form"))
                .andExpect(model().attribute("vehicleForm", viewModel));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEditVehiclePageRendersEditFormForExistingVehicle() throws Exception {
        VehicleResponse vehicle = sampleVehicle();
        VehicleFormPageViewModel viewModel = new VehicleFormPageViewModel(
                vehicle,
                true,
                List.of("AVAILABLE", "BUSY", "OFFLINE", "UNKNOWN"),
                Map.of()
        );

        when(vehicleQueryService.getVehicleById("vehicle-1")).thenReturn(vehicle);
        when(pageViewModelMapper.toVehicleFormPageViewModel(
                vehicle,
                true,
                List.of("AVAILABLE", "BUSY", "OFFLINE", "UNKNOWN"),
                null
        )).thenReturn(viewModel);

        mockMvc.perform(get("/admin/vehicles/vehicle-1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/vehicles/form"))
                .andExpect(model().attribute("vehicleForm", viewModel));

        verify(vehicleQueryService).getVehicleById("vehicle-1");
    }

    private VehicleResponse sampleVehicle() {
        return new VehicleResponse(
                "vehicle-1",
                "Van 01",
                VehicleStatus.AVAILABLE,
                1700000000L,
                1700000500L,
                null
        );
    }
}
