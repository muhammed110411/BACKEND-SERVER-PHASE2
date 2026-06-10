package com.example.phase2.web.controller;

import com.example.phase2.api.admin.response.DriverAnalyticsResponse;
import com.example.phase2.api.admin.response.DriverCardResponse;
import com.example.phase2.api.admin.response.DriverDetailResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.application.command.DriverLifecycleService;
import com.example.phase2.application.mapper.PageViewModelMapper;
import com.example.phase2.application.query.DriverQueryService;
import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.domain.model.Driver;
import com.example.phase2.web.viewmodel.DriverAnalyticsPageViewModel;
import com.example.phase2.web.viewmodel.DriverCreateForm;
import com.example.phase2.web.viewmodel.DriverDetailPageViewModel;
import com.example.phase2.web.viewmodel.DriverListPageViewModel;
import jakarta.validation.Valid;
import java.util.Objects;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/drivers")
public class DriverPageController {

    private static final String DRIVER_LIST_VIEW_NAME = "admin/drivers/list";
    private static final String DRIVER_CREATE_VIEW_NAME = "admin/drivers/create";
    private static final String DRIVER_DETAIL_VIEW_NAME = "admin/drivers/detail";
    private static final String DRIVER_ANALYTICS_VIEW_NAME = "admin/drivers/analytics";

    private static final String DRIVER_LIST_MODEL_ATTRIBUTE = "driverList";
    private static final String DRIVER_FORM_MODEL_ATTRIBUTE = "driverForm";
    private static final String DRIVER_DETAIL_MODEL_ATTRIBUTE = "driverDetail";
    private static final String DRIVER_ANALYTICS_MODEL_ATTRIBUTE = "driverAnalytics";

    private static final int FIRST_PAGE_INDEX = 0;
    private static final int DRIVER_LIST_PAGE_SIZE = 200;
    private static final String DEFAULT_DRIVER_SORT_BY = "name";
    private static final String DEFAULT_DRIVER_DIRECTION = "asc";

    private final DriverQueryService driverQueryService;
    private final DriverLifecycleService driverLifecycleService;
    private final PageViewModelMapper pageViewModelMapper;

    public DriverPageController(
            DriverQueryService driverQueryService,
            DriverLifecycleService driverLifecycleService,
            PageViewModelMapper pageViewModelMapper
    ) {
        this.driverQueryService = driverQueryService;
        this.driverLifecycleService = driverLifecycleService;
        this.pageViewModelMapper = pageViewModelMapper;
    }

    @GetMapping(params = {"!search", "!status", "!sortBy", "!direction"})
    public String getDriverListPage(Model model) {
        return renderDriverListPage(null, null, null, null, model);
    }

    @GetMapping
    public String getDriverListPage(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction,
            Model model
    ) {
        return renderDriverListPage(search, status, sortBy, direction, model);
    }

    @GetMapping("/new")
    public String getCreateDriverPage(Model model) {
        if (!model.containsAttribute(DRIVER_FORM_MODEL_ATTRIBUTE)) {
            model.addAttribute(DRIVER_FORM_MODEL_ATTRIBUTE, new DriverCreateForm());
        }
        return DRIVER_CREATE_VIEW_NAME;
    }

    @PostMapping("/new")
    public String createDriver(
            @Valid @ModelAttribute(DRIVER_FORM_MODEL_ATTRIBUTE) DriverCreateForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        validateCreateDriverForm(form, bindingResult);
        if (bindingResult.hasErrors()) {
            form.clearPasswordFields();
            return DRIVER_CREATE_VIEW_NAME;
        }

        try {
            Driver driver = driverLifecycleService.createDriver(
                    form.getName(),
                    form.getEmail(),
                    form.getPassword(),
                    UserRole.DRIVER
            );
            redirectAttributes.addAttribute("created", "true");
            return "redirect:/admin/drivers/" + driver.getId();
        } catch (IllegalArgumentException exception) {
            rejectCreateDriverFailure(bindingResult, exception);
            form.clearPasswordFields();
            return DRIVER_CREATE_VIEW_NAME;
        }
    }

    @GetMapping("/{driverId}")
    public String getDriverDetailPage(
            @PathVariable String driverId,
            @RequestParam(defaultValue = "false") boolean created,
            Model model
    ) {
        DriverDetailResponse driver = driverQueryService.getDriverDetail(driverId);

        DriverDetailPageViewModel viewModel = pageViewModelMapper.toDriverDetailPageViewModel(
                driver,
                "Driver Detail",
                true,
                true,
                "Driver record could not be found.",
                null
        );

        model.addAttribute(DRIVER_DETAIL_MODEL_ATTRIBUTE, viewModel);
        model.addAttribute("driverCreated", created);
        return DRIVER_DETAIL_VIEW_NAME;
    }

    @GetMapping("/{driverId}/analytics")
    public String getDriverAnalyticsPage(@PathVariable String driverId, Model model) {
        DriverDetailResponse driver = null;
        DriverAnalyticsResponse analytics = null;
        String errorMessage = null;

        try {
            driver = driverQueryService.getDriverDetail(driverId);
            analytics = driverQueryService.getDriverAnalytics(driverId, null, null);
        } catch (RuntimeException exception) {
            errorMessage = "Driver analytics could not be loaded. Retry or return to Drivers.";
        }

        DriverAnalyticsPageViewModel viewModel = pageViewModelMapper.toDriverAnalyticsPageViewModel(
                driverId,
                analytics,
                driver,
                null,
                null,
                "Driver Analytics",
                "All time",
                analytics != null && analytics.getTotalSessions() > 0,
                "No analytical data yet. Analytics will appear after session uploads."
        );
        viewModel.setErrorMessage(errorMessage);

        model.addAttribute(DRIVER_ANALYTICS_MODEL_ATTRIBUTE, viewModel);
        return DRIVER_ANALYTICS_VIEW_NAME;
    }

    private String renderDriverListPage(
            String search,
            String status,
            String sortBy,
            String direction,
            Model model
    ) {
        DriverAccountStatus statusFilter = parseStatus(status);
        String resolvedSortBy = hasText(sortBy) ? sortBy.trim() : DEFAULT_DRIVER_SORT_BY;
        String resolvedDirection = hasText(direction) ? direction.trim() : DEFAULT_DRIVER_DIRECTION;
        String resolvedSearch = hasText(search) ? search.trim() : null;

        PagedResponse<DriverCardResponse> drivers = driverQueryService.getDrivers(
                FIRST_PAGE_INDEX,
                DRIVER_LIST_PAGE_SIZE,
                resolvedSortBy,
                resolvedDirection,
                statusFilter,
                resolvedSearch
        );

        DriverListPageViewModel viewModel = pageViewModelMapper.toDriverListPageViewModel(
                drivers,
                resolvedSortBy,
                resolvedDirection,
                resolvedSearch,
                statusFilter,
                Math.toIntExact(drivers.getTotalItems()),
                "admin",
                "Drivers",
                "No drivers matched the selected filters."
        );

        model.addAttribute(DRIVER_LIST_MODEL_ATTRIBUTE, viewModel);
        return DRIVER_LIST_VIEW_NAME;
    }

    private DriverAccountStatus parseStatus(String status) {
        if (!hasText(status)) {
            return null;
        }
        return DriverAccountStatus.valueOf(status.trim().toUpperCase());
    }

    private void validateCreateDriverForm(DriverCreateForm form, BindingResult bindingResult) {
        if (!bindingResult.hasFieldErrors("confirmPassword")
                && !Objects.equals(form.getPassword(), form.getConfirmPassword())) {
            bindingResult.rejectValue(
                    "confirmPassword",
                    "driver.password.mismatch",
                    "Password confirmation must match."
            );
        }

        if (!bindingResult.hasFieldErrors("accountStatus")
                && form.getAccountStatus() != DriverAccountStatus.ACTIVE) {
            bindingResult.rejectValue(
                    "accountStatus",
                    "driver.status.unsupported",
                    "New driver accounts must start as ACTIVE."
            );
        }
    }

    private void rejectCreateDriverFailure(BindingResult bindingResult, IllegalArgumentException exception) {
        if (exception.getMessage() != null && exception.getMessage().toLowerCase().contains("email")) {
            bindingResult.rejectValue(
                    "email",
                    "driver.email.duplicate",
                    "A driver with this email already exists."
            );
            return;
        }

        bindingResult.reject("driver.create.failed", "Driver could not be created. Check the form and retry.");
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
