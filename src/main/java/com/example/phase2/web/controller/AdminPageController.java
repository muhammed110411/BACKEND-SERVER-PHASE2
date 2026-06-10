package com.example.phase2.web.controller;

import com.example.phase2.application.mapper.PageViewModelMapper;
import com.example.phase2.application.query.AdminDashboardQueryService;
import com.example.phase2.web.viewmodel.AdminDashboardPageViewModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    private static final String DASHBOARD_VIEW_NAME = "admin/dashboard";
    private static final String DASHBOARD_MODEL_ATTRIBUTE = "dashboard";

    private final AdminDashboardQueryService adminDashboardQueryService;
    private final PageViewModelMapper pageViewModelMapper;

    public AdminPageController(
            AdminDashboardQueryService adminDashboardQueryService,
            PageViewModelMapper pageViewModelMapper
    ) {
        this.adminDashboardQueryService = adminDashboardQueryService;
        this.pageViewModelMapper = pageViewModelMapper;
    }

    @GetMapping(params = {"!driverSearch", "!sortBy", "!direction", "!fromTimestamp", "!toTimestamp"})
    public String getAdminDashboard(Model model) {
        return renderDashboard(null, null, null, null, null, model);
    }

    @GetMapping
    public String getAdminDashboard(
            @RequestParam(required = false) String driverSearch,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) Long fromTimestamp,
            @RequestParam(required = false) Long toTimestamp,
            Model model
    ) {
        return renderDashboard(driverSearch, sortBy, direction, fromTimestamp, toTimestamp, model);
    }

    private String renderDashboard(
            String driverSearch,
            String sortBy,
            String direction,
            Long fromTimestamp,
            Long toTimestamp,
            Model model
    ) {
        AdminDashboardPageViewModel dashboardPageViewModel = fromTimestamp == null && toTimestamp == null
                ? adminDashboardQueryService.getDashboard()
                : adminDashboardQueryService.getDashboard(fromTimestamp, toTimestamp);

        model.addAttribute(DASHBOARD_MODEL_ATTRIBUTE, dashboardPageViewModel);
        model.addAttribute("driverSearch", driverSearch);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("fromTimestamp", fromTimestamp);
        model.addAttribute("toTimestamp", toTimestamp);

        return DASHBOARD_VIEW_NAME;
    }
}
