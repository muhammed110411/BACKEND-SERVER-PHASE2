package com.example.phase2.web.controller;

import com.example.phase2.api.admin.response.VehicleResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.application.command.VehicleManagementService;
import com.example.phase2.application.mapper.PageViewModelMapper;
import com.example.phase2.application.query.VehicleQueryService;
import com.example.phase2.application.viewmodel.VehicleFormPageViewModel;
import com.example.phase2.domain.enums.VehicleStatus;
import com.example.phase2.web.viewmodel.VehicleListPageViewModel;
import java.util.Arrays;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/vehicles")
public class VehiclePageController {

    private static final String VEHICLE_LIST_VIEW_NAME = "admin/vehicles/list";
    private static final String VEHICLE_FORM_VIEW_NAME = "admin/vehicles/form";

    private static final String VEHICLE_LIST_MODEL_ATTRIBUTE = "vehicleList";
    private static final String VEHICLE_FORM_MODEL_ATTRIBUTE = "vehicleForm";

    private static final int FIRST_PAGE_INDEX = 0;
    private static final int VEHICLE_TOTAL_COUNT_PROBE_SIZE = 1;
    private static final String DEFAULT_SORT_BY = "displayName";
    private static final String DEFAULT_DIRECTION = "asc";
    private static final String VEHICLE_LIST_EMPTY_STATE = "No vehicles matched the selected filters.";
    private static final String VEHICLE_LIST_INITIAL_EMPTY_STATE = "No managed vehicles are available yet.";
    private static final String VEHICLE_LIST_ERROR_STATE = "Vehicle roster could not be loaded right now. Retry when the service is reachable.";

    private final VehicleQueryService vehicleQueryService;
    private final VehicleManagementService vehicleManagementService;
    private final PageViewModelMapper pageViewModelMapper;

    public VehiclePageController(
            VehicleQueryService vehicleQueryService,
            VehicleManagementService vehicleManagementService,
            PageViewModelMapper pageViewModelMapper
    ) {
        this.vehicleQueryService = vehicleQueryService;
        this.vehicleManagementService = vehicleManagementService;
        this.pageViewModelMapper = pageViewModelMapper;
    }

    @GetMapping(params = {"!search", "!status", "!sortBy", "!direction"})
    public String getVehicleListPage(Model model) {
        return renderVehicleListPage(null, null, null, null, model);
    }

    @GetMapping
    public String getVehicleListPage(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction,
            Model model
    ) {
        return renderVehicleListPage(search, status, sortBy, direction, model);
    }

    @GetMapping("/new")
    public String getCreateVehiclePage(Model model) {
        VehicleFormPageViewModel viewModel = pageViewModelMapper.toVehicleFormPageViewModel(
                null,
                false,
                allowedStatuses(),
                null
        );
        model.addAttribute(VEHICLE_FORM_MODEL_ATTRIBUTE, viewModel);
        return VEHICLE_FORM_VIEW_NAME;
    }

    @GetMapping("/{vehicleId}/edit")
    public String getEditVehiclePage(@PathVariable String vehicleId, Model model) {
        VehicleResponse vehicle = vehicleQueryService.getVehicleById(vehicleId);
        VehicleFormPageViewModel viewModel = pageViewModelMapper.toVehicleFormPageViewModel(
                vehicle,
                true,
                allowedStatuses(),
                null
        );
        model.addAttribute(VEHICLE_FORM_MODEL_ATTRIBUTE, viewModel);
        return VEHICLE_FORM_VIEW_NAME;
    }

    private String renderVehicleListPage(
            String search,
            String status,
            String sortBy,
            String direction,
            Model model
    ) {
        VehicleStatus statusFilter = parseStatus(status);
        String resolvedSearch = hasText(search) ? search.trim() : null;
        String resolvedSortBy = hasText(sortBy) ? sortBy.trim() : DEFAULT_SORT_BY;
        String resolvedDirection = hasText(direction) ? direction.trim() : DEFAULT_DIRECTION;

        VehicleListPageViewModel viewModel;
        try {
            PagedResponse<VehicleResponse> vehicles = loadVehicleRoster(
                    resolvedSortBy,
                    resolvedDirection,
                    statusFilter,
                    resolvedSearch
            );

            viewModel = pageViewModelMapper.toVehicleListPageViewModel(
                    vehicles,
                    resolvedSortBy,
                    resolvedDirection,
                    resolvedSearch,
                    statusFilter,
                    Math.toIntExact(vehicles.getTotalItems()),
                    "Vehicles",
                    hasActiveFilters(resolvedSearch, statusFilter)
                            ? VEHICLE_LIST_EMPTY_STATE
                            : VEHICLE_LIST_INITIAL_EMPTY_STATE
            );
        } catch (RuntimeException exception) {
            viewModel = pageViewModelMapper.toVehicleListPageViewModel(
                    null,
                    resolvedSortBy,
                    resolvedDirection,
                    resolvedSearch,
                    statusFilter,
                    0,
                    "Vehicles",
                    hasActiveFilters(resolvedSearch, statusFilter)
                            ? VEHICLE_LIST_EMPTY_STATE
                            : VEHICLE_LIST_INITIAL_EMPTY_STATE
            );
            viewModel.setErrorMessage(VEHICLE_LIST_ERROR_STATE);
        }

        model.addAttribute(VEHICLE_LIST_MODEL_ATTRIBUTE, viewModel);
        return VEHICLE_LIST_VIEW_NAME;
    }

    private static VehicleStatus parseStatus(String status) {
        if (!hasText(status)) {
            return null;
        }
        try {
            return VehicleStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static java.util.List<String> allowedStatuses() {
        return Arrays.stream(VehicleStatus.values())
                .map(Enum::name)
                .toList();
    }

    private PagedResponse<VehicleResponse> loadVehicleRoster(
            String sortBy,
            String direction,
            VehicleStatus statusFilter,
            String searchQuery
    ) {
        PagedResponse<VehicleResponse> probePage = vehicleQueryService.getVehicles(
                FIRST_PAGE_INDEX,
                VEHICLE_TOTAL_COUNT_PROBE_SIZE,
                sortBy,
                direction,
                statusFilter,
                searchQuery
        );

        if (probePage.getTotalItems() <= VEHICLE_TOTAL_COUNT_PROBE_SIZE) {
            return probePage;
        }

        return vehicleQueryService.getVehicles(
                FIRST_PAGE_INDEX,
                Math.toIntExact(probePage.getTotalItems()),
                sortBy,
                direction,
                statusFilter,
                searchQuery
        );
    }

    private static boolean hasActiveFilters(String search, VehicleStatus statusFilter) {
        return hasText(search) || statusFilter != null;
    }
}
