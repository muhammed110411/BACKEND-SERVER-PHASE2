package com.example.phase2.web.controller;

import com.example.phase2.api.admin.response.SessionDetailResponse;
import com.example.phase2.api.admin.response.SessionSummaryResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.application.mapper.PageViewModelMapper;
import com.example.phase2.application.query.SessionQueryService;
import com.example.phase2.application.security.ApiClientAuthenticationService;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;
import com.example.phase2.security.AuthenticationEntryPointHandler;
import com.example.phase2.web.viewmodel.SessionDetailPageViewModel;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.isNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(SessionPageController.class)
class SessionPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SessionQueryService sessionQueryService;

    @MockitoBean
    private PageViewModelMapper pageViewModelMapper;

    @MockitoBean
    private ApiClientAuthenticationService apiClientAuthenticationService;

    @MockitoBean
    private AuthenticationEntryPointHandler authenticationEntryPointHandler;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSessionsPageRendersSummaryBrowserWithFilters() throws Exception {
        PagedResponse<SessionSummaryResponse> response = new PagedResponse<>(
                List.of(new SessionSummaryResponse(
                        "session-1",
                        "driver-1",
                        "vehicle-1",
                        1700000000L,
                        1700000500L,
                        SessionEndStatus.COMPLETED,
                        SessionValidity.VALID,
                        UploadProcessingStatus.PROCESSED,
                        91.5d,
                        500L,
                        12.75d,
                        2,
                        1,
                        1700000600L,
                        1700000700L
                )),
                0,
                25,
                1,
                1,
                false,
                false
        );

        when(sessionQueryService.getSessions(
                0,
                25,
                "startTimestamp",
                "desc",
                "driver-1",
                null,
                SessionValidity.VALID,
                SessionEndStatus.COMPLETED,
                UploadProcessingStatus.PROCESSED
        )).thenReturn(response);

        mockMvc.perform(get("/admin/sessions")
                        .param("driverId", "driver-1")
                        .param("status", "COMPLETED")
                        .param("validity", "VALID")
                        .param("uploadProcessingStatus", "PROCESSED")
                        .param("q", "session"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/sessions/list"))
                .andExpect(model().attribute("sessionPage", response))
                .andExpect(model().attribute("searchQuery", "session"))
                .andExpect(model().attribute("driverId", "driver-1"))
                .andExpect(model().attribute("sortBy", "startTimestamp"))
                .andExpect(model().attribute("sortDirection", "desc"))
                .andExpect(model().attribute("hasActiveSessionFilters", true));

        verify(sessionQueryService).getSessions(
                0,
                25,
                "startTimestamp",
                "desc",
                "driver-1",
                null,
                SessionValidity.VALID,
                SessionEndStatus.COMPLETED,
                UploadProcessingStatus.PROCESSED
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSessionDetailPageRendersReadOnlyHistoricalSessionView() throws Exception {
        SessionDetailResponse sessionDetail = new SessionDetailResponse(
                "session-1",
                "driver-1",
                "vehicle-1",
                1700000000L,
                1700000500L,
                SessionEndStatus.COMPLETED,
                SessionValidity.VALID,
                UploadProcessingStatus.PROCESSED,
                91.5d,
                1.0d,
                2.0d,
                3.0d,
                500L,
                12.75d,
                2,
                1,
                1700000600L,
                1700000700L,
                "client-1",
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
        SessionDetailPageViewModel viewModel = new SessionDetailPageViewModel();
        viewModel.setSessionDetail(sessionDetail);

        when(sessionQueryService.getSessionDetail("session-1")).thenReturn(sessionDetail);
        when(pageViewModelMapper.toSessionDetailPageViewModel(sessionDetail)).thenReturn(viewModel);

        mockMvc.perform(get("/admin/sessions/session-1")
                        .param("returnTo", "/admin/sessions?driverId=driver-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/sessions/detail"))
                .andExpect(model().attribute("sessionDetail", viewModel))
                .andExpect(model().attribute("sessionReturnUrl", "/admin/sessions?driverId=driver-1"));

        verify(sessionQueryService).getSessionDetail("session-1");
        verify(pageViewModelMapper).toSessionDetailPageViewModel(sessionDetail);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSessionDetailPageFallsBackToErrorStateWhenDetailFetchFails() throws Exception {
        SessionDetailPageViewModel emptyViewModel = new SessionDetailPageViewModel();

        when(sessionQueryService.getSessionDetail("missing-session")).thenThrow(new RuntimeException("boom"));
        when(pageViewModelMapper.toSessionDetailPageViewModel(isNull())).thenReturn(emptyViewModel);

        mockMvc.perform(get("/admin/sessions/missing-session"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/sessions/detail"))
                .andExpect(model().attribute("sessionDetail", emptyViewModel))
                .andExpect(model().attribute("sessionReturnUrl", "/admin/sessions"));

        verify(sessionQueryService).getSessionDetail("missing-session");
        verify(pageViewModelMapper).toSessionDetailPageViewModel(isNull());
    }
}
