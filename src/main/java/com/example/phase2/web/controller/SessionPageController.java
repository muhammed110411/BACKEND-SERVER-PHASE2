package com.example.phase2.web.controller;

import com.example.phase2.api.admin.response.SessionDetailResponse;
import com.example.phase2.api.admin.response.SessionSummaryResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.application.mapper.PageViewModelMapper;
import com.example.phase2.application.query.SessionQueryService;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.web.viewmodel.SessionDetailPageViewModel;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/sessions")
public class SessionPageController {

    private static final String SESSION_LIST_VIEW_NAME = "admin/sessions/list";
    private static final String SESSION_DETAIL_VIEW_NAME = "admin/sessions/detail";
    private static final String SESSION_LIST_MODEL_ATTRIBUTE = "sessionPage";
    private static final String SESSION_DETAIL_MODEL_ATTRIBUTE = "sessionDetail";
    private static final int DEFAULT_PAGE_SIZE = 25;

    private final SessionQueryService sessionQueryService;
    private final PageViewModelMapper pageViewModelMapper;

    public SessionPageController(
            SessionQueryService sessionQueryService,
            PageViewModelMapper pageViewModelMapper
    ) {
        this.sessionQueryService = sessionQueryService;
        this.pageViewModelMapper = pageViewModelMapper;
    }

    @GetMapping
    public String getSessionsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "startTimestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String driverId,
            @RequestParam(required = false) String vehicleId,
            @RequestParam(required = false) SessionValidity validity,
            HttpServletRequest request,
            Model model
    ) {
        try {
            PagedResponse<SessionSummaryResponse> sessions = sessionQueryService.getSessions(
                    Math.max(page, 0),
                    Math.max(size, 1),
                    sortBy,
                    sortDirection,
                    driverId,
                    vehicleId,
                    validity,
                    null,
                    null
            );
            model.addAttribute(SESSION_LIST_MODEL_ATTRIBUTE, sessions);
        } catch (RuntimeException exception) {
            model.addAttribute(SESSION_LIST_MODEL_ATTRIBUTE, emptySessionPage(Math.max(page, 0), Math.max(size, 1)));
            model.addAttribute("sessionsError", "Session history could not be loaded. Retry when the service is reachable.");
        }

        model.addAttribute("searchQuery", q);
        model.addAttribute("driverId", driverId);
        model.addAttribute("vehicleId", vehicleId);
        model.addAttribute("validity", validity);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("sortValue", sortBy + ":" + sortDirection);
        model.addAttribute("hasActiveSessionFilters", hasText(q)
                || hasText(driverId)
                || hasText(vehicleId)
                || validity != null);
        model.addAttribute("listReturnUrl", buildCurrentUrl(request));
        return SESSION_LIST_VIEW_NAME;
    }

    @GetMapping("/{sessionId}")
    public String getSessionDetailPage(
            @PathVariable String sessionId,
            @RequestParam(required = false) String returnTo,
            Model model
    ) {
        SessionDetailPageViewModel viewModel;

        try {
            SessionDetailResponse sessionDetail = sessionQueryService.getSessionDetail(sessionId);
            viewModel = pageViewModelMapper.toSessionDetailPageViewModel(sessionDetail);
        } catch (RuntimeException exception) {
            viewModel = pageViewModelMapper.toSessionDetailPageViewModel(null);
            viewModel.setErrorMessage("Session detail could not be loaded. Retry or return to the session history.");
        }

        model.addAttribute(SESSION_DETAIL_MODEL_ATTRIBUTE, viewModel);
        model.addAttribute("sessionReturnUrl", hasText(returnTo) ? returnTo : "/admin/sessions");
        return SESSION_DETAIL_VIEW_NAME;
    }

    private static PagedResponse<SessionSummaryResponse> emptySessionPage(int page, int size) {
        return new PagedResponse<>(List.of(), page, size, 0L, 0, false, false);
    }

    private static String buildCurrentUrl(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (!hasText(queryString)) {
            return request.getRequestURI();
        }
        return request.getRequestURI() + "?" + queryString;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
