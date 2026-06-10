package com.example.phase2.web.controller;

import com.example.phase2.application.query.FleetAnalyticsPageQueryService;
import com.example.phase2.web.viewmodel.FleetAnalyticsPageViewModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/fleet-analytics")
public class FleetAnalyticsPageController {

    private static final String FLEET_ANALYTICS_VIEW_NAME = "admin/fleet-analytics";
    private static final String FLEET_ANALYTICS_MODEL_ATTRIBUTE = "fleetAnalytics";

    private final FleetAnalyticsPageQueryService fleetAnalyticsPageQueryService;

    public FleetAnalyticsPageController(FleetAnalyticsPageQueryService fleetAnalyticsPageQueryService) {
        this.fleetAnalyticsPageQueryService = fleetAnalyticsPageQueryService;
    }

    @GetMapping
    public String getFleetAnalyticsPage(
            @RequestParam(required = false) Long fromTimestamp,
            @RequestParam(required = false) Long toTimestamp,
            Model model
    ) {
        FleetAnalyticsPageViewModel viewModel;

        try {
            viewModel = fleetAnalyticsPageQueryService.getFleetAnalyticsPage(fromTimestamp, toTimestamp);
        } catch (RuntimeException exception) {
            viewModel = new FleetAnalyticsPageViewModel(
                    null,
                    fromTimestamp,
                    toTimestamp,
                    "Fleet Analytics",
                    "Unavailable",
                    false,
                    "Fleet analytics are unavailable for this request."
            );
            viewModel.setErrorMessage("Fleet analytics could not be loaded. Retry the request or return to the dashboard.");
        }

        model.addAttribute(FLEET_ANALYTICS_MODEL_ATTRIBUTE, viewModel);
        return FLEET_ANALYTICS_VIEW_NAME;
    }
}
